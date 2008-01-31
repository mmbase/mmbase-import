/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.community.forms;

import org.springframework.web.struts.ActionSupport;
import org.springframework.web.context.WebApplicationContext;
import com.finalist.cmsc.services.community.security.AuthenticationService;
import com.finalist.cmsc.services.community.security.AuthorityService;
import com.finalist.cmsc.services.community.person.PersonService;
import com.finalist.cmsc.services.community.preferences.PreferenceService;

/**
 * @author Remco Bos
 */
public class AbstractCommunityAction extends ActionSupport {

	protected static final String ACTION_ADD = "add";

	protected static final String ACTION_EDIT = "edit";

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
}
