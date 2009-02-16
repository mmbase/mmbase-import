package com.finalist.cmsc.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.*;

import org.apache.commons.lang.StringUtils;
import org.mmbase.applications.email.SendMail;
import org.mmbase.module.core.MMBase;

import com.finalist.cmsc.mmbase.PropertiesUtil;

/**
 * Utility class for sending emails
 *
 * @author Cati Macarov
 */
public final class EmailSender {

   /**
    * Constructor. Creates a new instance.
    */
   private EmailSender() {
      // nothing
   }

   /**
    * Send email
    * @param emailFrom The email address of the sender 
    * @param nameFrom The name of the sender 
    * @param emailTo The email address of the receiver 
    * @param subject The subject of the email 
    * @param body The body of the email
    * @throws MessagingException message send faiked
    * @throws UnsupportedEncodingException The Character Encoding is not supported
    */
   public static void sendEmail(String emailFrom, String nameFrom, String emailTo, String subject, String body)
         throws UnsupportedEncodingException, MessagingException {
      sendEmail(emailFrom, nameFrom, emailTo, subject, body, null);
   }


   /**
    * Send email
    * @param emailFrom The email address of the sender 
    * @param nameFrom The name of the sender 
    * @param emailTo The email address of the receiver 
    * @param subject The subject of the email 
    * @param body The body of the email
    * @param replyTo Address as reply-to header in the message
    * @throws MessagingException message send faiked
    * @throws UnsupportedEncodingException The Character Encoding is not supported
    */
   public static void sendEmail(String emailFrom, String nameFrom, String emailTo, String subject,
         String body, String replyTo) throws MessagingException, UnsupportedEncodingException {
      List<String> toAddresses = new ArrayList<String>();
      toAddresses.add(emailTo);
      sendEmail(emailFrom, nameFrom, toAddresses , subject, body, null, replyTo);
   }
   
	/**
    * Send email
    * @param emailFrom The email address of the sender 
    * @param nameFrom The name of the sender 
    * @param toAddresses The list of email addresses of the receivers 
    * @param subject The subject of the email 
    * @param body The body of the email @param fileName The name of the attachment
    * @param attachment Binary part to add to the email message
    * @throws MessagingException message send faiked
    * @throws UnsupportedEncodingException The Character Encoding is not supported
    */
   public void sendEmail(String emailFrom, String nameFrom, List<String> toAddresses, String subject, String body,
         DataSource attachment) throws UnsupportedEncodingException, MessagingException {
      sendEmail(emailFrom, nameFrom, toAddresses, subject, body, attachment, null);
   }


   /**
    * Send email
    * @param emailFrom The email address of the sender 
    * @param nameFrom The name of the sender 
    * @param toAddresses The list of email addresses of the receivers 
    * @param subject The subject of the email 
    * @param body The body of the email @param fileName The name of the attachment
    * @param attachment Binary part to add to the message
    * @param replyTo Address as reply-to header in the message
    * @throws MessagingException message send faiked
    * @throws UnsupportedEncodingException The Character Encoding is not supported
    */
   public static void sendEmail(String emailFrom, String nameFrom, List<String> toAddresses,
         String subject, String body, DataSource attachment, String replyTo)
         throws MessagingException, UnsupportedEncodingException {
      if (StringUtils.isBlank(emailFrom)) {
         emailFrom = PropertiesUtil.getProperty("mail.system.email");
      }
      if (StringUtils.isBlank(nameFrom)) {
         nameFrom = PropertiesUtil.getProperty("mail.system.name");
      }
      
      Session session = getMailSession();
      
      // Define message
      MimeMessage message = new MimeMessage(session);
      message.setFrom(new InternetAddress(emailFrom, nameFrom));
      InternetAddress[] addresses = new InternetAddress[toAddresses.size()];
      for (int i = 0; i < toAddresses.size(); i++) {
         addresses[i] = new InternetAddress(toAddresses.get(i));
      }
      message.addRecipients(Message.RecipientType.TO, addresses);

      if (StringUtils.isNotBlank(replyTo)) {
         message.setReplyTo(InternetAddress.parse(replyTo));
      }

      message.setSubject(subject);
      
      if (attachment != null) { 
         Multipart multipart = createMultiPart(body, attachment);
         message.setContent(multipart);
      }
      else {
         message.setText(body);
      }

      Transport.send(message);
   }


   private static Multipart createMultiPart(String body, DataSource dataSource) throws MessagingException {
      // create the message part
      MimeBodyPart messageBodyPart = new MimeBodyPart();
      messageBodyPart.setText(body);
      Multipart multipart = new MimeMultipart();
      multipart.addBodyPart(messageBodyPart);

      // Part two is attachment
      if (dataSource != null) {
         messageBodyPart = new MimeBodyPart();
         // DataSource source = new FileDataSource(fileName);
         messageBodyPart.setDataHandler(new DataHandler(dataSource));
         messageBodyPart.setFileName(dataSource.getName());
         multipart.addBodyPart(messageBodyPart);
      }
      return multipart;
   }

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
      Session session = getMailSession();
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
   
   @SuppressWarnings("static-access")
   private static Session getMailSession() {
      Session session = ((SendMail) MMBase.getModule("sendmail")).getSession();
      return session;
   }

}
