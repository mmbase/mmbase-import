package com.finalist.cmsc.services.community;

import org.springframework.transaction.annotation.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.finalist.cmsc.services.community.dao.UserDAO;
import com.finalist.cmsc.services.community.dao.GroupDAO;
import com.finalist.cmsc.services.community.dao.RoleDAO;

import com.finalist.cmsc.services.community.data.User;
import com.finalist.cmsc.services.community.data.Group;
import com.finalist.cmsc.services.community.data.Role;

import java.util.List;

/**
 * @author menno menninga
 * @spring.bean name="service"
 * @spring.property name="userDAO" ref="userDAO"
 * @spring.property name="groupDAO" ref="groupDAO"
 * @spring.property name="RoleDAO" ref="RoleDAO"
 */
@Transactional
public class HibernateService {

   private static final Log log = LogFactory.getLog(HibernateService.class);

   private UserDAO userDAO = null;
   private GroupDAO groupDAO = null;
   private RoleDAO roleDAO = null;


   @Transactional(readOnly = false)
   public void setUserDAO(UserDAO userDAO) {
      this.userDAO = userDAO;
   }

   @Transactional(readOnly = false)
   public User getUser(String userId) {
      /*
       * Example of method use User user = new User(); try { user = getUser(new
       * Long(1)); } catch (Exception e) { // TODO Auto-generated //catch block
       * e.printStackTrace(); }
       */
      //List users = null;
      
      //users = (List)userDAO.getUser(userId);
      
      //User user = (User)users.get(0);
      
      //String test = user.getUserId();
      
      //System.out.println("DIT IS EEN TEST WERKT HIBERNATE? ZOJA: " + test);
      
      return userDAO.getUser(userId);
   }
   
   @Transactional(readOnly = false)
   public List<String> getAllUsers() {
      /*
       * Example of method use User user = new User(); try { user = getUser(new
       * Long(1)); } catch (Exception e) { // TODO Auto-generated //catch block
       * e.printStackTrace(); }
       */
      //List users = null;
      
      //users = (List)userDAO.getUser(userId);
      
      //User user = (User)users.get(0);
      
      //String test = user.getUserId();
      
      //System.out.println("DIT IS EEN TEST WERKT HIBERNATE? ZOJA: " + test);
      
      return userDAO.getAllUsers();
   }

   @Transactional(readOnly = false)
   public User createUser(User user) throws Exception {
      return userDAO.insertUser(user);
   }


   @Transactional(readOnly = false)
   public void updateUser(User u, User user) throws Exception {
      userDAO.insertUser(u);
   }


   @Transactional(readOnly = false)
   public void deleteUser(User u, User user) throws Exception {
      userDAO.deleteUser(u);
   }


   @Transactional(readOnly = false)
   public void setGroupDAO(GroupDAO groupDAO) {
      this.groupDAO = groupDAO;
   }


   @Transactional(readOnly = false)
   public Group getGroup(Long id) {
      /*
       * Example of method use Group group = new Group(); try { group =
       * getGroup(new Long(1)); } catch (Exception e) { // TODO Auto-generated
       * catch block e.printStackTrace(); }
       */
      return groupDAO.getGroup(id);
   }


   @Transactional(readOnly = false)
   public Group createGroup(Group group) throws Exception {
      return groupDAO.insertGroup(group);
   }


   @Transactional(readOnly = false)
   public void updateGroup(Group g, Group group) throws Exception {
      groupDAO.insertGroup(g);
   }


   @Transactional(readOnly = false)
   public void deleteGroup(Group g, Group group) throws Exception {
      groupDAO.deleteGroup(g);
   }


   @Transactional(readOnly = false)
   public void setRoleDAO(RoleDAO roleDAO) {
      this.roleDAO = roleDAO;
   }


   @Transactional(readOnly = false)
   public Role getRole(Long id) {
      /*
       * Example of method use Role role = new Role(); try { role = getRole(new
       * Long(1)); } catch (Exception e) { // TODO Auto-generated catch block
       * e.printStackTrace(); }
       */
      return roleDAO.getRole(id);
   }


   @Transactional(readOnly = false)
   public Role createRole(Role role) throws Exception {
      return roleDAO.insertRole(role);
   }


   @Transactional(readOnly = false)
   public void updateRole(Role r, Role role) throws Exception {
      roleDAO.insertRole(r);
   }


   @Transactional(readOnly = false)
   public void deleteRole(Role r, Role role) throws Exception {
      roleDAO.deleteRole(r);
   }
   
   public User createUser(String userName, String password, String firstName, String lastName, String emailadres) throws Exception{
      
      User user = new User();
      user.setUserId(userName);
      user.setPassword(password);
      user.setName(firstName);
      user.setLastname(lastName);
      user.setEmailadress(emailadres);
      
      return userDAO.insertUser(user);
   }
}
