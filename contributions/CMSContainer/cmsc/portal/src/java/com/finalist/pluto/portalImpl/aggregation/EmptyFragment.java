/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.pluto.portalImpl.aggregation;

import javax.servlet.ServletConfig;

import org.apache.pluto.om.window.PortletWindow;

import com.finalist.cmsc.beans.om.Portlet;
import com.finalist.cmsc.beans.om.PortletDefinition;
import com.finalist.cmsc.services.sitemanagement.SiteModelManager;
import com.finalist.pluto.portalImpl.om.common.impl.PreferenceSetImpl;

/**
 * Fragment for creating and adding portlets to a Screen
 * 
 * @author Wouter Heijke
 * @version $Revision: 1.2 $
 */
public class EmptyFragment extends PortletFragment {

	public EmptyFragment(ServletConfig config, Fragment parent, String layoutId, Portlet portlet, PortletDefinition definition) throws Exception {
		super(config, parent, layoutId, portlet, definition, null);

		if (portlet != null) {
			PortletWindow pw = getPortletWindow();
			PreferenceSetImpl ps = (PreferenceSetImpl) pw.getPortletEntity().getPreferenceSet();
            setDefaultPreferences(parent, portlet, ps);
		}
	}
}
