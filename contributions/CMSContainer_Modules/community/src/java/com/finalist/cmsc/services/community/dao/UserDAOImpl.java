package com.finalist.cmsc.services.community.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.FlushMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.store.Directory;

import com.finalist.cmsc.services.community.data.User;

public class UserDAOImpl extends HibernateDaoSupport implements UserDAO {
   protected final Log log = LogFactory.getLog(UserDAOImpl.class);

   private Directory luceneDirectory;


   /**
    * Sets directory of type Directory
    * 
    * @param directory
    *           The directory to set.
    */
   public void setLuceneDirectory(Directory luceneDirectory) {
      this.luceneDirectory = luceneDirectory;
   }


   @Transactional(readOnly = false)
   public User insertUser(User user) throws Exception {
      Set set = new HashSet();
      set.add(user);
      saveRecords(set);
      return user;
   }


   @Transactional(readOnly = false)
   public User getUser(String userId) {
      DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
      criteria.add(Restrictions.eq("userId", userId));
      List resultList = getHibernateTemplate().findByCriteria(criteria);
      return (resultList.size() != 1) ? null : (User) resultList.get(0);
   }


   @Transactional(readOnly = false)
   public void saveRecords(final Set<User> records) throws Exception {
      getSession().setFlushMode(FlushMode.AUTO);
      getHibernateTemplate().saveOrUpdateAll(records);
      getHibernateTemplate().flush();
   }


   public void deleteUser(User user) {
      removeObject(user);
      getSession().flush();
   }


   @Transactional(readOnly = false)
   public void removeObject(final Long id) {
      Object record = getHibernateTemplate().load(getPersistentClass(), id);
      getHibernateTemplate().delete(record);
   }


   @Transactional(readOnly = false)
   public void removeObject(final Object o) {
      getHibernateTemplate().delete(o);
      getSession().flush();
   }


   protected Class getPersistentClass() {
      return User.class;
   }


   public void updateUser(User user) throws Exception {
      Set<User> set = new HashSet<User>();
      set.add(user);
      saveRecords(set);
   }
   
   public List<String> getAllUsers(){
      DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
      List<String> resultList = getHibernateTemplate().findByCriteria(criteria);
      return resultList;
   }
}
