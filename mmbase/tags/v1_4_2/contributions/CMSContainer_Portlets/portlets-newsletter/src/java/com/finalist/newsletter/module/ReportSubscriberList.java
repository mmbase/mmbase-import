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

import com.finalist.newsletter.module.bean.SubscriptionOverviewBean;
import com.finalist.newsletter.util.BeanUtil;
import com.finalist.newsletter.util.NewsletterSubscriptionUtil;

public class ReportSubscriberList extends Action {

   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
      ActionMessages errors = new ActionMessages();

      List<SubscriptionOverviewBean> beanList = new ArrayList<SubscriptionOverviewBean>();
      List<String> subscribers = NewsletterSubscriptionUtil.getAllUsersWithSubscription();
      if (subscribers != null && subscribers.size() > 0) {
         for (int n = 0; n < subscribers.size(); n++) {
            String userName = subscribers.get(n);
            SubscriptionOverviewBean bean = BeanUtil.createSubscriptionOverviewBean(userName);
            beanList.add(bean);
         }
      }

      if (beanList != null && beanList.size() > 0 ) {
         request.setAttribute("subscriptionOverviewBeans", beanList);
         return (mapping.findForward("success"));
      } else {
         errors.add("error", new ActionMessage("error.no_items"));
         saveErrors(request, errors);
         return (mapping.findForward("error"));
      }
   }
}