/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.pluto.portalImpl.om.portlet.impl;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pluto.om.common.Description;
import org.apache.pluto.om.common.DescriptionSet;
import org.apache.pluto.om.common.DisplayName;
import org.apache.pluto.om.common.DisplayNameSet;
import org.apache.pluto.om.common.Language;
import org.apache.pluto.om.common.LanguageSet;
import org.apache.pluto.om.common.Parameter;
import org.apache.pluto.om.common.ParameterSet;
import org.apache.pluto.om.common.Preference;
import org.apache.pluto.om.common.PreferenceSet;
import org.apache.pluto.om.common.SecurityRoleRef;
import org.apache.pluto.om.common.SecurityRoleRefSet;
import org.apache.pluto.om.portlet.ContentType;
import org.apache.pluto.om.portlet.ContentTypeSet;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.om.portlet.PortletDefinitionCtrl;
import org.apache.pluto.om.servlet.ServletDefinition;
import org.apache.pluto.util.StringUtils;

import com.finalist.pluto.portalImpl.om.common.Support;
import com.finalist.pluto.portalImpl.om.common.impl.DescriptionSetImpl;
import com.finalist.pluto.portalImpl.om.common.impl.DisplayNameSetImpl;
import com.finalist.pluto.portalImpl.om.common.impl.LanguageSetImpl;
import com.finalist.pluto.portalImpl.om.common.impl.ParameterSetImpl;
import com.finalist.pluto.portalImpl.om.common.impl.PreferenceSetImpl;
import com.finalist.pluto.portalImpl.om.common.impl.SecurityRoleRefSetImpl;
import com.finalist.pluto.portalImpl.om.servlet.impl.ServletDefinitionImpl;
import com.finalist.pluto.portalImpl.util.ObjectID;

public class PortletDefinitionImpl implements PortletDefinition, PortletDefinitionCtrl, Serializable, Support {
   private static Log log = LogFactory.getLog(PortletDefinitionImpl.class);

   private PortletApplicationDefinition application;

   private LanguageSet resources = new LanguageSetImpl();

   private ClassLoader classLoader;

   private String className;

   private ContentTypeSet contentTypes = new ContentTypeSetImpl();

   private DescriptionSet descriptions = new DescriptionSetImpl();

   private DisplayNameSet displayNames = new DisplayNameSetImpl();

   private String expirationCache;

   public String id = "";

   private ParameterSet initParams = new ParameterSetImpl();

   private SecurityRoleRefSet initSecurityRoleRefs = new SecurityRoleRefSetImpl();

   private String name;

   private ObjectID objectId;

   private PreferenceSet preferences = new PreferenceSetImpl();

   // private PortletInfoImpl portletInfo;
   private String resourceBundle;

   private ServletDefinition servlet = new ServletDefinitionImpl();

   // contains Locale objects
   private List<Locale> supportedLocales = new ArrayList<Locale>();


   public String getClassName() {
      return className;
   }


   public void setClassName(String className) {
      this.className = className;
   }


   public ContentTypeSet getContentTypeSet() {
      return contentTypes;
   }


   public String getExpirationCache() {
      return expirationCache;
   }


   public ObjectID getId() {
      if (objectId == null) {
         objectId = ObjectID.createFromString(getGUID());
      }

      return objectId;
   }


   public ParameterSet getInitParameterSet() {
      return initParams;
   }


   public SecurityRoleRefSet getInitSecurityRoleRefSet() {
      return initSecurityRoleRefs;
   }


   public void setInitSecurityRoleRefSet(SecurityRoleRefSet initSecurityRoleRefSet) {
      this.initSecurityRoleRefs = initSecurityRoleRefSet;
   }


   public LanguageSet getLanguageSet() {
      ((LanguageSetImpl) resources).setClassLoader(this.getPortletClassLoader());
      return resources;
   }


   public String getName() {
      return name;
   }


   public void setName(String name) {
      this.name = name;
   }


   public PortletApplicationDefinition getPortletApplicationDefinition() {
      return application;
   }


   public ClassLoader getPortletClassLoader() {
      return classLoader;
   }


   public void setPortletClassLoader(ClassLoader portletClassLoader) {
      this.classLoader = portletClassLoader;
   }


   public PreferenceSet getPreferenceSet() {
      return preferences;
   }


   public void setPreferenceSet(PreferenceSet preferenceSet) {
      this.preferences = preferenceSet;
   }


   public ServletDefinition getServletDefinition() {
      return servlet;
   }


   public Description getDescription(Locale locale) {
      return descriptions.get(locale);
   }


   public DisplayName getDisplayName(Locale locale) {
      return displayNames.get(locale);
   }


   public void setId(String arg0) {
      // TODO
   }


   public void setDescriptions(DescriptionSet descriptions) {
      this.descriptions = descriptions;
   }


   public void setDisplayNames(DisplayNameSet displayNames) {
      this.displayNames = displayNames;
   }


   public void store() throws IOException {
      // not supported
   }


   private String getGUID() {
      String portletID = "";
      if (getName() != null)
         portletID += getName();

      String applicationName = application.getId().toString();
      if (applicationName != null && !"".equals(applicationName)) {
         portletID = applicationName + "." + portletID;
      }
      return portletID;
   }


   // Digester methods

