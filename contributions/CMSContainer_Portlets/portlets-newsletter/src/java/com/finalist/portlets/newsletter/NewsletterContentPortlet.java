/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.portlets.newsletter;

import java.io.IOException;
import java.util.List;

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

   private static Logger log = Logging.getLoggerInstance(NewsletterContentPortlet.class.getName());

   public static final String NEWSLETTERNUMBER = "newsletternumber";
   public static final String PUBLICATIONNUMBER = "publicationnumber";

   public static final String DISPLAYTYPE = "displaytype";
   public static final String DISPLAYTYPE_NORMAL = "all";
   public static final String DISPLAYTYPE_PERSONALIZED = "personalized";
   public static final String DISPLAYTYPE_DEFAULT = DISPLAYTYPE_NORMAL;


   @Override
   protected void doView(RenderRequest req, RenderResponse res) throws PortletException, java.io.IOException {
      log.debug("Executing the doView of the newsletter content portlet");
      PortletPreferences preferences = req.getPreferences();
      PortletSession session = req.getPortletSession(true);
      String template = preferences.getValue(PortalConstants.CMSC_PORTLET_VIEW_TEMPLATE, null);
      String page = preferences.getValue(PAGE, null);
      String newsletterNumber = page;

      if (newsletterNumber != null && !StringUtil.isEmptyOrWhitespace(newsletterNumber)) {
         log.debug("NewsletterNumber is " + newsletterNumber);
         setAttribute(req, PAGE, newsletterNumber);
         String displayType = "" + req.getParameter(DISPLAYTYPE);
         if (StringUtil.isEmpty(displayType)) {
            log.debug("Display type is not set, default wil be used. Default is: " + DISPLAYTYPE_DEFAULT);
            displayType = DISPLAYTYPE_DEFAULT;
         }

         String userName = (String) session.getAttribute("username");
         List<String> themes = null;
         String themeType = null;
         String number = null;

         String publicationNumber = req.getParameter(PUBLICATIONNUMBER);
         if (!StringUtil.isEmpty(publicationNumber)) {
            log.debug("User requeste to view publication: " + publicationNumber);
            themeType = NewsletterUtil.THEMETYPE_NEWSLETTERPUBLICATION;
            number = publicationNumber;
         }
         else {
            log.debug("User requested to view the default newsletter: " + newsletterNumber);
            themeType = NewsletterUtil.THEMETYPE_NEWSLETTER;
            number = newsletterNumber;
         }

         List<String> availableThemes = NewsletterUtil.getAllThemes(number, themeType);
         if (availableThemes != null || availableThemes.size() > 0) {
            log.debug("Found " + availableThemes.size() + " available themes");
            if (displayType.equals(DISPLAYTYPE_NORMAL)) {
               log.debug("The user requested to view the output in normal style");
               themes = NewsletterSubscriptionUtil.compareToUserSubscribedThemes(availableThemes, userName,
                     newsletterNumber);
            }
            else {
               log.debug("The user did not specify a display type and will get the default");
               themes = availableThemes;
            }
         }
         else {
            log.debug("No available themes have been found for number " + number + " and themetype " + themeType);
         }

         //

         if (themes != null && themes.size() > 0) {
            log.debug("After checking themes: " + themes.size() + " themes");
            setAttribute(req, "themes", themes);
            for (int i = 0; i < themes.size(); i++) {
               String themeNumber = themes.get(i);
               List<String> articles = NewsletterUtil.getArticlesForTheme(themeNumber);
               if (articles != null && articles.size() > 0) {
                  setAttribute(req, themeNumber, articles);
                  log.debug("Found " + articles.size() + " articles for theme " + themeNumber);
               }
               else {
                  log.debug("No articles could are available for theme " + themeNumber);
               }
            }
         }
         else {
            log.debug("No themes are available");
         }
      }
      else {
         log.debug("The page number could not be found");
      }
      doInclude("view", template, req, res);
   }


   @Override
   protected void doEditDefaults(RenderRequest req, RenderResponse res) throws IOException, PortletException {
      super.doEditDefaults(req, res);
   }


   @Override
   public void processEditDefaults(ActionRequest request, ActionResponse response) throws PortletException, IOException {
      log.debug("processEditDefaults - PAGE = " + request.getParameter(PAGE));
      super.processEditDefaults(request, response);
   }
}