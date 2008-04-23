package com.finalist.newsletter.util;

import com.finalist.cmsc.services.community.person.Person;
import com.finalist.newsletter.domain.Subscription;
import com.finalist.newsletter.services.CommunityModuleAdapter;
import org.apache.commons.beanutils.BeanUtils;
import org.mmbase.bridge.Node;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class POConvertUtils<T> {
     private static Logger log = Logging.getLoggerInstance(POConvertUtils.class.getName());
   public static List<Subscription> convertSubscriptions(List<Node> nodes) {
      List<Subscription> subscriptions = new ArrayList<Subscription>();
      for (Node node : nodes) {
         subscriptions.add(convertSubscription(node));
      }
      return subscriptions;
   }

   public static Subscription convertSubscription(Node node) {
      Subscription subscription = new Subscription();
//      Person subscripber = CommunityModuleAdapter.;
//      subscription.setSubscriber(subscripber);
      return subscription;
   }


   public void convert(T target, Node node) {

      try {
         Method[] targetFields = target.getClass().getDeclaredMethods();

         for (Method method : targetFields) {
            String name = method.getName();
            if("setId".equals(name)){
               BeanUtils.setProperty(target, "id", node.getNumber());               
            }
            if (null != getNodeValue(node, name)) {
               String propertyName = name.substring(3,4).toLowerCase()+name.substring(4, name.length());
               BeanUtils.setProperty(target, propertyName, getNodeValue(node, name));
            }
         }
      } catch (Exception e) {
        log.error("Error when convert node to pojo",e);
      }


   }

   private Object getNodeValue(Node node, String name) {
      String value = null;

      if (!name.startsWith("set")) {
         return null;
      }

      for (String propertyName : getPropertieNames(name)) {
         if (node.getNodeManager().hasField(propertyName)) {
            value = node.getStringValue(propertyName);
            break;
         }
      }

      return value;
   }

   private String[] getPropertieNames(String methodName) {
      methodName = methodName.substring(3, methodName.length());

      String[] names = new String[]{methodName.toLowerCase(),""};

      StringBuffer buffer = new StringBuffer();

      char[] chars = methodName.toCharArray();
      for (int i = 0; i < chars.length; i++) {
         char aChar = chars[i];

         if (0 == i) {
            buffer.append(String.valueOf(aChar).toLowerCase());
            continue;
         }

         if (Character.isUpperCase(aChar)) {
            buffer.append("_");
         }
         buffer.append(String.valueOf(aChar).toLowerCase());
      }

      names[1] = buffer.toString();

      return names;
   }

}
