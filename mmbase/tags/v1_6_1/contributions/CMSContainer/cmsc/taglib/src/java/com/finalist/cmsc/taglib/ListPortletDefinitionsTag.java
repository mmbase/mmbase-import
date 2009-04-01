/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.taglib;

import java.util.*;

import org.apache.pluto.om.portlet.*;

import com.finalist.pluto.portalImpl.services.portletdefinitionregistry.PortletDefinitionRegistry;

/**
 * List the available portlet definitions
 * 
 * @author Wouter Heijke
 * @version $Revision: 1.4 $
 */
public class ListPortletDefinitionsTag extends AbstractListTag<PortletDefinition> {

   @Override
   public List<PortletDefinition> getList() {
      List<PortletDefinition> portlets = new ArrayList<PortletDefinition>();
      PortletApplicationDefinitionList registry = PortletDefinitionRegistry.getPortletApplicationDefinitionList();

      for (Iterator<PortletApplicationDefinition> i = registry.iterator(); i.hasNext();) {
         PortletApplicationDefinition application = i.next();
         for (Iterator<PortletDefinition> j = application.getPortletDefinitionList().iterator(); j.hasNext();) {
            PortletDefinition portlet = j.next();
            portlets.add(portlet);
         }
      }
      return portlets;
   }

}
