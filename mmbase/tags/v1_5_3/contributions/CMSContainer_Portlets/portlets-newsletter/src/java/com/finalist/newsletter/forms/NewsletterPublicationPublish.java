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

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.security.UserRole;
import com.finalist.cmsc.services.community.ApplicationContextFactory;
import com.finalist.cmsc.struts.MMBaseFormlessAction;
import com.finalist.cmsc.util.ServerUtil;
import com.finalist.newsletter.domain.EditionStatus;
import com.finalist.newsletter.domain.Publication;
import com.finalist.newsletter.domain.StatisticResult.HANDLE;
import com.finalist.newsletter.services.NewsletterPublicationService;
import com.finalist.newsletter.util.NewsletterPublicationUtil;
import com.finalist.newsletter.util.NewsletterUtil;

public class NewsletterPublicationPublish extends MMBaseFormlessAction {

   /**
    * name of submit button in jsp to confirm removal
    */
   private static final String ACTION_REMOVE = "remove";

   /**
    * name of submit button in jsp to cancel removal
    */
   private static final String ACTION_CANCEL = "cancel";
   
   /**
    * System property to allow skipping the Freeze or Approve action 
    */
   public static final String NEWSLETTER_FREEZE_PROPERTY = "newsletter.workflow.allow.skip.freezing";
   public static final String NEWSLETTER_APPROVE_PROPERTY = "newsletter.workflow.allow.skip.approving";

   /**
    * @Override
    */
   public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {

      NewsletterPublicationService publicationService = (NewsletterPublicationService) ApplicationContextFactory.getBean("publicationService");
      int number = Integer.parseInt(getParameter(request, "number", true));
      Node newsletterNode = NewsletterPublicationUtil.getNewsletterByPublicationNumber(number);
      if (NewsletterUtil.isPaused(newsletterNode)) {
         request.setAttribute("isPaused", true);
         return mapping.findForward(SUCCESS);
      }
      Map<String, List<String>> sendResults = null;
      Node publicationNode = cloud.getNode(number);

      if (isSendAction(request)) {
         UserRole role = NavigationUtil.getRole(publicationNode.getCloud(), publicationNode, false);
         boolean isWebMaster = (role != null && SecurityUtil.isWebmaster(role));
         boolean isChiefEditor = (role != null && SecurityUtil.isChiefEditor(role));

         if (!isWebMaster && !isChiefEditor) {
            String process_status = publicationNode.getStringValue("process_status");
            if("true".equalsIgnoreCase(PropertiesUtil.getProperty(NEWSLETTER_FREEZE_PROPERTY))) {
               if (EditionStatus.INITIAL.value().equals(process_status)) {
                  request.setAttribute("message", "confirm_send.skip.freezing"); 
                  request.setAttribute("restriction", true);
                  return mapping.findForward("confirm_send");
               }
            }
            if("true".equalsIgnoreCase(PropertiesUtil.getProperty(NEWSLETTER_APPROVE_PROPERTY))) {
               if (EditionStatus.INITIAL.value().equals(process_status) || EditionStatus.FROZEN.value().equals(process_status)) {
                  request.setAttribute("message", "confirm_send.skip.approving"); 
                  request.setAttribute("restriction", true);
                  return mapping.findForward("confirm_send");
               }
            }
         }

         if (NavigationUtil.getChildCount(publicationNode) > 0 && !isWebMaster) {
            return mapping.findForward("confirmationpage");
         }
         if (ServerUtil.isSingle()) {
            sendResults = publicationService.deliver(number);
            publicationService.setStatus(number, Publication.STATUS.DELIVERED);
            NewsletterUtil.logPubliction(newsletterNode.getNumber(), HANDLE.POST);
            request.setAttribute("isSingle", true);
            request.setAttribute("sendResults", sendResults);
            request.setAttribute("sendSuccess", sendResults.get(NewsletterPublicationService.SEND_SUCCESS).size());
            request.setAttribute("sendFail", sendResults.get(NewsletterPublicationService.SEND_FAIL).size());
         } else {
            publicationService.setStatus(number, Publication.STATUS.READY);
            request.setAttribute("isPublish", true);
            return new ActionForward("/editors/workflow/publish.jsp?number="+number);
         }

         return mapping.findForward(SUCCESS);
      }

      if (isCancelAction(request)) {
         String forwardPath = mapping.findForward("cancel").getPath();
         forwardPath = forwardPath.concat("?showpage=" + number);
         if (StringUtils.isNotBlank(request.getParameter("forward"))) {
            ActionForward ret = new ActionForward(mapping.findForward("publicationedit").getPath() + "?newsletterId="
                     + request.getParameter("newsletterId"));
            ret.setRedirect(true);
            return ret;
         }
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
