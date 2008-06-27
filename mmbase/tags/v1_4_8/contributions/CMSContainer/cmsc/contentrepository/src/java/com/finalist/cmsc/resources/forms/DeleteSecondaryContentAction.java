package com.finalist.cmsc.resources.forms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.services.publish.Publish;
import com.finalist.cmsc.struts.MMBaseAction;

public class DeleteSecondaryContentAction extends MMBaseAction {

   private static transient Logger log = Logging.getLoggerInstance(DeleteSecondaryContentAction.class.getName());

   private static final String ACTION_REMOVE = "remove";
   private static final String ACTION_CANCEL = "cancel";


   @Override
   public String getRequiredRankStr() {
      return SITEADMIN;
   }


   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {
      DeleteSecondaryContentForm deleteForm = (DeleteSecondaryContentForm) form;
      String number = deleteForm.getObjectnumber();
      String object_type = request.getParameter("object_type");
      if (isRemoveAction(request)) {
         try {
            log.debug("deleting secondary content: " + number);
            Node objectNode = cloud.getNode(number);

            Publish.remove(objectNode);
            Publish.unpublish(objectNode);

            objectNode.delete(true);

            return mapping.findForward(object_type);
         }
         catch (NotFoundException nfe) {
            log.info("Failed to delete secondaryContent with number " + number + ", node not found");
         }
      }
      if (isCancelAction(request)) {
         return mapping.findForward(object_type);
      }
      return mapping.findForward("delete");
   }


   private boolean isRemoveAction(HttpServletRequest request) {
      return getParameter(request, ACTION_REMOVE) != null;
   }


   private boolean isCancelAction(HttpServletRequest request) {
      return getParameter(request, ACTION_CANCEL) != null;
   }

}
