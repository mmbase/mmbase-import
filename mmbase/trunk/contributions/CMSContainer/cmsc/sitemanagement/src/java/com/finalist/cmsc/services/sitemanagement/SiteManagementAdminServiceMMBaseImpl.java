/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.services.sitemanagement;

import javax.servlet.ServletConfig;

import net.sf.mmapps.commons.bridge.CloudUtil;
import net.sf.mmapps.commons.util.StringUtil;
import net.sf.mmapps.modules.cloudprovider.CloudProvider;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.beans.om.*;
import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.navigation.PortletUtil;
import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.security.UserRole;
import com.finalist.cmsc.services.Properties;

/**
 * MMBase specific PortalLayoutService implementation, in this case MMBase
 * manages the Screens/Layout and Portlets
 * 
 * @author Wouter Heijke
 */
public class SiteManagementAdminServiceMMBaseImpl extends SiteManagementAdminService {
	private static Log log = LogFactory.getLog(SiteManagementAdminServiceMMBaseImpl.class);

	private CloudProvider cloudProvider;
    private SiteModelManager siteModelManager;

	public void init(ServletConfig config, Properties aProperties) throws Exception {
		this.cloudProvider = CloudProviderFactory.getCloudProvider();
		log.info("SiteManagementService STARTED");
        
        siteModelManager = new SiteModelManager();
	}

	public boolean setPortletParameter(String portletId, PortletParameter param) {
		boolean result = false;

		if (param != null) {
            String key = param.getKey();
            String value = param.getValue();
            Cloud cloud = getUserCloud();
            PortletUtil.updatePortletParameter(cloud, portletId, key, value);
            siteModelManager.clearPortlet(portletId);
		}
        
		log.debug("++++ Param for portlet:'" + portletId + "'");
		return result;
	}

    public boolean setPortletNodeParameter(String portletId, PortletParameter param) {
        boolean result = false;

        if (param != null) {
            String key = param.getKey();
            String value = param.getValue();
            Cloud cloud = getUserCloud();
            Node node = null;
            if (!StringUtil.isEmptyOrWhitespace(value)) {
                node = cloud.getNode(value);
            }
            PortletUtil.updatePortletParameter(cloud, portletId, key, node);
            siteModelManager.clearPortlet(portletId);
        }

        log.debug("++++ Param for portlet:'" + portletId + "'");
        return result;
    }

    
	public boolean setPortletView(String portletId, String viewId) {
		boolean result = true;
		log.debug("setPortletView portlet='" + portletId + "' view='" + viewId + "'");
		try {
			Cloud cloud = getUserCloud();
			PortletUtil.updatePortletView(cloud, portletId, viewId);
            siteModelManager.clearPortlet(portletId);
		} catch (Exception e) {
			log.error("something went wrong while setting view (" + viewId + ") for portlet (" + portletId + ")");
            if (log.isDebugEnabled()) {
                log.debug(e);
            }
			result = false;
		}

		return result;
	}

    public boolean setPagePortlet(String screenId, String portletId, String id) {
        boolean result = true;
        log.debug("setScreenPortlet screen:'" + screenId + "' portlet:'" + portletId + "'");
        try {
            Cloud cloud = getUserCloud();
            PortletUtil.setPagePortlet(cloud, screenId, portletId, id);
            siteModelManager.clearPage(screenId);
        } catch (Exception e) {
            log.error("something went wrong while adding portlet (" + portletId + ")", e);
            result = false;
        }
        return result;
    }
    
	public boolean createPagePortlet(String screenId, String portletName, String definitionName, String layoutId,
			String viewId) {
        boolean result = true;
        log.debug("createScreenPortlet screen:'" + screenId + "' portlet:'" + portletName +
                  "' definition:'" + definitionName + "'");
        try {
    		Cloud cloud = getUserCloud();
    		Node newNode = PortletUtil.createPortlet(cloud, portletName, definitionName, viewId);
            PortletUtil.setPagePortlet(cloud, screenId, newNode, layoutId);
            siteModelManager.clearPage(screenId);
        } catch (Exception e) {
            log.error("something went wrong while creating portlet (" + portletName + ")", e);
            result = false;
        }

        return result;
	}

	public void deletePagePortlet(Page page, Portlet portlet, String layoutId) {
		if (page != null && portlet != null) {
			PortletUtil.deletePagePortlet(getUserCloud(), page.getId(), portlet.getId(), layoutId);
            siteModelManager.clearPage(page.getId());
		}
	}

    public boolean mayEdit(Page page) {
        boolean result = false;
        try {
            Cloud cloud = getUserCloud();
            UserRole role = NavigationUtil.getRole(cloud, page.getId());
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
                result = cloud.getUser().getRank().getInt() >= definition.getRank();
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

}
