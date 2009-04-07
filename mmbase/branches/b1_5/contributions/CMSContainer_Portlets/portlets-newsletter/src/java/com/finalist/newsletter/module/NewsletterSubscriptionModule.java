package com.finalist.newsletter.module;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.module.Module;

import com.finalist.cmsc.community.CommunityManager;

public class NewsletterSubscriptionModule extends Module  {
   static Log log = LogFactory.getLog(NewsletterSubscriptionModule.class);
   public void onload() {
      // nothing
      System.out.println();
   }
   public void init() {
      CommunityManager.registerListener(new UnsubscribeListener());
   }
}
