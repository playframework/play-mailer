package play.api.libs.mailer

import com.typesafe.config.Config

trait MailerComponents {
  def config: Config
  lazy val mailerClient: SMTPMailer = new SMTPMailer(new SMTPConfigurationProvider(config).get())
}
