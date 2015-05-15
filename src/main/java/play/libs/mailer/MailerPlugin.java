package play.libs.mailer;

import play.Play;

/**
 * Mailer plugin
 *
 * @deprecated Use injected MailerClient instead
 */
@Deprecated
public class MailerPlugin {

    public static String send(Email data) {
        return Play.application().injector().instanceOf(MailerClient.class).send(data);
    }
}
