package com.finalist.newsletter.publisher;

import com.finalist.newsletter.domain.Subscription;
import com.finalist.newsletter.domain.Publication;
import com.finalist.newsletter.domain.Newsletter;
import com.finalist.newsletter.publisher.NewsletterGenerator;
import com.finalist.newsletter.NewsletterSendFailException;
import com.finalist.cmsc.mmbase.PropertiesUtil;
import org.mmbase.module.Module;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.apache.commons.lang.StringUtils;

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

   public void deliver(Publication publication, List<Subscription> subscriptions) {
      for (Subscription subscription : subscriptions) {
         deliver(publication, subscription);
      }
   }

   public void deliver(Publication publication, Subscription subscription) {
      try {
         Message message = new MimeMessage(getMailSession());
         setBody(message, publication, subscription);
         setHeader(message, subscription);
         send(message);

         log.debug("mail send!");
      } catch (MessagingException e) {
         log.error(e);
         throw new NewsletterSendFailException(e);
      } catch (UnsupportedEncodingException e) {
         log.error(e);
         throw new NewsletterSendFailException(e);
      }
   }

   protected void send(Message message) throws MessagingException {
      Transport.send(message);
   }

   protected void setBody(Message message, Publication publication, Subscription subscription) throws MessagingException {
      NewsletterGenerator.generate(message, publication, subscription);
   }

   private void setHeader(Message message, Subscription subscription) throws MessagingException, UnsupportedEncodingException {

      System.out.println("------"+subscription.getNewsletter());

      Newsletter newsletter = subscription.getNewsletter();
      String emailFrom = getHeaderProperties(newsletter.getFromAddress(), "newsletter.default.fromaddress");
      String nameFrom = getHeaderProperties(newsletter.getFromName(), "newsletter.default.fromname");
      String emailReplyTo = getHeaderProperties(newsletter.getReplyAddress(), "newsletter.default.replytoadress");
      String nameReplyTo = getHeaderProperties(newsletter.getReplyName(), "newsletter.default.replyto");

      log.debug("set header property:<" + nameFrom + ">" + emailFrom + "<" + nameReplyTo + ">" + emailReplyTo+"|type:"+subscription.getMimeType());

      InternetAddress fromAddress = new InternetAddress(emailFrom);
      fromAddress.setPersonal(nameFrom);
      message.setFrom(fromAddress);

      InternetAddress replyToAddress = new InternetAddress(emailReplyTo);
      replyToAddress.setPersonal(nameReplyTo);
      message.setReplyTo(new InternetAddress[]{replyToAddress});

      InternetAddress toAddress = new InternetAddress(subscription.getSubscriber().getEmail());
      message.setRecipient(MimeMessage.RecipientType.TO, toAddress);

      message.setSubject(subscription.getNewsletter().getTitle());
      message.setHeader("Content-type", subscription.getMimeType());
   }

   private String getHeaderProperties(String property, String defaultKey) {


      if (StringUtils.isEmpty(property)) {
         property = PropertiesUtil.getProperty(defaultKey);
         log.debug("get header property:" + property + " from system property got:" + property);
      }
      log.debug("get header property:" + property +" got:"+property);
      return property;
   }

   protected Session getMailSession() {

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