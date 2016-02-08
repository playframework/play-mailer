package play.api.libs.mailer

import play.api.Configuration

// for compile-time injection
trait MailerComponents {
  def configuration: Configuration
  lazy val mailerClient = new SMTPMailer(new SMTPConfigurationProvider(configuration).get())
}
