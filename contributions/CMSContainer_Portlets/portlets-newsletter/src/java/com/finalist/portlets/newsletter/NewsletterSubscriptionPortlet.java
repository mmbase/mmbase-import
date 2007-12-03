/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
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

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.portalImpl.PortalConstants;
import com.finalist.cmsc.portlets.JspPortlet;
import com.finalist.newsletter.NewsletterGeneratorFactory;
import com.finalist.newsletter.util.NewsletterSubscriptionUtil;
import com.finalist.newsletter.util.NewsletterUtil;

public class NewsletterSubscriptionPortlet extends JspPortlet {

   private static Logger log = Logging.getLoggerInstance(NewsletterSubscriptionPortlet.class.getName());

   private static final String USER_SUBSCRIBED_THEMES = "subscriptions";
   private static final String AVAILABLE_NEWSLETTERS = "newsletters";

   private final String TEMPLATE_OPTIONS = "newsletter/subscription/options.jsp";
   private final String TEMPLATE_SUBSCRIBE = "newsletter/subscription/subscribe.jsp";

   @Override
   protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
      PortletSession session = request.getPortletSession();
      PortletPreferences preferences = request.getPreferences();

      String action = request.getParameter("action");

      if (isLoggedIn(session) == true) {
         String userName = (String) session.getAttribute("username");

         if (action == null) {
            log.debug("Action = null");
            request.setAttribute(NewsletterGeneratorFactory.AVAILABLE_MIMETYPES, NewsletterGeneratorFactory.mimeTypes);
            List<String> subscriptions = NewsletterSubscriptionUtil.getUserSubscribedThemes(userName);
            if (subscriptions != null) {
               log.debug(userName + " has " + subscriptions.size() + " subscriptions");
               request.setAttribute(USER_SUBSCRIBED_THEMES, subscriptions);
               String status = NewsletterSubscriptionUtil.getSubscriptionStatus(userName);

               request.setAttribute("status", status);
               doInclude("view", TEMPLATE_OPTIONS, request, response);
            } else {
               log.debug(userName + " has no subscriptions");
               doInclude("view", TEMPLATE_SUBSCRIBE, request, response);
            }
         } else {
            String template = "" + request.getParameter("template");
            doInclude("view", template, request, response);
         }
      } else {
         if (action != null && action.equals("login")) {

         } else {
            String template = preferences.getValue(PortalConstants.CMSC_PORTLET_VIEW_TEMPLATE, null);
            if (template != null && template.length() > 0) {
               doInclude("view", template, request, response);
            }
         }
      }
   }

   private boolean isLoggedIn(PortletSession session) {
      String userName = (String) session.getAttribute("username");
      if (userName != null && userName.length() > 0) {
         log.debug("Logged in as: " + userName);
         return (true);
      }
      return (false);
   }

   private void processChangeSubscription(ActionRequest request, String userName) {
      NewsletterSubscriptionUtil.unsubscribeFromAllThemes(userName);
      processNewSubscription(request, userName);
   }

   @Override
   public void processEditDefaults(ActionRequest request, ActionResponse response) throws PortletException, IOException {
      String[] availableNewsletters = request.getParameterValues(AVAILABLE_NEWSLETTERS);
      if (availableNewsletters != null && availableNewsletters.length > 0) {
         PortletPreferences preferences = request.getPreferences();
         String portletId = preferences.getValue(PortalConstants.CMSC_OM_PORTLET_ID, null);
         if (portletId != null) {
            setPortletNodeParameter(portletId, AVAILABLE_NEWSLETTERS, availableNewsletters);

         } else {
            log.debug("No portletId");
         }
      } else {
         log.debug("No newsletters selected");
      }

      super.processEditDefaults(request, response);
   }

   private void processNewSubscription(ActionRequest request, String userName) {
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
      NewsletterSubscriptionUtil.subscribeToThemes(userName, subscribeToThemes);
      String preferredMimeType = request.getParameter(NewsletterSubscriptionUtil.PREFERRED_MIMETYPE);
      NewsletterSubscriptionUtil.setPreferredMimeType(userName, preferredMimeType);
   }

   @Override
   public void processView(ActionRequest request, ActionResponse response) throws PortletException, IOException {
      PortletSession session = request.getPortletSession();

      String action = request.getParameter("action");

      if (isLoggedIn(session) == true) {
         String userName = (String) session.getAttribute("username");
         if (action != null) {
            response.setWindowState(WindowState.MAXIMIZED);
            if (action.equals("subscribe")) {
               processNewSubscription(request, userName);
            } else if (action.equals("change")) {
               processChangeSubscription(request, userName);
            } else if (action.equals("terminate")) {
               NewsletterSubscriptionUtil.terminateUserSubscription(userName);
            } else if (action.equals("pause")) {
               NewsletterSubscriptionUtil.pauseUserSubscriptions(userName);
            } else if (action.equals("resume")) {
               NewsletterSubscriptionUtil.resumeUserSubscriptions(userName);
            }
         }
      }
   }
}
