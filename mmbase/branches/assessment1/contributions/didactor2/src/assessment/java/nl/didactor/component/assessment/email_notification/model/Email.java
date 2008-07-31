package nl.didactor.component.assessment.email_notification.model;

/**
 * @javadoc
 * @version $Id: Email.java,v 1.2 2007-01-08 09:08:14 mmeeuwissen Exp $
 */

public class Email {
    private String body;
    private String from;
    private String to;
    private String subject;
    private String mimeType = "text/plain";

    public Email() {
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setFrom(String from) {
      this.from = from;
   }

    public void setTo(String to) {
        this.to = to;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getBody() {
        return body;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getSubject() {
        return subject;
    }

    public String getMimeType() {
        return mimeType;
    }

}
