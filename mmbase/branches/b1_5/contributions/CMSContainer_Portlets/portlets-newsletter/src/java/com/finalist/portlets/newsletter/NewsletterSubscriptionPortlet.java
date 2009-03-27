package com.finalist.portlets.newsletter;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.portalImpl.PortalConstants;
import com.finalist.cmsc.portlets.JspPortlet;
import com.finalist.cmsc.services.community.ApplicationContextFactory;
import com.finalist.newsletter.domain.Subscription;
import com.finalist.newsletter.domain.Subscription.STATUS;
import com.finalist.newsletter.services.CommunityModuleAdapter;
import com.finalist.newsletter.services.NewsletterSubscriptionServices;

public class NewsletterSubscriptionPortlet extends JspPortlet {
   
   protected  static final String NEWSLETTER_SUBSCRIPTION_INTRODUCTION_JSP = "/newsletter/subscription/introduction.jsp";
   protected  static final String NEWSLETTER_SUBSCRIPTION_SUBSCRIBE_JSP = "/newsletter/subscription/subscribe.jsp";
   private  static final String SUBSCRIPTION_LIST = "subscriptionList";
   private static final String IS_USER_LOGIN = "isUserLogin";
   private static final String VIEW = "view";
   private static final String SUBSCRIPTIONS = "subscriptions";
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

      if (!CommunityModuleAdapter.isUserLogin()) {
         request.setAttribute(IS_USER_LOGIN, CommunityModuleAdapter.isUserLogin());
         doInclude(VIEW, NEWSLETTER_SUBSCRIPTION_INTRODUCTION_JSP, request, response);
         return;
      }
   
      List<Subscription> subscriptionList = services.getSubscriptionList(newsletters, userId);
      request.setAttribute(SUBSCRIPTION_LIST, subscriptionList);
      doInclude(VIEW, NEWSLETTER_SUBSCRIPTION_SUBSCRIBE_JSP, request, response);

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
      PortletPreferences preferences = request.getPreferences();
      String[] allNewsletters = preferences.getValues(ALLOWED_NEWSLETTERS, null);
      String[] newsletters = request.getParameterValues(SUBSCRIPTIONS);
      NewsletterSubscriptionServices services = (NewsletterSubscriptionServices) ApplicationContextFactory.getBean("subscriptionServices");
      int subscriberId = CommunityModuleAdapter.getCurrentUserId();
      List<Subscription> allSubscribtions = services.getSubscriptions(allNewsletters, subscriberId);
      
      if(newsletters == null) {
         for (Subscription subscription : allSubscribtions) {
            services.terminateUserSubscription(Integer.toString(subscription.getId()));
         }
      }
      else {
         if(allSubscribtions.size() == 0) {
            for (String newsletterId : newsletters) {
               services.addNewRecord(subscriberId, Integer.valueOf(newsletterId));
            }
         }
         else {
            List<String> newsletterList = Arrays.asList(newsletters);
            List<Integer> subscribtions = new ArrayList<Integer>();
            for(Subscription subscription : allSubscribtions) {
               if (newsletterList.contains(String.valueOf(subscription.getNewsletter().getId()))) {
                  if(!STATUS.ACTIVE.equals(subscription.getStatus())){
                     services.resume(Integer.toString(subscription.getId()));
                  }
               }
               else {
                  services.terminateUserSubscription(Integer.toString(subscription.getId()));
               }
               subscribtions.add(subscription.getNewsletter().getId());
            }
            for (String newsletterId : newsletters) {
               if( !subscribtions.contains(Integer.valueOf(newsletterId))) {
                  services.addNewRecord(subscriberId, Integer.valueOf(newsletterId));
               }
            }
         }
      }
   }
}
