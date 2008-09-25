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
import java.util.Collection;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pluto.om.common.Description;
import org.apache.pluto.om.common.DescriptionSet;
import org.apache.pluto.om.common.PreferenceSet;
import org.apache.pluto.om.entity.PortletApplicationEntity;
import org.apache.pluto.om.entity.PortletEntity;
import org.apache.pluto.om.entity.PortletEntityCtrl;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.om.window.PortletWindowList;

import com.finalist.pluto.portalImpl.om.common.Support;
import com.finalist.pluto.portalImpl.om.common.impl.DescriptionSetImpl;
import com.finalist.pluto.portalImpl.om.common.impl.PreferenceSetImpl;
import com.finalist.pluto.portalImpl.om.window.impl.PortletWindowListImpl;
import com.finalist.pluto.portalImpl.services.portletdefinitionregistry.PortletDefinitionRegistry;
import com.finalist.pluto.portalImpl.util.ObjectID;

public class PortletEntityImpl implements PortletEntity, PortletEntityCtrl, Serializable, Support {
   private static Log log = LogFactory.getLog(PortletEntityImpl.class);

   private String id;

   private String definitionId;

   protected PreferenceSet preferences;

   private PreferenceSet origPreferences;

   private PortletApplicationEntity applicationEntity;

   private PortletWindowList portletWindows;

   private ObjectID objectId;

   private DescriptionSet descriptions;


   public PortletEntityImpl() {
      id = "";
      definitionId = "";
      preferences = new PreferenceSetImpl();
      origPreferences = new PreferenceSetImpl();
      portletWindows = new PortletWindowListImpl();
      descriptions = new DescriptionSetImpl();
   }


   // PortletEntity implementation.

   public ObjectID getId() {
      if (objectId == null && applicationEntity != null) {
         objectId = ObjectID.createFromString(applicationEntity.getId().toString() + "." + id);
      }
      return objectId;
   }


   public PreferenceSet getPreferenceSet() {
      return preferences;
   }


   public PortletDefinition getPortletDefinition() {
      return PortletDefinitionRegistry.getPortletDefinition(ObjectID.createFromString(definitionId));
   }


   public void setPortletDefinition(PortletDefinition portletDefinition) {
      this.definitionId = portletDefinition.getId().toString();
   }


   public PortletApplicationEntity getPortletApplicationEntity() {
      return applicationEntity;
   }


   public PortletWindowList getPortletWindowList() {
      return portletWindows;
   }


   /*
    * (non-Javadoc)
    * 
    * @see org.apache.pluto.om.entity.PortletEntity#getDescriptionSet()
    */
   public Description getDescription(Locale locale) {
      return descriptions.get(locale);
   }


   // PortletEntityCtrl implementation.

   public void setId(String id) {
      log.debug("====>PortletEntityImpl:" + id);
      this.id = id;
      objectId = null;
   }


   public void store() throws java.io.IOException {
      // TODO WOUTZ commented, see if it works
      // PortletEntityRegistry.store();

      // save preferences as original preferences
      origPreferences = new PreferenceSetImpl();
      ((PreferenceSetImpl) origPreferences).addAll((Collection) preferences);
   }


   public void reset() throws java.io.IOException {
      // reset by re-activating original preferences
      preferences = new PreferenceSetImpl();
      ((PreferenceSetImpl) preferences).clear();
      ((PreferenceSetImpl) preferences).addAll((Collection) origPreferences);
   }


   protected void setPortletApplicationEntity(PortletApplicationEntity applicationEntity) {
      this.applicationEntity = applicationEntity;
   }


   protected void setPortletWindowList(PortletWindowList portletWindows) {
      this.portletWindows = portletWindows;
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
