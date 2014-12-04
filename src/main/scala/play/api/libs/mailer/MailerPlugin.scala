package play.api.libs.mailer

import java.io.{File, FilterOutputStream, PrintStream}
import javax.mail.internet.InternetAddress

import org.apache.commons.mail._
import play.api._
import scala.collection.JavaConverters._

trait MailerJavaAPI {
  /**
   * Sends an email with the provided data.
   *
   * @param data data to send
   * @return the message id
   */
  def send(data: play.libs.mailer.Email): String
}

/**
 * plugin access
 */
object MailerPlugin {

  def send(email: Email)(implicit app: play.api.Application) = app.plugin(classOf[MailerPlugin]).get.instance.send(email)
}

/**
 * plugin interface
 */
trait MailerPlugin extends play.api.Plugin {
  def instance: MailerAPI
}

/**
 * plugin implementation
 */
class CommonsMailerPlugin(app: play.api.Application) extends MailerPlugin {

  private lazy val mock = app.configuration.getBoolean("smtp.mock").getOrElse(false)

  private lazy val mailerInstance: MailerAPI = {
    if (mock) {
      MockMailer
    } else {
      val smtpHost = app.configuration.getString("smtp.host").getOrElse(throw new RuntimeException("smtp.host needs to be set in application.conf in order to use this plugin (or set smtp.mock to true)"))
      val smtpPort = app.configuration.getInt("smtp.port").getOrElse(25)
      val smtpSsl = app.configuration.getBoolean("smtp.ssl").getOrElse(false)
      val smtpTls = app.configuration.getBoolean("smtp.tls").getOrElse(false)
      val smtpUser = app.configuration.getString("smtp.user")
      val smtpPassword = app.configuration.getString("smtp.password")
      val debugMode = app.configuration.getBoolean("smtp.debug").getOrElse(false)
      val smtpTimeout = app.configuration.getInt("smtp.timeout")
      val smtpConnectionTimeout = app.configuration.getInt("smtp.connectiontimeout")
      new CommonsMailer(smtpHost, smtpPort, smtpSsl, smtpTls, smtpUser, smtpPassword, debugMode, smtpTimeout, smtpConnectionTimeout) {
        override def send(email: MultiPartEmail): String = email.send()
        override def createMultiPartEmail(): MultiPartEmail = new MultiPartEmail()
        override def createHtmlEmail(): HtmlEmail = new HtmlEmail()
      }
    }
  }

  override lazy val enabled = !app.configuration.getString("apachecommonsmailerplugin").filter(_ == "disabled").isDefined

  override def onStart() {
    mailerInstance
  }

  def instance = mailerInstance
}

sealed trait Attachment

case class Email(subject: String,
                 from: String,
                 bodyText: Option[String] = None,
                 bodyHtml: Option[String] = None,
                 charset: Option[String] = None,
                 to: Seq[String] = Seq.empty,
                 cc: Seq[String] = Seq.empty,
                 bcc: Seq[String] = Seq.empty,
                 replyTo: Option[String] = None,
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


trait MailerAPI extends MailerJavaAPI {

  /**
   * Sends an email with the provided data.
   *
   * @param data data to send
   * @return the message id
   */
  def send(data: Email): String

  override def send(data: play.libs.mailer.Email): String = {
    val email = convert(data)
    send(email)
  }

  def convert(data: play.libs.mailer.Email):Email = {
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
      Option(data.getBodyText),
      Option(data.getBodyHtml),
      Option(data.getCharset),
      data.getTo.asScala.toSeq,
      data.getCc.asScala.toSeq,
      data.getBcc.asScala.toSeq,
      Option(data.getReplyTo),
      attachments,
      data.getHeaders.asScala.toSeq)
  }
}


case object MockMailer extends MailerAPI {

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
    email.attachments.foreach(attachment => Logger.info(s"attachment: $attachment"))
    email.headers.foreach(header => Logger.info(s"header: $header"))
    ""
  }
}

abstract class CommonsMailer(smtpHost: String, smtpPort: Int,
                            smtpSsl: Boolean,
                            smtpTls: Boolean,
                            smtpUser: Option[String],
                            smtpPass: Option[String],
                            debugMode: Boolean,
                            smtpTimeout: Option[Int],
                            smtpConnectionTimeout: Option[Int]) extends MailerAPI {

  def send(email: MultiPartEmail): String

  def createMultiPartEmail(): MultiPartEmail

  def createHtmlEmail(): HtmlEmail

  override def send(data: Email): String = send(createEmail(data))

  def createEmail(data: Email): MultiPartEmail = {
    val email = createEmail(data.bodyText, data.bodyHtml, data.charset.getOrElse("utf-8"))
    email.setSubject(data.subject)
    email.setFrom(data.from)
    data.replyTo.foreach(setAddress(_) { (address, name) => email.addReplyTo(address, name)})
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
    for(u <- smtpUser; p <- smtpPass) yield email.setAuthenticator(new DefaultAuthenticator(u, p))
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
          this.write(new Array(b):Array[Byte])
        }
      }))
    }
    email
  }

  /**
   * Creates an appropriate email object based on the content type.
   */
  private def createEmail(bodyText: Option[String], bodyHtml: Option[String], charset: String): MultiPartEmail = {
    val bodyHtmlOpt = bodyHtml.filter(_.trim.nonEmpty)
    val bodyTextOpt = bodyText.filter(_.trim.nonEmpty)
    if (bodyHtmlOpt.isDefined) {
      // HTML...
      val htmlEmail = createHtmlEmail()
      htmlEmail.setCharset(charset)
      htmlEmail.setHtmlMsg(bodyHtmlOpt.get)
      // ... with text ?
      if (bodyTextOpt.isDefined) {
        htmlEmail.setTextMsg(bodyTextOpt.get)
      }
      htmlEmail
    } else if (bodyTextOpt.isDefined) {
      // Text only
      val multiPartEmail = createMultiPartEmail()
      multiPartEmail.setCharset(charset)
      multiPartEmail.setMsg(bodyTextOpt.get)
      multiPartEmail
    } else {
      // Both empty
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
