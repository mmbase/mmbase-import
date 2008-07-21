/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.newsletter.forms;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.navigation.ServerUtil;
import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.security.UserRole;
import com.finalist.cmsc.services.publish.Publish;
import com.finalist.cmsc.struts.MMBaseFormlessAction;
import com.finalist.newsletter.ApplicationContextFactory;
import com.finalist.newsletter.domain.Publication;
import com.finalist.newsletter.domain.StatisticResult.HANDLE;
import com.finalist.newsletter.services.NewsletterPublicationService;
import com.finalist.newsletter.util.NewsletterPublicationUtil;
import com.finalist.newsletter.util.NewsletterUtil;

public class NewsletterPublicationPublish extends MMBaseFormlessAction {

   /** name of submit button in jsp to confirm removal */
   private static final String ACTION_REMOVE = "remove";

   /** name of submit button in jsp to cancel removal */
   private static final String ACTION_CANCEL = "cancel";

   @Override
   public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {

      NewsletterPublicationService publicationService = (NewsletterPublicationService) ApplicationContextFactory.getBean("publicationService");
      int number = Integer.parseInt(getParameter(request, "number", true));

      if(NewsletterUtil.isPaused(NewsletterPublicationUtil.getNewsletterByPublicationNumber(number))) {
         request.setAttribute("isPaused", true);
         return mapping.findForward(SUCCESS);
      }
      Map<String,List<String>> sendResults = null;
      Node publicationNode = cloud.getNode(number);

      if (isSendAction(request)) {

         UserRole role = NavigationUtil.getRole(publicationNode.getCloud(), publicationNode, false);
         boolean isWebMaster = (role != null && SecurityUtil.isWebmaster(role));

         if (NavigationUtil.getChildCount(publicationNode) > 0 && !isWebMaster) {
            return mapping.findForward("confirmationpage");
         }

         if (ServerUtil.isSingle()) {
            sendResults = publicationService.deliver(number);
            publicationService.setStatus(number, Publication.STATUS.DELIVERED);
            NewsletterUtil.logPubliction(number, HANDLE.POST);
            request.setAttribute("isSingle", true);
            request.setAttribute("sendResults", sendResults);
            request.setAttribute("sendSuccess", sendResults.get(NewsletterPublicationService.SEND_SUCCESS).size());
            request.setAttribute("sendFail", sendResults.get(NewsletterPublicationService.SEND_FAIL).size());
         }
         else {
            publicationService.setStatus(number, Publication.STATUS.READY);
            request.setAttribute("isPublish", true);
            Publish.publish(publicationNode);
         }

         return mapping.findForward(SUCCESS);
      }

      if (isCancelAction(request)) {
         String forwardPath = mapping.findForward("cancel").getPath();
         forwardPath = forwardPath.concat("?showpage=" + number);
         return new ActionForward(forwardPath);
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
