package play.api.libs.mailer

import java.io.{ File, FilterOutputStream, PrintStream }
import javax.inject.{ Inject, Provider }
import javax.mail.internet.InternetAddress

import com.google.inject.AbstractModule
import com.google.inject.name.Names
import com.typesafe.config.Config
import org.apache.commons.mail._
import org.slf4j.LoggerFactory
import play.libs.mailer.{ Email => JEmail, MailerClient => JMailerClient }

import scala.collection.JavaConverters._
import scala.util.Try
import scala.util.control.NonFatal

// for compile-time injection
trait MailerComponents {
  def config: Config
  lazy val mailerClient: SMTPMailer = new SMTPMailer(new SMTPConfigurationProvider(config).get())
}

// for runtime injection
class MailerModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[MailerClient]).to(classOf[SMTPDynamicMailer])
    bind(classOf[JMailerClient]).to(classOf[MailerClient])
    bind(classOf[MailerClient]).annotatedWith(Names.named("mock")).to(classOf[MockMailer])
    bind(classOf[JMailerClient]).annotatedWith(Names.named("mock")).to(classOf[MockMailer])
  }

}

class SMTPConfigurationModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[SMTPConfiguration]).toProvider(classOf[SMTPConfigurationProvider])
  }

}

// API

trait MailerClient extends JMailerClient {

  /**
   * Sends an email with the provided data.
   *
   * @param data data to send
   * @return the message id
   */
  def send(data: Email): String

  override def send(data: JEmail): String = {
    val email = convert(data)
    send(email)
  }

  protected def convert(data: JEmail): Email = {
    val attachments = data.getAttachments.asScala.map { attachment =>
      if (Option(attachment.getFile).isDefined) {
        AttachmentFile(
          attachment.getName,
          attachment.getFile,
          Option(attachment.getDescription), Option(attachment.getDisposition), Option(attachment.getContentId))
      } else {
        AttachmentData(
          attachment.getName,
          attachment.getData,
          attachment.getMimetype,
          Option(attachment.getDescription), Option(attachment.getDisposition), Option(attachment.getContentId))
      }
    }
    Email(
      Option(data.getSubject).getOrElse(""),
      Option(data.getFrom).getOrElse(""),
      data.getTo.asScala,
      Option(data.getBodyText),
      Option(data.getBodyHtml),
      Option(data.getCharset),
      data.getCc.asScala,
      data.getBcc.asScala,
      Option(data.getReplyTo),
      Option(data.getBounceAddress),
      attachments,
      data.getHeaders.asScala.toSeq)
  }
}

// Implementations

class SMTPMailer @Inject()(smtpConfiguration: SMTPConfiguration) extends MailerClient {

  private lazy val instance = {
    if (smtpConfiguration.mock) {
      new MockMailer()
    } else {
      new CommonsMailer(smtpConfiguration) {
        override def send(email: MultiPartEmail): String = email.send()

        override def createMultiPartEmail(): MultiPartEmail = new MultiPartEmail()

        override def createHtmlEmail(): HtmlEmail = new HtmlEmail()
      }
    }
  }

  override def send(data: Email): String = {
    instance.send(data)
  }
}

class SMTPDynamicMailer @Inject()(smtpConfigurationProvider: Provider[SMTPConfiguration]) extends MailerClient {

  override def send(data: Email): String = {
    new SMTPMailer(smtpConfigurationProvider.get()).send(data)
  }

}

abstract class CommonsMailer(conf: SMTPConfiguration) extends MailerClient {

  protected val logger = LoggerFactory.getLogger("play.mailer")

  def send(email: MultiPartEmail): String

  def createMultiPartEmail(): MultiPartEmail

  def createHtmlEmail(): HtmlEmail

  override def send(data: Email): String = send(createEmail(data))

