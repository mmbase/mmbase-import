package com.finalist.newsletter.util;

import java.util.ArrayList;
import java.util.List;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public abstract class NewsletterUtil {

	private static Logger log = Logging.getLoggerInstance(NewsletterUtil.class.getName());

	public static final String NEWSLETTER = "newsletter";
	public static final String NEWSLETTERPUBLICATION = "newsletterpublication";

	public static final String THEMETYPE_NEWSLETTER = "newslettertheme";
	public static final String THEMETYPE_NEWSLETTERPUBLICATION = "newsletterpublicationtheme";

	public static List<String> getAllThemes(String number, String themeType) {
		log.debug("GetAllThemes " + number);
		List<String> themes = new ArrayList<String>();
		Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
		Node newsletterNode = cloud.getNode(number);
		NodeList themeList = newsletterNode.getRelatedNodes(themeType);
		for (int i = 0; i < themeList.size(); i++) {
			Node themeNode = themeList.getNode(i);
			String theme = themeNode.getStringValue("number");
			themes.add(theme);
			log.debug("Found theme " + theme);
		}
		return (themes);
	}

	public static List<String> getArticlesForTheme(String themeNumber) {
		log.debug("GetArticlesForTheme " + themeNumber);
		List<String> articles = new ArrayList<String>();
		Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
		Node themeNode = cloud.getNode(themeNumber);
		NodeList articleList = themeNode.getRelatedNodes("article");
		for (int i = 0; i < articles.size(); i++) {
			Node articleNode = articleList.getNode(i);
			String article = articleNode.getStringValue("number");
			articles.add(article);
			log.debug("Found article " + article);
		}
		return (articles);
	}

}