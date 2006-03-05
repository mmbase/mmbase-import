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
package nl.leocms.content;

import java.util.List;

import nl.leocms.authorization.AuthorizationHelper;
import nl.leocms.util.ContentTypeHelper;
import nl.leocms.util.RubriekHelper;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeIterator;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.RelationManager;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Various utilities for ContentElements
 * 
 */
public class ContentUtil {

   /** MMbase logging system */
   private static Logger log = Logging.getLoggerInstance(ContentUtil.class.getName());

   private Cloud mmbase;
   
   /**
    * @param mmbase
    */
   public ContentUtil(Cloud mmbase) {
      super();
      this.mmbase = mmbase;
   }
   
   
   public void addSchrijver(Node content) {
      addSchrijver(content, mmbase.getUser().getIdentifier());
   }

   public void addSchrijver(Node content, String username) {
      log.debug("user " + username);
      AuthorizationHelper auth = new AuthorizationHelper(mmbase);
      Node user = auth.getUserNode(username);
      RelationManager schrijver = mmbase.getRelationManager("contentelement", "users", "schrijver");
      content.deleteRelations("schrijver");
      content.createRelation(user, schrijver).commit();
   }

   /**
    * Create the relation to the creatierubriek.
    * 
    * @param content  - Node contentelement
    * @param rubrieknr - String rubrieknumber (nodenumber)
    */
   public void addCreatieRubriek(Node content, String rubrieknr) {
      log.debug("Creatierubriek " + rubrieknr);
      Node rubriek = mmbase.getNode(rubrieknr);
      RelationManager creatierubriek = mmbase.getRelationManager("contentelement", "rubriek", "creatierubriek");
      content.createRelation(rubriek, creatierubriek).commit();
   }
   
   /**
    * Create a relation to the 'hoofdrubriek' of a content item.
    * 
    * Bv. creatierubriek huren: path = root - Leeuwarden - Wonen - huren<BR>
    * Hoofdrubriek = Wonen<BR><BR>
    * 
    * If the path is too short (eg. root - Leeuwarden), there is NO hoofdrubriek.
    * 
    * @param content - content element
    * @param creatierubriek - creatierubriek.
    */
   public void addHoofdRubriek(Node content, String creatierubriek) {
      RubriekHelper rubriekHelper = new RubriekHelper(this.mmbase);
      List list = rubriekHelper.getPathToRoot( mmbase.getNode(creatierubriek));
      if (list.size() >= 3) {
         Node hoofdrubriek = (Node) list.get(2);
         log.debug("Hoofdrubriek "+hoofdrubriek);
         RelationManager man = mmbase.getRelationManager("contentelement","rubriek","hoofdrubriek");
         content.createRelation(hoofdrubriek,man).commit();
      }
   }

   /**
    * Create a relation to the 'subsite' of a content item.
    * 
    * Bv. creatierubriek huren: path = root - Leeuwarden - Wonen - huren<BR>
    * subsite = Leeuwarden<BR><BR>
    * 
    * If the path is too short (eg. root), then root is the subsite.
    * 
    * @param content - content element
    * @param creatierubriek - creatierubriek.
    */
   public void addSubsite(Node content, String creatierubriek) {
      RubriekHelper rubriekHelper = new RubriekHelper(this.mmbase);
      List list = rubriekHelper.getPathToRoot( mmbase.getNode(creatierubriek));
      if (!list.isEmpty()) {
         Node subsiteRubriek = null;
         RelationManager subsite = mmbase.getRelationManager("contentelement","rubriek","subsite");
         if (list.size() >= 2) {
            subsiteRubriek = (Node) list.get(1);
         }
         else {
            subsiteRubriek = (Node) list.get(0);
         }
         log.debug("subsite "+subsiteRubriek);
         content.createRelation(subsiteRubriek,subsite).commit();
      }
   }
   
   /**
    * Check if a contentnode has a creatierubriek
    *  
    * @param content - Content Node 
    * @return true if the node has a related workflowitem
    */
   public boolean hasCreatieRubriek(Node content) {
      NodeList list = null;
      if (ContentTypeHelper.PAGINA.equals(content.getNodeManager().getName())) {
         list = content.getRelatedNodes("rubriek", "posrel", "SOURCE");         
      }
      else {
         list = content.getRelatedNodes("rubriek", "creatierubriek", "DESTINATION");
      }
      return !list.isEmpty();
   }
   
   public Node getCreatieRubriek(Node content) {
      NodeList list = null;
      if (ContentTypeHelper.PAGINA.equals(content.getNodeManager().getName())) {
         list = content.getRelatedNodes("rubriek", "posrel", "SOURCE");         
      }
      else {
         list = content.getRelatedNodes("rubriek", "creatierubriek", "DESTINATION");
      }
      return list.getNode(0);
   }

   /**
    * Check if a contentnode has a schrijver
    *  
    * @param content - Content Node 
    * @return true if the node has a related workflowitem
    */
   public boolean hasSchrijver(Node content) {
      NodeList list = content.getRelatedNodes("users", "schrijver", "DESTINATION");
      return !list.isEmpty();
   }
   
   public Node getSchrijver(Node content) {
      return content.getRelatedNodes("users", "schrijver", "DESTINATION").getNode(0);
   }

}
