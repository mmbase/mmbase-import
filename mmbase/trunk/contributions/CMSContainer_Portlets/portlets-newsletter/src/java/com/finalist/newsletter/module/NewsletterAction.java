package com.finalist.newsletter.module;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.finalist.newsletter.module.bean.NewsletterDetailBean;
import com.finalist.newsletter.module.bean.NewsletterOverviewBean;
import com.finalist.newsletter.util.NewsletterUtil;

public class NewsletterAction extends Action {

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

      if (action != null) {
         if (action.equals("overview")) {
            List<NewsletterOverviewBean> beanList = createOverview();
            if (beanList != null) {
               request.setAttribute("newsletterOverviewBeans", beanList);
               actionForward = mapping.findForward("overview");
            }
         } else if (action.equals("detail")) {
            String newsletterNumber = request.getParameter("number");
            if (newsletterNumber != null) {
               NewsletterDetailBean bean = BeanUtil.createNewsletterDetailBean(newsletterNumber);
               if (bean != null) {
                  request.setAttribute("newsletterDetailBean", bean);
                  actionForward = mapping.findForward("detail");
               }
            }
         }
      }
      return (actionForward);
   }

   private List<NewsletterOverviewBean> createOverview() {
      List<NewsletterOverviewBean> beanList = new ArrayList<NewsletterOverviewBean>();
      List<String> newsletters = NewsletterUtil.getAllNewsletters();
      if (newsletters != null && newsletters.size() > 0) {
         for (int n = 0; n < newsletters.size(); n++) {
            String newsletterNumber = newsletters.get(n);
            NewsletterOverviewBean bean = BeanUtil.createNewsletterOverviewBean(newsletterNumber);
            beanList.add(bean);
         }
      }
      return beanList;
   }
}
