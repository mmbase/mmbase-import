package com.finalist.cmsc.security.forms;

import java.util.Map;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.security.UserRole;
import com.finalist.cmsc.struts.MMBaseAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Nico Klasens
 */
public abstract class RolesAction extends MMBaseAction {

   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {
      if (this.isCancelled(request)) {
         removeFromSession(request, form);
         return mapping.findForward("savecancel");
      }

      Map<Integer, UserRole> requestRoles = SecurityUtil.buildRolesFromRequest(request);
      RolesForm groupForm = (RolesForm) form;

      groupForm.addRequestRoles(requestRoles);

      String save = request.getParameter("savetree");
      if ("true".equals(save)) {
         int id = groupForm.getId();
         Node groupNode = cloud.getNode(id);

         setGroupRights(cloud, groupForm, groupNode);

         removeFromSession(request, form);
         return mapping.findForward("savecancel");
      }

      String id = request.getParameter("nodeNumber");
      if (id != null) {
         Node node = cloud.getNode(id);
         groupForm.setName(node.getStringValue("name"));
         groupForm.setDescription(node.getStringValue("description"));
         RolesInfo info = getRolesInfo(cloud, node);
         groupForm.setRolesInfo(info);
      }

      return mapping.findForward(SUCCESS);
   }


   protected abstract RolesInfo getRolesInfo(Cloud cloud, Node group);


   protected abstract void setGroupRights(Cloud cloud, RolesForm groupForm, Node groupNode);
}