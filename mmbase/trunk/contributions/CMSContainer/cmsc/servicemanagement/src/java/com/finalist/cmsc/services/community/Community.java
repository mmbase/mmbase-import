package com.finalist.cmsc.services.community;

import java.util.List;
import java.util.Map;

import com.finalist.cmsc.services.ServiceManager;

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

   public static List<String> getPreferenceValues(String module, String userId, String key) {
      return communityService.getPreferenceValues(module, userId, key);
   }

   public static Map<String, Map<String,List<String>>> getPreferences(String module, String userId, String key, String value){
      return communityService.getPreferences(module, userId, key, value);
   }

   public static void createPreference(String module, String userId, String key, List<String> values){
      communityService.createPreference(module, userId, key, values);
   }

   public static void removePreferences(String module, String userId, String key){
      communityService.removePreferences(module, userId, key);
   }

   public static Map<String, Map<String, String>> getUserProperty(String userName){
      return communityService.getUserProperty(userName);
   }

   /**
    * DO NOT USE THIS METHOD. Really, <strong>DO NOT USE THIS METHOD</strong>.
    * <br />
    * <br />
    * The passwords should normally be stored in an encrypted form which makes
    * this method useless. For 4en5mei.nl the passwords are stored in plain text
    * and we moved this method here instead of creating a dependency hell on the
    * login-portlet. This is so we can remove this functionality easily and
    * trade it for a way to <em>reset</em> a password rather than email the
    * account data.
    *
    * @param username
    *           The username to search for a password.
    * @param senderName
    *           Name used as email sender
    * @param senderEmail
    *           Email used as email sender
    * @param emailSubject
    *           The email subject
    * @param emailBody
    *           The email body
    * @return <code>true</code> if a password was succesfully send to the
    *         user, <code>false</code> otherwise.
    *
    * @deprecated Don't use this method, this functionality should be
    *             implemented somewhere else.
    */
   public static boolean sendPassword(String username, String senderName, String senderEmail, String emailSubject, String emailBody) {
      return communityService.sendPassword(username, senderName, senderEmail, emailSubject, emailBody);
   }

}
