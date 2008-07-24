/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */package com.finalist.cmsc.community.forms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.finalist.cmsc.services.community.person.Person;
import com.finalist.cmsc.services.community.person.PersonService;
import com.finalist.cmsc.services.community.security.Authentication;
import com.finalist.cmsc.services.community.security.AuthenticationService;

/**
 * Add an Authentication and Person
 * 
 * @author Remco Bos
 * @author Wouter Heijke
 */
public class UserAddAction extends AbstractCommunityAction {

	private static Log log = LogFactory.getLog(UserAddInitAction.class);

	@Override
	public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws Exception {
		ActionMessages errors = new ActionMessages();
		Long authId = null;
		String tmpForward = (String) httpServletRequest.getSession().getAttribute("forward");
		if (!isCancelled(httpServletRequest)) {
			UserForm userForm = (UserForm) actionForm;

			// String accountName = userForm.getEmail();
			String accountName = userForm.getAccount();

			// AuthorityService aus = getAuthorityService(); //Never used (yet).
			AuthenticationService as = getAuthenticationService();
			PersonService ps = getPersonService();

			if (userForm.getAction().equalsIgnoreCase(UserForm.ACTION_ADD)) {

				Long authenticationId = as.getAuthenticationIdForUserId(userForm.getEmail());
				if (authenticationId == null) {
					Authentication authentication = as.createAuthentication(userForm.getEmail(), userForm.getPasswordText());
					if (authentication.getId() != null) {
						authId = authentication.getId();
						Person person = ps
								.createPerson(userForm.getFirstName(), userForm.getPrefix(), userForm.getLastName(), authentication.getId());
						person.setEmail(userForm.getEmail());
						ps.updatePerson(person);

					} else {
						log.info("add authenticationId failed");
					}
				} else {
					errors.add("email", new ActionMessage("userform.email.exist"));
					log.info("add check1 failed for: " + accountName);
				}

			} else if (userForm.getAction().equalsIgnoreCase(UserForm.ACTION_EDIT)) {
				Long authenticationId = as.getAuthenticationIdForUserId(accountName);
				if (authenticationId != null) {
					String newPassword1 = userForm.getPasswordText();
					String newPassword2 = userForm.getPasswordConfirmation();
					if (StringUtils.isNotBlank(newPassword1) && StringUtils.isNotBlank(newPassword2)) {
						if (newPassword1.equals(newPassword2)) {
							as.updateAuthenticationPassword(accountName, newPassword1);
						}
					}

					// First retrieve the right person object from the database
					Person person = ps.getPersonByAuthenticationId(authenticationId);

					if (person == null) { // User did not exists, so create
						// it.
						person = new Person();
						person.setAuthenticationId(authenticationId);
					}

					// Also save other fields entered in the form to the right
					// person
					// object
					person.setFirstName(userForm.getFirstName());
					person.setInfix(userForm.getPrefix());
					person.setLastName(userForm.getLastName());
					person.setEmail(userForm.getEmail());

					// Store the new person data to the database again.
					ps.updatePerson(person);

				} else {
					log.info("edit check1 failed for: " + accountName);
				}

			} else {
				log.info("action failed");
			}
		} else {
			log.info("cancelled");
			ActionForward ret;
			String newsletterId = (String) httpServletRequest.getSession().getAttribute("newsletterId");
			if (StringUtils.isNotBlank(tmpForward)) {
				ret = new ActionForward("/editors/newsletter/NewsletterPublicationSubscriberSearch.do?newsletterId="
						+ httpServletRequest.getSession().getAttribute("newsletterId"));
				ret.setRedirect(true);
			} else {
				ret = actionMapping.findForward(SUCCESS);
			}
			return ret;
		}
		if (errors.size() > 0) {
			ActionForward ret = new ActionForward("/editors/community/user.jsp");
			saveErrors(httpServletRequest, errors);
			return ret;
		} else {
			if ("newslettersubscribers".equals(tmpForward)) {
				ActionForward ret = new ActionForward(actionMapping.findForward("newslettersubscribers").getPath() + "?newsletterId="
						+ httpServletRequest.getSession().getAttribute("newsletterId"));
				httpServletRequest.getSession().removeAttribute("forward");
				httpServletRequest.getSession().removeAttribute("newsletterId");
				ret.setRedirect(true);
				return ret;
			} else if ("newslettersubscription".equals(tmpForward)) {
				ActionForward ret;
				if (((UserForm) actionForm).getAction().equalsIgnoreCase(UserForm.ACTION_ADD)) {
					ret = new ActionForward(actionMapping.findForward("newslettersubscription").getPath() + "?newsletterId="
							+ httpServletRequest.getSession().getAttribute("newsletterId") + "&authid=" + authId);
					ret.setRedirect(true);
				} else {
					ret = new ActionForward(actionMapping.findForward("newslettersubscribers").getPath() + "?newsletterId="
							+ httpServletRequest.getSession().getAttribute("newsletterId"));
				}

				httpServletRequest.getSession().removeAttribute("forward");
				httpServletRequest.getSession().removeAttribute("newsletterId");
				ret.setRedirect(true);
				return ret;
			}
		}
		removeFromSession(httpServletRequest, actionForm);
		return actionMapping.findForward(SUCCESS);
	}
}
