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
/* 

 */

package com.finalist.pluto.portalImpl.core;

import java.util.*;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;

import org.apache.pluto.om.window.PortletWindow;
import org.apache.pluto.services.information.DynamicInformationProvider;
import org.apache.pluto.services.information.PortletActionProvider;
import org.apache.pluto.services.information.PortletURLProvider;
import org.apache.pluto.services.information.ResourceURLProvider;

import com.finalist.pluto.portalImpl.services.config.Config;

public class DynamicInformationProviderImpl implements DynamicInformationProvider {

   private ServletConfig config;

   private PortalEnvironment env;

   HttpServletRequest request;


   public DynamicInformationProviderImpl(HttpServletRequest request, ServletConfig config) {
      this.request = request;
      this.config = config;

      env = PortalEnvironment.getPortalEnvironment(request);
   }


   // DynamicInformationProviderImpl implementation.

   public PortletMode getPortletMode(PortletWindow portletWindow) {
      return env.getPortalControlParameter().getMode(portletWindow);
   }


   public PortletURLProvider getPortletURLProvider(PortletWindow portletWindow) {
      return new PortletURLProviderImpl(this, portletWindow);
   }


   public ResourceURLProvider getResourceURLProvider(PortletWindow portletWindow) {
      return new ResourceURLProviderImpl(this, portletWindow);
   }


   public PortletActionProvider getPortletActionProvider(PortletWindow portletWindow) {
      return new PortletActionProviderImpl(request, config, portletWindow);
   }


   public PortletMode getPreviousPortletMode(PortletWindow portletWindow) {
      return env.getPortalControlParameter().getPrevMode(portletWindow);
   }


   public WindowState getPreviousWindowState(PortletWindow portletWindow) {
      return env.getPortalControlParameter().getPrevState(portletWindow);
   }


   public String getResponseContentType() {
      String mimetype = env.getRequestedMimetype();
      if (mimetype == null || mimetype.length() == 0) {
          mimetype = getSupportedMimeTypes()[0];
      }
      return mimetype;
   }


   public Iterator getResponseContentTypes() {
      String[] supportedMimetypes = getSupportedMimeTypes();
      return Arrays.asList(supportedMimetypes).iterator();
   }

   private String[] getSupportedMimeTypes() {
      String[] supportedMimetypes = Config.getParameters().getStrings("supported.mimetypes");
      return supportedMimetypes;
   }

   public WindowState getWindowState(PortletWindow portletWindow) {
      return env.getPortalControlParameter().getState(portletWindow);
   }


   public boolean isPortletModeAllowed(PortletMode mode) {
      // checks whether PortletMode is supported as example
      String[] supportedModes = Config.getParameters().getStrings("supported.portletmode");
      for (String element : supportedModes) {
         if (element.equalsIgnoreCase(mode.toString())) {
            return true;
         }
      }
      return false;
   }


   public boolean isWindowStateAllowed(WindowState state) {
      // checks whether WindowState is supported as example
      String[] supportedStates = Config.getParameters().getStrings("supported.windowstate");
      for (String element : supportedStates) {
         if (element.equalsIgnoreCase(state.toString())) {
            return true;
         }
      }
      return false;
   }

}
