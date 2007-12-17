package com.finalist.newsletter.module;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.finalist.newsletter.module.bean.SubscriptionOverviewBean;
import com.finalist.newsletter.util.NewsletterSubscriptionUtil;

public class SubscriptionAction extends Action {

   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

      List<SubscriptionOverviewBean> userStatisticsBeans = new ArrayList<SubscriptionOverviewBean>();
      List<String> users = NewsletterSubscriptionUtil.getAllUsersWithSubscription();
      if (users != null && users.size() > 0) {
         for (int u = 0; u < users.size(); u++) {
            String userName = users.get(u);
            SubscriptionOverviewBean bean = BeanUtil.getUserStatisticsBean(userName);
            userStatisticsBeans.add(bean);
         }
         request.setAttribute("userStatistics", userStatisticsBeans);
      }
      ActionForward ret = mapping.findForward("success");
      return (ret);
   }
}