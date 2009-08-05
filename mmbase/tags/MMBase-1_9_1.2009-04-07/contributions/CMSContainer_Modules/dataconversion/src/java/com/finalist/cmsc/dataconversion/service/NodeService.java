package com.finalist.cmsc.dataconversion.service;

import java.util.*;
import java.util.Map.Entry;

import net.sf.mmapps.commons.bridge.RelationUtil;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.SearchUtil;
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
    * @return mew node number
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

            while (iterator.hasNext()) {
               Node node = nodeManger.createNode();
               Elements element = iterator.next();
               log.info("->[old node number="+key+"]");
               Iterator<Map.Entry<String,Object>>  properties = element.getMap().entrySet().iterator();
               while (properties.hasNext()) {
                  Map.Entry<String,Object> entry= properties.next();
                  setNodeField(node, entry.getKey().toString(), entry.getValue());
               } 
               node.commit();
               number = node.getNumber();

               if(element.getValue("title") != null) {
                  log.info("->[new node number="+number+"] [title = "+element.getValue("title")+"]"); 
               }
               else {
                  log.info("->[new node number="+number+"]");
               }
            }
         }
         else if (data.getType() == Constants.RELATION_TYPE) {         

            Integer snumber = getSnumber( data, holder.getSnumber(),sources);
            Integer dnumber = getDnumber( data, holder.getDnumber(),sources);

            Node sourceNode = cloud.getNode(snumber.intValue());            
            Node desNode = cloud.getNode(dnumber.intValue());
            type = data.getDestinationRelationType();
            Relation relate;
            if ("destinationtype".equals(data.getReverse())) {
               relate = RelationUtil.createRelation(desNode, sourceNode, data.getDestinationRelationType());
            }else{
               relate = RelationUtil.createRelation(sourceNode, desNode, data.getDestinationRelationType());
            }
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
                        relate.setValue(name, "intro") ;
                     }
                     else {
                        setNodeField(relate, entry.getKey().toString(), entry.getValue());
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
               Relation relate = RelationUtil.createRelation(sourceNode, desNode, type);
               relate.commit();
            }
         }
      } catch (Exception e) {
         log.info(String.format("[type %s] [old Node %s] [Node %s ] +"+e.getMessage(),type,key,number));
      }
      return number;
   }
   private static void setNodeField(Node node, String fieldname, Object value) {
      Field nodeField = node.getNodeManager().getField(fieldname);
      if (nodeField.isReadOnly() || nodeField.getState() == Field.STATE_SYSTEM) {
         node.setValueWithoutProcess(fieldname, value);
      }
      else {
         node.setValue(fieldname, value) ;
      }
   }
   //createRelationData for the element relateddatatype
   public static void createRelationData(List<String> reskeys, Data reldata) {
      Cloud cloud = initCloud();
      String type = "";
      try {
         if (reldata.getType() == Constants.RELATION_DATA_TYPE) {
            for (String sd : reskeys) {
               String[] spd = sd.split(",");
               Integer snumber = getNewkey(cloud, Integer.parseInt(spd[0]));
               Integer dnumber = getNewkey(cloud, Integer.parseInt(spd[1]));

               if (snumber > 0 && dnumber > 0) {
                  Node sourceNode = cloud.getNode(snumber.intValue());
                  Node desNode = cloud.getNode(dnumber.intValue());
                  type = reldata.getDestinationRelationType();
                  Relation relate;
                  if ("destinationtype".equals(reldata.getReverse())) {
                     relate = RelationUtil.createRelation(desNode, sourceNode, type);
                  } else {
                     relate = RelationUtil.createRelation(sourceNode, desNode, type);
                  }
                  relate.commit();
               }
            }
         }
      } catch (Exception e) {
         log.info(String.format("[type %s] [old Node %s] [Node %s ] +" + e.getMessage(), type));
      }
   }
   // search newNumber from mapping
   private static Integer getNewkey(Cloud cloud,int parseInt) {
      NodeManager manager = cloud.getNodeManager("migration_mappings");
      NodeQuery query = manager.createQuery();
      SearchUtil.addEqualConstraint(query, manager.getField("old_number"), parseInt);
      NodeList list = query.getList();
      if(list != null && list.size() >0) {
         Node node = (Node)list.get(0);
         return node.getIntValue("new_number");
      }else{
         return -1;
      }
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
   
   // insertMigrationMappings to produce a mapping between old and   new node numbers.
   public static void insertMigrationMappings(List<Data> clonedSources) throws Exception {
      Cloud cloud = initCloud();
      NodeManager manager = cloud.getNodeManager("migration_mappings");
      for (Data data : clonedSources) {
         if (data.getType() == Constants.ENTITY_TYPE || data.getType() == Constants.ROOT_CATEGORY_TYPE) {
            HashMap<Integer, Integer> mappingNumber = data.getIdentifiers();
            for (Entry<Integer, Integer> entry : mappingNumber.entrySet()) {
               Node node = manager.createNode();
               Integer key = entry.getKey();
               Integer val = entry.getValue();
               node.setValue("old_number", key);
               node.setValue("new_number", val);
               node.commit();
            }
         }
      }
       log.info("----> end insertMigrationMappings ");
   }
   
   private static Cloud initCloud() {
      
      CloudContext context =  ContextProvider.getDefaultCloudContext();     
      return context.getCloud("mmbase");
   }
   
   public static void linkRootDatas(Map<String, ArrayList<Integer>> rootDatas, String node) {
      Cloud cloud = initCloud();
      Node desNode = cloud.getNode(Integer.parseInt(node));
      for (Entry<String, ArrayList<Integer>> entry : rootDatas.entrySet()) {
         ArrayList<Integer> val = entry.getValue();
         for (Integer i : val) {
            int sn=getNewkey(cloud,i);            
            if (sn>0) {
               Node sourceNode = cloud.getNode(sn);
               RelationUtil.createRelation(sourceNode, desNode, "creationrel");
               RelationUtil.createRelation(desNode, sourceNode, "contentrel");
            }
         }
      }
   }
   
}
