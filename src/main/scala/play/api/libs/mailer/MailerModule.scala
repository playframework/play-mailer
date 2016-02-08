package play.api.libs.mailer

import play.api.inject._
import play.api.{Configuration, Environment}
import play.libs.mailer.{MailerClient => JMailerClient}


// for runtime injection
class MailerModule extends Module {
  def bindings(environment: Environment, configuration: Configuration) = Seq(
    bind[MailerClient].to[SMTPDynamicMailer],
    bind[JMailerClient].to(bind[MailerClient]),
    bind[MailerClient].qualifiedWith("mock").to[MockMailer],
    bind[JMailerClient].qualifiedWith("mock").to[MockMailer]
  )
}

class SMTPConfigurationModule extends Module {
  def bindings(environment: Environment, configuration: Configuration) = Seq(
    bind[SMTPConfiguration].toProvider[SMTPConfigurationProvider]
  )
}

@deprecated("Use SMTPConfigurationModule instead", "4.0.0")
class MailerConfigurationModule extends SMTPConfigurationModule
