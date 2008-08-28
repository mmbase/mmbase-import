package com.finalist.cmsc.mmbase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.sf.mmapps.commons.util.StringUtil;
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

   private static final String PROPERTY_HIDDEN_TYPES = "system.contenttypes.hide";

   /** MMbase logging system */
   private static Logger log = Logging.getLoggerInstance(PropertiesUtil.class.getName());

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
         if (!DEFAULT.equals(environment) && StringUtil.isEmpty(result)) {
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


   /**
    * Helper method to get all hidden types
    * 
    * @return
    */
   public static List<String> getHiddenTypes() {
      String property = getProperty(PROPERTY_HIDDEN_TYPES);
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
