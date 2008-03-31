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

   protected static final String AUTHENTICATION_ID = "authid";

   public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request,
         HttpServletResponse httpServletResponse) throws Exception {

      String authId = request.getParameter(AUTHENTICATION_ID);
      UserForm userForm = (UserForm) actionForm;
      userForm.clear();

      Authentication auth = null;
      
      AuthenticationService as = getAuthenticationService();
      if (authId != null) {
          auth = as.getAuthenticationById(Long.valueOf(authId));
      }
      
      if (auth != null) {
         userForm.setAction(UserForm.ACTION_EDIT);
         userForm.setAccount(auth.getUserId());
         
         PersonService ps = getPersonService();
         //Returns null when no Person object was found!
         Person person = ps.getPersonByAuthenticationId(auth.getId());
        
         if (person != null) {
            userForm.setFirstName(person.getFirstName());
            userForm.setPrefix(person.getInfix());
            userForm.setLastName(person.getLastName());
            userForm.setEmail(person.getEmail());
         } else {
            log.debug("person failed");
         }
      } else {
         // new
         userForm.setAction(UserForm.ACTION_ADD);
      }

      return actionMapping.findForward(SUCCESS);
   }
}