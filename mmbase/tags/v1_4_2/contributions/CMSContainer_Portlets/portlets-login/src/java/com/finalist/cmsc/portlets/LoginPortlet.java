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
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.finalist.cmsc.services.community.Community;

/**
 * Login portlet
 * 
 * @author Remco Bos
 */
public class LoginPortlet extends CmscPortlet {

	private static final String ACEGI_SECURITY_FORM_USERNAME_KEY = "j_username";

	private static final String ACEGI_SECURITY_FORM_PASSWORD_KEY = "j_password";

	private static final Log log = LogFactory.getLog(LoginPortlet.class);

	public void processView(ActionRequest request, ActionResponse response) throws PortletException, IOException {
		String action = request.getParameter("action");
		if ("login".equals(action)) {
			String userName = request.getParameter(ACEGI_SECURITY_FORM_USERNAME_KEY);
			String password = request.getParameter(ACEGI_SECURITY_FORM_PASSWORD_KEY);
			if (!StringUtils.isBlank(userName) && !StringUtils.isBlank(password)) {
			    Community.login(userName, password);
			}

			if (Community.isAuthenticated()) {
				log.info(String.format("Login successful for user %s", userName));
			} else {
				log.info(String.format("Login failed for user %s", userName));
				response.setRenderParameter("errormessage", "login.failed");
			}

		} else if ("logout".equals(action)) {
			Community.logout();
		} else if ("send_password".equals(action)) {
            throw new UnsupportedOperationException("Community module does not have all methods yet");
//            String username = request.getParameter("username");
//            if (!StringUtils.isBlank(username)) {
//            }
		}
		else {
		    // Unknown
			log.error(String.format("Unknown action '%s'", action));
		}
	}

	@Override
	protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
	    String error = request.getParameter("errormessage");
	    if (!StringUtils.isBlank(error)) {
	        request.setAttribute("errormessage", error);
	    }
	    
		String template = null;
		if (Community.isAuthenticated()) {
			template = "login/logout.jsp";
		} else {
			template = "login/login.jsp";
	        String action = request.getParameter("action");
	        if (!StringUtils.isBlank(action) && "send_password".equals(action)) {
	            template = "login/send_password.jsp";
	        }
		}
		doInclude("view", template, request, response);
	}
}
