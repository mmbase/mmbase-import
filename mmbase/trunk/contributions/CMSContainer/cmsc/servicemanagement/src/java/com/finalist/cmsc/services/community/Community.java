package com.finalist.cmsc.services.community;

import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.servlet.http.HttpServletRequest;

import com.finalist.cmsc.services.ServiceManager;
import com.finalist.cmsc.services.community.CommunityService;

public class Community {
   private final static CommunityService cService = (CommunityService) ServiceManager
         .getService(CommunityService.class);


   public static boolean loginUser(ActionRequest request, ActionResponse response, String userText, String passText) {
      return cService.loginUser(request, response, userText, passText);
   }


   public static boolean logoutUser(/** HttpServletRequest HttpRequest, * */
   ActionRequest request, ActionResponse response) {
      return cService.logoutUser(/** HttpRequest, * */
      request, response);
   }
   
   public static List<String> getUsersWithPreferences(String key, String value){
      return cService.getUsersWithPreferences(key, value);
   }
   
   public static String getUserPreference(String userName, String key){
      return cService.getUserPreference(userName, key);
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
      
   }
   
   public static boolean setUserPreferenceValues(String userName, Map<String, String> preferences){
      return cService.setUserPreferenceValues(userName, preferences);
   }
   
   public static boolean hasPermission(String userName, String permission){
      return cService.hasPermission(userName, permission);
   }
}
