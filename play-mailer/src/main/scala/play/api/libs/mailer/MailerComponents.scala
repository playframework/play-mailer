package play.api.libs.mailer

import com.typesafe.config.Config

// for compile-time injection
trait MailerComponents {

  def config: Config

  lazy val mailerClient: SMTPMailer = new SMTPMailer(new SMTPConfigurationProvider(config).get())

}

