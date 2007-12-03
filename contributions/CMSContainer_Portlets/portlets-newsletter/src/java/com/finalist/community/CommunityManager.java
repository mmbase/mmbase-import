package com.finalist.community;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.*;

import com.finalist.cmsc.services.community.Community;

/*
 * Temporary Community interface implementation 
 */

public class CommunityManager {

   private static Map<String, String> prefs = new HashMap<String, String>();

   public static final String TEMPLATE_LOGIN = "newsletter/subscription/login.jsp";

   // A note to Menno: The community must be able to handle multiple
   // preferences with the same key. For example, there can be multiple
   // preferences with key "newslettertheme", but each key has a different
   // value attached to it.

   // This method returns a list of usernames (identifiers) who have the given
   // key/value pair in their preferences. For example, when given the
   // key/value pair of "newsletter" / "949" this method returns all users that
   // have this preference set.

   // This method retuns the value for the given username and key from the
   // users preferences
   public static String getUserPreference(String userName, String key) {
      return Community.getUserPreference(userName, key);
   }
   
   // This method returns a list of values for the specified user and key. This
   // method can be compared to the getParameterValues method of the
   // HTTPRequest For example, when given the userName "Jan" en the key
   // "newsletterTheme" it can return a list containing all occurences of
   // NEWSLETTERTHEME IN THE USERS PREFERENCES
   public static List<String> getUserPreferenceValues(String userName, String key) {
      return Community.getUserPreferences(userName, key);
   }
   
   public static List<String> getUsersWithPreference(String key, String value) {
      return Community.getUsersWithPreferences(key, value);
   }

   // This method removes all occurrences of the given key from the givenusers
   // preference listing For example, if there is a key "newslettertheme" with
   // 5 related values, this method removes all of those values and the key
   // when given the key "newslettertheme"
   public static boolean removeUserPreference(String userName, String key) {
      return Community.removeUserPreference(userName, key);
   }


   // This method removes the given key/value combination from the given users
   // preferencelist
   public static void removeUserPreference(String userName, String key, String value) {
      Community.removeUserPreference(userName, key, value);
   }

   // This method can be used to store a preference for a user. It takes the
   // username and a key/value pair
   public static boolean setUserPreference(String userName, String key, String value){
      return Community.setUserPreference(userName, key, value);
   }
   
   // I do not yet need this one, but may be handy. This method receives a list
   // or map of key/value paires and sets them for the given user.
   public static boolean setUserPreferenceValues(String userName, Map<String, String> preferences) {
      return Community.setUserPreferenceValues(userName, preferences);
   }
   
   // This methods allows a module or portlet to check if a given user has the
   // requested permission. The clinet does not care about the role or group of
   // the user.
   public static boolean hasPermission(String userName, String permission) {
      return Community.hasPermission(userName, permission); // temp
   }
}