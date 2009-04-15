/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.services.sitemanagement;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;

import net.sf.mmapps.commons.bridge.CloudUtil;
import org.apache.commons.lang.StringUtils;
import net.sf.mmapps.modules.cloudprovider.CloudProvider;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.beans.om.*;
import com.finalist.cmsc.navigation.*;
import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.security.UserRole;
import com.finalist.cmsc.services.Properties;

/**
 * MMBase specific PortalLayoutService implementation, in this case MMBase
 * manages the Pages/Layout and Portlets
 * 
 * @author Wouter Heijke
 */
public class SiteManagementAdminServiceMMBaseImpl extends SiteManagementAdminService {
   private static Log log = LogFactory.getLog(SiteManagementAdminServiceMMBaseImpl.class);

   private CloudProvider cloudProvider;
   private SiteModelManager siteModelManager;


   @Override
   public void init(ServletConfig config, Properties aProperties) throws Exception {
      this.cloudProvider = CloudProviderFactory.getCloudProvider();
      log.info("SiteManagementService STARTED");

      siteModelManager = new SiteModelManager();
   }


   @Override
   public boolean setPortletParameter(String portletId, PortletParameter param) {
      boolean result = false;

      if (param != null) {
         String key = param.getKey();
         List<String> values = param.getValues();
         Cloud cloud = getUserCloud();

         PortletUtil.updatePortletParameter(cloud, portletId, key, values);
         updatePageForPortlet(portletId);

         siteModelManager.clearPortlet(portletId);
      }

      log.debug("++++ Param for portlet:'" + portletId + "'");
      return result;
   }


   @Override
   public boolean setPortletNodeParameter(String portletId, PortletParameter param) {
      boolean result = false;

      if (param != null) {
         String key = param.getKey();
         List<String> values = param.getValues();
         Cloud cloud = getUserCloud();
         List<Node> nodeList = new ArrayList<Node>();

         Node node = null;
         if (!values.isEmpty()) {
            for (String value : values) {
               if (StringUtils.isNotBlank(value)) {
                  node = cloud.getNode(value);
                  nodeList.add(node);
               }
            }
         }
         PortletUtil.updateNodeParameter(cloud, portletId, key, nodeList);
         updatePageForPortlet(portletId);

         siteModelManager.clearPortlet(portletId);
      }

      log.debug("++++ Param for portlet:'" + portletId + "'");
      return result;
   }


   @Override
   public boolean setPortletView(String portletId, String viewId) {
      boolean result = true;
      log.debug("setPortletView portlet='" + portletId + "' view='" + viewId + "'");
      try {
         Cloud cloud = getUserCloud();
         PortletUtil.updatePortletView(cloud, portletId, viewId);
         updatePageForPortlet(portletId);

         siteModelManager.clearPortlet(portletId);
      }
      catch (Exception e) {
         log.error("something went wrong while setting view (" + viewId + ") for portlet (" + portletId + ")");
         if (log.isDebugEnabled()) {
            log.debug(e);
         }
         result = false;
      }

      return result;
   }


   @Override
   public boolean setPagePortlet(String pageId, String portletId, String id) {
      boolean result = true;
      log.debug("page:'" + pageId + "' portlet:'" + portletId + "'");
      try {
         Cloud cloud = getUserCloud();
         PortletUtil.setPagePortlet(cloud, pageId, portletId, id);
         updatePage(pageId);

         siteModelManager.clearItem(pageId);
      }
      catch (Exception e) {
         log.error("something went wrong while adding portlet (" + portletId + ")", e);
         result = false;
      }
      return result;
   }


   @Override
   public boolean createPagePortlet(String pageId, String portletName, String definitionName, String layoutId,
         String viewId) {
      boolean result = true;
      log.debug("page:'" + pageId + "' portlet:'" + portletName + "' definition:'" + definitionName + "'");
      try {
         Cloud cloud = getUserCloud();
         Node newNode = PortletUtil.createPortlet(cloud, portletName, definitionName, viewId);
         PortletUtil.setPagePortlet(cloud, pageId, newNode, layoutId);
         updatePage(pageId);

         siteModelManager.clearItem(pageId);
      }
      catch (Exception e) {
         log.error("something went wrong while creating portlet (" + portletName + ")", e);
         result = false;
      }

      return result;
   }


   @Override
   public void deletePagePortlet(Page page, Portlet portlet, String layoutId) {
      if (page != null && portlet != null) {
         PortletUtil.deletePagePortlet(getUserCloud(), page.getId(), portlet.getId(), layoutId);
         updatePage(page.getId());

         siteModelManager.clearItem(page.getId());
      }
   }


   @Override
   public boolean mayEdit(NavigationItem item) {
      boolean result = false;
      try {
         Cloud cloud = getUserCloud();
         if (cloud != null) {
             UserRole role = NavigationUtil.getRole(cloud, item.getId());
             result = role != null && SecurityUtil.isWriter(role);
         }
      }
      catch (Exception e) {
         log.error("something went wrong checking page edit (" + item.getId() + ")");
         if (log.isDebugEnabled()) {
            log.debug(e);
         }
      }
      return result;
   }


   @Override
   public boolean mayEdit(Portlet portlet) {
      boolean result = false;
      try {
         Cloud cloud = getUserCloud();
         if (cloud != null) {
             PortletDefinition definition = siteModelManager.getPortletDefinition(portlet.getDefinition());
             if (definition.isSingle()) {
                result = cloud.getUser().getRank().getInt() >= definition.getRank();
             }
             else {
                result = true;
             }
         }
      }
      catch (Exception e) {
         log.error("something went wrong checking portlet edit (" + portlet.getId() + ")");
         if (log.isDebugEnabled()) {
            log.debug(e);
         }
      }
      return result;
   }

   protected void updatePageForPortlet(String portletId) {
      Cloud cloud = getUserCloud();
      Node portlet = cloud.getNode(portletId);
      Node page = PagesUtil.getPage(portlet);
      if (page != null) {
         updatePage(page);
      }
   }


   protected void updatePage(String pageId) {
      updatePage(Integer.parseInt(pageId));
   }


   protected void updatePage(int pageId) {
      Cloud cloud = getUserCloud();
      Node page = cloud.getNode(pageId);
      updatePage(page);
   }


   protected void updatePage(Node page) {
      // trigger system field processing like dates
      page.commit();
   }


   protected Cloud getUserCloud() {
      Cloud cloud = CloudUtil.getCloudFromThread();
      if (cloud == null) {
         log.warn("User cloud not found in thread; make sure that the user cloud is bound");
         cloud = cloudProvider.getAdminCloud();
      }
      return cloud;
   }

}
