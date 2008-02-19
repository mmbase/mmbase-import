package com.finalist.newsletter.publisher;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.services.community.NewsletterCommunication;
import com.finalist.newsletter.generator.NewsletterGenerator;
import com.finalist.newsletter.generator.NewsletterGeneratorFactory;
import com.finalist.newsletter.util.NewsletterPublicationUtil;
import com.finalist.newsletter.util.NewsletterSubscriptionUtil;
import com.finalist.newsletter.util.NewsletterUtil;

public class NewsletterPublisher extends Thread {

   private static final String UNSENT_NEWSLETTER = "unsent_newsletter";

   private static Logger log = Logging.getLoggerInstance(NewsletterPublisher.class.getName());

   private int publicationNumber;
   private Cloud cloud;

   public NewsletterPublisher(int publicationNumber) {
      this.publicationNumber = publicationNumber;
      this.cloud = CloudProviderFactory.getCloudProvider().getCloud();
   }

   private void createConfirmationList(List<String> subscribers) {
      for (int s = 0; s < subscribers.size(); s++) {
         String userName = subscribers.get(s);
         NewsletterCommunication.setUserPreference(userName, UNSENT_NEWSLETTER, String.valueOf(publicationNumber));
      }
   }

   private Message generateNewsletter(String userName, int publicationNumber, String mimeType) {
      NewsletterGeneratorFactory factory = NewsletterGeneratorFactory.getInstance();
      NewsletterGenerator generator = factory.getNewsletterGenerator(publicationNumber, mimeType);
      if (generator != null) {
         Message message = generator.generateNewsletterMessage(userName);
         return (message);
      }
      return (null);
   }

   private void removeFromConfirmationList(String userName) {
      NewsletterCommunication.removeUserPreference(userName, UNSENT_NEWSLETTER, String.valueOf(publicationNumber));
   }

   @Override
   public void run() {
      startMassPublishing();
   }

   private void sendNewsletter(Node publicationNode, String userName) {
      String mimeType = NewsletterSubscriptionUtil.getPreferredMimeType(userName);
      Message message = generateNewsletter(userName, publicationNumber, mimeType);

      try {
         message = setMailHeaders(publicationNode, userName, message);
         Transport.send(message);
         removeFromConfirmationList(userName);
      } catch (MessagingException e) {
         log.debug("An error occurred while trying to send a newsletter e-mail");
         log.debug(e.getMessage());
      } catch (UnsupportedEncodingException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   private Message setMailHeaders(Node publicationNode, String userName, Message message) throws MessagingException, UnsupportedEncodingException {

      String emailFrom = null;
      String nameFrom = null;
      String emailReplyTo = null;
      String nameReplyTo = null;

      Node newsletterNode = SearchUtil.findRelatedNode(publicationNode, "newsletter", "related");
      if (newsletterNode != null) {
         emailFrom = newsletterNode.getStringValue("from_mail");
         nameFrom = newsletterNode.getStringValue("from_name");
         emailReplyTo = newsletterNode.getStringValue("replyto_mail");
         nameReplyTo = newsletterNode.getStringValue("replyto_name");
      }

      if (emailFrom == null || emailFrom.length() == 0) {
         emailFrom = PropertiesUtil.getProperty("newsletter.from.mail");
         if (emailFrom == null || emailFrom.length() == 0) {
            emailFrom = PropertiesUtil.getProperty("mail.system.email");
         }
      }

      if (nameFrom == null || nameFrom.length() == 0) {
         nameFrom = PropertiesUtil.getProperty("newsletter.from.name");
      }

      if (emailReplyTo == null || emailReplyTo.length() == 0) {
         emailReplyTo = PropertiesUtil.getProperty("newsletter.replyto.mail");
      }

      if (nameReplyTo == null || nameReplyTo.length() == 0) {
         nameReplyTo = PropertiesUtil.getProperty("newsletter.replyto.name");
      }

      if (emailFrom != null && emailFrom.length() > 0) {
         InternetAddress fromAddress = new InternetAddress(emailFrom);
         if (nameFrom != null && nameFrom.length() > 0) {
            fromAddress.setPersonal(nameFrom);
         }
         message.setFrom(fromAddress);
      } else {
         return (null);
      }

      if (emailReplyTo != null && emailReplyTo.length() > 0) {
         InternetAddress replyToAddress = new InternetAddress(emailReplyTo);
         if (nameReplyTo != null && nameReplyTo.length() > 0) {
            replyToAddress.setPersonal(nameReplyTo);
         }
         InternetAddress[] addresses = new InternetAddress[1];
         addresses[0] = replyToAddress;
         message.setReplyTo(addresses);
      } else {
         return (null);
      }

      String userEmail = NewsletterCommunication.getUserPreference(userName, "email");
      if (userEmail != null && userEmail.length() > 0) {
         InternetAddress toAddress = new InternetAddress(userEmail);
         message.setRecipient(RecipientType.TO, toAddress);
      } else {
         return (null);
      }

      String subject = "" + publicationNode.getStringValue("subject");
      message.setSubject(subject);

      return (message);
   }

   private void startMassPublishing() {
      Node publicationNode = cloud.getNode(publicationNumber);
      NodeList newsletterNodeList = publicationNode.getRelatedNodes(NewsletterUtil.NEWSLETTER);
      Node newsletterNode = newsletterNodeList.getNode(0);
      int newsletterNumber = newsletterNode.getNumber();
      List<String> subscribers = NewsletterSubscriptionUtil.getSubscribersForNewsletter(newsletterNumber);

      if (subscribers != null) {
         createConfirmationList(subscribers);
         for (int subscribersIterator = 0; subscribersIterator < subscribers.size(); subscribersIterator++) {
            String userName = subscribers.get(subscribersIterator);
            sendNewsletter(publicationNode, userName);
         }
      }
      NewsletterPublicationUtil.setPublicationNumber(newsletterNode, 1);
      NewsletterPublicationUtil.updatePublicationTitle(publicationNode);
   }

}