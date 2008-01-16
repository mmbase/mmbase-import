package com.finalist.cmsc.services.community.dao;

import com.finalist.cmsc.services.community.data.User;

/**
 * UserDAOImpl, this is a hibernate DAO/transaction class.
 * This class is the implementation of the DAO interface
 * it contains the methods for transactions to the database
 * 
 * @author menno menninga
 */
public class UserDAOImpl extends GenericDAO<User> implements UserDAO {
   
   public UserDAOImpl() {
      super(User.class);
   }
}
