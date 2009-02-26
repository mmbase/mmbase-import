package com.finalist.newsletter.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.RelationList;
import org.mmbase.bridge.RelationManager;
import org.mmbase.bridge.util.Queries;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.storage.search.FieldValueConstraint;
import org.mmbase.storage.search.RelationStep;
import org.mmbase.storage.search.Step;
import org.mmbase.storage.search.StepField;

import com.finalist.cmsc.beans.MMBaseNodeMapper;
import com.finalist.cmsc.beans.om.ContentElement;
import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.mmbase.RelationUtil;
import com.finalist.newsletter.NewsletterSendFailException;
import com.finalist.newsletter.cao.impl.NewsLetterStatisticCAOImpl;
import com.finalist.newsletter.domain.Schedule;
import com.finalist.newsletter.domain.Term;
import com.finalist.newsletter.domain.StatisticResult.HANDLE;
import com.finalist.newsletter.services.impl.StatisticServiceImpl;
import com.finalist.portlets.newsletter.NewsletterContentPortlet;

public abstract class NewsletterUtil {
   
   private static Log log = LogFactory.getLog(NewsletterUtil.class);
   
   public static final String DOWN = "DOWN";
   public static final String POSREL = "posrel";
   public static final String SCHEDULE = "schedule";
   public static final String SYSTEM_LIVEPATH = "system.livepath";
   public static final String PORTLETREL = "portletrel";
   public static final String PORTLETDEFINITION = "portletdefinition";
   public static final String PORTLET = "portlet";
   public static final String TITLE = "title";
   public static final String SOURCE = "source";
   public static final String NEWSLETTERCONTENT = "newslettercontent";
   public static final String NUMBER = "number";
   public static final String ARTICLE = "article";
   public static final String NEWSLETTER = "newsletter";
   public static final String M_VALUE = "value";
   public static final String NEWSLETTERPUBLICATION = "newsletterpublication";
   public static final String RELATED = "related";

   public static int countNewsletters() {

      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      NodeList newsletterList = SearchUtil.findNodeList(cloud, NEWSLETTER);
      if (newsletterList != null) {
         return (0 + newsletterList.size());
      }
      return (0);
   }

   public static int countPublications() {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      NodeList publicationList = SearchUtil.findNodeList(cloud, NEWSLETTERPUBLICATION);
      if (publicationList != null) {
         return (0 + publicationList.size());
      }
      return (0);
   }

   public static int countPublications(int newsletterNumber) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      Node newsletterNode = cloud.getNode(newsletterNumber);
      NodeList publicationsList = newsletterNode.getRelatedNodes(NEWSLETTERPUBLICATION);

