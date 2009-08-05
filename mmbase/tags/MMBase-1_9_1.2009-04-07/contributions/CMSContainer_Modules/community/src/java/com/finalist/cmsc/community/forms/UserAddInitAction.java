/*
 * 
 * This software is OSI Certified Open Source Software. OSI Certified is a certification mark of the Open Source
 * Initiative.
 * 
 * The license (Mozilla version 1.0) can be read at the MMBase site. See http://www.MMBase.org/license
 * 
 */
package com.finalist.cmsc.community.forms;

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
 * @author Remco Bos
 * @author Wouter Heijke
 */
public class UserAddInitAction extends AbstractCommunityAction {
   private static Log log = LogFactory.getLog(UserAddInitAction.class);

   protected static final String AUTHENTICATION_ID = "authid";
   protected static final String FORWARD_GROUP = "group";

   public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request,
         HttpServletResponse httpServletResponse) throws Exception {

      String authId = request.getParameter(AUTHENTICATION_ID);
      String groupFoeward = request.getParameter(FORWARD_GROUP);
      setUserFormParamater(actionForm, authId);
      if (null == groupFoeward || ",".equals(groupFoeward)) {
         setNewsletterForward(request);
      } else {
         setGroupForward(request, groupFoeward);
      }
      return actionMapping.findForward(SUCCESS);
   }

   private void setGroupForward(HttpServletRequest request, String groupFoeward) {
      String[] temp = groupFoeward.split(",");
      String groupNmae = temp[0];
      if (temp.length == 2) {
         String option = temp[1];
         request.setAttribute("option", option);
      }
      request.setAttribute("groupName", groupNmae);
   }

   private void setNewsletterForward(HttpServletRequest request) {
      if (StringUtils.isNotBlank(request.getParameter("forward"))) {
         request.getSession().setAttribute("forward", request.getParameter("forward"));
         // community preference return back
         if (request.getParameter("path") != null) {
            request.getSession().setAttribute("path", request.getParameter("path"));
         }
      }
   }

   private void setUserFormParamater(ActionForm actionForm, String authId) {
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
         // Returns null when no Person object was found!
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
   }
}