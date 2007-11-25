package com.finalist.newsletter;

import java.util.List;

import javax.mail.Message;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

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

	private void startPublishing() {
		log.debug("Starting the publishing cylcus for publication with number " + publicationNumber);
		Node publicationNode = cloud.getNode(this.publicationNumber);
		NodeList newsletterNodeList = publicationNode.getRelatedNodes(NewsletterUtil.NEWSLETTER);
		Node newsletterNode = newsletterNodeList.getNode(0);
		String newsletterNumber = newsletterNode.getStringValue("number");
		List<String> subscribers = NewsletterSubscriptionUtil.getSubscribersForNewsletter(newsletterNumber);

		for (int subscribersIterator = 0; subscribersIterator < subscribers.size(); subscribersIterator++) {
			String userName = subscribers.get(subscribersIterator);
			String mimeType = NewsletterSubscriptionUtil.getPreferredMimeType(userName);
			Message newsletter = generateNewsletter(userName, publicationNumber, mimeType);
			boolean mailSent = createEmailNode(publicationNode, newsletter, userName);
		}
	}

	private Message generateNewsletter(String userName, String publicationNumber, String mimeType) {
		log.debug("Request to generate a newsletter for user " + userName + " from publication " + publicationNumber + " with mimetype " + mimeType);
		NewsletterGeneratorFactory factory = NewsletterGeneratorFactory.getInstance();
		NewsletterGenerator generator = factory.getNewsletterGenerator(publicationNumber, mimeType);
		if (generator != null) {
			Message content = generator.generateNewsletterContent(userName);
			return (content);
		}
		return (null);
	}	


	private boolean createEmailNode(Node publicationNode, Message newsletter, String userName) {

		return (false);
	}

	@Override
	public void run() {
		startPublishing();
	}

}