  def createEmail(data: Email): MultiPartEmail = {
    val email = createEmail(data.bodyText, data.bodyHtml, data.charset.getOrElse("utf-8"))
    email.setSubject(data.subject)
    setAddress(data.from) { (address, name) => email.setFrom(address, name) }
    data.replyTo.foreach(setAddress(_) { (address, name) => email.addReplyTo(address, name) })
    data.bounceAddress.foreach(email.setBounceAddress)
    data.to.foreach(setAddress(_) { (address, name) => email.addTo(address, name) })
    data.cc.foreach(setAddress(_) { (address, name) => email.addCc(address, name) })
    data.bcc.foreach(setAddress(_) { (address, name) => email.addBcc(address, name) })
    data.headers.foreach {
      header => email.addHeader(header._1, header._2)
    }
    conf.timeout.foreach(email.setSocketTimeout)
    conf.connectionTimeout.foreach(email.setSocketConnectionTimeout)
    data.attachments.foreach {
      case attachmentData: AttachmentData =>
        handleAttachmentData(email, attachmentData)
      case attachmentFile: AttachmentFile =>
        handleAttachmentFile(email, attachmentFile)
    }
    email.setHostName(conf.host)
    email.setSmtpPort(conf.port)
    email.setSSLOnConnect(conf.ssl)
    if (conf.ssl) {
      email.setSslSmtpPort(conf.port.toString)
    }
    email.setStartTLSEnabled(conf.tls || conf.tlsRequired)
    email.setStartTLSRequired(conf.tlsRequired)
    for (u <- conf.user; p <- conf.password) yield email.setAuthenticator(new DefaultAuthenticator(u, p))
    if (conf.debugMode && logger.isDebugEnabled) {
      email.setDebug(conf.debugMode)
      email.getMailSession.setDebugOut(new PrintStream(new FilterOutputStream(null) {
        override def write(b: Array[Byte]) {
          logger.debug(new String(b))
        }

        override def write(b: Array[Byte], off: Int, len: Int) {
          logger.debug(new String(b, off, len))
        }

        override def write(b: Int) {
          this.write(new Array(b): Array[Byte])
        }
      }))
    }
    email
  }

  /**
   * Creates an appropriate email object based on the content type.
   */
  private def createEmail(bodyText: Option[String], bodyHtml: Option[String], charset: String): MultiPartEmail = {
    (bodyHtml.filter(_.trim.nonEmpty), bodyText.filter(_.trim.nonEmpty)) match {
      case (Some(htmlMsg), bodyTextOpt) =>
        val htmlEmail = createHtmlEmail()
        htmlEmail.setCharset(charset)
        htmlEmail.setHtmlMsg(htmlMsg)
        bodyTextOpt.foreach { bodyText =>
          htmlEmail.setTextMsg(bodyText)
        }
        htmlEmail
      case (None, Some(msg)) =>
        val multiPartEmail = createMultiPartEmail()
        multiPartEmail.setCharset(charset)
        multiPartEmail.setMsg(msg)
        multiPartEmail
      case _ =>
        createMultiPartEmail()
    }
  }

  /**
   * Extracts an email address from the given string and passes to the enclosed method.
   */
  private def setAddress(emailAddress: String)(setter: (String, String) => Unit) = {
    if (emailAddress != null) {
      try {
        val iAddress = new InternetAddress(emailAddress)
        val address = iAddress.getAddress
        val name = iAddress.getPersonal
        setter(address, name)
      } catch {
        case NonFatal(_) => setter(emailAddress, null)
      }
    }
  }

  private def handleAttachmentData(email: MultiPartEmail, attachmentData: AttachmentData) {
    val description = attachmentData.description.getOrElse(attachmentData.name)
    val disposition = attachmentData.disposition.getOrElse(EmailAttachment.ATTACHMENT)
    val dataSource = new javax.mail.util.ByteArrayDataSource(attachmentData.data, attachmentData.mimetype)
    attachmentData.contentId match {
      case Some(cid) =>
        email match {
          case htmlEmail: HtmlEmail => htmlEmail.embed(dataSource, attachmentData.name, cid)
          case _ => if (conf.debugMode && logger.isDebugEnabled) {
            logger.debug("You need to set an HTML body to embed images with cid")
          }
        }
      case None => email.attach(dataSource, attachmentData.name, description, disposition)
    }
  }

