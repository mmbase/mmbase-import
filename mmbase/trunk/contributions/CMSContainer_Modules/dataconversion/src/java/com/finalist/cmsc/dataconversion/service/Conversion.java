package com.finalist.cmsc.dataconversion.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.sql.DataSource;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.finalist.cmsc.dataconversion.dataaccess.DataAccessDelegate;
import com.finalist.cmsc.dataconversion.dataaccess.DataHolder;
import com.finalist.cmsc.dataconversion.dataaccess.DataSourceFactory;

/**
 * the type used to convert
 * @author kevin
 *
 */

public class Conversion {

   private static final Logger log = Logging.getLoggerInstance(Conversion.class.getName());
   
   private static final String IMPORT_FILE = "/WEB-INF/dataconversion/import.xml"; 
   private Properties properties;       
   private DataSource dataSource;   
   private String node;
   private ServletContext context;
   
   private String table_log = "[Import data from table %s]";
   private String sefRelation_log = "[Import self relation of table %s]";
   private String relation_log = " [Import  relation from %s to %s][relation type:%s]";
   private String result_count = " [Total :%s  ][ table %s]";
   private String result_rel_count = " [Total :%s  ][ table %s]";   
  
   public Conversion( ) {

   }
   public Conversion(Properties properties, ServletContext context) {
      this.properties = properties;
      this.context = context;
      if(properties.getProperty("node") != null) {
         node = properties.getProperty("node");
      }
      this.dataSource = DataSourceFactory.getDataSource(properties);
   }
   
   /**
    * the main method used to converse all data from older db 
    */
   public void converseAll() {
      Document dom = null; 
      long beginTime = System.currentTimeMillis();
      try {
    	  
    	  
         log.info(" ###############################  begin  import ######################");
         dom = XMLParser.parseXMLToDOM(context.getResourceAsStream(IMPORT_FILE));       
        // XMLParser.DTDValidator(dom);
         Element element = (Element)dom.getElementsByTagName("import").item(0);
         NodeList list = element.getChildNodes();         
         List<Data> sources =  new ArrayList<Data>();
         for(int i = 0 ; i < list.getLength(); i++) {
            //datatype node
            if(list.item(i).getNodeName().equals("datatype")) {
               //deal with data type node
               log.info(String.format(table_log,XMLUtil.getSourceType((Element)list.item(i))));
               Data sorData = getDataOfDataType((Element)list.item(i));
               //load data number from 
               HashMap<Integer,Integer> keys = DataAccessDelegate.getNumbersOfDataType((Element)list.item(i), dataSource);
               log.info(String.format(result_count,keys.size(),XMLUtil.getSourceType((Element)list.item(i))));
               sorData.setIdentifiers(keys);
               sources.add(sorData);

               if(XMLUtil.hasSelfRelation((Element)list.item(i))) {
                  log.info(String.format(sefRelation_log,XMLUtil.getSourceType((Element)list.item(i))));
                  Data relData = getDataOfSelfRelation((Element)list.item(i));
                  HashMap<Integer,Integer> keys1 = DataAccessDelegate.getNumbersOfSelfRelation((Element)list.item(i), dataSource);
                  log.info(String.format(result_count,keys1.size(),XMLUtil.getSourceType((Element)list.item(i))));
                  if (keys1 == null || keys1.isEmpty()) {
                     continue;
                  }
                  relData.setIdentifiers(keys1);
                  sources.add(relData);
               }                              
               Data rootData = getRootData((Element)list.item(i));
               HashMap<Integer,Integer> keysd = DataAccessDelegate.addChildRelation((Element)list.item(i), dataSource);
               log.info(String.format(sefRelation_log,keysd.size()));
               rootData.setIdentifiers(keysd);
               rootData.setRelateId(Integer.parseInt(node));
               
              // sources.add(rootData);  
               log.info("----> before recur");
               recur((Element)list.item(i),sources);
               log.info("----> after recur");
               for(Data data:sources) {
                  if(data.getType() == Constants.ENTITY_TYPE) {
                     log.info(String.format(" ----> begin to save type [%s]",data.getDestinationType()));
                  }
                  else if(data.getType() == Constants.RELATION_TYPE) {
                     log.info(String.format(" ----> begin to save type [%s]",data.getDestinationRelationType()));
                  }
                  else if(data.getType() == Constants.SELF_RELATION_TYPE) {
                     log.info(String.format(" ----> begin to save type [%s]",data.getSourceRelationType()));
                  }
                  Iterator<Integer> iterator =  data.getIdentifiers().keySet().iterator();
                  while(iterator.hasNext()) {
                     Integer key = iterator.next();
                     Element elment = XMLUtil.getElementByTableName(data.getTableName(),(Element)list.item(i));
                     DataHolder holder = DataAccessDelegate.getElementByPrimaryKey(elment, dataSource, key,data.getType());
                     Integer number = NodeService.insertData(holder,data,sources,key);                   
                     data.getIdentifiers().put(key, number);
                  }
                  log.info("----> end saving ");
               }  
               System.out.println("finished.........");
               NodeService.insertData(null,rootData,sources,null);
            } 
         }
         log.info(String.format(" ---->#######################finished importing   [time:%s mins] #############################",(System.currentTimeMillis()-beginTime)/(1000*60)));
         NodeService.insertProperties(properties);
      } 
      catch (Exception e) {
         log.error(e.getMessage());
         e.printStackTrace();
      }   
   }      
  /**
   * recur to get elements related 
   * @param element dom element 
   * @param sources a collection object which hold Data Objects  
   * @throws Exception
   */
   private void recur(Element element,List<Data> sources) throws Exception {
      
      Element[] relates = XMLUtil.getDirectRelateChildNodes(element);
      if(relates.length < 1) {
         return ;
      }

      for(int i = 0 ; i < relates.length ; i++) {
         log.info("###################### begin Import table ["+XMLUtil.getSourceType(relates[i])+"]   ####################");
         log.info(String.format(table_log,XMLUtil.getSourceType(relates[i])));
         Data sorData = getDataOfDataType(relates[i]);
         HashMap<Integer,Integer> keys = DataAccessDelegate.getNumbersOfDataType(relates[i], dataSource);
         log.info(String.format(result_count,keys.size(),XMLUtil.getSourceType(relates[i])));                     
         if (keys == null || keys.isEmpty()) {
            continue;
         }
         sorData.setIdentifiers(keys);
         sources.add(sorData);               
         log.info(String.format(relation_log,XMLUtil.getSourceType(element),XMLUtil.getSourceType(relates[i]),XMLUtil.getSourceRelationType(relates[i])));
         Data relData = getDataOfRelation(relates[i]);
         HashMap<Integer,Integer> keys1 = DataAccessDelegate.getNumbersOfRelation(relates[i], dataSource);
         log.info(String.format(result_rel_count,keys1.size(),XMLUtil.getSourceRelationType(relates[i])));
         if (keys1  != null && !keys1.isEmpty()) {
            relData.setIdentifiers(keys1);
            sources.add(relData);
         }
         log.info("###################### end Import table ["+XMLUtil.getSourceType(relates[i])+"]  ####################");
         recur(relates[i], sources);
      }
   }
   
