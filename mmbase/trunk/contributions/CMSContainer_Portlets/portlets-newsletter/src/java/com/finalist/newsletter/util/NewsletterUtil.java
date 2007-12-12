package com.finalist.newsletter.util;

import java.util.ArrayList;
import java.util.List;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public abstract class NewsletterUtil {

   private static Logger log = Logging.getLoggerInstance(NewsletterUtil.class.getName());

   public static final String NEWSLETTER = "newsletter";
   public static final String NEWSLETTERPUBLICATION = "newsletterpublication";

   public static final String THEMETYPE_NEWSLETTER = "newslettertheme";
   public static final String THEMETYPE_NEWSLETTERPUBLICATION = "newsletterpublicationtheme";

   public static void deleteNewsletterThemesForNewsletter(String number, String themeType) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getAdminCloud();
      Node newsletterNode = cloud.getNode(number);
      NodeManager newsletterThemeNodeManager = cloud.getNodeManager(themeType);
      NodeList themes = newsletterNode.getRelatedNodes(newsletterThemeNodeManager);
      if (themes != null) {
         for (int i = 0; i < themes.size(); i++) {
            Node themeNode = themes.getNode(i);
            themeNode.deleteRelations();
            themeNode.delete();
         }
      }
   }

   public static String findNewsletterForTheme(String themeNumber) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getAdminCloud();
      Node themeNode = cloud.getNode(themeNumber);
      String managerName = null;
      String themeType = themeNode.getNodeManager().getName();
      if (themeType.equals("newslettertheme")) {
         managerName = "newsletter";
      } else {
         managerName = "newsletterpublication";
      }
      Node newsletterNode = SearchUtil.findRelatedNode(themeNode, managerName, null);
      if (newsletterNode != null) {
         return (newsletterNode.getStringValue("number"));
      }
      return (null);
   }

   public static List<String> getAllThemes(String number, String themeType) {
      log.debug("GetAllThemes " + number);
      List<String> themes = new ArrayList<String>();
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      Node newsletterNode = cloud.getNode(number);
      NodeManager themeNodeManager = cloud.getNodeManager(themeType);
      NodeList themeList = newsletterNode.getRelatedNodes(themeNodeManager);
      for (int i = 0; i < themeList.size(); i++) {
         Node themeNode = themeList.getNode(i);
         String theme = themeNode.getStringValue("number");
         themes.add(theme);
         log.debug("Found theme " + theme + " - " + themeNode.getStringValue("title"));
      }
      String defaultTheme = getDefaultTheme(number, themeType);
      themes.remove(defaultTheme);
      return (themes);
   }

   public static List<String> getArticlesForTheme(String themeNumber) {
      List<String> articles = new ArrayList<String>();
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      Node themeNode = cloud.getNode(themeNumber);
      NodeManager articleNodeManager = cloud.getNodeManager("article");
      NodeList articleList = themeNode.getRelatedNodes(articleNodeManager);
      if (articleList != null) {
         for (int i = 0; i < articleList.size(); i++) {
            Node articleNode = articleList.getNode(i);
            String article = articleNode.getStringValue("number");
            articles.add(article);
            log.debug("Found article " + article + " for theme " + themeNumber);
         }
      }
      return (articles);
   }

   public static String getDefaultTheme(String number, String themeType) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      Node newsletterNode = cloud.getNode(number);
      Node defaultThemeNode = SearchUtil.findRelatedNode(newsletterNode, themeType, "defaulttheme");
      if (defaultThemeNode != null) {
         String defaultTheme = defaultThemeNode.getStringValue("number");
         log.debug("Found default theme " + defaultTheme + " - " + defaultThemeNode.getStringValue("title"));
         return (defaultTheme);
      }
      log.debug("Default theme not found");
      return (null);
   }

   public static List<String> removeDuplicates(List<String> primary, List<String> secundary) {
      if (primary != null && secundary != null) {
         List<String> removals = new ArrayList<String>();
         for (int i = 0; i < secundary.size(); i++) {
            String key = secundary.get(i);
            if (primary.contains(key)) {
               removals.add(key);

            }
         }
         for (int r = 0; r < removals.size(); r++) {
            secundary.remove(removals.get(r));
            log.debug("Duplicate key removed: " + removals.get(r));
         }
      }
      return (secundary);
   }
   

}