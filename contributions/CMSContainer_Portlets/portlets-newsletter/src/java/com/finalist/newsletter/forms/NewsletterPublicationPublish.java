/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.newsletter.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.security.UserRole;
import com.finalist.cmsc.struts.MMBaseFormlessAction;
import com.finalist.newsletter.publisher.NewsletterPublisher;

public class NewsletterPublicationPublish extends MMBaseFormlessAction {

   /** name of submit button in jsp to confirm removal */
   private static final String ACTION_REMOVE = "remove";

   /** name of submit button in jsp to cancel removal */
   private static final String ACTION_CANCEL = "cancel";

   @Override
   public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {

      int number = Integer.parseInt(getParameter(request, "number", true));
      Node publicationNode = cloud.getNode(number);

      if (isSendAction(request)) {

         UserRole role = NavigationUtil.getRole(publicationNode.getCloud(), publicationNode, false);
         boolean isWebMaster = (role != null && SecurityUtil.isWebmaster(role));

         if (NavigationUtil.getChildCount(publicationNode) > 0 && !isWebMaster) {
            return mapping.findForward("confirmationpage");
         }
//         Thread publisher = new NewsletterPublisher(number);
//         publisher.start();

         return mapping.findForward(SUCCESS);
      }

      if (isCancelAction(request)) {
         String forwardPath = mapping.findForward("cancel").getPath();
         forwardPath = forwardPath.concat("?showpage=" + number);
         return new ActionForward(forwardPath);
      }

      // neither remove or cancel, show confirmation page

      String publishDate = publicationNode.getStringValue("publishdate");
      if (publishDate != null && publishDate.length() > 0 ) {
         return mapping.findForward("confirm_resend");
      }
      return mapping.findForward("confirm_send");
   }

   private boolean isCancelAction(HttpServletRequest request) {
      return getParameter(request, ACTION_CANCEL) != null;
   }

   private boolean isSendAction(HttpServletRequest request) {
      return getParameter(request, ACTION_REMOVE) != null;
   }

}
