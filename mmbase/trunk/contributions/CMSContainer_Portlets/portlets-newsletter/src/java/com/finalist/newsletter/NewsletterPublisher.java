package com.finalist.newsletter;

import java.util.List;
import java.util.ResourceBundle;

import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.community.CommunityManager;
import com.finalist.newsletter.util.NewsletterSubscriptionUtil;
import com.finalist.newsletter.util.NewsletterUtil;

public class NewsletterPublisher extends Thread {

   private static Logger log = Logging.getLoggerInstance(NewsletterPublisher.class.getName());

   private String publicationNumber;
   private Cloud cloud;

   public NewsletterPublisher(String publicationNumber) {
      this.publicationNumber = publicationNumber;
      this.cloud = CloudProviderFactory.getCloudProvider().getCloud();
      log.debug("A new  instance of NewsletterPublisher is created for publication with number " + publicationNumber);
   }

   @Override
   public void run() {
      startPublishing();
      log.debug("Publication thread started for publication " + publicationNumber);
   }

   private void startPublishing() {
      Node publicationNode = cloud.getNode(this.publicationNumber);
      NodeList newsletterNodeList = publicationNode.getRelatedNodes(NewsletterUtil.NEWSLETTER);
      Node newsletterNode = newsletterNodeList.getNode(0);
      String newsletterNumber = newsletterNode.getStringValue("number");
      List<String> subscribers = NewsletterSubscriptionUtil.getSubscribersForNewsletter(newsletterNumber);

      for (int subscribersIterator = 0; subscribersIterator < subscribers.size(); subscribersIterator++) {
         String userName = subscribers.get(subscribersIterator);
         String mimeType = NewsletterSubscriptionUtil.getPreferredMimeType(userName);
         MimeMessage newsletter = generateNewsletter(userName, publicationNumber, mimeType);
         boolean result = sendNewsletter(newsletter, userName, publicationNode);
      }
   }

   private MimeMessage generateNewsletter(String userName, String publicationNumber, String mimeType) {
      log.debug("Request to generate a newsletter for user " + userName + " from publication " + publicationNumber + " with mimetype " + mimeType);
      NewsletterGeneratorFactory factory = NewsletterGeneratorFactory.getInstance();
      NewsletterGenerator generator = factory.getNewsletterGenerator(publicationNumber, mimeType);
      if (generator != null) {
         MimeMessage content = generator.generateNewsletterContent(userName);
         return (content);
      }
      return (null);
   }

   private boolean sendNewsletter(MimeMessage newsletter, String userName, Node publicationNode) {
      ResourceBundle rb = ResourceBundle.getBundle("newsletter");
      String userEmail = CommunityManager.getUserPreference(userName, "email");
      String subject = publicationNode.getStringValue("subject");
      String description = publicationNode.getStringValue("description");

      try {
         newsletter.setSubject(subject);
         newsletter.setDescription(description);

         Transport.send(newsletter);
         return (true);
      } catch (MessagingException mex) {
         log.debug("Unable to send newsletter to " + userName + " due to exception " + mex.getMessage());
         return (false);
      }
   }

}