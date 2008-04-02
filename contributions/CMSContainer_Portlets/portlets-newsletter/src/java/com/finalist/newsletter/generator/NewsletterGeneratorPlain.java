package com.finalist.newsletter.generator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

public class NewsletterGeneratorPlain extends NewsletterGenerator {

   public NewsletterGeneratorPlain(int publicationNumber) {
      super(publicationNumber);
      // TODO Auto-generated constructor stub
   }

   @Override
   protected String getType() {      
      return "text/plain";
   }
}
