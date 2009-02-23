/*
 * 
 * This software is OSI Certified Open Source Software. OSI Certified is a certification mark of the Open Source
 * Initiative.
 * 
 * The license (Mozilla version 1.0) can be read at the MMBase site. See http://www.MMBase.org/license
 * 
 */
package com.finalist.cmsc.services.community.security;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.finalist.cmsc.paging.PagingStatusHolder;
import com.finalist.cmsc.services.HibernateService;

/**
 * @author Remco Bos
 */
public class AuthorityHibernateService extends HibernateService implements AuthorityService {

   /** {@inheritDoc} */
   @Transactional
   public Authority createAuthority(String parentName, String name) {
      Authority authority = new Authority();
      authority.setName(name);
      getSession().save(authority);
      return authority;
   }

   /** {@inheritDoc} */
   @Transactional
   public void deleteAuthority(String name) {
      Authority authority = findAuthorityByName(name);
      Set < Authentication > set = new HashSet < Authentication >();
      set.addAll(authority.getAuthentications());
      for (Authentication authentication : authority.getAuthentications()) {
         authentication.getAuthorities().remove(authority);
      }
      authority.getAuthentications().clear();
      getSession().delete(authority);

   }

   /** {@inheritDoc} */
   @Transactional(readOnly = true)
   public boolean authorityExists(String name) {
      Authority authority = findAuthorityByName(name);
      return authority != null;
   }

   /** {@inheritDoc} */
   @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
   public Authority findAuthorityByName(String name) {
      Criteria criteria = getSession().createCriteria(Authority.class).add(Restrictions.eq("name", name));
      return findAuthorityByCriteria(criteria);
   }
   /** {@inheritDoc} */
   @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
   public Authority findAuthorityById(Long authorityId) {
      Criteria criteria = getSession().createCriteria(Authority.class).add(Restrictions.eq("id",authorityId));
      return findAuthorityByCriteria(criteria);
   }

   @SuppressWarnings("unchecked")
   private List < Authority > addConditionToCriteria(PagingStatusHolder holder, Criteria criteria) {
      criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
      if ("group".equalsIgnoreCase(holder.getSort()) && "asc".equalsIgnoreCase(holder.getDir())) {
         criteria.addOrder(Order.asc("name"));
      } else if ("group".equalsIgnoreCase(holder.getSort()) && "desc".equalsIgnoreCase(holder.getDir())) {
         criteria.addOrder(Order.desc("name"));
      }
      List list = criteria.list();
      List newlist = new ArrayList();
      for (int i = holder.getOffset(); i < holder.getOffset() + holder.getPageSize() && i < list.size(); i++) {
         newlist.add(list.get(i));
      }
      return newlist;
   }

   @Transactional(readOnly = true)
   public List < Authority > findAssociatedAuthorityByName(String name, PagingStatusHolder holder) {
      Criteria criteria = getSession().createCriteria(Authority.class).add(Restrictions.like("name", "%" + name + "%"));
      return addConditionToCriteria(holder, criteria);
   }

   /** {@inheritDoc} */
   @Transactional(readOnly = true)
   public Set < String > getAuthorityNames() {
      Criteria criteria = getSession().createCriteria(Authority.class);
      return findAuthorityNamesByCriteria(criteria);
   }

   @Transactional(readOnly = true)
   public List < Authority > getAllAuthorities(PagingStatusHolder holder) {
      Criteria criteria = getSession().createCriteria(Authority.class);
      return addConditionToCriteria(holder, criteria);
   }

   /** {@inheritDoc} */
   @Transactional(readOnly = true)
   public Set < String > getAuthorityNamesForUser(String userId) {
      Criteria criteria = getSession().createCriteria(Authority.class).createCriteria("authentications").add(
            Restrictions.eq("userId", userId));
      return findAuthorityNamesByCriteria(criteria);
   }

   @SuppressWarnings("unchecked")
   private Authority findAuthorityByCriteria(Criteria criteria) {
      List authorities = criteria.list();
      return authorities.size() > 0 ? (Authority) authorities.get(0) : null;
   }

