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
package com.finalist.pluto.portalImpl.services.config;

import javax.servlet.ServletConfig;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.finalist.cmsc.services.Parameters;
import com.finalist.cmsc.services.Properties;

/**
 * * The implementation of the {@link ConfigService}. * *
 * <P> * To establish the configuration parameters, this implementation * first
 * looks in the servlet configuration, then the servlet * context, and lastly
 * its own configuration file.
 */
public class ConfigServiceImpl extends ConfigService {
   private static Log log = LogFactory.getLog(ConfigServiceImpl.class);

   private Parameters iParameters;


   public void init(ServletConfig aConfig, Properties aProperties) throws Exception {
      log.debug("init ConfigServiceImpl");

      iParameters = new Parameters(aConfig);

      Parameters contextParams = new Parameters(aConfig.getServletContext());

      contextParams.setParent(aProperties);

      iParameters.setParent(contextParams);
   }


   public void destroy() {
      iParameters = null;
   }


   public Parameters getParameters() {
      return (iParameters);
   }

}
