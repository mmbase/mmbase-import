package com.finalist.cmsc.security.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.struts.MMBaseFormlessAction;

/**
 * DeleteTaskAction
 * 
 * @author Nico Klasens
 */
public class DeleteGroupAction extends MMBaseFormlessAction {

   @Override
   public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {
      String id = request.getParameter("id");
      if (id != null) {
         Node user = cloud.getNode(id);
         user.delete(true);
      }
      return mapping.findForward(SUCCESS);
   }
}