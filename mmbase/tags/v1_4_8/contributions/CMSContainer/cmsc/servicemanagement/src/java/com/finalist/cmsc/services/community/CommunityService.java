package com.finalist.cmsc.services.community;

import java.util.List;
import java.util.Map;

import com.finalist.cmsc.services.Service;

/**
 * CommunityService, this is a CMSc service class.
 * This class is the abstract service class that will be used by the
 * community class.
 *
 * @author menno menninga
 */
public abstract class CommunityService extends Service {

   public abstract void login(String userName, String password);

   public abstract void logout();

   public abstract boolean isAuthenticated();

   public abstract String getAuthenticatedUser();

   public abstract List<String> getAuthorities();

   public abstract boolean hasAuthority(String authority);

   public abstract List<String> getPreferenceValues(String module, String userId, String key);

   public abstract Map<String, Map<String,List<String>>> getPreferences(String module, String userId, String key, String value);

   public abstract void createPreference(String module, String userId, String key, List<String> values);

   public abstract void removePreferences(String module, String userId, String key);

   public abstract Map<String, Map<String, String>> getUserProperty(String userName);

   /**
    * Do not use.
    *
    * @see Community#sendPassword(String, String, String, String, String)
    */
   abstract boolean sendPassword(String username, String senderName, String senderEmail, String emailSubject, String emailBody);
}
