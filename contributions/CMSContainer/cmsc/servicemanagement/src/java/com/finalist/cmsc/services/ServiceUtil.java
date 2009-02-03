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

import com.finalist.cmsc.mmbase.PropertiesUtil;

/**
 * @author Marco
 * 
 */
public class ServiceUtil {
   
   private static final String PROPERTY_HIDDEN_TYPES = "system.contenttypes.hide";
   
   public static List<LabelValueBean> getDirectChildTypes(Cloud cloud,String parent) {
      List<NodeManager> resultManager = new ArrayList<NodeManager>();
      NodeManagerList nml = cloud.getNodeManagers();
      Iterator<NodeManager> v = nml.iterator();
      List<String> hiddenTypes = getHiddenTypes();
      while (v.hasNext()) {
         NodeManager child = v.next();
         String name = child.getName();
         if (!hiddenTypes.contains(name) && isDirectChildType(cloud,name, parent)) {
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
      List<String> hiddenTypes = getHiddenTypes();
      while (v.hasNext()) {
         String child = v.next().getName();
         if (!hiddenTypes.contains(child) && isChildType(cloud,child, parent)) {
            result.add(child);
         }
      }
      Collections.sort(result);
      return result;
   }

   public static List<LabelValueBean> getAllChildTypesNew(Cloud cloud,String parent) {
      List<LabelValueBean> result = new ArrayList<LabelValueBean>();
      List<NodeManager> resultManager = new ArrayList<NodeManager>();
      NodeManagerList nml = cloud.getNodeManagers();
      Iterator<NodeManager> v = nml.iterator();
      while (v.hasNext()) {
         NodeManager child = v.next();
         if (isChildType(cloud, child.getName(), parent)) {
            resultManager.add(child);
         }
      }
      for (NodeManager manager : resultManager) {
         String name = manager.getName();
         LabelValueBean bean = new LabelValueBean(manager.getGUIName(), name);
         result.add(bean);
      }
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
       //  NodeManager nmTemp = childManager.getParent();
       //  while (!parent.equals(nmTemp.getName())) {
       //     nmTemp = nmTemp.getParent();
        // }
       //  return true;
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
   private static List<String> getHiddenTypes() {
      String property = PropertiesUtil.getProperty(PROPERTY_HIDDEN_TYPES);
      if (property == null) {
         return new ArrayList<String>();
      }

      ArrayList<String> list = new ArrayList<String>();
      String[] values = property.split(",");
      for (String value : values) {
         list.add(value);
      }
      return list;
   }

}
