/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.services.sitemanagement;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

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
	
	public static List<Page> getPages(Page page) {
		return cService.getPages(page);
	}

	public static List<Page> getPages(Site site) {
		return cService.getPages(site);
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
	
	public static String getPath(Page page, boolean includeRoot) {
		return cService.getPath(page, includeRoot);
	}

    public static String getPath(int pageid, boolean includeRoot) {
        return cService.getPath(pageid, includeRoot);
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

    public static List<String> getContentTypes(String portletId) {
        return cService.getContentTypes(portletId);
    }

    public static Set<String> getPagePositions(String pageId) {
        return cService.getPagePositions(pageId);
    }

	public static String getPageImageForPage(String name, String path) {
		return cService.getPageImageForPath(name, path);
	}

    public static Layout getLayout(int layout) {
        return cService.getLayout(layout);
    }

    public static Portlet getPortlet(Integer portletId){
        return cService.getPortlet(portletId);
    }

    public static PortletDefinition getPortletDefinition(int definition) {
        return cService.getPortletDefinition(definition);
    }

    public static View getView(int view) {
        return cService.getView(view);
    }

}
