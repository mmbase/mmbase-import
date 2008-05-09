/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.pluto.portalImpl.services.portletdefinitionregistry.rule;

import org.apache.commons.digester.Rule;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;

public class PortletRule extends Rule {
   protected final static Log log = LogFactory.getLog(PortletRule.class);

   private PortletApplicationDefinition app;


   public PortletRule(PortletApplicationDefinition app) {
      this.app = app;
   }


   public PortletRule() {
   }

}