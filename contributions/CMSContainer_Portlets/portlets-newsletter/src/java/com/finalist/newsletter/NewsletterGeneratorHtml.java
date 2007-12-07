package com.finalist.newsletter;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public class NewsletterGeneratorHtml extends NewsletterGenerator {

   static final Logger log = Logging.getLoggerInstance(NewsletterGeneratorHtml.class);
   
   public NewsletterGeneratorHtml(String publicationNumber) {
      super(publicationNumber);
      // TODO Auto-generated constructor stub
   }

   @Override
   protected MimeMultipart generateNewsletterMessage(String userName) {

      Session session = getMailSession();

      
      
      MimeMultipart content = new MimeMultipart();
      MimeBodyPart contentBodyPart = new MimeBodyPart();

      String rawContent = getContent(userName);
      try {
         content.addBodyPart(contentBodyPart);

      } catch (MessagingException e) {
         e.printStackTrace();
      }
      return (null);
   }

}
