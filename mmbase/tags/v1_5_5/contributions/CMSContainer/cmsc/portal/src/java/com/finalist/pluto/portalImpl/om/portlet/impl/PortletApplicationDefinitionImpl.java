/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.pluto.portalImpl.om.portlet.impl;

import java.io.Serializable;
import java.util.*;

import javax.portlet.PortletMode;

import org.apache.pluto.om.portlet.*;
import org.apache.pluto.om.servlet.WebApplicationDefinition;
import org.apache.pluto.util.StringUtils;

import com.finalist.pluto.portalImpl.om.common.Support;
import com.finalist.pluto.portalImpl.om.servlet.impl.WebApplicationDefinitionImpl;
import com.finalist.pluto.portalImpl.util.ObjectID;

public class PortletApplicationDefinitionImpl implements PortletApplicationDefinition, Serializable, Support {

   private String GUID;

   private String appId;

   private String version;

   private List<PortletMode> customPortletMode = new ArrayList<PortletMode>();

   private List customPortletState = new ArrayList();

   private List userAttribute = new ArrayList();

   private List securityConstraint = new ArrayList();

   private PortletDefinitionList portlets = new PortletDefinitionListImpl();

   private WebApplicationDefinition webApplication = new WebApplicationDefinitionImpl();

   private ObjectID id;

   private String contextPath;


   public PortletApplicationDefinitionImpl() {
   }


   public ObjectID getId() {
      if (id == null) {
         id = ObjectID.createFromString(getGUID());
      }
      return id;
   }


   public String getVersion() {
      return version;
   }


   public PortletDefinitionList getPortletDefinitionList() {
      return portlets;
   }


   public WebApplicationDefinition getWebApplicationDefinition() {
      return webApplication;
   }


   // setters for digester

   public void addPortletDefinition(PortletDefinition def) {
      ((PortletDefinitionImpl) def).setPortletApplicationDefinition(this);
      ((PortletDefinitionListImpl) portlets).add(def);
   }


   // internal methods.

   private String getGUID() {
      if (GUID == null) {
         GUID = "";
         String id = "";

         if (webApplication != null) {
            id = webApplication.getContextRoot();
         }
         else {
            id = contextPath;
         }

         if (id != null) {
            if (id.startsWith("/")) {
               id = id.substring(id.indexOf("/") + 1);
            }

            GUID += id;
         }
      }
      return GUID;
   }


   private void setContextRoot(String contextRoot) {
      // Test for IBM WebSphere
      if (contextRoot != null && contextRoot.endsWith(".war")) {
         contextRoot = contextRoot.substring(0, contextRoot.length() - 4);
      }
      this.contextPath = contextRoot;
   }


   // additional methods.

   public String getAppId() {
      return appId;
   }


   public void setAppId(String appId) {
      this.appId = appId;
   }


   public void setVersion(String version) {
      this.version = version;
   }


   // not yet fully supported:
   public Collection getCustomPortletMode() {
      return customPortletMode;
   }


   public void setCustomPortletMode(Collection customPortletMode) {
      this.customPortletMode = (ArrayList) customPortletMode;
   }


   public Collection getCustomPortletState() {
      return customPortletState;
   }


   public void setCustomPortletState(Collection customPortletState) {
      this.customPortletState = (ArrayList) customPortletState;
   }


   public Collection getUserAttribute() {
      return userAttribute;
   }


   public void setUserAttribute(Collection userAttribute) {
      this.userAttribute = (ArrayList) userAttribute;
   }


   public Collection getSecurityConstraint() {
      return securityConstraint;
   }


   public void setSecurityConstraint(Collection securityConstraint) {
      this.securityConstraint = (ArrayList) securityConstraint;
   }


   protected void setWebApplicationDefinition(WebApplicationDefinition webApplication) {
      this.webApplication = webApplication;
   }


   public void postLoad(Object parameter) throws Exception {
      ((Support) portlets).postLoad(parameter);
   }


   // internal methods used for debugging purposes only

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
      buffer.append("objectID='");
      buffer.append(getId().toString());
      buffer.append("'");
      StringUtils.newLine(buffer, indent);
      buffer.append("GUID='");
      buffer.append(GUID);
      buffer.append("'");
      StringUtils.newLine(buffer, indent);
      buffer.append("version='");
      buffer.append(version);
      buffer.append("'");

      Iterator iterator = portlets.iterator();
      if (iterator.hasNext()) {
         StringUtils.newLine(buffer, indent);
         buffer.append("Portlets:");
      }
      while (iterator.hasNext()) {
         buffer.append(((PortletDefinitionImpl) iterator.next()).toString(indent + 2));
      }
      if (webApplication != null) {
         StringUtils.newLine(buffer, indent);
         buffer.append("webApplication:");
         buffer.append(((WebApplicationDefinitionImpl) webApplication).toString(indent + 2));
      }
      StringUtils.newLine(buffer, indent);
      buffer.append("}");
      return buffer.toString();
   }

}
