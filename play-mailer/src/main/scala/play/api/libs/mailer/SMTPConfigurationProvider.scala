package play.api.libs.mailer

import jakarta.inject.{ Inject, Provider }

import com.typesafe.config.Config

class SMTPConfigurationProvider @Inject() (config: Config) extends Provider[SMTPConfiguration] {
  override def get(): SMTPConfiguration = {
    SMTPConfiguration(config.getConfig("play.mailer"))
  }
}
