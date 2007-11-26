/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.pluto.portalImpl.services.portletdefinitionregistry;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;
import org.apache.pluto.om.portlet.PortletApplicationDefinitionList;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.xml.sax.SAXException;

import com.finalist.cmsc.services.Properties;
import com.finalist.pluto.portalImpl.om.common.impl.DescriptionImpl;
import com.finalist.pluto.portalImpl.om.common.impl.DisplayNameImpl;
import com.finalist.pluto.portalImpl.om.common.impl.LanguageImpl;
import com.finalist.pluto.portalImpl.om.common.impl.ParameterImpl;
import com.finalist.pluto.portalImpl.om.common.impl.PreferenceImpl;
import com.finalist.pluto.portalImpl.om.common.impl.SecurityRoleRefImpl;
import com.finalist.pluto.portalImpl.om.portlet.impl.ContentTypeImpl;
import com.finalist.pluto.portalImpl.om.portlet.impl.PortletApplicationDefinitionImpl;
import com.finalist.pluto.portalImpl.om.portlet.impl.PortletApplicationDefinitionListImpl;
import com.finalist.pluto.portalImpl.om.portlet.impl.PortletDefinitionImpl;
import com.finalist.pluto.portalImpl.services.portletdefinitionregistry.rule.PortletApplicationRule;

public class PortletDefinitionRegistryServiceDigesterImpl extends PortletDefinitionRegistryService {
   private static Log log = LogFactory.getLog(PortletDefinitionRegistryServiceDigesterImpl.class);

   private String appName = "CMSC-APP";

   private PortletApplicationDefinitionListImpl registry;

   private Map<ObjectID, PortletDefinition> definitions = new HashMap<ObjectID, PortletDefinition>();;


