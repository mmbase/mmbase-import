package com.finalist.pluto.portalImpl.om.portlet.impl;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.om.portlet.PortletDefinitionList;
import org.apache.pluto.util.StringUtils;

import com.finalist.pluto.portalImpl.om.common.AbstractSupportSet;
import com.finalist.pluto.portalImpl.om.common.Support;

public class PortletDefinitionListImpl extends AbstractSupportSet implements PortletDefinitionList,
      java.io.Serializable, Support {
   private static Log log = LogFactory.getLog(PortletDefinitionListImpl.class);


   public PortletDefinition get(ObjectID objectId) {
      Iterator iterator = this.iterator();
      while (iterator.hasNext()) {
         PortletDefinition portletDefinition = (PortletDefinition) iterator.next();
         if (portletDefinition.getId().equals(objectId)) {
            return portletDefinition;
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
         buffer.append(((PortletDefinitionImpl) iterator.next()).toString(indent + 2));
      }
      return buffer.toString();
   }
}
