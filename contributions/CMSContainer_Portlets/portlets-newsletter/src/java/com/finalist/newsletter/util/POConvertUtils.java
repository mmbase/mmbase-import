package com.finalist.newsletter.util;

import com.finalist.cmsc.services.community.person.Person;
import com.finalist.newsletter.domain.Subscription;
import org.mmbase.bridge.Node;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.beanutils.BeanUtils;

import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class POConvertUtils<T> {

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


   public void convert(T target, Node node) throws IllegalAccessException, InvocationTargetException, InstantiationException {


      Method[] targetFields = target.getClass().getDeclaredMethods();

      for (Method method : targetFields) {
         String name = method.getName();
         if (null != getNodeValue(node, name)) {
            BeanUtils.setProperty(target, getPropertieName(name), getNodeValue(node, name));
         }
      }
   }

   private Object getNodeValue(Node node, String name) {
      String value = null;

      if (name.startsWith("set") && node.getNodeManager().hasField(getPropertieName(name))) {
         value = node.getStringValue(getPropertieName(name));
      }

      return value;
   }

   private String getPropertieName(String name) {
      return name.substring(3, name.length()).toLowerCase();
   }

}
