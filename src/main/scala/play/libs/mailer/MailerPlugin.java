package play.libs.mailer;

import play.Play;
import play.api.libs.mailer.MailerClient;
import play.libs.mailer.Email;

/**
 * plugin access
 */
public class MailerPlugin {
    
    public static String send(Email data) {
        MailerClient client = play.Play.application().injector().instanceOf(MailerClient.class);
        return client.send(data);
    }
}