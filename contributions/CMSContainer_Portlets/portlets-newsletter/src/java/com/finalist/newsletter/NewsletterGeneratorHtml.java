package com.finalist.newsletter;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

public class NewsletterGeneratorHtml extends NewsletterGenerator {

   public NewsletterGeneratorHtml(String publicationNumber) {
      super(publicationNumber);
      // TODO Auto-generated constructor stub
   }

   @Override
   protected MimeMultipart generateNewsletterMessage(String userName) {

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
