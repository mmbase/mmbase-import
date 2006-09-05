/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.services.sitemanagement;

import java.util.*;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;

import net.sf.mmapps.commons.bridge.CloudUtil;
import net.sf.mmapps.modules.cloudprovider.CloudProvider;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.Cloud;
import org.mmbase.module.Module;
import org.mmbase.module.tools.MMAdmin;
import org.mmbase.security.Rank;
import org.mmbase.security.UserContext;

import com.finalist.cmsc.beans.om.*;
import com.finalist.cmsc.services.Properties;
import com.finalist.cmsc.services.security.LoginSession;

/**
 * MMBase specific PortalLayoutService implementation, in this case MMBase
 * manages the Screens/Layout and Portlets
 * 
 * @author Wouter Heijke
 */
public class SiteManagementServiceMMBaseImpl extends SiteManagementService {
	private static Log log = LogFactory.getLog(SiteManagementServiceMMBaseImpl.class);

	private CloudProvider cloudProvider;
    private SiteModelManager siteModelManager;

	public void init(ServletConfig config, Properties aProperties) throws Exception {
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

    @Override
	public boolean isNavigation(String path) {
		log.debug("isNavigation:'" + path + "'");
        return siteModelManager.hasPage(path); 
	}

    @Override
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

    @Override
    public List<Site> getSites() {
        return siteModelManager.getSites();
	}

    @Override
	public List<Page> getPages(Page page) {
		if (page != null) {
            return siteModelManager.getChildren(page);
		}
		return new ArrayList<Page>();
	}

    @Override
	public List<Page> getPages(Site site) {
        if (site != null) {
            return siteModelManager.getChildren(site);
        }
        return new ArrayList<Page>();
	}

    @Override
	public Page getPage(int channel) {
	    return siteModelManager.getPage(channel);
	}

    @Override
	public List<Stylesheet> getStylesheetForPageByPath (String path) {
        List<Page> pagesToRoot = getListFromPath(path);//get all pages to root
        List<Stylesheet> stylesheets = new ArrayList<Stylesheet>();

        for(int count = 0; count < pagesToRoot.size(); count++){
            Page page = pagesToRoot.get(count);

            List<Integer> stylesheetNumbers = page.getStylesheet();
            for (int j =0; j <stylesheetNumbers.size(); j++) {
                Integer stylesheetNumber = stylesheetNumbers.get(j);
                Stylesheet stylesheet = siteModelManager.getStylesheet(stylesheetNumber.intValue());
                stylesheets.add(stylesheet);
            }
        }
        return stylesheets;
    }

    @Override
	public Page getPageFromPath(String path) {
        return siteModelManager.getPage(path);
	}

    @Override
	public Site getSiteFromPath(String path) {
        return siteModelManager.getSite(path);
	}

    @Override
	public List<Page> getListFromPath(String path) {
        return siteModelManager.getPagesForPath(path);
	}

    @Override
	public String getPath(Page page, boolean includeRoot) {
        return siteModelManager.getPath(page, includeRoot);
	}

    @Override
    public String getPath(int pageId, boolean includeRoot) {
        Page page = siteModelManager.getPage(pageId);
        return siteModelManager.getPath(page, includeRoot);
    }

    @Override
    public List<View> getViews(String screenId, String layoutId) {
        return siteModelManager.getViews(screenId, layoutId);
    }

    @Override
    public List<View> getViews(String definitionId) {
        return siteModelManager.getViews(definitionId);
    }
    
    @Override
    public List<View> getViews(PortletDefinition definition) {
        return siteModelManager.getViews(definition);
    }
    
    @Override
    public List<PortletDefinition> getSingletonPortlets(String screenId, String layoutId) {
        List<PortletDefinition> defs = siteModelManager.getSingletonPortlets(screenId, layoutId);
        removeDefinitionsBasedOnRank(defs);
        return defs;
    }

    @Override
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
    
	private Cloud getUserCloud() {
        Cloud cloud = CloudUtil.getCloudFromThread();
        if (cloud == null) {
            log.warn("User cloud not found in thread; make sure that the user cloud is bound");
            cloud = cloudProvider.getAdminCloud();
        }
		return cloud;
	}

    @Override
   public List<String> getContentTypes(String portletId) {
        return siteModelManager.getContentTypes(portletId);
    }

    @Override
    public Set<String> getPagePositions(String pageId) {
        return siteModelManager.getPagePositions(Integer.valueOf(pageId));
    }

    @Override
	public String getPageImageForPath(String name, String path) {
        List<Page> pagesToRoot = getListFromPath(path);//get all pages to root

        for(int count = pagesToRoot.size() - 1; count >= 0; count--){
            Page page = pagesToRoot.get(count);
            String image = page.getPageImage(name);
            if(image != null) {
            	return image;
            }
        }
		return null;
	}

    @Override
    public Layout getLayout(int layout) {
        return siteModelManager.getLayout(layout);
    }

    @Override
    public Portlet getPortlet(int portletId) {
        return siteModelManager.getPortlet(portletId);
    }

    @Override
    public PortletDefinition getPortletDefinition(int definition) {
        return siteModelManager.getPortletDefinition(definition);
    }

    @Override
    public View getView(int view) {
        return siteModelManager.getView(view);
    }

}
