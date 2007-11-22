/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is LeoCMS.
 *
 * The Initial Developer of the Original Code is
 * 'De Gemeente Leeuwarden' (The dutch municipality Leeuwarden).
 *
 * See license.txt in the root of the LeoCMS directory for the full license.
 */
package nl.leocms.util;

import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.Cloud;
import com.finalist.mmbase.util.CloudFactory;

/** 
 * This class facilitates the use of nodes from the back-end.
 * @author Ronald Kramp
 * @version $Revision: 1.1 $
 */
public class PropertiesUtil {
  
   /** 
    * Returns the value of the field <CODE>field</CODE> of the node whose id equals <CODE>key</CODE>.
    * @param key The node-id of the properties node to be retrieved.
    * @return The value of the properties node.
    */   
   public static String getProperty(String key) {
      Cloud cloud = CloudFactory.getCloud();
      NodeManager propertiesManager = cloud.getNodeManager("properties");
      NodeList list = propertiesManager.getList("[key]='" + key + "'", null, null);
      
      String result = "";
      
      if (list.size() > 0) {
         result = list.getNode(0).getStringValue("value");
      }
      return result;
   }
   
   public static void setProperty(String key, String value) {
      Cloud cloud = CloudFactory.getCloud();
      NodeManager propertiesManager = cloud.getNodeManager("properties");
      Node property = null;

      NodeList list = propertiesManager.getList("[key]='" + key + "'", null, null);
      if (list.isEmpty()) {
         property = propertiesManager.createNode();
         property.setValue("key", key);
      }
      else {
         property = list.getNode(0);
      }

      property.setValue("value", value);
      property.commit();
   }
}
