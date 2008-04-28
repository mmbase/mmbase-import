package com.finalist.newsletter.util;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sf.mmapps.commons.beans.MMBaseNodeMapper;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.Query;
import org.mmbase.bridge.util.Queries;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.storage.search.Constraint;
import org.mmbase.storage.search.FieldValueInConstraint;
import org.mmbase.storage.search.RelationStep;
import org.mmbase.storage.search.Step;

import com.finalist.cmsc.beans.om.ContentElement;
import com.finalist.newsletter.cao.impl.NewsletterSubscriptionCAOImpl;

public abstract class NewsletterUtil {
   private static Log log = LogFactory
   .getLog(NewsletterUtil.class);
   public static final String NEWSLETTER = "newsletter";
   public static final String NEWSLETTERPUBLICATION = "newsletterpublication";

   public static final String THEMETYPE_NEWSLETTER = "newslettertheme";
   public static final String THEMETYPE_NEWSLETTERPUBLICATION = "newsletterpublicationtheme";

   public static int countNewsletters() {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      NodeList newsletterList = SearchUtil.findNodeList(cloud, "newsletter");
      if (newsletterList != null) {
         return (0 + newsletterList.size());
      }
      return (0);
   }

   public static int countPublications() {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      NodeList publicationList = SearchUtil.findNodeList(cloud, "newsletterpublication");
      if (publicationList != null) {
         return (0 + publicationList.size());
      }
      return (0);
   }

