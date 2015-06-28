package controllers

import java.io.File
import javax.inject.Inject

import org.apache.commons.mail.EmailAttachment
import play.api.Configuration
import play.api.Play.current
import play.api.libs.mailer._
import play.api.mvc.{Action, Controller}

class ApplicationScala @Inject()(mailer: MailerClient) extends Controller {

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
    val id = mailer.send(email)
    Ok(s"Email $id sent!")
  }

  def configureAndSend = Action {
    val email = Email("Simple email", "from@email.com", Seq("to@email.com"))
    val id = mailer.configure(Configuration.from(Map("host" -> "typesafe.org", "port" -> 1234))).send(email)
    Ok(s"Email $id sent!")
  }
}
