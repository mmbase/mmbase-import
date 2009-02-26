package com.finalist.savedform;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeIterator;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.resources.forms.DeleteSecondaryContentAction;
import com.finalist.cmsc.resources.forms.DeleteSecondaryContentForm;
import com.finalist.cmsc.struts.MMBaseAction;

public class DeleteSavedFormAction extends DeleteSecondaryContentAction {

   private static final Logger log = Logging.getLoggerInstance(DeleteSavedFormAction.class.getName());


   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {

      DeleteSecondaryContentForm deleteForm = (DeleteSecondaryContentForm) form;

      String number = deleteForm.getObjectnumber();
      if (MMBaseAction.ADMINISTRATOR.equals(cloud.getUser().getRank().toString())) {
         log.debug("deleting secondary content: " + number);
         Node responseForm = cloud.getNode(number);
         NodeIterator savedFormIterator = responseForm.getRelatedNodes("savedform").nodeIterator();
         while (savedFormIterator.hasNext()) {
            Node savedForm = savedFormIterator.nextNode();
            NodeIterator savedFieldIterator = savedForm.getRelatedNodes("savedfieldvalue").nodeIterator();
            while (savedFieldIterator.hasNext()) {
               Node fieldValueNode = savedFieldIterator.nextNode();
               fieldValueNode.delete(true);
            }
            savedForm.delete(true);
         }

      }
      else {
         log.warn("did not delete secondary content because user was not administrator: " + number + " ("
               + cloud.getUser() + ":" + cloud.getUser().getRank() + ")");
      }

      String returnurl = deleteForm.getReturnurl();
      return new ActionForward(returnurl);

   }


   public String getRequiredRankStr() {
      return ADMINISTRATOR;
   }

}
