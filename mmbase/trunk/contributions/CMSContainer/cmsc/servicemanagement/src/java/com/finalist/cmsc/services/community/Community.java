package com.finalist.cmsc.services.community;

import java.util.List;
import java.util.Map;

import com.finalist.cmsc.services.ServiceManager;
import com.finalist.cmsc.services.community.CommunityService;

/**
 * Community, this is a CMSc service class.
 * This class is the abstract service class that wil be implemented
 * by the "CommunityServiceMysqlImpl".
 * In this class comes the request from a portlet or module and will be
 * redirected to the "CommunityServiceMysqlImpl".
 * 
 * @author menno menninga
 */
public class Community {

	private final static CommunityService communityService = 
	   (CommunityService) ServiceManager.getService(CommunityService.class);

   public static void login(String userName, String password) {
      communityService.login(userName, password);
   }

   public static void logout() {
      communityService.logout();
   }
   
   public static boolean isAuthenticated() {
      return communityService.isAuthenticated();
   }

   public static String getAuthenticatedUser() {
      return communityService.getAuthenticatedUser();
   }
   
   public static List<String> getAuthorities() {
      return communityService.getAuthorities();
   }
   
   public static boolean hasAuthority(String authority) {
      return communityService.hasAuthority(authority);
   }

   public Map<String, Map<String,List<String>>> getPreferences(String module, String userId, String key, String value){
      return communityService.getPreferences(module, userId, key, value);
   }
   
   public void createPreference(String module, String userId, String key, List<String> values){
      communityService.createPreference(module, userId, key, values);
   }
   
   public void removePreferences(String module, String userId, String key){
      communityService.removePreferences(module, userId, key);
   }
   
   public Map<String, Map<String, String>> getUserProperty(String userName){
      return communityService.getUserProperty(userName);
   }

}
