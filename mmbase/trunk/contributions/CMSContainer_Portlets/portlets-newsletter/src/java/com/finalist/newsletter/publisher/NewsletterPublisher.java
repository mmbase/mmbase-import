package com.finalist.newsletter.publisher;

import com.finalist.newsletter.domain.Subscription;
import com.finalist.newsletter.domain.Publication;
import com.finalist.newsletter.domain.Newsletter;
import com.finalist.newsletter.publisher.NewsletterGenerator;
import com.finalist.newsletter.NewsletterSendFailException;
import com.finalist.newsletter.util.NewsletterUtil;
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
import java.util.Date;

public class NewsletterPublisher {

   private static Logger log = Logging.getLoggerInstance(NewsletterPublisher.class.getName());

   private static String personaliser;

   public static void setPersonaliser(String personaliser) {
      NewsletterPublisher.personaliser = personaliser;
   }

   public void deliver(Publication publication, Subscription subscription) {
      try {
         Message message = new MimeMessage(getMailSession());
         Newsletter newsletter = publication.getNewsletter();
         setSenderInfomation(message,
               newsletter.getFromAddress(),
               newsletter.getFromName(),
               newsletter.getReplyAddress(),
               newsletter.getReplyName());

         setRecipient(message, subscription.getEmail());
         setBody(publication, subscription, message);
         setTitle(message, publication.getNewsletter().getTitle());
         setMIME(message, subscription.getMimeType());

         Transport.send(message);
         log.debug("mail send! publication:"+publication.getId()+"to subscription"+subscription.getId()+" in MIME"+subscription.getMimeType());
      } catch (MessagingException e) {
         log.error(e);
         throw new NewsletterSendFailException(e);
      } catch (UnsupportedEncodingException e) {
         log.error(e);
         throw new NewsletterSendFailException(e);
      }
   }

   private void setBody(Publication publication, Subscription subscription, Message message) throws MessagingException {
      String url = NewsletterUtil.getTermURL(publication.getUrl(),subscription.getTerms(),publication.getId());
      int articleCounts = NewsletterUtil.countArticlesByNewsletter(publication.getNewsletterId());
      String content = " ";
      if(articleCounts == 0) {
         if(publication.getNewsletter().getSendempty()) {
            content = publication.getNewsletter().getTxtempty();
         }
      }
      else {
         content = NewsletterGenerator.generate(url, subscription.getMimeType());
      }
      
      if (null != getPersonalise()) {
         content = getPersonalise().personalise(content, subscription, publication);
      }
      
      message.setText(content + "\n");
   }


   private void setSenderInfomation(Message message, String fromAddress, String fromName, String replyAddress, String replyName)
         throws MessagingException, UnsupportedEncodingException {

      String emailFrom = getHeaderProperties(fromAddress, "newsletter.default.fromaddress");
      String nameFrom = getHeaderProperties(fromName, "newsletter.default.fromname");
      String emailReplyTo = getHeaderProperties(replyAddress, "newsletter.default.replytoadress");
      String nameReplyTo = getHeaderProperties(replyName, "newsletter.default.replyto");

      log.debug("set header property:<" + nameFrom + ">" + emailFrom + "<" + nameReplyTo + ">" + emailReplyTo);

      InternetAddress senderAddress = new InternetAddress(emailFrom);
      senderAddress.setPersonal(nameFrom);
      message.setFrom(senderAddress);

      InternetAddress replyToAddress = new InternetAddress(emailReplyTo);
      replyToAddress.setPersonal(nameReplyTo);
      message.setReplyTo(new InternetAddress[]{replyToAddress});


   }

   private void setMIME(Message message, String mime) throws MessagingException {
      message.setHeader("MIME-Version", "1.0");
      message.setHeader("Content-Type", mime);
      message.setHeader("X-Mailer", "Recommend-It Mailer V2.03c02");
      message.setSentDate(new Date());
   }

   private void setTitle(Message message, String title) throws MessagingException {
      message.setSubject(title);
   }

   private void setRecipient(Message message, String email) throws MessagingException {
      InternetAddress toAddress = new InternetAddress(email);
      message.setRecipient(MimeMessage.RecipientType.TO, toAddress);
   }

   private String getHeaderProperties(String property, String defaultKey) {
      if (StringUtils.isBlank(property)) {
         property = PropertiesUtil.getProperty(defaultKey);

         log.debug("get property:" + defaultKey + " from system property got:" + property);
      }

      if (StringUtils.isBlank(property)) {
         property = "newslettermodule@cmscontainer.org";

         log.debug("get property:" + defaultKey + " from system property failed use default:" + property);
      }

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

   private Personaliser getPersonalise() {
      Personaliser ps = null;
      if (null == personaliser) {
         personaliser = PropertiesUtil.getProperty("newsletter.personaliser");
      }

      if (StringUtils.isNotEmpty(personaliser)) {
         try {
            ps = (Personaliser) Class.forName(personaliser).newInstance();
         } catch (ClassNotFoundException e) {
            log.error("No specified personaliser found:" + personaliser, e);
         } catch (IllegalAccessException e) {
            log.error(e);
         } catch (InstantiationException e) {
            log.error(e);
         }
      }
      return ps;

   }
}