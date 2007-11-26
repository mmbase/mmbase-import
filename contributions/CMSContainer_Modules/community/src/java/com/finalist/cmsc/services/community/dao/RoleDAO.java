package com.finalist.cmsc.services.community.dao;

import java.util.List;
import java.util.Set;

import com.finalist.cmsc.services.community.data.Role;

public interface RoleDAO {

   /**
    * Inserts a Client into the persistence mechanism, returning a primary key
    * used to identify it.
    * 
    * @param Client
    *           The client to persist.
    * @return a primary key if inserted, null on error
    */
   public Role insertRole(Role role) throws Exception;


   public Role getRole(final Long id);


   /**
    * Deletes a Client from the persistence mechanism.
    * 
    * @param primaryKey
    *           The primary key of the client to delete.
    * @return true if the client is deleted, false on error
    */
   public void deleteRole(Role role);


   public void saveRecords(final Set<Role> records) throws Exception;


   public void updateRole(Role role) throws Exception;
}
