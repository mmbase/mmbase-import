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
 * @version $Revision: 1.1 $
 */
public class ListPortletDefinitionsTag extends AbstractListTag {

	public List getList() {
		List portlets = new ArrayList();
		PortletApplicationDefinitionList registry = PortletDefinitionRegistry.getPortletApplicationDefinitionList();
		
		for (Iterator i = registry.iterator(); i.hasNext();) {
			PortletApplicationDefinition application = (PortletApplicationDefinition) i.next();
			for (Iterator j = application.getPortletDefinitionList().iterator(); j.hasNext();) {
				PortletDefinition portlet = (PortletDefinition) j.next();
				portlets.add(portlet);
			}
		}
        return portlets;
	}

}
