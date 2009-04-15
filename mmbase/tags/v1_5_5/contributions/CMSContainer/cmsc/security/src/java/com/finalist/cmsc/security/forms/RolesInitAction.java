package com.finalist.cmsc.security.forms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.*;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.struts.MMBaseAction;

/**
 * @author Nico Klasens
 */
public abstract class RolesInitAction extends MMBaseAction {

   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {
      RolesForm userForm = (RolesForm) form;

      String id = request.getParameter("nodeNumber");
      if (id != null) {
         userForm.clear();

         Node node = cloud.getNode(id);

         userForm.setId(node.getNumber());
         userForm.setName(node.getStringValue("name"));
         userForm.setDescription(node.getStringValue("description"));
         RolesInfo info = getRolesInfo(cloud, node);
         userForm.setRolesInfo(info);
         return mapping.findForward(SUCCESS);
      }
      return mapping.findForward("nouser");
   }


   protected abstract RolesInfo getRolesInfo(Cloud cloud, Node group);

}