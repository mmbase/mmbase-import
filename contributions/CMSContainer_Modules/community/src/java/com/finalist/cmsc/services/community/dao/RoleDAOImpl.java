package com.finalist.cmsc.services.community.dao;

import com.finalist.cmsc.services.community.data.Role;

/**
 * RoleDAOImpl, this is a hibernate DAO/transaction class.
 * This class is the implementation of the DAO interface
 * it contains the methods for transactions to the database
 * 
 * @author menno menninga
 */
public class RoleDAOImpl extends GenericDAO<Role> implements RoleDAO {

   public RoleDAOImpl() {
      super(Role.class);
   }
}
