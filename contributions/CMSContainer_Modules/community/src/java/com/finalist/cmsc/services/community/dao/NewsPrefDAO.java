package com.finalist.cmsc.services.community.dao;

import java.util.List;
import java.util.Set;

import com.finalist.cmsc.services.community.data.NewsPref;

public interface NewsPrefDAO {

   /**
    * Inserts a NewsPref into the persistence mechanism, returning a primary key
    * used to identify it.
    * 
    * @param NewsPref
    *           The NewsPref to persist.
    * @return a primary key if inserted, null on error
    */
   public NewsPref insertNewsPref(NewsPref newsPref) throws Exception;


   public NewsPref getNewsPref(String userId);
   
   public List<String> getUsersWithPreferences(String key, String value);

   public List<String> getUserPreference(String userName, String key);
   
   public List<String> getAllNewsPrefs();
   
   /**
    * Deletes a NewsPref from the persistence mechanism.
    * 
    * @param primaryKey
    *           The primary key of the NewsPref to delete.
    * @return true if the NewsPref is deleted, false on error
    */
   public boolean deleteNewsPrefByCriteria(String userName, String key);
   
   public void removeNewsPref(String userName, String key, String value);

   public void saveRecords(final Set<NewsPref> records) throws Exception;


   public void updateNewsPref(NewsPref newsPref) throws Exception;
   
}
