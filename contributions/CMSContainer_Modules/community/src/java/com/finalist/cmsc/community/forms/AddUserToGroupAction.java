package com.finalist.cmsc.community.forms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class AddUserToGroupAction extends AbstractCommunityAction{
   public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response)
    throws Exception{
      if(request.getParameter("users")!=null){
         String[] userIds = request.getParameter("users").split(";");
         SearchForm searchform = (SearchForm)actionForm;
         String[] groupNames = searchform.getChk_group();
         for(String groupName:groupNames){
               for(String userId:userIds){
                  getAuthenticationService().addAuthorityToUser(userId, groupName);
               }
         }
         removeFromSession(request, searchform);
         request.getSession().removeAttribute("groupName");
      }
      
      return actionMapping.findForward("success");
   }
}
