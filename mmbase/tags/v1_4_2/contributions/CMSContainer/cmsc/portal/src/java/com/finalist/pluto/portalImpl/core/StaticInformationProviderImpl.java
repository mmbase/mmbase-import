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

import javax.servlet.ServletConfig;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.services.information.PortalContextProvider;
import org.apache.pluto.services.information.StaticInformationProvider;

import com.finalist.pluto.portalImpl.services.portletdefinitionregistry.PortletDefinitionRegistry;

public class StaticInformationProviderImpl implements StaticInformationProvider {
   private static Log log = LogFactory.getLog(StaticInformationProviderImpl.class);

   private PortalContextProvider provider;


   public StaticInformationProviderImpl(ServletConfig config) {
      // nothing do to
   }


   // StaticInformationProvider implementation.

   public PortalContextProvider getPortalContextProvider() {
      if (provider == null) {
         provider = new PortalContextProviderImpl();
      }

      return provider;
   }


   public PortletDefinition getPortletDefinition(ObjectID portletGUID) {
      log.debug("portletGUID='" + portletGUID + "'");
      return PortletDefinitionRegistry.getPortletDefinition(portletGUID);
   }
}
