package com.finalist.newsletter.module;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.finalist.newsletter.module.bean.NewsletterOverviewBean;
import com.finalist.newsletter.util.BeanUtil;
import com.finalist.newsletter.util.NewsletterUtil;

public class ReportNewsletterList extends Action {

   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
      ActionMessages errors = new ActionMessages();

      List<NewsletterOverviewBean> beanList = new ArrayList<NewsletterOverviewBean>();
      List<Integer> newsletters = NewsletterUtil.getAllNewsletters();
      if (newsletters != null && newsletters.size() > 0) {
         for (int n = 0; n < newsletters.size(); n++) {
            int newsletterNumber = newsletters.get(n);
            NewsletterOverviewBean bean = BeanUtil.createNewsletterOverviewBean(newsletterNumber);
            beanList.add(bean);
         }
      }
      if (beanList != null && beanList.size() > 0 ) {
         request.setAttribute("newsletterOverviewBeans", beanList);
         return (mapping.findForward("success"));
      } else {
         errors.add("error", new ActionMessage("error.no_items"));
         saveErrors(request, errors);
         return (mapping.findForward("error"));
      }
   }
}