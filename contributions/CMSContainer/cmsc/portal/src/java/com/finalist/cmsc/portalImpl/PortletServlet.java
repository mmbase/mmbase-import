/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.portalImpl;

import java.io.IOException;
import java.util.Enumeration;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pluto.Constants;
import org.apache.pluto.core.CoreUtils;
import org.apache.pluto.core.InternalPortletRequest;
import org.apache.pluto.core.InternalPortletResponse;
import org.apache.pluto.factory.PortletObjectAccess;
import org.apache.pluto.om.portlet.PortletDefinition;

import com.finalist.pluto.container.factory.PortletFactory;
import com.finalist.pluto.container.factory.impl.PortletFactoryImpl;

/**
 * Servlet to handle Portlets
 * 
 * @author Wouter Heijke
 * @version $Revision: 1.5 $
 */
public class PortletServlet extends HttpServlet {
   private static Log log = LogFactory.getLog(PortletServlet.class);

   private Portlet portletClass = null;

   private PortletFactory portletFactory;


   public void init(ServletConfig config) throws ServletException {
      super.init(config);

      log.debug("==>PortletServlet INIT<===");

      portletFactory = new PortletFactoryImpl();
   }


   public void init() {
      log.debug("PortletServlet init done");
   }


   public final String getInitParameter(String name) {
      return getServletConfig().getInitParameter(name);
   }


   public final Enumeration getInitParameterNames() {
      return getServletConfig().getInitParameterNames();
   }


   public ServletContext getServletContext() {
      return getServletConfig().getServletContext();
   }


   protected long getLastModified(HttpServletRequest req) {
      return -1;
   }


   public String getServletInfo() {
      return "";
   }


   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      dispatch(req, resp);
   }


   protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      dispatch(req, resp);
   }


   protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      dispatch(req, resp);
   }


   public void destroy() {
      portletFactory.destroy();
      super.destroy();
   }


   private void dispatch(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

      // if (!portletInitialized) {
      // throw new ServletException("this portlet uses the <load-on-startup>
      // flag. You have to turn it off");
      // }

      PortletDefinition portletDefinition = (PortletDefinition) request
            .getAttribute(PortalConstants.CMSC_PORTLET_DEFINITION);
      try {
         portletClass = portletFactory.getPortletInstance(this.getServletContext(), portletDefinition);
         Integer method_id = (Integer) request.getAttribute(Constants.METHOD_ID);
         if (method_id.equals(Constants.METHOD_RENDER)) {
            RenderRequest renderRequest = (RenderRequest) request.getAttribute(Constants.PORTLET_REQUEST);
            RenderResponse renderResponse = (RenderResponse) request.getAttribute(Constants.PORTLET_RESPONSE);

            // prepare container objects to run in this webModule
            prepareRenderRequest(renderRequest, request);
            prepareRenderResponse(renderResponse, request, response);
            
            portletClass.render(renderRequest, renderResponse);
         }
         else if (method_id.equals(Constants.METHOD_ACTION)) {
            ActionRequest actionRequest = (ActionRequest) request.getAttribute(Constants.PORTLET_REQUEST);
            ActionResponse actionResponse = (ActionResponse) request.getAttribute(Constants.PORTLET_RESPONSE);

            // prepare container objects to run in this webModule
            prepareActionRequest(actionRequest, request);
            prepareActionResponse(actionResponse, request, response);

            portletClass.processAction(actionRequest, actionResponse);
         }
         else if (method_id.equals(Constants.METHOD_NOOP)) { // AKA Load
            PortletContext portletContext = null;
            PortletConfig portletConfig = null;

            portletContext = PortletObjectAccess.getPortletContext(this.getServletContext(), portletDefinition
                  .getPortletApplicationDefinition());
            portletConfig = PortletObjectAccess.getPortletConfig(this.getServletConfig(), portletContext,
                  portletDefinition);
            // fill attribute, so that JSPs/servlets can access the config
            request.setAttribute(Constants.PORTLET_CONFIG, portletConfig);
            try {
               portletClass.init(portletConfig);
            }
            catch (PortletException e) {
               throw new ServletException(e);
            }
         }
      }
      catch (javax.portlet.UnavailableException e) {
         /*
          * if (e.isPermanent()) { throw new
          * javax.servlet.UnavailableException(e.getMessage()); } else { throw
          * new javax.servlet.UnavailableException(e.getMessage(),
          * e.getUnavailableSeconds()); }
          */

         // destroy isn't called by Tomcat, so we have to fix it
         try {
            portletClass.destroy();
         }
         catch (Throwable t) {
            // don't care for Exception
         }

         // handle everything as permanently for now
         throw new javax.servlet.UnavailableException(e.getMessage());
      }
      catch (PortletException e) {
         throw new ServletException(e);
      }
      finally {
         request.removeAttribute(Constants.PORTLET_CONFIG);
      }
   }


   private void prepareActionRequest(ActionRequest portletRequest, HttpServletRequest servletRequest) {
      InternalPortletRequest internalPortletRequest = CoreUtils.getInternalRequest(portletRequest);
      internalPortletRequest.lateInit(servletRequest);
   }


   private void prepareRenderRequest(RenderRequest portletRequest, HttpServletRequest servletRequest) {
      InternalPortletRequest internalPortletRequest = CoreUtils.getInternalRequest(portletRequest);
      internalPortletRequest.lateInit(servletRequest);
   }


   private void prepareRenderResponse(RenderResponse portletResponse, HttpServletRequest servletRequest,
         HttpServletResponse servletResponse) {
      InternalPortletResponse internalPortletResponse = CoreUtils.getInternalResponse(portletResponse);
      internalPortletResponse.lateInit(servletRequest, servletResponse);
   }


   private void prepareActionResponse(ActionResponse portletResponse, HttpServletRequest servletRequest,
         HttpServletResponse servletResponse) {
      InternalPortletResponse internalPortletResponse = CoreUtils.getInternalResponse(portletResponse);
      internalPortletResponse.lateInit(servletRequest, servletResponse);
   }
}
