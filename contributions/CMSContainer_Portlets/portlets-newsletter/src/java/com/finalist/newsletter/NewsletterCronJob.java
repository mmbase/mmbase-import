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
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.services.publish.Publish;
import com.finalist.newsletter.util.NewsletterPublicationUtil;
import com.finalist.newsletter.util.NewsletterUtil;

public class NewsletterCronJob implements CronJob {

	private static Logger log = Logging.getLoggerInstance(NewsletterCronJob.class.getName());

	public void run() {
		log.debug("Running NewsletterCronJob");
		long startTime = System.currentTimeMillis();
		List<Node> newslettersToPublish = getNewslettersToPublish();
		for (int newsletterIterator = 0; newsletterIterator < newslettersToPublish.size(); newsletterIterator++) {
			Node newsletterNode = newslettersToPublish.get(newsletterIterator);
			String newsletterNumber = newsletterNode.getStringValue("number");
			Node publicationNode = NewsletterPublicationUtil.createPublication(newsletterNumber);
			Publish.publish(publicationNode);
		}
	}

	private List<Node> getNewslettersToPublish() {
		long currentTime = System.currentTimeMillis();
		Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
		NodeManager manager = cloud.getNodeManager(NewsletterUtil.NEWSLETTER);
		NodeQuery query = manager.createQuery();
		NodeList newsletters = manager.getList(query);
		List<Node> newslettersToPublish = new ArrayList<Node>();
		log.debug("Found " + newsletters.size() + " newsletter in database");
		for (int i = 0; i < newsletters.size(); i++) {
			Node newsletter = newsletters.getNode(i);
			if (Publish.isPublished(newsletter)) {
				long publishInterval = newsletter.getLongValue("publishinterval");
				if (publishInterval > 0) {
					newslettersToPublish.add(newsletter);
					log.debug("Newsletter " + newsletter.getNumber() + " is added to the list of newsletters to publish.");
				} else {
					log.debug("Newsletter " + newsletter.getNumber() + " requires manual publication and will not be processed.");
				}
			} else {
				log.debug("Newsletter " + newsletter.getNumber() + " is not published to the Live site and will not be processed");
			}
		}
		return (newslettersToPublish);
	}

	public void init(CronEntry arg0) {
		log.debug("NewsletterCronJob init");

	}

	public void stop() {
		log.debug("NewsletterCronJob stop !");

	}

}
