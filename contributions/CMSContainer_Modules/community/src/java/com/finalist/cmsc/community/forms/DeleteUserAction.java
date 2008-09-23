package com.finalist.cmsc.community.forms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Delete a Community user
 * 
 * @author Wouter Heijke
 */
public class DeleteUserAction extends AbstractCommunityAction {

   protected static final String AUTHENTICATION_ID = "authid";
   protected static final String FORWARD_GROUP = "group";

   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
         HttpServletResponse httpServletResponse) throws Exception {
      String groupFoeward = request.getParameter(FORWARD_GROUP);
      deleteUser(request);
      if (null == groupFoeward || ",".equals(groupFoeward)) {
         return mapping.findForward(SUCCESS);
      } else {
         // deleteUser(request);
         setGroupParamater(request, groupFoeward);
         return mapping.findForward(FORWARD_GROUP);
      }
      // return mapping.findForward(SUCCESS);
   }

   private void setGroupParamater(HttpServletRequest request, String groupFoeward) {
      String[] temp = groupFoeward.split(",");
      String groupNmae = temp[0];
      if (temp.length == 2) {
         String option = temp[1];
         request.setAttribute("option", option);
      }
      request.setAttribute("groupName", groupNmae);
   }

   private void deleteUser(HttpServletRequest request) {
      String authenticationId = request.getParameter(AUTHENTICATION_ID);
      if (StringUtils.isNotBlank(authenticationId)) {
         Long authId = Long.valueOf(authenticationId);
         getPersonService().deletePersonByAuthenticationId(authId);
         getAuthenticationService().deleteAuthentication(authId);
      }
   }
}
