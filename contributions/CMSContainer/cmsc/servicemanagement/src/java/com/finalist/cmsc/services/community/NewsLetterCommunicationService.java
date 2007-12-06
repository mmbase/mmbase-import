package com.finalist.cmsc.services.community;

import java.util.*;

import com.finalist.cmsc.services.Service;

public abstract class NewsLetterCommunicationService extends Service{
   public abstract List<String> getUsersWithPreferences(String key, String value);
   
   public abstract String getUserPreference(String userName, String key);
   
   public abstract List<String> getUserPreferences(String userName, String key);
   
   public abstract boolean setUserPreference(String userName, String key, String value);
   
   public abstract boolean removeUserPreference(String userName, String key);
   
   public abstract void removeUserPreference(String userName, String key, String value);
   
   public abstract boolean setUserPreferenceValues(String userName, Map<String, String> preferences);
   
   public abstract boolean hasPermission(String userName, String permission);
}
