/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.community.forms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.finalist.cmsc.services.community.person.Person;
import com.finalist.cmsc.services.community.person.PersonService;
import com.finalist.cmsc.services.community.security.Authentication;
import com.finalist.cmsc.services.community.security.AuthenticationService;

/**
 * @author Remco Bos
 * @author Wouter Heijke
 */
public class UserAddInitAction extends AbstractCommunityAction {
	private static Log log = LogFactory.getLog(UserAddInitAction.class);

	public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request,
			HttpServletResponse httpServletResponse) throws Exception {

//		AuthorityService aus = getAuthorityService();

		String id = request.getParameter(USERID);
		UserForm userForm = (UserForm) actionForm;
		if (id != null) {
			userForm.setAction(UserForm.ACTION_EDIT);

			AuthenticationService as = getAuthenticationService();
			Authentication auth = as.findAuthentication(id);
			if (auth != null) {
				PersonService ps = getPersonService();
				Person person = ps.getPersonByUserId(id); //Returns null when no Person object was found!
				userForm.setAccount(id);
				
				if (person != null) {
					userForm.setFirstName(person.getFirstName());
					userForm.setPrefix(person.getInfix());
					userForm.setLastName(person.getLastName());
					userForm.setEmail(person.getEmail());
					
				} else {
					log.info("person failed");
				}

			} else {
				log.info("auth failed");
			}

		} else {
			// new
			userForm.setAction(UserForm.ACTION_ADD);
		}

		return actionMapping.findForward(SUCCESS);
	}
}