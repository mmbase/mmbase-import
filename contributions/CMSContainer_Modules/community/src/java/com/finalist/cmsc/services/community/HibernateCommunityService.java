package com.finalist.cmsc.services.community;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

import com.finalist.cmsc.services.community.dao.UserDAO;
import com.finalist.cmsc.services.community.dao.ModulePrefDAO;
import com.finalist.cmsc.services.community.dao.GroupDAO;
import com.finalist.cmsc.services.community.dao.GroupUserRoleDAO;
import com.finalist.cmsc.services.community.dao.RoleDAO;
import com.finalist.cmsc.services.community.data.GroupUserRole;
import com.finalist.cmsc.services.community.data.Group;
import com.finalist.cmsc.services.community.data.ModulePref;
import com.finalist.cmsc.services.community.data.User;
import com.finalist.cmsc.services.community.data.Role;

/**
 * HibernatCommunityService, this is a Hibernate service class.
 * This class makes use of the DAO/transaction classes.
 * All the request's to the database come through here.
 * This class will find out the class that is needed for each
 * request and call the method in the needed class.
 * 
 * @author menno menninga
 */
@Transactional
public class HibernateCommunityService {

   private UserDAO userDAO = null;
   
   private ModulePrefDAO modulePrefDAO = null;
   
   private GroupDAO groupDAO = null;
   
   private GroupUserRoleDAO groupUserRoleDAO = null;
   
   private RoleDAO roleDAO = null;
   
   @Transactional(readOnly = false)
   public void setUserDAO(UserDAO userDAO) {
      this.userDAO = userDAO;
   }
   
   @Transactional(readOnly = false)
   public void setModulePrefDAO(ModulePrefDAO modulePrefDAO) {
      this.modulePrefDAO = modulePrefDAO;
   }
   
   @Transactional(readOnly = false)
   public void setGroupDAO(GroupDAO groupDAO) {
      this.groupDAO = groupDAO;
   }
   
   @Transactional(readOnly = false)
   public void setGroupUserRoleDAO(GroupUserRoleDAO groupUserRoleDAO) {
      this.groupUserRoleDAO = groupUserRoleDAO;
   }
   
   @Transactional(readOnly = false)
   public void setRoleDAO(RoleDAO roleDAO) {
      this.roleDAO = roleDAO;
   }
   
   public Map<String, Map<String,List<String>>> getPreferences(String module, String userId, String key, String value){
      if (module == "Newsletter"){
         Map<String, Map<String,List<String>>> resultList = modulePrefDAO.getPreferences(module, userId, key, value);
         return resultList;
      }
      return (null);
   }
   
   public List<String> getObject(String module, Map<String, String> preferences){
      if (module == "Newsletter"){
         List<String> resultList = modulePrefDAO.getObject(preferences);
         return resultList;
      }
      if (module == "User"){
         List<String> resultList = userDAO.getObject(preferences);
         return resultList;
      }
      if (module == "Group"){
         List<String> resultList = groupDAO.getObject(preferences);
         return resultList;
      }
      if (module == "Role"){
         List<String> resultList = roleDAO.getObject(preferences);
         return resultList;
      }
      if (module == "UserGroups"){
         List<String> resultList = groupUserRoleDAO.getObject(preferences);
         return resultList;
      }
      return (null);
   }
   
   public void createPreference(String module, String userId, String key, List<String> values){
      try{
         ListIterator it = values.listIterator();
         while (it.hasNext()){
            ModulePref modulePref = new ModulePref();
            modulePref.setModuleValue(it.next().toString());
            modulePref.setModuleKey(key); 
            modulePref.setUserId(userId);
            modulePref.setModule(module);
            modulePrefDAO.insertByObject(modulePref);
         }
      }
      catch(Exception e){
         System.out.println("Can't create the preferences: " + e);
      }
   }
   
   public void removePreferences(String module, String userId, String key){
      if (module == "Newsletter"){
         modulePrefDAO.deleteByCriteria(module, userId, key);
      }
   }
   
   public Map<String, Map<String, String>> getUserProperty(String userName){
      Map<String, Map<String, String>> resultList = userDAO.getUserProperty(userName);
      if (resultList == null){
         return resultList;
      }
      return (null);
   }
}
