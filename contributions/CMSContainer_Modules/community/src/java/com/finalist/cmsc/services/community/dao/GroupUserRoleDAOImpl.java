package com.finalist.cmsc.services.community.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.store.Directory;
import org.hibernate.FlushMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import com.finalist.cmsc.services.community.data.GroupUserRole;

public class GroupUserRoleDAOImpl  extends HibernateDaoSupport implements GroupUserRoleDAO {

   protected final Log log = LogFactory.getLog(GroupUserRoleDAOImpl.class);

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
   public GroupUserRole insertGroupUserRole(GroupUserRole groupUserRole) throws Exception {
      Set set = new HashSet();
      set.add(groupUserRole);
      saveRecords(set);
      return groupUserRole;
   }


   @Transactional(readOnly = false)
   public GroupUserRole getGroupUserRole(final Long id) {
      DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
      criteria.add(Restrictions.eq("id", id));
      List resultList = getHibernateTemplate().findByCriteria(criteria);
      return (resultList.size() != 1) ? null : (GroupUserRole) resultList.get(0);
   }
   
   @Transactional(readOnly = false)
   public GroupUserRole getGroupUserRoleByUserId(String userName) {
      DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
      criteria.add(Restrictions.eq("userId", userName));
      List resultList = getHibernateTemplate().findByCriteria(criteria);
      return (resultList.size() != 1) ? null : (GroupUserRole) resultList.get(0);
   }
   
   @Transactional(readOnly = false)
   public List getGroupUserRoleList(String userName) {
      System.out.println("GroupUserRoleDAOImpl Ingevoerde user: " + userName);
      DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
      criteria.add(Restrictions.eq("userId", userName));
      List resultList = (List)getHibernateTemplate().findByCriteria(criteria);
      return resultList;
   }


   @Transactional(readOnly = false)
   public void saveRecords(final Set<GroupUserRole> records) throws Exception {
      getSession().setFlushMode(FlushMode.AUTO);
      getHibernateTemplate().saveOrUpdateAll(records);
      getHibernateTemplate().flush();
   }


   public void deleteGroupUserRole(GroupUserRole groupUserRole) {
      removeObject(groupUserRole);
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
      return GroupUserRole.class;
   }


   public void updateGroupUserRole(GroupUserRole groupUserRole) throws Exception {
      Set<GroupUserRole> set = new HashSet<GroupUserRole>();
      set.add(groupUserRole);
      saveRecords(set);
   }
}
