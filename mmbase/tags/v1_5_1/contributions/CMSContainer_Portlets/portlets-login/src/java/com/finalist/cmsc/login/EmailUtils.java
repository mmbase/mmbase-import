package com.finalist.cmsc.login;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.module.Module;

import com.finalist.cmsc.mmbase.PropertiesUtil;

public final  class EmailUtils {
   
   private static final Log log = LogFactory.getLog(EmailUtils.class);
   
   public static void  sendEmail(String emailFrom, String nameFrom, String emailTo,
         String subject, String body, String replyTo,String contentType)
         throws MessagingException, UnsupportedEncodingException,
         AddressException {
      if (StringUtils.isBlank(emailFrom)) {
         emailFrom = PropertiesUtil.getProperty("mail.system.email");
      }
      if (StringUtils.isBlank(nameFrom)) {
         nameFrom = PropertiesUtil.getProperty("mail.system.name");
      }
      Session session = getMailSession(emailTo, emailFrom);
      MimeMessage message = new MimeMessage(session);
      message.setFrom(new InternetAddress(emailFrom, nameFrom));
      message.addRecipient(Message.RecipientType.TO, new InternetAddress(
            emailTo));
      if (StringUtils.isNotBlank(replyTo)) {
         message.setReplyTo(InternetAddress.parse(replyTo));
      }
      message.setHeader("Content-Transfer-Encoding", "quoted-printable");
      message.setSubject(subject);
      message.setContent(body,contentType );
      Transport.send(message);
   }

   public static Session getMailSession(String toEmail, String senderEmail) {

      Session session = null;
      String datasource = getParameter("datasource");
      String context = getParameter("context");
      try {
         Context initCtx = new InitialContext();
         Context envCtx = (Context) initCtx.lookup(context);
         session = (javax.mail.Session) envCtx.lookup(datasource);
         Properties properties = new Properties();
         properties.putAll(session.getProperties());
      } 
      catch (NamingException e) {
         log.fatal("Configured dataSource '" + datasource + "' of context '"
               + context + "' is not a Session ");
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
         log.warn("The property " + parameter + " is missing, taking default "
               + parameter);
      }
      return parameter;
   }
}
