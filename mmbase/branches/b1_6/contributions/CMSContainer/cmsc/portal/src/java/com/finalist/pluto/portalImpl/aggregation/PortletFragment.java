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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pluto.PortletContainerException;
import org.apache.pluto.om.ControllerObjectAccess;
import org.apache.pluto.om.entity.PortletEntity;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.om.servlet.ServletDefinition;
import org.apache.pluto.om.servlet.ServletDefinitionCtrl;
import org.apache.pluto.om.window.*;

import com.finalist.cmsc.beans.om.*;
import com.finalist.cmsc.portalImpl.PortalConstants;
import com.finalist.cmsc.portalImpl.headerresource.HeaderResource;
import com.finalist.cmsc.util.HttpUtil;
import com.finalist.cmsc.util.ServerUtil;
import com.finalist.pluto.portalImpl.core.*;
import com.finalist.pluto.portalImpl.om.common.impl.PreferenceSetImpl;
import com.finalist.pluto.portalImpl.om.entity.impl.PortletEntityImpl;
import com.finalist.pluto.portalImpl.om.servlet.impl.WebApplicationDefinitionImpl;
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
 * @author Wouter Heijke
 */
public class PortletFragment extends AbstractFragment {

   private static final Log log = LogFactory.getLog(PortletFragment.class);

   public static final String PORTLET_ERROR_MSG = "Error occurred in portlet!";

   private com.finalist.cmsc.beans.om.Portlet portlet;
   private PortletWindow portletWindow;
   private StringWriter storedWriter;
   private int expirationCache = -1;

   private List<HeaderResource> headerResources;


   public PortletFragment(ServletConfig config, Fragment parent, String layoutId,
         com.finalist.cmsc.beans.om.Portlet portlet, com.finalist.cmsc.beans.om.PortletDefinition definition, View view)
         throws Exception {
      super(layoutId, config, parent);

      if (portlet == null) {
         throw new IllegalArgumentException("Portlet is null for layoutid " + layoutId);
      }
      if (definition == null) {
         throw new IllegalArgumentException("Portlet is null for layoutid " + layoutId);
      }

      this.portlet = portlet;

      PortletEntityImpl portletEntity = new PortletEntityImpl();
      portletEntity.setId(getId());
      portletEntity.setDefinitionId(definition.getDefinition());
      PortletDefinition portletDefinition = portletEntity.getPortletDefinition();
      if (portletDefinition == null) {
         throw new IllegalArgumentException("Missing definition " + definition.getDefinition() + " in portlet.xml");
      }
      
      // for now set CMSC portlet params in the preferences of the portlet
      // entiy
      log.debug("Create - portlet: " + portlet.getId());

      PreferenceSetImpl ps = (PreferenceSetImpl) portletEntity.getPreferenceSet();
      setDefaultPreferences(ps);

      List<Object> p = portlet.getPortletparameters();
      if (!p.isEmpty()) {
         Iterator<Object> pparams = p.iterator();
         while (pparams.hasNext()) {
            Object objectParam = pparams.next();
            if (objectParam instanceof PortletParameter) {
               PortletParameter param = (PortletParameter) objectParam;
               String key = param.getKey();
               List<String> values = param.getValues();
               if (values != null) {
                  log.debug("key: " + key + " value: " + values);
                  ps.add(key, values);
               }
            }
            if (objectParam instanceof NodeParameter) {
               NodeParameter param = (NodeParameter) objectParam;
               String key = param.getKey();
               List<String> values = param.getValues();
               if (values != null) {
                  log.debug("key: " + key + " value: " + values);
                  ps.add(key, values);
               }
            }
         }
      }

      // also add the view
      if (view != null) {
         ps.add(PortalConstants.CMSC_OM_VIEW_ID, view.getId());
         ps.add(PortalConstants.CMSC_PORTLET_VIEW_TEMPLATE, view.getResource());
      }
      
      String expiractionFromDefinition = portletDefinition.getExpirationCache();
      /* Portlet spec 1.0 PLT.18.1 Expiration Cache
       * For a portlet that has not defined expiration cache in the deployment descriptor,
       * if the expiration cache property is set it must be ignored by the portlet-container.
       *
       * Here we are doing something similar for the expiration settings or our Definitions, views and Portlets
       * which are configured through the database
       */
      if (StringUtils.isNotBlank(expiractionFromDefinition)) {
         try {
            expirationCache = Integer.valueOf(expiractionFromDefinition);
         }
         catch(NumberFormatException nfe) {
            log.error("Cache expiration in xml is not a number for " + portletDefinition.getName());
         }

         if (definition.getExpirationcache() > -1) {
            expirationCache = definition.getExpirationcache();
         }
         if (view != null && view.getExpirationcache() > -1) {
            expirationCache = Math.min(expirationCache, view.getExpirationcache());
         }
         if (portlet.getExpirationcache() > -1) {
            expirationCache = portlet.getExpirationcache();
         }
      }

      portletWindow = new PortletWindowImpl(getKey());
      ((PortletWindowCtrl) portletWindow).setPortletEntity(portletEntity);
      PortletWindowList windowList = portletEntity.getPortletWindowList();
      ((PortletWindowListCtrl) windowList).add(portletWindow);
   }


