/*
 * 
 * This software is OSI Certified Open Source Software. OSI Certified is a certification mark of the Open Source
 * Initiative.
 * 
 * The license (Mozilla version 1.0) can be read at the MMBase site. See http://www.MMBase.org/license
 * 
 */
package com.finalist.cmsc.services.community.preferences;

import java.util.List;
import java.util.Map;

import com.finalist.cmsc.services.community.domain.PreferenceVO;

/**
 * @author Remco Bos
 */
public interface PreferenceService {

   /**
    * Returns all preferences by module
    * 
    * @param module
    *           Module name
    * @return map A map with preferences for a module, grouped by autenticationId
    */
   // Map<Long, Map<String, String>> getPreferencesByModule(String module);
   /**
    * Returns all preferences by userId
    * 
    * @param user
    *           User id
    * @return map A map with preferences for a userId, grouped by module
    */
   // Map<String, Map<String, String>> getPreferencesByUserId(String userId);
   /**
    * Returns all preferences by module and user
    * 
    * @param module
    * @param userId
    * @return map A map with preferences for a module and userId
    */
   // Map<String, String> getPreferences(String module, String userId);
   /**
    * Returns all preferences by module, userId and key
    * 
    * @param module
    * @param userId
    * @param key
    * @return map A map with preferences for a module, userId and key
    */
   // Map<String, String> getPreferences(String module, String userId, String key);
   Map < Long , Map < String , String >> getPreferencesByModule(String module);

   Map < String , Map < String , String >> getPreferencesByUserId(String userId);

   List < String > getPreferenceValues(String module, String userId, String key);

   void createPreference(String module, String userId, String key, String value);

   void updatePreference(String module, String userId, String key, String oldValue, String newValue);

   void deletePreference(String module, String userId, String key, String value);

   public List < PreferenceVO > getPreferences(PreferenceVO preference, int offset, int pageSize, String orderBy,
         String direction);

   public List < PreferenceVO > getPreferences(int offset, int pageSize, String orderBy, String direction);

   public void createPreference(PreferenceVO preference);

   public void updatePreference(PreferenceVO preferenceVO);

   public void deletePreference(String number);

   public List < String > getAllUserIds();

   public int getTotalCount(PreferenceVO preference);

   List < Preference > getListPreferencesByUserId(String userId);

   void createPreference(Preference preferences, String userId);

   void deletePreference(long number);

   void batchCleanByAuthenticationId(long authenticationId);
}
