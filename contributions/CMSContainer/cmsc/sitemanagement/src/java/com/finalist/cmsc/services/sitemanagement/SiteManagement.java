/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.services.sitemanagement;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.finalist.cmsc.beans.om.*;
import com.finalist.cmsc.services.ServiceManager;
import com.finalist.cmsc.services.security.LoginSession;

/**
 * This class is a static accessor for a <code>SiteManagementService</code>
 * implementation.
 * 
 * @author Wouter Heijke
 */
public class SiteManagement {
   private static Log log = LogFactory.getLog(SiteManagement.class);

   private final static SiteManagementService cService = (SiteManagementService) ServiceManager
         .getService(SiteManagementService.class);


   public static boolean isNavigation(String path) {
      if (cService == null) {
         log.info("SiteManagementService not started");
         return false;
      }
      return cService.isNavigation(path);
   }


   public static LoginSession getLoginSession(HttpServletRequest request) {
      if (cService == null) {
         log.info("SiteManagementService not started");
         return null;
      }
      return cService.getLoginSession(request);
   }


   public static List<Site> getSites() {
      if (cService == null) {
         log.info("SiteManagementService not started");
         return new ArrayList<Site>();
      }
      return cService.getSites();
   }

   public static <E extends NavigationItem> List<E> getNavigationItems(NavigationItem parent, Class<E> childClazz) {
       return cService.getNavigationItems(parent, childClazz);
   }
   
   public static List<NavigationItem> getNavigationItems(NavigationItem item) {
       return cService.getNavigationItems(item);
   }

   public static List<Page> getPages(Page page) {
       return cService.getPages(page);
   }


   public static NavigationItem getNavigationItem(int id) {
      return cService.getNavigationItem(id);
   }


   public static NavigationItem getNavigationItemFromPath(String path) {
      return cService.getNavigationItemFromPath(path);
   }


   public static List<Stylesheet> getStylesheetForPageByPath(String path, boolean override) {
      return cService.getStylesheetForPageByPath(path, override);
   }


   public static Site getSiteFromPath(String path) {
      return cService.getSiteFromPath(path);
   }


   public static String getPath(NavigationItem item, boolean includeRoot) {
      return cService.getPath(item, includeRoot);
   }


   public static String getPath(int itemId, boolean includeRoot) {
      return cService.getPath(itemId, includeRoot);
   }

   public static List<Page> getPagesFromPath(String path) {
       return cService.getPagesFromPath(path);
   }
   
   public static List<NavigationItem> getListFromPath(String path) {
      return cService.getListFromPath(path);
   }


   public static List<View> getViews(String pageId, String layoutId) {
      return cService.getViews(pageId, layoutId);
   }


   public static List<View> getViews(String definitionId) {
      return cService.getViews(definitionId);
   }


   public static List<View> getViews(PortletDefinition definition) {
      return cService.getViews(definition);
   }


   public static List<PortletDefinition> getSingletonPortlets(String pageId, String layoutId) {
      return cService.getSingletonPortlets(pageId, layoutId);
   }


   public static List<PortletDefinition> getPortletDefintions(String pageId, String layoutId) {
      return cService.getPortletDefintions(pageId, layoutId);
   }


   public static List<String> getContentTypes(String portletId) {
      return cService.getContentTypes(portletId);
   }


   public static Set<String> getPagePositions(String pageId) {
      return cService.getPagePositions(pageId);
   }


   public static List<Integer> getPageImagesForPage(String name, String path) {
      return cService.getPageImagesForPath(name, path);
   }


   public static Layout getLayout(int layout) {
      return cService.getLayout(layout);
   }


   public static Portlet getPortlet(Integer portletId) {
      return cService.getPortlet(portletId);
   }


   public static PortletDefinition getPortletDefinition(int definition) {
      return cService.getPortletDefinition(definition);
   }


   public static View getView(int view) {
      return cService.getView(view);
   }


   public static String getSite(NavigationItem item) {
      return cService.getSite(item);
   }


   public static void resetSiteCache() {
      cService.resetSiteCache();
   }

   public static NavigationItem convertToNavigationItem(Object dest) {
        NavigationItem item = null;
        if (dest instanceof NavigationItem) {
            item = (NavigationItem) dest;
        }
        else
            if (dest instanceof Integer) {
                item = convertToNavigationItemInteger((Integer) dest);
            }
            else
                if (dest instanceof String) {
                    item = convertToNavigationItemString((String) dest);
                }
                else {
                    throw new IllegalArgumentException(
                            "only NavigationItem, integer or string allowed: " + dest.getClass());
                }
        return item;
    }

    /**
     * Set destination node number to navigate to.
     * 
     * @param n the node number
     */
    public static NavigationItem convertToNavigationItemInteger(Integer n) {
        return SiteManagement.getNavigationItem(n.intValue());
    }

    /**
     * Set the destination node path to navigate to.
     * 
     * @param s comma, slash or space separated list of node numbers and/or aliases
     */
    public static NavigationItem convertToNavigationItemString(String s) {
        NavigationItem temp = null;
        if (StringUtils.isNotBlank(s)) {
            if (StringUtils.isNumeric(s)) {
                temp = SiteManagement.getNavigationItem(Integer.parseInt(s));
            }
            else {
                temp = SiteManagement.getNavigationItemFromPath(s);
            }
        }
        return temp;
    }

}
