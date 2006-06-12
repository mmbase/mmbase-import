/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.portalImpl.services.sitemanagement;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.finalist.cmsc.beans.om.*;
import com.finalist.cmsc.portalImpl.security.LoginSession;
import com.finalist.pluto.portalImpl.aggregation.ScreenFragment;
import com.finalist.pluto.portalImpl.services.Service;

/**
 * <P>
 * The <CODE>SiteManagementService</CODE> interface represents a complete site
 * with all screens, windows and portlet entries on it. It is accessed by the
 * datastore layer to get information about the site.
 * 
 * @author Wouter Heijke
 * @version $Revision: 1.1 $
 */
public abstract class SiteManagementService extends Service {

	public abstract boolean isNavigation(String path);

	/**
	 * Returns a screen from the content repository
	 * 
	 * @param name of path that leads to this screen, usually a Page
	 * @return ScreenFragment
	 */
	public abstract ScreenFragment getScreen(String name);

	/**
	 * Returns a LoginSession object from the content repository
	 * 
	 * @param request
	 * @return LoginSession
	 */
	public abstract LoginSession getLoginSession(HttpServletRequest request);

	/**
	 * Sets a parameter in a Portlet
	 * 
	 * @param portletId
	 * @param param
	 * @return boolean
	 */
	public abstract boolean setPortletParameter(String portletId, PortletParameter param);

    public abstract boolean setPortletNodeParameter(String portletId, PortletParameter param);
    
	public abstract boolean setPortletView(String portletId, String viewId);

	public abstract boolean setPagePortlet(String screenId, String portletId, String id);

	public abstract boolean createPagePortlet(String screenId, String portletName, String definitionName, String id, String viewId);

	public abstract List<Site> getSites();
	
	public abstract List<Page> getPages(Page page);

	public abstract List<Page> getPages(Site site);

	public abstract void deletePagePortlet(Page page, Portlet portlet, String layoutId);
	
	public abstract Page getPage(int id);

	public abstract Page getPageFromPath(String path);

	public abstract Site getSiteFromPath(String path);

	public abstract String getPageLink(Page page, boolean includeRoot);

    public abstract List getStylesheetForPageByPath(String page);     
        
	public abstract List<Page> getListFromPath(String path);

    public abstract List<View> getViews(String screenId, String layoutId);

    public abstract List<View> getViews(String definitionId);
    
    public abstract List<View> getViews(PortletDefinition definition);

    public abstract List<PortletDefinition> getSingletonPortlets(String screenId, String layoutId);

    public abstract List<PortletDefinition> getPortletDefintions(String screenId, String layoutId);

    public abstract boolean mayEdit(Page page);

    public abstract boolean mayEdit(Portlet portlet);

    public abstract List<String> getContentTypes(String portletId);

}
