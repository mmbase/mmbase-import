package com.finalist.newsletter.util;

import com.finalist.cmsc.services.community.person.Person;
import com.finalist.newsletter.domain.Subscription;
import org.mmbase.bridge.Node;

import java.util.List;
import java.util.ArrayList;

public class POConvertUtils {
   public static List<Subscription> convertSubscriptions(List<Node> nodes) {
      List<Subscription> subscriptions = new ArrayList<Subscription>();
      for (Node node : nodes) {
         subscriptions.add(convertSubscription(node));
      }
      return subscriptions;
   }

   public static Subscription convertSubscription(Node node) {
      Subscription subscription = new Subscription();
      Person subscripber = CommunityModuleAdapter.getSubscriber(Integer.decode(node.getStringValue("subscriber")));
      subscription.setSubscriber(subscripber);
      return subscription;
   }

}