   public static int countPublications(int newsletterNumber) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      Node newsletterNode = cloud.getNode(newsletterNumber);
      NodeList publicationsList = newsletterNode.getRelatedNodes("newsletterpublication");
      if (publicationsList != null) {
         return (publicationsList.size());
      }
      return (0);
   }

   public static int countThemes() {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      NodeList themeList = SearchUtil.findNodeList(cloud, "newslettertheme");
      if (themeList != null) {
         int newsletters = countNewsletters();
         return (0 - newsletters + themeList.size());
      }
      return (0);
   }

   public static int countThemes(int newsletterNumber) {
      int amount = 0;
      List<Integer> themes = getAllThemes(newsletterNumber);
      if (themes != null) {
         amount = themes.size();
      }
      return (amount);
   }

   public static void deleteNewsletterThemesForNewsletter(int number) {
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

   public static String determineNodeType(int number) {
      if (number > 0) {
         Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
         Node node = cloud.getNode(number);
         String type = node.getNodeManager().getName();
         return (type);
      }
      return (null);
   }

   public static String determineThemeType(int number) {
      String themeType = null;
      if (isNewsletter(number)) {
         themeType = NewsletterUtil.THEMETYPE_NEWSLETTER;
      }
      if (isNewsletterPublication(number)) {
         themeType = NewsletterUtil.THEMETYPE_NEWSLETTERPUBLICATION;
      }
      return (themeType);
   }

   public static int findNewsletterForTheme(int themeNumber) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getAdminCloud();
      Node themeNode = cloud.getNode(themeNumber);
      String managerName = null;
      String themeType = themeNode.getNodeManager().getName();
      if (themeType.equals("newslettertheme")) {
         managerName = "newsletter";
      } else {
         managerName = "newsletterpublication";
      }
      Node newsletterNode = SearchUtil.findRelatedNode(themeNode, managerName, "newslettertheme");
      if (newsletterNode != null) {
         return (newsletterNode.getNumber());
      }
      return (0);
   }

   public static List<Integer> getAllNewsletters() {
      List<Integer> newsletters = new ArrayList<Integer>();
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      NodeList newsletterList = SearchUtil.findNodeList(cloud, NEWSLETTER);
      if (newsletterList != null && newsletterList.size() > 0) {
         for (int n = 0; n < newsletterList.size(); n++) {
            Node newsletterNode = newsletterList.getNode(n);
            int newsletterNumber = newsletterNode.getNumber();
            newsletters.add(newsletterNumber);
         }
      }
      return (newsletters);
   }

   public static List<Integer> getAllPublications() {
      List<Integer> publications = new ArrayList<Integer>();
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      NodeList publicationList = SearchUtil.findNodeList(cloud, "newsletterpublication");
      if (publicationList != null && publicationList.size() > 0) {
         for (int n = 0; n < publicationList.size(); n++) {
            Node publicationNode = publicationList.getNode(n);
            int publicationNumber = publicationNode.getNumber();
            publications.add(publicationNumber);
         }
      }
      return (publications);
   }

   public static List<Integer> getAllThemes() {
      List<Integer> themes = new ArrayList<Integer>();
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      NodeList themeList = SearchUtil.findNodeList(cloud, "newslettertheme");
      if (themeList != null && themeList.size() > 0) {
         for (int n = 0; n < themeList.size(); n++) {
            Node themeNode = themeList.getNode(n);
            int themeNumber = themeNode.getNumber();
            themes.add(themeNumber);
         }
      }
      return (themes);
   }

   public static List<Integer> getAllThemes(int number) {
      List<Integer> themes = new ArrayList<Integer>();
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      Node newsletterNode = cloud.getNode(number);
      String themeType = determineThemeType(number);
      NodeList themeList = SearchUtil.findRelatedNodeList(newsletterNode, themeType, "newslettertheme");
      for (int i = 0; i < themeList.size(); i++) {
         Node themeNode = themeList.getNode(i);
         int theme = themeNode.getNumber();
         themes.add(theme);
      }
      return (themes);
   }

   public static List<Integer> getArticlesForTheme(int themeNumber) {
      if (themeNumber > 0) {
         List<Integer> articles = new ArrayList<Integer>();
         Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
         Node themeNode = cloud.getNode(themeNumber);
         NodeManager articleNodeManager = cloud.getNodeManager("article");
         NodeList articleList = themeNode.getRelatedNodes(articleNodeManager);
         if (articleList != null) {
            for (int i = 0; i < articleList.size(); i++) {
               Node articleNode = articleList.getNode(i);
               int article = articleNode.getNumber();
               articles.add(article);
            }
         }
         return (articles);
      }
      return (null);
   }
   
   public static List<ContentElement> getArticlesByNewsletter(int newsletterNumber,int offset,int elementsPerPage,String orderBy,String direction) {
      if (newsletterNumber > 0) {
         List<ContentElement> articles = new ArrayList<ContentElement>();
         Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
         Node newsletterNode = cloud.getNode(newsletterNumber);
         
         NodeManager tagNodeManager = cloud.getNodeManager("tag");   
         NodeList tags = newsletterNode.getRelatedNodes(tagNodeManager);
         NodeManager articleNodeManager = cloud.getNodeManager("article"); 
         Node tag = null;     
         SortedSet<Integer> sort= new TreeSet<Integer>();
         for (int i = 0 ; i < tags.size(); i++) {
            tag =  tags.getNode(i);
            sort.add(new Integer(tag.getNumber()));
         }
         if(sort.size() == 0) {
            return (null);
         }
         NodeQuery query = cloud.createNodeQuery();
         Step parameterStep = query.addStep(articleNodeManager);
         query.setNodeStep(parameterStep);
         query.addRelationStep(tagNodeManager,
               null, null);
         SearchUtil.addInConstraint(query, tagNodeManager
               .getField("number"), sort);           
         Queries.addSortOrders(query, orderBy, direction);
         query.setOffset(offset);
         query.setMaxNumber(elementsPerPage);
         NodeList articleList =query.getList();
         if (articleList != null) {
            for (int i = 0; i < articleList.size(); i++) {
               Node articleNode = articleList.getNode(i);
               ContentElement element = MMBaseNodeMapper.copyNode(articleNode, ContentElement.class);
               articles.add(element);
            }
         }
         
         return (articles);
      }
      return (null);
   }
   
   public static int countArticlesByNewsletter(int newsletterNumber) {
      if (newsletterNumber > 0) {
         Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
         Node newsletterNode = cloud.getNode(newsletterNumber);
         
         NodeManager tagNodeManager = cloud.getNodeManager("tag");   
         NodeList tags = newsletterNode.getRelatedNodes(tagNodeManager);
         NodeManager articleNodeManager = cloud.getNodeManager("article"); 
         Node tag = null;     
         SortedSet<Integer> sort= new TreeSet<Integer>();
         for (int i = 0 ; i < tags.size(); i++) {
            tag =  tags.getNode(i);
            sort.add(new Integer(tag.getNumber()));
         }
         if(sort.size() == 0) {
            return (0);
         }
         NodeQuery query = cloud.createNodeQuery();
         Step parameterStep = query.addStep(articleNodeManager);
         query.setNodeStep(parameterStep);
         query.addRelationStep(tagNodeManager,
               null, null);
         SearchUtil.addInConstraint(query, tagNodeManager
               .getField("number"), sort);  
         
//         NodeQuery  query =  Queries.createRelatedNodesQuery(tag,articleNodeManager,null,null);
         return Queries.count(query);
      }
      return (0);
   }
   
   public static int getDefaultTheme(int number) {
      int defaultTheme = 0;
      if (number > 0) {
         Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
         Node newsletterNode = cloud.getNode(number);
         String themeType = determineThemeType(number);
         Node defaultThemeNode = SearchUtil.findRelatedNode(newsletterNode, themeType, "defaulttheme");
         if (defaultThemeNode != null) {
            defaultTheme = defaultThemeNode.getNumber();
         }
      }
      return (defaultTheme);
   }

   public static String getTitle(int newsletterNumber) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      Node newsletterNode = cloud.getNode(newsletterNumber);
      String title = newsletterNode.getStringValue("title");
      return (title);
   }

   public static boolean isNewsletter(int number) {
      boolean result = false;
      if (number > 0) {
         String key = "" + determineNodeType(number);
         if (key.equals("newsletter")) {
            result = true;
         }
      }
      return (result);
   }

   public static boolean isNewsletterOrPublication(int number) {
      boolean result = false;
      if (number > 0) {
         if (isNewsletter(number) == true || isNewsletterPublication(number) == true) {
            result = true;
         }
      }
      return (result);
   }

   public static boolean isNewsletterPublication(int number) {
      boolean result = false;
      if (number > 0) {
         String key = "" + determineNodeType(number);
         if (key.equals("newsletterpublication")) {
            result = true;
         }
      }
      return (result);
   }

   public static List<Integer> removeDuplicates(List<Integer> primary, List<Integer> secundary) {
      if (primary != null && secundary != null) {
         List<Integer> removals = new ArrayList<Integer>();
         for (int i = 0; i < secundary.size(); i++) {
            int key = secundary.get(i);
            if (primary.contains(key)) {
               removals.add(key);

            }
         }
         for (int r = 0; r < removals.size(); r++) {
            secundary.remove(removals.get(r));
         }
      }
      return (secundary);
   }

   public static boolean isPaused(int number) {
      if (number > 0) {
         Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
         Node newsletterNode = cloud.getNode(number);
         if (newsletterNode != null) {
            boolean isPaused = newsletterNode.getBooleanValue("paused");
            return (isPaused);
         }
      }
      return (false);
   }

   public static void pauseNewsletter(int number) {
      if (number > 0) {
         Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
         Node newsletterNode = cloud.getNode(number);
         if (newsletterNode != null) {
            newsletterNode.setBooleanValue("paused", true);
            newsletterNode.commit();
         }
      }
   }

   public static void resumeNewsletter(int number) {
      if (number > 0) {
         Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
         Node newsletterNode = cloud.getNode(number);
         if (newsletterNode != null) {
            newsletterNode.setBooleanValue("paused", false);
            newsletterNode.commit();
         }
      }
   }
}