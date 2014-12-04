package play.libs.mailer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Email {

  private String subject;
  private String from;
  private String bodyText;
  private String bodyHtml;
  private List<String> to = new ArrayList<String>();
  private List<String> cc = new ArrayList<String>();
  private List<String> bcc = new ArrayList<String>();
  private String replyTo;
  private List<Attachment> attachments = new ArrayList<Attachment>();
  private String charset;
  private Map<String, String> headers = new HashMap<String, String>();

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getFrom() {
    return from;
  }

  public void setFrom(String from) {
    this.from = from;
  }

  public String getBodyText() {
    return bodyText;
  }

  public void setBodyText(String bodyText) {
    this.bodyText = bodyText;
  }

  public String getBodyHtml() {
    return bodyHtml;
  }

  public void setBodyHtml(String bodyHtml) {
    this.bodyHtml = bodyHtml;
  }

  public void addTo(String address) {
    this.to.add(address);
  }

  public List<String> getTo() {
    return to;
  }

  public void setTo(List<String> to) {
    this.to = to;
  }

  public void addCc(String address) {
    this.cc.add(address);
  }

  public List<String> getCc() {
    return cc;
  }

  public void setCc(List<String> cc) {
    this.cc = cc;
  }

  public void addBcc(String address) {
    this.bcc.add(address);
  }

  public List<String> getBcc() {
    return bcc;
  }

  public void setBcc(List<String> bcc) {
    this.bcc = bcc;
  }

  public String getReplyTo() {
    return replyTo;
  }

  public void setReplyTo(String replyTo) {
    this.replyTo = replyTo;
  }

  public void addAttachment(String name, File file) {
    this.attachments.add(new Attachment(name, file));
  }

  public void addAttachment(String name, File file, String description, String disposition) {
    this.attachments.add(new Attachment(name, file, description, disposition));
  }

  public void addAttachment(String name, byte[] data, String mimeType) {
    this.attachments.add(new Attachment(name, data, mimeType));
  }

  public void addAttachment(String name, byte[] data, String mimeType, String description, String disposition) {
    this.attachments.add(new Attachment(name, data, mimeType, description, disposition));
  }

  public List<Attachment> getAttachments() {
    return attachments;
  }

  public void setAttachments(List<Attachment> attachments) {
    this.attachments = attachments;
  }

  public String getCharset() {
    return charset;
  }

  public void setCharset(String charset) {
    this.charset = charset;
  }

  public void addHeader(String key, String value) {
    this.headers.put(key, value);
  }

  public Map<String, String> getHeaders() {
    return headers;
  }

  public void setHeaders(Map<String, String> headers) {
    this.headers = headers;
  }
}
