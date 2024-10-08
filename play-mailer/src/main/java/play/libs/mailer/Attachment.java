package play.libs.mailer;

import jakarta.activation.DataSource;
import java.io.File;
import java.net.URL;

public class Attachment {

  private String name;
  private File file;
  private String description;
  private String disposition;
  private byte[] data;
  private DataSource dataSource;
  private URL url;
  private String mimetype;
  private String contentId;

  public Attachment(String name, File file, String description, String disposition) {
    this.name = name;
    this.file = file;
    this.description = description;
    this.disposition = disposition;
  }

  public Attachment(String name, File file) {
    this.name = name;
    this.file = file;
  }

  public Attachment(String name, File file, String contentId) {
    this.name = name;
    this.file = file;
    this.contentId = contentId;
  }

  public Attachment(String name, byte[] data) {
    this.name = name;
    this.data = data;
  }

  public Attachment(String name, byte[] data, String mimetype) {
    this.name = name;
    this.data = data;
    this.mimetype = mimetype;
  }

  public Attachment(String name, byte[] data, String mimetype, String contentId) {
    this.name = name;
    this.data = data;
    this.mimetype = mimetype;
    this.contentId = contentId;
  }

  public Attachment(String name, byte[] data, String mimetype, String description, String disposition) {
    this.name = name;
    this.data = data;
    this.mimetype = mimetype;
    this.description = description;
    this.disposition = disposition;
  }

  public Attachment(String name, DataSource dataSource) {
    this.name = name;
    this.dataSource = dataSource;
  }

  public Attachment(String name, DataSource dataSource, String contentId) {
    this.name = name;
    this.dataSource = dataSource;
    this.contentId = contentId;
  }

  public Attachment(String name, DataSource dataSource, String description, String disposition) {
    this.name = name;
    this.dataSource = dataSource;
    this.description = description;
    this.disposition = disposition;
  }

  public Attachment(String name, URL url) {
    this.name = name;
    this.url = url;
  }

  public Attachment(String name, URL url, String contentId) {
    this.name = name;
    this.url = url;
    this.contentId = contentId;
  }

  public Attachment(String name, URL url, String description, String disposition) {
    this.name = name;
    this.url = url;
    this.description = description;
    this.disposition = disposition;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public File getFile() {
    return file;
  }

  public void setFile(File file) {
    this.file = file;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDisposition() {
    return disposition;
  }

  public void setDisposition(String disposition) {
    this.disposition = disposition;
  }

  public byte[] getData() {
    return data;
  }

  public void setData(byte[] data) {
    this.data = data;
  }

  public DataSource getDataSource() {
    return dataSource;
  }

  public void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public URL getUrl() {
    return url;
  }

  public void setUrl(URL url) {
    this.url = url;
  }

  public String getMimetype() {
    return mimetype;
  }

  public void setMimetype(String mimetype) {
    this.mimetype = mimetype;
  }

  public String getContentId() {
    return contentId;
  }

  public void setContentId(String contentId) {
    this.contentId = contentId;
  }
}
