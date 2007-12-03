package com.finalist.newsletter;

import java.util.List;
import java.util.ResourceBundle;

import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.MimeMultipart;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.applications.email.SendMail;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.util.Mail;
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
         MimeMultipart content = generateNewsletter(userName, publicationNumber, mimeType);
         sendNewsletter(content, userName, publicationNode);
      }
   }

   private MimeMultipart generateNewsletter(String userName, String publicationNumber, String mimeType) {
      log.debug("Request to generate a newsletter for user " + userName + " from publication " + publicationNumber + " with mimetype " + mimeType);
      NewsletterGeneratorFactory factory = NewsletterGeneratorFactory.getInstance();
      NewsletterGenerator generator = factory.getNewsletterGenerator(publicationNumber, mimeType);
      if (generator != null) {
         MimeMultipart content = generator.generateNewsletterContent(userName);
         return (content);
      }
      return (null);
   }

   private void sendNewsletter(MimeMultipart content, String userName, Node publicationNode) {
      ResourceBundle rb = ResourceBundle.getBundle("newsletter");
      String userEmail = CommunityManager.getUserPreference(userName, "email");
      String subject = publicationNode.getStringValue("subject");
      String description = publicationNode.getStringValue("description");

      SendMail mailer = new SendMail();
      

   }

}