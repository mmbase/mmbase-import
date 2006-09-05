/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.services.sitemanagement;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.finalist.cmsc.beans.om.*;
import com.finalist.cmsc.services.Service;
import com.finalist.cmsc.services.security.LoginSession;

/**
 * <P>
 * The <CODE>SiteManagementService</CODE> interface represents a complete site
 * with all screens, windows and portlet entries on it. It is accessed by the
 * datastore layer to get information about the site.
 * 
 * @author Wouter Heijke
 */
public abstract class SiteManagementService extends Service {

	public abstract boolean isNavigation(String path);

	/**
	 * Returns a LoginSession object from the content repository
	 * 
	 * @param request
	 * @return LoginSession
	 */
	public abstract LoginSession getLoginSession(HttpServletRequest request);

	public abstract List<Site> getSites();
	
	public abstract List<Page> getPages(Page page);

	public abstract List<Page> getPages(Site site);
	
	public abstract Page getPage(int id);

	public abstract Page getPageFromPath(String path);

	public abstract Site getSiteFromPath(String path);

	public abstract String getPath(Page page, boolean includeRoot);

    public abstract String getPath(int pageid, boolean includeRoot);
    
    public abstract List getStylesheetForPageByPath(String page);     
        
	public abstract List<Page> getListFromPath(String path);

    public abstract List<View> getViews(String screenId, String layoutId);

    public abstract List<View> getViews(String definitionId);
    
    public abstract List<View> getViews(PortletDefinition definition);

    public abstract List<PortletDefinition> getSingletonPortlets(String screenId, String layoutId);

    public abstract List<PortletDefinition> getPortletDefintions(String screenId, String layoutId);

    public abstract List<String> getContentTypes(String portletId);

    public abstract Set<String> getPagePositions(String pageId);

	public abstract String getPageImageForPath(String name, String path);

    public abstract Layout getLayout(int layout);

    public abstract Portlet getPortlet(int portletId);

    public abstract PortletDefinition getPortletDefinition(int definition);

    public abstract View getView(int view);
}
