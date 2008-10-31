package com.finalist.pluto.portalImpl.om.servlet.impl;

import java.util.Locale;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pluto.om.common.Description;
import org.apache.pluto.om.common.DescriptionSet;
import org.apache.pluto.om.common.DisplayName;
import org.apache.pluto.om.common.DisplayNameSet;
import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.common.ParameterSet;
import org.apache.pluto.om.servlet.ServletDefinition;
import org.apache.pluto.om.servlet.ServletDefinitionCtrl;
import org.apache.pluto.om.servlet.WebApplicationDefinition;
import com.finalist.pluto.portalImpl.om.common.Support;

public class ServletDefinitionImpl implements ServletDefinition, ServletDefinitionCtrl, java.io.Serializable, Support {
   private static Log log = LogFactory.getLog(ServletDefinitionImpl.class);


   public ObjectID getId() {
      log.debug("???  getId()");
      return null;
   }


   public String getServletName() {
      log.debug("???  getServletName()");
      return null;
   }


   public DisplayName getDisplayName(Locale arg0) {
      log.debug("???  getDisplayName()");
      return null;
   }


   public Description getDescription(Locale arg0) {
      log.debug("???  getDescription()");
      return null;
   }


   public String getServletClass() {
      log.debug("???  getServletClass()");
      return null;
   }


   public ParameterSet getInitParameterSet() {
      log.debug("???  getInitParameterSet()");
      return null;
   }


   public WebApplicationDefinition getWebApplicationDefinition() {
      log.debug("???  getWebApplicationDefinition()");
      return null;
   }


   public RequestDispatcher getRequestDispatcher(ServletContext arg0) {
      log.debug("???  getRequestDispatcher()");
      return null;
   }


   public long getAvailable() {
      log.debug("???  getAvailable()");
      return 0;
   }


   public boolean isUnavailable() {
      log.debug("???  isUnavailable()");
      return false;
   }


   public void setId(String arg0) {
      log.debug("???  setId()");
   }


   public void setServletName(String arg0) {
      log.debug("???  setServletName()");
   }


   public void setDescriptions(DescriptionSet arg0) {
      log.debug("???  setDescriptions()");
   }


   public void setDisplayNames(DisplayNameSet arg0) {
      log.debug("???  setDisplayNames()");
   }


   public void setServletClass(String arg0) {
      log.debug("???  setServletClass()");
   }


   public void setAvailable(long arg0) {
      log.debug("???  setAvailable()");
   }


   public void postLoad(Object parameter) throws Exception {
   }


   public String toString() {
      return toString(0);
   }


   public String toString(int indent) {
      StringBuffer buffer = new StringBuffer(50);
      // StringUtils.newLine(buffer, indent);
      // buffer.append(getClass().toString());
      // buffer.append(":");
      // StringUtils.newLine(buffer, indent);
      // buffer.append("{");
      // StringUtils.newLine(buffer, indent);
      // buffer.append("id='");
      // buffer.append(id);
      // buffer.append("'");
      // StringUtils.newLine(buffer, indent);
      // buffer.append("servletName='");
      // buffer.append(servletName);
      // buffer.append("'");
      // StringUtils.newLine(buffer, indent);
      // buffer.append(((DescriptionSetImpl) descriptions).toString(indent));
      //
      // StringUtils.newLine(buffer, indent);
      // buffer.append(((DisplayNameSetImpl) displayNames).toString(indent));
      //
      // if (servletClass != null) {
      // buffer.append("servletClass='");
      // buffer.append(servletClass);
      // buffer.append("'");
      // }
      // else
      // if (jspFile != null) {
      // buffer.append("jspFile='");
      // buffer.append(jspFile);
      // buffer.append("'");
      // }
      // StringUtils.newLine(buffer, indent);
      // buffer.append(((org.apache.pluto.portalImpl.om.common.impl.ParameterSetImpl)
      // initParams)
      // .toString(indent));
      //
      // StringUtils.newLine(buffer, indent);
      // buffer
      // .append(((org.apache.pluto.portalImpl.om.common.impl.SecurityRoleRefSetImpl)
      // initSecurityRoleRefs)
      // .toString(indent));
      //
      // if (servletMapping != null) {
      // StringUtils.newLine(buffer, indent);
      // buffer.append("Linked ServletMapping:");
      // buffer.append(servletMapping.toString(indent + 2));
      // }
      // StringUtils.newLine(buffer, indent);
      // buffer.append("}");
      return buffer.toString();
   }

}
