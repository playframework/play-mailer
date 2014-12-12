package play.api.libs.mailer

import java.io.File
import javax.mail.Part

import org.apache.commons.mail.{EmailConstants, HtmlEmail, MultiPartEmail}
import org.specs2.mutable._

class MailerPluginSpec extends Specification {

  object SimpleMailerAPI extends MailerAPI {
    override def send(data: Email): String = ""
    override def convert(data: play.libs.mailer.Email) = super.convert(data)
  }
  class MockMultiPartEmail extends MultiPartEmail {
    override def getPrimaryBodyPart = super.getPrimaryBodyPart
    override def getContainer = super.getContainer
  }
  class MockHtmlEmail extends HtmlEmail {
    def getHtml = this.html
    def getText = this.text
    override def getPrimaryBodyPart = super.getPrimaryBodyPart
    override def getContainer = super.getContainer
  }
  object MockCommonsMailer extends MockCommonsMailerWithTimeouts(None, None)

  class MockCommonsMailerWithTimeouts(smtpTimeout: Option[Int], smtpConnectionTimeout: Option[Int])
    extends CommonsMailer("typesafe.org", 1234, true, false, Some("user"), Some("password"), false, smtpTimeout, smtpConnectionTimeout) {
    override def send(email: MultiPartEmail) = ""
    override def createMultiPartEmail(): MultiPartEmail = new MockMultiPartEmail
    override def createHtmlEmail(): HtmlEmail = new MockHtmlEmail
  }

  "The CommonsMailer" should {
    "configure SMTP" in {
      val mailer = MockCommonsMailer
      val email = mailer.createEmail(Email(
        subject = "Subject",
        from = "James Roper <jroper@typesafe.com>"
      ))
      email.getSmtpPort mustEqual "1234"
      email.getSslSmtpPort mustEqual "1234"
      email.getMailSession.getProperty("mail.smtp.auth") mustEqual "true"
      email.getMailSession.getProperty("mail.smtp.host") mustEqual "typesafe.org"
      email.getMailSession.getProperty("mail.smtp.starttls.enable") mustEqual "false"
      email.getMailSession.getProperty("mail.debug") mustEqual "false"
    }

    "configure the SMTP timeouts if configured" in {
      val mailer = new MockCommonsMailerWithTimeouts(Some(10), Some(99))
      val email = mailer.createEmail(Email(
        subject = "Subject",
        from = "James Roper <jroper@typesafe.com>"
      ))
      email.getSocketTimeout mustEqual 10
      email.getSocketConnectionTimeout mustEqual 99
    }

    "leave default SMTP timeouts if they are not configured" in {
      val mailer = new MockCommonsMailerWithTimeouts(None, None)
      val email = mailer.createEmail(Email(
        subject = "Subject",
        from = "James Roper <jroper@typesafe.com>"
      ))
      email.getSocketTimeout mustEqual EmailConstants.SOCKET_TIMEOUT_MS
      email.getSocketConnectionTimeout mustEqual EmailConstants.SOCKET_TIMEOUT_MS
    }

    "create an empty email" in {
      val mailer = MockCommonsMailer
      val messageId = mailer.send(Email(
        subject = "Subject",
        from = "James Roper <jroper@typesafe.com>",
        to = Seq("Guillaume Grossetie <ggrossetie@localhost.com>")
      ))
      messageId mustEqual ""
    }

    "create a simple email" in {
      val mailer = MockCommonsMailer
      val email = mailer.createEmail(Email(
        subject = "Subject",
        from = "James Roper <jroper@typesafe.com>",
        to = Seq("Guillaume Grossetie <ggrossetie@localhost.com>"),
        bodyText = Some("A text message"),
        bodyHtml = Some("<html><body><p>An <b>html</b> message</p></body></html>")
      ))
      simpleEmailMust(email)
      email must beAnInstanceOf[HtmlEmail]
      email must beAnInstanceOf[MockHtmlEmail]
      email.asInstanceOf[MockHtmlEmail].getText mustEqual "A text message"
      email.asInstanceOf[MockHtmlEmail].getHtml mustEqual "<html><body><p>An <b>html</b> message</p></body></html>"
    }

    "create a simple email with attachment" in {
      val mailer = MockCommonsMailer
      val email = mailer.createEmail(Email(
        subject = "Subject",
        from = "James Roper <jroper@typesafe.com>",
        to = Seq("Guillaume Grossetie <ggrossetie@localhost.com>"),
        bodyText = Some("A text message"),
        attachments = Seq(AttachmentFile("play icon", getPlayIcon))
      ))
      simpleEmailMust(email)
      email must beAnInstanceOf[MultiPartEmail]
      email must beAnInstanceOf[MockMultiPartEmail]
      email.asInstanceOf[MockMultiPartEmail].getContainer.getCount mustEqual 2
      val textPart = email.asInstanceOf[MockMultiPartEmail].getContainer.getBodyPart(0)
      textPart.getContentType mustEqual "text/plain"
      textPart.getContent mustEqual "A text message"
      email.asInstanceOf[MockMultiPartEmail].getPrimaryBodyPart.getContent mustEqual "A text message"
      val attachmentPart = email.asInstanceOf[MockMultiPartEmail].getContainer.getBodyPart(1)
      attachmentPart.getFileName mustEqual "play icon"
      attachmentPart.getDescription mustEqual "play icon"
      attachmentPart.getDisposition mustEqual Part.ATTACHMENT
    }

    "create a simple email with inline attachment and description" in {
      val mailer = MockCommonsMailer
      val email = mailer.createEmail(Email(
        subject = "Subject",
        from = "James Roper <jroper@typesafe.com>",
        to = Seq("Guillaume Grossetie <ggrossetie@localhost.com>"),
        bodyText = Some("A text message"),
        attachments = Seq(AttachmentFile("play icon", getPlayIcon, Some("A beautiful icon"), Some(Part.INLINE)))
      ))
      simpleEmailMust(email)
      email must beAnInstanceOf[MultiPartEmail]
      email must beAnInstanceOf[MockMultiPartEmail]
      email.asInstanceOf[MockMultiPartEmail].getContainer.getCount mustEqual 2
      val textPart = email.asInstanceOf[MockMultiPartEmail].getContainer.getBodyPart(0)
      textPart.getContentType mustEqual "text/plain"
      textPart.getContent mustEqual "A text message"
      email.asInstanceOf[MockMultiPartEmail].getPrimaryBodyPart.getContent mustEqual "A text message"
      val attachmentPart = email.asInstanceOf[MockMultiPartEmail].getContainer.getBodyPart(1)
      attachmentPart.getFileName mustEqual "play icon"
      attachmentPart.getDescription mustEqual "A beautiful icon"
      attachmentPart.getDisposition mustEqual Part.INLINE
    }
  }

