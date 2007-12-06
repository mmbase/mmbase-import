package com.finalist.cmsc.services.community;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.portlet.PortletSession;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.finalist.cmsc.services.community.data.NewsPref;
import com.finalist.cmsc.services.community.HibernateNewsPrefService;

public class NewsLetterCommunicationServiceMysqlImpl extends NewsLetterCommunicationService {

   private static Log log = LogFactory.getLog(NewsLetterCommunicationServiceMysqlImpl.class);

   private PortletSession session;
   
   private ApplicationContext aC;

   public List<String> getAllNewsPrefs(){
      aC = new ClassPathXmlApplicationContext("applicationContext.xml");
      HibernateNewsPrefService hibservice = (HibernateNewsPrefService)aC.getBean("serviceNewsLetter");
      List<String> newsPrefs = hibservice.getAllNewsPrefs();
      if (newsPrefs != null && newsPrefs.size() > 0){
         return newsPrefs;
      }
      return (null);
   }
   
   public List<String> getUsersWithPreferences(String key, String value){

      aC = new ClassPathXmlApplicationContext("applicationContext.xml");
      
      HibernateNewsPrefService hibservice = (HibernateNewsPrefService)aC.getBean("serviceNewsLetter");;

      List<String> resultList = hibservice.getUsersWithPreferences(key, value);
      if (resultList != null && resultList.size() > 0){
         return resultList;
      }
      return (null);
   }
   
   public String getUserPreference(String userName, String key) {
      
      aC = new ClassPathXmlApplicationContext("applicationContext.xml");
      
      HibernateNewsPrefService hibservice = (HibernateNewsPrefService)aC.getBean("serviceNewsLetter");;
      
      String preference = hibservice.getUserPreference(userName, key);
      if (preference != null){
         return preference;
      }
      return (null);
   }
   
   public List<String> getUserPreferences(String userName, String key) {
      
      aC = new ClassPathXmlApplicationContext("applicationContext.xml");
      
      HibernateNewsPrefService hibservice = (HibernateNewsPrefService)aC.getBean("serviceNewsLetter");;
      
      List<String> preferenceList = hibservice.getUserPreferences(userName, key);
      if (preferenceList != null && preferenceList.size() > 0){
         return preferenceList;
      }
      return (null);
   }
   
   public boolean setUserPreference(String userName, String key, String value){
      
      aC = new ClassPathXmlApplicationContext("applicationContext.xml");
      boolean succes;
      HibernateNewsPrefService hibservice = (HibernateNewsPrefService)aC.getBean("serviceNewsLetter");
      try{
         NewsPref newsPref = hibservice.createUserPreference(userName, key, value);
         if(newsPref != null){
            succes = true;
         }
         else{
            succes = false;
         }
      }
      catch (Exception e){
         succes = false;
      }
      
      return succes;
   }
   
   public boolean setUserPreferenceValues(String userName, Map<String, String> preferences) {
      
      aC = new ClassPathXmlApplicationContext("applicationContext.xml");
      boolean succes;
      NewsPref newsPref = new NewsPref();
      HibernateNewsPrefService hibservice = (HibernateNewsPrefService)aC.getBean("serviceNewsLetter");
      try{
         
         Iterator keys = preferences.keySet().iterator();
         Iterator values = preferences.values().iterator();
         
         while(keys.hasNext() && values.hasNext()){
            newsPref = hibservice.createUserPreference(userName, keys.next().toString(), values.next().toString());
         }
         if (newsPref != null){
            succes = true;
         }
         else{
            succes = false;
         }
      }
      catch (Exception e){
         succes = false;
      }
      return succes;
   }
   
   public boolean removeUserPreference(String userName, String key){
      aC = new ClassPathXmlApplicationContext("applicationContext.xml");
      boolean succes;
      HibernateNewsPrefService hibservice = (HibernateNewsPrefService)aC.getBean("serviceNewsLetter");
      succes = hibservice.removeUserPreference(userName, key);
      return succes;
   }
   
   public void removeUserPreference(String userName, String key, String value){
      aC = new ClassPathXmlApplicationContext("applicationContext.xml");
      HibernateNewsPrefService hibservice = (HibernateNewsPrefService)aC.getBean("serviceNewsLetter");
      hibservice.removeUserPreference(userName, key, value);
   }
   
   public boolean hasPermission(String userName, String permission){
      
      boolean permissionB;
      
      if(userName == "jaspers" || userName == "admin"){
         permissionB = true;
      }
      else{
         permissionB = false;
      }
      return permissionB;
   }
   
}
