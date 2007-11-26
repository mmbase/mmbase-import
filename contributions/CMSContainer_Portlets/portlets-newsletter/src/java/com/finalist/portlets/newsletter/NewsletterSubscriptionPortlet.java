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
import com.finalist.community.CommunityManager;
import com.finalist.newsletter.util.NewsletterSubscriptionUtil;

public class NewsletterSubscriptionPortlet extends JspPortlet {

   private static Logger log = Logging.getLoggerInstance(NewsletterSubscriptionPortlet.class.getName());

   private final String TEMPLATE_LOGIN = CommunityManager.TEMPLATE_LOGIN;
   private final String TEMPLATE_OPTIONS = "newsletter/subscription/options.jsp";
   private final String TEMPLATE_SUBSCRIBE = "newsletter/subscription/subscribe.jsp";

   private static final String USER_SUBSCRIBED_THEMES = "subscriptions";
   private static final String AVAILABLE_NEWSLETTERS = "newsletters";


   @Override
   protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
      PortletSession session = request.getPortletSession();
      PortletPreferences preferences = request.getPreferences();

      String action = request.getParameter("action");

      if (isLoggedIn(session) == true) {
         String userName = (String) session.getAttribute("username");

         if (action == null) {
            log.debug("Action = null");

            List<String> mimeTypes = new ArrayList<String>();
            mimeTypes.add("text/html");
            mimeTypes.add("text/plain");
            request.setAttribute("mimetypes", mimeTypes);

            // List<String> subscriptions =
            // NewsletterSubscriptionUtil.getUserSubscribedThemes(userName);
            List<String> subscriptions = null;
            if (subscriptions != null) {
               log.debug("Has subscriptions");
               request.setAttribute(USER_SUBSCRIBED_THEMES, subscriptions);
               String status = NewsletterSubscriptionUtil.getSubscriptionStatus(userName);
               request.setAttribute("status", status);
               doInclude("view", TEMPLATE_OPTIONS, request, response);
            }
            else {
               log.debug("Has no subscriptions");
               doInclude("view", TEMPLATE_SUBSCRIBE, request, response);
            }
         }
         else {
            String template = "" + request.getParameter("template");
            doInclude("view", template, request, response);
         }
      }
      else {
         if (action != null && action.equals("login")) {
            doInclude("view", TEMPLATE_LOGIN, request, response);
         }
         else {
            String template = preferences.getValue(PortalConstants.CMSC_PORTLET_VIEW_TEMPLATE, null);
            if (template != null && template.length() > 0) {
               doInclude("view", template, request, response);
            }
         }
      }
   }


   @Override
   public void processView(ActionRequest request, ActionResponse response) throws PortletException, IOException {
      PortletPreferences pref = request.getPreferences();
      PortletSession session = request.getPortletSession();

      String action = request.getParameter("action");

      if (isLoggedIn(session) == true) {
         String userName = (String) session.getAttribute("username");
         if (action != null) {
            response.setWindowState(WindowState.MAXIMIZED);
            if (action.equals("subscribe")) {

               String[] themes = request.getParameterValues(NewsletterSubscriptionUtil.NEWSLETTER_THEME);
               if (themes != null && themes.length > 0) {
                  boolean result = NewsletterSubscriptionUtil.subscribeToThemes(userName, themes);
               }
            }
            else if (action.equals("change")) {
               String[] themes = request.getParameterValues("theme");
               // TODO, theme update
               String mimeType = request.getParameter("mimetype");
               if (mimeType != null) {
                  boolean result = NewsletterSubscriptionUtil.setPreferredMimeType(userName, mimeType);
               }
            }
            else if (action.equals("terminate")) {
               boolean result = NewsletterSubscriptionUtil.terminateUserSubscription(userName);
            }
            else if (action.equals("pause")) {
               boolean result = NewsletterSubscriptionUtil.pauseUserSubscriptions(userName);
            }
            else if (action.equals("resume")) {
               boolean result = NewsletterSubscriptionUtil.resumeUserSubscriptions(userName);
            }
         }
      }
   }


   @Override
   protected void doEditDefaults(RenderRequest request, RenderResponse response) throws IOException, PortletException {
      PortletPreferences preferences = request.getPreferences();
      super.doEditDefaults(request, response);
   }


   @Override
   public void processEditDefaults(ActionRequest request, ActionResponse response) throws PortletException, IOException {
      String[] availableNewsletters = request.getParameterValues(AVAILABLE_NEWSLETTERS);
      if (availableNewsletters != null && availableNewsletters.length > 0) {
         PortletPreferences preferences = request.getPreferences();
         String portletId = preferences.getValue(PortalConstants.CMSC_OM_PORTLET_ID, null);
         if (portletId != null) {
            this.setPortletNodeParameter(portletId, AVAILABLE_NEWSLETTERS, availableNewsletters);

         }
         else {
            log.debug("No portletId");
         }
      }
      else {
         log.debug("No newsletters selected");
      }

      super.processEditDefaults(request, response);
   }


   private boolean isLoggedIn(PortletSession session) {
      String userName = (String) session.getAttribute("username");
      if (userName != null && userName.length() > 0) {
         log.debug("Logged in as: " + userName);
         return (true);
      }
      return (false);
   }
}
