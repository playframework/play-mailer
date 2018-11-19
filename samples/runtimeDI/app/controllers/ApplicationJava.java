package controllers;

import org.apache.commons.mail.EmailAttachment;
import play.api.libs.mailer.MailerClient;
import play.libs.mailer.Email;
import play.mvc.Controller;
import play.mvc.Result;
import play.Environment;

import javax.inject.Inject;
import java.io.File;

public class ApplicationJava extends Controller {

  private final Environment environment;
  private final MailerClient mailer;

  @Inject
  public ApplicationJava(Environment environment, MailerClient mailer) {
    this.environment = environment;
    this.mailer = mailer;
  }

  public Result send() {
    String cid = "1234";
    final Email email = new Email()
      .setSubject("Simple email")
      .setFrom("Mister FROM <from@email.com>")
      .addTo("Miss TO <to@email.com>")
      .addAttachment("favicon.png", new File(environment.getFile("public/images/favicon.png"), cid))
      .addAttachment("data.txt", "data".getBytes(), "text/plain", "Simple data", EmailAttachment.INLINE)
      .setBodyText("A text message")
      .setBodyHtml("<html><body><p>An <b>html</b> message with cid <img src=\"cid:" + cid + "\"></p></body></html>");
    String id = mailer.send(email);
    return ok("Email " + id + " sent!");
  }
}
