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
import com.finalist.pluto.portalImpl.aggregation.PortletFragment;
import com.finalist.pluto.portalImpl.aggregation.ScreenFragment;
import com.finalist.pluto.portalImpl.services.ServiceManager;

/**
 * This class is a static accessor for a <code>SiteManagementService</code>
 * implementation.
 * 
 * @author Wouter Heijke
 * @version $Revision: 1.1 $
 */
public class SiteManagement {
	private final static SiteManagementService cService = (SiteManagementService) ServiceManager
			.getService(SiteManagementService.class);

	public static boolean isNavigation(String path) {
		return cService.isNavigation(path);
	}

	public static ScreenFragment getScreen(String name) {
		return cService.getScreen(name);
	}

	public static LoginSession getLoginSession(HttpServletRequest request) {
		return cService.getLoginSession(request);
	}

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

	public static boolean createScreenPortlet(String screenId, String portletName, String definitionName, String layoutId, String viewId) {
		return cService.createPagePortlet(screenId, portletName, definitionName, layoutId, viewId);
	}

	public static List<Site> getSites() {
		return cService.getSites();
	}
	
	public static List<Page> getPages(Page page) {
		return cService.getPages(page);
	}

	public static List<Page> getPages(Site site) {
		return cService.getPages(site);
	}

	public static void deleteScreenPortlet(ScreenFragment screenFragment, PortletFragment portletFragment) {
        if (screenFragment != null && portletFragment != null) {
            Page page = screenFragment.getPage();
            Portlet portlet = portletFragment.getPortlet();
            String layoutId = portletFragment.getLayoutId();
            if (page != null && portlet != null) {
                cService.deletePagePortlet(page, portlet, layoutId);
            }
        }
	}
	
	public static Page getPage(int pageId) {
		return cService.getPage(pageId);
	}

	public static Page getPageFromPath(String path) {
		return cService.getPageFromPath(path);
	}

    public static List getStylesheetForPageByPath(String path) {
		return cService.getStylesheetForPageByPath(path);
	}
	public static Site getSiteFromPath(String path) {
		return cService.getSiteFromPath(path);
	}
	
	public static String getPageLink(Page page, boolean includeRoot) {
		return cService.getPageLink(page, includeRoot);
	}

	public static List<Page> getListFromPath(String path) {
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

    public static boolean mayEdit(Page page) {
        return cService.mayEdit(page);
    }

    public static boolean mayEdit(Portlet portlet) {
        return cService.mayEdit(portlet);
    }

    public static List<String> getContentTypes(String portletId) {
        return cService.getContentTypes(portletId);
    }

}