   public void addDisplayName(DisplayName displayname) {
      ((DisplayNameSetImpl) displayNames).add(displayname);
   }


   public void addDescription(Description description) {
      ((DescriptionSetImpl) descriptions).add(description);
   }


   public void addInitParameter(Parameter parameter) {
      ((ParameterSetImpl) initParams).add(parameter);
   }


   public void addContentType(ContentType contenttype) {
      ((ContentTypeSetImpl) contentTypes).add(contenttype);
   }


   public void addSupportedLocale(String locale) {
      // parse locale String
      StringTokenizer tokenizer = new StringTokenizer(locale, "_");
      String[] localeDef = new String[3];
      for (int i = 0; i < 3; i++) {
         if (tokenizer.hasMoreTokens()) {
            localeDef[i] = tokenizer.nextToken();
         }
         else {
            localeDef[i] = "";
         }
      }
      supportedLocales.add(new java.util.Locale(localeDef[0], localeDef[1], localeDef[2]));
   }


   public void addLanguage(Language language) {
      ((LanguageSetImpl) resources).add(language);
   }


   public void addSecurityRoleRef(SecurityRoleRef security) {
      initSecurityRoleRefs.add(security);
   }


   public void addPreference(Preference preference) {
      ((PreferenceSetImpl) preferences).add(preference);
   }


   public void setExpirationCache(String expirationCache) {
      this.expirationCache = expirationCache;
   }


   protected void setPortletApplicationDefinition(PortletApplicationDefinition application) {
      this.application = application;
   }


   public void setResourceBundle(String resourceBundle) {
      this.resourceBundle = resourceBundle;
   }


   protected void setServletDefinition(ServletDefinition servlet) {
      this.servlet = servlet;
   }


   public void postLoad(Object parameter) throws Exception {
      ((Support) contentTypes).postLoad(this);

      if (resources == null) {
         resources = new LanguageSetImpl();
      }
      if (resourceBundle != null) {
         ((LanguageSetImpl) resources).setResources(resourceBundle);
      }
      ((Support) resources).postLoad(this.supportedLocales);
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
      buffer.append("objectID='");
      buffer.append(getId());
      buffer.append("'");
      StringUtils.newLine(buffer, indent);
      buffer.append("guid='");
      buffer.append(getGUID());
      buffer.append("'");
      StringUtils.newLine(buffer, indent);
      buffer.append("id='");
      buffer.append(id);
      buffer.append("'");
      StringUtils.newLine(buffer, indent);
      buffer.append("name='");
      buffer.append(name);
      buffer.append("'");

      StringUtils.newLine(buffer, indent);
      buffer.append(((LanguageSetImpl) resources).toString(indent));

      StringUtils.newLine(buffer, indent);
      buffer.append(((ParameterSetImpl) initParams).toString(indent));

      StringUtils.newLine(buffer, indent);
      buffer.append(((PreferenceSetImpl) preferences).toString(indent));

      StringUtils.newLine(buffer, indent);
      buffer.append(((SecurityRoleRefSetImpl) initSecurityRoleRefs).toString(indent));

      StringUtils.newLine(buffer, indent);
      buffer.append(((ContentTypeSetImpl) contentTypes).toString(indent));

      StringUtils.newLine(buffer, indent);
      buffer.append(((DescriptionSetImpl) descriptions).toString(indent));

      StringUtils.newLine(buffer, indent);
      buffer.append(((DisplayNameSetImpl) displayNames).toString(indent));

      if (servlet != null) {
         StringUtils.newLine(buffer, indent);
         buffer.append("servlet:");
         buffer.append(((ServletDefinitionImpl) servlet).toString(indent + 2));
      }
      StringUtils.newLine(buffer, indent);
      buffer.append("}");
      return buffer.toString();
   }


   // additional methods.

   public Collection getContentTypes() {
      return (ContentTypeSetImpl) contentTypes;
   }


   public Collection getCastorDisplayNames() {
      return (DisplayNameSetImpl) displayNames;
   }


   public Collection getCastorDescriptions() {
      return (DescriptionSetImpl) descriptions;
   }


   public Collection getDescriptions() {
      return (DescriptionSetImpl) descriptions;
   }


   public SecurityRoleRefSet getInitSecurityRoleRefs() {
      return initSecurityRoleRefs;
   }


   public PreferenceSet getPreferences() {
      return preferences;
   }


   public LanguageSet getResources() {
      return resources;
   }


   public String getResourceBundle() {
      return this.resourceBundle;
   }


   public Collection<Locale> getSupportedLocales() {
      return supportedLocales;
   }


   public void setContentTypes(ContentTypeSet castorContentTypes) {
      this.contentTypes = castorContentTypes;
   }


   public void setInitParams(ParameterSet castorInitParams) {
      this.initParams = castorInitParams;
   }


   public void setInitSecurityRoleRefs(SecurityRoleRefSet castorInitSecurityRoleRefs) {
      this.initSecurityRoleRefs = castorInitSecurityRoleRefs;
   }


   public void setPreferences(PreferenceSet castorPreferences) {
      this.preferences = castorPreferences;
   }


   public void setResources(LanguageSet resources) {
      this.resources = resources;
   }


   public void setSupportedLocales(Collection<Locale> supportedLocales) {
      this.supportedLocales = (ArrayList<Locale>) supportedLocales;
   }

}
