package com.finalist.newsletter.util;

import java.util.ArrayList;
import java.util.List;

import net.sf.mmapps.commons.bridge.RelationUtil;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.Transaction;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public abstract class NewsletterPublicationUtil {

	private static Logger log = Logging.getLoggerInstance(NewsletterPublicationUtil.class.getName());

	public static Node getPublishedPublication(String publicationNumber) {
		Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
		// TODO - Query that only selects newsletterpublications that have been
		// published (publicationdate)
		return (null);
	}

	public static Node getUnpublishedPublication(String newsletterNumber) {
		Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
		// TODO - Query to select a node from newsletterpublication that has not
		// yet been published ( publishdate )
		return (null);
	}

	public static Node createPublication(String newsletterNumber) {
		Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
		// Get the newsletternode for the newsletter for which the publication
		// // is requested
		Node newsletterNode = cloud.getNode(newsletterNumber);
		// Create a publicationnode
		NodeManager publicationNodeManager = cloud.getNodeManager("newsletterpublication");
		// TODO - Use transaction
		Transaction transaction = cloud.createTransaction();
		Node publicationNode = publicationNodeManager.createNode();
		// Copy the newsletter properties to the publication
		String newsletterTitle = newsletterNode.getStringValue("title");
		String newsletterDescription = newsletterNode.getStringValue("description");
		publicationNode.setStringValue("title", newsletterTitle);
		publicationNode.setStringValue("description", newsletterDescription);
		publicationNode.setBooleanValue("inmenu", false);
		publicationNode.setBooleanValue("secure", false);
		publicationNode.setBooleanValue("accepted", false);
		// Relate the just created publication to the newsletter
		RelationUtil.createRelation(newsletterNode, publicationNode, "related");
		// Copy newsletterthemes to newsletterpublicationthemes
		NodeManager publicationThemeNodeManager = cloud.getNodeManager("newsletterpublicationtheme");
		NodeList newsletterThemeNodes = newsletterNode.getRelatedNodes("newslettertheme");
		for (int i = 0; i < newsletterThemeNodes.size(); i++) {
			Node newsletterThemeNode = newsletterThemeNodes.getNode(i);
			Node publicationThemeNode = publicationThemeNodeManager.createNode();
			String themeTitle = newsletterThemeNode.getStringValue("title");
			String themeShortDescription = newsletterThemeNode.getStringValue("shortdescription");
			String themeDescription = newsletterThemeNode.getStringValue("description");
			publicationThemeNode.setStringValue("title", themeTitle);
			publicationThemeNode.setStringValue("shortdescription", themeShortDescription);
			publicationThemeNode.setStringValue("description", themeDescription);
			// relate the publicationtheme to the publication
			RelationUtil.createRelation(publicationNode, publicationThemeNode, "related");
			// Copy relations from theme to article
			NodeList relatedArticles = newsletterThemeNode.getRelatedNodes("article");
			for (int a = 0; a < relatedArticles.size(); a++) {
				Node articleNode = relatedArticles.getNode(a);
				RelationUtil.createRelation(publicationThemeNode, articleNode, "related");
			}
		}

		if (transaction.commit() == true) {
			return (publicationNode);
		}
		return (null);
	}

	// Delete a publication, only if not yet published
	public static boolean deleteTestPublication(String publicationNumber) {
		Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
		NodeManager publicationManager = cloud.getNodeManager("newsletterpublication");
		NodeManager publicationThemeManager = cloud.getNodeManager("newsletterpublicationtheme");
		Node publicationNode = cloud.getNode(publicationNumber);
		NodeList publicationThemeList = publicationNode.getRelatedNodes(publicationThemeManager);
		// TODO - Use transaction
		Transaction transaction = cloud.createTransaction();
		for (int i = 0; i < publicationThemeList.size(); i++) {
			Node publicationThemeNode = publicationThemeList.getNode(i);
			publicationThemeNode.deleteRelations();
			publicationThemeNode.delete();
		}
		publicationNode.deleteRelations();
		publicationNode.delete();
		return (transaction.commit());
	}

	public static boolean acceptTestNewsletter(String publicationNumber) {
		Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
		Node publicationNode = cloud.getNode(publicationNumber);
		if (publicationNode == null) {
			return (false);
		}
		publicationNode.setBooleanValue("accepted", true);
		publicationNode.commit();
		return (true);
	}

	public static boolean isAcceptedPublication(String publicationNumber) {
		Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
		Node publicationNode = cloud.getNode(publicationNumber);
		boolean result = publicationNode.getBooleanValue("accepted");
		log.debug("Asking if the publication with number " + publicationNumber + " is accepted. Answer is: " + result);
		return (result);
	}

	public static List<String> getAllThemesForPublication(String publicationNumber) {
		List<String> themes = new ArrayList<String>();
		Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
		Node newsletterNode = cloud.getNode(publicationNumber);
		NodeList themeList = newsletterNode.getRelatedNodes("newslettertheme");
		for (int i = 0; i < themeList.size(); i++) {
			Node themeNode = themeList.getNode(i);
			String theme = themeNode.getStringValue("number");
			themes.add(theme);
		}
		return (themes);
	}
}