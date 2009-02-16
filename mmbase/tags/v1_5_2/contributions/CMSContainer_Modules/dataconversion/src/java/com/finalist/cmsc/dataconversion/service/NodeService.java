package com.finalist.cmsc.dataconversion.service;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import net.sf.mmapps.commons.bridge.RelationUtil;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.CloudContext;
import org.mmbase.bridge.ContextProvider;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.Relation;
import org.mmbase.module.core.MMBase;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.dataconversion.dataaccess.DataHolder;
import com.finalist.cmsc.dataconversion.dataaccess.Elements;

/**
 *  the class  is a delegate for cmsc application 
 * @author Kevin
 *
 */
public class NodeService {

   private static final Logger log = Logging.getLoggerInstance(NodeService.class.getName());   
   
   /**
    *  insert one record into cmsc db.
    * @param holder
    * @param data
    * @param sources
    * @param key
    * @return
    */
   public static Integer insertData(DataHolder holder,Data data,List<Data> sources,Integer key) {  
      Cloud cloud = initCloud();      
      Integer number = null;
      String type = "";
      try {
         if(data.getType() == Constants.ENTITY_TYPE) {
            String tableName = holder.getTableName();
            NodeManager nodeManger =  cloud.getNodeManager(tableName);
            type = tableName;
            Collection<Elements> collection = holder.getCollection();
            Iterator<Elements> iterator = collection.iterator();
            Object modifyDate = null ;
            while (iterator.hasNext()) {
               Node node = nodeManger.createNode();
               Elements element = iterator.next();
               log.info("->[old node number="+key+"]");
               Iterator<Map.Entry<String,Object>>  properties = element.getMap().entrySet().iterator();
               while (properties.hasNext()) {
                  Map.Entry<String,Object> entry= properties.next();
                  if(!entry.getKey().toString().equals("lastmodifieddate")) {
                     node.setObjectValue(entry.getKey().toString(), entry.getValue()) ;
                  }
                  else {
                     modifyDate = entry.getValue();
                  }
               } 
               node.commit();
               number = node.getNumber();
               if(modifyDate != null) {
                  MMObjectNode objectNode = MMBase.getMMBase().getBuilder(tableName).getNode(number);
                  objectNode.setValue("lastmodifieddate", modifyDate); 
                  objectNode.commit();
               }
               if(element.getValue("title") != null) {
                  log.info("->[new node number="+number+"] [title = "+element.getValue("title")+"]"); 
               }
               else {
                  log.info("->[new node number="+number+"]");
               }
            }
            nodeManger.commit();
         }
         else if (data.getType() == Constants.RELATION_TYPE) {         

            Integer snumber = getSnumber( data, holder.getSnumber(),sources);
            Integer dnumber = getDnumber( data, holder.getDnumber(),sources);

            Node sourceNode = cloud.getNode(snumber.intValue());            
            Node desNode = cloud.getNode(dnumber.intValue());
            type = data.getDestinationRelationType();
            Relation relate = RelationUtil.createRelation(sourceNode, desNode, data.getDestinationRelationType());
            if(sourceNode.getNodeManager().getName().equals("contentchannel") && desNode.getNodeManager().getName().equals("article")) {
               Relation creationRelation  = RelationUtil.createRelation(desNode, sourceNode, "creationrel");
               creationRelation.commit();
            }
            if(holder.getCollection() != null) {
               Elements[] elements = new Elements[0];
               elements = holder.getCollection().toArray(elements);
               if(elements != null && elements.length >0) {
                  Elements element = elements[0];
                  Iterator<Map.Entry<String,Object>>  properties = element.getMap().entrySet().iterator();
                  while (properties.hasNext()) {
                     Map.Entry<String,Object> entry= properties.next();
                     String name = entry.getKey().toString();
                     if(name.equals("pos") && data.getDestinationRelationType().equals("imagerel")) {
                        relate.setObjectValue(name, "intro") ;
                     }
                     else {
                        relate.setObjectValue(entry.getKey().toString(), entry.getValue()) ;
                     }
                  }
                  relate.commit();
               }
            }
         }
         else if(data.getType() == Constants.SELF_RELATION_TYPE){
            Integer snumber = getDnumber( data, holder.getSnumber(),sources);
            Integer dnumber = getDnumber( data, holder.getDnumber(),sources);
            
            Node sourceNode = cloud.getNode(snumber.intValue());            
            Node desNode = cloud.getNode(dnumber.intValue());
            type = data.getDestinationRelationType();
            Relation relate = RelationUtil.createRelation(sourceNode, desNode, data.getDestinationRelationType());
            relate.commit();
         }
         else if(data.getType() == Constants.ROOT_CATEGORY_TYPE){
            Iterator<Integer> iterator = data.getIdentifiers().keySet().iterator();
            Integer sourceId = data.getRelateId();
            Node sourceNode = cloud.getNode(sourceId.intValue()); 
            type = data.getDestinationRelationType();
            while(iterator.hasNext()) {
               Integer id = iterator.next();
               Integer dId = getDnumber( data, id,sources);
               Node desNode = cloud.getNode(dId.intValue());
               Relation relate = RelationUtil.createRelation(sourceNode, desNode, data.getDestinationRelationType());
               relate.commit();
            }
         }
      } catch (Exception e) {
         // TODO Auto-generated catch block
         log.info(String.format("[type %s] [old Node %s] [Node %s ] +"+e.getMessage(),type,key,number));
      }
      return number;
   }
   /**
    * get the dnumber from data collection
    * @param data
    * @param souceNumber
    * @param sources
    * @return
    */   
   private static Integer getDnumber(Data data,Integer souceNumber,List<Data> sources) {
      
      Integer number = null;
      
      out:
      for(Data source : sources) {
         if(source.getTableName().equals(data.getTableName()))
         {
            Iterator<Map.Entry<Integer,Integer>> iterator = source.getIdentifiers().entrySet().iterator();
            while(iterator.hasNext()) {
               Map.Entry<Integer,Integer> entry = iterator.next();
               if(entry.getKey().equals(souceNumber)) {
                  number = entry.getValue();
                  break out;
               }
            }
         }
      }
      return number;
   }
   
   /**
    * get the snumber from data collection
    * @param data
    * @param souceNumber
    * @param sources
    * @return
    */
   private static Integer getSnumber(Data data,Integer souceNumber,List<Data> sources) {
      
      Integer number = null;
      
      out:
      for(Data source : sources) {
         if(source.getTableName().equals(data.getRelateTable()))
         {
            Iterator<Map.Entry<Integer,Integer>> iterator = source.getIdentifiers().entrySet().iterator();
            while(iterator.hasNext()) {
               Map.Entry<Integer,Integer> entry = iterator.next();
               if(entry.getKey().equals(souceNumber)) {
                  number = entry.getValue();
                  break out;
               }
            }
         }
      }
      return number;
   }
   
   public static void insertProperties(Properties properties) throws Exception {
      Cloud cloud = initCloud();
      NodeManager manager = cloud.getNodeManager("dataconversion");
      Node node = manager.createNode();
      node.setStringValue("id", properties.getProperty("uuid"));
      node.setStringValue("url", properties.getProperty("url"));
      node.setStringValue("driver", properties.getProperty("driverClassName"));
      node.setStringValue("username", properties.getProperty("username"));
      node.setStringValue("password", properties.getProperty("password"));
      node.setIntValue("signal", 1);
      node.commit();
   }
   
   private static Cloud initCloud() {
      
      CloudContext context =  ContextProvider.getDefaultCloudContext();     
      return context.getCloud("mmbase");
   } 
}
