package com.finalist.newsletter.generator;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public class NewsletterGeneratorHtml extends NewsletterGenerator {

   static final Logger log = Logging.getLoggerInstance(NewsletterGeneratorHtml.class);

   public NewsletterGeneratorHtml(int publicationNumber) {
      super(publicationNumber);
      // TODO Auto-generated constructor stub
   }

   @Override
   public Message generateNewsletterMessage(String userName) {
      Session session = getMailSession();
      Message message = new MimeMessage(session);
      String rawHtmlContent = getContent(userName);

      if (rawHtmlContent != null) {
         // BodyPart htmlBodyPart = new MimeBodyPart();
         // Multipart content = new MimeMultipart();
         try {
            // content.addBodyPart(htmlBodyPart);
            // message.setContent(content);
            message.setText(rawHtmlContent + "\n");
            message.setHeader("Content-type", "text/html");
         } catch (MessagingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }

      return (message);
   }

}
