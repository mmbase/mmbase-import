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
   public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
            throws Exception {

      if (!isCancelled(httpServletRequest)) {
         UserForm userForm = (UserForm) actionForm;

         // String accountName = userForm.getEmail();
         String accountName = userForm.getAccount();

         // AuthorityService aus = getAuthorityService(); //Never used (yet).
         AuthenticationService as = getAuthenticationService();
         PersonService ps = getPersonService();

         if (userForm.getAction().equalsIgnoreCase(UserForm.ACTION_ADD)) {
            Long authenticationId = as.getAuthenticationIdForUserId(accountName);
            if (authenticationId == null) {
               Authentication authentication = as.createAuthentication(userForm.getEmail(), userForm.getPasswordText());
               // Long authenticationId =
               // as.getAuthenticationIdForUserId(accountName);
               if (authentication.getId() != null) {
                  Person person = ps.createPerson(userForm.getFirstName(), userForm.getPrefix(), userForm.getLastName(), authentication.getId());
                  person.setEmail(userForm.getEmail()); // Also add an email address to the user.
                  ps.updatePerson(person);

               } else {
                  log.info("add authenticationId failed");
               }
            } else {
               log.info("add check1 failed for: " + accountName);
            }

         } else if (userForm.getAction().equalsIgnoreCase(UserForm.ACTION_EDIT)) {
            Long authenticationId = as.getAuthenticationIdForUserId(accountName);
            if (authenticationId != null) {
               String newPassword1 = userForm.getPasswordText();
               String newPassword2 = userForm.getPasswordConfirmation();
               if (!StringUtils.isBlank(newPassword1) && !StringUtils.isBlank(newPassword2)) {
                  if (newPassword1.equals(newPassword2)) {
                     as.updateAuthenticationPassword(accountName, newPassword1);
                  }
               }

               // First retrieve the right person object from the database
               Person person = ps.getPersonByAuthenticationId(authenticationId);

               if (person == null) { // User did not exists, so create it.
                  person = new Person();
                  person.setAuthenticationId(authenticationId);
               }

               // Also save other fields entered in the form to the right person
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
      }

      removeFromSession(httpServletRequest, actionForm);

      return actionMapping.findForward(SUCCESS);
   }
}
