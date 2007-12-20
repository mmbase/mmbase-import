package com.finalist.newsletter.module;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.finalist.newsletter.util.NewsletterSubscriptionUtil;

public class SubscriberAction extends Action {

   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
      String action = (String) request.getParameter("action");
      String userName = (String) request.getParameter("username");
      ActionForward actionForward = mapping.findForward("error");
      ActionMessages errors = new ActionMessages();
      ActionMessages messages = new ActionMessages();

      if (action != null) {
         if (action.equals("unsubscribe")) {
            if (userName != null) {
               NewsletterSubscriptionUtil.unsubscribeFromAllNewsletters(userName);
               actionForward = mapping.findForward("return" + "&username=" + userName);
            }
         } else if (action.equals("terminate")) {
            if (userName != null) {
               NewsletterSubscriptionUtil.terminateUserSubscription(userName);
            }
         } else if (action.equals("pause")) {
            if (userName != null) {
               NewsletterSubscriptionUtil.pauseSubscription(userName);
               actionForward = mapping.findForward("return" + "&username=" + userName);
            }
         } else if (action.equals("resume")) {
            if (userName != null) {
               NewsletterSubscriptionUtil.resumeSubscription(userName);
               actionForward = mapping.findForward("return" + "&username=" + userName);

            }
         } else if (action.equals("update")) {
            if (userName != null) {
               actionForward = mapping.findForward("return" + "&username=" + userName);
            }
         } else {
            errors.add("unknown_action", new ActionMessage("error.unknown_action"));
         }
      }
      saveErrors(request, errors);
      this.saveMessages(request, messages);
      return (actionForward);
   }
}