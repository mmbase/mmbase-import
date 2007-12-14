package com.finalist.newsletter.util;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.services.community.NewsletterCommunication;
import com.finalist.newsletter.NewsletterGeneratorFactory;

public abstract class NewsletterSubscriptionUtil {

   private static Logger log = Logging.getLoggerInstance(NewsletterSubscriptionUtil.class.getName());
   private static final ResourceBundle rb = ResourceBundle.getBundle("portlets-newslettersubscription");

   public static final String NEWSLETTER = "newsletter";
   public static final String NEWSLETTER_THEME = "newslettertheme";
   public static final String PREFERRED_MIMETYPE = "preferredmimetype";

   public static final String MIMETYPE_HTML = "text/html";
   public static final String MIMETYPE_PLAIN = "text/plain";
   public static final String MIMETIPE_DEFAULT = MIMETYPE_HTML;
   public static final String USERNAME = "username";

   public static final String SUBSCRIPTION_STATUS_KEY = "subscriptionstatus";
   public static final String SUBSCRIPTION_STATUS_ACTIVE = rb.getString("status.active");
   public static final String SUBSCRIPTION_STATUS_INACTIVE = rb.getString("status.inactive");
   public static final String SUBSCRIPTION_STATUS_DEFAULT = SUBSCRIPTION_STATUS_ACTIVE;
   public static final String STATUS_OPTIONS = "statusoptions";

   private static List<String> statusOptions = new ArrayList<String>();

   static {
      statusOptions.add(SUBSCRIPTION_STATUS_ACTIVE);
      statusOptions.add(SUBSCRIPTION_STATUS_INACTIVE);
   }

   // public static final List AVAILABLE_MIMETYPES = new ArrayList<String>()
   // {MIMETYPE_HTML, MIMETYPE_PLAIN;;

   public static List<String> compareToUserSubscribedThemes(List<String> compareWithThemes, String userName, String newsletterNumber) {
      if (compareWithThemes == null || userName == null || newsletterNumber == null) {
         return (null);
      }
      List<String> userThemes = getUserSubscribedThemes(userName, newsletterNumber);
      List<String> themes = new ArrayList<String>();
      for (int i = 0; i < compareWithThemes.size(); i++) {
         String theme = compareWithThemes.get(i);
         if (userThemes.contains(theme)) {
            themes.add(theme);
         }
      }
      return (themes);
   }

   public static String getPreferredMimeType(String userName) {
      if (userName != null) {
         String preferredMimeType = NewsletterCommunication.getUserPreference(userName, PREFERRED_MIMETYPE);
         log.debug("Found preferred mimetype " + preferredMimeType + " for user " + userName);
         return (preferredMimeType);
      }
      return (null);
   }

   public static List<String> getStatusOptions() {
      return (statusOptions);
   }

   public static List<String> getSubscribersForNewsletter(String newsletterNumber) {
      List<String> subscribers = NewsletterCommunication.getUsersWithPreferences(NewsletterUtil.NEWSLETTER, newsletterNumber);
      return (subscribers);
   }

   public static String getSubscriptionStatus(String userName) {
      return (NewsletterCommunication.getUserPreference(userName, SUBSCRIPTION_STATUS_KEY));
   }

   public static List<String> getUserSubscribedThemes(String userName) {
      if (userName != null) {
         List<String> themeList = NewsletterCommunication.getUserPreferences(userName, NEWSLETTER_THEME);
         return (themeList);
      }
      return (null);
   }

   public static List<String> getUserSubscribedNewsletters(String userName) {
      if (userName != null) {
         List<String> newsletterList = NewsletterCommunication.getUserPreferences(userName, NEWSLETTER);
         return (newsletterList);
      }
      return (null);
   }

   public static List<String> getUserSubscribedThemes(String userName, String newsletterNumber) {
      if (userName != null && newsletterNumber != null) {
         List<String> themeList = NewsletterCommunication.getUserPreferences(userName, "newslettertheme");
         return (themeList);
      }
      return (null);
   }

   public static void setPreferredMimeType(String userName, String mimeType) {
      if (userName != null) {
         if (mimeType == null) {
            mimeType = NewsletterGeneratorFactory.MIMETYPE_DEFAULT;
         }
         NewsletterCommunication.removeUserPreference(userName, PREFERRED_MIMETYPE);
         NewsletterCommunication.setUserPreference(userName, PREFERRED_MIMETYPE, mimeType);
         log.debug("Preferred mimetype for user " + userName + " set to " + mimeType);
      }
   }

   public static void setSubscriptionStatus(String userName, String status) {
      if (status == null) {
         status = SUBSCRIPTION_STATUS_DEFAULT;
      }
      if (userName != null && status != null) {
         if (statusOptions.contains(status)) {
            NewsletterCommunication.removeUserPreference(userName, SUBSCRIPTION_STATUS_KEY);
            NewsletterCommunication.setUserPreference(userName, SUBSCRIPTION_STATUS_KEY, status);
            log.debug("Subscription status for user " + userName + " set to " + status);
            return;
         }
         log.debug("Unknown status type: " + status);
      }
   }

   private static void subscribe(String userName, List<String> objects, String prefType) {
      if (userName != null && objects != null) {
         for (int i = 0; i < objects.size(); i++) {
            String objectNumber = objects.get(i);
            NewsletterCommunication.setUserPreference(userName, prefType, objectNumber);
            log.debug("Adding preference " + prefType + " - " + objectNumber + " to user " + userName);
         }
      }
   }

   public static void subscribeToNewsletters(String userName, List<String> newsletters) {
      subscribe(userName, newsletters, NEWSLETTER);
   }

   public static void subscribeToTheme(String userName, String theme) {
      if (userName != null && theme != null) {
         NewsletterCommunication.setUserPreference(userName, NEWSLETTER_THEME, theme);
      }
   }

   public static void subscribeToThemes(String userName, List<String> themes) {
      subscribe(userName, themes, NEWSLETTER_THEME);
   }

   public static void terminateUserSubscription(String userName) {
      if (userName != null) {
         NewsletterCommunication.removeNewsPrefByUser(userName);
         log.debug("Subscriptions for user " + userName + " terminated");
      }
   }

   public static void unsubscribeFromAllThemes(String userName) {
      if (userName != null) {
         NewsletterCommunication.removeUserPreference(userName, NEWSLETTER_THEME);
      }
   }

   public static void unsubscribeFromTheme(String userName, String theme) {
      if (userName != null && theme != null) {
         NewsletterCommunication.removeUserPreference(userName, NEWSLETTER_THEME, theme);
      }
   }

   public static void unsubscribeFromThemes(String userName, List<String> themes) {
      if (userName != null && themes != null) {
         for (int i = 0; i < themes.size(); i++) {
            NewsletterCommunication.removeUserPreference(userName, NEWSLETTER_THEME, themes.get(i));
         }
      }
   }
}