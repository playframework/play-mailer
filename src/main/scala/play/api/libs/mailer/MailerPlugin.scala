package play.api.libs.mailer

import java.io.{File, FilterOutputStream, PrintStream}
import javax.inject.Inject
import javax.mail.internet.InternetAddress

import org.apache.commons.mail._
import play.api.inject._
import play.api.{Configuration, Environment, Logger, PlayConfig}
import play.libs.mailer.{Email => JEmail, MailerClient => JMailerClient}
import play.{Configuration => JConfiguration}

import scala.collection.JavaConverters._

class MailerModule extends Module {
  def bindings(environment: Environment, configuration: Configuration) = Seq(
    bind[MailerClient].to[CommonsMailer],
    bind[JMailerClient].to(bind[MailerClient]),
    bind[MailerClient].qualifiedWith("mock").to[MockMailer],
    bind[JMailerClient].qualifiedWith("mock").to[MockMailer]
  )
}

// API

@deprecated("Use injected MailerClient instead", "2.4.0")
object MailerPlugin {

  def send(email: Email)(implicit app: play.api.Application) = app.injector.instanceOf[MailerClient].send(email)
}

trait MailerClient extends JMailerClient {

  /**
   * Sends an email with the provided data.
   *
   * @param data data to send
   * @return the message id
   */
  def send(data: Email): String

  /**
   * Configure the underlying instance of mailer.
   *
   * @param configuration configuration
   * @return the mailer client
   */
  def configure(configuration: Configuration): MailerClient

  override def configure(configuration: JConfiguration): JMailerClient = {
    configure(Configuration(configuration.underlying()))
  }

  override def send(data: JEmail): String = {
    val email = convert(data)
    send(email)
  }

  protected def convert(data: JEmail): Email = {
    val attachments = data.getAttachments.asScala.map {
      case attachment =>
        if (Option(attachment.getFile).isDefined) {
          AttachmentFile(
            attachment.getName,
            attachment.getFile,
            Option(attachment.getDescription), Option(attachment.getDisposition))
        } else {
          AttachmentData(
            attachment.getName,
            attachment.getData,
            attachment.getMimetype,
            Option(attachment.getDescription), Option(attachment.getDisposition))
        }
    }.toSeq
    Email(
      Option(data.getSubject).getOrElse(""),
      Option(data.getFrom).getOrElse(""),
      data.getTo.asScala.toSeq,
      Option(data.getBodyText),
      Option(data.getBodyHtml),
      Option(data.getCharset),
      data.getCc.asScala.toSeq,
      data.getBcc.asScala.toSeq,
      Option(data.getReplyTo),
      Option(data.getBounceAddress),
      attachments,
      data.getHeaders.asScala.toSeq)
  }
}

// Implementations

class CommonsMailer @Inject()(configuration: Configuration) extends MailerClient {

  private val defaultConfig = PlayConfig(configuration).getDeprecatedWithFallback("play.mailer", "smtp")
  private lazy val mock = defaultConfig.get[Boolean]("mock")

  private lazy val instance = {
    if (mock) {
      new MockMailer()
    } else {
      new SMTPMailer(defaultConfig) {
        override def send(email: MultiPartEmail): String = email.send()
        override def createMultiPartEmail(): MultiPartEmail = new MultiPartEmail()
        override def createHtmlEmail(): HtmlEmail = new HtmlEmail()
      }
    }
  }

  override def send(data: Email): String = {
    instance.send(data)
  }

  override def configure(configuration: Configuration) = {
    instance.configure(configuration)
  }
}

abstract class SMTPMailer(defaultConfig: PlayConfig, var config: Option[SMTPConfiguration] = None) extends MailerClient {

  def send(email: MultiPartEmail): String

  def createMultiPartEmail(): MultiPartEmail

  def createHtmlEmail(): HtmlEmail

  override def send(data: Email): String = send(createEmail(data))

  override def configure(configuration: Configuration) = {
    config = Some(SMTPConfiguration(PlayConfig(Configuration.reference.getConfig("play.mailer").get ++ configuration)))
    this
  }

