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

public class SubscriptionAction extends Action {

   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
      String action = request.getParameter("action");
      String userName = request.getParameter("username");
      String[] checked = request.getParameterValues("checked");

      ActionForward actionForward = mapping.findForward("error");
      ActionMessages errors = new ActionMessages();
      ActionMessages messages = new ActionMessages();

      if (action != null) {
         if (action.equals("unsubscribe")) {
            if (userName != null) {
               NewsletterSubscriptionUtil.unsubscribeFromAllNewsletters(userName);
               return (mapping.findForward("success"));
            } else if (checked != null && checked.length > 0) {
               for (int i = 0; i < checked.length; i++) {
                  int newsletterNumber = Integer.parseInt(checked[i]);
                  NewsletterSubscriptionUtil.unsubscribeAllFromNewsletter(newsletterNumber);
                  return (mapping.findForward("success"));
               }
            }
         } else if (action.equals("terminate")) {
            if (userName != null) {
               actionForward = mapping.findForward("return");
               String path = actionForward.getPath() + "&username=" + userName;
               actionForward.setPath(path);
            }
         } else if (action.equals("pause")) {
            if (userName != null) {
               NewsletterSubscriptionUtil.pauseSubscription(userName);
               actionForward = mapping.findForward("return");
               String path = actionForward.getPath() + "&username=" + userName;
               actionForward.setPath(path);
            }
         } else if (action.equals("resume")) {
            if (userName != null) {
               NewsletterSubscriptionUtil.resumeSubscription(userName);
               actionForward = mapping.findForward("return");
               String path = actionForward.getPath() + "&username=" + userName;
               actionForward.setPath(path);

            }
         } else if (action.equals("update")) {
            if (userName != null) {
               actionForward = mapping.findForward("return");
               String path = actionForward.getPath() + "&username=" + userName;
               actionForward.setPath(path);
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