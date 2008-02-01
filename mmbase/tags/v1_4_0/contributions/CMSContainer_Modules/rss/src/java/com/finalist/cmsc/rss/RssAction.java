package com.finalist.cmsc.rss;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.navigation.ServerUtil;
import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.struts.MMBaseAction;

/**
 * Provides an RSS feed of a channel in the repository. The request parameter
 * <code>rubriek</code> is used to define the channel. To prevent guessing of
 * a channel this parameter does not map one on one to a channel but has to be
 * defined by a system property. Some other search properties like publish date
 * can also be defined by system properties. RSS specific values can also be
 * defined by system properties either general or feed specific. For example if
 * we need to provide an rss feed to channel Repository/sport we can define a
 * system property "rss.sport.rubriek" where sport is the name to be used in the
 * url like "/rss.do?rubriek=sport". If we want to give this RSS feed a title we
 * can define "rss.title". If we have more than one feed we can give this feed
 * it's own title with "rss.sport.title".
 * 
 * @author robm
 */
public class RssAction extends MMBaseAction {

   private static Logger log = Logging.getLoggerInstance(RssAction.class.getName());

   private static final String PREFIX = "rss.";
   private static final String DEFAULT_CONTENTTYPE = "article";
   private static final String DEFAULT_CHANNEL = "";
   private static final String DEFAULT_DATE = "-1";
   private static final int DEFAULT_MAX_NUMBER = 256;
   private static final String RESULTS = "results";
   private static final String RESULT_COUNT = "resultCount";


   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {

      log.debug("Starting rss feed");

      String feed = request.getParameter("rubriek");

      addRssPropertyToRequest(request, feed, "title");
      addRssPropertyToRequest(request, feed, "link");
      addRssPropertyToRequest(request, feed, "language");
      addRssPropertyToRequest(request, feed, "description");
      addRssPropertyToRequest(request, feed, "copyright");
      addRssPropertyToRequest(request, feed, "managingEditor");
      addRssPropertyToRequest(request, feed, "webMaster");

      String contentTypes = getProperty(feed, "contenttypes", DEFAULT_CONTENTTYPE);
      List<String> contentTypesList = new ArrayList<String>();

      StringTokenizer tokenizer = new StringTokenizer(contentTypes, ", \t\n\r\f");
      while (tokenizer.hasMoreTokens()) {
         String type = tokenizer.nextToken();
         contentTypesList.add(type);
      }

      Node parentChannel = getParentChannel(cloud, feed);
      if (parentChannel == null) {
         return mapping.findForward("success");
      }

      int maxNumber = getProperty(feed, "max", DEFAULT_MAX_NUMBER);

      boolean useLifecycle = true;
      if (useLifecycle && ServerUtil.isLive()) {
         // A live server will remove expired nodes.
         useLifecycle = false;
      }

      NodeQuery query = RepositoryUtil.createLinkedContentQuery(parentChannel, contentTypesList,
            ContentElementUtil.PUBLISHDATE_FIELD, "down", useLifecycle, null, 0, maxNumber, -1, -1, -1);
      NodeManager nodeManager = query.getNodeManager();

      addPublicationDateConstraint(nodeManager, query);

      NodeList results = query.getNodeManager().getList(query);
      // Remove all contentelements which do not want to be shown in rss.
      removeRssNodes(results);

      // Set everyting on the request.
      request.setAttribute(RESULT_COUNT, Integer.valueOf(Queries.count(query)));
      request.setAttribute(RESULTS, results);

      return mapping.findForward("success");

   }


   /** Returns null so every user can access the rss feed without logging in */
   public String getRequiredRankStr() {
      return null;
   }


   /**
    * Finds the channel in the repository mapped to <code>feed</code>.
    * Returns an empty channel if nothing is found.
    */
   private Node getParentChannel(Cloud cloud, String feed) {
      String channel = getProperty(feed, "rubriek", DEFAULT_CHANNEL);
      if (!StringUtils.isEmpty(channel)) {
         Node parentNode = RepositoryUtil.getChannelFromPath(cloud, channel);
         if (parentNode != null) {
            return parentNode;
         }
      }
      return null;
   }


   /**
    * Adds a rss property to the request to be used by the jsp view.
    */
   private void addRssPropertyToRequest(HttpServletRequest request, String feed, String attribute) {
      request.setAttribute(attribute, getProperty(feed, attribute, ""));
   }


   /**
    * Finds the system property defined by <code>key</code>. If nothing is
    * found <code>defaultValue</code> is returned.
    */
   private String getProperty(String key, String defaultValue) {
      String value = PropertiesUtil.getProperty(PREFIX + key);
      if (StringUtils.isEmpty(value)) {
         return defaultValue;
      }
      return value;
   }


   private int getProperty(String feed, String key, int defaultValue) {
      String value = getProperty(feed, key, "");
      if (!StringUtils.isEmpty(value)) {
         try {
            return Integer.parseInt(value);
         }
         catch (NumberFormatException ignored) {
            // ignored
         }
      }
      return defaultValue;
   }


   /**
    * Finds the system property defined by <code>feed</code> and
    * <code>key</code>. If nothing is found the property defined by
    * <code>key</code> is returned. If still nothing is found the
    * <code>defaultValue</code> is returned. For example if feed is "nieuws"
    * and key is "title": the search order would be:
    * <ol>
    * <li>rss.nieuws.title</li>
    * <li>rss.title</li>
    * <li>defaultValue</li>
    * </ol>
    */
   private String getProperty(String feed, String key, String defaultValue) {
      String value = getProperty(feed + "." + key, "");
      if (StringUtils.isEmpty(value)) {
         value = getProperty(key, "");
      }
      if (StringUtils.isEmpty(value)) {
         value = defaultValue;
      }
      return value;
   }


   private void addPublicationDateConstraint(NodeManager nodeManager, NodeQuery query) {
      String days = getProperty("publicatiedatum", DEFAULT_DATE);
      SearchUtil.addDayConstraint(query, nodeManager, ContentElementUtil.PUBLISHDATE_FIELD, days);
   }


   private void removeRssNodes(NodeList results2) {
      for (Iterator<Node> iterator = results2.iterator(); iterator.hasNext();) {
         Node node = iterator.next();
         if (node.getNodeManager().hasField("use_in_rss") && !node.getBooleanValue("use_in_rss")) {
            iterator.remove();
         }
      }
   }
}
