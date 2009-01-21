package com.finalist.newsletter.forms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.web.struts.DispatchActionSupport;

import com.finalist.newsletter.services.NewsletterSubscriptionServices;

/**
 * Adding relationship between newsletter and person
 * 
 * @author Lisa
 * @version
 * 
 */
public class NewsletterSubscriptionAddRelAction extends DispatchActionSupport {

   private NewsletterSubscriptionServices subscriptionServices;

   /**
    * Initialize service object : subscriptionServices
    */
   protected void onInit() {
      super.onInit();
      subscriptionServices = (NewsletterSubscriptionServices) getWebApplicationContext().getBean(
            "subscriptionServices");
   }

   /**
    * unspecified adding relationship between newsletter and person, refreshing the newsletter subscriber list
    */
   protected ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response) throws Exception {

      log.debug("No parameter specified,go to dashboard");

      String newsletterId = request.getParameter("newsletterId");
      String authId = request.getParameter("authid");

      if (StringUtils.isNotBlank(newsletterId) && StringUtils.isNotBlank(authId)) {
         subscriptionServices.addNewRecord(Integer.parseInt(authId), Integer.parseInt(newsletterId));
      }
      request.setAttribute("newsletterId", request.getParameter("newsletterId"));
      return mapping.findForward("success");
   }

   /**
    * specified making selected person subscribing the newsletter ,refreshing the newsletter subscriber list
    * 
    * @param mapping
    * @param form
    * @param request
    * @param response
    * @return ActionForward refreshing the newsletter subscriber list
    * @throws Exception
    */
   public ActionForward subscribeNewsletters(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response) throws Exception {
      
      log.debug("With parameter subscribeNewsletters,go to search page");
      
      String newsletterId = request.getParameter("newsletterId");
      
      if (StringUtils.isNotBlank(newsletterId)) {
         String[] authIds = request.getParameterValues("chk_");
         for (String authId : authIds) {
            if (subscriptionServices.noSubscriptionRecord(Integer.parseInt(authId), Integer.parseInt(newsletterId))) {
               subscriptionServices.addNewRecord(Integer.parseInt(authId), Integer.parseInt(newsletterId));
            }
         }
      }
      ActionForward ret = new ActionForward(mapping.findForward("success").getPath() + "?newsletterId="
            + request.getParameter("newsletterId"));
      ret.setRedirect(true);
      return ret;
   }
}