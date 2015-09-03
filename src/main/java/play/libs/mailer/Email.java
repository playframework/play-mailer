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
  private String bounceAddress;
  private List<Attachment> attachments = new ArrayList<Attachment>();
  private String charset;
  private Map<String, String> headers = new HashMap<String, String>();

  public String getSubject() {
    return subject;
  }

  public Email setSubject(String subject) {
    this.subject = subject;
    return this;
  }

  public String getFrom() {
    return from;
  }

  public Email setFrom(String from) {
    this.from = from;
    return this;
  }

  public String getBodyText() {
    return bodyText;
  }

  public Email setBodyText(String bodyText) {
    this.bodyText = bodyText;
    return this;
  }

  public String getBodyHtml() {
    return bodyHtml;
  }

  public Email setBodyHtml(String bodyHtml) {
    this.bodyHtml = bodyHtml;
    return this;
  }

  public Email addTo(String address) {
    this.to.add(address);
    return this;
  }

  public List<String> getTo() {
    return to;
  }

  public Email setTo(List<String> to) {
    this.to = to;
    return this;
  }

  public Email addCc(String address) {
    this.cc.add(address);
    return this;
  }

  public List<String> getCc() {
    return cc;
  }

  public Email setCc(List<String> cc) {
    this.cc = cc;
    return this;
  }

  public Email addBcc(String address) {
    this.bcc.add(address);
    return this;
  }

  public List<String> getBcc() {
    return bcc;
  }

  public Email setBcc(List<String> bcc) {
    this.bcc = bcc;
    return this;
  }

  public String getReplyTo() {
    return replyTo;
  }

  public Email setReplyTo(String replyTo) {
    this.replyTo = replyTo;
    return this;
  }

  public String getBounceAddress() {
    return bounceAddress;
  }

  public Email setBounceAddress(String bounceAddress) {
    this.bounceAddress = bounceAddress;
    return this;
  }

  public Email addAttachment(String name, File file) {
    this.attachments.add(new Attachment(name, file));
    return this;
  }

  public Email addAttachment(String name, File file, String contentId) {
    this.attachments.add(new Attachment(name, file, contentId));
    return this;
  }

  public Email addAttachment(String name, File file, String description, String disposition) {
    this.attachments.add(new Attachment(name, file, description, disposition));
    return this;
  }

  public Email addAttachment(String name, byte[] data, String mimeType) {
    this.attachments.add(new Attachment(name, data, mimeType));
    return this;
  }

  public Email addAttachment(String name, byte[] data, String mimeType, String contentId) {
    this.attachments.add(new Attachment(name, data, mimeType, contentId));
    return this;
  }

  public Email addAttachment(String name, byte[] data, String mimeType, String description, String disposition) {
    this.attachments.add(new Attachment(name, data, mimeType, description, disposition));
    return this;
  }

  public List<Attachment> getAttachments() {
    return attachments;
  }

  public Email setAttachments(List<Attachment> attachments) {
    this.attachments = attachments;
    return this;
  }

  public String getCharset() {
    return charset;
  }

  public Email setCharset(String charset) {
    this.charset = charset;
    return this;
  }

  public Email addHeader(String key, String value) {
    this.headers.put(key, value);
    return this;
  }

  public Map<String, String> getHeaders() {
    return headers;
  }

  public Email setHeaders(Map<String, String> headers) {
    this.headers = headers;
    return this;
  }
}
