/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.portalImpl.services.sitemanagement;

import java.util.*;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;

import net.sf.mmapps.commons.bridge.CloudUtil;
import net.sf.mmapps.modules.cloudprovider.CloudProvider;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.*;
import org.mmbase.module.Module;
import org.mmbase.module.tools.MMAdmin;
import org.mmbase.security.Rank;
import org.mmbase.security.UserContext;

import com.finalist.cmsc.beans.om.*;
import com.finalist.cmsc.navigation.*;
import com.finalist.cmsc.portalImpl.security.LoginSession;
import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.security.UserRole;
import com.finalist.pluto.portalImpl.aggregation.*;
import com.finalist.pluto.portalImpl.util.Properties;

/**
 * MMBase specific PortalLayoutService implementation, in this case MMBase
 * manages the Screens/Layout and Portlets
 * 
 * @author Wouter Heijke
 * @version $Revision: 1.1 $
 */
public class SiteManagementServiceMMBaseImpl extends SiteManagementService {
	private static Log log = LogFactory.getLog(SiteManagementServiceMMBaseImpl.class);

	private CloudProvider cloudProvider;
	private ServletConfig servletConfig;
    private SiteModelManager siteModelManager;

	/**
	 * @see net.sf.mmapps.commons.portalImpl.services.Service#init(javax.servlet.ServletConfig, net.sf.mmapps.commons.portalImpl.util.Properties)
	 */
	public void init(ServletConfig config, Properties aProperties) throws Exception {
		this.servletConfig = config;
		this.cloudProvider = CloudProviderFactory.getCloudProvider();
		log.info("SiteManagementService STARTED");
        
        waitForMMBase();
        
        siteModelManager = new SiteModelManager();
	}

	private void waitForMMBase() {
        MMAdmin mmadmin = (MMAdmin) Module.getModule("mmadmin", true);
        
        try {
            while (! mmadmin.getState()) {
                // not started, sleep some time
                Thread.sleep(1000L);
            }
        } catch (InterruptedException e) {
            log.debug(e.getMessage(), e);
        }
    }

    /**
	 * @see net.sf.mmapps.commons.portalImpl.services.sitemanagement.SiteManagementService#isNavigation(java.lang.String)
	 */
	public boolean isNavigation(String path) {
		log.debug("isNavigation:'" + path + "'");
        return siteModelManager.hasPage(path); 
	}

	/**
	 * @see net.sf.mmapps.commons.portalImpl.services.sitemanagement.SiteManagementService#getScreen(java.lang.String)
	 */
	public ScreenFragment getScreen(String path) {
		log.debug("FIND:" + path);
        try {
            Page screen = siteModelManager.getPage(path);
            if (screen != null) {
                ScreenFragment sf = new ScreenFragment(servletConfig, screen, siteModelManager);
        		return sf;
            }
        } catch (Exception e) {
            log.error("Error while constructing screen:'" + path + "'", e);
        }
        return null;
	}

	/**
	 * @see net.sf.mmapps.commons.portalImpl.services.sitemanagement.SiteManagementService#getLoginSession(javax.servlet.http.HttpServletRequest)
	 */
	public LoginSession getLoginSession(HttpServletRequest request) {
		Cloud cloud = null;

		LoginSession ls = new LoginSession();
		ls.setAuthenticated(false);

		if (request != null) {
            cloud = CloudUtil.getCloudFromSession(request);
		} else {
			log.error("No request");
		}

		if (cloud != null) {
			UserContext u = cloud.getUser();
			// log.debug("UserContext='" + u.getIdentifier() + "'");

			Rank r = u.getRank();
			if (r.getInt() >= Rank.BASICUSER_INT) {
				ls.setAuthenticated(true);
			} else {
				ls.setAuthenticated(false);
			}
		} else {
			log.debug("No cloud found");
		}

		return ls;
	}

	/**
	 * @see net.sf.mmapps.commons.portalImpl.services.sitemanagement.SiteManagementService#setPortletParameter(java.lang.String, net.sf.mmapps.commons.beans.om.PortletParameter)
	 */
	public boolean setPortletParameter(String portletId, PortletParameter param) {
		boolean result = false;

		if (param != null) {
            String key = param.getKey();
            String value = param.getValue();
            Cloud cloud = getUserCloud();
            PortletUtil.updatePortletParameter(cloud, portletId, key, value);
		}

		log.debug("++++ Param for portlet:'" + portletId + "'");
		return result;
	}

