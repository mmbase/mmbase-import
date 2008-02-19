package com.finalist.portlets.newsletter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import com.finalist.cmsc.portalImpl.PortalConstants;
import com.finalist.cmsc.portlets.JspPortlet;
import com.finalist.newsletter.generator.NewsletterGeneratorFactory;
import com.finalist.newsletter.util.NewsletterSubscriptionUtil;
import com.finalist.newsletter.util.NewsletterUtil;

public class NewsletterSubscriptionPortlet extends JspPortlet {

   private static final String HAS_SUBSCRIPTIONS = "hassubscriptions";
   private static final String NEWSLETTERSUBSCRIPTIONS = "newslettersubscriptions";
   private static final String ACTION_SUBSCRIBE = "subscribe";
   private static final String ACTION_CHANGE = "change";
   private static final String ACTION_TERMINATE = "terminate";

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
      PortletSession session = request.getPortletSession(true);
      PortletPreferences preferences = request.getPreferences();

      String action = request.getParameter("action");

      if (isLoggedIn(session) == true) {
         String userName = getUserName(session);

         List<String> availableMimeTypes = NewsletterGeneratorFactory.getMimeTypes();
         request.setAttribute(NewsletterGeneratorFactory.AVAILABLE_MIMETYPES, availableMimeTypes);
         List<Integer> availableStatusOptions = NewsletterSubscriptionUtil.getStatusOptions();
         request.setAttribute(NewsletterSubscriptionUtil.STATUS_OPTIONS, availableStatusOptions);

         List<Integer> subscribedThemes = NewsletterSubscriptionUtil.getUserSubscribedThemes(userName);
         List<Integer> subscribedNewsletters = NewsletterSubscriptionUtil.getUserSubscribedNewsletters(userName);

         if (subscribedNewsletters != null && subscribedNewsletters.size() > 0) {
            request.setAttribute(NEWSLETTERSUBSCRIPTIONS, subscribedNewsletters);
         }

         if (subscribedThemes != null && subscribedThemes.size() > 0) {
            request.setAttribute(NewsletterSubscriptionUtil.NEWSLETTER_THEME, subscribedThemes);
         }

         if ((subscribedThemes != null && subscribedThemes.size() > 0) || (subscribedNewsletters != null && subscribedNewsletters.size() > 0)) {
            request.setAttribute(HAS_SUBSCRIPTIONS, true);
            String status = NewsletterSubscriptionUtil.getSubscriptionStatus(userName);
            request.setAttribute(NewsletterSubscriptionUtil.SUBSCRIPTION_STATUS_KEY, status);
            String preferredMimeType = NewsletterSubscriptionUtil.getPreferredMimeType(userName);
            request.setAttribute(NewsletterSubscriptionUtil.PREFERRED_MIMETYPE, preferredMimeType);
         }

         if (action != null) {
            String template = "" + request.getParameter("template");
            doInclude("view", template, request, response);
         } else {
            String template = preferences.getValue(PortalConstants.CMSC_PORTLET_VIEW_TEMPLATE, null);
            doInclude("view", template, request, response);
         }
      } else {

      }
   }

   private String getUserName(PortletSession session) {
      return ((String) session.getAttribute("userName", PortletSession.APPLICATION_SCOPE));

   }

   private boolean isLoggedIn(PortletSession session) {
      String userName = getUserName(session);
      if (userName != null && userName.length() > 0) {
         return (true);
      }
      return (false);
   }

   private void processChangeSubscription(ActionRequest request, ActionResponse response) {
      PortletSession session = request.getPortletSession();
      String userName = getUserName(session);
      NewsletterSubscriptionUtil.unsubscribeFromAllNewsletters(userName);
      processNewSubscription(request, response);
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

   private void processNewSubscription(ActionRequest request, ActionResponse response) {
      PortletSession session = request.getPortletSession();
      String userName = getUserName(session);
      List<Integer> subscribeToThemes = new ArrayList<Integer>();
      List<Integer> subscribeToNewsletters = new ArrayList<Integer>();

      String[] newsletters = request.getParameterValues(NEWSLETTERSUBSCRIPTIONS);
      if (newsletters != null) {
         List<Integer> newsletterList = new ArrayList<Integer>();
         for (int n = 0; n < newsletters.length; n++) {
            newsletterList.add(Integer.parseInt(newsletters[n]));
         }
         subscribeToNewsletters.addAll(newsletterList);
      }

      String[] themes = request.getParameterValues(NewsletterSubscriptionUtil.NEWSLETTER_THEME);
      if (themes != null) {
         for (int i = 0; i < themes.length; i++) {
            String theme = themes[i];
            int themeNumber = Integer.parseInt(theme);
            subscribeToThemes.add(themeNumber);
            int newsletterNumber = NewsletterUtil.findNewsletterForTheme(themeNumber);
            if (newsletterNumber > 0) {

               int defaultTheme = NewsletterUtil.getDefaultTheme(newsletterNumber);
               if (!subscribeToThemes.contains(defaultTheme)) {
                  subscribeToThemes.add(defaultTheme);
               }
               if (!subscribeToNewsletters.contains(newsletterNumber)) {
                  subscribeToNewsletters.add(newsletterNumber);
               }
            }
         }
      }

      NewsletterSubscriptionUtil.subscribeToNewsletters(userName, subscribeToNewsletters);
      NewsletterSubscriptionUtil.subscribeToThemes(userName, subscribeToThemes);
      String preferredMimeType = request.getParameter(NewsletterSubscriptionUtil.PREFERRED_MIMETYPE);
      NewsletterSubscriptionUtil.setPreferredMimeType(userName, preferredMimeType);
      int status = Integer.parseInt(request.getParameter(NewsletterSubscriptionUtil.SUBSCRIPTION_STATUS_KEY));
      NewsletterSubscriptionUtil.setSubscriptionStatus(userName, status);
   }

   private void processTermination(ActionRequest request, ActionResponse response) {
      PortletSession session = request.getPortletSession();
      String userName = getUserName(session);
      String confirmation = request.getParameter("confirm_unsubscribe");
      if (confirmation != null) {
         NewsletterSubscriptionUtil.terminateUserSubscription(userName);
      }
   }

   @Override
   public void processView(ActionRequest request, ActionResponse response) throws PortletException, IOException {
      PortletSession session = request.getPortletSession();

      String action = request.getParameter("action");

      if (isLoggedIn(session) == true) {
         if (action != null) {
            response.setWindowState(WindowState.MAXIMIZED);
            if (action.equals(ACTION_SUBSCRIBE)) {
               processNewSubscription(request, response);
            } else if (action.equals(ACTION_CHANGE)) {
               processChangeSubscription(request, response);
            } else if (action.equals(ACTION_TERMINATE)) {
               processTermination(request, response);
            }
         }
      }
   }
}
