/*
 * 
 * This software is OSI Certified Open Source Software. OSI Certified is a certification mark of the Open Source
 * Initiative.
 * 
 * The license (Mozilla version 1.0) can be read at the MMBase site. See http://www.MMBase.org/license
 * 
 */
package com.finalist.cmsc.community.forms;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.struts.ActionSupport;

import com.finalist.cmsc.services.community.person.PersonService;
import com.finalist.cmsc.services.community.preferences.PreferenceService;
import com.finalist.cmsc.services.community.security.AuthenticationService;
import com.finalist.cmsc.services.community.security.AuthorityService;

/**
 * @author Remco Bos
 */
public class AbstractCommunityAction extends ActionSupport {

   protected static final String SUCCESS = "success";

   protected static final String CANCEL = "cancel";

   protected AuthenticationService getAuthenticationService() {
      WebApplicationContext ctx = getWebApplicationContext();
      return (AuthenticationService) ctx.getBean("authenticationService");
   }

   protected AuthorityService getAuthorityService() {
      WebApplicationContext ctx = getWebApplicationContext();
      return (AuthorityService) ctx.getBean("authorityService");
   }

   protected PersonService getPersonService() {
      WebApplicationContext ctx = getWebApplicationContext();
      return (PersonService) ctx.getBean("personService");
   }

   protected PreferenceService getPreferenceService() {
      WebApplicationContext ctx = getWebApplicationContext();
      return (PreferenceService) ctx.getBean("preferenceService");
   }

   protected void removeFromSession(HttpServletRequest request, ActionForm form) {
      HttpSession session = request.getSession();
      for (Enumeration < String > iter = session.getAttributeNames(); iter.hasMoreElements();) {
         String name = iter.nextElement();
         Object value = session.getAttribute(name);
         if (form == value) { // same reference
            session.removeAttribute(name);
         }
      }
   }
}
