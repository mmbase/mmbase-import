package com.finalist.pluto.portalImpl.om.servlet.impl;

import java.util.*;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pluto.om.common.Description;
import org.apache.pluto.om.common.DescriptionSet;
import org.apache.pluto.om.common.DisplayName;
import org.apache.pluto.om.common.DisplayNameSet;
import org.apache.pluto.om.common.ParameterSet;
import org.apache.pluto.om.common.SecurityRoleSet;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;
import org.apache.pluto.om.servlet.ServletDefinitionList;
import org.apache.pluto.om.servlet.WebApplicationDefinition;
import org.apache.pluto.util.StringUtils;

import com.finalist.pluto.portalImpl.om.common.Support;
import com.finalist.pluto.portalImpl.om.common.impl.DescriptionSetImpl;
import com.finalist.pluto.portalImpl.om.common.impl.DisplayNameSetImpl;
import com.finalist.pluto.portalImpl.om.common.impl.ParameterSetImpl;
import com.finalist.pluto.portalImpl.om.common.impl.SecurityRoleSetImpl;
import com.finalist.pluto.portalImpl.util.ObjectID;

public class WebApplicationDefinitionImpl implements WebApplicationDefinition, java.io.Serializable, Support {
   private static Log log = LogFactory.getLog(WebApplicationDefinitionImpl.class);

   private ObjectID objectId = null;

   private SecurityRoleSet securityRoles = new SecurityRoleSetImpl();

   private DescriptionSet descriptions = new DescriptionSetImpl();

   private DisplayNameSet displayNames = new DisplayNameSetImpl();

   private ServletDefinitionList servlets = new ServletDefinitionListImpl();

   private ParameterSet initParams = new ParameterSetImpl();

   private String contextPath; // Config.getParameters().getString("webapp.contextname");


   public ObjectID getId() {
      log.debug("???  getId()");
      if (objectId == null) {
         objectId = ObjectID.createFromString("CMSC-WEBAPP");
      }
      return objectId;
   }


   public DisplayName getDisplayName(Locale locale) {
      return displayNames.get(locale);
   }


   public Description getDescription(Locale locale) {
      return descriptions.get(locale);
   }


   public ParameterSet getInitParameterSet() {
      return initParams;
   }


   public ServletDefinitionList getServletDefinitionList() {
      return servlets;
   }


   public ServletContext getServletContext(ServletContext servletContext) {
      return servletContext.getContext(contextPath);
   }


   public String getContextRoot() {
      return contextPath;
   }


   public void setContextRoot(String contextPath) {
      this.contextPath = contextPath;
   }


   public SecurityRoleSet getSecurityRoles() {
      return securityRoles;
   }


   public void postLoad(Object parameter) throws Exception {
      Vector structure = (Vector) parameter;
      PortletApplicationDefinition portletApplication = (PortletApplicationDefinition) structure.get(0);

      ((Support) portletApplication).postLoad(this);

      ((Support) servlets).postLoad(this);

      ((Support) descriptions).postLoad(parameter);
      ((Support) displayNames).postLoad(parameter);
   }


   public String toString() {
      return toString(0);
   }


   public String toString(int indent) {
      StringBuffer buffer = new StringBuffer(50);
      StringUtils.newLine(buffer, indent);
      buffer.append(getClass().toString());
      buffer.append(":");
      StringUtils.newLine(buffer, indent);
      buffer.append("{");
      StringUtils.newLine(buffer, indent);
      buffer.append("id='");
      buffer.append(objectId);
      buffer.append("'");

      StringUtils.newLine(buffer, indent);
      buffer.append(((DisplayNameSetImpl) displayNames).toString(indent));

      StringUtils.newLine(buffer, indent);
      buffer.append(((DescriptionSetImpl) descriptions).toString(indent));

      StringUtils.newLine(buffer, indent);
      buffer.append(((ParameterSetImpl) initParams).toString(indent));

      StringUtils.newLine(buffer, indent);
      buffer.append(((ServletDefinitionListImpl) servlets).toString(indent));

      StringUtils.newLine(buffer, indent);
      buffer.append("contextPath='");
      buffer.append(contextPath);
      buffer.append("'");
      StringUtils.newLine(buffer, indent);
      buffer.append("}");
      return buffer.toString();
   }

}
