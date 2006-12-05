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
 * The Initial Developer of the Original Code is 'Media Competence'
 *
 * See license.txt in the root of the LeoCMS directory for the full license.
 */
package nl.leocms.connectors.UISconnector.shared.properties.process;

import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;
import nl.leocms.connectors.UISconnector.shared.properties.model.*;


public class PropertyUtil
{

   private static final Logger log = Logging.getLoggerInstance(PropertyUtil.class);

   public static void setProperties(Cloud cloud, Node contentNode, List listProperties) throws Exception {

      // Delete relation of the contentNode to pools
      // We don't matter about deleting obsolete pools and topics, this will be done by a seperate process
      if (contentNode != null) {
         String contentType = contentNode.getNodeManager().getName();
         NodeList nl = cloud.getList("",
                                     contentType + ",posrel,pools",
                                     "posrel.number",
                                     contentType + ".number='" + contentNode.getNumber() + "'",
                                     null, null, null, true);
         int nlSize = nl.size();
         for (int f = 0; f < nlSize; f++) {
            Node relation = cloud.getNode(nl.getNode(f).getStringValue("posrel.number"));
            relation.delete(true);
         }
      }

      // Add pools to contentNode
      for (Iterator it = listProperties.iterator(); it.hasNext(); ) {
         Property property = (Property) it.next();

         NodeManager nmTopics = cloud.getNodeManager("topics");

         // Match topics on externid
         Node nodeTopic = null;
         NodeList nlTopics = nmTopics.getList("externid='" + property.getPropertyId() + "'", null, null);
         if (nlTopics.size() > 0) {
            nodeTopic = nlTopics.getNode(0);

         }
         else {
            log.info("creating topic " + property.getPropertyId());
            nodeTopic = nmTopics.createNode();
            nodeTopic.setStringValue("externid", property.getPropertyId());
         }
         nodeTopic.setStringValue("title", property.getPropertyDescription());
         nodeTopic.commit();

         for (Iterator it2 = property.getPropertyValues().iterator(); it2.hasNext(); ) {
            PropertyValue propertyValue = (PropertyValue) it2.next();

            NodeManager nmPools = cloud.getNodeManager("pools");
            // Match topics on externid
            Node nodePool = null;
            NodeList nlPools = nmPools.getList("externid='" + propertyValue.getPropertyValueId() + "'", null, null);
            if (nlPools.size() > 0) {
               nodePool = nlPools.getNode(0);

            }
            else {
               log.info("creating pool " + propertyValue.getPropertyValueId());
               nodePool = nmPools.createNode();
               nodePool.setStringValue("externid", propertyValue.getPropertyValueId());
            }
            nodePool.setStringValue("name", propertyValue.getPropertyValueDescription());
            nodePool.commit();

            // Relate pool to contentNode
            if (contentNode != null) {
               contentNode.createRelation(nodePool, cloud.getRelationManager("posrel")).commit();
            }
            // Relate pool to topic if necessary
            NodeList nl2 = cloud.getList(nodeTopic.getStringValue("number"),
                                         "topics,posrel,pools",
                                         "pools.number",
                                         "pools.number='" + nodePool.getStringValue("number") + "'",
                                         null, null, null, true);
            if (nl2.size() == 0) {
               nodeTopic.createRelation(nodePool, cloud.getRelationManager("posrel")).commit();
            }
         }
      }
   }
}


