package play.libs.mailer;

import play.Play;

/**
 * plugin access
 */
public class MailerPlugin {

  public static String send(Email data) {
    return Play.application().plugin(play.api.libs.mailer.MailerPlugin.class).instance().send(data);
  }
}
