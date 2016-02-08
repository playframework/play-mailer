package play.api.libs.mailer

import javax.inject.{Provider, Inject}

import play.api.{Configuration, PlayConfig}


case class SMTPConfiguration(host: String,
                             port: Int,
                             ssl: Boolean = false,
                             tls: Boolean = false,
                             user: Option[String] = None,
                             password: Option[String] = None,
                             debugMode: Boolean = false,
                             timeout: Option[Int] = None,
                             connectionTimeout: Option[Int] = None,
                             mock: Boolean = false)

object SMTPConfiguration {

  def apply(config: PlayConfig) = new SMTPConfiguration(
    resolveHost(config),
    config.get[Int]("port"),
    config.get[Boolean]("ssl"),
    config.get[Boolean]("tls"),
    config.getOptional[String]("user"),
    config.getOptional[String]("password"),
    config.get[Boolean]("debug"),
    config.getOptional[Int]("timeout"),
    config.getOptional[Int]("connectiontimeout"),
    config.get[Boolean]("mock")
  )

  def resolveHost(config: PlayConfig) = {
    if (config.get[Boolean]("mock")) {
      // host won't be used anyway...
      ""
    } else {
      config.getOptional[String]("host").getOrElse(throw new RuntimeException("host needs to be set in order to use this plugin (or set play.mailer.mock to true in application.conf)"))
    }
  }
}

class SMTPConfigurationProvider @Inject()(configuration: Configuration) extends Provider[SMTPConfiguration] {
  override def get() = {
    val config = PlayConfig(configuration).getDeprecatedWithFallback("play.mailer", "smtp")
    SMTPConfiguration(config)
  }
}
