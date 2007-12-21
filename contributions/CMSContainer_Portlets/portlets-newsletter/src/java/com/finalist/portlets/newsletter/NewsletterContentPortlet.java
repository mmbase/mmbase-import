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

import com.finalist.cmsc.beans.om.Page;
import com.finalist.cmsc.portalImpl.PortalConstants;
import com.finalist.cmsc.portlets.AbstractContentPortlet;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;
import com.finalist.newsletter.util.NewsletterSubscriptionUtil;
import com.finalist.newsletter.util.NewsletterUtil;

public class NewsletterContentPortlet extends AbstractContentPortlet {

   private static final String KEY_DUPLICATEHANDLERS = "duplicatehandlers";
   private static final String KEY_DEFAULTTHEME = "defaulttheme";
   private static final String KEY_ADDITIONAL_THEMES = "additionalthemes";
   private static final String KEY_DEFAULTARTICLES = "defaultarticles";

   private static ResourceBundle rb = ResourceBundle.getBundle("portlets-newslettercontent");

   public static final String KEY_ARTICLES = "articles";

   public static final String KEY_DISPLAYTYPE = "displaytype";
   public static final String DISPLAYTYPE_NORMAL = "all";
   public static final String DISPLAYTYPE_PERSONALIZED = "personalized";
   public static final String DISPLAYTYPE_DEFAULT = DISPLAYTYPE_NORMAL;

   public static final String DUPLICATE_HANDLING_TYPE = "duplicatehandling";
   public static final String DUPLICATE_HANDLING_SHOW = rb.getString("duplicatehandling.show");
   public static final String DUPLICATE_HANDLING_HIDE = rb.getString("duplicatehandling.hide");
   public static final String DUPLICATE_HANDLING_TYPE_DEFAULT = DUPLICATE_HANDLING_HIDE;

   private static List<String> duplicateHandlers = new ArrayList<String>();

   static {
      duplicateHandlers.add(NewsletterContentPortlet.DUPLICATE_HANDLING_SHOW);
      duplicateHandlers.add(NewsletterContentPortlet.DUPLICATE_HANDLING_HIDE);
   }

   @Override
   protected void doView(RenderRequest request, RenderResponse res) throws PortletException, java.io.IOException {
      PortletPreferences preferences = request.getPreferences();
      PortletSession session = request.getPortletSession(true);
      String template = preferences.getValue(PortalConstants.CMSC_PORTLET_VIEW_TEMPLATE, null);
      String duplicateHandling = preferences.getValue(DUPLICATE_HANDLING_TYPE, null);

      String currentPath = getUrlPath(request);
      Page result = SiteManagement.getPageFromPath(currentPath);
      

      if (result != null) {
         String pageNumber = String.valueOf(result.getId());

         if (NewsletterUtil.isNewsletterOrPublication(pageNumber)) {
            String displayType = determineDisplayType(request);

            String defaultTheme = NewsletterUtil.getDefaultTheme(pageNumber);
            List<String> defaultArticles = NewsletterUtil.getArticlesForTheme(defaultTheme);
            if (defaultArticles != null && defaultArticles.size() > 0) {
               request.setAttribute(KEY_DEFAULTTHEME, defaultTheme);
               request.setAttribute(KEY_DEFAULTARTICLES, defaultArticles);
            }

            List<String> additionalThemes = null;
            List<String> availableThemes = NewsletterUtil.getAllThemes(pageNumber);
            if (availableThemes != null && availableThemes.size() > 0) {
               if (displayType.equals(DISPLAYTYPE_PERSONALIZED)) {
                  String userName = getUserName(session);
                  if (userName != null) {
                     additionalThemes = NewsletterSubscriptionUtil.compareToUserSubscribedThemes(availableThemes, userName, pageNumber);
                  }
               } else {
                  additionalThemes = availableThemes;
               }
            }

            if (additionalThemes != null && additionalThemes.size() > 0) {
               List<String> temporaryArticleListing = new ArrayList<String>();
               temporaryArticleListing.addAll(defaultArticles);

               for (int i = 0; i < additionalThemes.size(); i++) {
                  String themeNumber = additionalThemes.get(i);
                  List<String> articles = NewsletterUtil.getArticlesForTheme(themeNumber);
                  if (duplicateHandling.equals(DUPLICATE_HANDLING_HIDE)) {
                     articles = NewsletterUtil.removeDuplicates(temporaryArticleListing, articles);
                  }
                  if (articles != null && articles.size() > 0) {
                     request.setAttribute(KEY_ARTICLES + themeNumber, articles);
                     for (int a = 0; a < articles.size(); a++) {
                        temporaryArticleListing.add(articles.get(a));
                     }
                  } else {
                     additionalThemes.remove(themeNumber);
                     i--;
                  }
               }
               request.setAttribute(KEY_ADDITIONAL_THEMES, additionalThemes);
            }
         } else {
            throw new RuntimeException("Newsletterportlet placed on non-newsletter node");
         }
      } else {
         throw new RuntimeException("The page number could not be found");
      }
      doInclude("view", template, request, res);
   }

   private String getUserName(PortletSession session) {
      String userName = (String) session.getAttribute("userName", PortletSession.APPLICATION_SCOPE);
      return userName;
   }

   private String determineDisplayType(RenderRequest request) {
      String displayType = (String) request.getAttribute(KEY_DISPLAYTYPE);
      if (displayType == null) {
         displayType = DISPLAYTYPE_DEFAULT;
      }
      return displayType;
   }

   @Override
   protected void doEditDefaults(RenderRequest request, RenderResponse response) throws IOException, PortletException {
      request.setAttribute(KEY_DUPLICATEHANDLERS, duplicateHandlers);
      super.doEditDefaults(request, response);
   }

   @Override
   public void processEditDefaults(ActionRequest request, ActionResponse response) throws PortletException, IOException {
      PortletPreferences preferences = request.getPreferences();
      String portletId = preferences.getValue(PortalConstants.CMSC_OM_PORTLET_ID, null);
      String duplicateHandling = request.getParameter(DUPLICATE_HANDLING_TYPE);
      if (duplicateHandling == null) {
         duplicateHandling = DUPLICATE_HANDLING_TYPE_DEFAULT;
      }
      setPortletParameter(portletId, DUPLICATE_HANDLING_TYPE, duplicateHandling);
      super.processEditDefaults(request, response);
   }
}