  private def handleAttachmentFile(email: MultiPartEmail, attachmentFile: AttachmentFile) {
    val description = attachmentFile.description.getOrElse(attachmentFile.name)
    val disposition = attachmentFile.disposition.getOrElse(EmailAttachment.ATTACHMENT)
    val emailAttachment = new EmailAttachment()
    emailAttachment.setName(attachmentFile.name)
    emailAttachment.setPath(attachmentFile.file.getPath)
    emailAttachment.setDescription(description)
    emailAttachment.setDisposition(disposition)
    attachmentFile.contentId match {
      case Some(cid) =>
        email match {
          case htmlEmail: HtmlEmail => htmlEmail.embed(attachmentFile.file, cid)
          case _ => if (conf.debugMode && logger.isDebugEnabled) {
            logger.debug("You need to set an HTML body to embed images with cid")
          }
        }
      case None => email.attach(emailAttachment)
    }
  }
}

class MockMailer @Inject() extends MailerClient {

  protected val logger = LoggerFactory.getLogger("play.mailer")

  override def send(email: Email): String = {
    logger.info("mock implementation, send email")
    logger.info(s"subject: ${email.subject}")
    logger.info(s"from: ${email.from}")
    email.bodyText.foreach(bodyText => logger.info(s"bodyText: $bodyText"))
    email.bodyHtml.foreach(bodyHtml => logger.info(s"bodyHtml: $bodyHtml"))
    email.to.foreach(to => logger.info(s"to: $to"))
    email.cc.foreach(cc => logger.info(s"cc: $cc"))
    email.bcc.foreach(bcc => logger.info(s"bcc: $bcc"))
    email.replyTo.foreach(replyTo => logger.info(s"replyTo: $replyTo"))
    email.bounceAddress.foreach(bounce => logger.info(s"bounceAddress: $bounce"))
    email.attachments.foreach(attachment => logger.info(s"attachment: $attachment"))
    email.headers.foreach(header => logger.info(s"header: $header"))
    ""
  }
}

sealed trait Attachment

case class Email(subject: String,
  from: String,
  to: Seq[String] = Seq.empty,
  bodyText: Option[String] = None,
  bodyHtml: Option[String] = None,
  charset: Option[String] = None,
  cc: Seq[String] = Seq.empty,
  bcc: Seq[String] = Seq.empty,
  replyTo: Option[String] = None,
  bounceAddress: Option[String] = None,
  attachments: Seq[Attachment] = Seq.empty,
  headers: Seq[(String, String)] = Seq.empty)

case class AttachmentFile(name: String,
  file: File,
  description: Option[String] = None,
  disposition: Option[String] = None,
  contentId: Option[String] = None) extends Attachment

case class AttachmentData(name: String,
  data: Array[Byte],
  mimetype: String,
  description: Option[String] = None,
  disposition: Option[String] = None,
  contentId: Option[String] = None) extends Attachment

case class SMTPConfiguration(host: String,
  port: Int,
  ssl: Boolean = false,
  tls: Boolean = false,
  tlsRequired: Boolean = false,
  user: Option[String] = None,
  password: Option[String] = None,
  debugMode: Boolean = false,
  timeout: Option[Int] = None,
  connectionTimeout: Option[Int] = None,
  mock: Boolean = false)

object SMTPConfiguration {

  @inline
  private def getOptionString(config: Config, name: String) = {
    Try(config.getString(name)).toOption
  }

  @inline
  private def getOptionInt(config: Config, name: String) = {
    Try(config.getInt(name)).toOption
  }

  def apply(config: Config) = new SMTPConfiguration(
    resolveHost(config),
    config.getInt("port"),
    config.getBoolean("ssl"),
    config.getBoolean("tls"),
    config.getBoolean("tlsRequired"),
    getOptionString(config, "user"),
    getOptionString(config, "password"),
    config.getBoolean("debug"),
    getOptionInt(config, "timeout"),
    getOptionInt(config, "connectiontimeout"),
    config.getBoolean("mock")
  )

  def resolveHost(config: Config): String = {
    if (config.getBoolean("mock")) {
      // host won't be used anyway...
      ""
    } else {
      getOptionString(config, "host").getOrElse(throw new RuntimeException("host needs to be set in order to use this plugin (or set play.mailer.mock to true in application.conf)"))
    }
  }

}

class SMTPConfigurationProvider @Inject()(config: Config) extends Provider[SMTPConfiguration] {
  override def get(): SMTPConfiguration = {
    SMTPConfiguration(config.getConfig("play.mailer"))
  }
}

class MailerConfigurationModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[SMTPConfiguration]).toProvider(classOf[SMTPConfigurationProvider])
  }

}
