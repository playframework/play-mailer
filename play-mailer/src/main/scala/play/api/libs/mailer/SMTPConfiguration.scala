package play.api.libs.mailer

import com.typesafe.config.{ Config, ConfigFactory }

import scala.util.Try

case class SMTPConfiguration(
    host: String,
    port: Int,
    ssl: Boolean = false,
    tls: Boolean = false,
    tlsRequired: Boolean = false,
    user: Option[String] = None,
    password: Option[String] = None,
    debugMode: Boolean = false,
    timeout: Option[Int] = None,
    connectionTimeout: Option[Int] = None,
    props: Config = ConfigFactory.empty(),
    mock: Boolean = false
)

object SMTPConfiguration {

  @inline
  private def getOptionString(config: Config, name: String) = {
    Try(config.getString(name)).toOption
  }

  @inline
  private def getOptionInt(config: Config, name: String) = {
    Try(config.getInt(name)).toOption
  }

  def apply(config: Config) = new SMTPConfiguration(
    resolveHost(config),
    config.getInt("port"),
    config.getBoolean("ssl"),
    config.getBoolean("tls"),
    config.getBoolean("tlsRequired"),
    getOptionString(config, "user"),
    getOptionString(config, "password"),
    config.getBoolean("debug"),
    getOptionInt(config, "timeout"),
    getOptionInt(config, "connectiontimeout"),
    config.getConfig("props"),
    config.getBoolean("mock")
  )

  def resolveHost(config: Config): String = {
    if (config.getBoolean("mock")) {
      // host won't be used anyway...
      ""
    } else {
      getOptionString(config, "host").getOrElse(throw new RuntimeException("host needs to be set in order to use this plugin (or set play.mailer.mock to true in application.conf)"))
    }
  }

}

