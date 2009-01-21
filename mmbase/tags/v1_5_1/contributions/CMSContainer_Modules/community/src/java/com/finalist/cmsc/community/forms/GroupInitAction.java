/*
 * 
 * This software is OSI Certified Open Source Software. OSI Certified is a certification mark of the Open Source
 * Initiative.
 * 
 * The license (Mozilla version 1.0) can be read at the MMBase site. See http://www.MMBase.org/license
 * 
 */
package com.finalist.cmsc.community.forms;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.LabelValueBean;

import com.finalist.cmsc.services.community.security.Authentication;
import com.finalist.cmsc.services.community.security.AuthenticationService;
import com.finalist.cmsc.services.community.security.Authority;
import com.finalist.cmsc.services.community.security.AuthorityService;

/**
 * @author Wouter Heijke
 */
public class GroupInitAction extends AbstractCommunityAction {

   private static Log log = LogFactory.getLog(GroupInitAction.class);

   protected static final String GROUPID = "groupid";

   public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request,
         HttpServletResponse httpServletResponse) throws Exception {

      List < LabelValueBean > usersList = new ArrayList < LabelValueBean > ();
      List < LabelValueBean > membersList = new ArrayList < LabelValueBean > ();

      AuthenticationService as = getAuthenticationService();
      List < Authentication > users = as.findAuthentications();

      AuthorityService aus = getAuthorityService();

      String id = request.getParameter(GROUPID);
      GroupForm groupForm = (GroupForm) actionForm;

      if (id != null) {
         groupForm.setAction(GroupForm.ACTION_EDIT);
         Authority auth = aus.findAuthorityByName(id);
         if (auth != null) {
            groupForm.setName(auth.getName());
            for (Authentication user : users) {
               Set < String > names = aus.getAuthorityNamesForUser(user.getUserId());
               String label = user.getUserId();
               LabelValueBean bean = new LabelValueBean(label, label);
               if (names.contains(id)) {
                  membersList.add(bean);
               } else {
                  usersList.add(bean);
               }
            }
         } else {
            log.error("group failed");
         }
      } else {
         // new
         groupForm.setAction(GroupForm.ACTION_ADD);
         groupForm.reset(actionMapping, request);
         for (Authentication user : users) {
            String label = user.getUserId();
            LabelValueBean bean = new LabelValueBean(label, label);
            usersList.add(bean);
         }
      }

      request.setAttribute("membersList", membersList);
      request.setAttribute("usersList", usersList);

      return actionMapping.findForward(SUCCESS);

   }
}
