/*
 * Copyright 2003,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.finalist.pluto.container.invoker.impl;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.UnavailableException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pluto.Constants;
import org.apache.pluto.core.CoreUtils;
import org.apache.pluto.core.InternalPortletRequest;
import org.apache.pluto.core.InternalPortletResponse;
import org.apache.pluto.invoker.PortletInvoker;
import org.apache.pluto.om.portlet.PortletDefinition;

import com.finalist.cmsc.portalImpl.PortalConstants;

public class PortletInvokerImpl implements PortletInvoker {
   private static Log log = LogFactory.getLog(PortletInvokerImpl.class);

   private ServletConfig servletConfig;

   private PortletDefinition portletDefinition;


   public PortletInvokerImpl(PortletDefinition portletDefinition, ServletConfig servletConfig) {
      this.portletDefinition = portletDefinition;
      this.servletConfig = servletConfig;
   }


   public void action(ActionRequest request, ActionResponse response) throws PortletException, IOException {
      invoke(request, response, Constants.METHOD_ACTION);
   }


   public void render(RenderRequest request, RenderResponse response) throws PortletException, IOException {
      invoke(request, response, Constants.METHOD_RENDER);
   }


   public void load(PortletRequest request, RenderResponse response) throws PortletException {
      try {
         invoke(request, response, Constants.METHOD_NOOP);
      }
      catch (IOException e) {
         log.error("PortletInvokerImpl.load() - Error while dispatching portlet.", e);
         throw new PortletException(e);
      }
   }


   /*
    * generic method to be used called by both, action and render
    */
   protected void invoke(PortletRequest portletRequest, PortletResponse portletResponse, Integer methodID)
         throws PortletException, IOException {
      InternalPortletRequest internalPortletRequest = CoreUtils.getInternalRequest(portletRequest);
      InternalPortletResponse internalPortletResponse = CoreUtils.getInternalResponse(portletResponse);

      // gather all required data from request and response
      ServletRequest servletRequest = ((HttpServletRequestWrapper) internalPortletRequest).getRequest();
      ServletResponse servletResponse = ((HttpServletResponseWrapper) internalPortletResponse).getResponse();
      ServletContext servletContext = servletConfig.getServletContext();

      RequestDispatcher dispatcher = servletContext.getNamedDispatcher(PortalConstants.CMSC_PORTLET_SERVLET);
      if (dispatcher != null) {
         try {
            servletRequest.setAttribute(Constants.METHOD_ID, methodID);
            servletRequest.setAttribute(Constants.PORTLET_REQUEST, portletRequest);
            servletRequest.setAttribute(Constants.PORTLET_RESPONSE, portletResponse);
            servletRequest.setAttribute(PortalConstants.CMSC_PORTLET_DEFINITION, portletDefinition);
            dispatcher.include(servletRequest, servletResponse);
         }
         catch (javax.servlet.UnavailableException e) {
            log.error("PortletInvokerImpl.invoke() - Error while dispatching portlet.", e);
            if (e.isPermanent()) {
               throw new UnavailableException(e.getMessage());
            }
            else {
               throw new UnavailableException(e.getMessage(), e.getUnavailableSeconds());
            }
         }
         catch (ServletException e) {
            if (e.getRootCause() != null) {
               log.error("PortletInvokerImpl.render() - Error while dispatching portlet.", e.getRootCause());
               if (e.getRootCause() instanceof PortletException) {
                  throw (PortletException) e.getRootCause();
               }
               else {
                  throw new PortletException(e.getRootCause());
               }
            }
            else {
               log.error("PortletInvokerImpl.invoke() - Error while dispatching portlet.", e);
               throw new PortletException(e);
            }
         }
         finally {
            servletRequest.removeAttribute(Constants.METHOD_ID);
            servletRequest.removeAttribute(Constants.PORTLET_REQUEST);
            servletRequest.removeAttribute(Constants.PORTLET_RESPONSE);
            servletRequest.removeAttribute(PortalConstants.CMSC_PORTLET_DEFINITION);
         }
      }
      else {
         log.error("PortletInvokerImpl.action() - Unable to find RequestDispatcher.");
         throw new PortletException("Unable to find dispatcher for context: " + servletContext.getServletContextName());
      }
   }
}
