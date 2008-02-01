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

import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.pluto.services.information.DynamicInformationProvider;
import org.apache.pluto.services.information.InformationProviderService;
import org.apache.pluto.services.information.StaticInformationProvider;

import com.finalist.pluto.portalImpl.factory.InformationProviderFactory;

public class InformationProviderServiceFactoryImpl implements InformationProviderFactory, InformationProviderService {

   private ServletConfig servletConfig;


   // InformationProviderFactory implementation.
   // InformationProviderService implementation.

   public StaticInformationProvider getStaticProvider() {
      ServletContext context = servletConfig.getServletContext();

      StaticInformationProvider provider = (StaticInformationProvider) context
            .getAttribute("com.finalist.pluto.portalImpl.StaticInformationProvider");

      if (provider == null) {
         provider = new StaticInformationProviderImpl(servletConfig);
         context.setAttribute("com.finalist.pluto.portalImpl.StaticInformationProvider", provider);
      }

      return provider;
   }


   public DynamicInformationProvider getDynamicProvider(HttpServletRequest request) {
      DynamicInformationProvider provider = (DynamicInformationProvider) request
            .getAttribute("com.finalist.pluto.portalImpl.DynamicInformationProvider");

      if (provider == null) {
         provider = new DynamicInformationProviderImpl(request, servletConfig);
         request.setAttribute("com.finalist.pluto.portalImpl.DynamicInformationProvider", provider);
      }

      return provider;
   }


   // additional methods.

   public void init(ServletConfig config, Map properties) throws Exception {
      servletConfig = config;
   }


   public void destroy() throws Exception {
   }

}
