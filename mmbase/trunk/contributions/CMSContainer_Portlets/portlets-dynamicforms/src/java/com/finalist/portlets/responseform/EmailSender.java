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
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.apache.commons.lang.StringUtils;
import com.finalist.cmsc.mmbase.PropertiesUtil;

/**
 * Utility class for sending emails
 * 
 * @author Cati Macarov
 */
public class EmailSender {

   private static Properties props = new Properties();
   private static EmailSender instance = null;
   private static String mailHost = null;


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
      if ((tempMailHost != null) && (!tempMailHost.equals(mailHost))) {
         instance = new EmailSender(tempMailHost);
      }
      if (instance == null) {
         instance = new EmailSender(tempMailHost);
      }
      return instance;
   }


   /*
    * @param emailFrom The email address of the sender @param nameFrom The name
    * of the sender @param toAddresses The list of email addresses of the
    * receivers @param subject The subject of the email @param body The body of
    * the email @param fileName The name of the attachment
    */
   public void sendEmail(String emailFrom, String nameFrom, List<String> toAddresses, String subject, String body,
         DataSource dataSource) throws UnsupportedEncodingException, MessagingException {
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
      message.setSubject(subject);
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
      message.setContent(multipart);
      Transport.send(message);
   }


   /*
    * @param emailFrom The email address of the sender @param nameFrom The name
    * of the sender @param emailTo The email address of the receiver @param
    * subject The subject of the email @param body The body of the email
    */
   public void sendEmail(String emailFrom, String nameFrom, String emailTo, String subject, String body)
         throws UnsupportedEncodingException, MessagingException {
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
      message.setSubject(subject);
      message.setText(body);
      Transport.send(message);
   }

}
