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

   public static void deleteNewsletterThemesForNewsletter(String number) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getAdminCloud();
      Node newsletterNode = cloud.getNode(number);
      String themeType = determineThemeType(number);
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

   public static List<String> getAllThemes(String number) {
      List<String> themes = new ArrayList<String>();
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      Node newsletterNode = cloud.getNode(number);
      String themeType = determineThemeType(number);
      NodeManager themeNodeManager = cloud.getNodeManager(themeType);
      NodeList themeList = newsletterNode.getRelatedNodes(themeNodeManager);
      for (int i = 0; i < themeList.size(); i++) {
         Node themeNode = themeList.getNode(i);
         String theme = themeNode.getStringValue("number");
         themes.add(theme);
         log.debug("Found theme " + theme + " - " + themeNode.getStringValue("title"));
      }
      String defaultTheme = getDefaultTheme(number);
      themes.remove(defaultTheme);
      return (themes);
   }

   public static List<String> getArticlesForTheme(String themeNumber) {
      if (themeNumber != null) {
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
      return (null);
   }

   public static String getDefaultTheme(String number) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      Node newsletterNode = cloud.getNode(number);
      String themeType = determineThemeType(number);
      Node defaultThemeNode = SearchUtil.findRelatedNode(newsletterNode, themeType, "defaulttheme");
      if (defaultThemeNode != null) {
         String defaultTheme = defaultThemeNode.getStringValue("number");
         return (defaultTheme);
      }
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

   public static List<String> getAllNewsletters() {
      List<String> newsletters = new ArrayList<String>();
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      NodeList newsletterList = SearchUtil.findNodeList(cloud, NEWSLETTER);
      if (newsletterList != null && newsletterList.size() > 0) {
         for (int n = 0; n < newsletterList.size(); n++) {
            Node newsletterNode = newsletterList.getNode(n);
            String newsletterNumber = String.valueOf(newsletterNode.getNumber());
            newsletters.add(newsletterNumber);
         }
      }
      return (newsletters);
   }

   public static String getTitle(String newsletterNumber) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      Node newsletterNode = cloud.getNode(newsletterNumber);
      String title = newsletterNode.getStringValue("title");
      return (title);
   }

   public static int countNewsletters() {
      return (0);
   }

   public static int countThemes(String newsletterNumber) {
      return (0);
   }

   public static int countPublications(String newsletterNumber) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      Node newsletterNode = cloud.getNode(newsletterNumber);
      NodeList publicationsList = newsletterNode.getRelatedNodes("newsletterpublication");
      if (publicationsList != null) {
         return (publicationsList.size());
      }
      return (0);
   }

   public static String determineNodeType(String number) {
      if (number != null) {
         Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
         Node node = cloud.getNode(number);
         String type = node.getNodeManager().getName();
         return (type);
      }
      return (null);
   }

   public static String determineThemeType(String number) {
      String themeType = null;      
      if (isNewsletter(number)) {
         themeType = NewsletterUtil.THEMETYPE_NEWSLETTER;
      }
      if (isNewsletterPublication(number)) {
         themeType = NewsletterUtil.THEMETYPE_NEWSLETTERPUBLICATION;
      }
      return (themeType);
   }

   public static boolean isNewsletter(String number) {
      boolean result = false;
      String key = "" + determineNodeType(number);
      if (key.equals("newsletter")) {
         result = true;
      }
      return (result);
   }

   public static boolean isNewsletterPublication(String number) {
      boolean result = false;
      String key = "" + determineNodeType(number);
      if (key.equals("newsletterpublication")) {
         result = true;
      }
      return (result);
   }

   public static boolean isNewsletterOrPublication(String number) {
      boolean result = false;
      if (number != null) {
         if (isNewsletter(number) == true || isNewsletterPublication(number) == true) {
            result = true;
         }
      }
      return (result);
   }

}