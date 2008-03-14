package com.finalist.newsletter.module;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.finalist.newsletter.module.bean.SubscriptionDetailBean;
import com.finalist.newsletter.util.BeanUtil;
import com.finalist.newsletter.util.NewsletterSubscriptionUtil;

public class ReportSubscriberSubscriptions extends Action {

   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
      ActionMessages errors = new ActionMessages();

      String userName = request.getParameter("username");
      if (userName != null) {
         SubscriptionDetailBean bean = BeanUtil.createSubscriptionDetailBean(userName);
         if (bean != null) {
            List<Integer> subscribedThemes = NewsletterSubscriptionUtil.getUserSubscribedThemes(userName);
            List<Integer> subscribedNewsletters = NewsletterSubscriptionUtil.getUserSubscribedNewsletters(userName);

            if (subscribedNewsletters != null && subscribedNewsletters.size() > 0) {
               request.setAttribute("newslettersubscriptions", subscribedNewsletters);
            }

            if (subscribedThemes != null && subscribedThemes.size() > 0) {
               request.setAttribute("themesubscriptions", subscribedThemes);
            }

            if ((subscribedThemes != null && subscribedThemes.size() > 0) || (subscribedNewsletters != null && subscribedNewsletters.size() > 0)) {
               String status = NewsletterSubscriptionUtil.getSubscriptionStatus(userName);
               request.setAttribute(NewsletterSubscriptionUtil.SUBSCRIPTION_STATUS_KEY, status);
               String preferredMimeType = NewsletterSubscriptionUtil.getPreferredMimeType(userName);
               request.setAttribute(NewsletterSubscriptionUtil.PREFERRED_MIMETYPE, preferredMimeType);
            }

            request.setAttribute("subscriptionDetailBean", bean);
            return (mapping.findForward("success"));
         } else {
            errors.add("error", new ActionMessage("error.no_items"));

         }
      }
      saveErrors(request, errors);
      return (mapping.findForward("error"));
   }

}