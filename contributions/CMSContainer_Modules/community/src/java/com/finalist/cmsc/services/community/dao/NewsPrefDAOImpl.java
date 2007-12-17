package com.finalist.cmsc.services.community.dao;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.store.Directory;
import org.hibernate.FlushMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Projections;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import com.finalist.cmsc.services.community.data.NewsPref;

public class NewsPrefDAOImpl extends HibernateDaoSupport implements NewsPrefDAO{

   protected final Log log = LogFactory.getLog(NewsPrefDAOImpl.class);

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
   public NewsPref insertNewsPref(NewsPref newsPref) throws Exception {
      Set set = new HashSet();
      set.add(newsPref);
      saveRecords(set);
      return newsPref;
   }


   @Transactional(readOnly = false)
   public NewsPref getNewsPref(String newsPrefId) {
      DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
      criteria.add(Restrictions.eq("newsPrefId", newsPrefId));
      List resultList = getHibernateTemplate().findByCriteria(criteria);
      System.out.println("dit is de resultlist in NewsPrefDAOImpl: " + resultList);
      return (resultList.size() != 1) ? null : (NewsPref) resultList.get(0);
   }
   
   @Transactional(readOnly = false)
   public List<String> getUsersWithPreferences(String key, String value){
      DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
      criteria.add(Restrictions.eq("newsletterKey", key));
      criteria.add(Restrictions.eq("newsletterValue", value));
      criteria.setProjection(Projections.property("userId"));
      List<String> resultList = (List<String>)getHibernateTemplate().findByCriteria(criteria);
      System.out.println("dit is de resultlist in NewsPrefDAOImpl: " + resultList);
      return resultList;
   }
   
   public List<String> getUsersWithPreference(String key){
      DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
      criteria.add(Restrictions.eq("newsletterKey", key));
      criteria.setProjection(Projections.property("userId"));
      List<String> resultList = (List<String>)getHibernateTemplate().findByCriteria(criteria);
      //String preference = resultList.get(0);
      return resultList;
   }
   
   @Transactional(readOnly = false)
   public List<String> getUserPreference(String userName, String key){
      DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
      criteria.add(Restrictions.eq("userId", userName));
      criteria.add(Restrictions.eq("newsletterKey", key));
      criteria.setProjection(Projections.property("newsletterValue"));
      List<String> resultList = (List<String>)getHibernateTemplate().findByCriteria(criteria);
      //String preference = resultList.get(0);
      return resultList;
   }

   @Transactional(readOnly = false)
   public void saveRecords(final Set<NewsPref> records) throws Exception {
      getSession().setFlushMode(FlushMode.AUTO);
      getHibernateTemplate().saveOrUpdateAll(records);
      getHibernateTemplate().flush();
   }
   
   public List<String> getUsersWithPreferencesId(String userName, String key){
      DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
      criteria.add(Restrictions.eq("userId", userName));
      criteria.add(Restrictions.eq("newsletterKey", key));
      criteria.setProjection(Projections.property("id"));
      List<String> resultList = (List<String>)getHibernateTemplate().findByCriteria(criteria);
      return resultList;
   }
   
   @Transactional(readOnly = false)
   public boolean deleteNewsPrefByCriteria(String userName, String key) {
      
      List<String> deleteId = getUsersWithPreferencesId(userName, key);
      
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
   
   public List<String> getUserWithPreferenceId(String userName, String key, String value){
      DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
      criteria.add(Restrictions.eq("userId", userName));
      criteria.add(Restrictions.eq("newsletterKey", key));
      criteria.add(Restrictions.eq("newsletterValue", value));
      criteria.setProjection(Projections.property("id"));
      List<String> resultList = (List<String>)getHibernateTemplate().findByCriteria(criteria);
      return resultList;
   }
   
   @Transactional(readOnly = false)
   public void removeNewsPref(String userName, String key, String value){
      
      List<String> deleteId = getUserWithPreferenceId(userName, key, value);
      
      Iterator de = deleteId.listIterator();
      while (de.hasNext()) {
         String idString = de.next().toString();
         Long id = Long.parseLong(idString.trim());
         removeObject(id);
      }
      getSession().flush();
   }
   
   public List<String> getUserWithPreferenceId(String userName){
      DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
      criteria.add(Restrictions.eq("userId", userName));
      criteria.setProjection(Projections.property("id"));
      List<String> resultList = (List<String>)getHibernateTemplate().findByCriteria(criteria);
      return resultList;
   }
   
   @Transactional(readOnly = false)
   public void removeNewsPrefByUser(String userName){

      List<String> deleteId = getUserWithPreferenceId(userName);
      
      Iterator de = deleteId.listIterator();
      while (de.hasNext()) {
         String idString = de.next().toString();
         Long id = Long.parseLong(idString.trim());
         removeObject(id);
      }
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
      return NewsPref.class;
   }


   public void updateNewsPref(NewsPref newsPref) throws Exception {
      Set<NewsPref> set = new HashSet<NewsPref>();
      set.add(newsPref);
      saveRecords(set);
   }
   
   public List<String> getAllNewsPrefs(){
      DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
      List<String> resultList = (List<String>)getHibernateTemplate().findByCriteria(criteria);
      return resultList;
   }
   
   public List countK(String key, String value){
      DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
      criteria.setProjection(Projections.rowCount());
      criteria.add(Restrictions.eq("newsletterKey", key));
      criteria.add(Restrictions.eq("newsletterValue", value));
      List result = (List)getHibernateTemplate().findByCriteria(criteria);
      
      return result;
   }
   
   public List count(String userName, String key) {
      DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
      criteria.setProjection(Projections.rowCount());
      criteria.add(Restrictions.eq("userId", userName));
      criteria.add(Restrictions.eq("newsletterKey", key));
      List result = (List)getHibernateTemplate().findByCriteria(criteria);
      
      return result;
   }

   public List count(String userName, String key, String value) {
      DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
      criteria.setProjection(Projections.rowCount());
      criteria.add(Restrictions.eq("userId", userName));
      criteria.add(Restrictions.eq("newsletterKey", key));
      criteria.add(Restrictions.eq("newsletterValue", value));
      List result = (List)getHibernateTemplate().findByCriteria(criteria);
      
      return result;
   }
   
   public List countByKey(String key){
      DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
      criteria.setProjection(Projections.rowCount());
      criteria.add(Restrictions.eq("newsletterKey", key));
      List result = (List)getHibernateTemplate().findByCriteria(criteria);
      
      return result;
   }
}
