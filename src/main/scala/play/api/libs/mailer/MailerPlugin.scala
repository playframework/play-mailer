package play.api.libs.mailer


@deprecated("Use injected MailerClient instead", "2.4.0")
object MailerPlugin {

  def send(email: Email)(implicit app: play.api.Application) = app.injector.instanceOf[MailerClient].send(email)
}
