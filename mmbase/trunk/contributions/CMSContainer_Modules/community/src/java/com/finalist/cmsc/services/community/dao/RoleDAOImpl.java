package com.finalist.cmsc.services.community.dao;

import com.finalist.cmsc.services.community.data.Role;

public class RoleDAOImpl extends GenericDAO<Role> implements RoleDAO {

   public RoleDAOImpl() {
      super(Role.class);
   }
}