    /**
     * @see net.sf.mmapps.commons.portalImpl.services.sitemanagement.SiteManagementService#setPortletNodeParameter(java.lang.String, net.sf.mmapps.commons.beans.om.PortletParameter)
     */
    public boolean setPortletNodeParameter(String portletId, PortletParameter param) {
        boolean result = false;

        if (param != null) {
            String key = param.getKey();
            String value = param.getValue();
            Cloud cloud = getUserCloud();
            Node node = cloud.getNode(value);
            PortletUtil.updatePortletParameter(cloud, portletId, key, node);
        }

        log.debug("++++ Param for portlet:'" + portletId + "'");
        return result;
    }

    
	/**
	 * @see net.sf.mmapps.commons.portalImpl.services.sitemanagement.SiteManagementService#setPortletView(java.lang.String, java.lang.String)
	 */
	public boolean setPortletView(String portletId, String viewId) {
		boolean result = true;
		log.debug("setPortletView portlet='" + portletId + "' view='" + viewId + "'");
		try {
			Cloud cloud = getUserCloud();
			PortletUtil.updatePortletView(cloud, portletId, viewId);
		} catch (Exception e) {
			log.error("something went wrong while setting view (" + viewId + ") for portlet (" + portletId + ")");
            if (log.isDebugEnabled()) {
                log.debug(e);
            }
			result = false;
		}

		return result;
	}

    /**
     * @see net.sf.mmapps.commons.portalImpl.services.sitemanagement.SiteManagementService#setPagePortlet(java.lang.String, java.lang.String, java.lang.String)
     */
    public boolean setPagePortlet(String screenId, String portletId, String id) {
        boolean result = true;
        log.debug("setScreenPortlet screen:'" + screenId + "' portlet:'" + portletId + "'");
        try {
            Cloud cloud = getUserCloud();
            PortletUtil.setPagePortlet(cloud, screenId, portletId, id);
        } catch (Exception e) {
            log.error("something went wrong while adding portlet (" + portletId + ")", e);
            result = false;
        }
        return result;
    }
    
	/**
	 * @see net.sf.mmapps.commons.portalImpl.services.sitemanagement.SiteManagementService#createPagePortlet(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean createPagePortlet(String screenId, String portletName, String definitionName, String layoutId,
			String viewId) {
        boolean result = true;
        log.debug("createScreenPortlet screen:'" + screenId + "' portlet:'" + portletName +
                  "' definition:'" + definitionName + "'");
        try {
    		Cloud cloud = getUserCloud();
    		Node newNode = PortletUtil.createPortlet(cloud, portletName, definitionName, viewId);
            PortletUtil.setPagePortlet(cloud, screenId, newNode, layoutId);
        } catch (Exception e) {
            log.error("something went wrong while creating portlet (" + portletName + ")", e);
            result = false;
        }

        return result;
	}

	/**
	 * @see net.sf.mmapps.commons.portalImpl.services.sitemanagement.SiteManagementService#getSites()
	 */
	public List<Site> getSites() {
        return siteModelManager.getSites();
	}

	/**
	 * @see net.sf.mmapps.commons.portalImpl.services.sitemanagement.SiteManagementService#getPages(net.sf.mmapps.commons.beans.om.Page)
	 */
	public List<Page> getPages(Page page) {
		if (page != null) {
            return siteModelManager.getChildren(page);
		}
		return new ArrayList<Page>();
	}

	/**
	 * @see net.sf.mmapps.commons.portalImpl.services.sitemanagement.SiteManagementService#getPages(net.sf.mmapps.commons.beans.om.Site)
	 */
	public List<Page> getPages(Site site) {
        if (site != null) {
            return siteModelManager.getChildren(site);
        }
        return new ArrayList<Page>();
	}

	/**
	 * @see com.finalist.pluto.portalImpl.services.sitemanagement.SiteManagementService#deletePagePortlet(net.sf.mmapps.commons.beans.om.Page, net.sf.mmapps.commons.beans.om.Portlet, java.lang.String)
	 */
	public void deletePagePortlet(Page page, Portlet portlet, String layoutId) {
		if (page != null && portlet != null) {
			PortletUtil.deletePagePortlet(getUserCloud(), page.getId(), portlet.getId(), layoutId);
		}
	}

	/**
	 * @see net.sf.mmapps.commons.portalImpl.services.sitemanagement.SiteManagementService#getPage(int)
	 */
	public Page getPage(int channel) {
	    return siteModelManager.getPage(channel);
	}

	/**
	 * @see net.sf.mmapps.commons.portalImpl.services.sitemanagement.SiteManagementService#getStylesheetForPage(int)
	 */
	public List<Stylesheet> getStylesheetForPageByPath (String path) {
            List<Page> pagesToRoot = getListFromPath(path);//get all pages to root
            List<Integer> stylesheetNumbers;
            List<Stylesheet> stylesheets = new ArrayList<Stylesheet>();
            for(int i=pagesToRoot.size(); i > 0; i-- ){//start with deepest page in tree
                Page page = pagesToRoot.get(i-1);
                if(!page.getStylesheet().isEmpty()){
                    stylesheetNumbers = page.getStylesheet();
                    for (int j =0; j <stylesheetNumbers.size(); j++) {
                        Integer stylesheetNumber = stylesheetNumbers.get(j);
                        Stylesheet stylesheet = siteModelManager.getStylesheet(stylesheetNumber.intValue());
                        stylesheets.add(stylesheet);
                        return stylesheets;
                    }
                }
            }
            return new ArrayList<Stylesheet>();
        }


