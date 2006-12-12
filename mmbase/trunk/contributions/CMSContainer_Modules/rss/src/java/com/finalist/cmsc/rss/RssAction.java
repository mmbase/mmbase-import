package com.finalist.cmsc.rss;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.mmapps.commons.util.StringUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Field;
import org.mmbase.bridge.FieldIterator;
import org.mmbase.bridge.FieldList;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.util.Queries;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.storage.search.Constraint;
import org.mmbase.storage.search.SortOrder;
import org.mmbase.storage.search.Step;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.struts.MMBaseAction;

/** 
 * Provides an RSS feed of a channel in the repository. The request parameter <code>rubriek</code>
 * is used to define the channel. To prevent guessing of a channel this parameter does not map
 * one on one to a channel but has to be defined by a system property. Some other search properties
 * like publish date can also be defined by system properties. RSS specific values can also be defined
 * by system properties either general or feed specific.
 * 
 * For example if we need to provide an rss feed to channel Repository/sport we can define a system property
 * "rss.sport.rubriek" where sport is the name to be used in the url like "/rss.do?rubriek=sport".
 * 
 * If we want to give this RSS feed a title we can define "rss.title". If we have more than one feed we can give 
 * this feed it's own title with "rss.sport.title".
 * 
 * 
 * @author robm
 *
 */
public class RssAction extends MMBaseAction {

    private static Logger log = Logging.getLoggerInstance(RssAction.class.getName());
    
    private static final String PREFIX = "rss.";
    private static final String DEFAULT_CONTENTTYPE = "article";
    private static final String DEFAULT_CHANNEL = "";
    private static final String DEFAULT_DATE = "-1";
    private static final int  DEFAULT_MAX_NUMBER = 256;
    private static final String RESULTS = "results";
    private static final String RESULT_COUNT = "resultCount";

    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response, Cloud cloud) throws Exception {

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
        NodeManager nodeManager = cloud.getNodeManager(contentTypes);

        String parentChannel = getParentChannel(cloud, feed);
        if (StringUtil.isEmpty(parentChannel)) {
            return mapping.findForward("success");
        }
        NodeQuery query = createQuery(cloud, nodeManager, parentChannel);
        query.addSortOrder(query.getStepField(nodeManager.getField(ContentElementUtil.PUBLISHDATE_FIELD)), SortOrder.ORDER_ASCENDING);
        query.setDistinct(true);
        query.setOffset(0);
        query.setMaxNumber(getProperty(feed, "max", DEFAULT_MAX_NUMBER));
        addPublicationDateConstraint(nodeManager, query);
        addRssConstraint(nodeManager, query);
        
        log.debug("QUERY: " + query.toSql());

        // Set everyting on the request.
        request.setAttribute(RESULT_COUNT, Integer.valueOf(Queries.count(query)));
        request.setAttribute(RESULTS, cloud.getList(query));
        
        // TODO why do we need to set this explicitly again after the searchaction?
        request.setAttribute("contenttypes", contentTypes);
        return mapping.findForward("success");

    }

    /** Returns null so every user can access the rss feed without logging in */
    public String getRequiredRankStr() {
        return null;
    }
    
    /** Finds the channel in the repository mapped to <code>feed</code>.
     * Returns an empty channel if nothing is found.
     */
    private String getParentChannel(Cloud cloud, String feed) {
        String channel = getProperty(feed, "rubriek", DEFAULT_CHANNEL);
        if (!StringUtils.isEmpty(channel)) {
            Node parentNode = RepositoryUtil.getChannelFromPath(cloud, channel);
            if(parentNode != null) {
                return String.valueOf(parentNode.getNumber());
            }
        }
        return "";
    }


    /** Adds a rss property to the request to be used by the jsp view.
     */ 
    private void addRssPropertyToRequest(HttpServletRequest request, String feed, String attribute) {
        request.setAttribute(attribute, getProperty(feed, attribute, ""));
    }

    /** Finds the system property defined by <code>key</code>. 
     * If nothing is found <code>defaultValue</code> is returned.
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
            } catch (NumberFormatException ignored) {
                // ignored
            }
        }
        return defaultValue;
    }
    
    /** Finds the system property defined by <code>feed</code> and <code>key</code>.
     * If nothing is found the property defined by <code>key</code> is returned.
     * If still nothing is found the <code>defaultValue</code> is returned.
     * 
     * For example if feed is "nieuws" and key is "title": the search order would be:
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
    
    private NodeQuery createQuery(Cloud cloud, NodeManager nodeManager, String parentChannel) {
        NodeQuery query = cloud.createNodeQuery();
        Step step = query.addStep(cloud.getNodeManager(RepositoryUtil.CONTENTCHANNEL));
        query.addNode(step, cloud.getNode(parentChannel));
        Step relationStep = query.addRelationStep(nodeManager, RepositoryUtil.CONTENTREL, "DESTINATION").getNext();
        query.setNodeStep(relationStep);
        return query;
    }

    private void addPublicationDateConstraint(NodeManager nodeManager, NodeQuery query) {
        String days = getProperty("publicatiedatum", DEFAULT_DATE);
        SearchUtil.addDayConstraint(query, nodeManager, ContentElementUtil.PUBLISHDATE_FIELD, days);
    }

    private void addRssConstraint(NodeManager nodeManager, NodeQuery query) {
        // not all content elements have a use_in_rss flag
        FieldList fields = nodeManager.getFields();
        for (FieldIterator iter = fields.fieldIterator(); iter.hasNext(); ) {
            Field field = iter.nextField();
            if ("use_in_rss".equals(field.getName())) {
                Constraint constraint = query.createConstraint(query.getStepField(field), 3, Boolean.valueOf(true));
                SearchUtil.addConstraint(query, constraint);
                break;
            }
        }
    }
}
