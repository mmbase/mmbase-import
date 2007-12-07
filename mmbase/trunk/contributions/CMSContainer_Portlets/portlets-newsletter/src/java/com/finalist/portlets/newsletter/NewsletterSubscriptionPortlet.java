package com.finalist.portlets.newsletter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.portalImpl.PortalConstants;
import com.finalist.cmsc.portlets.JspPortlet;
import com.finalist.newsletter.NewsletterGeneratorFactory;
import com.finalist.newsletter.util.NewsletterSubscriptionUtil;
import com.finalist.newsletter.util.NewsletterUtil;

public class NewsletterSubscriptionPortlet extends JspPortlet {

   private static final String HAS_SUBSCRIPTIONS = "hassubscriptions";
   private static final String ACTION_SUBSCRIBE = "subscribe";
   private static final String ACTION_CHANGE = "change";
   private static final String ACTION_TERMINATE = "terminate";

   private static Logger log = Logging.getLoggerInstance(NewsletterSubscriptionPortlet.class.getName());

   private static final String AVAILABLE_NEWSLETTERS = "newsletters";

   @Override
   protected void doEditDefaults(RenderRequest request, RenderResponse response) throws IOException, PortletException {
      PortletPreferences preferences = request.getPreferences();
      String[] newsletters = preferences.getValues(AVAILABLE_NEWSLETTERS, null);
      if (newsletters != null) {
         request.setAttribute(AVAILABLE_NEWSLETTERS, newsletters);
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
         List<String> subscriptions = NewsletterSubscriptionUtil.getUserSubscribedThemes(userName);

         List<String> mimeTypes = NewsletterGeneratorFactory.getMimeTypes();
         request.setAttribute(NewsletterGeneratorFactory.AVAILABLE_MIMETYPES, mimeTypes);

         List<String> statusOptions = NewsletterSubscriptionUtil.getStatusOptions();
         request.setAttribute(NewsletterSubscriptionUtil.STATUS_OPTIONS, statusOptions);

         if (subscriptions != null && subscriptions.size() > 0) {
            request.setAttribute(HAS_SUBSCRIPTIONS, true);
            request.setAttribute(NewsletterSubscriptionUtil.NEWSLETTER_THEME, subscriptions);

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
         log.debug("Logged in as: " + userName);
         return (true);
      }
      log.debug("Not logged in");
      return (false);
   }

   private void processChangeSubscription(ActionRequest request, ActionResponse response) {
      PortletSession session = request.getPortletSession();
      String userName = getUserName(session);
      NewsletterSubscriptionUtil.unsubscribeFromAllThemes(userName);
      processNewSubscription(request, response);
   }

   @Override
   public void processEditDefaults(ActionRequest request, ActionResponse response) throws PortletException, IOException {
      String[] availableNewsletters = request.getParameterValues(AVAILABLE_NEWSLETTERS);
         PortletPreferences preferences = request.getPreferences();
         String portletId = preferences.getValue(PortalConstants.CMSC_OM_PORTLET_ID, null);
         if (portletId != null) {            
            setPortletNodeParameter(portletId, AVAILABLE_NEWSLETTERS, availableNewsletters);
         } else {
            log.debug("No portletId");
         }

      super.processEditDefaults(request, response);
   }

   private void processNewSubscription(ActionRequest request, ActionResponse response) {
      PortletSession session = request.getPortletSession();
      String userName = getUserName(session);
      List<String> subscribeToThemes = new ArrayList<String>();
      String[] newsletters = request.getParameterValues("newsletter");
      if (newsletters != null) {
         for (int i = 0; i < newsletters.length; i++) {
            String newsletterNumber = newsletters[i];
            String defaultTheme = NewsletterUtil.getDefaultTheme(newsletterNumber, NewsletterUtil.THEMETYPE_NEWSLETTER);
            subscribeToThemes.add(defaultTheme);
            log.debug("Adding default theme to subscription list " + defaultTheme);
         }
      }
      String[] themes = request.getParameterValues(NewsletterSubscriptionUtil.NEWSLETTER_THEME);
      if (themes != null) {
         for (int i = 0; i < themes.length; i++) {
            String themeNumber = themes[i];
            subscribeToThemes.add(themeNumber);
            log.debug("Adding theme to subscription list " + themeNumber);
            String newsletterNumber = NewsletterUtil.findNewsletterForTheme(themeNumber);
            if (newsletterNumber != null) {
               String defaultTheme = NewsletterUtil.getDefaultTheme(newsletterNumber, NewsletterUtil.NEWSLETTER);
               if (!subscribeToThemes.contains(defaultTheme)) {
                  subscribeToThemes.add(defaultTheme);
                  log.debug("Adding default theme to subscription list " + defaultTheme);
               }
            }
         }
      }
      if (newsletters != null) {
         List<String> newsletterList = Arrays.asList(newsletters);
         NewsletterSubscriptionUtil.subscribeToNewsletters(userName, newsletterList);
      }
      NewsletterSubscriptionUtil.subscribeToThemes(userName, subscribeToThemes);
      String preferredMimeType = request.getParameter(NewsletterSubscriptionUtil.PREFERRED_MIMETYPE);
      NewsletterSubscriptionUtil.setPreferredMimeType(userName, preferredMimeType);
      String status = request.getParameter(NewsletterSubscriptionUtil.SUBSCRIPTION_STATUS_KEY);
      NewsletterSubscriptionUtil.setSubscriptionStatus(userName, status);
   }

   @Override
   public void processView(ActionRequest request, ActionResponse response) throws PortletException, IOException {
      PortletSession session = request.getPortletSession();

      String action = request.getParameter("action");

      if (isLoggedIn(session) == true) {
         String userName = getUserName(session);
         if (action != null) {
            response.setWindowState(WindowState.MAXIMIZED);
            if (action.equals(ACTION_SUBSCRIBE)) {
               processNewSubscription(request, response);
            } else if (action.equals(ACTION_CHANGE)) {
               processChangeSubscription(request, response);
            } else if (action.equals(ACTION_TERMINATE)) {
               NewsletterSubscriptionUtil.terminateUserSubscription(userName);
            }
         }
      }
   }
}
