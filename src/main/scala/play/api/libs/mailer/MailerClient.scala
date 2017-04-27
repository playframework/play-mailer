package play.api.libs.mailer

import play.libs.mailer.{ Email => JEmail, MailerClient => JMailerClient }

import scala.collection.JavaConverters._

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
      data.getReplyTo.asScala,
      Option(data.getBounceAddress),
      attachments,
      data.getHeaders.asScala.toSeq)
  }
}
