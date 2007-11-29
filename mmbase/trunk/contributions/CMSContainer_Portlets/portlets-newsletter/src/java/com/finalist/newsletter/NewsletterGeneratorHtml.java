package com.finalist.newsletter;

import javax.mail.internet.MimeMessage;

public class NewsletterGeneratorHtml extends NewsletterGenerator {

   public NewsletterGeneratorHtml(String publicationNumber) {
      super(publicationNumber);
      // TODO Auto-generated constructor stub
   }

   @Override
   protected MimeMessage generateNewsletterContent(String userName) {

      return null;
   }

}