  def createEmail(data: Email): MultiPartEmail = {
    val conf = config.getOrElse(SMTPConfiguration(defaultConfig))
    val email = createEmail(data.bodyText, data.bodyHtml, data.charset.getOrElse("utf-8"))
    email.setSubject(data.subject)
    setAddress(data.from) { (address, name) => email.setFrom(address, name) }
    data.replyTo.foreach(setAddress(_) { (address, name) => email.addReplyTo(address, name)})
    data.bounceAddress.foreach(email.setBounceAddress)
    data.to.foreach(setAddress(_) { (address, name) => email.addTo(address, name)})
    data.cc.foreach(setAddress(_) { (address, name) => email.addCc(address, name)})
    data.bcc.foreach(setAddress(_) { (address, name) => email.addBcc(address, name)})
    data.headers.foreach {
      header => email.addHeader(header._1, header._2)
    }
    conf.timeout.foreach(email.setSocketTimeout)
    conf.connectionTimeout.foreach(email.setSocketConnectionTimeout)
    data.attachments.foreach {
      case attachmentData: AttachmentData =>
        val description = attachmentData.description.getOrElse(attachmentData.name)
        val disposition = attachmentData.disposition.getOrElse(EmailAttachment.ATTACHMENT)
        val dataSource = new javax.mail.util.ByteArrayDataSource(attachmentData.data, attachmentData.mimetype)
        email.attach(dataSource, attachmentData.name, description, disposition)
      case attachmentFile: AttachmentFile =>
        val description = attachmentFile.description.getOrElse(attachmentFile.name)
        val disposition = attachmentFile.disposition.getOrElse(EmailAttachment.ATTACHMENT)
        val emailAttachment = new EmailAttachment()
        emailAttachment.setName(attachmentFile.name)
        emailAttachment.setPath(attachmentFile.file.getPath)
        emailAttachment.setDescription(description)
        emailAttachment.setDisposition(disposition)
        email.attach(emailAttachment)
    }
    email.setHostName(conf.host)
    email.setSmtpPort(conf.port)
    email.setSSLOnConnect(conf.ssl)
    if (conf.ssl) {
      email.setSslSmtpPort(conf.port.toString)
    }
    email.setStartTLSEnabled(conf.tls)
    for (u <- conf.user; p <- conf.password) yield email.setAuthenticator(new DefaultAuthenticator(u, p))
    if (conf.debugMode && Logger.isDebugEnabled) {
      email.setDebug(conf.debugMode)
      email.getMailSession.setDebugOut(new PrintStream(new FilterOutputStream(null) {
        override def write(b: Array[Byte]) {
          Logger.debug(new String(b))
        }

        override def write(b: Array[Byte], off: Int, len: Int) {
          Logger.debug(new String(b, off, len))
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
      case (Some(bodyHtml), bodyTextOpt) =>
        val htmlEmail = createHtmlEmail()
        htmlEmail.setCharset(charset)
        htmlEmail.setHtmlMsg(bodyHtml)
        bodyTextOpt.foreach { bodyText =>
          htmlEmail.setTextMsg(bodyText)
        }
        htmlEmail
      case (None, Some(bodyText)) =>
        val multiPartEmail = createMultiPartEmail()
        multiPartEmail.setCharset(charset)
        multiPartEmail.setMsg(bodyText)
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
        case e: Exception =>
          setter(emailAddress, null)
      }
    }
  }
}

class MockMailer @Inject() extends MailerClient {

  override def send(email: Email): String = {
    Logger.info("mock implementation, send email")
    Logger.info(s"subject: ${email.subject}")
    Logger.info(s"from: ${email.from}")
    email.bodyText.foreach(bodyText => Logger.info(s"bodyText: $bodyText"))
    email.bodyHtml.foreach(bodyHtml => Logger.info(s"bodyHtml: $bodyHtml"))
    email.to.foreach(to => Logger.info(s"to: $to"))
    email.cc.foreach(cc => Logger.info(s"cc: $cc"))
    email.bcc.foreach(bcc => Logger.info(s"to: $bcc"))
    email.replyTo.foreach(replyTo => Logger.info(s"replyTo: $replyTo"))
    email.bounceAddress.foreach(bounce => Logger.info(s"bounceAddress: $bounce"))
    email.attachments.foreach(attachment => Logger.info(s"attachment: $attachment"))
    email.headers.foreach(header => Logger.info(s"header: $header"))
    ""
  }

  override def configure(configuration: Configuration) = this
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
                          disposition: Option[String] = None) extends Attachment

case class AttachmentData(name: String,
                          data: Array[Byte],
                          mimetype: String,
                          description: Option[String] = None,
                          disposition: Option[String] = None) extends Attachment

case class SMTPConfiguration(host: String,
                             port: Int,
                             ssl: Boolean = false,
                             tls: Boolean = false,
                             user: Option[String],
                             password: Option[String],
                             debugMode: Boolean = false,
                             timeout: Option[Int] = None,
                             connectionTimeout: Option[Int] = None)

object SMTPConfiguration {

  def apply(config: PlayConfig) = new SMTPConfiguration(
    config.get[Option[String]]("host").getOrElse(throw new RuntimeException("host needs to be set in order to use this plugin (or set play.mailer.mock to true in application.conf)")),
    config.get[Int]("port"),
    config.get[Boolean]("ssl"),
    config.get[Boolean]("tls"),
    config.get[Option[String]]("user"),
    config.get[Option[String]]("password"),
    config.get[Boolean]("debug"),
    config.get[Option[Int]]("timeout"),
    config.get[Option[Int]]("connectiontimeout")
  )
}