   protected final void setDefaultPreferences(PreferenceSetImpl ps) {
      ps.add(PortalConstants.CMSC_OM_PORTLET_ID, String.valueOf(portlet.getId()));
      ps.add(PortalConstants.CMSC_OM_PORTLET_DEFINITIONID, String.valueOf(portlet.getDefinition()));
   }


   public void processAction(HttpServletRequest request, HttpServletResponse response) throws IOException {
      setupRequest(request);

      try {
         PortletContainerFactory.getPortletContainer().processPortletAction(portletWindow,
               ServletObjectAccess.getServletRequest(request, portletWindow),
               ServletObjectAccess.getServletResponse(response));
      }
      catch (PortletException e) {
         log.fatal("process portlet raised an exception", e);
         response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
      }
      catch (PortletContainerException e) {
         log.fatal("portlet container raised an exception", e);
         response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
      }
      cleanRequest(request);
   }


   public void service(HttpServletRequest request, HttpServletResponse response) {
      log.debug("PortletFragment service enters");
      storedWriter = new StringWriter();
      setupRequest(request);

      HttpServletRequest wrappedRequest = ServletObjectAccess.getServletRequest(request, portletWindow);
      // load the Portlet
      // If there is an error loading, then we will save the error message and
      // attempt
      // to render it inside the Portlet, so the Portal has a chance of still
      // looking
      // okay
      String errorMsg = null;
      try {
          log.debug("|| portletLoad:'" + portletWindow.getId() + "'");

         PortletContainerFactory.getPortletContainer().portletLoad(portletWindow, wrappedRequest, response);

         PortletDefinition def = portletWindow.getPortletEntity().getPortletDefinition();
         if (def == null) {
            throw new PortletException("PortletDefinition not found for window " + portletWindow.getId());
         }

         // store the context path in the webapp.
         PortletApplicationDefinition app = def.getPortletApplicationDefinition();
         WebApplicationDefinitionImpl wa = (WebApplicationDefinitionImpl) app.getWebApplicationDefinition();
         wa.setContextRoot(request.getContextPath());
      }
      catch (PortletContainerException e) {
         log.error("PortletContainerException-Error in Portlet", e);
         errorMsg = getErrorMsg(e);
      }
      catch (Throwable t) {
         // If we catch any throwable, we want to try to continue
         // so that the rest of the portal renders correctly
         log.error("Error in Portlet", t);
         if (t instanceof VirtualMachineError) {
            // if the Throwable is a VirtualMachineError then
            // it is very unlikely (!) that the portal is going
            // to render correctly.
            throw (Error) t;
         }
         else {
            errorMsg = getErrorMsg(t);
         }
      }

      if (errorMsg != null) {
         storedWriter.write(errorMsg);
         return;
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

      ServletDefinition servletDefinition = getServletDefinition();

      if (servletDefinition != null && !servletDefinition.isUnavailable()) {
         PrintWriter writer2 = new PrintWriter(storedWriter);

         // create a wrapped response which the Portlet will be rendered to
         ServletResponseImpl wrappedResponse = (ServletResponseImpl) ServletObjectAccess.getStoredServletResponse(
               response, writer2);

         try {
            // render the Portlet to the wrapped response, to be output
            // later.
            PortletContainerFactory.getPortletContainer().renderPortlet(portletWindow, wrappedRequest, wrappedResponse);
         }
         catch (UnavailableException e) {
            writer2.println("the portlet is currently unavailable!");

            ServletDefinitionCtrl servletDefinitionCtrl = (ServletDefinitionCtrl) ControllerObjectAccess
                  .get(servletDefinition);
            if (e.isPermanent()) {
               servletDefinitionCtrl.setAvailable(Long.MAX_VALUE);
            }
            else {
               int unavailableSeconds = e.getUnavailableSeconds();
               if (unavailableSeconds <= 0) {
                  unavailableSeconds = 60; // arbitrary default
               }
               servletDefinitionCtrl.setAvailable(System.currentTimeMillis() + unavailableSeconds * 1000);
            }
         }
         catch (Exception e) {
            writer2.println(getErrorMsg(e));
         }

      }
      else {
         log.error("Error no servletDefinition!!!");
      }
      cleanRequest(request);
      log.debug("PortletFragment service exits");
   }


   private void setupRequest(HttpServletRequest request) {
      request.setAttribute(PortalConstants.CMSC_OM_PORTLET_LAYOUTID, getKey());
      request.setAttribute(PortalConstants.FRAGMENT, this);
   }

   private void cleanRequest(HttpServletRequest request) {
       request.removeAttribute(PortalConstants.FRAGMENT);
       request.removeAttribute(PortalConstants.CMSC_OM_PORTLET_LAYOUTID);
   }


   private ServletDefinition getServletDefinition() {
      ServletDefinition servletDefinition = null;
      PortletEntity portletEntity = portletWindow.getPortletEntity();
      if (portletEntity == null) {
         log.error("PortletEntity not found for window " + portletWindow.getId());
      }
      else {
         PortletDefinition portletDefinition = portletEntity.getPortletDefinition();
         if (portletDefinition == null) {
            log.error("PortletDefinition not found for entity " + portletEntity.getId());
         }
         else {
            servletDefinition = portletDefinition.getServletDefinition();
         }
      }
      return servletDefinition;
   }


   public void writeToResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
      PrintWriter responseWriter = response.getWriter();
      try {
         boolean unavailable = getServletDefinition().isUnavailable();
         request.setAttribute(PortalConstants.FRAGMENT, this);

         String portletHeaderJsp = getServletContextParameterValue("portlet.header.jsp", "PortletFragmentHeader.jsp");
         // output the header JSP page

         // request.setAttribute("portletInfo", portletInfo);
         RequestDispatcher rd = getMainRequestDispatcher(portletHeaderJsp, response.getContentType());
         rd.include(request, response);
         try {
            // output the Portlet
            // no error message, so output the Portlet
            if (unavailable) {
               // the portlet is unavailable
               responseWriter.println("the portlet is currently unavailable!");
            }
            else {
               responseWriter.print(storedWriter.toString());
            }
         }
         finally {
            // output the footer JSP page
            String portletFooterJsp = getServletContextParameterValue("portlet.footer.jsp", "PortletFragmentFooter.jsp");
            RequestDispatcher rdFooter = getMainRequestDispatcher(portletFooterJsp, response.getContentType());
            rdFooter.include(request, response);

            request.removeAttribute(PortalConstants.FRAGMENT);
         }
      }
      catch (ServletException e) {
         log.error("Error in portlet servlet");
         responseWriter.println("Error in portlet servlet");
      }
      catch (IOException e) {
         log.error("Error in portlet");
         responseWriter.println("Error in portlet");
      }
      finally {
         storedWriter = null;
      }
   }

   public PortletMode getPortletMode(PortalEnvironment env, PortletWindow portletWindow) {
       return env.getPortalControlParameter().getMode(portletWindow);
   }


   public PortletWindow getPortletWindow() {
      return portletWindow;
   }


   public com.finalist.cmsc.beans.om.Portlet getPortlet() {
      return portlet;
   }


   protected String getErrorMsg(Throwable t) {
      if (ServerUtil.isStaging()) {
         return "<pre>" + PORTLET_ERROR_MSG + "\n\n" + HttpUtil.getExceptionInfo(t) + "</pre>";
      }
      else {
         return "";
      }
   }


   public final String getKey() {
      return getId(); // "_" + layoutId;
   }


   public Collection<HeaderResource> getHeaderResources() {
      return headerResources;
   }


   public void addHeaderResource(HeaderResource resource) {
      if (headerResources == null) {
         headerResources = new ArrayList<HeaderResource>();
      }
      headerResources.add(resource);
   }



   public int getExpirationCache() {
      return expirationCache;
   }

}
