package com.finalist.cmsc.services.community.dao;

import com.finalist.cmsc.services.community.data.User;

public class UserDAOImpl extends GenericDAO<User> implements UserDAO {
   
   public UserDAOImpl() {
      super(User.class);
   }
}