      if (publicationsList != null) {
         return (publicationsList.size());
      }
      return (0);
   }

   public static void deleteRelatedElement(int number) {

      Cloud cloud = CloudProviderFactory.getCloudProvider().getAdminCloud();
      Node newsletterNode = cloud.getNode(number);
      deleteSubscriptionByNewsletter(newsletterNode);
      deleteNewsletterLogForNewsletter(number);
      deleteSubscriptionForNewsletter(number);
   }
   
   public static void deleteSubscriptionForNewsletter(int newsletterNumber) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getAdminCloud();
      NodeManager nodeparameterManager = cloud.getNodeManager("nodeparameter");
      NodeQuery query = cloud.createNodeQuery();
      Step step = query.addStep(nodeparameterManager);
      query.setNodeStep(step);
      SearchUtil.addEqualConstraint(query, nodeparameterManager.getField(M_VALUE), newsletterNumber);
  
      NodeList nodeparameters = query.getList();
      if (nodeparameters != null) {
         for (int i = 0; i < nodeparameters.size(); i++) {
            Node logNode = nodeparameters.getNode(i);
            logNode.deleteRelations();
            logNode.delete();
         }
      }
   }

   public static void deleteNewsletterLogForNewsletter(int newsletterNumber) {

      Cloud cloud = CloudProviderFactory.getCloudProvider().getAdminCloud();
      NodeManager newsletterLogManager = cloud.getNodeManager("newsletterdailylog");
      NodeQuery query = cloud.createNodeQuery();
      Step step = query.addStep(newsletterLogManager);
      query.setNodeStep(step);
      SearchUtil.addEqualConstraint(query, newsletterLogManager.getField(NEWSLETTER), newsletterNumber);
  
      NodeList logs = query.getList();
      if (logs != null) {
         for (int i = 0; i < logs.size(); i++) {
            Node logNode = logs.getNode(i);
            logNode.deleteRelations();
            logNode.delete();
         }
      }
   }

   public static void deleteSubscriptionByNewsletter(Node newsletterNode) {

      NodeManager subscriptionNodeManager = newsletterNode.getCloud().getNodeManager("subscriptionrecord");
      NodeList subscriptions = newsletterNode.getRelatedNodes(subscriptionNodeManager);
      if (subscriptions != null) {
         for (int i = 0; i < subscriptions.size(); i++) {
            Node subscriptionNode = subscriptions.getNode(i);
            subscriptionNode.deleteRelations();
            subscriptionNode.delete();
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
      NodeList publicationList = SearchUtil.findNodeList(cloud, NEWSLETTERPUBLICATION);
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
         NodeManager articleNodeManager = cloud.getNodeManager(ARTICLE);
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

   public static List<ContentElement> getArticlesByNewsletter(int itemNumber, String termNumbers, int offset, int elementsPerPage, String orderBy, String direction) {

      String[] numbers = termNumbers.split(",");
      SortedSet<Integer> sort = new TreeSet<Integer>();
      for (String number : numbers) {
         sort.add(new Integer(number));
      }
      if (sort.size() == 0) {
         return (null);
      }
      return getArticles(itemNumber, offset, elementsPerPage, orderBy, direction, sort);
   }

   public static List<ContentElement> getArticlesByNewsletter(int newsletterNumber, int offset, int elementsPerPage, String orderBy, String direction) {
      if (newsletterNumber > 0) {
         return getArticles(offset, elementsPerPage, orderBy, direction, newsletterNumber);
      }
      return (null);
   }

   public static List<ContentElement> getArticles(int offset, int elementsPerPage, String orderBy, String direction, int number) {
      List<ContentElement> articles = new ArrayList<ContentElement>();
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();

      NodeManager newsletterNodeManager = cloud.getNodeManager(NEWSLETTER);
      NodeManager articleNodeManager = cloud.getNodeManager(ARTICLE);
      NodeQuery query = cloud.createNodeQuery();
      Step parameterStep = query.addStep(articleNodeManager);
      query.setNodeStep(parameterStep);
      RelationStep relationStep = query.addRelationStep(newsletterNodeManager, null, null);
      Step step = relationStep.getNext();
      StepField stepField = query.createStepField(step, newsletterNodeManager.getField(NUMBER));
      FieldValueConstraint constraint = query.createConstraint(stepField, number);
      SearchUtil.addConstraint(query, constraint);
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

   public static List<ContentElement> getArticles(int newsletterNumber, int offset, int elementsPerPage, String orderBy, String direction, SortedSet<Integer> sort) {
      List<ContentElement> articles = new ArrayList<ContentElement>();

      List<Node> relatedArticles = getArticles(newsletterNumber, sort, orderBy, direction);
      if (relatedArticles == null) {
         return null;
      }
      if (relatedArticles.size() > offset) {
         int totalCount = 0;
         if (offset + elementsPerPage >= relatedArticles.size()) {
            totalCount = relatedArticles.size();
         } else {
            totalCount = offset + elementsPerPage;
         }
         for (int i = offset; i < totalCount; i++) {
            Node articleNode = relatedArticles.get(i);
            ContentElement element = MMBaseNodeMapper.copyNode(articleNode, ContentElement.class);
            articles.add(element);
         }
      }
      return (articles);

   }

   public static int countArticles(int newsletterNumber, SortedSet<Integer> sort) {

      List<Node> articles = getArticles(newsletterNumber, sort, null, null);
      return articles == null ? 0 : articles.size();
   }

   public static List<Node> getArticles(int newsletterNumber, SortedSet<Integer> sort, String orderBy, String direction) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      NodeManager articleNodeManager = cloud.getNodeManager(ARTICLE);

      NodeManager newsletterNodeManager = cloud.getNodeManager(NEWSLETTER);
      NodeQuery articleQuery = cloud.createNodeQuery();
      Step articleStep = articleQuery.addStep(articleNodeManager);
      articleQuery.setNodeStep(articleStep);
      RelationStep relationStep = articleQuery.addRelationStep(newsletterNodeManager, null, null);
      Step step = relationStep.getNext();
      StepField stepField = articleQuery.createStepField(step, newsletterNodeManager.getField(NUMBER));
      FieldValueConstraint constraint = articleQuery.createConstraint(stepField, newsletterNumber);
      SearchUtil.addConstraint(articleQuery, constraint);
      Queries.addSortOrders(articleQuery, orderBy, direction);

      NodeList articleNodes = articleQuery.getList();
      NodeManager termNodeManager = cloud.getNodeManager("term");

      NodeQuery query = cloud.createNodeQuery();
      Step parameterStep = query.addStep(articleNodeManager);
      query.setNodeStep(parameterStep);
      query.addRelationStep(termNodeManager, NEWSLETTERCONTENT, SearchUtil.DESTINATION);
      SearchUtil.addInConstraint(query, termNodeManager.getField(NUMBER), sort);
      NodeList termRelatedArticles = query.getList();

      List<Node> articles = new ArrayList<Node>();

      if (articleNodes != null) {
         for (int i = 0; i < articleNodes.size(); i++) {
            Node article = articleNodes.getNode(i);
            if (termRelatedArticles == null) {
               return null;
            }
            for (int j = 0; j < termRelatedArticles.size(); j++) {
               Node termRelatedArticle = termRelatedArticles.getNode(j);
               if (termRelatedArticle.getNumber() == article.getNumber()) {
                  articles.add(article);
                  break;
               }
            }
         }
      }
      return articles;

   }

   public static int countArticles(int number) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      NodeManager articleNodeManager = cloud.getNodeManager(ARTICLE);
      Node newsletterNode = cloud.getNode(number);
      int count = newsletterNode.countRelatedNodes(articleNodeManager, NEWSLETTERCONTENT, SOURCE);

      return count;
   }

   public static int countArticlesByNewsletter(int itemNumber, String termNumbers) {

      String[] numbers = termNumbers.split(",");
      SortedSet<Integer> sort = new TreeSet<Integer>();
      for (String number : numbers) {
         sort.add(new Integer(number));
      }
      if (sort.size() == 0) {
         return (0);
      }
      return countArticles(itemNumber, sort);
   }

   public static int countArticlesByNewsletter(int newsletterNumber) {
      if (newsletterNumber > 0) {
         return countArticles(newsletterNumber);
      }
      return (0);
   }


   public static String getTitle(int newsletterNumber) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      Node newsletterNode = cloud.getNode(newsletterNumber);
      String title = newsletterNode.getStringValue(TITLE);
      return (title);
   }

   public static boolean isNewsletter(int number) {
      boolean result = false;
      if (number > 0) {
         String key = "" + determineNodeType(number);
         if (key.equals(NEWSLETTER)) {
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
         if (key.equals(NEWSLETTERPUBLICATION)) {
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
         return "true".equals(newsletterNode.getStringValue("paused"));
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
         log.debug("Pause newsletter " + number);
         Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
         Node newsletterNode = cloud.getNode(number);
         if (newsletterNode != null) {
            newsletterNode.setStringValue("paused", "true");
            newsletterNode.commit();
         }
      }
   }

   public static void resumeNewsletter(int number) {
      if (number > 0) {
         Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
         Node newsletterNode = cloud.getNode(number);
         if (newsletterNode != null) {
            newsletterNode.setStringValue("paused", "false");
            newsletterNode.commit();
         }
      }
   }

   public static String getTermURL(String url, Set<Term> terms, int publicationId) {
      if (null != terms) {
         log.debug("get publication " + publicationId + " with " + terms.size() + " terms");
         Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
         Node publicationNode = cloud.getNode(publicationId);

         List<Node> relatedportlets = publicationNode.getRelatedNodes(PORTLET);

         String termIds = "";
         for (Term term : terms) {
            termIds += term.getId() + ",";
         }
         if (termIds.endsWith(",")) {
            termIds = termIds.substring(0, termIds.length() - 1);
         }
         for (Node portlet : relatedportlets) {
            List<Node> portletdefNodes = portlet.getRelatedNodes(PORTLETDEFINITION);
            String portletDefinition = portletdefNodes.get(0).getStringValue("definition");
            if (portletDefinition.equals(NewsletterContentPortlet.DEFINITION)) {
               RelationList relations = portlet.getRelations(PORTLETREL, publicationNode.getNodeManager());
               String name = relations.getRelation(0).getStringValue("name");
               url += "/_rp_".concat(name).concat("_").concat(NewsletterContentPortlet.NEWSLETTER_TERMS_PARAM).concat("/1_").concat(termIds);
            }
         }
      }
      return url;
   }


   public static String getServerURL() {
      String hostUrl = PropertiesUtil.getProperty(SYSTEM_LIVEPATH);

      if (StringUtils.isEmpty(hostUrl)) {
         throw new NewsletterSendFailException("get property <system.livepath> from system property and get nothing");
      }

      log.debug("get property <system.livepath> from system property and get:" + hostUrl);

      if (!hostUrl.endsWith("/")) {
         hostUrl += "/";
      }
      return hostUrl;
   }


   public static void logPubliction(int newsletterId, HANDLE handle) {
      StatisticServiceImpl service = new StatisticServiceImpl();
      NewsLetterStatisticCAOImpl statisticCAO = new NewsLetterStatisticCAOImpl();
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      statisticCAO.setCloud(cloud);
      service.setStatisticCAO(statisticCAO);
      service.logPubliction(newsletterId, handle);
   }


   public static String calibrateRelativeURL(String inputString) {
      return calibrateRelativeURL(inputString, NewsletterUtil.getServerURL());
   }

   public static String calibrateRelativeURL(String inputString, String liveURL) {

      if (liveURL.charAt(liveURL.length() - 1) == '/') {
         liveURL = liveURL.substring(0, liveURL.lastIndexOf("/"));
      }

      if (StringUtils.split(liveURL, "/").length > 2) {
         liveURL = liveURL.substring(0, liveURL.lastIndexOf("/"));
      }

      liveURL += "/";

      inputString = StringUtils.replace(inputString, "href=\"/", "href=\"" + liveURL);
      inputString = StringUtils.replace(inputString, "src=\"/", "src=\"" + liveURL);
      return inputString;
   }

   public static void addScheduleForNewsletter(Node newsletterNode) {
      NodeManager scheduleNodeManager = newsletterNode.getCloud().getNodeManager(SCHEDULE);
      NodeList schedules = SearchUtil.findRelatedOrderedNodeList(newsletterNode, SCHEDULE, POSREL, "createdatetime", DOWN);
      if (schedules.size() == 0) {
         addScheduleNode(newsletterNode, scheduleNodeManager);
      } else {
         Node firstScheduleNode = schedules.getNode(0);
         if (firstScheduleNode.getStringValue("expression").equals(newsletterNode.getStringValue(SCHEDULE))) {
            return;
         }
         for (int i = 0; i < schedules.size(); i++) {
            Node scheduleNode = schedules.getNode(i);
            if (scheduleNode.getStringValue("expression").equals(newsletterNode.getStringValue(SCHEDULE))) {
               scheduleNode.setLongValue("createdatetime", System.currentTimeMillis());
               scheduleNode.commit();
               return;
            }
         }
         addScheduleNode(newsletterNode, scheduleNodeManager);
      }
   }

   public static void addScheduleNode(Node newsletterNode, NodeManager scheduleNodeManager) {
      if (StringUtils.isEmpty(newsletterNode.getStringValue(SCHEDULE))) {
         return;
      }
      Node scheduleNode = scheduleNodeManager.createNode();
      scheduleNode.setStringValue("expression", newsletterNode.getStringValue(SCHEDULE));
      scheduleNode.setLongValue("createdatetime", System.currentTimeMillis());
      scheduleNode.commit();
      RelationUtil.createRelation(newsletterNode, scheduleNode, POSREL);
   }

   public static List<Schedule> getSchedulesBynewsletterId(int id,Locale language) {
      List<Schedule> schedules = new ArrayList<Schedule>();
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      Node newsletterNode = cloud.getNode(id);
      NodeList scheduleList = SearchUtil.findRelatedOrderedNodeList(newsletterNode, SCHEDULE, POSREL, "createdatetime", DOWN);
      for (int i = 1; i < scheduleList.size(); i++) {
         Node scheduleNode = scheduleList.getNode(i);
         Schedule schedule = new Schedule();
         schedule.setId(scheduleNode.getIntValue(NUMBER));
         schedule.setExpression(scheduleNode.getStringValue("expression"));
         if(null!=language){
            schedule.setScheduleDescription(getScheduleMessageByExpression(scheduleNode.getStringValue("expression"),language));
         }else{
            schedule.setScheduleDescription(getScheduleMessageByExpression(scheduleNode.getStringValue("expression")));
         }
         schedules.add(schedule);
      }
      return schedules;
   }

   public static String getScheduleMessageByExpression(String expression,Locale language) {
      if (StringUtils.isEmpty(expression)) {
         return "";
      }
      StringBuilder scheduleMessage = null;
      ResourceBundle rb = ResourceBundle.getBundle("cmsc-calendar");
      if(null!=language){
         rb = ResourceBundle.getBundle("cmsc-calendar",language);
      }
      String[] expressions = expression.split("\\|");
      String type;
      if (expressions == null || expressions.length == 0) {
         return null;
      }
      scheduleMessage = new StringBuilder();
      type = expressions[0];
      if (type.equals("1")) {
         scheduleMessage.append(rb.getString("calendar.once") + ",");
         scheduleMessage.append(rb.getString("calendar.start.datetime"));
         scheduleMessage.append(expressions[1]).append(" ").append(expressions[2]).append(":").append(expressions[3]);
      } else if (type.equals("2")) {
         scheduleMessage.append(rb.getString("calendar.daily") + ",");
         scheduleMessage.append(rb.getString("calendar.start.datetime"));
         scheduleMessage.append(expressions[1]).append(" ").append(expressions[2]).append(":").append(expressions[3]);
         scheduleMessage.append("<br/>");
         if (expressions[4].equals("0")) {
            scheduleMessage.append(rb.getString("calendar.approach.interval.pre")).append(rb.getString("calendar.approach.interval.day"));
         } else if (expressions[4].equals("1")) {
            scheduleMessage.append(rb.getString("calendar.approach.interval.pre")).append(rb.getString("calendar.approach.weekday"));
         } else if (expressions[4].equals("2")) {
            scheduleMessage.append(rb.getString("calendar.approach.interval.pre")).append(" ").append(expressions[5]).append(" ").append(rb.getString("calendar.approach.interval.day"));
         }
      } else if (type.equals("3")) {
         scheduleMessage.append(rb.getString("calendar.weekly")).append(",").append(rb.getString("calendar.start.datetime"));
         scheduleMessage.append(expressions[1]).append(":").append(expressions[2]);
         scheduleMessage.append("<br/>").append(rb.getString("calendar.approach.interval.pre")).append(expressions[3]).append(rb.getString("calendar.approach.interval.week"));

         String tempWeek = "";
         for (int i = 0; i < expressions[4].length(); i++) {
            String month = expressions[4].substring(i, i + 1);
            if (month.equals("1")) {
               tempWeek += rb.getString("calendar.week.monday") + ",";
            } else if (month.equals("2")) {
               tempWeek += rb.getString("calendar.week.tuesday") + ",";
            } else if (month.equals("3")) {
               tempWeek += rb.getString("calendar.week.wednesday") + ",";
            } else if (month.equals("4")) {
               tempWeek += rb.getString("calendar.week.thursday") + ",";
            } else if (month.equals("5")) {
               tempWeek += rb.getString("calendar.week.friday") + ",";
            } else if (month.equals("6")) {
               tempWeek += rb.getString("calendar.week.saturday") + ",";
            } else if (month.equals("7")) {
               tempWeek += rb.getString("calendar.week.sunday") + ",";
            }
         }
         if (StringUtils.isNotEmpty(tempWeek)) {
            if (tempWeek.endsWith(",")) {
               tempWeek = tempWeek.substring(0, tempWeek.length() - 1);
            }
         }
         scheduleMessage.append("<br/>").append(rb.getString("calendar.approach.interval.week")).append(":").append(tempWeek);
      } else {
         scheduleMessage.append(rb.getString("calendar.monthly")).append(",").append(rb.getString("calendar.start.datetime")).append(expressions[1]).append(":").append(expressions[2]);
         String months = "";
         if (expressions[3].equals("0")) {
            scheduleMessage.append("<br/>").append(rb.getString("calendar.approach.interval.pre")).append(" ").append(expressions[4]).append(" ").append(rb.getString("calendar.approach.interval.day"));
            months = expressions[5];
         } else if (expressions[3].equals("1")) {
            scheduleMessage.append("<br/>").append(rb.getString("calendar.week"));
            if (expressions[4].equals("1")) {
               scheduleMessage.append(" ").append(rb.getString("calendar.which.week.first")).append(" ").append(rb.getString("calendar.approach.interval.week")).append(",");
            } else if (expressions[4].equals("2")) {
               scheduleMessage.append(" ").append(rb.getString("calendar.which.week.second")).append(" ").append(rb.getString("calendar.approach.interval.week")).append(",");
            } else if (expressions[4].equals("3")) {
               scheduleMessage.append(" ").append(rb.getString("calendar.which.week.third")).append(" ").append(rb.getString("calendar.approach.interval.week")).append(",");
            } else if (expressions[4].equals("4")) {
               scheduleMessage.append(" ").append(rb.getString("calendar.which.week.forth")).append(" ").append(rb.getString("calendar.approach.interval.week")).append(",");
            } else if (expressions[4].equals("5")) {
               scheduleMessage.append(" ").append(rb.getString("calendar.which.week.last")).append(" ").append(rb.getString("calendar.approach.interval.week")).append(",");
            }

            if (expressions[5].equals("1")) {
               scheduleMessage.append(" ").append(rb.getString("calendar.week.monday")).append(".");
            } else if (expressions[5].equals("2")) {
               scheduleMessage.append(" ").append(rb.getString("calendar.week.tuesday")).append(".");
            } else if (expressions[5].equals("3")) {
               scheduleMessage.append(" ").append(rb.getString("calendar.week.wednesday")).append(".");
            } else if (expressions[5].equals("4")) {
               scheduleMessage.append(" ").append(rb.getString("calendar.week.thursday")).append(".");
            } else if (expressions[5].equals("5")) {
               scheduleMessage.append(" ").append(rb.getString("calendar.week.friday")).append(".");
            } else if (expressions[5].equals("6")) {
               scheduleMessage.append(" ").append(rb.getString("calendar.week.saturday")).append(".");
            } else if (expressions[5].equals("7")) {
               scheduleMessage.append(" ").append(rb.getString("calendar.week.sunday")).append(".");
            }
            months = expressions[6];
         }

         String temp = "";
         for (int i = 0; i < months.length(); i++) {
            String month = months.substring(i, i + 1);
            if (month.equals("0")) {
               temp += rb.getString("calendar.month.january") + ",";
            } else if (month.equals("1")) {
               temp += rb.getString("calendar.month.february") + ",";
            } else if (month.equals("2")) {
               temp += rb.getString("calendar.month.march") + ",";
            } else if (month.equals("3")) {
               temp += rb.getString("calendar.month.april") + ",";
            } else if (month.equals("4")) {
               temp += rb.getString("calendar.month.may") + ",";
            } else if (month.equals("5")) {
               temp += rb.getString("calendar.month.june") + ",";
            } else if (month.equals("6")) {
               temp += rb.getString("calendar.month.july") + ",";
            } else if (month.equals("7")) {
               temp += rb.getString("calendar.month.august") + ",";
            } else if (month.equals("8")) {
               temp += rb.getString("calendar.month.september") + ",";
            } else if (month.equals("9")) {
               temp += rb.getString("calendar.month.october") + ",";
            } else if (month.equals("a")) {
               temp += rb.getString("calendar.month.november") + ",";
            } else if (month.equals("b")) {
               temp += rb.getString("calendar.month.december") + ",";
            }
         }
         if (StringUtils.isNotEmpty(temp)) {
            if (temp.endsWith(",")) {
               temp = temp.substring(0, temp.length() - 1);
            }
         }
         scheduleMessage.append("<br/>").append(rb.getString("calendar.month")).append(" ").append(temp);
      }
      return scheduleMessage.toString();
   }

   public static void deleteSchedule(int scheduleId) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      Node scheduleNode = cloud.getNode(scheduleId);
      scheduleNode.deleteRelations();
      scheduleNode.delete();
   }

   public static void restoreSchedule(int scheduleId) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      Node scheduleNode = cloud.getNode(scheduleId);
      Node newsletterNode = SearchUtil.findRelatedNode(scheduleNode, NEWSLETTER, POSREL);
      if (newsletterNode != null) {
         newsletterNode.setStringValue(SCHEDULE, scheduleNode.getStringValue("expression"));
         newsletterNode.setStringValue("scheduledescription", getScheduleMessageByExpression(scheduleNode.getStringValue("expression")));
         newsletterNode.commit();
      }
      scheduleNode.setLongValue("createdatetime", System.currentTimeMillis());
      scheduleNode.commit();
   }
   
   public static String getScheduleMessageByExpression(String stringValue) {
      
      return getScheduleMessageByExpression(stringValue,null);
   }

   public static void addNewsletterCreationChannel(int newsletterId ,int editionId) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      RelationManager relManager = cloud.getRelationManager(NEWSLETTER, NEWSLETTERPUBLICATION, RELATED);
      relManager.createRelation(cloud.getNode(newsletterId), cloud.getNode(editionId)).commit();
   }

   public static void getSchedulesBynewsletterId(Integer valueOf) {
      getSchedulesBynewsletterId(valueOf,null);
      
   }
}