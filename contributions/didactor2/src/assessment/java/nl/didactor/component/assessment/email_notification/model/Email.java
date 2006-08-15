package nl.didactor.component.assessment.email_notification.model;

public class Email {
   private String body;
   private String from;
   private String to;
   private String subject;

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
}
