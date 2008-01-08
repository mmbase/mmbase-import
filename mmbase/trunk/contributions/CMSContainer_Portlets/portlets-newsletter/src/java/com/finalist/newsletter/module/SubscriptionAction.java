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

import com.finalist.newsletter.module.bean.SubscriptionDetailBean;
import com.finalist.newsletter.module.bean.SubscriptionOverviewBean;
import com.finalist.newsletter.util.BeanUtil;
import com.finalist.newsletter.util.NewsletterSubscriptionUtil;

public class SubscriptionAction extends Action {

   private List<SubscriptionOverviewBean> createOverview() {
      List<SubscriptionOverviewBean> beanList = new ArrayList<SubscriptionOverviewBean>();
      List<String> subscribers = NewsletterSubscriptionUtil.getAllUsersWithSubscription();
      if (subscribers != null && subscribers.size() > 0) {
         for (int n = 0; n < subscribers.size(); n++) {
            String userName = subscribers.get(n);
            SubscriptionOverviewBean bean = BeanUtil.createSubscriptionOverviewBean(userName);
            beanList.add(bean);
         }
      }
      return beanList;
   }

   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
      String action = request.getParameter("action");
      ActionForward actionForward = mapping.findForward("error");
      ActionMessages errors = new ActionMessages();

      if (action != null) {
         if (action.equals("overview")) {
            List<SubscriptionOverviewBean> beanList = createOverview();
            if (beanList != null) {
               request.setAttribute("subscriptionOverviewBeans", beanList);
               actionForward = mapping.findForward("overview");
            } else {
               errors.add("error", new ActionMessage("error.no_items"));
               actionForward = mapping.findForward("error");
            }
         } else if (action.equals("detail")) {
            String userName = request.getParameter("username");
            if (userName != null) {
               SubscriptionDetailBean bean = BeanUtil.createSubscriptionDetailBean(userName);
               if (bean != null) {
                  request.setAttribute("subscriptionDetailBean", bean);
                  actionForward = mapping.findForward("detail");
               } else {
                  errors.add("error", new ActionMessage("error.no_items"));
                  actionForward = mapping.findForward("error");
               }
            }
         }
      }
      saveErrors(request, errors);
      return (actionForward);
   }

}