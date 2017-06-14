package play.api.libs.mailer

import com.google.inject.AbstractModule
import com.google.inject.name.Names
import play.libs.mailer.{ MailerClient => JMailerClient }

class MailerModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[MailerClient]).to(classOf[SMTPDynamicMailer])
    bind(classOf[JMailerClient]).to(classOf[MailerClient])
    bind(classOf[MailerClient]).annotatedWith(Names.named("mock")).to(classOf[MockMailer])
    bind(classOf[JMailerClient]).annotatedWith(Names.named("mock")).to(classOf[MockMailer])
  }

}
