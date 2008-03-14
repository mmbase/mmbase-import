/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.services.sitemanagement;

import com.finalist.cmsc.beans.om.*;
import com.finalist.cmsc.services.Service;

/**
 * <P>
 * The <CODE>SiteManagementAdminService</CODE> interface can be used to modify
 * the site and all children
 */
public abstract class SiteManagementAdminService extends Service {

   public abstract boolean setPortletParameter(String portletId, PortletParameter param);


   public abstract boolean setPortletNodeParameter(String portletId, PortletParameter param);


   public abstract boolean setPortletView(String portletId, String viewId);


   public abstract boolean setPagePortlet(String pageId, String portletId, String id);


   public abstract boolean createPagePortlet(String pageId, String portletName, String definitionName, String id,
         String viewId);


   public abstract void deletePagePortlet(Page page, Portlet portlet, String layoutId);


   public abstract boolean mayEdit(NavigationItem item);


   public abstract boolean mayEdit(Portlet portlet);

}