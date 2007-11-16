package com.finalist.newsletter;

import java.util.ArrayList;
import java.util.List;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.applications.crontab.CronEntry;
import org.mmbase.applications.crontab.CronJob;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;

import com.finalist.newsletter.util.NewsletterPublicationUtil;
import com.finalist.newsletter.util.NewsletterUtil;

public class NewsletterCronJob implements CronJob {

	public void run() {
		long startTime = System.currentTimeMillis();
		List<Node> newslettersToPublish = getNewslettersToPublish();
		for (int newsletterIterator = 0; newsletterIterator < newslettersToPublish.size(); newsletterIterator++) {
			Node newsletterNode = newslettersToPublish.get(newsletterIterator);
			String newsletterNumber = newsletterNode.getStringValue("number");
			Node publicationNode = NewsletterPublicationUtil.createPublication(newsletterNumber);
			String publicationNumber = publicationNode.getStringValue("number");
			NewsletterPublisher publisher = new NewsletterPublisher(publicationNumber);
			publisher.startPublishing();
		}
	}

	private List<Node> getNewslettersToPublish() {
		long currentTime = System.currentTimeMillis();
		Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
		NodeManager manager = cloud.getNodeManager(NewsletterUtil.NEWSLETTER);
		NodeQuery query = manager.createQuery();
		NodeList newsletters = manager.getList(query);
		List<Node> newslettersToPublish = new ArrayList<Node>();
		for (int i = 0; i < newsletters.size(); i++) {
			Node newsletter = newsletters.getNode(i);
			long publicationInterval = newsletter.getLongValue("publishinterval");
		}
		return (newslettersToPublish);
	}

	public void init(CronEntry arg0) {
		// TODO Auto-generated method stub

	}

	public void stop() {
		// TODO Auto-generated method stub

	}

}
