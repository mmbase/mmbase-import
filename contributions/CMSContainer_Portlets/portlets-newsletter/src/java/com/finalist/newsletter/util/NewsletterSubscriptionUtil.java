package com.finalist.newsletter.util;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.finalist.cmsc.services.community.NewsletterCommunication;
import com.finalist.newsletter.domain.Subscription;
import com.finalist.newsletter.services.CommunityModuleAdapter;
import org.mmbase.bridge.Node;

public abstract class NewsletterSubscriptionUtil {

   private static final ResourceBundle rb = ResourceBundle.getBundle("portlets-newslettersubscription");

   public static final String NEWSLETTER = "newsletter";
   public static final String NEWSLETTER_THEME = "newslettertheme";
   public static final String PREFERRED_MIMETYPE = "preferredmimetype";

   public static final String MIMETYPE_HTML = "text/html";
   public static final String MIMETYPE_PLAIN = "text/plain";
   public static final String MIMETIPE_DEFAULT = MIMETYPE_HTML;
   public static final String USERNAME = "username";

   public static final String SUBSCRIPTION_STATUS_KEY = "subscriptionstatus";
   public static final int SUBSCRIPTION_STATUS_TERMINATED = 0;
   public static final int SUBSCRIPTION_STATUS_ACTIVE = 1;
   public static final int SUBSCRIPTION_STATUS_PAUSED = 2;   
   public static final int SUBSCRIPTION_STATUS_DEFAULT  = SUBSCRIPTION_STATUS_ACTIVE;
   public static final String STATUS_OPTIONS = "statusoptions";

   private static List<Integer> statusOptions = new ArrayList<Integer>();

   static {
      statusOptions.add(SUBSCRIPTION_STATUS_ACTIVE);
      statusOptions.add(SUBSCRIPTION_STATUS_PAUSED);
   }

   // public static final List AVAILABLE_MIMETYPES = new ArrayList<String>()
   // {MIMETYPE_HTML, MIMETYPE_PLAIN;;

   public static List<Integer> compareToUserSubscribedThemes(List<Integer> compareWithThemes, String userName, int newsletterNumber) {
      if (compareWithThemes == null || userName == null || newsletterNumber <= 0) {
         return (null);
      }
      List<Integer> userThemes = getUserSubscribedThemes(userName, newsletterNumber);
      List<Integer> themes = new ArrayList<Integer>();
      for (int i = 0; i < compareWithThemes.size(); i++) {
         int theme = compareWithThemes.get(i);
         if (userThemes.contains(theme)) {
            themes.add(theme);
         }
      }
      return (themes);
   }

   public static int countSubscriptions() {
      int number = NewsletterCommunication.countByKey(NEWSLETTER);
      return (number);
   }

   public static int countSubscriptions(int newsletterNumber) {
      int number = NewsletterCommunication.countK(NEWSLETTER, String.valueOf(newsletterNumber));
      return (number);
   }

   public static List<String> getAllUsersWithSubscription() {
      List<String> users = null;
      return (users);
   }

   public static int getNumberOfSubscribedNewsletters(String userName) {
      int amount = 0;
      if (userName != null) {
         amount = NewsletterCommunication.count(userName, NEWSLETTER);
      }
      return (amount);
   }

   public static String getPreferredMimeType(String userName) {
      if (userName != null) {
         String preferredMimeType = NewsletterCommunication.getUserPreference(userName, PREFERRED_MIMETYPE);
         return (preferredMimeType);
      }
      return (null);
   }

   public static List<Integer > getStatusOptions() {
      return (statusOptions);
   }

   public static List<String> getSubscribersForNewsletter(int newsletterNumber) {
      List<String> subscribers = NewsletterCommunication.getUsersWithPreferences(NewsletterUtil.NEWSLETTER, String.valueOf(newsletterNumber));
      return (subscribers);
   }

   public static String getSubscriptionStatus(String userName) {
      return (NewsletterCommunication.getUserPreference(userName, SUBSCRIPTION_STATUS_KEY));
   }

   public static List<Integer> getUserSubscribedNewsletters(String userName) {
      if (userName != null) {
         List<String> newsletterList = NewsletterCommunication.getUserPreferences(userName, NEWSLETTER);
         List<Integer> newsletters = new ArrayList<Integer>();
         if (newsletterList != null) {
            for (int i = 0; i < newsletterList.size(); i++) {
               newsletters.add(Integer.valueOf(newsletterList.get(i)));
            }
         }
         return (newsletters);
      }
      return (null);
   }

   public static List<Integer> getUserSubscribedThemes(String userName) {
      if (userName != null) {
         List<String> themeList = NewsletterCommunication.getUserPreferences(userName, NEWSLETTER_THEME);
         List<Integer> themes = new ArrayList<Integer>();
         if (themeList != null) {
            for (int i = 0; i < themeList.size(); i++) {
               themes.add(Integer.valueOf(themeList.get(i)));
            }
         }

         return (themes);
      }
      return (null);
   }

