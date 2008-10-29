package com.finalist.newsletter.util;

import java.util.ArrayList;
import java.util.List;

import org.mmbase.bridge.Node;

import com.finalist.newsletter.domain.Subscription;
import com.finalist.newsletter.services.CommunityModuleAdapter;

public abstract class NewsletterSubscriptionUtil {

   public static final String NEWSLETTER = "newsletter";
   public static final String NEWSLETTER_THEME = "newslettertheme";
   public static final String PREFERRED_MIMETYPE = "preferredmimetype";

   public static final String MIMETYPE_HTML = "text/html";
   public static final String MIMETYPE_PLAIN = "text/plain";
   public static final String MIMETIPE_DEFAULT = MIMETYPE_HTML;
   public static final String USERNAME = "username";

   public static final String SUBSCRIPTION_STATUS_KEY = "subscriptionstatus";
   public static final int SUBSCRIPTION_STATUS_TERMINATED = 0;
   public static final int SUBSCRIPTION_STATUS_ACTIVE = 1;
   public static final int SUBSCRIPTION_STATUS_PAUSED = 2;
   public static final int SUBSCRIPTION_STATUS_DEFAULT = SUBSCRIPTION_STATUS_ACTIVE;
   public static final String STATUS_OPTIONS = "statusoptions";

   private static List<Integer> statusOptions = new ArrayList<Integer>();

   static {
      statusOptions.add(SUBSCRIPTION_STATUS_ACTIVE);
      statusOptions.add(SUBSCRIPTION_STATUS_PAUSED);
   }

   public static List<String> getAllUsersWithSubscription() {
      List<String> users = null;
      return (users);
   }

   public static List<Integer> getStatusOptions() {
      return (statusOptions);
   }

   public static Subscription convertFromNode(Node node) {
      Subscription subscription = new Subscription();
      subscription.setId(node.getIntValue("number"));
      subscription.setMimeType(node.getStringValue("format"));
      subscription.setStatus(Subscription.STATUS.valueOf(node.getStringValue("status")));
      //subscription.setSubscriber(CommunityModuleAdapter.getUserById(node.getStringValue("subscriber")));
      subscription.setSubscriberId(node.getStringValue("subscriber"));
      return subscription;
   }

}