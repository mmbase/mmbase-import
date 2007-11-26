package com.finalist.cmsc.services.community.dao;

import java.util.List;
import java.util.Set;

import com.finalist.cmsc.services.community.data.Group;

public interface GroupDAO {

   /**
    * Inserts a Client into the persistence mechanism, returning a primary key
    * used to identify it.
    * 
    * @param Client
    *           The client to persist.
    * @return a primary key if inserted, null on error
    */
   public Group insertGroup(Group group) throws Exception;


   public Group getGroup(final Long id);


   /**
    * Deletes a Client from the persistence mechanism.
    * 
    * @param primaryKey
    *           The primary key of the client to delete.
    * @return true if the client is deleted, false on error
    */
   public void deleteGroup(Group group);


   public void saveRecords(final Set<Group> records) throws Exception;


   public void updateGroup(Group group) throws Exception;
}
