package com.finalist.newsletter.util;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.services.community.NewsLetterCommunication;
import com.finalist.newsletter.NewsletterGeneratorFactory;

public abstract class NewsletterSubscriptionUtil {

   private static Logger log = Logging.getLoggerInstance(NewsletterSubscriptionUtil.class.getName());
   private static final ResourceBundle rb = ResourceBundle.getBundle("portlets-newslettersubscription");

   public static final String NEWSLETTER = "newsletter";
   public static final String NEWSLETTER_THEME = "newslettertheme";
   public static final String PREFERRED_MIMETYPE = "preferredmimetype";

   public static final String MIMETYPE_HTML = "html";
   public static final String MIMETYPE_PLAIN = "plain";
   public static final String MIMETIPE_DEFAULT = MIMETYPE_HTML;
   public static final String USERNAME = "username";

   public static final String SUBSCRIPTION_STATUS_KEY = "subscriptionstatus";
   public static final String SUBSCRIPTION_STATUS_ACTIVE = rb.getString("status.active");
   public static final String SUBSCRIPTION_STATUS_INACTIVE = rb.getString("status.inactive");

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
         String theme = (String) compareWithThemes.get(i);
         if (userThemes.contains(theme)) {
            themes.add(theme);
         }
      }
      return (themes);
   }

   public static String getPreferredMimeType(String userName) {
      if (userName != null) {
         return (NewsLetterCommunication.getUserPreference(userName, PREFERRED_MIMETYPE));
      }
      return (null);
   }

   public static List<String> getSubscribersForNewsletter(String newsletterNumber) {
      List<String> subscribers = NewsLetterCommunication.getUsersWithPreferences(NewsletterUtil.NEWSLETTER, newsletterNumber);
      return (subscribers);
   }

   public static String getSubscriptionStatus(String userName) {
      if (userName != null) {
         return (NewsLetterCommunication.getUserPreference(userName, SUBSCRIPTION_STATUS_KEY));
      }
      return (null);
   }

   public static List<String> getUserSubscribedThemes(String userName) {
      if (userName != null) {
         List<String> themeList = NewsLetterCommunication.getUserPreferences(userName, "newslettertheme");
         return (themeList);
      }
      return (null);
   }

   public static List<String> getUserSubscribedThemes(String userName, String newsletterNumber) {
      if (userName != null && newsletterNumber != null) {
         List<String> themeList = NewsLetterCommunication.getUserPreferences(userName, "newslettertheme");
         return (themeList);
      }
      return (null);
   }

   public static boolean pauseUserSubscriptions(String userName) {
      NewsLetterCommunication.setUserPreference(userName, SUBSCRIPTION_STATUS_KEY, SUBSCRIPTION_STATUS_INACTIVE);
      log.debug("Subscriptionstatus for user " + userName + " set to " + SUBSCRIPTION_STATUS_INACTIVE);
      return (true);
   }

   public static boolean resumeUserSubscriptions(String userName) {
      NewsLetterCommunication.setUserPreference(userName, SUBSCRIPTION_STATUS_KEY, SUBSCRIPTION_STATUS_ACTIVE);
      log.debug("Subscriptionstatus for user " + userName + " set to " + SUBSCRIPTION_STATUS_ACTIVE);
      return (true);
   }

   public static boolean setPreferredMimeType(String userName, String mimeType) {
      if (userName != null) {
         if (mimeType == null) {
            mimeType = NewsletterGeneratorFactory.MIMETYPE_DEFAULT;
         }
         NewsLetterCommunication.setUserPreference(userName, PREFERRED_MIMETYPE, mimeType);
         log.debug("Preferred mimetype for user " + userName + " set to " + mimeType);
         return (true);
      }
      return (false);
   }

   public static boolean subscribeToTheme(String userName, String theme) {
      NewsLetterCommunication.setUserPreference(userName, NEWSLETTER_THEME, theme);
      return (true);
   }

   public static void subscribeToNewsletters(String userName, List<String> newsletters) {
      subscribe(userName, newsletters, NEWSLETTER);
   }
   
   public static void subscribeToThemes(String userName, List<String> themes) {
      subscribe(userName, themes, NEWSLETTER_THEME);
   }

   private static void subscribe(String userName, List<String> objects, String prefType) {
      if (userName != null && objects != null) {
         for (int i = 0; i < objects.size(); i++) {
            String objectNumber = objects.get(i);
            NewsLetterCommunication.setUserPreference(userName, prefType, objectNumber);
            log.debug("Adding preference " + prefType + " - " + objectNumber + " to user " + userName);
         }         
      }      
   }

   public static boolean terminateUserSubscription(String userName) {
      NewsLetterCommunication.removeUserPreference(userName, NewsletterSubscriptionUtil.NEWSLETTER_THEME);
      return (true);
   }

   public static boolean unsubscribeFromAllThemes(String userName) {
      if (userName != null) {
         List<String> themes = getUserSubscribedThemes(userName);
         unsubscribeFromThemes(userName, themes);
         return (true);
      }
      return (false);
   }

   public static boolean unsubscribeFromTheme(String userName, String theme) {
      NewsLetterCommunication.removeUserPreference(userName, NEWSLETTER_THEME, theme);
      return (true);
   }

   public static boolean unsubscribeFromThemes(String userName, List<String> themes) {
      if (userName != null && themes != null) {
         for (int i = 0; i < themes.size(); i++) {
            NewsLetterCommunication.removeUserPreference(userName, NEWSLETTER_THEME, themes.get(i));
         }
      }
      return (true);
   }

   public static List<String> getStatusOptions() {
      return (statusOptions);
   }
}