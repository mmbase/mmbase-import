package com.finalist.cmsc.services.community.dao;

import java.util.List;
import java.util.Set;

import com.finalist.cmsc.services.community.data.User;

public interface UserDAO {

   /**
    * Inserts a Client into the persistence mechanism, returning a primary key
    * used to identify it.
    * 
    * @param Client
    *           The client to persist.
    * @return a primary key if inserted, null on error
    */
   public User insertUser(User user) throws Exception;


   public User getUser(String userId);
   
   public List<String> getAllUsers();


   /**
    * Deletes a Client from the persistence mechanism.
    * 
    * @param primaryKey
    *           The primary key of the client to delete.
    * @return true if the client is deleted, false on error
    */
   public void deleteUser(User user);


   public void saveRecords(final Set<User> records) throws Exception;


   public void updateUser(User user) throws Exception;
}
