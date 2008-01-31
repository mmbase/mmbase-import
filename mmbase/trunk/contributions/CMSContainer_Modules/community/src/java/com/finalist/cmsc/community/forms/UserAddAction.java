/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */package com.finalist.cmsc.community.forms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.finalist.cmsc.services.community.person.PersonService;
import com.finalist.cmsc.services.community.security.AuthenticationService;

/**
 * Add a Authentication and Person
 * 
 * @author Remco Bos
 * @author Wouter Heijke
 */
public class UserAddAction extends AbstractCommunityAction {

	private static Log log = LogFactory.getLog(UserAddInitAction.class);

	public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws Exception {

		if (!isCancelled(httpServletRequest)) {
			UserForm userForm = (UserForm) actionForm;

			AuthenticationService as = getAuthenticationService();
			PersonService ps = getPersonService();

			if (userForm.getAction().equalsIgnoreCase(ACTION_ADD)) {
				Long check1 = as.getAuthenticationIdForUserId(userForm.getEmail());
				if (check1 == null) {
					as.createAuthentication(userForm.getEmail(), userForm.getPassword());
					Long check2 = as.getAuthenticationIdForUserId(userForm.getEmail());
					if (check2 != null) {

						ps.createPerson(userForm.getVoornaam(), userForm.getTussenVoegsels(), userForm.getAchterNaam(), userForm
								.getEmail());
					} else {
						log.info("add check2 failed");
					}
				} else {
					log.info("add check1 failed");
				}
			} else if (userForm.getAction().equalsIgnoreCase(ACTION_EDIT)) {
				Long check1 = as.getAuthenticationIdForUserId(userForm.getEmail());
				if (check1 == null) {
					// as.updateAuthentication(userId, oldPassword, newPassword)

				} else {
					log.info("edit check1 failed");
				}

			} else {
				log.info("action failed");
			}
		} else {
			log.info("cancelled");
		}

		removeFromSession(httpServletRequest, actionForm);

		return actionMapping.findForward(SUCCESS);
	}
}