   private void setData(Data data,Element element) {
      data.setTableName(XMLUtil.getSourceType(element));
      data.setDestinationType(XMLUtil.getDestinationType(element)); 
      data.setRelationType(XMLUtil.getRelateType(element));
      data.setRelateTable(XMLUtil.getRelateSourceType(element));
   }
   private Data getDataOfDataType(Element element) {
      Data sorData = new Data(Constants.ENTITY_TYPE);              
      setData(sorData,element);
      sorData.setDestinationRelationType(XMLUtil.getDestinationRelationType(element));
      return sorData;
   }
   
   private Data getDataOfSelfRelation(Element element) {
      Data relData = new Data(Constants.SELF_RELATION_TYPE);
      setData(relData,element);
      relData.setDestinationRelationType(XMLUtil.getSelfRelDesRelationType(element));
      relData.setSourceRelationType(XMLUtil.getSelfRelSourceRelationType(element));
      return relData;
   }
   
   private Data getDataOfRelation(Element element) {
      Data relData = new Data(Constants.RELATION_TYPE);  
      setData(relData,element);
      relData.setDestinationRelationType(XMLUtil.getDestinationRelationType(element));
      return relData;
   }
   private Data getRootData(Element element) {
      Data rootData = new Data(Constants.ROOT_CATEGORY_TYPE);
      setData(rootData,element);
      rootData.setDestinationRelationType(XMLUtil.getSelfRelDesRelationType(element));
      return rootData;
   }
   
   public static void main(String[] args) throws Exception {
      
      Properties properties = new Properties();
//      properties.setProperty("driverClassName", "com.mysql.jdbc.Driver");
//      properties.setProperty("url", "jdbc:mysql://localhost:3306/finalist");
//      properties.setProperty("maxActive", "10");
//      properties.setProperty("maxWait", "500");
//      properties.setProperty("username", "root");
//      properties.setProperty("password", "root");
//      properties.setProperty("defaultAutoCommit", "true");
//      properties.setProperty("defaultReadOnly", "false");
//      properties.setProperty("defaultTransactionIsolation", "READ_COMMITTED");
//      properties.setProperty("defaultCatalog", "test");
//      properties.setProperty("validationQuery", "SELECT DUMMY FROM DUAL");
      
      properties.setProperty("driverClassName", "org.postgresql.Driver");
      properties.setProperty("url", "jdbc:postgresql://192.168.1.230:5432/roa");
      properties.setProperty("maxActive", "10");
      properties.setProperty("maxWait", "500");
      properties.setProperty("username", "root");
      properties.setProperty("password", "root");
   
 // FP: Disabled to get it working with servlet context
 //     Conversion conversion = new Conversion(properties);
 //     conversion.converseAll();
   }
}
