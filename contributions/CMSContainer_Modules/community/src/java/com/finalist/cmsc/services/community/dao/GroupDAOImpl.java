package com.finalist.cmsc.services.community.dao;

import com.finalist.cmsc.services.community.data.Group;

public class GroupDAOImpl extends GenericDAO<Group> implements GroupDAO {
   
   public GroupDAOImpl() {
      super(Group.class);
   }
}
