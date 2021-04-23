package play.api.libs.mailer

import java.io.File
import java.net.URL
import javax.activation.DataSource

sealed trait Attachment

case class AttachmentData(
    name: String,
    data: Array[Byte],
    mimetype: String,
    description: Option[String] = None,
    disposition: Option[String] = None,
    contentId: Option[String] = None
) extends Attachment

case class AttachmentFile(
    name: String,
    file: File,
    description: Option[String] = None,
    disposition: Option[String] = None,
    contentId: Option[String] = None
) extends Attachment

case class AttachmentDataSource(
    name: String,
    dataSource: DataSource,
    description: Option[String] = None,
    disposition: Option[String] = None,
    contentId: Option[String] = None
) extends Attachment

case class AttachmentURL(
    name: String,
    url: URL,
    description: Option[String] = None,
    disposition: Option[String] = None,
    contentId: Option[String] = None
) extends Attachment