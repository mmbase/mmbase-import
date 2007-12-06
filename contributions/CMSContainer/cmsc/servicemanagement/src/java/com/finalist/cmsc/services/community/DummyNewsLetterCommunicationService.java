package com.finalist.cmsc.services.community;

import java.util.List;
import java.util.Map;

public class DummyNewsLetterCommunicationService extends NewsLetterCommunicationService{

   public List<String> getUsersWithPreferences(String key, String value){
      return null;
   }
   
   public String getUserPreference(String userName, String key){
      return null;
   }
   
   public List<String> getUserPreferences(String userName, String key){
      return null;
   }
   
   public boolean setUserPreference(String userName, String key, String value){
      return false;
   }
   
   public boolean removeUserPreference(String userName, String key){
      return false;
   }
   
   public void removeUserPreference(String userName, String key, String value){
      
   }
   
   public boolean setUserPreferenceValues(String userName, Map<String, String> preferences){
      return false;
   }
   
   public boolean hasPermission(String userName, String permission){
      return false;
   }
}
