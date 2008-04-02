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
   }

   @Override
   protected String getType() {      
      return "text/html";
   }
}
