package com.finalist.pluto.portalImpl.om.servlet.impl;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pluto.om.servlet.ServletDefinition;
import org.apache.pluto.om.servlet.ServletDefinitionList;
import org.apache.pluto.om.servlet.ServletDefinitionListCtrl;
import org.apache.pluto.util.StringUtils;

import com.finalist.pluto.portalImpl.om.common.AbstractSupportSet;
import com.finalist.pluto.portalImpl.om.common.Support;

public class ServletDefinitionListImpl extends AbstractSupportSet implements ServletDefinitionList,
      ServletDefinitionListCtrl, java.io.Serializable, Support {
   private static Log log = LogFactory.getLog(ServletDefinitionListImpl.class);


   public ServletDefinition get(String name) {
      Iterator iterator = this.iterator();
      while (iterator.hasNext()) {
         ServletDefinition servletDefinition = (ServletDefinition) iterator.next();
         if (servletDefinition.getServletName().equals(name)) {
            return servletDefinition;
         }
      }
      return null;
   }


   public ServletDefinition add(String name, String className) {
      ServletDefinitionImpl servletDefinition = new ServletDefinitionImpl();
      servletDefinition.setServletName(name);
      servletDefinition.setServletClass(className);

      super.add(servletDefinition);

      return servletDefinition;
   }


   public ServletDefinition remove(String name) {
      Iterator iterator = this.iterator();
      while (iterator.hasNext()) {
         ServletDefinition servletDefinition = (ServletDefinition) iterator.next();
         if (servletDefinition.getServletName().equals(name)) {
            super.remove(servletDefinition);
            return servletDefinition;
         }
      }
      return null;
   }


   public void remove(ServletDefinition servletDefinition) {
      super.remove(servletDefinition);
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
         buffer.append(((ServletDefinitionImpl) iterator.next()).toString(indent + 2));
      }
      return buffer.toString();
   }
}
