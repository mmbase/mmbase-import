package com.finalist.newsletter.services;

import java.util.List;
import java.util.Set;

public interface SubscriptionHibernateService {
	
	public List<Object[]> getSubscribersRelatedInfo(Set<Long> authenticationIds, String fullName, String userName, String email, int pageSize, int offset, String order, String direction);
	public int getSubscribersCount(Set<Long>authenticationIds, String fullName, String userName, String email);
}
