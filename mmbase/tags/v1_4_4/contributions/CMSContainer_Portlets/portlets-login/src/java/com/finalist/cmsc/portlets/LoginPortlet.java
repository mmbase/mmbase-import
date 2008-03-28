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
   protected static final String ACTION_PARAMETER = "action";

   private static final String ACEGI_SECURITY_FORM_USERNAME_KEY = "j_username";
   private static final String ACEGI_SECURITY_FORM_PASSWORD_KEY = "j_password";

   private static final Log log = LogFactory.getLog(LoginPortlet.class);

   @Override
   public void processView(ActionRequest request, ActionResponse response) throws PortletException, IOException {
      String action = request.getParameter(ACTION_PARAMETER);
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
      } else {
         // Unknown
         log.error(String.format("Unknown action '%s'", action));
      }
   }

   @Override
   protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {

      String template;

      String error = request.getParameter("errormessage");
      if (!StringUtils.isBlank(error)) {
         request.setAttribute("errormessage", error);
      }

      if (Community.isAuthenticated()) {
         template = "login/logout.jsp";
      } else {
         template = "login/login.jsp";
      }

      doInclude("view", template, request, response);
   }
}
