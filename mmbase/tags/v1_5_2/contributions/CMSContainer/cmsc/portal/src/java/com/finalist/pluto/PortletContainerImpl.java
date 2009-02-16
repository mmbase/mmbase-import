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
package com.finalist.pluto;

import java.io.IOException;
import java.util.Properties;

import javax.portlet.*;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pluto.PortletContainerException;
import org.apache.pluto.PortletContainerServices;
import org.apache.pluto.core.InternalActionResponse;
import org.apache.pluto.factory.PortletObjectAccess;
import org.apache.pluto.invoker.PortletInvoker;
import org.apache.pluto.invoker.PortletInvokerAccess;
import org.apache.pluto.om.window.PortletWindow;
import org.apache.pluto.services.PortletContainerEnvironment;

import com.finalist.cmsc.beans.om.*;
import com.finalist.cmsc.beans.om.Portlet;
import com.finalist.cmsc.portalImpl.PortalConstants;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;
import com.finalist.cmsc.services.sitemanagement.SiteManagementAdmin;
import com.finalist.pluto.portalImpl.aggregation.EmptyFragment;
import com.finalist.pluto.portalImpl.aggregation.PortletFragment;
import com.finalist.pluto.portalImpl.core.CmscPortletMode;

/**
 * Implements the Pluto Container.
 * 
 * @author Wouter Heijke
 */
public class PortletContainerImpl extends org.apache.pluto.PortletContainerImpl {
   private static Log log = LogFactory.getLog(PortletContainerImpl.class);

   private String uniqueContainerName;

   public void init(String uniqueContainerName, ServletConfig servletConfig, PortletContainerEnvironment environment,
         Properties properties) throws PortletContainerException {

      this.uniqueContainerName = uniqueContainerName;
      super.init(uniqueContainerName, servletConfig, environment, properties);
   }


   public void processPortletAction(PortletWindow portletWindow, HttpServletRequest servletRequest,
         HttpServletResponse servletResponse) throws PortletException, IOException {

      PortletContainerServices.prepare(uniqueContainerName);
      PortletInvoker invoker = null;

      if (log.isDebugEnabled()) {
         log.debug("PortletContainerImpl.performPortletAction(" + portletWindow.getId() + ") called.");
      }

      String location = null;

      InternalActionResponse _actionResponse = null;
      ActionRequest actionRequest = null;

      try {
         /* ActionRequest */
         actionRequest = PortletObjectAccess.getActionRequest(portletWindow, servletRequest, servletResponse);

         ActionResponse actionResponse = PortletObjectAccess.getActionResponse(portletWindow, servletRequest,
               servletResponse);
         invoker = PortletInvokerAccess.getPortletInvoker(portletWindow.getPortletEntity().getPortletDefinition());

         // FINALIST ADDED delete portlet mode

         PortletMode m = actionRequest.getPortletMode();
         if (m != null && m.equals(CmscPortletMode.DELETE)) {
            log.debug("CMSC Portlet DELETE action");

            PortletFragment portletFragment = (PortletFragment) servletRequest.getAttribute(PortalConstants.FRAGMENT);
            if (portletFragment instanceof EmptyFragment) {
               log.debug("Can't delete empty portlets of this type.");
            }
            else {
               String pageId = (String) servletRequest.getAttribute(PortalConstants.CMSC_OM_PAGE_ID);
               NavigationItem item = SiteManagement.getNavigationItem(Integer.valueOf(pageId));
               if (item instanceof Page) {
                   Page page = (Page) item;
                   Portlet portlet = portletFragment.getPortlet();
                   String layoutId = portletFragment.getKey();
                   SiteManagementAdmin.deleteScreenPortlet(page, portlet, layoutId);
               }
               actionResponse.setPortletMode(PortletMode.VIEW);
            }
         }
         else {
            // call action() at the portlet
            invoker.action(actionRequest, actionResponse);
         }
         // FINALIST ADDED delete portlet mode
         _actionResponse = (InternalActionResponse) actionResponse;

         location = _actionResponse.getRedirectLocation();
      }
      catch (PortletException e) {
         throw e;
      }
      catch (IOException e) {
         throw e;
      }
      catch (RuntimeException e) {
         throw e;
      }
      finally {
         try {
            redirect(location, portletWindow, servletRequest, servletResponse, _actionResponse);
         }
         finally {
            PortletInvokerAccess.releasePortletInvoker(invoker);
            PortletContainerServices.release();
         }
      }
   }

}
