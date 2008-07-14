package com.finalist.portlets.responseform;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.*;

import org.apache.commons.lang.StringUtils;

import com.finalist.cmsc.mmbase.PropertiesUtil;

/**
 * Utility class for sending emails
 *
 * @author Cati Macarov
 */
public final class EmailSender {

   private static EmailSender instance = null;

   private Properties props = new Properties();
   private String mailHost = null;


   /**
    * Constructor. Creates a new instance.
    */
   private EmailSender(String mailHostValue) {
      mailHost = mailHostValue;
      props.put("mail.smtp.host", mailHost);
   }


   /**
    * Singleton access method
    *
    * @return an instance of the EmailSender
    */
   public static synchronized EmailSender getInstance() {
      String tempMailHost = PropertiesUtil.getProperty("mail.smtp.host");
      if (instance == null) {
         instance = new EmailSender(tempMailHost);
      }
      else {
         if ((tempMailHost != null) && (!tempMailHost.equals(instance.mailHost))) {
            instance = new EmailSender(tempMailHost);
         }
      }

      return instance;
   }


	/**
    * @param emailFrom The email address of the sender 
    * @param nameFrom The name of the sender 
    * @param toAddresses The list of email addresses of the receivers 
    * @param subject The subject of the email 
    * @param body The body of the email @param fileName The name of the attachment
    * @param attachment Binary part to add to the email message
    */
   public void sendEmail(String emailFrom, String nameFrom, List<String> toAddresses, String subject, String body,
         DataSource attachment) throws UnsupportedEncodingException, MessagingException {
      sendEmail(emailFrom, nameFrom, toAddresses, subject, body, attachment, null);
   }


   /**
    * @param emailFrom The email address of the sender 
    * @param nameFrom The name of the sender 
    * @param toAddresses The list of email addresses of the receivers 
    * @param subject The subject of the email 
    * @param body The body of the email @param fileName The name of the attachment
    * @param attachment Binary part to add to the message
    * @param replyTo Address as reply-to header in the message
    */
   public void sendEmail(String emailFrom, String nameFrom, List<String> toAddresses,
         String subject, String body, DataSource attachment, String replyTo)
         throws MessagingException, UnsupportedEncodingException, AddressException {
      if (StringUtils.isBlank(emailFrom)) {
         emailFrom = PropertiesUtil.getProperty("mail.system.email");
      }
      if (StringUtils.isBlank(nameFrom)) {
         nameFrom = PropertiesUtil.getProperty("mail.system.name");
      }
      Session session = Session.getInstance(props, null);
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
      Multipart multipart = createMultiPart(body, attachment);
      message.setContent(multipart);
      Transport.send(message);
   }

   private Multipart createMultiPart(String body, DataSource dataSource) throws MessagingException {
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


   /**
    * @param emailFrom The email address of the sender 
    * @param nameFrom The name of the sender 
    * @param emailTo The email address of the receiver 
    * @param subject The subject of the email 
    * @param body The body of the email
    */
   public void sendEmail(String emailFrom, String nameFrom, String emailTo, String subject, String body)
         throws UnsupportedEncodingException, MessagingException {
      sendEmail(emailFrom, nameFrom, emailTo, subject, body, null);
   }


   /**
    * @param emailFrom The email address of the sender 
    * @param nameFrom The name of the sender 
    * @param emailTo The email address of the receiver 
    * @param subject The subject of the email 
    * @param body The body of the email
    * @param replyTo Address as reply-to header in the message
    */
   public void sendEmail(String emailFrom, String nameFrom, String emailTo, String subject,
         String body, String replyTo) throws MessagingException, UnsupportedEncodingException,
         AddressException {
      if (StringUtils.isBlank(emailFrom)) {
         emailFrom = PropertiesUtil.getProperty("mail.system.email");
      }
      if (StringUtils.isBlank(nameFrom)) {
         nameFrom = PropertiesUtil.getProperty("mail.system.name");
      }
      Session session = Session.getInstance(props, null);
      MimeMessage message = new MimeMessage(session);
      message.setFrom(new InternetAddress(emailFrom, nameFrom));
      message.addRecipient(Message.RecipientType.TO, new InternetAddress(emailTo));
      if (StringUtils.isNotBlank(replyTo)) {
         message.setReplyTo(InternetAddress.parse(replyTo));
      }
      message.setSubject(subject);
      message.setText(body);
      Transport.send(message);
   }

}
