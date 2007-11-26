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

import com.finalist.cmsc.services.community.data.Role;

public class RoleDAOImpl extends HibernateDaoSupport implements RoleDAO {

   protected final Log log = LogFactory.getLog(RoleDAOImpl.class);

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
   public Role insertRole(Role role) throws Exception {
      Set set = new HashSet();
      set.add(role);
      saveRecords(set);
      return role;
   }


   @Transactional(readOnly = false)
   public Role getRole(final Long id) {
      DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
      criteria.add(Restrictions.eq("id", id));
      List resultList = getHibernateTemplate().findByCriteria(criteria);
      return (resultList.size() != 1) ? null : (Role) resultList.get(0);
   }


   @Transactional(readOnly = false)
   public void saveRecords(final Set<Role> records) throws Exception {
      getSession().setFlushMode(FlushMode.AUTO);
      getHibernateTemplate().saveOrUpdateAll(records);
      getHibernateTemplate().flush();
   }


   public void deleteRole(Role role) {
      removeObject(role);
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
      return Role.class;
   }


   public void updateRole(Role role) throws Exception {
      Set<Role> set = new HashSet<Role>();
      set.add(role);
      saveRecords(set);
   }
}
