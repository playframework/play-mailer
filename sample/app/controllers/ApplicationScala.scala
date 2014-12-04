package controllers

import java.io.File

import play.api.libs.mailer._
import org.apache.commons.mail.EmailAttachment
import play.api.mvc.{Action, Controller}
import play.api.Play.current

object ApplicationScala  extends Controller {

  def index = Action {
    val email = Email(
      subject = "Simple email",
      from = "Mister FROM <from@email.com>",
      to = Seq("Miss TO <to@email.com>"),
      attachments = Seq(
        AttachmentFile("favicon.png", new File(current.classloader.getResource("public/images/favicon.png").getPath)),
        AttachmentData("data.txt", "data".getBytes, "text/plain", Some("Simple data"), Some(EmailAttachment.INLINE))
      ),
      bodyText = Some("A text message"),
      bodyHtml = Some("<html><body><p>An <b>html</b> message</p></body></html>")
    )
    MailerPlugin.send(email)
    Ok(views.html.index("Your new application is ready."))
  }
}
