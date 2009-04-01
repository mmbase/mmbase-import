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

package com.finalist.pluto.portalImpl.om.entity.impl;

import java.io.Serializable;
import java.util.Iterator;

import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.entity.PortletApplicationEntity;
import org.apache.pluto.om.entity.PortletApplicationEntityList;
import org.apache.pluto.om.entity.PortletApplicationEntityListCtrl;

import com.finalist.pluto.portalImpl.om.common.AbstractSupportSet;
import com.finalist.pluto.portalImpl.om.common.Support;

public class PortletApplicationEntityListImpl extends AbstractSupportSet implements PortletApplicationEntityList,
      PortletApplicationEntityListCtrl, Serializable, Support {

   // PortletApplicationEntityList implementation.

   public PortletApplicationEntity get(ObjectID objectId) {
      Iterator iterator = this.iterator();
      while (iterator.hasNext()) {
         PortletApplicationEntity portletApplicationEntity = (PortletApplicationEntity) iterator.next();
         if (portletApplicationEntity.getId().equals(objectId)) {
            return portletApplicationEntity;
         }
      }
      return null;
   }


   // PortletApplicationEntityListCtrl implementation.

   public PortletApplicationEntity add(String definitionId) {
      PortletApplicationEntityImpl entity = new PortletApplicationEntityImpl();

      int id = -1;
      for (Iterator iter = iterator(); iter.hasNext();) {
         PortletApplicationEntityImpl ent = (PortletApplicationEntityImpl) iter.next();
         // TODO woutz no more castor
         // try {
         // id = Math.max(id, Integer.parseInt(ent.getCastorId()));
         // } catch (NumberFormatException e) {
         // // don't care
         // }
      }
      entity.setId(Integer.toString(++id));
      entity.setDefinitionId(definitionId);

      add(entity);

      return entity;
   }


   // additional methods.

   // additional internal methods

   public PortletApplicationEntity get(String objectId) {
      Iterator iterator = this.iterator();
      while (iterator.hasNext()) {
         PortletApplicationEntity portletApplicationEntity = (PortletApplicationEntity) iterator.next();
         if (portletApplicationEntity.getId().toString().equals(objectId)) {
            return portletApplicationEntity;
         }
      }
      return null;
   }

}
