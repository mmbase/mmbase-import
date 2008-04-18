/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.portlets.banner;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import net.sf.mmapps.modules.cloudprovider.CloudProvider;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Field;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.Relation;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.storage.search.CompositeConstraint;
import org.mmbase.storage.search.Constraint;
import org.mmbase.storage.search.FieldValueConstraint;

import com.finalist.cmsc.beans.om.ContentElement;
import com.finalist.cmsc.portalImpl.PortalConstants;
import com.finalist.cmsc.portlets.ContentChannelPortlet;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;

public class BannerPortlet extends ContentChannelPortlet {


   private static final Log log = LogFactory.getLog(BannerPortlet.class);

   protected static final String PARAM_REDIRECT = "redirect";
   protected static final String BANNER = "banner";

   @Override
   public void init(PortletConfig config) throws PortletException {
      super.init(config);
   }


   @Override
   protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
      String position = (String) request.getAttribute(PortalConstants.CMSC_OM_PORTLET_LAYOUTID);
      request.setAttribute("bannerPosition", position);
      super.doView(request, response);
   }


   @Override
   protected void addContentElements(RenderRequest request, String channel) {
      // get the contentelements from the channel
      super.addContentElements(request, channel);
      handleBannerCounters(request);
   }

   protected void handleBannerCounters(RenderRequest request) {
      // remove all banners from the content elemens that have reached max clicks (
      String screenId = (String) request.getAttribute(PortalConstants.CMSC_OM_PAGE_ID);
      String page = SiteManagement.getPath(Integer.valueOf(screenId), true);
      String position = (String) request.getAttribute(PortalConstants.CMSC_OM_PORTLET_LAYOUTID);

      CloudProvider cloudProvider = CloudProviderFactory.getCloudProvider();
      Cloud cloud = cloudProvider.getCloud();
      // get the node list from the request
      List<ContentElement> elements = (List<ContentElement>) request.getAttribute(ELEMENTS);
      if (elements != null) {
         Date now = new Date();
         for (ListIterator<ContentElement> iter = elements.listIterator(); iter.hasNext();) {
            ContentElement element = iter.next();
            if (BANNER.equals(element.getNodeType())) {
                Node banner = cloud.getNode(element.getId());
                if (banner != null) {
                   log.debug("Checking banner for maxclicks: " + banner.getNumber());
                   Node counter = findCounterNode(banner, page, position);
                   if (counter != null) {
                      if (banner.getBooleanValue("use_maxclicks")
                              && (counter.getIntValue("clicks") >= banner.getIntValue("maxclicks"))) {
                         log.debug("Maximum number of clicks reached for banner: " + banner.getNumber() + ", skipping it");
                         iter.remove();
                      }
                   } else {
                      counter = createBannerCounter(cloud, banner, page, position);
                      counter.setDateValue("enddate", now);
                      counter.commit();
                   }
                } else {
                   log.debug("No banner found for id: " + element.getId());
                }
            }
         }
      }
   }


   @Override
   public void processView(ActionRequest request, ActionResponse response) {

      String position = (String) request.getAttribute(PortalConstants.CMSC_OM_PORTLET_LAYOUTID);
      String screenId = (String) request.getAttribute(PortalConstants.CMSC_OM_PAGE_ID);
      String page = SiteManagement.getPath(Integer.valueOf(screenId), true);

      CloudProvider cloudProvider = CloudProviderFactory.getCloudProvider();
      Cloud cloud = cloudProvider.getCloud();

      String bannerId = request.getParameter("elementId");
      if (!StringUtils.isBlank(bannerId)) {
         try {
            Node banner = cloud.getNode(bannerId);
            Node counter = findCounterNode(banner, page, position);
            if (counter == null) {
               counter = createBannerCounter(cloud, banner, page, position);
               getLogger().debug("Could not find counter for banner: " + bannerId + ", created a new one");
            }
            int clicks = counter.getIntValue("clicks") + 1;
            Date now = new Date();
            counter.setIntValue("clicks", clicks);
            counter.setDateValue("enddate", now);

            counter.commit();
            getLogger().debug("Clicks updated to: " + clicks + " for banner: " + bannerId);

            String redirectUrl = request.getParameter(PARAM_REDIRECT);
            if (!StringUtils.isBlank(redirectUrl)) {
               getLogger().debug("Redirecting request for banner: " + bannerId + " to: " + redirectUrl);
               if (redirectUrl.indexOf("?") > -1) {
                  redirectUrl += "&";
               } else {
                  redirectUrl += "?";
               }

               redirectUrl += "elementId=" + bannerId + "&page=" + screenId + "&position=" + position;
               response.sendRedirect(redirectUrl);
            } else {
               Node url = SearchUtil.findRelatedNode(banner, "urls", "posrel");
               response.sendRedirect(url.getStringValue("url"));
            }
         }
         catch (Exception ex) {
            getLogger().error("Unable to update clicks on banner: " + bannerId, ex);
            // continue with redirect in any case
         }
      } else {
         log.warn("No elementId found, check banners for page: " + page + " and position: " + position);
      }
   }


   private Node createBannerCounter(Cloud cloud, Node banner, String page, String position) {
      Node counter = cloud.getNodeManager("bannercounter").createNode();
      counter.setValue("page", page);
      counter.setValue("position", position);
      counter.setIntValue("clicks", 0);
      counter.commit();
      Relation posrel = banner.createRelation(counter, cloud.getRelationManager("posrel"));
      posrel.commit();
      return counter;
   }


   private Node findCounterNode(Node node, String page, String position) {
      log.debug("FindCounterNode for node: " + node.getNumber() + ", page: " + page + ", position: " + position);
      if (page == null || position == null) {
         return null;
      }

      // find a counter with the highest number and that's probably the one we
      // want
      NodeQuery query = SearchUtil.createRelatedNodeListQuery(node, "bannercounter", "posrel", 
                                                              null, null, "number", "down");
      addConstraint(query, query.getNodeManager(), "page", page);
      addConstraint(query, query.getNodeManager(), "position", position);
      query.setMaxNumber(1);
      NodeList counters = query.getList();
      log.debug("Found counters: " + counters);
      if (counters.size() == 0) {
         return null;
      }
      return counters.getNode(0);
   }


   private void addConstraint(NodeQuery query, NodeManager manager, String fieldName, String value) {
      Field field = manager.getField(fieldName);
      FieldValueConstraint constraint = query.createConstraint(query.getStepField(field), 3, value);

      addConstraint(query, constraint);
      query.setCaseSensitive(constraint, false);
   }


   private void addConstraint(NodeQuery query, Constraint constraint) {
      if (query.getConstraint() == null) {
         query.setConstraint(constraint);
      } else {
         CompositeConstraint newc = query.createConstraint(query.getConstraint(), 2, constraint);
         query.setConstraint(newc);
      }
   }
}