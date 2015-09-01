package controllers

import javax.inject.Provider

import play.api.libs.mailer.SMTPConfiguration
import play.api.{Configuration, Environment}
import play.api.inject.Module

class CustomSMTPConfigurationProvider extends Provider[SMTPConfiguration] {
  override def get() = new SMTPConfiguration("typesafe.org", 1234)
}

class CustomMailerConfigurationModule extends Module {
  def bindings(environment: Environment, configuration: Configuration) = Seq(
    bind[SMTPConfiguration].toProvider[CustomSMTPConfigurationProvider]
  )
}