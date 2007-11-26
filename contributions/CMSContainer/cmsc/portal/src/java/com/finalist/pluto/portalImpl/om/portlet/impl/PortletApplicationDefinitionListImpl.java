/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.pluto.portalImpl.om.portlet.impl;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;
import org.apache.pluto.om.portlet.PortletApplicationDefinitionList;
import org.apache.pluto.util.StringUtils;

public class PortletApplicationDefinitionListImpl extends HashSet implements PortletApplicationDefinitionList,
      Serializable {

   public PortletApplicationDefinition get(ObjectID objectId) {
      Iterator iterator = this.iterator();
      while (iterator.hasNext()) {
         PortletApplicationDefinition portletApplicationDefinition = (PortletApplicationDefinition) iterator.next();
         if (portletApplicationDefinition.getId().equals(objectId)) {
            return portletApplicationDefinition;
         }
      }
      return null;
   }


   public PortletApplicationDefinition get(String objectId) {
      Iterator iterator = this.iterator();
      while (iterator.hasNext()) {
         PortletApplicationDefinition portletApplicationDefinition = (PortletApplicationDefinition) iterator.next();
         if (portletApplicationDefinition.getId().toString().equals(objectId)) {
            return portletApplicationDefinition;
         }
      }
      return null;
   }


   public String toString() {
      return toString(0);
   }


   public String toString(int indent) {
      StringBuffer buffer = new StringBuffer(50);
      StringUtils.newLine(buffer, indent);
      buffer.append(getClass().toString());
      buffer.append(": ");
      Iterator iterator = this.iterator();
      while (iterator.hasNext()) {
         buffer.append(((PortletApplicationDefinitionImpl) iterator.next()).toString(indent + 2));
      }
      return buffer.toString();
   }
}
