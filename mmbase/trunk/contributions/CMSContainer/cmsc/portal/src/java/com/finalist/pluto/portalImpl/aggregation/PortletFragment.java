/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.pluto.portalImpl.aggregation;

import java.io.*;
import java.util.*;

import javax.portlet.*;
import javax.portlet.UnavailableException;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pluto.PortletContainerException;
import org.apache.pluto.om.ControllerObjectAccess;
import org.apache.pluto.om.servlet.ServletDefinition;
import org.apache.pluto.om.servlet.ServletDefinitionCtrl;
import org.apache.pluto.om.window.*;

import com.finalist.cmsc.beans.om.*;
import com.finalist.cmsc.portalImpl.PortalConstants;
import com.finalist.cmsc.portalImpl.services.sitemanagement.SiteModelManager;
import com.finalist.pluto.portalImpl.core.*;
import com.finalist.pluto.portalImpl.om.common.impl.PreferenceSetImpl;
import com.finalist.pluto.portalImpl.om.entity.impl.PortletEntityImpl;
import com.finalist.pluto.portalImpl.om.window.impl.PortletWindowImpl;
import com.finalist.pluto.portalImpl.servlet.ServletObjectAccess;
import com.finalist.pluto.portalImpl.servlet.ServletResponseImpl;

/**
 * <p>
 * Responsible for rendering a single Portlet.
 * <p>
 * <p>
 * Requires two JSP files to exist, PortletFragmentHeader.jsp and
 * PortletFragmentFooter.jsp. These pages define the header and footer of the
 * portlet.
 * </p>
 * 
 * @author Stephan Hesmer
 * @author Nick Lothian
 * @author Wouter Heijke
 * @version $Revision: 1.1 $
 */
public class PortletFragment extends AbstractFragmentSingle {
    
    private static Log log = LogFactory.getLog(PortletFragment.class);

    public static final String PORTLET_ERROR_MSG = "Error occurred in portlet!";

	private com.finalist.cmsc.beans.om.Portlet portlet;
    private String layoutId;
	private PortletWindow portletWindow;

	public PortletFragment(ServletConfig config, Fragment parent, String layoutId, com.finalist.cmsc.beans.om.Portlet portlet, SiteModelManager siteModelManager) throws Exception {
		super(layoutId, config, parent);
		this.portlet = portlet;
        this.layoutId = layoutId;

        com.finalist.cmsc.beans.om.PortletDefinition definition = 
                            siteModelManager.getPortletDefinition(portlet.getDefinition());

        PortletEntityImpl portletEntity = new PortletEntityImpl();
        portletEntity.setId(getId());
        portletEntity.setDefinitionId(definition.getDefinition());

		// for now set CMSC portlet params in the preferences of the portlet
		// entiy
		if (portlet != null) {
            log.debug("Create - portlet: " + portlet.getId());
            
			PreferenceSetImpl ps = (PreferenceSetImpl) portletEntity.getPreferenceSet(); 
            setDefaultPreferences(parent, portlet, ps);
            
            List p = portlet.getPortletparameters();
			if (p.size() > 0) {
				Iterator pparams = p.iterator();
				while (pparams.hasNext()) {
                    Object objectParam = pparams.next();
                    if (objectParam instanceof PortletParameter) {
    					PortletParameter param = (PortletParameter) objectParam;
    					String key = param.getKey();
                        String value = param.getValue();
                        log.debug("key: " + key + " value: " + value);
                        ps.add(key, value);
                    }
                    if (objectParam instanceof NodeParameter) {
                        NodeParameter param = (NodeParameter) objectParam;
                        String key = param.getKey();
                        String value = String.valueOf(param.getValue().getNumber());
                        log.debug("key: " + key + " value: " + value);
                        ps.add(key, value);
                    }
				}
			}
            
			// also add the view
			View v = siteModelManager.getView(portlet.getView());
			if (v != null) {
				ps.add(PortalConstants.CMSC_OM_VIEW_ID, v.getId());
                ps.add(PortalConstants.CMSC_PORTLET_VIEW_TEMPLATE, v.getResource());
			}
		}

		portletWindow = new PortletWindowImpl(getKey());
		((PortletWindowCtrl) portletWindow).setPortletEntity(portletEntity);
		PortletWindowList windowList = portletEntity.getPortletWindowList();
		((PortletWindowListCtrl) windowList).add(portletWindow);
	}

    protected void setDefaultPreferences(Fragment parent, com.finalist.cmsc.beans.om.Portlet portlet, PreferenceSetImpl ps) {
        ScreenFragment screenFragment = (ScreenFragment)parent;
        ps.add(PortalConstants.CMSC_OM_PAGE_ID, screenFragment.getPage().getId());
        ps.add(PortalConstants.CMSC_OM_PORTLET_ID, String.valueOf(portlet.getId()));
        ps.add(PortalConstants.CMSC_OM_PORTLET_DEFINITIONID, String.valueOf(portlet.getDefinition()));
        ps.add(PortalConstants.CMSC_OM_PORTLET_LAYOUTID, String.valueOf(layoutId));
    }

	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.debug("PortletFragment service enters");
		HttpServletRequest wrappedRequest = ServletObjectAccess.getServletRequest(request, portletWindow);
		ServletResponseImpl wrappedResponse = (ServletResponseImpl) ServletObjectAccess.getServletResponse(response);
		PrintWriter responseWriter = response.getWriter();
		StringWriter storedWriter = new StringWriter();

