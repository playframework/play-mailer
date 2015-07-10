package play.api.libs.mailer

import java.io.{File, FilterOutputStream, PrintStream}
import javax.inject.Inject
import javax.mail.internet.InternetAddress

import org.apache.commons.mail._
import play.api.inject._
import play.api.{Configuration, Environment, Logger, PlayConfig}
import play.libs.mailer.{Email => JEmail, MailerClient => JMailerClient}

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

  override def send(data: JEmail): String = {
    val email = convert(data)
    send(email)
  }

  protected def convert(data: play.libs.mailer.Email): Email = {
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

  private val mailerConfig = PlayConfig(configuration).getDeprecatedWithFallback("play.mailer", "smtp")
  private lazy val mock = mailerConfig.get[Boolean]("mock")

  private lazy val instance = {
    if (mock) {
      new MockMailer()
    } else {
      val smtpHost = mailerConfig.getOptional[String]("host").getOrElse(throw new RuntimeException("play.mailer.host needs to be set in application.conf in order to use this plugin (or set play.mailer.mock to true)"))
      val smtpPort = mailerConfig.get[Int]("port")
      val smtpSsl = mailerConfig.get[Boolean]("ssl")
      val smtpTls = mailerConfig.get[Boolean]("tls")
      val smtpUser = mailerConfig.getOptional[String]("user")
      val smtpPassword = mailerConfig.getOptional[String]("password")
      val debugMode = mailerConfig.get[Boolean]("debug")
      val smtpTimeout = mailerConfig.getOptional[Int]("timeout")
      val smtpConnectionTimeout = mailerConfig.getOptional[Int]("connectiontimeout")
      new SMTPMailer(smtpHost, smtpPort, smtpSsl, smtpTls, smtpUser, smtpPassword, debugMode, smtpTimeout, smtpConnectionTimeout) {
        override def send(email: MultiPartEmail): String = email.send()
        override def createMultiPartEmail(): MultiPartEmail = new MultiPartEmail()
        override def createHtmlEmail(): HtmlEmail = new HtmlEmail()
      }
    }
  }

  override def send(data: Email): String = instance.send(data)
}

abstract class SMTPMailer(smtpHost: String, smtpPort: Int,
                          smtpSsl: Boolean,
                          smtpTls: Boolean,
                          smtpUser: Option[String],
                          smtpPass: Option[String],
                          debugMode: Boolean,
                          smtpTimeout: Option[Int],
                          smtpConnectionTimeout: Option[Int]) extends MailerClient {

  def send(email: MultiPartEmail): String

  def createMultiPartEmail(): MultiPartEmail

  def createHtmlEmail(): HtmlEmail

  override def send(data: Email): String = send(createEmail(data))

  def createEmail(data: Email): MultiPartEmail = {
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
    smtpTimeout.foreach(email.setSocketTimeout)
    smtpConnectionTimeout.foreach(email.setSocketConnectionTimeout)
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
    email.setHostName(smtpHost)
    email.setSmtpPort(smtpPort)
    email.setSSLOnConnect(smtpSsl)
    if (smtpSsl) {
      email.setSslSmtpPort(smtpPort.toString)
    }
    email.setStartTLSEnabled(smtpTls)
    for (u <- smtpUser; p <- smtpPass) yield email.setAuthenticator(new DefaultAuthenticator(u, p))
    if (debugMode && Logger.isDebugEnabled) {
      email.setDebug(debugMode)
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
