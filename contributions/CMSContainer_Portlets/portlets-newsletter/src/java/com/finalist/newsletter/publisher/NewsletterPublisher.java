package com.finalist.newsletter.publisher;

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
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

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
      startPublishing();
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
      }
   }

   private Message setMailHeaders(Node publicationNode, String userName, Message message) throws MessagingException {

      message.setFrom(new InternetAddress("jasperstroomer@quicknet.nl"));
      
      String userEmail = NewsletterCommunication.getUserPreference(userName, "email");
      InternetAddress toAddress = new InternetAddress(userEmail);
      message.setRecipient(RecipientType.TO, toAddress);

      String subject = publicationNode.getStringValue("subject");
      message.setSubject(subject);

      return (message);
   }

   private void startPublishing() {
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