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

import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;
import com.finalist.mmbase.util.CloudFactory;


/**
 * Util class to put nodes in a publish queue.
 * @author  Ronald Kramp
 * @version PublishUtil.java,v 1.2 2003/07/28 09:44:16 nico Exp
 */
public class PublishUtil {
   private static Logger log = Logging.getLoggerInstance(PublishUtil.class.getName());
   private static boolean publish = false;

   /**
    * add a node to the publish queue
    * @param objectNodenodeno the node to put in the publish queue
    **/
   public static void PublishOrUpdateNode(MMObjectNode objectNodenode) {
      int number = objectNodenode.getIntValue("number");
      PublishOrUpdateNode(number);
   }

   /**
    * add a node to the publish queue
    * @param node the node to put in the publish queue
    **/
   public static void PublishOrUpdateNode(Node node) {
      int number = node.getNumber();
      PublishOrUpdateNode(number);
   }

   /**
    * add a node to the publish queue
    * @param number the number of the node to put in the publish queue
    **/
   public static void PublishOrUpdateNode(int number) {
      log.debug("PublishOrUpdateNode with number = " + number);
      Cloud cloud = CloudFactory.getCloud();
      NodeManager nodeManager = cloud.getNodeManager("publishqueue");
      Node node = nodeManager.createNode();
      node.setIntValue("sourcenumber", number);
      node.setStringValue("action", "update");
      node.commit();
   }

   /**
    * add the node to the queue to remove all published instances of a node
    * @param objectNodenode the node to be removed
    **/
   public static void removeNode(MMObjectNode objectNodenode) {
      int number = objectNodenode.getIntValue("number");
      removeNode(number);
   }

   /**
    * add the node to the queue to remove all published instances of a node
    * @param number the number of the node to be removed
    **/
   public static void removeNode(int number) {
      log.debug("removeNode with number = " + number);
      Cloud cloud = CloudFactory.getCloud();
      NodeManager nodeManager = cloud.getNodeManager("publishqueue");
      Node node = nodeManager.createNode();
      node.setIntValue("sourcenumber", number);
      node.setStringValue("action", "remove");
      node.commit();
   }

}
