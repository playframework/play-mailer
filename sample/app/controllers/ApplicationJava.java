package controllers;

import org.apache.commons.mail.EmailAttachment;
import play.Configuration;
import play.Play;
import play.api.libs.mailer.MailerClient;
import play.libs.mailer.Email;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ApplicationJava extends Controller {

  private final MailerClient mailer;

  @Inject
  public ApplicationJava(MailerClient mailer) {
    this.mailer = mailer;
  }

  public Result send() {
    final Email email = new Email()
      .setSubject("Simple email")
      .setFrom("Mister FROM <from@email.com>")
      .addTo("Miss TO <to@email.com>")
      .addAttachment("favicon.png", new File(Play.application().classloader().getResource("public/images/favicon.png").getPath()))
      .addAttachment("data.txt", "data".getBytes(), "text/plain", "Simple data", EmailAttachment.INLINE)
      .setBodyText("A text message")
      .setBodyHtml("<html><body><p>An <b>html</b> message</p></body></html>");
    String id = mailer.send(email);
    return ok("Email " + id + " sent!");
  }

  public Result configureAndSend() {
    final Email email = new Email()
      .setSubject("Simple email")
      .setFrom("from@email.com")
      .addTo("to@email.com");
    Map<String, Object> conf = new HashMap<>();
    conf.put("host", "typesafe.org");
    conf.put("port", 1234);
    String id = mailer.configure(new Configuration(conf)).send(email);
    return ok("Email " + id + " sent!");
  }
}
