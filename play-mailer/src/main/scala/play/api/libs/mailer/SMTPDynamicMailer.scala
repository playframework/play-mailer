package play.api.libs.mailer

import javax.inject.{ Inject, Provider }

class SMTPDynamicMailer @Inject() (smtpConfigurationProvider: Provider[SMTPConfiguration]) extends MailerClient {

  override def send(data: Email): String = {
    new SMTPMailer(smtpConfigurationProvider.get()).send(data)
  }

}
