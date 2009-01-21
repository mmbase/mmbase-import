/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.pluto.portalImpl.services.portletdefinitionregistry.rule;

import org.apache.commons.digester.Rule;
import org.xml.sax.Attributes;

import com.finalist.pluto.portalImpl.om.portlet.impl.PortletApplicationDefinitionImpl;

public class PortletApplicationRule extends Rule {
   protected String appName;


   public PortletApplicationRule(String appName) {
      this.appName = appName;
   }


   public void begin(String namespace, String name, Attributes attributes) throws Exception {
      PortletApplicationDefinitionImpl app = new PortletApplicationDefinitionImpl();
      digester.push(app);
   }


   public void end(String arg0, String arg1) throws Exception {
      digester.pop();
   }
}
