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
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import java.util.ArrayList;

public class PoolUtil {

   private static Logger log = Logging.getLoggerInstance(PoolUtil.class.getName());

   public static NodeList getPool(Cloud cloud, String sNodeNumber){
      NodeList nPools = null;
      try {
        Node n = cloud.getNode(sNodeNumber);
        nPools = n.getRelatedNodes("pools", "posrel", null);
      } catch (Exception e) {
        log.error("Node " + sNodeNumber + " does not exist, maybe it was deleted.");
      }
      return nPools;
   }

   /*
   * true if sPoolNumber is one of the pools related to sNodeNumber
   */
   public static boolean containsPool(Cloud cloud, String sNodeNumber, String sPoolNumber) {
     NodeList nPools = getPool(cloud, sNodeNumber);
     boolean containsPool = (nPools != null) && nPools.contains(cloud.getNode(sPoolNumber));
     log.debug(sNodeNumber + " is related" + (containsPool?"":" not") + " to pool " + sPoolNumber); 
     return containsPool; 
   }

   /* Adds the pools related to source that match poolsConstraint also to destination
   */
   public static void addPools(Cloud cloud, String sSource, String sDestination, String poolsConstraint) {

      NodeList nlPools = cloud.getList(
                           sSource,
                           "object,posrel,pools",
                           "pools.number",
                           poolsConstraint,
                           null, null, null, true);

      for(int p = 0; p<nlPools.size(); p++) {

         String sPool = nlPools.getNode(p).getStringValue("pools.number");
         NodeList nl = cloud.getList(
                           sPool,
                           "pools,posrel,object",
                           "posrel.number,object.number",
                           "object.number = '"  + sDestination + "'",
                           null, null, null, true);
         if(nl.size()==0) {
            log.info("Node" + sDestination + " is not related to pool " + sPool + ". Creating relation now.");
            Node pool = cloud.getNode(sPool);
            Node destination = cloud.getNode(sDestination);
            RelationManager posrelRelMan = cloud.getRelationManager("posrel");
            Relation posrel = posrelRelMan.createRelation(pool,destination);
            posrel.setIntValue("pos",1);
            posrel.commit();
          } else {
            log.info("Node" + sDestination + " already related to pool " + sPool + ". Adding one to clickcount.");
            Node posrel = cloud.getNode(nl.getNode(0).getStringValue("posrel.number"));
            int clickCount = posrel.getIntValue("pos");
            posrel.setIntValue("pos",clickCount+1);
            posrel.commit();
          }

      }
   }

   public static NodeList getByPools(Cloud cloud, String sSource, String sPath, String sNodeType,
                                     String sFields, String sOrderby, String sMemberId) {

      ArrayList priorityNumbers = new ArrayList();

      NodeList nlPriority = cloud.getList(
                            sSource,
                            sPath + "," + sNodeType + ",posrel,pools,posrel,deelnemers",
                            sNodeType + ".number"+sFields,
                            "deelnemers.number=" + sMemberId,
                            sOrderby, null, null, true);

      log.debug("Finding priority nodes by cloud.getList(" +
                            sSource + "," +
                            sPath + "," + sNodeType + ",posrel,pools,posrel,deelnemers" + "," +
                            sNodeType + ".number"+sFields + "," +
                            "deelnemers.number=" + sMemberId + "," +
                            sOrderby + ")");

      NodeList nlAll = cloud.getList(
                       sSource,
                       sPath + "," + sNodeType,
                       sNodeType + ".number"+sFields,
                       null,
                       sOrderby, null, "destination", true);

      for(int p = 0; p<nlPriority.size(); p++) {
         priorityNumbers.add(nlPriority.getNode(p).getStringValue(sNodeType + ".number"));
         log.debug("adding priority node " + nlPriority.getNode(p).getStringValue(sNodeType + ".number") );
      }

      for(int p = 0; p<nlAll.size(); p++) {
         Node node = nlAll.getNode(p);
         if (!priorityNumbers.contains(node.getStringValue(sNodeType + ".number"))) {
            nlPriority.add(node);
         }
      }
      return nlPriority;
   }
}
