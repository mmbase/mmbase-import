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
import java.util.ResourceBundle;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import net.sf.mmapps.commons.util.StringUtil;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.portalImpl.PortalConstants;
import com.finalist.cmsc.portlets.AbstractContentPortlet;
import com.finalist.newsletter.util.NewsletterSubscriptionUtil;
import com.finalist.newsletter.util.NewsletterUtil;

public class NewsletterContentPortlet extends AbstractContentPortlet {

   private static final String DEFAULTTHEME = "defaulttheme";
   private static final String ADDITIONAL_THEMES = "additionalthemes";
   private static final String DEFAULTARTICLES = "defaultarticles";

   private static Logger log = Logging.getLoggerInstance(NewsletterContentPortlet.class.getName());
   private static ResourceBundle rb = ResourceBundle.getBundle("portlets-newslettercontent");

   public static final String NEWSLETTERNUMBER = "newsletternumber";
   public static final String PUBLICATIONNUMBER = "publicationnumber";
   public static final String ARTICLES = "articles";

   public static final String DISPLAYTYPE = "displaytype";
   public static final String DISPLAYTYPE_NORMAL = "all";
   public static final String DISPLAYTYPE_PERSONALIZED = "personalized";
   public static final String DISPLAYTYPE_DEFAULT = DISPLAYTYPE_NORMAL;

   public static final String DUPLICATE_HANDLING = "duplicatehandling";
   public static final String DUPLICATE_HANDLING_SHOW = rb.getString("duplicatehandling.show");
   public static final String DUPLICATE_HANDLING_HIDE = rb.getString("duplicatehandling.hide");
   public static final String DUPLICATE_HANDLING_DEFAULT = DUPLICATE_HANDLING_HIDE;

   @Override
   protected void doEditDefaults(RenderRequest req, RenderResponse res) throws IOException, PortletException {
      List<String> duplicateHandlers = new ArrayList<String>();
      duplicateHandlers.add(NewsletterContentPortlet.DUPLICATE_HANDLING_SHOW);
      duplicateHandlers.add(NewsletterContentPortlet.DUPLICATE_HANDLING_HIDE);
      req.setAttribute("duplicatehandlers", duplicateHandlers);
      super.doEditDefaults(req, res);
   }

   @Override
   protected void doView(RenderRequest request, RenderResponse res) throws PortletException, java.io.IOException {
      log.debug("Executing the doView of the newsletter content portlet");
      PortletPreferences preferences = request.getPreferences();
      PortletSession session = request.getPortletSession(true);
      String template = preferences.getValue(PortalConstants.CMSC_PORTLET_VIEW_TEMPLATE, null);
      String duplicateHandling = preferences.getValue(DUPLICATE_HANDLING, null);
      String page = preferences.getValue(PAGE, null);

      if (page != null && !StringUtil.isEmptyOrWhitespace(page)) {

         String displayType = (String) request.getAttribute(DISPLAYTYPE);
         if (displayType == null) {
            log.debug("Display type is not set, default wil be used. Default is: " + DISPLAYTYPE_DEFAULT);
            displayType = DISPLAYTYPE_DEFAULT;
         }

         String number = null;
         String themeType = null;
         String newsletterNumber = page;
         String publicationNumber = request.getParameter(PUBLICATIONNUMBER);

         if (publicationNumber != null && !StringUtil.isEmptyOrWhitespace(publicationNumber)) {
            log.debug("User requeste to view publication: " + publicationNumber);
            themeType = NewsletterUtil.THEMETYPE_NEWSLETTERPUBLICATION;
            number = publicationNumber;
         } else {
            log.debug("User requested to view the default newsletter: " + newsletterNumber);
            themeType = NewsletterUtil.THEMETYPE_NEWSLETTER;
            number = newsletterNumber;
         }

         List<String> additionalThemes = null;
         List<String> availableThemes = NewsletterUtil.getAllThemes(number, themeType);
         if (availableThemes != null && availableThemes.size() > 0) {
            if (displayType.equals(DISPLAYTYPE_PERSONALIZED)) {
               log.debug("The user requested to view the output in normal style");
               String userName = (String) session.getAttribute("userName", PortletSession.APPLICATION_SCOPE);
               if (userName == null) {
                  userName = request.getParameter("userName");
               }
               if (userName != null) {
                  additionalThemes = NewsletterSubscriptionUtil.compareToUserSubscribedThemes(availableThemes, userName, number);
               }
            } else {
               log.debug("The user did not specify a display type and will get the default");
               additionalThemes = availableThemes;
            }
         } else {
            log.debug("No available themes have been found for number " + number + " and themetype " + themeType);
         }

         String defaultTheme = NewsletterUtil.getDefaultTheme(number, themeType);
         List<String> defaultArticles = NewsletterUtil.getArticlesForTheme(defaultTheme);
         if (defaultArticles != null && defaultArticles.size() > 0) {
            request.setAttribute(DEFAULTTHEME, defaultTheme);
            request.setAttribute(DEFAULTARTICLES, defaultArticles);
         }

         if (additionalThemes != null && additionalThemes.size() > 0) {
            log.debug("Processing " + additionalThemes.size() + " additional themes");

            List<String> temporaryArticleListing = new ArrayList<String>();
            temporaryArticleListing.addAll(defaultArticles);

            for (int i = 0; i < additionalThemes.size(); i++) {
               String themeNumber = additionalThemes.get(i);
               List<String> articles = NewsletterUtil.getArticlesForTheme(themeNumber);
               if (duplicateHandling.equals(DUPLICATE_HANDLING_HIDE)) {
                  articles = NewsletterUtil.removeDuplicates(temporaryArticleListing, articles);
               }
               if (articles != null && articles.size() > 0) {
                  request.setAttribute(ARTICLES + themeNumber, articles);
                  for (int a = 0; a < articles.size(); a++) {
                     temporaryArticleListing.add(articles.get(a));
                  }
               } else {
                  log.debug("No articles could are available for theme " + themeNumber);
                  additionalThemes.remove(themeNumber);
                  i--;
               }
            }
            request.setAttribute(ADDITIONAL_THEMES, additionalThemes);
         } else {
            log.debug("No themes are available");
         }
      } else {
         log.debug("The page number could not be found");
      }
      doInclude("view", template, request, res);
   }

   @Override
   public void processEditDefaults(ActionRequest request, ActionResponse response) throws PortletException, IOException {
      log.debug("processEditDefaults - PAGE = " + request.getParameter(PAGE));
      PortletPreferences preferences = request.getPreferences();
      String portletId = preferences.getValue(PortalConstants.CMSC_OM_PORTLET_ID, null);

      String duplicateHandling = request.getParameter(DUPLICATE_HANDLING);
      if (duplicateHandling == null) {
         duplicateHandling = DUPLICATE_HANDLING_DEFAULT;
      }
      setPortletParameter(portletId, DUPLICATE_HANDLING, duplicateHandling);
      super.processEditDefaults(request, response);
   }
}