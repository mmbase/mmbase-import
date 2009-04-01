/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.services.publish;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;

import com.finalist.cmsc.services.ServiceManager;

public class Publish {

   private final static PublishService cService = (PublishService) ServiceManager.getService(PublishService.class);


   public static boolean isPublished(Node node) {
      return cService.isPublished(node);
   }


   public static void publish(Node node) {
      cService.publish(node);
   }


   public static void publish(Node node, NodeList nodes) {
      cService.publish(node, nodes);
   }


   public static boolean isPublishable(Node node) {
      return cService.isPublishable(node);
   }


   public static void remove(Node node) {
      cService.remove(node);
   }


   public static void unpublish(Node node) {
      cService.unpublish(node);
   }


   public static int getRemoteNumber(Node node) {
      return cService.getRemoteNumber(node);
   }

   public static Node getRemoteNode(Node node) {
       return cService.getRemoteNode(node);
   }
   
   public static String getRemoteContentUrl(Node node) {
       return cService.getRemoteContentUrl(node);
   }

   public static String getRemoteUrl(String appPath) {
       return cService.getRemoteUrl(appPath);
   }

   public static Cloud getRemoteCloud(Cloud cloud) {
      return cService.getRemoteCloud(cloud);
   }

}
