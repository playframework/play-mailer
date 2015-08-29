import play.api._
import play.api.ApplicationLoader.Context
import router.Routes
import play.api.libs.mailer._

class SimpleApplicationLoader extends ApplicationLoader {
  def load(context: Context) = {
    new ApplicationComponents(context).application
  }
}

class ApplicationComponents(context: Context) extends BuiltInComponentsFromContext(context) with MailerComponents {  
  lazy val applicationController = new controllers.ApplicationScala(mailerClient)
  lazy val assets = new controllers.Assets(httpErrorHandler)
  lazy val router = new Routes(httpErrorHandler, applicationController)
}