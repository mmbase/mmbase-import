package com.finalist.cmsc.community.forms;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.util.LabelValueBean;

import com.finalist.cmsc.services.community.security.Authentication;
import com.finalist.cmsc.services.community.security.AuthenticationService;
import com.finalist.cmsc.services.community.security.AuthorityService;

/**
 * @author Wouter Heijke
 */
public class GroupAction extends AbstractCommunityAction {

   public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request,
         HttpServletResponse httpServletResponse) throws Exception {

      if (!isCancelled(request)) {
         GroupForm groupForm = (GroupForm) actionForm;
         List < LabelValueBean > membersList = new ArrayList < LabelValueBean > ();
         List < LabelValueBean > usersList = new ArrayList < LabelValueBean > ();

         String id = groupForm.getName();

         // get all users
         AuthenticationService as = getAuthenticationService();
         List < Authentication > users = as.findAuthentications();

         AuthorityService aus = getAuthorityService();

         for (Authentication user : users) {
            String label = user.getUserId();
            LabelValueBean bean = new LabelValueBean(label, label);
            usersList.add(bean);

         }
         // get members and remove them from users
         for (String memberName : groupForm.getMembers()) {
            String label = memberName;
            LabelValueBean beanMember = new LabelValueBean(label, label);
            membersList.add(beanMember);
            usersList.remove(beanMember);
         }

         request.setAttribute("membersList", membersList);
         request.setAttribute("usersList", usersList);

         // validate
         ActionMessages errors = new ActionMessages();

         if (groupForm.getAction().equalsIgnoreCase(GroupForm.ACTION_ADD)) {
            if (id == null || id.length() < 3) {
               errors.add("groupname", new ActionMessage("error.groupname.invalid"));
               saveErrors(request, errors);
               return actionMapping.getInputForward();
            } else {
               boolean exist = aus.authorityExists(id);
               if (exist) {
                  errors.add("groupname", new ActionMessage("error.groupname.alreadyexists"));
                  saveErrors(request, errors);
                  return actionMapping.getInputForward();
               }
            }

            aus.createAuthority(null, id);
         }

         if (id != null) {
            List < Authentication > current = as.findAuthenticationsForAuthority(id);
            for (String memberName : groupForm.getMembers()) {
               Authentication m = as.findAuthentication(memberName);
               if (!current.contains(m)) {
                  as.addAuthorityToUser(memberName, id);
               } else {
                  current.remove(m);
               }
            }
            for (Authentication user : current) {
               as.removeAuthorityFromUser(user.getUserId(), id);
            }
         }
      }
      removeFromSession(request, actionForm);

      return actionMapping.findForward(SUCCESS);
   }
}
