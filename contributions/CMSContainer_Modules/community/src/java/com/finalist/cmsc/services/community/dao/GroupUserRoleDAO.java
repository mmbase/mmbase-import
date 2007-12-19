package com.finalist.cmsc.services.community.dao;

import java.util.*;

import org.springframework.transaction.annotation.Transactional;

import com.finalist.cmsc.services.community.data.GroupUserRole;

public interface GroupUserRoleDAO {
  
   /**
    * Inserts a Client into the persistence mechanism, returning a primary key
    * used to identify it.
    * 
    * @param Client
    *           The client to persist.
    * @return a primary key if inserted, null on error
    */
   public GroupUserRole insertGroupUserRole(GroupUserRole groupUserRole) throws Exception;


   public GroupUserRole getGroupUserRole(final Long id);
   
   public GroupUserRole getGroupUserRoleByUserId(String userId);
   
   public List getGroupUserRoleList(String userId);

   /**
    * Deletes a Client from the persistence mechanism.
    * 
    * @param primaryKey
    *           The primary key of the client to delete.
    * @return true if the client is deleted, false on error
    */
   public void deleteGroupUserRole(GroupUserRole groupUserRole);


   public void saveRecords(final Set<GroupUserRole> records) throws Exception;


   public void updateGroupUserRole(GroupUserRole groupUserRole) throws Exception;
}
