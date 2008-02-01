/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.portlets;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.finalist.cmsc.services.community.Community;

/**
 * Login / Logout portlet
 * 
 * @author Menno Menninga / Remco Bos
 * @version $Revision: 1.5 $
 */
public class LoginPortlet extends CmscPortlet {

	private static final Log log = LogFactory.getLog(LoginPortlet.class);

	public void processView(ActionRequest request, ActionResponse response)
			throws PortletException, IOException {
		String action = request.getParameter("action");
		if ("login".equals(action)) {
			if (!Community.loginUser(request, response)) {
				response.setPortletMode(PortletMode.VIEW);
			}
		} else if ("logout".equals(action)) {
			if (!Community.logoutUser(request, response)) {
				response.setPortletMode(PortletMode.VIEW);
			}
		} else {
			response.setPortletMode(PortletMode.VIEW);
			log.error(String.format("Unknown action '%s'", action));
		}
	}
}
