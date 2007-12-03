package com.finalist.newsletter;

import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class NewsletterGeneratorPlain extends NewsletterGenerator {

   public NewsletterGeneratorPlain(String publicationNumber) {
      super(publicationNumber);
      // TODO Auto-generated constructor stub
   }

   @Override
   protected MimeMultipart generateNewsletterContent(String userName) {
      // TODO Auto-generated method stub

      return null;
   }

}
