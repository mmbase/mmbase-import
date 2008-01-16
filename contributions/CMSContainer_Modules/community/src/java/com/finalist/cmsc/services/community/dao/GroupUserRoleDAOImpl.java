package com.finalist.cmsc.services.community.dao;

import com.finalist.cmsc.services.community.data.GroupUserRole;

/**
 * GroupUserRoleDAOImpl, this is a hibernate DAO/transaction class.
 * This class is the implementation of the DAO interface
 * it contains the methods for transactions to the database
 * 
 * @author menno menninga
 */
public class GroupUserRoleDAOImpl  extends GenericDAO<GroupUserRole> implements GroupUserRoleDAO {

   public GroupUserRoleDAOImpl() {
      super(GroupUserRole.class);
   }
}
