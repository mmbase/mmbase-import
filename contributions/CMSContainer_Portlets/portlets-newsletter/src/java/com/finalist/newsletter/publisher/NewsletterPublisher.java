package com.finalist.newsletter.publisher;

import com.finalist.newsletter.domain.Subscription;
import com.finalist.newsletter.domain.Publication;
import com.finalist.newsletter.publisher.NewsletterGenerator;
import org.mmbase.module.Module;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class NewsletterPublisher {

   private static Logger log = Logging.getLoggerInstance(NewsletterPublisher.class.getName());

   public void deliver(Publication publication,List<Subscription> subscriptions) {
      for (Subscription subscription : subscriptions) {
         try {
            Message message = new MimeMessage(getMailSession());
            setBody(message, publication);
            setHeader(message, subscription);
            Transport.send(message);
         } catch (MessagingException e) {
            log.error(e);
         } catch (UnsupportedEncodingException e) {
            log.error(e);
         }
      }
   }

   private void setBody(Message message, Publication publication,Subscription subscription) throws MessagingException {
      NewsletterGenerator.generate(message,publication,subscription);
   }

   private void setHeader(Message message, Subscription subscription) throws MessagingException, UnsupportedEncodingException {

      String emailFrom = subscription.getFromAddress();
      String nameFrom = subscription.getFromName();
      String emailReplyTo = subscription.getReplyAddress();
      String nameReplyTo = subscription.getReplyname();

      InternetAddress fromAddress = new InternetAddress(emailFrom);
      fromAddress.setPersonal(nameFrom);
      message.setFrom(fromAddress);

      InternetAddress replyToAddress = new InternetAddress(emailReplyTo);
      replyToAddress.setPersonal(nameReplyTo);
      message.setReplyTo(new InternetAddress[]{replyToAddress});

      InternetAddress toAddress = new InternetAddress(subscription.getSubscriber().getEmail());
      message.setRecipient(MimeMessage.RecipientType.TO, toAddress);

      message.setSubject(subscription.getTitle());
      message.setHeader("Content-type",subscription.getMimeType());
   }

   private static Session getMailSession() {

      Session session = null;

      try {
         String datasource = getParameter("datasource");
         String context = getParameter("context");

         Context initCtx = new InitialContext();
         Context envCtx = (Context) initCtx.lookup(context);
         session = (javax.mail.Session) envCtx.lookup(datasource);
      } catch (NamingException e) {
         log.fatal("Configured dataSource '" + getParameter("datasource") + "' of context '" + getParameter("context") + "' is not a Session ");
      }
      return session;
   }

   private static String getParameter(String name) {
      Module sendmailModule = Module.getModule("sendmail");
      if (sendmailModule == null) {
         log.fatal("Sendmail module not installed which is required for newsletter generation");
         return null;
      }

      String parameter = sendmailModule.getInitParameter(name);
      if (parameter == null) {
         parameter = "java:comp/env";
         log.warn("The property " + parameter + " is missing, taking default " + parameter);
      }
      return parameter;
   }
}