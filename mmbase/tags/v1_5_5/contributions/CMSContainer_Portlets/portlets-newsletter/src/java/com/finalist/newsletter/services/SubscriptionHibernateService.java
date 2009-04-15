package com.finalist.newsletter.services;

import java.util.List;
import java.util.Set;

public interface SubscriptionHibernateService {

   public List<Object[]> getSubscribersRelatedInfo(Set<Long> authenticationIds, String fullName, String userName, String email, boolean paging);
   
   public int getSubscribersRelatedInfoCount(Set<Long> authenticationIds, String fullName, String userName, String email, boolean paging);
}
