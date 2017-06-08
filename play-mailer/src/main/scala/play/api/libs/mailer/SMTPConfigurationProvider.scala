package play.api.libs.mailer

import javax.inject.{ Inject, Provider }

import com.typesafe.config.Config

class SMTPConfigurationProvider @Inject() (configuration: Config) extends Provider[SMTPConfiguration] {

  override def get(): SMTPConfiguration = {
    SMTPConfiguration(configuration.getConfig("play.mailer"))
  }

}
