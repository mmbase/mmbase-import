package com.finalist.cmsc.services.community.dao;

import com.finalist.cmsc.services.community.data.GroupUserRole;

public class GroupUserRoleDAOImpl  extends GenericDAO<GroupUserRole> implements GroupUserRoleDAO {

   public GroupUserRoleDAOImpl() {
      super(GroupUserRole.class);
   }
}
