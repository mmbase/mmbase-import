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

import com.finalist.newsletter.module.bean.NewsletterDetailBean;
import com.finalist.newsletter.module.bean.NewsletterOverviewBean;
import com.finalist.newsletter.util.BeanUtil;
import com.finalist.newsletter.util.NewsletterUtil;

public class NewsletterAction extends Action {

   private List<NewsletterOverviewBean> createOverview() {
      List<NewsletterOverviewBean> beanList = new ArrayList<NewsletterOverviewBean>();
      List<Integer> newsletters = NewsletterUtil.getAllNewsletters();
      if (newsletters != null && newsletters.size() > 0) {
         for (int n = 0; n < newsletters.size(); n++) {
            int newsletterNumber = newsletters.get(n);
            NewsletterOverviewBean bean = BeanUtil.createNewsletterOverviewBean(newsletterNumber);
            beanList.add(bean);
         }
      }
      return beanList;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping,
    *      org.apache.struts.action.ActionForm,
    *      javax.servlet.http.HttpServletRequest,
    *      javax.servlet.http.HttpServletResponse)
    */
   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
      String action = request.getParameter("action");
      ActionForward actionForward = mapping.findForward("error");
      ActionMessages errors = new ActionMessages();

      if (action != null) {
         if (action.equals("overview")) {
            List<NewsletterOverviewBean> beanList = createOverview();
            if (beanList != null) {
               request.setAttribute("newsletterOverviewBeans", beanList);
               actionForward = mapping.findForward("overview");
            } else {
               errors.add("error", new ActionMessage("error.no_items"));
               actionForward = mapping.findForward("error");
            }
         } else if (action.equals("detail")) {
            int newsletterNumber = Integer.parseInt(request.getParameter("number"));
            if (newsletterNumber > 0) {
               NewsletterDetailBean bean = BeanUtil.createNewsletterDetailBean(newsletterNumber);
               if (bean != null) {
                  if (bean.getSubscribers() != null && bean.getSubscribers().size() > 0) {
                     request.setAttribute("newsletterDetailBean", bean);
                     actionForward = mapping.findForward("detail");

                  } else {
                     errors.add("error", new ActionMessage("error.no_items"));
                     actionForward = mapping.findForward("error");
                  }
               } else {
                  errors.add("error", new ActionMessage("error.no_data"));
                  actionForward = mapping.findForward("error");
               }
            }
         }
      }
      saveErrors(request, errors);
      return (actionForward);
   }
}
