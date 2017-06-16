package play.api.libs.mailer

import com.google.inject.AbstractModule

class SMTPConfigurationModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[SMTPConfiguration]).toProvider(classOf[SMTPConfigurationProvider])
  }

}
