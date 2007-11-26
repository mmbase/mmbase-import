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

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pluto.services.ContainerService;

public class PortletContainerEnvironment implements org.apache.pluto.services.PortletContainerEnvironment {
   private static Log log = LogFactory.getLog(PortletContainerEnvironment.class);

   private HashMap<Class, ContainerService> services = new HashMap<Class, ContainerService>();


   public PortletContainerEnvironment() {
   }


   // org.apache.pluto.services.PortletContainerEnvironment implementation.

   public ContainerService getContainerService(Class service) {
      return services.get(service);
   }


   // additional methods.

   public void addContainerService(ContainerService service) {
      Class serviceClass = service.getClass();
      log.debug("class='" + serviceClass.getName() + "'");
      while (serviceClass != null) {
         Class[] interfaces = serviceClass.getInterfaces();
         for (Class element : interfaces) {
            Class[] interfaces2 = element.getInterfaces();
            for (Class element2 : interfaces2) {
               if (element2.equals(ContainerService.class)) {
                  services.put(element, service);
               }
            }
         }
         serviceClass = serviceClass.getSuperclass();
      }
   }

}
