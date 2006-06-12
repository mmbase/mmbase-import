package com.finalist.pluto.portalImpl.aggregation;

import java.io.IOException;
import java.util.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.finalist.cmsc.beans.om.*;
import com.finalist.cmsc.portalImpl.PortalConstants;
import com.finalist.cmsc.portalImpl.services.sitemanagement.SiteModelManager;
import com.finalist.pluto.portalImpl.core.PortalURL;

/**
 * @author Wouter Heijke
 * @version $Revision: 1.1 $
 */
public class ScreenFragment extends AbstractFragmentContainer{
	private static Log log = LogFactory.getLog(ScreenFragment.class);
    
	private Page page;
    private Layout layout;

	public ScreenFragment(ServletConfig config, Page page, SiteModelManager siteModelManager) throws Exception {
		super(null, config, null);
		this.page = page;
        layout = siteModelManager.getLayout(page.getLayout());
        
        log.debug("Create - page: " + page.getId() + " layout: " + page.getLayout());
        
        // place portletfragments and emptyfragments in the screenfragment
        
        Set<String> names = layout.getNames();
        for (Iterator iter = names.iterator(); iter.hasNext();) {
            String layoutId = (String) iter.next();
            Integer portletId = page.getPortlet(layoutId);
            Portlet portlet = siteModelManager.getPortlet(portletId);
            if (portlet != null) {
                PortletFragment pf = new PortletFragment(config, this, layoutId, portlet, siteModelManager);
                this.addChild(pf);
            } else {
                createDefaultPortlet(layoutId, config, siteModelManager);
            }
        }
	}
    
    private void createDefaultPortlet(String layoutId, ServletConfig config, SiteModelManager siteModelManager) {
        try {
            Portlet empty = siteModelManager.getPortlet(-1);
            page.addPortlet(layoutId, -1);
            EmptyFragment ef = new EmptyFragment(config, this, layoutId, empty, siteModelManager);
            addChild(ef);
        } catch (Exception e) {
            log.error("cannot create default portlet");
            if (log.isDebugEnabled()) {
                log.debug(e);
            }
        }
    }


	public void createURL(PortalURL url) {
		// do nothing
		// we assume that the given url points already to the base portal
		// servlet
	}

	public boolean isPartOfURL(PortalURL url) {
		return true;
	}

	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setAttribute(PortalConstants.FRAGMENT, this);
		if (page != null) {
			if (layout != null) {
				log.debug("using layout:'" + layout.getResource() + "' for page:'" + page.getTitle() + "'");
				RequestDispatcher rd = getMainRequestDispatcher(layout.getResource());
				rd.include(request, response);
			} else {
				log.error("No layout for Screen");
			}
		} else {
			log.error("No page for ScreenFragment");
		}
	}

	public Page getPage() {
		return page;
	}
    
    public Layout getLayout() {
        return layout;
    }

	public String getKey() {
		return this.getId();
	}

}