   @SuppressWarnings("unchecked")
   private Set < String > findAuthorityNamesByCriteria(Criteria criteria) {
      List authorityList = criteria.list();
      Set < String > result = new HashSet < String >();
      for (Iterator iter = authorityList.iterator(); iter.hasNext();) {
         Authority authority = (Authority) iter.next();
         result.add(authority.getName());
      }
      return result;
   }

   @SuppressWarnings("unchecked")
   private Set < Authority > findAuthoritesByCriteria(Criteria criteria) {
      List authorityList = criteria.list();
      Set < Authority > result = new HashSet < Authority >();
      for (Iterator iter = authorityList.iterator(); iter.hasNext();) {
         Authority authority = (Authority) iter.next();
         result.add(authority);
      }
      return result;
   }

   @Transactional(readOnly = true)
   public int countAllAuthorities() {
      Criteria criteria = getSession().createCriteria(Authority.class);
      return findAuthoritesByCriteria(criteria).size();
   }

   @Transactional(readOnly = true)
   public int countAssociatedAuthorities(String name) {
      Criteria criteria = getSession().createCriteria(Authority.class).add(Restrictions.like("name", "%" + name + "%"));
      return findAuthoritesByCriteria(criteria).size();
   }

   /**
    * @param map stored paramate
    * @param holder treat PagingStatus
    * @return  List stored authorities  
    */
   @Transactional(readOnly = true)
   public List < Authority > getAssociatedAuthorities(Map conditions, PagingStatusHolder holder) {
      List < Authority > authorities = new ArrayList < Authority >();
      StringBuffer strb = new StringBuffer();
      basicGetAssociatedAuthorities(conditions, strb);
      strb.append(holder.getSortToken());
      Query q = getSession().createSQLQuery(strb.toString());
      q.setMaxResults(holder.getPageSize()).setFirstResult(holder.getOffset());
      List < String > l = q.list();
      for (String s : l) {
         Authority authority = new Authority();
         // String str=((Object[])s)[0].toString();
         authority = findAuthorityByName(s);
         authorities.add(authority);
      }
      return authorities;
   }

   private void basicGetAssociatedAuthorities(Map conditions, StringBuffer strb) {
      strb.append("select distinct asn.name "
            +
            // ",concat(p.firstName ,p.lastName) as fullNmae " +
            "from  authorities asn" + " left outer join authentication_authorities on"
            + " asn.id=authentication_authorities.authority_id" + " left outer join authentication on"
            + " authentication_authorities.authentication_id=authentication.id"
            + " left outer join people p on p.authenticationId=authentication.id");
      if (null != conditions && conditions.containsKey("group")) {
         String group = (String) conditions.get("group");
         strb.append(" where upper(asn.name) like'%" + group.toUpperCase() + "%'");
      }

      if (null != conditions && conditions.containsKey("username")) {
         String[] members = (String[]) conditions.get("username");
         if (members.length < 1) return;
         int i = 0;
         for (String m : members) {
            String[] names = m.split(" ");
            if (names.length > 2) continue;
            if (i == 0 && null == conditions.get("group")) strb.append(" where (");
            if (i == 0 && null != conditions.get("group")) strb.append(" or (");
            if (i > 0) strb.append("or(");
            if (names.length == 2) strb.append(" upper(concat(p.firstName ,p.lastName)) like'%"
                  + names[0].toUpperCase() + "%'or upper(concat(p.firstName ,p.lastName)) like'%"
                  + names[1].toUpperCase() + "%'");
            else if (names.length == 1) strb.append(" upper(concat(p.firstName ,p.lastName)) like'%"
                  + names[0].toUpperCase() + "%'");
            strb.append(")");
            i++;
         }
      }
   }

   /**
    * @param map stored paramate
    * @param holder treat PagingStatus
    * @return Num counted  
    */
   @Transactional(readOnly = true)
   public int getAssociatedAuthoritiesNum(Map conditions, PagingStatusHolder holder) {
      StringBuffer strb = new StringBuffer();
      basicGetAssociatedAuthorities(conditions, strb);
      Query q = getSession().createSQLQuery(strb.toString());
      return q.list().size();
   }
}
