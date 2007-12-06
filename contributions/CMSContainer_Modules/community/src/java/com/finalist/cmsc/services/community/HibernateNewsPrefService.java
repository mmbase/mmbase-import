package com.finalist.cmsc.services.community;

import org.springframework.transaction.annotation.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.finalist.cmsc.services.community.dao.NewsPrefDAO;

import com.finalist.cmsc.services.community.data.NewsPref;

import java.util.List;

public class HibernateNewsPrefService {

   private static final Log log = LogFactory.getLog(HibernateService.class);
   
   private NewsPrefDAO newsPrefDAO = null;
   
   @Transactional(readOnly = false)
   public void setNewsPrefDAO(NewsPrefDAO newsPrefDAO) {
      this.newsPrefDAO = newsPrefDAO;
   }
   
   @Transactional(readOnly = false)
   public List<String> getAllNewsPrefs() {
      return newsPrefDAO.getAllNewsPrefs();
   }
   
   @Transactional(readOnly = false)
   public List<String> getUsersWithPreferences(String key, String value){   
      List<String> usersWithPreferences = newsPrefDAO.getUsersWithPreferences(key, value);
      if (usersWithPreferences != null && usersWithPreferences.size() > 0) {
         return usersWithPreferences;
      }
      return (null);
   }
   
   public String getUserPreference(String userName, String key) {
      List<String> resultList = newsPrefDAO.getUserPreference(userName, key);
      if (resultList != null && resultList.size() > 0) {
         String userPreference = resultList.get(0);
         return userPreference;
      }
      return (null);
   }
   
   public List<String> getUserPreferences(String userName, String key){
      List<String> userPreferences = newsPrefDAO.getUserPreference(userName, key);
      if (userPreferences != null && userPreferences.size() > 0) {
         return userPreferences;
      }
      return (null);
   }
   
   public NewsPref createUserPreference(String userName, String key, String value) throws Exception{
      
      NewsPref newsPref = new NewsPref();
      newsPref.setNewsletterKey(key);
      newsPref.setNewsletterValue(value);
      newsPref.setUserId(userName);
      
      return newsPrefDAO.insertNewsPref(newsPref);
   }
   
   public boolean removeUserPreference(String userName, String key){
      return newsPrefDAO.deleteNewsPrefByCriteria(userName, key);
   }
   
   public void removeUserPreference(String userName, String key, String value){
      newsPrefDAO.removeNewsPref(userName, key, value);
   } 
}
