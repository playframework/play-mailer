package play.api.libs.mailer

import java.io.{File, FilterOutputStream, PrintStream}
import javax.inject.{Inject, Provider}
import javax.mail.internet.InternetAddress

import org.apache.commons.mail._
import play.api.inject._
import play.api.{Configuration, Environment, Logger, PlayConfig}
import play.libs.mailer.{Email => JEmail, MailerClient => JMailerClient}

import scala.collection.JavaConverters._



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

class MailerConfigurationModule extends Module {
  def bindings(environment: Environment, configuration: Configuration) = Seq(
    bind[SMTPConfiguration].toProvider[SMTPConfigurationProvider]
  )
}
