package com.finalist.cmsc.security.forms;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.mmbase.bridge.*;

import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.struts.MMBaseAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * GroupAction
 *
 * @author Nico Klasens
 */
public class GroupAction extends MMBaseAction {
    
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, Cloud cloud) throws Exception {
       if (!isCancelled(request)) {
         GroupForm groupForm = (GroupForm) form;
         Node groupNode = getOrCreateNode(groupForm, cloud, SecurityUtil.GROUP);
         if (groupForm.getId() == -1) {
             groupNode.setStringValue("name", groupForm.getName());
         }
         groupNode.setStringValue("description", groupForm.getDescription());
         groupNode.commit();

         SecurityUtil.setGroupMembers(cloud, groupNode, groupForm.getMembers());
      }
       removeFromSession(request, form);
      return mapping.findForward(SUCCESS);
   }
    
}