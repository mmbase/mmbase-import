package com.finalist.cmsc.services.community.dao;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

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
   
   public Map<String, Map<String, String>> getUserProperty(String userName) {
      DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
         criteria.add(Restrictions.eq("userId", userName));
         List<String> resultList = getHibernateTemplate().findByCriteria(criteria);
         Map<String, Map<String, String>> resultMap = new HashMap<String, Map<String, String>>();
         Map<String, String> valueMap = new HashMap<String, String>();
         if (resultList != null){
            User user = (User)resultList;
            valueMap.put("userName", user.getUserId());
            valueMap.put("password", user.getPassword());
            valueMap.put("firstName", user.getName());
            valueMap.put("lastName", user.getLastname());
            valueMap.put("emailAdress", user.getEmailadress());
            resultMap.put(userName, valueMap);
            return resultMap;
         }
         return (null);
   }
}
