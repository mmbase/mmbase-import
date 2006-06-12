/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.portalImpl.registry;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.finalist.pluto.portalImpl.aggregation.Fragment;
import com.finalist.pluto.portalImpl.aggregation.ScreenFragment;

/**
 * Registry to keep track of things in the CMSC Portal
 * 
 * @author Wouter Heijke
 * @version $Revision: 1.1 $
 */
public class PortalRegistry {
	private static Log log = LogFactory.getLog(PortalRegistry.class);

	private static final String PORTAL_REGISTRY = "com.finalist.pluto.portalImpl.core.PortalRegistry";

	private ScreenFragment screen;

	private static String contextPath;
	
	public static PortalRegistry getPortalRegistry(HttpServletRequest request) {
		PortalRegistry reg = (PortalRegistry) request.getSession().getAttribute(PORTAL_REGISTRY);
		if (reg == null) {
			reg = new PortalRegistry();
			request.getSession().setAttribute(PORTAL_REGISTRY, reg);
		}
		
		contextPath = request.getContextPath();
		return reg;
	}
	
	public void setScreen(ScreenFragment screen) {
		this.screen = screen;
	}

	public ScreenFragment getScreen() {
		return this.screen;
	}
	
	public String getContextPath() {
		return contextPath;
	}

	public Fragment getFragment(String id) {
		Fragment fragment = null;
		ScreenFragment s = getScreen();
		Iterator iterator = s.getChildFragments().iterator();
		while (iterator.hasNext()) {
			Fragment tmp = (Fragment) iterator.next();
			if (tmp != null) {
				if (tmp.getKey().equalsIgnoreCase(id)) {
					fragment = tmp;
					break;
				}
			}
		}
		log.debug("getFragment: '" + id + "':'" + fragment + "'");
		return fragment;
	}
}