  "The MailerAPI" should {
    "convert email from Java to Scala" in {
      val data = new play.libs.mailer.Email()
      data.setSubject("Subject")
      data.setFrom("James Roper <jroper@typesafe.com>")
      data.addTo("Guillaume Grossetie <ggrossetie@localhost.com>")
      data.addCc("Daniel Spasojevic <dspasojevic@github.com>")
      data.addBcc("Sparkbitpl <sparkbitpl@github.com>")
      data.setBodyText("A text message")
      data.setBodyHtml("<html><body><p>An <b>html</b> message</p></body></html>")
      data.setCharset("UTF-16")
      data.addHeader("key", "value")
      data.addAttachment("play icon", getPlayIcon, "A beautiful icon", Part.ATTACHMENT)
      data.addAttachment("data.txt", "data".getBytes, "text/plain", "Simple data", Part.INLINE)

      val convert = SimpleMailerAPI.convert(data)
      convert.subject mustEqual "Subject"
      convert.from mustEqual "James Roper <jroper@typesafe.com>"
      convert.to.size mustEqual 1
      convert.to.head mustEqual "Guillaume Grossetie <ggrossetie@localhost.com>"
      convert.cc.size mustEqual 1
      convert.cc.head mustEqual "Daniel Spasojevic <dspasojevic@github.com>"
      convert.bcc.size mustEqual 1
      convert.bcc.head mustEqual "Sparkbitpl <sparkbitpl@github.com>"
      convert.bodyText mustEqual Some("A text message")
      convert.bodyHtml mustEqual Some("<html><body><p>An <b>html</b> message</p></body></html>")
      convert.charset mustEqual Some("UTF-16")
      convert.headers.size mustEqual 1
      convert.headers.head mustEqual ("key", "value")
      convert.attachments.size mustEqual 2
      convert.attachments(0) must beAnInstanceOf[AttachmentFile]
      convert.attachments(0).asInstanceOf[AttachmentFile].name mustEqual "play icon"
      convert.attachments(0).asInstanceOf[AttachmentFile].file mustEqual getPlayIcon
      convert.attachments(0).asInstanceOf[AttachmentFile].description mustEqual Some("A beautiful icon")
      convert.attachments(0).asInstanceOf[AttachmentFile].disposition mustEqual Some(Part.ATTACHMENT)
      convert.attachments(1) must beAnInstanceOf[AttachmentData]
      convert.attachments(1).asInstanceOf[AttachmentData].name mustEqual "data.txt"
      convert.attachments(1).asInstanceOf[AttachmentData].data mustEqual "data".getBytes
      convert.attachments(1).asInstanceOf[AttachmentData].description mustEqual Some("Simple data")
      convert.attachments(1).asInstanceOf[AttachmentData].disposition mustEqual Some(Part.INLINE)
    }
  }

  def simpleEmailMust(email: MultiPartEmail) {
    email.getSubject mustEqual "Subject"
    email.getFromAddress.getPersonal mustEqual "James Roper"
    email.getFromAddress.getAddress mustEqual "jroper@typesafe.com"
    email.getToAddresses must have size 1
    email.getToAddresses.get(0).getPersonal mustEqual "Guillaume Grossetie"
    email.getToAddresses.get(0).getAddress mustEqual "ggrossetie@localhost.com"
  }

  def getPlayIcon: File = {
    new File(Thread.currentThread().getContextClassLoader.getResource("play_icon_full_color.png").toURI)
  }
}
