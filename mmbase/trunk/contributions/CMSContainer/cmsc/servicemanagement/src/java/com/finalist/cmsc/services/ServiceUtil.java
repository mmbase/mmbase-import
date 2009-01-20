package com.finalist.cmsc.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.struts.util.LabelValueBean;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeManagerList;
import org.mmbase.bridge.NotFoundException;

/**
 * @author Marco
 * 
 */
public class ServiceUtil {
   

   public static List<LabelValueBean> getDirectChildTypes(Cloud cloud,String parent) {
      List<NodeManager> resultManager = new ArrayList<NodeManager>();
      NodeManagerList nml = cloud.getNodeManagers();
      Iterator<NodeManager> v = nml.iterator();
      while (v.hasNext()) {
         NodeManager child = v.next();
         if (isDirectChildType(cloud,child.getName(), parent)) {
            resultManager.add(child);
         }
      }
      Collections.sort(resultManager, new Comparator<NodeManager>() {
         public int compare(NodeManager o1, NodeManager o2) {
            return o1.getGUIName().compareTo(o2.getGUIName());
         }
      });
      
      List<LabelValueBean> result = new ArrayList<LabelValueBean>();
      for (NodeManager manager : resultManager) {
         String name = manager.getName();
         LabelValueBean bean = new LabelValueBean(manager.getGUIName(), name);
         result.add(bean);
      }
      return result;
   }
   
   public static List<String> getAllChildTypes(Cloud cloud,String parent) {
      List<String> result = new ArrayList<String>();
      NodeManagerList nml = cloud.getNodeManagers();
      Iterator<NodeManager> v = nml.iterator();
      while (v.hasNext()) {
         String child = v.next().getName();
         if (isChildType(cloud,child, parent)) {
            result.add(child);
         }
      }
      Collections.sort(result);
      return result;
   }

   private static boolean isDirectChildType(Cloud cloud,String child, String parent) {
      if (parent.equals(child)) {
         // parent manager is not a parent type
         return false;
      }
      try {
         NodeManager childManager = cloud.getNodeManager(child);
         String actualParent = childManager.getParent().getName();
         if (parent.equals(actualParent)) {
            return true;
         }
      }
      catch (NotFoundException nfe) {
         // Ran out of NodeManager parents
      }
      return false;
   }
   
   private static boolean isChildType(Cloud cloud,String child, String parent) {
      if (parent.equals(child)) {
         // parent manager is not a parent type
         return false;
      }
      try {
         NodeManager nmTemp = cloud.getNodeManager(child).getParent();
         while (!parent.equals(nmTemp.getName())) {
            nmTemp = nmTemp.getParent();
         }
         return true;
      }
      catch (NotFoundException nfe) {
         // Ran out of NodeManager parents
      }
      return false;
   }
}
