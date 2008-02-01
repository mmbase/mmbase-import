package com.finalist.newsletter.publisher;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;

import org.mmbase.applications.email.SendMail;

public class NewsletterSendMail extends SendMail {

   public boolean send(Message message) {
      // super.send(message);
      try {
         Transport.send(message);
         return (true);
      } catch (MessagingException e) {
         e.printStackTrace();
      }
      return false;
   }
}
