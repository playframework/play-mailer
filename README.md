# Play Mailer

[![Twitter Follow](https://img.shields.io/twitter/follow/playframework?label=follow&style=flat&logo=twitter&color=brightgreen)](https://twitter.com/playframework)
[![Discord](https://img.shields.io/discord/931647755942776882?logo=discord&logoColor=white)](https://discord.gg/g5s2vtZ4Fa)
[![GitHub Discussions](https://img.shields.io/github/discussions/playframework/playframework?&logo=github&color=brightgreen)](https://github.com/playframework/playframework/discussions)
[![StackOverflow](https://img.shields.io/static/v1?label=stackoverflow&logo=stackoverflow&logoColor=fe7a16&color=brightgreen&message=playframework)](https://stackoverflow.com/tags/playframework)
[![YouTube](https://img.shields.io/youtube/channel/views/UCRp6QDm5SDjbIuisUpxV9cg?label=watch&logo=youtube&style=flat&color=brightgreen&logoColor=ff0000)](https://www.youtube.com/channel/UCRp6QDm5SDjbIuisUpxV9cg)
[![Twitch Status](https://img.shields.io/twitch/status/playframework?logo=twitch&logoColor=white&color=brightgreen&label=live%20stream)](https://www.twitch.tv/playframework)
[![OpenCollective](https://img.shields.io/opencollective/all/playframework?label=financial%20contributors&logo=open-collective)](https://opencollective.com/playframework)

[![Build Status](https://github.com/playframework/play-mailer/actions/workflows/build-test.yml/badge.svg)](https://github.com/playframework/play-mailer/actions/workflows/build-test.yml)
[![Maven](https://img.shields.io/maven-central/v/org.playframework/play-mailer_2.13.svg?logo=apache-maven)](https://mvnrepository.com/artifact/org.playframework/play-mailer_2.13)
[![Repository size](https://img.shields.io/github/repo-size/playframework/play-mailer.svg?logo=git)](https://github.com/playframework/play-mailer)
[![Scala Steward badge](https://img.shields.io/badge/Scala_Steward-helping-blue.svg?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=)](https://scala-steward.org)
[![Mergify Status](https://img.shields.io/endpoint.svg?url=https://api.mergify.com/v1/badges/playframework/play-mailer&style=flat)](https://mergify.com)

Play Mailer is a powerful Scala Mailing library. It provides a simple configurable mailer.

## Getting Started

To get started you add `play-mailer` and `play-mailer-guice` as a dependency in SBT:

```scala
libraryDependencies += "org.playframework" %% "play-mailer" % -version-
libraryDependencies += "org.playframework" %% "play-mailer-guice" % -version-

// Until version 9.x:
libraryDependencies += "com.typesafe.play" %% "play-mailer" % -version-
libraryDependencies += "com.typesafe.play" %% "play-mailer-guice" % -version-
```

## Versioning

The Play Mailer plugin supports several different versions of Play.

| Plugin version   | Play version    |
|------------------|-----------------|
| 10.x             | 3.0.x           |
| 9.x              | 2.9.x           |
| 8.x              | 2.8.x           |
| 7.x              | 2.7.x           |

See [GitHub releases](https://github.com/playframework/play-mailer/releases) for the latest versions.

After that you need to configure the mailer inside your `application.conf`:
 
```HOCON
play.mailer {
  host = "example.com" // (mandatory)
  port = 25 // (defaults to 25)
  ssl = no // (defaults to no)
  tls = no // (defaults to no)
  tlsRequired = no // (defaults to no)
  user = null // (optional)
  password = null // (optional)
  debug = no // (defaults to no, to take effect you also need to set the log level to "DEBUG" for the "play.mailer" logger)
  timeout = null // (defaults to 60s in milliseconds)
  connectiontimeout = null // (defaults to 60s in milliseconds)
  mock = no // (defaults to no, will only log all the email properties instead of sending an email)
  props {
    // Additional SMTP properties used by JavaMail. Can override existing configuration keys from above.
    // A given property will be set for both the "mail.smtp.*" and the "mail.smtps.*" prefix.
    // For a list of properties see:
    // https://javaee.github.io/javamail/docs/api/com/sun/mail/smtp/package-summary.html#properties

    // Example:
    // To set the local host name used in the SMTP HELO or EHLO command:
    // localhost = 127.0.0.1
    // Results in "mail.smtp.localhost=127.0.0.1" and "mail.smtps.localhost=127.0.0.1" in the JavaMail session.
  }
}
```

## Usage

### Scala

#### Runtime Injection

Use the `@Inject` annotation on the constructor, service of your component or controller:

```scala
import play.api.libs.mailer._
import java.io.File
import org.apache.commons.mail2.jakarta.EmailAttachment
import jakarta.inject.Inject

class MailerService @Inject() (mailerClient: MailerClient) {

  def sendEmail = {
    val cid = "1234"
    val email = Email(
      "Simple email",
      "Mister FROM <from@email.com>",
      Seq("Miss TO <to@email.com>"),
      // adds attachment
      attachments = Seq(
        AttachmentFile("attachment.pdf", new File("/some/path/attachment.pdf")),
        // adds inline attachment from byte array
        AttachmentData("data.txt", "data".getBytes, "text/plain", Some("Simple data"), Some(EmailAttachment.INLINE)),
        // adds cid attachment
        AttachmentFile("image.jpg", new File("/some/path/image.jpg"), contentId = Some(cid))
      ),
      // sends text, HTML or both...
      bodyText = Some("A text message"),
      bodyHtml = Some(s"""<html><body><p>An <b>html</b> message with cid <img src="cid:$cid"></p></body></html>""")
    )
    mailerClient.send(email)
  }

}
```

> Configuration will be retrieved each time mailerClient.send(email) is called. 
> This means that mailer client will always be up to date if you have a dynamic configuration.

#### Compile Time Injection

If you use Compile time Injection you can remove `libraryDependencies += "org.playframework" %% "play-mailer-guice" % -version-` from your `build.sbt`.

Create the MailerService without the `@Inject` annotation:

```scala
import play.api.libs.mailer._

class MyComponent(mailerClient: MailerClient) {

  def sendEmail = {
     val email = Email("Simple email", "Mister FROM <from@email.com>", Seq("Miss TO <to@email.com>"), bodyText = Some("A text message"))
     mailerClient.send(email)
  }
}
```

Then you need to register the `MailerComponents` trait in your main Components file:

```scala
import play.api._
import play.api.ApplicationLoader.Context
import router.Routes
import play.api.libs.mailer._

class MyApplicationLoader extends ApplicationLoader {
  def load(context: Context) = {
    new ApplicationComponents(context).application
  }
}

class ApplicationComponents(context: Context) extends BuiltInComponentsFromContext(context) with MailerComponents {
  lazy val myComponent = new MyComponent(mailerClient)
  // create your controllers here ...
  lazy val router = new Routes(...) // inject your controllers here
  lazy val config = configuration.underlying
}
```

#### Dynamic Configuration

By default the Mailer Plugin will automatically configure the injected instance with the `application.conf`.

If you want to configure the injected instances from another source, you will need to override the default provider:

Create a new file named `CustomSMTPConfigurationProvider.scala`:

```scala
class CustomSMTPConfigurationProvider extends Provider[SMTPConfiguration] {
  override def get() = {
    // Custom configuration
    new SMTPConfiguration("example.org", 1234)
  }
}

class CustomMailerConfigurationModule extends Module {
  def bindings(environment: Environment, configuration: Configuration) = Seq(
    bind[SMTPConfiguration].toProvider[CustomSMTPConfigurationProvider]
  )
}
```

And override the default provider inside you `application.conf`:

```HOCON
play.modules {
    # Disable the default provider
    disabled += "play.api.libs.mailer.SMTPConfigurationModule"
    # Enable the custom provider (see above)
    enabled += "controllers.CustomMailerConfigurationModule"
}
```

> The get() method of your CustomSMTPConfigurationProvider will be called multiple times.
> As a consequence, we recommend that code inside the get() method should be fast.


#### Multiple SMTPMailer instances

You can also use the SMTPMailer constructor to create new instances with custom configuration:

```scala
val email = Email("Simple email", "Mister FROM <from@email.com>")
new SMTPMailer(SMTPConfiguration("example.org", 1234)).send(email)
new SMTPMailer(SMTPConfiguration("playframework.com", 5678)).send(email)
```

### Java

For Java you can just create a simple MailerService and Inject the MailerClient into it:

```java
import play.libs.mailer.Email;
import play.libs.mailer.MailerClient;
import jakarta.inject.Inject;
import java.io.File;
import org.apache.commons.mail.EmailAttachment;

public class MailerService {
  @Inject MailerClient mailerClient;

  public void sendEmail() {
    String cid = "1234";
    Email email = new Email()
      .setSubject("Simple email")
      .setFrom("Mister FROM <from@email.com>")
      .addTo("Miss TO <to@email.com>")
      // adds attachment
      .addAttachment("attachment.pdf", new File("/some/path/attachment.pdf"))
      // adds inline attachment from byte array
      .addAttachment("data.txt", "data".getBytes(), "text/plain", "Simple data", EmailAttachment.INLINE)
      // adds cid attachment
      .addAttachment("image.jpg", new File("/some/path/image.jpg"), cid)
      // sends text, HTML or both...
      .setBodyText("A text message")
      .setBodyHtml("<html><body><p>An <b>html</b> message with cid <img src=\"cid:" + cid + "\"></p></body></html>");
    mailerClient.send(email);
  }
}
```

## Releasing a new version

See https://github.com/playframework/.github/blob/main/RELEASING.md

## License

This software is licensed under the Apache 2 license, quoted below.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this project except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
