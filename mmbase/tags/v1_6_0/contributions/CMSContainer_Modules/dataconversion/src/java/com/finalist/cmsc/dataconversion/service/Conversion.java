package com.finalist.cmsc.dataconversion.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.sql.DataSource;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.w3c.dom.*;

import com.finalist.cmsc.dataconversion.dataaccess.DataAccessDelegate;
import com.finalist.cmsc.dataconversion.dataaccess.DataAccessor;
import com.finalist.cmsc.dataconversion.dataaccess.DataHolder;
import com.finalist.cmsc.dataconversion.dataaccess.DataSourceFactory;

/**
 * the type used to convert
 * 
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

   public Conversion() {
      // nothing
   }

   public Conversion(Properties properties, ServletContext context) {
      this.properties = properties;
      this.context = context;
      if (properties.getProperty("node") != null) {
         node = properties.getProperty("node");
      }
      this.dataSource = DataSourceFactory.getDataSource(properties);
   }

   /**
    * the main method used to converse all data from older db
    */
   public void convertAll() {
      Document dom = null;
      long beginTime = System.currentTimeMillis();
      try {
         log.info(" ###############################  begin  import ######################");
         dom = XMLParser.parseXMLToDOM(context.getResourceAsStream(IMPORT_FILE));
         // XMLParser.DTDValidator(dom);
         Element element = (Element) dom.getElementsByTagName("import").item(0);
         String encoding = element.getAttribute("encoding");
         DataAccessor.encoding = encoding;

         NodeList list = element.getChildNodes();

         List<Data> clondSources = new ArrayList<Data>();
         for (int i = 0; i < list.getLength(); i++) {
            // datatype node
            Node itemDatatype = list.item(i);
            List<Data> sources = processDatatype(itemDatatype);
            clondSources.addAll(sources);
         }
         NodeService.insertMigrationMappings(clondSources);
         createRelationDataType(clondSources);
         linkRoot(list);
         log.info(String.format(
               " ---->#######################finished importing   [time:%s mins] #############################",
               (System.currentTimeMillis() - beginTime) / (1000 * 60)));
         NodeService.insertProperties(properties);
      }
      catch (Exception e) {
         log.error(e.getMessage());
         e.printStackTrace();
      }
   }

   private List<Data> processDatatype(Node itemDatatype) throws Exception {
      List<Data> sources = new ArrayList<Data>();
      if (itemDatatype.getNodeName().equals("datatype")) {
         // deal with data type node
         log.info(String.format(table_log, XMLUtil.getSourceType((Element) itemDatatype)));
         Data sorData = getDataOfDataType((Element) itemDatatype);
         // load data number from
         HashMap<Integer, Integer> keys = DataAccessDelegate.getNumbersOfDataType(
               (Element) itemDatatype, dataSource);
         log.info(String.format(result_count, keys.size(), XMLUtil
               .getSourceType((Element) itemDatatype)));
         sorData.setIdentifiers(keys);
         sources.add(sorData);

         if (XMLUtil.hasSelfRelation((Element) itemDatatype)) {
            log.info(String.format(sefRelation_log, XMLUtil.getSourceType((Element) itemDatatype)));
            HashMap<Integer, Integer> keys1 = DataAccessDelegate.getNumbersOfSelfRelation(
                  (Element) itemDatatype, dataSource);
            log.info(String.format(result_count, keys1.size(), XMLUtil
                  .getSourceType((Element) itemDatatype)));
            if (keys1 == null || keys1.isEmpty()) {
               return sources;
            }
            Data relData = getDataOfSelfRelation((Element) itemDatatype);
            relData.setIdentifiers(keys1);
            sources.add(relData);
         }

         log.info("----> before recur");
         recur((Element) itemDatatype, sources);
         log.info("----> after recur");
         relDatatype((Element) itemDatatype, sources);
         saveType(itemDatatype, sources);
         log.info("entity type and it relation finished.........");

         Data rootData = getRootData((Element) itemDatatype);
         HashMap<Integer, Integer> keysd = DataAccessDelegate.addChildRelation(
               (Element) itemDatatype, dataSource);
         log.info(String.format(sefRelation_log, keysd.size()));
         rootData.setIdentifiers(keysd);
         rootData.setRelateId(Integer.parseInt(node));
         NodeService.insertData(null, rootData, sources, null);
      }
      // end if
      return sources;
   }

   private void linkRoot(NodeList list) {
      Map<String, ArrayList<Integer>> rootDatas = new HashMap<String, ArrayList<Integer>>();
      for (int i = 0; i < list.getLength(); i++) {
         if (list.item(i).getNodeName().equals("datatype")) {
            String destinationtype = XMLUtil.getDestinationType((Element) list.item(i));
            String sourcetype = XMLUtil.getSourceType((Element) list.item(i));
            DataAccessor da = new DataAccessor(dataSource);
            ArrayList<Integer> primerKeys = da.getPrimerKeyList(sourcetype);
            rootDatas.put(destinationtype, primerKeys);
         }
      }
      NodeService.linkRootDatas(rootDatas, node);
   }

   private void createRelationDataType(List<Data> sources) throws Exception {
      for (Data reldata : sources) {
         if (reldata.getType() == Constants.RELATION_DATA_TYPE) {
            List<String> reskeys = DataAccessDelegate.getResOfRelation(reldata, dataSource);
            NodeService.createRelationData(reskeys, reldata);
         }
      }
   }

   private void saveType(Node item, List<Data> sources) throws Exception {

      for (Data data : sources) {
         if (data.getType() != Constants.RELATION_DATA_TYPE) {
            if (data.getType() == Constants.ENTITY_TYPE) {
               log.info(String.format(" ----> begin to save type [%s]", data.getDestinationType()));
            }
            else
               if (data.getType() == Constants.RELATION_TYPE) {
                  log.info(String.format(" ----> begin to save type [%s]", data
                        .getDestinationRelationType()));
               }
               else
                  if (data.getType() == Constants.SELF_RELATION_TYPE) {
                     log.info(String.format(" ----> begin to save type [%s]", data
                           .getSourceRelationType()));
                  }
            Iterator<Integer> iterator = data.getIdentifiers().keySet().iterator();
            while (iterator.hasNext()) {
               Integer key = iterator.next();

               Element elment = XMLUtil.getElementByTableName(data.getTableName(), (Element) item);
               DataHolder holder = DataAccessDelegate.getElementByPrimaryKey(elment, dataSource,
                     key, data.getType());
               Integer number = NodeService.insertData(holder, data, sources, key);
               if (number != null) {
                  data.getIdentifiers().put(key, number);
               }
            }
            log.info("----> end saving ");
         }
      }
   }

   /**
    * recur to get elements related
    * 
    * @param element
    *           dom element
    * @param sources
    *           a collection object which hold Data Objects
    * @throws Exception
    */
   private void recur(Element element, List<Data> sources) throws Exception {

      Element[] relates = XMLUtil.getDirectRelateChildNodes(element);
      if (relates.length < 1) {
         return;
      }

      for (Element relatesData : relates) {
         log.info("###################### begin Import table ["
               + XMLUtil.getSourceType(relatesData) + "]   ####################");
         log.info(String.format(table_log, XMLUtil.getSourceType(relatesData)));

         HashMap<Integer, Integer> keys = DataAccessDelegate.getNumbersOfDataType(relatesData,
               dataSource);
         log.info(String.format(result_count, keys.size(), XMLUtil.getSourceType(relatesData)));
         if (keys == null || keys.isEmpty()) {
            continue;
         }
         Data sorData = getDataOfDataType(relatesData);
         sorData.setIdentifiers(keys);
         sources.add(sorData);

         log.info(String.format(relation_log, XMLUtil.getSourceType(element), XMLUtil
               .getSourceType(relatesData), XMLUtil.getSourceRelationType(relatesData)));

         HashMap<Integer, Integer> keys1 = DataAccessDelegate.getNumbersOfRelation(relatesData,
               dataSource);
         log.info(String.format(result_rel_count, keys1.size(), XMLUtil
               .getSourceRelationType(relatesData)));
         if (keys1 != null && !keys1.isEmpty()) {
            Data relData = getDataOfRelation(relatesData);
            relData.setIdentifiers(keys1);
            sources.add(relData);
         }
         log.info("###################### end Import table [" + XMLUtil.getSourceType(relatesData)
               + "]  ####################");
         recur(relatesData, sources);
      }
   }

   private void relDatatype(Element element, List<Data> sources) throws Exception {
      Element[] relates = XMLUtil.getRelateChildNodes(element);
      for (Element relate : relates) {
         log.info("###################### begin import relateddatatype Type ["
               + XMLUtil.getSourceType(relate) + "]   ####################");
         Data relData = getDataOfRelationType(relate);
         sources.add(relData);
         log.info("###################### end import relateddatatype Type ["
               + XMLUtil.getSourceType(relate) + "]  ####################");
      }
   }

   // add getDataOfRelationType method
   private Data getDataOfRelationType(Element element) {
      Data relData = new Data(Constants.RELATION_DATA_TYPE);
      setData(relData, element);
      String destion = XMLUtil.getDestinationRelationType(element);
      relData.setDestinationRelationType(destion);
      String sourceRelationType = XMLUtil.getSourceRelationType(element);
      relData.setSourceRelationType(sourceRelationType);
      String reverse = XMLUtil.getReverseRelationType(element);
      relData.setReverse(reverse);
      return relData;
   }

   private void setData(Data data, Element element) {
      data.setTableName(XMLUtil.getSourceType(element));
      data.setDestinationType(XMLUtil.getDestinationType(element));
      data.setRelationType(XMLUtil.getRelateType(element));
      data.setRelateTable(XMLUtil.getRelateSourceType(element));
   }

   private Data getDataOfDataType(Element element) {
      Data sorData = new Data(Constants.ENTITY_TYPE);
      setData(sorData, element);
      sorData.setDestinationRelationType(XMLUtil.getDestinationRelationType(element));
      return sorData;
   }

   private Data getDataOfSelfRelation(Element element) {
      Data relData = new Data(Constants.SELF_RELATION_TYPE);
      setData(relData, element);
      relData.setDestinationRelationType(XMLUtil.getSelfRelDesRelationType(element));
      relData.setSourceRelationType(XMLUtil.getSelfRelSourceRelationType(element));
      return relData;
   }

   private Data getDataOfRelation(Element element) {
      Data relData = new Data(Constants.RELATION_TYPE);
      setData(relData, element);
      relData.setDestinationRelationType(XMLUtil.getDestinationRelationType(element));
      // add #############
      String reverse = XMLUtil.getReverseRelationType(element);
      relData.setReverse(reverse);
      return relData;
   }

   private Data getRootData(Element element) {
      Data rootData = new Data(Constants.ROOT_CATEGORY_TYPE);
      setData(rootData, element);
      rootData.setDestinationRelationType(XMLUtil.getSelfRelDesRelationType(element));
      return rootData;
   }

   public static void main(String[] args) throws Exception {

      Properties properties = new Properties();
      // properties.setProperty("driverClassName", "com.mysql.jdbc.Driver");
      // properties.setProperty("url", "jdbc:mysql://localhost:3306/finalist");
      // properties.setProperty("maxActive", "10");
      // properties.setProperty("maxWait", "500");
      // properties.setProperty("username", "root");
      // properties.setProperty("password", "root");
      // properties.setProperty("defaultAutoCommit", "true");
      // properties.setProperty("defaultReadOnly", "false");
      // properties.setProperty("defaultTransactionIsolation", "READ_COMMITTED");
      // properties.setProperty("defaultCatalog", "test");
      // properties.setProperty("validationQuery", "SELECT DUMMY FROM DUAL");

      properties.setProperty("driverClassName", "org.postgresql.Driver");
      properties.setProperty("url", "jdbc:postgresql://192.168.1.230:5432/roa");
      properties.setProperty("maxActive", "10");
      properties.setProperty("maxWait", "500");
      properties.setProperty("username", "root");
      properties.setProperty("password", "root");

      // FP: Disabled to get it working with servlet context
      // Conversion conversion = new Conversion(properties);
      // conversion.converseAll();
   }
}
