package com.finalist.portlets.guestbook.search;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.struts.action.*;
import org.mmbase.bridge.Cloud;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.resources.forms.DeleteSecondaryContentAction;
import com.finalist.cmsc.resources.forms.DeleteSecondaryContentForm;
import com.finalist.cmsc.services.publish.Publish;

public class DeleteGuestMessageAction extends DeleteSecondaryContentAction {

   private static final Logger log = Logging.getLoggerInstance(DeleteGuestMessageAction.class.getName());


   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {

      DeleteSecondaryContentForm deleteForm = (DeleteSecondaryContentForm) form;

      String number = deleteForm.getObjectnumber();
      log.debug("deleting secondary content: " + number);
      boolean isRemote = Boolean.parseBoolean(request.getParameter("isRemote"));
      Cloud remoteCloud = getCloudForAnonymousUpdate(isRemote);
      remoteCloud.getNode(number).delete(true);

      String returnurl = deleteForm.getReturnurl();
      log.debug("Returnurl: " + returnurl);
      return new ActionForward(returnurl);

   }

   public Cloud getCloudForAnonymousUpdate(boolean isRemote) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      if (isRemote) {
         return Publish.getRemoteCloud(cloud);
      }
      return cloud;
   }


   @Override
   public String getRequiredRankStr() {
      return BASIC_USER;
   }

}
