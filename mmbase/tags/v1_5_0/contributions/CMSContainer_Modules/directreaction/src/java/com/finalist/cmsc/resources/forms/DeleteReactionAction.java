package com.finalist.cmsc.resources.forms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.*;
import org.mmbase.bridge.Cloud;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.services.publish.Publish;
import com.finalist.cmsc.struts.MMBaseAction;

public class DeleteReactionAction extends DeleteSecondaryContentAction {

   private static final Logger log = Logging.getLoggerInstance(DeleteReactionAction.class.getName());


   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {

      DeleteSecondaryContentForm deleteForm = (DeleteSecondaryContentForm) form;

      String number = deleteForm.getObjectnumber();
      if (MMBaseAction.ADMINISTRATOR.equals(cloud.getUser().getRank().toString())) {
         log.debug("deleting secondary content: " + number);
         Cloud remoteCloud = Publish.getRemoteCloud(cloud);
         remoteCloud.getNode(number).delete(true);
      }
      else {
         log.warn("did not delete secondary content because user was not administrator: " + number + " ("
               + cloud.getUser() + ":" + cloud.getUser().getRank() + ")");
      }

      String returnurl = deleteForm.getReturnurl();
      return new ActionForward(returnurl);

   }

   @Override
   public String getRequiredRankStr() {
      return ADMINISTRATOR;
   }

}
