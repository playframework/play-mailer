package play.api.libs.mailer

import org.specs2.mock.Mockito
import org.specs2.mutable._
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test._

class MailerModuleSpec extends Specification with Mockito {

  "The mailer module" should {
    import play.libs.mailer.{ MailerClient => JMailerClient }
    import play.api.inject.bind

    val mockedConfigurationProvider = mock[SMTPConfigurationProvider]
    mockedConfigurationProvider.get() returns SMTPConfiguration("typesafe.org", 25, mock = true)

    def createApp(additionalConfiguration: Map[String, _]): Application = {
      new GuiceApplicationBuilder().configure(additionalConfiguration).build()
    }

    val applicationWithMinimalMailerConfiguration = createApp(additionalConfiguration = Map("play.mailer.host" -> "typesafe.org", "play.mailer.port" -> 25))
    val applicationWithMoreMailerConfiguration = createApp(additionalConfiguration = Map("play.mailer.host" -> "typesafe.org", "play.mailer.port" -> 25, "play.mailer.user" -> "typesafe", "play.mailer.password" -> "typesafe"))
    val applicationWithMockedConfigurationProvider = new GuiceApplicationBuilder().overrides(bind[SMTPConfiguration].to(mockedConfigurationProvider)).build()

    "provide the Scala mailer client" in new WithApplication(applicationWithMinimalMailerConfiguration) {
      app.injector.instanceOf[MailerClient] must beAnInstanceOf[SMTPDynamicMailer]
    }
    "provide the Java mailer client" in new WithApplication(applicationWithMinimalMailerConfiguration) {
      app.injector.instanceOf[JMailerClient] must beAnInstanceOf[SMTPDynamicMailer]
    }
    "provide the Scala mocked mailer client" in new WithApplication() {
      app.injector.instanceOf(bind[MailerClient].qualifiedWith("mock")) must beAnInstanceOf[MockMailer]
    }
    "provide the Java mocked mailer client" in new WithApplication() {
      app.injector.instanceOf(bind[JMailerClient].qualifiedWith("mock")) must beAnInstanceOf[MockMailer]
    }
    "call the configuration each time we send an email" in new WithApplication(applicationWithMockedConfigurationProvider) {
      val mail = Email("Test Configurable Mailer", "root@typesafe.org")
      app.injector.instanceOf[MailerClient].send(mail)
      app.injector.instanceOf[MailerClient].send(mail)
      there was two(mockedConfigurationProvider).get()
    }
    "validate the configuration" in new WithApplication(applicationWithMoreMailerConfiguration) {
      app.injector.instanceOf(bind[SMTPConfiguration]) must ===(SMTPConfiguration("typesafe.org", 25, user = Some("typesafe"), password = Some("typesafe")))
    }
  }

}
