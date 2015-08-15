package play.libs.mailer;

import play.Configuration;

/**
 * A mailer client.
 */
public interface MailerClient {

  /**
   * Sends an email with the provided data.
   *
   * @param email The email to send.
   * @return The message id.
   */
  String send(Email email);
}
