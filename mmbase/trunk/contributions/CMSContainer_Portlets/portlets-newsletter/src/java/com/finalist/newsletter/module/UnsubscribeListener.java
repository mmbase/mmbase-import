package com.finalist.newsletter.module;

import com.finalist.cmsc.community.CommunityListener;
import com.finalist.cmsc.services.community.ApplicationContextFactory;
import com.finalist.newsletter.services.NewsletterSubscriptionServices;

public class UnsubscribeListener implements CommunityListener{
   public void notify(Long authId) {
      NewsletterSubscriptionServices subscriptionServices = (NewsletterSubscriptionServices) ApplicationContextFactory.getBean("subscriptionServices");
      subscriptionServices.deleteSubscriptionsByAuthId(authId);
   }
}
