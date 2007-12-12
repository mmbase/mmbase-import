package com.finalist.newsletter;

import java.util.List;
import java.util.ResourceBundle;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMultipart;

import com.finalist.cmsc.services.community.NewsletterCommunication;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.applications.email.SendMail;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.services.community.NewsletterCommunication;
import com.finalist.cmsc.services.community.NewsletterCommunicationService;
import com.finalist.newsletter.util.NewsletterSubscriptionUtil;
import com.finalist.newsletter.util.NewsletterUtil;

public class NewsletterPublisher extends Thread {

   private static final String UNSENT_NEWSLETTER = "unsent_newsletter";

   private static Logger log = Logging.getLoggerInstance(NewsletterPublisher.class.getName());

   private String publicationNumber;
   private Cloud cloud;

   public NewsletterPublisher(String publicationNumber) {
      this.publicationNumber = publicationNumber;
      this.cloud = CloudProviderFactory.getCloudProvider().getCloud();
      log.debug("A new  instance of NewsletterPublisher is created for publication with number " + publicationNumber);
   }

   private Message generateNewsletter(String userName, String publicationNumber, String mimeType) {
      log.debug("Request to generate a newsletter for user " + userName + " from publication " + publicationNumber + " with mimetype " + mimeType);
      NewsletterGeneratorFactory factory = NewsletterGeneratorFactory.getInstance();
      NewsletterGenerator generator = factory.getNewsletterGenerator(publicationNumber, mimeType);
      if (generator != null) {
         Message message = generator.generateNewsletterMessage(userName);
         return (message);
      }
      return (null);
   }

   @Override
   public void run() {
      startPublishing();
      log.debug("Publication thread started for publication " + publicationNumber);
   }

   private void startPublishing() {
      Node publicationNode = cloud.getNode(publicationNumber);
      NodeList newsletterNodeList = publicationNode.getRelatedNodes(NewsletterUtil.NEWSLETTER);
      Node newsletterNode = newsletterNodeList.getNode(0);
      String newsletterNumber = newsletterNode.getStringValue("number");
      List<String> subscribers = NewsletterSubscriptionUtil.getSubscribersForNewsletter(newsletterNumber);

      if (subscribers != null) {

         createConfirmationList(subscribers);

         for (int subscribersIterator = 0; subscribersIterator < subscribers.size(); subscribersIterator++) {
            String userName = subscribers.get(subscribersIterator);
            sendNewsletter(publicationNode, userName);
         }
      }
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
      ResourceBundle rb = ResourceBundle.getBundle("newsletter");
      
      String userEmail = NewsletterCommunication.getUserPreference(userName, "email");
      message.setRecipient(RecipientType.TO, new InternetAddress(userEmail));
      
      String subject = publicationNode.getStringValue("subject");
      message.setSubject(subject);
      
      String description = publicationNode.getStringValue("description");
      message.setDescription(description);

      return (message);
   }

   private void createConfirmationList(List<String> subscribers) {
      for (int s = 0; s < subscribers.size(); s++) {
         String userName = subscribers.get(s);
         NewsletterCommunication.setUserPreference(userName, UNSENT_NEWSLETTER, publicationNumber);
      }
   }

   private void removeFromConfirmationList(String userName) {
      NewsletterCommunication.removeUserPreference(userName, UNSENT_NEWSLETTER, publicationNumber);
   }
}