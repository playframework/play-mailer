package play.api.libs.mailer

import javax.inject.Inject

import org.slf4j.LoggerFactory

class MockMailer @Inject() extends MailerClient {

  protected val logger = LoggerFactory.getLogger("play.mailer")

  override def send(email: Email): String = {
    logger.info("mock implementation, send email")
    logger.info(s"subject: ${email.subject}")
    logger.info(s"from: ${email.from}")
    email.bodyText.foreach(bodyText => logger.info(s"bodyText: $bodyText"))
    email.bodyHtml.foreach(bodyHtml => logger.info(s"bodyHtml: $bodyHtml"))
    email.to.foreach(to => logger.info(s"to: $to"))
    email.cc.foreach(cc => logger.info(s"cc: $cc"))
    email.bcc.foreach(bcc => logger.info(s"bcc: $bcc"))
    email.replyTo.foreach(replyTo => logger.info(s"replyTo: $replyTo"))
    email.bounceAddress.foreach(bounce => logger.info(s"bounceAddress: $bounce"))
    email.attachments.foreach(attachment => logger.info(s"attachment: $attachment"))
    email.headers.foreach(header => logger.info(s"header: $header"))
    ""
  }
}
