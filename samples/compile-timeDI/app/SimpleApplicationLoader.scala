import play.api._
import play.api.ApplicationLoader.Context
import router.Routes
import play.api.libs.mailer._

class SimpleApplicationLoader extends ApplicationLoader {
  def load(context: Context) = {
    new ApplicationComponents(context).application
  }
}

class ApplicationComponents(context: Context) extends BuiltInComponentsFromContext(context) with MailerComponents with play.api.NoHttpFiltersComponents {
  lazy val applicationController = new _root_.controllers.ApplicationScala(mailerClient, environment, controllerComponents)
  lazy val router = new Routes(httpErrorHandler, applicationController)
  lazy val config = configuration.underlying
}