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
package com.finalist.pluto.portalImpl.om.entity.impl;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pluto.om.entity.PortletApplicationEntity;
import org.apache.pluto.om.entity.PortletEntityList;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;

import com.finalist.pluto.portalImpl.om.common.Support;
import com.finalist.pluto.portalImpl.services.portletdefinitionregistry.PortletDefinitionRegistry;
import com.finalist.pluto.portalImpl.util.ObjectID;

public class PortletApplicationEntityImpl implements PortletApplicationEntity, Serializable, Support {
   private static Log log = LogFactory.getLog(PortletApplicationEntityImpl.class);

   private String id = "";

   private String definitionId = "";

   private PortletEntityList portlets = new PortletEntityListImpl();

   private ObjectID objectId;


   // PortletApplicationEntity implementation.

   public ObjectID getId() {
      if (objectId == null) {
         objectId = ObjectID.createFromString(id);
      }
      // log.debug("====>PortletApplicationEntityImpl:" + objectId);
      return objectId;
   }


   public PortletEntityList getPortletEntityList() {
      return portlets;
   }


   public PortletApplicationDefinition getPortletApplicationDefinition() {
      return PortletDefinitionRegistry.getPortletApplicationDefinitionList().get(
            ObjectID.createFromString(definitionId));
   }


   // additional methods.

   // additional internal methods

   public void setId(String id) {
      log.debug("====>PortletApplicationEntityImpl:" + id);
      this.id = id;
      objectId = null;
   }


   public String getDefinitionId() {
      return definitionId;
   }


   public void setDefinitionId(String definitionId) {
      this.definitionId = definitionId;
   }


   public void postLoad(Object parameter) throws Exception {
   }

}
