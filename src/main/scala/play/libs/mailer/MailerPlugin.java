package play.libs.mailer;

import play.Play;
import play.api.libs.mailer.MailerAPI;

/**
 * plugin access
 */
public class MailerPlugin {

    public static MailerAPI email() {
        return Play.application().plugin(play.api.libs.mailer.MailerPlugin.class).email();
    }
}
