package com.finalist.cmsc.services.community;

import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import com.finalist.cmsc.services.community.dao.UserDAO;
import com.finalist.cmsc.services.community.data.User;

@Transactional
public class HibernateCommunityService {

   private UserDAO userDAO = null;
   
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
}
