package controllers;

import java.io.File;

import org.apache.commons.mail.EmailAttachment;

import play.Play;
import play.libs.mailer.Email;
import play.libs.mailer.MailerPlugin;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

public class ApplicationJava extends Controller {
  
  public static Result index() {
    final Email email = new Email();
    email.setSubject("Simple email");
    email.setFrom("Mister FROM <from@email.com>");
    email.addTo("Miss TO <to@email.com>");
    email.addAttachment("favicon.png", new File(Play.application().classloader().getResource("public/images/favicon.png").getPath()));
    email.addAttachment("data.txt", "data".getBytes(), "text/plain", "Simple data", EmailAttachment.INLINE);
    email.setBodyText("A text message");
    email.setBodyHtml("<html><body><p>An <b>html</b> message</p></body></html>");
    MailerPlugin.send(email)
    return ok(index.render("Your new application is ready."));
  }
  
}
