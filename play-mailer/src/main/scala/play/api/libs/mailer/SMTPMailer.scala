package play.api.libs.mailer

import javax.inject.Inject

import org.apache.commons.mail.{ HtmlEmail, MultiPartEmail }

class SMTPMailer @Inject() (smtpConfiguration: SMTPConfiguration) extends MailerClient {

  private lazy val instance = {
    if (smtpConfiguration.mock) {
      new MockMailer()
    } else {
      new CommonsMailer(smtpConfiguration) {
        override def send(email: MultiPartEmail): String = email.send()

        override def createMultiPartEmail(): MultiPartEmail = new MultiPartEmail()

        override def createHtmlEmail(): HtmlEmail = new HtmlEmail()
      }
    }
  }

  override def send(data: Email): String = {
    instance.send(data)
  }
}
