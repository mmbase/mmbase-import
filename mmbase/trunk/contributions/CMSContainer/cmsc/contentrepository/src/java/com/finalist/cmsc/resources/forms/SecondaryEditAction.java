package com.finalist.cmsc.resources.forms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.struts.MMBaseAction;

public class SecondaryEditAction extends MMBaseAction {

   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {
      SecondaryEditForm editForm = (SecondaryEditForm) form;
      if (SecondaryEditForm.ACTION_SAVE.equals(editForm.getAction())) {
         Node node = cloud.getNode(editForm.getNumber());
         node.setStringValue("title", editForm.getTitle());
         String nodetype = node.getNodeManager().getName();
         if("urls".equals(nodetype)){
            node.setStringValue("url", editForm.getUrl());
         }
         node.setStringValue("description", editForm.getDescription());
         node.commit();

         ActionForward actionForward = new ActionForward(editForm.getReturnUrl());
         actionForward.setRedirect(true);
         return actionForward;
      }
      else if (SecondaryEditForm.ACTION_CANCEL.equals(editForm.getAction())) {
         ActionForward actionForward = new ActionForward(editForm.getReturnUrl());
         actionForward.setRedirect(true);
         return actionForward;
      }
      else if (SecondaryEditForm.ACTION_INIT.equals(editForm.getAction())) {
         Node node = cloud.getNode(editForm.getNumber());
         editForm.setTitle(node.getStringValue("title"));
         String nodetype = node.getNodeManager().getName();
         if("urls".equals(nodetype)){
            editForm.setUrl(node.getStringValue("url"));
         }
         editForm.setDescription(node.getStringValue("description"));
      }

      return mapping.findForward(SUCCESS);
   }
}
