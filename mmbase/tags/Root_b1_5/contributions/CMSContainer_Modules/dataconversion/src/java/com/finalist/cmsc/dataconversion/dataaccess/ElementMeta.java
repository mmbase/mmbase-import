package com.finalist.cmsc.dataconversion.dataaccess;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.finalist.cmsc.dataconversion.service.XMLUtil;

public class ElementMeta {
   
   private  Element element;
   
   public ElementMeta(Element element) {
      this.element = element;
   }
   
   public String[] getFieldNames() {
    
      return getFields("field");  
   }
   
   public String[] getRelateFields() {
    
      return getFields("relationfield");    
   }
   
   private String[] getFields(String type) {
      NodeList nodeList = element.getChildNodes();
      String[] fields = new String[0];
      List<String> list = new ArrayList<String>();
      for(int i = 0 ; i <nodeList.getLength() ; i++) {
         Node node = nodeList.item(i);
         if(node.getNodeName().equalsIgnoreCase(type)) {
            Element elem = (Element)node;
            list.add(elem.getAttribute("sourcename"));
         }
      }
      return list.toArray(fields);
   }
   
   public String getDesFieldName(String sourceName) {
      NodeList nodeList = element.getChildNodes();
      String desName = "";
      for(int i = 0 ; i <nodeList.getLength() ; i++) {
         Node node = nodeList.item(i);
         if(node.getNodeName() != "#text") {
            if(((Element)node).getAttribute("sourcename").equalsIgnoreCase(sourceName)) {
               desName = ((Element)node).getAttribute("destinationname");
               break;
            }
         }
      }
      return desName;
   }
   
   public String getPrefix(String fieldName) {
      NodeList nodeList = element.getChildNodes();
      String prefix = "";
      for(int i = 0 ; i <nodeList.getLength() ; i++) {
         Node node = nodeList.item(i);
         if(node.getNodeName().equalsIgnoreCase("field")) {
            Element elem = (Element)node;
            if(fieldName.equals(elem.getAttribute("sourcename")) && StringUtils.isNotEmpty(elem.getAttribute("prefix"))) {
               prefix = elem.getAttribute("prefix");
            }
         }
      }
      return prefix;
   }
   
   public String getSourceTableName() {
      return element.getAttribute("sourcetype");
   }
   
   public String getDesTableName() {
      return element.getAttribute("destinationtype");
   }
   
   public String getRelTableName() {
      return element.getAttribute("sourcerelationtype");
   }
   
   public String getSelfRelTableName() {
      return XMLUtil.getSelfRelSourceRelationType(element);
   }
   
   public String getSelfRelDesTableName() {
      return XMLUtil.getSelfRelDesRelationType(element);
   }
   
   public String getDestinationRelTableName() {
      return element.getAttribute("destinationrelationtype");
   }
   
   public String getRelDataType() {
      return ((Element)element.getParentNode()).getAttribute("sourcetype");
   }
}
