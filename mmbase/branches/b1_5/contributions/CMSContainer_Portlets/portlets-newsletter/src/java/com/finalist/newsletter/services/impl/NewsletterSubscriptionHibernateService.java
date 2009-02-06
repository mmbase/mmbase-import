package com.finalist.newsletter.services.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.springframework.transaction.annotation.Transactional;

import com.finalist.cmsc.paging.PagingStatusHolder;
import com.finalist.cmsc.paging.PagingUtils;
import com.finalist.cmsc.services.HibernateService;
import com.finalist.newsletter.services.SubscriptionHibernateService;

public class NewsletterSubscriptionHibernateService extends HibernateService implements SubscriptionHibernateService {

   @Transactional
   public List<Object[]> getSubscribersRelatedInfo(Set<Long> authenticationIds, String fullName, String userName, String email, boolean paging) {

      Query query = executeSubscribersSearch(authenticationIds, fullName, userName, email, paging, false);

      //Execute query
      return query.list();
   }

   @Transactional
   private Query executeSubscribersSearch(Set<Long> authenticationIds, String fullName, String userName, String email, boolean paging,
          boolean onlyCount) {
      PagingStatusHolder pagingHolder = PagingUtils.getStatusHolder();
      StringBuilder strb = new StringBuilder();
      if (onlyCount) {
         strb.append("select count(*)");
      }
      else { 
         strb.append("select person.firstName, person.infix, person.lastName, person.email, person.authenticationId, authentication1.userId");
      }
      strb.append(" from people person, authentication authentication1 " + "where person.authenticationId = authentication1.id");
      if (StringUtils.isNotBlank(fullName)) {
         String[] names = fullName.split(" ");
         if (names.length >= 2) {
            strb.append(" and (person.firstName like '%" + names[0] + "%' or person.firstName like '%" + fullName + "%')"
                  + " and (person.lastName like '%" + names[1] + "%' or person.lastName like '%" + fullName + "%')");
         } else if (names.length == 1) {
            strb.append(" and (person.firstName like '%" + names[0] + "%')");
         }
      }
      if (StringUtils.isNotBlank(email)) {
         strb.append(" and person.email like '%" + email.trim() + "%'");
      }
      if (StringUtils.isNotBlank(userName)) {
         strb.append(" and authentication1.userId like '%" + userName.trim() + "%'");
      }
      if (authenticationIds.size() > 0) {
         StringBuffer idStr = new StringBuffer(" and authentication1.id in (");
         for (Long authentication : authenticationIds) {
            idStr.append(authentication + ",");
         }
         idStr.delete(idStr.length() - 1, idStr.length());
         idStr.append(")");
         strb.append(idStr);
      }
      String order = pagingHolder.getSort();

      if ("fullname".equals(order)) {
         strb.append(" order by person.firstName");
      } else if ("username".equals(order)) {
         strb.append(" order by authentication1.userId");
      } else if ("email".equals(order)) {
         strb.append(" order by person.email");
      } else {
         strb.append(" order by person.id");
      }
      strb.append(" " + pagingHolder.getDir());

      Query query = getSession().createSQLQuery(strb.toString());
      if (paging) {
         query.setFirstResult(pagingHolder.getOffset());
         query.setMaxResults(pagingHolder.getPageSize());
      }
      return query;
   }
   
   @Transactional
   public int getSubscribersRelatedInfoCount(Set<Long> authenticationIds, String fullName, String userName, String email, boolean paging) {
      Query query = executeSubscribersSearch(authenticationIds, fullName, userName, email, paging, true);
      return (Integer)(query.uniqueResult());
   }
   
}
