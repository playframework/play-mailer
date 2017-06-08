package play.api.libs.mailer

import java.io.{ FilterOutputStream, PrintStream }
import javax.mail.internet.InternetAddress

import org.apache.commons.mail.{ DefaultAuthenticator, EmailAttachment, HtmlEmail, MultiPartEmail }
import org.slf4j.LoggerFactory

abstract class CommonsMailer(conf: SMTPConfiguration) extends MailerClient {

  private val logger = LoggerFactory.getLogger("play.mailer")

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
        case e: Exception =>
          setter(emailAddress, null)
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
