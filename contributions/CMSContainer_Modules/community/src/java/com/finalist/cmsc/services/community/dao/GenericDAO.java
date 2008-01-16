package com.finalist.cmsc.services.community.dao;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.hibernate.FlushMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import org.apache.lucene.store.Directory;

import com.finalist.cmsc.services.community.data.User;
import com.finalist.cmsc.services.community.data.Group;
import com.finalist.cmsc.services.community.data.Role;
import com.finalist.cmsc.services.community.data.GroupUserRole;
import com.finalist.cmsc.services.community.data.ModulePref;

/**
 * GenericDAO, this is a hibernate DAO/transaction class.
 * This class is the implementation of the DAO interface
 * it contains the methods for transactions to the database
 * This class is a generic class. From this class you can 
 * query the database in generic a way.
 * 
 * @author menno menninga
 */
public class GenericDAO<T> extends HibernateDaoSupport implements DAO<T> {

   public static class Param{
      String param;
      Object value;
      boolean like;
      public Param(String param, Object value, boolean like) {
         super();
         this.param = param;
         this.value = value;
         this.like = like;
      }
      public Param(String param, Object value) {
         super();
         this.param = param;
         this.value = value;
      }
   }
     
   private static Log log = LogFactory.getLog(GenericDAO.class);
   
   private final Class<T> persistentClass;
   
   private Directory luceneDirectory;
   
   public GenericDAO(Class<T> persistentClass){
      this.persistentClass = persistentClass;
   }
   
   protected Class<T> getPersistentClass() {
      return persistentClass;
   }
   
   /**
    * Sets directory of type Directory
    * 
    * @param directory
    *           The directory to set.
    */
   public void setLuceneDirectory(Directory luceneDirectory) {
      this.luceneDirectory = luceneDirectory;
   }
   
   @SuppressWarnings("unchecked")
   public List<String> getObject(Map<String, String> preferences) {
      DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
      if (preferences.containsKey("userId")){
         String criteriaString = preferences.get("userId");
         criteria.add(Restrictions.eq("userId", criteriaString));
         List<String> resultList = getHibernateTemplate().findByCriteria(criteria);
         return resultList;
      }
      else if (preferences.containsKey("groupId")){
         String criteriaString = preferences.get("groupId");
         criteria.add(Restrictions.eq("groupId", criteriaString));
         List<String> resultList = getHibernateTemplate().findByCriteria(criteria);
         return resultList;
      }
      else if (preferences.containsKey("roleId")){
         String criteriaString = preferences.get("roleId");
         criteria.add(Restrictions.eq("roleId", criteriaString));
         List<String> resultList = getHibernateTemplate().findByCriteria(criteria);
         return resultList;
      }
      return (null);
   }
   
   @SuppressWarnings("unchecked")
   public List<String> insertByObject(T t) throws Exception {
      Set set = new HashSet();
      set.add(t);
      saveRecords(set);
      return (List<String>)t;
   }
   
   @SuppressWarnings("unchecked")
   public void saveRecords(final Set<T> records) throws Exception {
      getSession().setFlushMode(FlushMode.AUTO);
      getHibernateTemplate().saveOrUpdateAll(records);
      getHibernateTemplate().flush();
   }
   
   @SuppressWarnings("unchecked")
   public boolean deleteByCriteria(String module, String userId, String key) {
      List<String> deleteId = getDeleteItem(module, userId, key);
           
      boolean succes;
      try{
         Iterator de = deleteId.listIterator();
         while (de.hasNext()) {
            String idString = de.next().toString();
            Long id = Long.parseLong(idString.trim());
            removeObject(id);
         }
         getSession().flush();
         succes = true;
      }
      catch (Exception e){
         succes = false;
      }
      return succes;
   }
  
   @SuppressWarnings("unchecked")
   public List<String> getDeleteItem(String module, String userId, String key){
      DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
      if(module != null || userId != null || key != null){
         if(module != null){
            criteria.add(Restrictions.eq("module", module));
         }
         if(userId != null){
            criteria.add(Restrictions.eq("userId", userId));
         }
         if(key != null){
            criteria.add(Restrictions.eq("newsletterKey", key));
         }
      }
      criteria.setProjection(Projections.property("id"));
      List<String> resultList = (List<String>)getHibernateTemplate().findByCriteria(criteria);
      return resultList;
   }
  
   @SuppressWarnings("unchecked")
   public void removeObject(final Long id) {
      Object record = getHibernateTemplate().load(getPersistentClass(), id);
      getHibernateTemplate().delete(record);
   }
}
