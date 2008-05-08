package com.finalist.newsletter.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sf.mmapps.commons.beans.MMBaseNodeMapper;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.lang.StringUtils;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.RelationList;
import org.mmbase.bridge.util.Queries;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.storage.search.Step;

import com.finalist.cmsc.beans.om.ContentElement;
import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.newsletter.domain.Term;
import com.finalist.newsletter.NewsletterSendFailException;
import com.finalist.portlets.newsletter.NewsletterContentPortlet;

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

   public static void deleteNewsletterTermsForNewsletter(int number) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getAdminCloud();
      Node newsletterNode = cloud.getNode(number);
      NodeManager newsletterTermNodeManager = cloud.getNodeManager("term");
      NodeList terms = newsletterNode.getRelatedNodes(newsletterTermNodeManager);
      if (terms != null) {
         for (int i = 0; i < terms.size(); i++) {
            Node termNode = terms.getNode(i);
            termNode.deleteRelations();
            termNode.delete();
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

   public static List<ContentElement> getArticlesByNewsletter(String termNumbers, int offset, int elementsPerPage, String orderBy, String direction) {

      String[] numbers = termNumbers.split(",");
      SortedSet<Integer> sort = new TreeSet<Integer>();
      for (int i = 0; i < numbers.length; i++) {
         sort.add(new Integer(numbers[i]));
      }
      if (sort.size() == 0) {
         return (null);
      }
      return getArticles(offset, elementsPerPage, orderBy, direction, sort);
   }

   public static List<ContentElement> getArticlesByNewsletter(int newsletterNumber, int offset, int elementsPerPage, String orderBy, String direction) {
      if (newsletterNumber > 0) {
         Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
         Node newsletterNode = cloud.getNode(newsletterNumber);
         NodeManager termNodeManager = cloud.getNodeManager("term");
         NodeList terms = newsletterNode.getRelatedNodes(termNodeManager);

         SortedSet<Integer> sort = new TreeSet<Integer>();
         for (int i = 0; i < terms.size(); i++) {
            Node term = terms.getNode(i);
            sort.add(new Integer(term.getNumber()));
         }
         if (sort.size() == 0) {
            return (null);
         }
         return getArticles(offset, elementsPerPage, orderBy, direction, sort);
      }
      return (null);
   }

   public static List<ContentElement> getArticles(int offset, int elementsPerPage, String orderBy, String direction, SortedSet<Integer> sort) {
      List<ContentElement> articles = new ArrayList<ContentElement>();
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();

      NodeManager termNodeManager = cloud.getNodeManager("term");
      NodeManager articleNodeManager = cloud.getNodeManager("article");
      NodeQuery query = cloud.createNodeQuery();
      Step parameterStep = query.addStep(articleNodeManager);
      query.setNodeStep(parameterStep);
      query.addRelationStep(termNodeManager, null, null);
      SearchUtil.addInConstraint(query, termNodeManager.getField("number"), sort);
      Queries.addSortOrders(query, orderBy, direction);
      query.setOffset(offset);
      query.setMaxNumber(elementsPerPage);
      NodeList articleList = query.getList();
      if (articleList != null) {
         for (int i = 0; i < articleList.size(); i++) {
            Node articleNode = articleList.getNode(i);
            ContentElement element = MMBaseNodeMapper.copyNode(articleNode, ContentElement.class);
            articles.add(element);
         }
      }
      return (articles);

   }

   public static int countArticles(SortedSet<Integer> sort) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();

      NodeManager termNodeManager = cloud.getNodeManager("term");
      NodeManager articleNodeManager = cloud.getNodeManager("article");
      NodeQuery query = cloud.createNodeQuery();
      Step parameterStep = query.addStep(articleNodeManager);
      query.setNodeStep(parameterStep);
      query.addRelationStep(termNodeManager, null, null);
      SearchUtil.addInConstraint(query, termNodeManager.getField("number"), sort);

      return Queries.count(query);
   }

   public static int countArticlesByNewsletter(String termNumbers) {

      String[] numbers = termNumbers.split(",");
      SortedSet<Integer> sort = new TreeSet<Integer>();
      for (int i = 0; i < numbers.length; i++) {
         sort.add(new Integer(numbers[i]));
      }
      if (sort.size() == 0) {
         return (0);
      }
      return countArticles(sort);
   }

   public static int countArticlesByNewsletter(int newsletterNumber) {
      if (newsletterNumber > 0) {
         Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
         Node newsletterNode = cloud.getNode(newsletterNumber);

         NodeManager termNodeManager = cloud.getNodeManager("term");
         NodeList terms = newsletterNode.getRelatedNodes(termNodeManager);
         SortedSet<Integer> sort = new TreeSet<Integer>();
         for (int i = 0; i < terms.size(); i++) {
            Node term = terms.getNode(i);
            sort.add(new Integer(term.getNumber()));
         }
         if (sort.size() == 0) {
            return (0);
         }
//         NodeQuery  query =  Queries.createRelatedNodesQuery(term,articleNodeManager,null,null);
         return countArticles(sort);
      }
      return (0);
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
         return isPaused(newsletterNode);
      }
      return (false);
   }

   public static boolean isPaused(Node newsletterNode) {
      if (newsletterNode != null) {
         boolean isPaused = newsletterNode.getBooleanValue("paused");
         Date now = new Date();
         return (isPaused && now.after(newsletterNode.getDateValue("pausedstartdate")) && now.before(newsletterNode.getDateValue("pausedstopdate")));
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

   public static String getTermURL(String url, Set<Term> terms, int publicationId) {
      if (null != terms) {
         log.debug("get publication " + publicationId + " with " + terms.size() + " terms");
         Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
         Node publicationNode = cloud.getNode(publicationId);

         List<Node> relatedportlets = publicationNode.getRelatedNodes("portlet");

         String termIds = "";
         for (Term term : terms) {
            termIds += term.getId() + ",";
         }
         if (termIds.endsWith(",")) {
            termIds = termIds.substring(0, termIds.length() - 2);
         }
         for (Node portlet : relatedportlets) {
            List<Node> portletdefNodes = portlet.getRelatedNodes("portletdefinition");
            String portletDefinition = portletdefNodes.get(0).getStringValue("definition");
            if (portletDefinition.equals(NewsletterContentPortlet.DEFINITION)) {
               RelationList relations = portlet.getRelations("portletrel", publicationNode.getNodeManager());
               String name = relations.getNode(0).getStringValue("name");
               url += "/_rp_".concat(name).concat("_").concat(NewsletterContentPortlet.NEWSLETTER_TERMS_PARAM).concat("/1_").concat(termIds);
            }
         }
      }
      return url;
   }


   public static String getHostUrl() {
      String url = getServerURL();
      if (StringUtils.isNotBlank(getApplicatoinURL())) {
         url += getApplicatoinURL();
      }
      if (!url.endsWith("/")) {
         url += "/";
      }
      return url;
   }

   public static String getServerURL() {
      String hostUrl = PropertiesUtil.getProperty("host");

      if (StringUtils.isEmpty(hostUrl)) {
         throw new NewsletterSendFailException("get property <host> from system property and get nothing");
      }

      log.debug("get property <host> from system property and get:" + hostUrl);

      if (!hostUrl.endsWith("/")) {
         hostUrl += "/";
      }
      return hostUrl;
   }

   public static String getApplicatoinURL() {
      String application = PropertiesUtil.getProperty("application");

      if (StringUtils.isEmpty(application)) {
         throw new NewsletterSendFailException("get property <application> from system property and get nothing");
      }

      log.debug("get property <host> from system property and get:" + application);

      return application;
   }

}