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

import java.util.*;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.Relation;
import org.mmbase.bridge.RelationIterator;
import org.mmbase.bridge.RelationList;
import org.mmbase.bridge.RelationManager;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @author Gerard van de Weerd
 * Nov 5, 2003
 *
 * This class contains useful (!) methods to retrieve information from the MMBase cloud.
 *
 */
public class MMBaseHelper {
   /** Logger. */
   private static Logger log = Logging.getLoggerInstance(MMBaseHelper.class.getName());

   private Cloud cloud = null;
      /**
       *
       */
   public MMBaseHelper(Cloud cloud) {
      this.cloud = cloud;
   }

   /**
    * Returns the number of the first (!) relation (of the given rel type)
    * between the source node (identified by the given source number) and
    * the destination node (identified by the given destination number)
    * Or -1 if no relation is defined.
    * @param contentrelNumber
    * @return
    */
   public int getFirstRelationNumber(String source, String destination, String relType) {


      Node sourceNode = cloud.getNode(source);
      Node destinationNode = cloud.getNode(destination);
      RelationManager relTypeRelMan = cloud.getRelationManager(relType);
      RelationList rels = relTypeRelMan.getRelations(destinationNode);
      RelationIterator relsIter = rels.relationIterator();
      if (relsIter.hasNext()) {
         // max of 1
         return relsIter.nextRelation().getNumber();
      }
      return -1;
   }

   /**
    * private helper that returns the pos relation at the given pos
    * @param rels the relation list to search through
    * @param pos the position
    * @return the relation at pos or null if nonexistant
    */
   public Node getPosRelAtPosition(RelationList rels, int pos) {
      RelationIterator relsIter = rels.relationIterator();
      Relation tr;
      while (relsIter.hasNext()) {
         tr = relsIter.nextRelation();
         if (pos == tr.getIntValue("pos")) {
            return tr.getDestination();
         }
      }
      return null;
   }

   /**
       * Verander de volgorde van een pos rel
       * @param parentNode - Node van de parent
       * @param childs - String met childnodenumbers bv. "170,173,178"
       */
      public void changeOrder(Node parentNode, String childs) {
         StringTokenizer tokenizer = new StringTokenizer(childs, ",");
         List tokens = new ArrayList();
         while (tokenizer.hasMoreTokens()) {
            tokens.add(tokenizer.nextToken());
         }
         RelationList list = parentNode.getRelations("posrel");
         RelationIterator iter = list.relationIterator();
         while (iter.hasNext()) {
            Relation rel = iter.nextRelation();
            int destination = rel.getDestination().getNumber();
            rel.setIntValue("pos", tokens.indexOf("" + destination));
            rel.commit();
            NodeManager manager = cloud.getNodeManager("remotenodes");
            NodeList nodeList = manager.getList("sourcenumber = " + destination, null, null);
            if ((nodeList != null) && (nodeList.size() > 0)) {
               PublishUtil.PublishOrUpdateNode(cloud.getNode(rel.getNumber()));
            }
         }
      }

      public void addDefaultRelations() {
          log.info("addDefaultRelations()");
          ContentHelper ch = new ContentHelper(cloud);
          ApplicationHelper ap = new ApplicationHelper(cloud);
          
          HashMap pathsFromPageToElements = ap.pathsFromPageToElements();
          ArrayList containerTypes = ap.getContainerTypes();
          
          NodeList nl = cloud.getList("","contentelement","contentelement.number",
            null,"contentelement.number","up",null,true);
          int nlSize = nl.size();
          for (int i = 0; i < nlSize; i++) {
             if(i%(nlSize/100)==0) {
                log.info("checked " + i + " of " + nlSize + " contentelements (= " + i/(nlSize/100) + "%)");
             }
             String sContentElement = nl.getNode(i).getStringValue("contentelement.number");
             ch.addDefaultRelations(sContentElement, pathsFromPageToElements, containerTypes);
             ch.addSchrijver(sContentElement);
          }
      }
      
}
