package com.finalist.cmsc.services.community;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.portlet.PortletSession;

import org.springframework.context.ApplicationContext;

/**
 * This class is for now just a sort of interface because the Newsletter module
 * is not yet updated to the new generic way of getting preferences. This class
 * can be deleted as soon as the Newsletter module is updated.
 * 
 * @author menno menninga
 */
public class NewsletterCommunicationServiceMysqlImpl extends NewsletterCommunicationService {

   private static Log log = LogFactory.getLog(NewsletterCommunicationServiceMysqlImpl.class);

   private PortletSession session;
   
   private ApplicationContext aC;

   public List<String> getAllNewsPrefs(){
      return (null);
   }
   
   public List<String> getUsersWithPreferences(String key, String value){
      return (null);
   }
   
   public List<String> getUsersWithPreference(String key) {
      return (null);
   }
   
   public String getUserPreference(String userName, String key) {
      return (null);
   }
   
   public List<String> getUserPreferences(String userName, String key) {
      return (null);
   }
   
   public boolean setUserPreference(String userName, String key, String value){
      return (false);
   }
   
   public boolean setUserPreferenceValues(String userName, Map<String, String> preferences) {
      return (false);
   }
   
   public boolean removeUserPreference(String userName, String key){
      return (false);
   }
   
   public void removeUserPreference(String userName, String key, String value){
      
   }
   
   public void removeNewsPrefByUser(String userName){
      
   }
   
   public int countK(String key, String value) {
      return (0);
   }

   public int count(String userName, String key) {
      return (0);
   }

   public int count(String userName, String key, String value) {
      return (0);
   }
   
   public int countByKey(String key) {
      return (0);
   }
   
   public boolean hasPermission(String userName, String permission){
      return (false);
   }
   
}