		// load the Portlet
		// If there is an error loading, then we will save the error message and attempt
		// to render it inside the Portlet, so the Portal has a chance of still looking
		// okay
		String errorMsg = null;
		try {
			log.debug("|| portletLoad:'" + portletWindow.getId() + "'");
			PortletContainerFactory.getPortletContainer().portletLoad(portletWindow, wrappedRequest, response);
		} catch (PortletContainerException e) {
			log.error("PortletContainerException-Error in Portlet", e);
			errorMsg = getErrorMsg();
		} catch (Throwable t) {
			// If we catch any throwable, we want to try to continue
			// so that the rest of the portal renders correctly
			log.error("Error in Portlet", t);
			if (t instanceof VirtualMachineError) {
				// if the Throwable is a VirtualMachineError then
				// it is very unlikely (!) that the portal is going
				// to render correctly.
				throw (Error) t;
			} else {
				errorMsg = getErrorMsg();
			}

		}

		PortalEnvironment env = (PortalEnvironment) request.getAttribute(PortalEnvironment.REQUEST_PORTALENV);
		PortalURL thisURL = env.getRequestedPortalURL();

		log.debug("|| thisURL='" + thisURL + "'");

		PortalControlParameter thisControl = new PortalControlParameter(thisURL);
		if (thisControl.isOnePortletWindowMaximized()) {
			WindowState currentState = thisControl.getState(portletWindow);
			if (!WindowState.MAXIMIZED.equals(currentState)) {
				return;
			}
		}

		ServletDefinition servletDefinition = portletWindow.getPortletEntity().getPortletDefinition().getServletDefinition();
		if (servletDefinition != null && !servletDefinition.isUnavailable()) {
			PrintWriter writer2 = new PrintWriter(storedWriter);

			// create a wrapped response which the Portlet will be rendered to
			wrappedResponse = (ServletResponseImpl) ServletObjectAccess.getStoredServletResponse(response, writer2);

			try {
				// render the Portlet to the wrapped response, to be output
				// later.
				PortletContainerFactory.getPortletContainer().renderPortlet(portletWindow, wrappedRequest, wrappedResponse);
			} catch (UnavailableException e) {
				writer2.println("the portlet is currently unavailable!");

				ServletDefinitionCtrl servletDefinitionCtrl = (ServletDefinitionCtrl) ControllerObjectAccess.get(portletWindow
						.getPortletEntity().getPortletDefinition().getServletDefinition());
				if (e.isPermanent()) {
					servletDefinitionCtrl.setAvailable(Long.MAX_VALUE);
				} else {
					int unavailableSeconds = e.getUnavailableSeconds();
					if (unavailableSeconds <= 0) {
						unavailableSeconds = 60; // arbitrary default
					}
					servletDefinitionCtrl.setAvailable(System.currentTimeMillis() + unavailableSeconds * 1000);
				}
			} catch (Exception e) {
				writer2.println(getErrorMsg());
			}

		} else {
			log.error("Error no servletDefinition!!!");
		}

        String portletHeaderJsp = getServletContextParameterValue("portlet.header.jsp", "PortletFragmentHeader.jsp"); 
		// output the header JSP page
		request.setAttribute(PortalConstants.FRAGMENT, this);
		// request.setAttribute("portletInfo", portletInfo);
		RequestDispatcher rd = getMainRequestDispatcher(portletHeaderJsp);
		rd.include(request, response);
		try {
			// output the Portlet
			// check if there is an error message
			if (errorMsg == null) {
                // no error message, so output the Portlet
                if (portletWindow.getPortletEntity().getPortletDefinition().getServletDefinition()
                        .isUnavailable()) {
                    // the portlet is unavailable
                    responseWriter.println("the portlet is currently unavailable!");
                }
                else {
                    responseWriter.println(storedWriter.toString());
                }
			} else {
				// output the errror message
				responseWriter.println(errorMsg);
			}
		} finally {
			// output the footer JSP page
            String portletFooterJsp = getServletContextParameterValue("portlet.footer.jsp", "PortletFragmentFooter.jsp");
			RequestDispatcher rdFooter = getMainRequestDispatcher(portletFooterJsp);
			rdFooter.include(request, response);
		}
		log.debug("PortletFragment service exits");
	}

	public void createURL(PortalURL url) {
		getParent().createURL(url);
		url.addLocalNavigation(getId());
	}

	public boolean isPartOfURL(PortalURL url) {
		return true;
	}

	public PortletWindow getPortletWindow() {
		return portletWindow;
	}

	public com.finalist.cmsc.beans.om.Portlet getPortlet() {
		return portlet;
	}

    public String getLayoutId() {
        return layoutId;
    }
    
	protected String getErrorMsg() {
		return PORTLET_ERROR_MSG;
	}

	public String getKey() {
		return getId(); // "_" + layoutId;
	}

}
