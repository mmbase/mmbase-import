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
package com.finalist.pluto.portalImpl.core;

import java.util.Iterator;
import java.util.Map;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pluto.om.window.PortletWindow;
import org.apache.pluto.services.information.PortletURLProvider;

/**
 * @changes pluto-1.0.1 Logging
 */
public class PortletURLProviderImpl implements PortletURLProvider {
   private static Log log = LogFactory.getLog(PortletURLProviderImpl.class);

   protected String windowid;
   protected String page;
   protected String host;

   private DynamicInformationProviderImpl provider;

   private PortletWindow portletWindow;

   private PortletMode mode;

   private WindowState state;

   private boolean action;

   private boolean secure;

   private boolean clearParameters;

   private Map parameters;


   public PortletURLProviderImpl(DynamicInformationProviderImpl provider, PortletWindow portletWindow) {
      this.provider = provider;
      this.portletWindow = portletWindow;
   }


   // PortletURLProvider implementation.

   public void setPortletMode(PortletMode mode) {
      this.mode = mode;
   }


   public void setWindowState(WindowState state) {
      this.state = state;
   }


   public void setAction() {
      action = true;
   }


   public void setSecure() {
      secure = true;
   }


   public void clearParameters() {
      clearParameters = true;
   }


   public void setParameters(Map parameters) {
      this.parameters = parameters;

      Iterator entries = this.parameters.entrySet().iterator();
      while (entries.hasNext()) {
         Map.Entry entry = (Map.Entry) entries.next();
         String name = (String) entry.getKey();
         Object value = entry.getValue();
         String[] values = value instanceof String ? new String[] { (String) value } : (String[]) value;
      }
   }


   public String toString() {
      PortalURL url = null;
      if (page != null) {
         url = new PortalURL(host, provider.request, page);
      }
      else {
         url = PortalEnvironment.getPortalEnvironment(provider.request).getRequestedPortalURL();
      }

      PortalControlParameter controlURL = new PortalControlParameter(url);

      if (page != null) {
         if (mode != null) {
            controlURL.setMode(windowid, mode);
         }
         if (state != null) {
            controlURL.setState(windowid, state);
         }

         // set portlet id for associated request parms
         controlURL.setPortletId(windowid);
         if (action) {
            controlURL.setAction(windowid);
         }
      }
      else {
         if (mode != null) {
            controlURL.setMode(portletWindow, mode);
         }
         if (state != null) {
            controlURL.setState(portletWindow, state);
         }
         if (clearParameters) {
            controlURL.clearRenderParameters(portletWindow);
         }

         // set portlet id for associated request parms
         controlURL.setPortletId(portletWindow);
         if (action) {
            controlURL.setAction(portletWindow);
         }
      }

      if (parameters != null) {
         Iterator names = parameters.keySet().iterator();
         while (names.hasNext()) {
            String name = (String) names.next();
            Object value = parameters.get(name);
            String[] values = value instanceof String ? new String[] { (String) value } : (String[]) value;
            if (action) {
               // controlURL.setRequestParam(NamespaceMapperAccess.getNamespaceMapper().encode(portletWindow.getId(),
               // name),values);
               controlURL.setRequestParam(name, values);
            }
            else {
               if (page != null) {
                  controlURL.setRenderParam(windowid, name, values);
               }
               else {
                  controlURL.setRenderParam(portletWindow, name, values);
               }
            }
         }
      }

      return url.toString(controlURL, new Boolean(secure));
   }


   public void setPage(String page) {
      this.page = page;
   }


   public void setWindowid(String windowid) {
      this.windowid = windowid;
   }

   public void setHost(String host) {
      this.host = host;
   }

}
