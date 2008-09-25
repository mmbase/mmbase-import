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

package com.finalist.pluto.portalImpl.aggregation;

import com.finalist.cmsc.services.Parameters;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class AbstractFragment implements Fragment {
   private static final Logger log = Logging.getLoggerInstance(AbstractFragment.class.getName());

   private String id;

   private ServletConfig config;

   private Parameters initParameters;

   private static Properties routings;


   public AbstractFragment(String id, ServletConfig config, Fragment parent) throws Exception {
      StringBuffer compId = new StringBuffer();
      if (parent != null) {
         String parentID = parent.getId();
         if (parentID != null) {
            compId.append(parentID);
            compId.append("_");
         }
      }

      if (id != null) {
         compId.append(id);
         this.id = compId.toString();
      }

      this.config = config;
      initRoutingRules();
   }


   public String getId() {
      return id;
   }


   public Parameters getInitParameters() {
      return initParameters;
   }


   // additional methods.
   public ServletConfig getServletConfig() {
      return config;
   }


   public String getServletContextParameterValue(String name, String defaultValue) {
      String contextValue = getServletConfig().getServletContext().getInitParameter(name);
      if (contextValue != null && !"".equals(contextValue)) {
         return contextValue;
      }
      return defaultValue;
   }


   public RequestDispatcher getMainRequestDispatcher(String resourceName, String contentType) {
       contentType = removeCharsetFromContentType(contentType);
       if (null != contentType && !routings.containsKey(contentType)) {
           throw new IllegalArgumentException("invalid contenttype: " + contentType);
       }
       
      String location = routings.getProperty(contentType);
      log.debug(String.format("Get RequestDispatcher of %s from %s", resourceName, location));

      return getRequestDispatcher("cmsc.portal.layout.base.dir", location.trim(), resourceName.trim());
   }

    private String removeCharsetFromContentType(String contentType) {
        int indexOf = contentType.indexOf(";");
        if (indexOf > -1) {
            contentType = contentType.substring(0, indexOf);
        }
        return contentType;
    }

   public RequestDispatcher getRequestDispatcher(String contentName, String defaultValue, String resourceName) {
      String root = getServletContextParameterValue(contentName, defaultValue);
      return getServletConfig().getServletContext().getRequestDispatcher(root + resourceName);
   }


   public String getInitParameterValue(String name) {
      return initParameters.getString(name);
   }

   private void initRoutingRules() {
     if (routings == null) {
         InputStream inputStream = this.getClass().getResourceAsStream("layoutrouting.properties");
         routings = new Properties();
         try {
            routings.load(inputStream);
         } catch (IOException e) {
            log.error(e);
         }
     }
   }
}
