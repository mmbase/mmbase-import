package com.finalist.cmsc.mmbase;

import java.util.*;

import org.apache.commons.lang.StringUtils;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.Field;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeIterator;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.module.core.MMBase;
import org.mmbase.storage.search.FieldCompareConstraint;
import org.mmbase.storage.search.FieldValueConstraint;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This class facilitates the use of nodes for properties.
 * 
 * @author Nico Klasens
 */
public class PropertiesUtil {

   /** MMbase logging system */
   private static final Logger log = Logging.getLoggerInstance(PropertiesUtil.class.getName());

   private final static String DEFAULT = "value";
   private final static String DEV = "dev";
   private final static String TEST = "test";
   private final static String SIT = "preprod";
   private final static String PROD = "prod";

   /** Environment server is running in (dev,test,sit,prod) */
   private static String environment = DEFAULT;

   private static boolean warnOnce = true;


   /**
    * Returns the value of the field <CODE>field</CODE> of the node whose id
    * equals <CODE>key</CODE>.
    * 
    * @param key
    *           The node-id of the properties node to be retrieved.
    * @return The value of the properties node.
    */
   public static String getProperty(String key) {
      return getProperty(key, CloudProviderFactory.getCloudProvider().getCloud());
   }


   /**
    * Returns the value of the field <CODE>field</CODE> of the node whose id
    * equals <CODE>key</CODE>.
    * 
    * @param key
    *           The node-id of the properties node to be retrieved.
    * @param cloud
    *           cloud to read property from.
    * @return The value of the properties node.
    */
   public static String getProperty(String key, Cloud cloud) {
      if (DEFAULT.equals(environment)) {
         setEnvironment(cloud);
         log.debug("Environment " + environment);
      }
      return getProp(key, cloud);
   }
   
   /**
    * Returns the list of values of the field <CODE>field</CODE> of the node whose id
    * equals <CODE>key</CODE>.
    * 
    * @param key
    *           The node-id of the properties node to be retrieved.
    * @return The list of values of the properties node.
    */
   public static List<String> getPropertyAsList(String key) {
      return getPropertyAsList(key, CloudProviderFactory.getCloudProvider().getCloud());
   }
   
   /**
    * Returns the list of values of the field <CODE>field</CODE> of the node whose id
    * equals <CODE>key</CODE>.
    * 
    * @param key
    *           The node-id of the properties node to be retrieved.
    * @param cloud
    *           cloud to read property from.
    * @return The list of values of the properties node.
    */
   public static List<String> getPropertyAsList(String key, Cloud cloud) {
      if (DEFAULT.equals(environment)) {
         setEnvironment(cloud);
         log.debug("Environment " + environment);
      }
      String prop = getProp(key, cloud);
      return convertToList(prop);
   }

   private static List<String> convertToList(String prop) {
      List<String> list = new ArrayList<String>();
      StringTokenizer tokenizer = new StringTokenizer(prop, ", \t\n\r\f");
      while (tokenizer.hasMoreTokens()) {
         String str = tokenizer.nextToken();
         list.add(str);
      }
      return list;
   }



   private static void setEnvironment(Cloud cloud) {
      String propertyKey = "mmservers";
      Node mmservers = getPropertyNodes(cloud, propertyKey);
      if (mmservers != null) {
         String machineName = MMBase.getMMBase().getMachineName();
         if (isServerInEnv(machineName, mmservers.getStringValue(PROD))) {
            environment = PROD;
            return;
         }
         if (isServerInEnv(machineName, mmservers.getStringValue(SIT))) {
            environment = SIT;
            return;
         }
         if (isServerInEnv(machineName, mmservers.getStringValue(TEST))) {
            environment = TEST;
            return;
         }
         if (isServerInEnv(machineName, mmservers.getStringValue(DEV))) {
            environment = DEV;
            return;
         }
         if (warnOnce) {
            log.warn("Server " + machineName + " not in Property 'mmservers'. Using default value");
            warnOnce = false;
         }
      }
      else {
         if (warnOnce) {
            log.warn("Property 'mmservers' missing. Using default value");
            warnOnce = false;
         }
      }
   }


   private static Node getPropertyNodes(Cloud cloud, String propertyKey) {
      NodeManager propertiesManager = cloud.getNodeManager("properties");
      NodeQuery query = propertiesManager.createQuery();
      Field keyField = propertiesManager.getField("key");
      FieldValueConstraint constraint = query.createConstraint((query.getStepField(keyField)),
            FieldCompareConstraint.EQUAL, propertyKey);
      query.setConstraint(constraint);

      NodeList list = propertiesManager.getList(query);
      if (list.size() > 0) {
         return list.getNode(0);
      }
      return null;
   }


   private static boolean isServerInEnv(String machineName, String servers) {
      String[] serversArray = servers.split(",");
      for (String element : serversArray) {
         if (element != null && machineName.equals(element.trim())) {
            return true;
         }
      }
      return false;
   }


   private static String getProp(String key, Cloud cloud) {
      Node property = getPropertyNodes(cloud, key);

      String result = "";
      if (property != null) {
         result = property.getStringValue(environment);
         if (!DEFAULT.equals(environment) && StringUtils.isEmpty(result)) {
            log.warn("Property '" + key + "' empty in environment " + environment + ". Using default value");
            result = property.getStringValue(DEFAULT);
         }
      }
      log.debug("Property=" + key + ", value=" + result);
      return result;
   }


   public static void setProperty(String key, String value) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      setProperty(cloud, key, value);
   }


   public static void setProperty(Cloud cloud, String key, String value) {
      if (DEFAULT.equals(environment)) {
         setEnvironment(cloud);
         log.info("Environment " + environment);
      }
      setProp(cloud, key, value);
   }


   public static void setProp(Cloud cloud, String key, String value) {
      NodeManager propertiesManager = cloud.getNodeManager("properties");
      Node property = getPropertyNodes(cloud, key);
      if (property == null) {
         property = propertiesManager.createNode();
         property.setValue("key", key);
      }

      property.setValue(environment, value);
      property.commit();
      log.info("Changed Property " + key + "in environment " + environment + " value=" + value);
   }


   public static Map<String, String> getModuleProperties(String module) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      Map<String, String> result = new TreeMap<String, String>();
      NodeManager propertiesManager = cloud.getNodeManager("properties");
      NodeQuery query = propertiesManager.createQuery();
      Field keyField = propertiesManager.getField("module");
      FieldValueConstraint constraint = query.createConstraint((query.getStepField(keyField)),
            FieldCompareConstraint.EQUAL, module);
      query.setConstraint(constraint);

      NodeList list = propertiesManager.getList(query);
      for (NodeIterator ni = list.nodeIterator(); ni.hasNext();) {
         Node node = ni.nextNode();
         result.put(node.getStringValue("key"), node.getStringValue(environment));
      }

      return result;
   }

}
