# Play-Mailer

[![Build Status](https://travis-ci.org/playframework/play-mailer.svg?branch=master)](https://travis-ci.org/playframework/play-mailer)

Play Mailer is a powerful Scala Mailing library. It provides a simple configurable mailer.

## Getting Started

To get started you add `play-mailer` and `play-mailer-guice` as a dependency in SBT:

```scala
libraryDependencies += "com.typesafe.play" %% "play-mailer" % -version-
libraryDependencies += "com.typesafe.play" %% "play-mailer-guice" % -version-
```

## Versioning

The Play Mailer plugin supports several different versions of Play.

| Plugin version   | Play version    |
|------------------|-----------------|
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
import org.apache.commons.mail.EmailAttachment
import javax.inject.Inject

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

If you use Compile time Injection you can remove `libraryDependencies += "com.typesafe.play" %% "play-mailer-guice" % -version-` from your `build.sbt`.

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
    new SMTPConfiguration("typesafe.org", 1234)
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
new SMTPMailer(SMTPConfiguration("typesafe.org", 1234)).send(email)
new SMTPMailer(SMTPConfiguration("playframework.com", 5678)).send(email)
```

### Java

For Java you can just create a simple MailerService and Inject the MailerClient into it:

```java
import play.libs.mailer.Email;
import play.libs.mailer.MailerClient;
import javax.inject.Inject;
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

## License


This software is licensed under the Apache 2 license, quoted below.

Copyright 2012 Typesafe (http://www.typesafe.com).

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this project except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

## Support

The Play Mailer library is *[Community Driven][]*.

[Community Driven]: https://developer.lightbend.com/docs/lightbend-platform/introduction/getting-help/support-terminology.html#community-driven
