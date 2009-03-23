package com.finalist.portlets.newsletter;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.portlet.*;
import org.apache.commons.lang.StringUtils;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.portalImpl.PortalConstants;
import com.finalist.cmsc.portlets.JspPortlet;
import com.finalist.cmsc.services.community.ApplicationContextFactory;
import com.finalist.newsletter.domain.Subscription;
import com.finalist.newsletter.services.CommunityModuleAdapter;
import com.finalist.newsletter.services.NewsletterSubscriptionServices;

public class NewsletterSubscriptionPortlet extends JspPortlet {
   private static Logger log = Logging.getLoggerInstance(NewsletterSubscriptionPortlet.class.getName());
   private static final String ALLOWED_NEWSLETTERS = "allowednewsletters";

   @Override
   protected void doEditDefaults(RenderRequest request, RenderResponse response) throws IOException, PortletException {
      PortletPreferences preferences = request.getPreferences();
      String[] newsletters = preferences.getValues(ALLOWED_NEWSLETTERS, null);
      if (newsletters != null) {
         request.setAttribute(ALLOWED_NEWSLETTERS, newsletters);
      }

      super.doEditDefaults(request, response);
   }

   @Override
   protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
      PortletPreferences preferences = request.getPreferences();
      NewsletterSubscriptionServices services = (NewsletterSubscriptionServices) ApplicationContextFactory.getBean("subscriptionServices");
      int userId = CommunityModuleAdapter.getCurrentUserId();
      String[] newsletters = preferences.getValues(ALLOWED_NEWSLETTERS, null);

      if (!CommunityModuleAdapter.isUserLogin() ||
            (services.getActiveSubscription(userId).size()<1 && null == request.getParameter("action"))) {
         request.setAttribute("isUserLogin", CommunityModuleAdapter.isUserLogin());
         doInclude("view", "/newsletter/subscription/introduction.jsp", request, response);
         return;
      }
   
      String action = request.getParameter("action");
      log.debug(String.format("User %s start a subscribe with action %s", userId, action));

      if ("pause".equals(action)) {
         log.debug("Paused subscriptions ,need confrim");

         List<Subscription> subscriptionsToBePause = getSubscriptionsFromParameter(request,Subscription.STATUS.ACTIVE);
         request.setAttribute("subscriptionsToBePause", subscriptionsToBePause);
         doInclude("view", "/newsletter/subscription/pauseform.jsp", request, response);
         return;
      }
      else if ("resume".equals(action)) {
         log.debug("Resume paused subscriptions ,need confrim");

         List<Subscription> subscriptionsToBeResume = getSubscriptionsFromParameter(request, Subscription.STATUS.PAUSED);
         request.setAttribute("subscriptionsToBeResume", subscriptionsToBeResume);
         doInclude("view", "/newsletter/subscription/confirmResume.jsp", request, response);
         return;
      }
      else if ("terminate".equals(action)) {
         log.debug("Terminate subscriptions ,need confrim");

         List<Subscription> subscriptionsToBeTerminate = getSubscriptionsFromParameter(request,null);

         request.setAttribute("subscriptionsToBeTerminate", subscriptionsToBeTerminate);
         doInclude("view", "/newsletter/subscription/confirmTerminate.jsp", request, response);
         return;
      }

      List<Subscription> subscriptionList = services.getSubscriptionList(newsletters, userId);
      request.setAttribute("subscriptionList", subscriptionList);
      doInclude("view", "/newsletter/subscription/subscribe.jsp", request, response);

   }

   private List<Subscription> getSubscriptionsFromParameter(RenderRequest request, Subscription.STATUS status) {
      NewsletterSubscriptionServices services = (NewsletterSubscriptionServices) ApplicationContextFactory.getBean("subscriptionServices");

      List<Subscription> subscriptions = new ArrayList<Subscription>();
      if (null != request.getParameterValues("subscriptions")) {
         for (String sId : request.getParameterValues("subscriptions")) {
            Subscription subscription = services.getSubscription(sId);
            if (null==status||status.equals(subscription.getStatus())) {
               subscriptions.add(services.getSubscription(sId));
            }
         }
      }
      return subscriptions;
   }

   @Override
   public void processEditDefaults(ActionRequest request, ActionResponse response) throws PortletException, IOException {
      String[] availableNewsletters = request.getParameterValues(ALLOWED_NEWSLETTERS);
      PortletPreferences preferences = request.getPreferences();
      String portletId = preferences.getValue(PortalConstants.CMSC_OM_PORTLET_ID, null);
      if (portletId != null) {
         setPortletNodeParameter(portletId, ALLOWED_NEWSLETTERS, availableNewsletters);
      }
      super.processEditDefaults(request, response);
   }


   @Override
   public void processView(ActionRequest request, ActionResponse response) throws PortletException, IOException {

      String action = request.getParameter("action");
      log.debug("Process view action:" + action);
      if (action != null) {
         if ("pause".equals(action)) {
            processPause(request, response);
         }
         else if ("terminate".equals(action)) {
            processTermination(request, response);
         }
         else if ("resume".equals(action)) {
            processResume(request, response);
         }
      }

   }

   private void processTermination(ActionRequest request, ActionResponse response) {
      NewsletterSubscriptionServices services = (NewsletterSubscriptionServices) ApplicationContextFactory.getBean("subscriptionServices");
      String confirmation = request.getParameter("confirm_unsubscribe");

      String[] subscriptionIds = request.getParameterValues("subscriptions");
      log.debug(String.format("Terminate subscription %s confirm:%s", subscriptionIds, confirmation));

      if (confirmation != null) {
         for (String id : subscriptionIds) {
            services.terminateUserSubscription(id);
         }
      }
      else {
         response.setRenderParameters(request.getParameterMap());
      }
   }

   private void processResume(ActionRequest request, ActionResponse response) {
      NewsletterSubscriptionServices services = (NewsletterSubscriptionServices) ApplicationContextFactory.getBean("subscriptionServices");
      String confirmation = request.getParameter("confirm_resume");

      String[] subscriptionIds = request.getParameterValues("subscriptions");
      log.debug(String.format("resume subscription %s confirm:%s", subscriptionIds, confirmation));

      if (confirmation != null) {
         for (String id : subscriptionIds) {
            services.resume(id);
         }
      }
      else {
         response.setRenderParameters(request.getParameterMap());
      }
   }

   private void processPause(ActionRequest request, ActionResponse response) {

      NewsletterSubscriptionServices services = (NewsletterSubscriptionServices) ApplicationContextFactory.getBean("subscriptionServices");
      String duration = request.getParameter("timeduration");
      String durationunit = request.getParameter("durationunit");
      String resumeDate = request.getParameter("resumeDate");

      String confirmation = request.getParameter("confirm_pause");

      String[] subscriptionIds = request.getParameterValues("subscriptions");
      log.debug(String.format("pause subscription %s confirm:%s", subscriptionIds, confirmation));

      if (confirmation != null) {
         for (String id : subscriptionIds) {
            if (StringUtils.isBlank(resumeDate)) {
               services.pause(id, duration, durationunit);
            }
            else {
               services.pause(id, resumeDate);
            }
         }
      }
      else {
         response.setRenderParameters(request.getParameterMap());
      }


   }
}
