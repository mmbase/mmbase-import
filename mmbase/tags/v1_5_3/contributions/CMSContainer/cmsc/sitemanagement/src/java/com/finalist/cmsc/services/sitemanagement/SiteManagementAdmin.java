/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.services.sitemanagement;

import com.finalist.cmsc.beans.om.*;
import com.finalist.cmsc.services.ServiceManager;

/**
 * This class is a static accessor for a <code>SiteManagementService</code>
 * implementation.
 * 
 * @author Wouter Heijke
 */
public class SiteManagementAdmin {

   private final static SiteManagementAdminService cService = (SiteManagementAdminService) ServiceManager
         .getService(SiteManagementAdminService.class);


   public static boolean setPortletParameter(String portletId, PortletParameter param) {
      return cService.setPortletParameter(portletId, param);
   }


   public static boolean setPortletNodeParameter(String portletId, PortletParameter param) {
      return cService.setPortletNodeParameter(portletId, param);
   }


   public static boolean setPortletView(String portletId, String viewId) {
      return cService.setPortletView(portletId, viewId);
   }


   public static boolean setScreenPortlet(String screenId, String portletId, String layoutId) {
      return cService.setPagePortlet(screenId, portletId, layoutId);
   }


   public static boolean createScreenPortlet(String screenId, String portletName, String definitionName,
         String layoutId, String viewId) {
      return cService.createPagePortlet(screenId, portletName, definitionName, layoutId, viewId);
   }


   public static void deleteScreenPortlet(Page page, Portlet portlet, String layoutId) {
      if (page != null && portlet != null) {
         cService.deletePagePortlet(page, portlet, layoutId);
      }
   }


   public static boolean mayEdit(NavigationItem item) {
      return cService.mayEdit(item);
   }


   public static boolean mayEdit(Portlet portlet) {
      return cService.mayEdit(portlet);
   }

}
