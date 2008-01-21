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

import com.finalist.newsletter.module.bean.NewsletterDetailBean;
import com.finalist.newsletter.module.bean.NewsletterSubscriberBean;
import com.finalist.newsletter.util.BeanUtil;

public class ReportNewsletterSubscriptions extends Action {

   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
      ActionMessages errors = new ActionMessages();

      int newsletterNumber = Integer.parseInt(request.getParameter("number"));
      if (newsletterNumber > 0) {
         request.setAttribute("number", newsletterNumber);
         List<NewsletterSubscriberBean> subscribers = BeanUtil.getSubscriberBeans(newsletterNumber);
         if (subscribers != null && subscribers.size() > 0) {
            request.setAttribute("subscriberOverviewBeans", subscribers);
            return (mapping.findForward("success"));
         } else {
            errors.add("error", new ActionMessage("error.no_items"));
         }
      }
      saveErrors(request, errors);
      return (mapping.findForward("error"));
   }
}
