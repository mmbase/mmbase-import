/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.newsletter.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;

import com.finalist.cmsc.services.community.ApplicationContextFactory;
import com.finalist.cmsc.services.publish.Publish;
import com.finalist.cmsc.struts.MMBaseFormlessAction;
import com.finalist.newsletter.services.NewsletterPublicationService;
import com.finalist.newsletter.util.NewsletterPublicationUtil;
import com.finalist.newsletter.util.NewsletterUtil;

public class NewsletterPublicationSendEmail extends MMBaseFormlessAction {

   /**
    * the parameter of action
    */
   private static final String ACTION = "action";

   /**
    * name of submit button in jsp to send email
    */
   private static final String ACTION_SEND = "send";

   /**
    * name of submit button in jsp to cancel removal
    */
   private static final String ACTION_CANCEL = "cancel";

   @Override
   public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {

      int number = Integer.parseInt(getParameter(request, "number", true));
      if (NewsletterUtil.isPaused(NewsletterPublicationUtil.getNewsletterByPublicationNumber(number))) {
         request.setAttribute("isPaused", true);
         return mapping.findForward(SUCCESS);
      }
      if (isSendAction(request)) {
         if(Publish.isPublished(cloud.getNode(number))){
            String email = getParameter(request, "email");
            String mimeType = request.getParameter("mimetype");
            NewsletterPublicationService publicationService = (NewsletterPublicationService) ApplicationContextFactory.getBean("publicationService");
            NewsletterPublicationUtil.publish(cloud, number);
            publicationService.deliver(number, email, mimeType);
            return mapping.findForward(SUCCESS);
         } else {
            request.setAttribute("errormessage", true);
            return mapping.findForward("inputpage");
         }
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

      // neither remove or cancel, show confirmation page
      return mapping.findForward("inputpage");
   }

   private boolean isCancelAction(HttpServletRequest request) {
      return getParameter(request, ACTION) != null && getParameter(request, ACTION).equals(ACTION_CANCEL);
   }

   private boolean isSendAction(HttpServletRequest request) {
      return getParameter(request, ACTION) != null && getParameter(request, ACTION).equals(ACTION_SEND);
   }

}
