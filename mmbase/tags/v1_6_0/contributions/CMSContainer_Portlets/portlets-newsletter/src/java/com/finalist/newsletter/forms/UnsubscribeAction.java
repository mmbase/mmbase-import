package com.finalist.newsletter.forms;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.finalist.cmsc.services.community.ApplicationContextFactory;
import com.finalist.newsletter.services.NewsletterSubscriptionServices;

public class UnsubscribeAction extends Action {

   private static Log log = LogFactory.getLog(UnsubscribeAction.class);

   private static final String ACTION_REMOVE = "remove";

   NewsletterSubscriptionServices service;


   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                HttpServletResponse response) throws Exception {
      service = (NewsletterSubscriptionServices) ApplicationContextFactory.getBean("subscriptionServices");
      log.debug("Excute unsubscribe");
      int userId = Integer.parseInt(request.getParameter("userId"));
      int newsletterId = Integer.parseInt(request.getParameter("newsletterId"));

      if (isRemoveAction(request)) {

         service.modifyStauts(userId, newsletterId, "INACTIVE");
         return mapping.findForward("finish");
      }

      return mapping.findForward("delete");
   }

   private boolean isRemoveAction(HttpServletRequest request) {
      return getParameter(request, ACTION_REMOVE) != null;
   }

   public String getParameter(HttpServletRequest request, String name) {
      String value = request.getParameter(name);
      if (value == null) {
         value = (String) request.getAttribute(name);
      }
      return value;
   }
}
