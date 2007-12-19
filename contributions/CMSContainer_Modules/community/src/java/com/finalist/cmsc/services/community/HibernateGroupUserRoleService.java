package com.finalist.cmsc.services.community;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;

import com.finalist.cmsc.services.community.dao.GroupUserRoleDAO;

import com.finalist.cmsc.services.community.data.GroupUserRole;

import java.util.List;

public class HibernateGroupUserRoleService {

   private static final Log log = LogFactory.getLog(HibernateUserService.class);

   private GroupUserRoleDAO groupUserRoleDAO = null;
   
   @Transactional(readOnly = false)
   public void setGroupUserRoleDAO(GroupUserRoleDAO groupUserRoleDAO) {
      this.groupUserRoleDAO = groupUserRoleDAO;
   }
   
   public GroupUserRole insertGroupUserRole(GroupUserRole groupUserRole) throws Exception {
      return groupUserRoleDAO.insertGroupUserRole(groupUserRole);
   }

   public GroupUserRole getGroupUserRoleByUserId(String userName){
      return groupUserRoleDAO.getGroupUserRoleByUserId(userName);
   }
   
   public List getGroupUserRoleList(String userId){
      System.out.println("HibernateGroupUserRoleService Ingevoerde user: " + userId);
      return groupUserRoleDAO.getGroupUserRoleList(userId);
   }
}