   protected void init(ServletConfig config, Properties props) throws Exception {
      log.debug("PortletDefinitionRegistryServiceDigesterImpl");

      ServletContext context = config.getServletContext();

      registry = new PortletApplicationDefinitionListImpl();

      try {
         Digester digester = new Digester();
         digester.setValidating(false);

         digester.addRule("portlet-app", new PortletApplicationRule(appName));

         digester.addSetProperties("portlet-app", "id", "applicationIdentifier");

         digester.addObjectCreate("portlet-app/portlet", PortletDefinitionImpl.class);
         digester.addSetNext("portlet-app/portlet", "addPortletDefinition");

         digester.addSetProperties("portlet-app/portlet", "id", "portletIdentifier");
         digester.addBeanPropertySetter("portlet-app/portlet/portlet-name", "name");
         digester.addBeanPropertySetter("portlet-app/portlet/portlet-class", "className");
         digester.addBeanPropertySetter("portlet-app/portlet/expiration-cache", "expirationCache");
         digester.addBeanPropertySetter("portlet-app/portlet/resource-bundle", "resourceBundle");
         digester.addCallMethod("portlet-app/portlet/supported-locale", "addSupportedLocale", 0);

         digester.addObjectCreate("portlet-app/portlet/display-name", DisplayNameImpl.class);
         digester.addSetProperties("portlet-app/portlet/display-name", "lang", "language");
         digester.addBeanPropertySetter("portlet-app/portlet/display-name", "displayName");
         digester.addSetNext("portlet-app/portlet/display-name", "addDisplayName");

         digester.addObjectCreate("portlet-app/portlet/description", DescriptionImpl.class);
         digester.addSetProperties("portlet-app/portlet/description", "lang", "language");
         digester.addBeanPropertySetter("portlet-app/portlet/description", "description");
         digester.addSetNext("portlet-app/portlet/description", "addDescription");

         digester.addObjectCreate("portlet-app/portlet/init-param", ParameterImpl.class);
         digester.addBeanPropertySetter("portlet-app/portlet/init-param/name", "name");
         digester.addBeanPropertySetter("portlet-app/portlet/init-param/value", "value");
         digester.addSetNext("portlet-app/portlet/init-param", "addInitParameter");

         digester.addObjectCreate("portlet-app/portlet/init-param/description", DescriptionImpl.class);
         digester.addSetProperties("portlet-app/portlet/init-param/description", "lang", "language");
         digester.addBeanPropertySetter("portlet-app/portlet/init-param/description", "description");
         digester.addSetNext("portlet-app/portlet/init-param/description", "addDescription");

         digester.addObjectCreate("portlet-app/portlet/supports", ContentTypeImpl.class);
         digester.addBeanPropertySetter("portlet-app/portlet/supports/mime-type", "contentType");
         digester.addCallMethod("portlet-app/portlet/supports/portlet-mode", "addPortletMode", 0);
         digester.addSetNext("portlet-app/portlet/supports", "addContentType");

         digester.addObjectCreate("portlet-app/portlet/portlet-info", LanguageImpl.class);
         digester.addBeanPropertySetter("portlet-app/portlet/portlet-info/title", "title");
         digester.addBeanPropertySetter("portlet-app/portlet/portlet-info/short-title", "shortTitle");
         digester.addCallMethod("portlet-app/portlet/portlet-info/keywords", "setKeywords", 0,
               new Class[] { String.class });
         digester.addSetNext("portlet-app/portlet/portlet-info", "addLanguage");

         digester.addObjectCreate("portlet-app/portlet/portlet-preferences/preference", PreferenceImpl.class);
         digester.addBeanPropertySetter("portlet-app/portlet/portlet-preferences/preference/name", "name");
         digester.addBeanPropertySetter("portlet-app/portlet/portlet-preferences/preference/value", "value");
         digester.addCallMethod("portlet-app/portlet/portlet-preferences/preference/read-only", "setReadOnly", 0,
               new Class[] { Boolean.class });
         digester.addSetNext("portlet-app/portlet/portlet-preferences/preference", "addPreference");

         digester.addObjectCreate("portlet-app/portlet/security-role-ref", SecurityRoleRefImpl.class);
         digester.addBeanPropertySetter("portlet-app/portlet/security-role-ref/role-name", "roleName");
         digester.addBeanPropertySetter("portlet-app/portlet/security-role-ref/role-link", "roleLink");
         digester.addSetNext("portlet-app/portlet/security-role-ref", "addSecurityRoleRef");

         digester.addObjectCreate("portlet-app/portlet/security-role-ref/description", DescriptionImpl.class);
         digester.addSetProperties("portlet-app/portlet/security-role-ref/description", "lang", "language");
         digester.addBeanPropertySetter("portlet-app/portlet/security-role-ref/description", "description");
         digester.addSetNext("portlet-app/portlet/security-role-ref/description", "addDescription");

         Set webInfResources = context.getResourcePaths("/WEB-INF/");
         for (Iterator iter = webInfResources.iterator(); iter.hasNext();) {
            String resource = (String) iter.next();
            if (resource.startsWith("/WEB-INF/portlet")) {
               InputStream stream = context.getResourceAsStream(resource);
               PortletApplicationDefinitionImpl pd = (PortletApplicationDefinitionImpl) digester.parse(stream);
               registry.add(pd);

               for (Iterator<PortletApplicationDefinitionImpl> i = registry.iterator(); i.hasNext();) {
                  PortletApplicationDefinition application = i.next();
                  for (Iterator j = application.getPortletDefinitionList().iterator(); j.hasNext();) {
                     PortletDefinition portlet = (PortletDefinition) j.next();
                     definitions.put(portlet.getId(), portlet);
                  }
               }

               pd.postLoad(null);
            }
         }
      }
      catch (IOException e) {
         log.error("", e);
      }
      catch (SAXException e) {
         log.error("", e);
      }
   }


   public PortletApplicationDefinitionList getPortletApplicationDefinitionList() {
      return registry;
   }


   public PortletDefinition getPortletDefinition(ObjectID id) {
      PortletDefinition pd = definitions.get(id);
      return pd;
   }

}
