package com.finalist.cmsc.community.forms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Put users into selected group(s).
 * @author Eva
 *
 */
public class AddUserToGroupAction extends AbstractCommunityAction {
   public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request,
         HttpServletResponse response) throws Exception {
      SearchForm searchform = (SearchForm) actionForm;
      getInitActionParramater(request, searchform);
      String[] authIds = searchform.getChk_();
      String groupName = request.getParameter("groupName");
      if (null != groupName && null != authIds) {
         for (String authId : authIds) {
            getAuthenticationService().addAuthorityToUserByAuthenticationId(authId, groupName);
         }
         return actionMapping.findForward("group");
      }
      return actionMapping.findForward("success");
   }

   private void getInitActionParramater(HttpServletRequest request, SearchForm searchform) {
      if (request.getParameter("users") != null) {
         String[] userIds = request.getParameter("users").split(";");
         String[] groupNames = searchform.getChk_group();
         if (null != groupNames) {
            for (String groupName : groupNames) {
               addlistUserToGroup(userIds, groupName);
            }
         }
         removeFromSession(request, searchform);
         request.getSession().removeAttribute("groupName");
      }
   }

   private void addlistUserToGroup(String[] userIds, String groupName) {
      for (String userId : userIds) {
         getAuthenticationService().addAuthorityToUser(userId, groupName);
      }
   }
}
