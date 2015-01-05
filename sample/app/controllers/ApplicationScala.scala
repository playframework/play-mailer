package controllers

import java.io.File

import play.api.libs.mailer._
import org.apache.commons.mail.EmailAttachment
import play.api.mvc.{Action, Controller}
import play.api.Play.current

object ApplicationScala  extends Controller {

  def send = Action {
    val email = Email(
      "Simple email",
      "Mister FROM <from@email.com>",
      Seq("Miss TO <to@email.com>"),
      attachments = Seq(
        AttachmentFile("favicon.png", new File(current.classloader.getResource("public/images/favicon.png").getPath)),
        AttachmentData("data.txt", "data".getBytes, "text/plain", Some("Simple data"), Some(EmailAttachment.INLINE))
      ),
      bodyText = Some("A text message"),
      bodyHtml = Some("<html><body><p>An <b>html</b> message</p></body></html>")
    )
    val id = MailerPlugin.send(email)
    Ok(s"Email $id sent!")
  }
}
