package com.finalist.cmsc.dataconversion.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XMLUtil {

   public static String getDestinationType(Element element) {
      return element.getAttribute("destinationtype");
   }
   
   public static boolean isRelatedElement(Element element) {
      return element.getNodeName().equals("related")?Boolean.TRUE:Boolean.FALSE;
   }
   
   public static String getSourceRelationType(Element element) {
      return element.getAttribute("sourcerelationtype");
   }
   
   public static String getDestinationRelationType(Element element) {
      return element.getAttribute("destinationrelationtype");
   }
   
   public static String getSourceType(Element element) {
      return element.getAttribute("sourcetype");
   }
   
   public static String getRelateSourceType(Element element) {
      return ((Element)element.getParentNode()).getAttribute("sourcetype");
   }
   
   public static String getRelateType(Element element) {
      return ((Element)element.getParentNode()).getAttribute("destinationtype");
   }
   
   public static int getSize(Element element) {
      if(StringUtils.isEmpty(element.getAttribute("size"))) {
         return -1;
      }
      return Integer.parseInt(element.getAttribute("size"));
   }
   public static boolean hasRelation(Element element) {
      
      NodeList relationList = element.getElementsByTagName("relationfield");
      return relationList.getLength() >0;
   }
   public static boolean hasSelfRelation(Element element) {
      
      boolean flag = false;
      NodeList childs = element.getChildNodes();
      for(int i = 0 ;i < childs.getLength() ; i++) {
         if (childs.item(i).getNodeName().equalsIgnoreCase("selfrelated")) {
            flag = true;
         }
      }
      return flag;
   }
   public static Element[] getDirectRelateChildNodes(Element element) {
    //  element.
      Element[] elements = new Element[0];
      List<Element> list = new ArrayList<Element>();      
      NodeList relationList = element.getElementsByTagName("related");
      for(int i = 0 ; i < relationList.getLength() ; i++) {
         if(((Element)relationList.item(i)).getParentNode() == element) {
            list.add((Element)relationList.item(i));
         }
      }
     return list.toArray(elements);
   }
   
   public static Element getSelfRelateChildNodes(Element element) {
      Element ele = null;
      NodeList childs = element.getChildNodes();
      for(int i = 0 ;i < childs.getLength() ; i++) {
         if (childs.item(i).getNodeName().equalsIgnoreCase("selfrelated")) {
            ele = (Element)childs.item(i);
         }
      }
      return ele;
   }
   
   public static String getSelfRelSourceRelationType(Element element) {
      Element ele = getSelfRelateChildNodes(element);
      if(ele != null) {
    	  return ele.getAttribute("sourcerelationtype");
      }
      else {
    	  return null;
      }
   }
   
   public static String getSelfRelDesRelationType(Element element) {
      Element ele = getSelfRelateChildNodes(element);
      if(ele != null) {
    	  return ele.getAttribute("destinationrelationtype");
      }
      else {
    	  return null;
      }
   }
   
   public static String[] getAllTableNames(Document dom) {
      String[] tableNames = new String[0];
      
      Set<String> set = new HashSet<String>();      
      NodeList dataType = dom.getElementsByTagName("datatype");
      add(dataType,set);
      NodeList  relationList = dom.getElementsByTagName("related");
      add(relationList,set);
      
      return set.toArray(tableNames);
   }
   
   private static void add(NodeList nodes,Set<String> set) {
      
      for(int i = 0 ; i < nodes.getLength();i++) {
         String tableName = ((Element)nodes.item(i)).getAttribute("destinationtype");
         set.add(tableName);
         
         String rel = ((Element)nodes.item(i)).getAttribute("destinationtype");
         if(StringUtils.isNotEmpty(rel)) {
            set.add(rel);
         }
      }      
   }
   
   public static Element getElementByTableName(String name,Element element) {
      if(element.getAttribute("sourcetype").equalsIgnoreCase(name))
         return element;
      NodeList relationList = element.getElementsByTagName("related");
      Element ele = null;
      for(int i = 0 ; i < relationList.getLength(); i++) {
         if(((Element)relationList.item(i)).getAttribute("sourcetype").equalsIgnoreCase(name)) {
            ele =  (Element)relationList.item(i);
            break;
         }
      }
      return ele;
   }
   
 
 }
