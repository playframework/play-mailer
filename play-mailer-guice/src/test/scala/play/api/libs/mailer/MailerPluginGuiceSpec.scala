package play.api.libs.mailer

import com.typesafe.config.ConfigFactory
import org.mockito.Mockito
import org.mockito.Mockito._
import org.specs2.mutable._
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test._

class MailerPluginGuiceSpec extends Specification {

  "The mailer module" should {
    import play.libs.mailer.{ MailerClient => JMailerClient }

    val mockedConfigurationProvider = mock(classOf[SMTPConfigurationProvider])
    when(mockedConfigurationProvider.get()).thenReturn(SMTPConfiguration("example.org", 25, mock = true))

    def createApp(additionalConfiguration: Map[String, _]): Application = {
      new GuiceApplicationBuilder()
        .configure(additionalConfiguration)
        .overrides(new ConfigModule) // Play 2.5.x "hack"
        .build()
    }

    val applicationWithMinimalMailerConfiguration = createApp(additionalConfiguration = Map("play.mailer.host" -> "example.org", "play.mailer.port" -> 25))

    val applicationWithMockedConfigurationProvider = new GuiceApplicationBuilder()
      .overrides(new ConfigModule) // Play 2.5.x "hack"
      .overrides(bind[SMTPConfiguration].to(mockedConfigurationProvider))
      .build()
    val applicationWithMoreMailerConfiguration = createApp(additionalConfiguration = Map("play.mailer.host" -> "example.org", "play.mailer.port" -> 25, "play.mailer.user" -> "typesafe", "play.mailer.password" -> "typesafe"))

    "provide the Scala mailer client" in new WithApplication(applicationWithMinimalMailerConfiguration) {
      override def running() = {
        app.injector.instanceOf[MailerClient] must beAnInstanceOf[SMTPDynamicMailer]
      }
    }
    "provide the Java mailer client" in new WithApplication(applicationWithMinimalMailerConfiguration) {
      override def running() = {
        app.injector.instanceOf[JMailerClient] must beAnInstanceOf[SMTPDynamicMailer]
      }
    }
    "provide the Scala mocked mailer client" in new WithApplication(applicationWithMinimalMailerConfiguration) {
      override def running() = {
        app.injector.instanceOf(bind[MailerClient].qualifiedWith("mock")) must beAnInstanceOf[MockMailer]
      }
    }
    "provide the Java mocked mailer client" in new WithApplication(applicationWithMinimalMailerConfiguration) {
      override def running() = {
        app.injector.instanceOf(bind[JMailerClient].qualifiedWith("mock")) must beAnInstanceOf[MockMailer]
      }
    }
    "call the configuration each time we send an email" in new WithApplication(applicationWithMockedConfigurationProvider) {
      override def running() = {
        val mail = Email("Test Configurable Mailer", "root@example.org")
        app.injector.instanceOf[MailerClient].send(mail)
        app.injector.instanceOf[MailerClient].send(mail)
        Mockito.verify(mockedConfigurationProvider, times(2)).get()
      }
    }
    "validate the configuration" in new WithApplication(applicationWithMoreMailerConfiguration) {
      override def running() = {
        app.injector.instanceOf(bind[SMTPConfiguration]) must ===(SMTPConfiguration("example.org", 25,
          user = Some("typesafe"), password = Some("typesafe"), props = ConfigFactory.parseString("ssl.checkserveridentity=true")))
      }
    }
  }
}
