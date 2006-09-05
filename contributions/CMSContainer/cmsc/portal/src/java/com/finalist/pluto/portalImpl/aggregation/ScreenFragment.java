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
import com.finalist.pluto.portalImpl.core.PortalURL;

/**
 * @author Wouter Heijke
 * @version $Revision: 1.2 $
 */
public class ScreenFragment extends AbstractFragmentContainer{
	private static Log log = LogFactory.getLog(ScreenFragment.class);
    
	private Page page;
    private Layout layout;

	public ScreenFragment(ServletConfig config, Page page, Layout layout) throws Exception {
		super(null, config, null);
		this.page = page;
        this.layout = layout; 
        log.debug("Create - page: " + page.getId() + " layout: " + page.getLayout());
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
        
        Iterator portlets = this.getChildFragments().iterator();
        while(portlets.hasNext()) {
            Fragment portlet = (Fragment) portlets.next();
            // let the Portlet do it's thing
            portlet.service(request, response);
        }
        
		if (page != null) {
			if (layout != null) {
                log.debug("using layout:'" + layout.getResource() + "' for page:'" + page.getTitle() + "'");

                request.setAttribute(PortalConstants.FRAGMENT, this);
				RequestDispatcher rd = getMainRequestDispatcher(layout.getResource());
				rd.include(request, response);
                request.removeAttribute(PortalConstants.FRAGMENT);
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
