package com.finalist.cmsc.services.community;

import java.util.List;
import java.util.Map;

import com.finalist.cmsc.services.ServiceManager;
import com.finalist.cmsc.services.community.NewsletterCommunicationService;

public class NewsletterCommunication {
   private final static NewsletterCommunicationService cService = (NewsletterCommunicationService) ServiceManager
   .getService(NewsletterCommunicationService.class);
   
   public static List<String> getUsersWithPreferences(String key, String value){
      return cService.getUsersWithPreferences(key, value);
   }
   
   public static String getUserPreference(String userName, String key){
      return cService.getUserPreference(userName, key);
   }
   
   public List<String> getUsersWithPreference(String key){
      return cService.getUsersWithPreference(key);
   }
   
   public static List<String> getUserPreferences(String userName, String key){
      return cService.getUserPreferences(userName, key);
   }
   
   public static boolean setUserPreference(String userName, String key, String value){
      return cService.setUserPreference(userName, key, value);
   }
   
   public static boolean removeUserPreference(String userName, String key){
      return cService.removeUserPreference(userName, key);
   }
   
   public static void removeUserPreference(String userName, String key, String value){
      cService.removeUserPreference(userName, key, value);
   }
   
   public static boolean setUserPreferenceValues(String userName, Map<String, String> preferences){
      return cService.setUserPreferenceValues(userName, preferences);
   }
   
   public static boolean hasPermission(String userName, String permission){
      return cService.hasPermission(userName, permission);
   }
   
   public static void removeNewsPrefByUser(String userName){
      cService.removeNewsPrefByUser(userName);
   }
   
   public static int countK(String key, String value) {
      return cService.countK(key, value);
   }

   public static int count(String userName, String key) {
      return cService.count(userName, key);
   }

   public static int count(String userName, String key, String value) {
      return cService.count(userName, key, value);
   }
   
   public static int countByKey(String key){
      return cService.countByKey(key);
   }
}