	/**
	 * @see net.sf.mmapps.commons.portalImpl.services.sitemanagement.SiteManagementService#getPageFromPath(java.lang.String)
	 */
	public Page getPageFromPath(String path) {
        return siteModelManager.getPage(path);
	}

	/**
	 * @see net.sf.mmapps.commons.portalImpl.services.sitemanagement.SiteManagementService#getSiteFromPath(java.lang.String)
	 */
	public Site getSiteFromPath(String path) {
        return siteModelManager.getSite(path);
	}

	/**
	 * @see net.sf.mmapps.commons.portalImpl.services.sitemanagement.SiteManagementService#getListFromPath(java.lang.String)
	 */
	public List<Page> getListFromPath(String path) {
        return siteModelManager.getPagesForPath(path);
	}

	/**
	 * @see com.finalist.pluto.portalImpl.services.sitemanagement.SiteManagementService#getPageLink(net.sf.mmapps.commons.beans.om.Page, boolean)
	 */
	public String getPageLink(Page page, boolean includeRoot) {
        return siteModelManager.getPath(page, includeRoot);
	}

    /**
     * @see com.finalist.pluto.portalImpl.services.sitemanagement.SiteManagementService#getViews(java.lang.String, java.lang.String)
     */
    public List<View> getViews(String screenId, String layoutId) {
        return siteModelManager.getViews(screenId, layoutId);
    }

    public List<View> getViews(String definitionId) {
        return siteModelManager.getViews(definitionId);
    }
    
    /**
     * @see com.finalist.cmsc.portalImpl.services.sitemanagement.SiteManagementService#getViews(com.finalist.cmsc.beans.om.PortletDefinition)
     */
    public List<View> getViews(PortletDefinition definition) {
        return siteModelManager.getViews(definition);
    }
    
    /**
     * @see com.finalist.pluto.portalImpl.services.sitemanagement.SiteManagementService#getSingletonPortlets(java.lang.String, java.lang.String)
     */
    public List<PortletDefinition> getSingletonPortlets(String screenId, String layoutId) {
        List<PortletDefinition> defs = siteModelManager.getSingletonPortlets(screenId, layoutId);
        removeDefinitionsBasedOnRank(defs);
        return defs;
    }

    /**
     * @see com.finalist.pluto.portalImpl.services.sitemanagement.SiteManagementService#getPortletDefintions(java.lang.String, java.lang.String)
     */
    public List<PortletDefinition> getPortletDefintions(String screenId, String layoutId) {
        List<PortletDefinition> defs =  siteModelManager.getPortletDefintions(screenId, layoutId);
        removeDefinitionsBasedOnRank(defs);
        return defs;
    }
    

    private void removeDefinitionsBasedOnRank(List<PortletDefinition> defs) {
        Cloud cloud = getUserCloud();
        Rank rank = cloud.getUser().getRank();
        for (Iterator iter = defs.iterator(); iter.hasNext();) {
            PortletDefinition definition = (PortletDefinition) iter.next();
            if (definition.getRank() > rank.getInt()) {
                iter.remove();
            }
        }
    }

    public boolean mayEdit(Page page) {
        boolean result = false;
        try {
            Cloud cloud = getUserCloud();
            UserRole role = NavigationUtil.getRoleForUser(cloud, page.getId());
            result = role != null && SecurityUtil.isWriter(role);
        } catch (Exception e) {
            log.error("something went wrong checking page edit (" + page.getId() + ")");
            if (log.isDebugEnabled()) {
                log.debug(e);
            }
        }
        return result;
    }

    public boolean mayEdit(Portlet portlet) {
        boolean result = false;
        try {
            Cloud cloud = getUserCloud();
            PortletDefinition definition = 
                siteModelManager.getPortletDefinition(portlet.getDefinition());
            if (definition.isSingle()) {
                result = cloud.getUser().getRank() == Rank.ADMIN;
            }
            else {
                result = true;
            }
        } catch (Exception e) {
            log.error("something went wrong checking portlet edit (" + portlet.getId() + ")");
            if (log.isDebugEnabled()) {
                log.debug(e);
            }
        }
        return result;
    }

    
	private Cloud getUserCloud() {
        Cloud cloud = CloudUtil.getCloudFromThread();
        if (cloud == null) {
            log.warn("User cloud not found in thread; make sure that the user cloud is bound");
            cloud = cloudProvider.getAdminCloud();
        }
		return cloud;
	}

    public List<String> getContentTypes(String portletId) {
        return siteModelManager.getContentTypes(portletId);
    }

}