   public static List<Integer> getUserSubscribedThemes(String userName, int newsletterNumber) {
      if (userName != null && newsletterNumber > 0) {
         List<String> themeList = NewsletterCommunication.getUserPreferences(userName, "newslettertheme");
         List<Integer> themes = new ArrayList<Integer>();
         if (themeList != null) {
            for (int i = 0; i < themeList.size(); i++) {
               themes.add(Integer.valueOf(themeList.get(i)));
            }
         }

         return (themes);
      }
      return (null);
   }

   public static void pauseSubscription(String userName) {
      setSubscriptionStatus(userName, SUBSCRIPTION_STATUS_PAUSED);
   }

   public static void resumeSubscription(String userName) {
      setSubscriptionStatus(userName, SUBSCRIPTION_STATUS_ACTIVE);
   }

   public static void setPreferredMimeType(String userName, String mimeType) {
      if (userName != null) {
         if (mimeType == null) {
         }
         NewsletterCommunication.removeUserPreference(userName, PREFERRED_MIMETYPE);
         NewsletterCommunication.setUserPreference(userName, PREFERRED_MIMETYPE, mimeType);
      }
   }

   public static void setSubscriptionStatus(String userName, int status) {
      if (status < 0 ) {
         status = SUBSCRIPTION_STATUS_DEFAULT;
      }
      if (userName != null && status >= 0 ) {
         NewsletterCommunication.removeUserPreference(userName, SUBSCRIPTION_STATUS_KEY);
         NewsletterCommunication.setUserPreference(userName, SUBSCRIPTION_STATUS_KEY, String.valueOf(status));
         return;
      }
   }

   private static void subscribe(String userName, List<Integer> objects, String prefType) {
      if (userName != null && objects != null) {
         for (int i = 0; i < objects.size(); i++) {
            int objectNumber = objects.get(i);
            NewsletterCommunication.setUserPreference(userName, prefType, String.valueOf(objectNumber));
         }
      }
   }

   public static void subscribeToNewsletters(String userName, List<Integer> newsletters) {
      subscribe(userName, newsletters, NEWSLETTER);
      NewsletterCommunication.setUserPreference(userName, "subscribtiondate", String.valueOf(System.currentTimeMillis()));
   }

   public static void subscribeToTheme(String userName, int theme) {
      if (userName != null && theme > 0) {
         NewsletterCommunication.setUserPreference(userName, NEWSLETTER_THEME, String.valueOf(theme));
      }
   }

   public static void subscribeToThemes(String userName, List<Integer> themes) {
      subscribe(userName, themes, NEWSLETTER_THEME);
   }

   public static void terminateUserSubscription(String userName) {
      if (userName != null) {
         NewsletterSubscriptionUtil.unsubscribeFromAllNewsletters(userName);
         NewsletterSubscriptionUtil.unsubscribeFromAllThemes(userName);
         NewsletterSubscriptionUtil.setSubscriptionStatus(userName, SUBSCRIPTION_STATUS_TERMINATED);
      }
   }

   public static void unsubscribeFromAllNewsletters(String userName) {
      if (userName != null) {
         NewsletterCommunication.removeUserPreference(userName, NEWSLETTER);
      }
   }

   public static void unsubscribeFromAllThemes(String userName) {
      if (userName != null) {
         NewsletterCommunication.removeUserPreference(userName, NEWSLETTER_THEME);
      }
   }

   public static void unsubscribeFromTheme(String userName, int theme) {
      if (userName != null && theme > 0) {
         NewsletterCommunication.removeUserPreference(userName, NEWSLETTER_THEME, String.valueOf(theme));
      }
   }

   public static void unsubscribeFromThemes(String userName, List<Integer> themes) {
      if (userName != null && themes != null) {
         for (int i = 0; i < themes.size(); i++) {
            int theme = themes.get(i);
            NewsletterCommunication.removeUserPreference(userName, NEWSLETTER_THEME, String.valueOf(theme));
         }
      }
   }

   public static void unsubscribeFromNewsletter(String userName, int newsletterNumber) {

   }

   public static void unsubscribeAllFromNewsletter(int newsletterNumber) {
      List<String> subscribers = NewsletterSubscriptionUtil.getAllSubscribers(newsletterNumber);
      if (subscribers != null && subscribers.size() > 0) {
         for (int s = 0; s < subscribers.size(); s++) {
            String userName = subscribers.get(s);
            NewsletterSubscriptionUtil.unsubscribeFromNewsletter(userName, newsletterNumber);
         }
      }
   }

   public static List<String> getAllSubscribers(int newsletterNumber) {
      List<String> subscribers = null;
      return (subscribers);
   }

    public static Subscription convertFromNode(Node node) {
      Subscription subscription = new Subscription();
      subscription.setId(node.getIntValue("number"));
      subscription.setMimeType(node.getStringValue("format"));
      subscription.setStatus(Subscription.STATUS.valueOf(node.getStringValue("status")));
      subscription.setSubscriber(CommunityModuleAdapter.getUserById(node.getStringValue("subscriber")));
      subscription.setSubscriberId(node.getStringValue("subscriber"));
      return subscription;
   }

}