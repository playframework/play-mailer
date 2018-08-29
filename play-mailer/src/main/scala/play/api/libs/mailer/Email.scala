package play.api.libs.mailer

case class Email(
    subject: String,
    from: String,
    to: Seq[String] = Seq.empty,
    bodyText: Option[String] = None,
    bodyHtml: Option[String] = None,
    charset: Option[String] = None,
    cc: Seq[String] = Seq.empty,
    bcc: Seq[String] = Seq.empty,
    replyTo: Seq[String] = Seq.empty,
    bounceAddress: Option[String] = None,
    attachments: Seq[Attachment] = Seq.empty,
    headers: Seq[(String, String)] = Seq.empty)
