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
package nl.leocms.pagina;

import java.util.*;

import nl.leocms.util.PublishUtil;
import nl.leocms.util.PaginaHelper;
import nl.leocms.content.ContentUtil;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.Relation;
import org.mmbase.bridge.RelationList;
import org.mmbase.bridge.RelationManager;
import org.mmbase.bridge.RelationIterator;

/**
 * @author Gerard van de Weerd
 * Date :Oct 20, 2003
 *
 */
public class PaginaUtil {

   /** Logger instance. */
   private static Logger log = Logging.getLoggerInstance(PaginaUtil.class);

   Cloud cloud;
   /**
    * @param cloud
    */
   public PaginaUtil(Cloud cloud) {
      this.cloud = cloud;
   }

   public Node createPagina(Node parent, String username) {
      NodeManager manager = cloud.getNodeManager("pagina");
      Node newPagina = manager.createNode();
      newPagina.commit();
      RelationManager relManager = cloud.getRelationManager("rubriek","pagina","posrel");
      Relation relation = relManager.createRelation( parent, newPagina);

      ContentUtil contentUtil = new ContentUtil(cloud);
      contentUtil.addSchrijver(newPagina, username);

      RelationList existing = parent.getRelations("posrel","pagina");
      int pos = -1;
      for (int i = 0; i < existing.size(); i++) {
         pos = Math.max(existing.getRelation(i).getIntValue("pos"), pos);
      }
      relation.setIntValue("pos", pos + 1);
      relation.commit();
      return newPagina;
   }

   /**
    * Verander de volgorde van pagina's
    * @param parentNode - Node van de parent
    * @param childs - String with childnodenumbers eg. "170,173,178"
    */
   public void changeOrder(Node parentNode, String childs) {

      StringTokenizer tokenizer = new StringTokenizer(childs, ",");
      log.debug("childs = " + childs);
      List tokens = new ArrayList();
      while (tokenizer.hasMoreTokens()) {
         tokens.add(tokenizer.nextToken());
      }
      // *** save the order of the pages
      RelationList cList = parentNode.getRelations("posrel","pagina");
      RelationIterator iter = cList.relationIterator();
      while (iter.hasNext()) {
         Relation rel = iter.nextRelation();
         int destination = rel.getDestination().getNumber();
         if(tokens.indexOf("" + destination)!=-1) {
            rel.setIntValue("pos", tokens.indexOf("" + destination));
            rel.commit();
         } else {
            log.error("Could not find pagina " + destination + " in list of childs " + childs);
         }
     /* hh    NodeManager manager = cloud.getNodeManager("remotenodes");
        NodeList nodeList = manager.getList("sourcenumber = " + destination, null, null);
        if ((nodeList != null) && (nodeList.size() > 0)) {
           PublishUtil.PublishOrUpdateNode(cloud.getNode(rel.getNumber()));
        }
     */
      }
      // *** save the order of the rubrieken related to this parent (if any)
      cList = parentNode.getRelations("parent",cloud.getNodeManager("rubriek"),"DESTINATION");
      iter = cList.relationIterator();
      while (iter.hasNext()) {
         Relation rel = iter.nextRelation();
         int destination = rel.getDestination().getNumber();
         if(tokens.indexOf("" + destination)!=-1) {
            rel.setIntValue("pos", tokens.indexOf("" + destination));
            rel.commit();
         } else {
            log.error("Could not find rubriek " + destination + " in list of childs " + childs);
         }
      }
   }


   /**
    * Returns all nodes that are related to the page in a HashSet
    * @param page Node
    * @return HashSet
    */
   public HashSet doesPageContainContentElements(Node page) {
      
      PaginaHelper ph = new PaginaHelper(cloud);
      HashSet hsetResult = new HashSet();
      for (Iterator it=ph.pathsFromPageToElements.keySet().iterator();it.hasNext();) {
         String objecttype = (String) it.next();
         if(objecttype.indexOf("#")==-1) {
            String currentPath = (String) ph.pathsFromPageToElements.get(objecttype);
            currentPath = currentPath.replaceAll("object",objecttype) + ",gebruikt,template";
            NodeList objects = cloud.getList(null,
                                 currentPath,
                                 objecttype + ".number",
                                 "pagina.number='" + page.getNumber() + "'",
                                 null, null, null, false);
            for(int c = 0; c< objects.size(); c++) {
               Node object = cloud.getNode(objects.getNode(c).getStringValue(objecttype + ".number"));
               hsetResult.add(object);
            }
         }
      }
      return hsetResult;
    }


    /**
    * Removes the page
    *
    * @param paginaNodeNumber
    */
   public void removePagina(String paginaNodeNumber) {
      Node paginaNode = cloud.getNode(paginaNodeNumber);
      paginaNode.deleteRelations();
      paginaNode.delete(true);
   /* hh   PublishUtil.removeNode( paginaNode.getNumber());
   */
   }
}
