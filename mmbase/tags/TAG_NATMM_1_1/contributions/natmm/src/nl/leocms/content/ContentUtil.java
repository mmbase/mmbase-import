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

import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Various utilities for ContentElements
 * 
 */
public class ContentUtil {

   /** MMbase logging system */
   private static Logger log = Logging.getLoggerInstance(ContentUtil.class.getName());

   private Cloud cloud;
   
   /**
    * @param mmbase
    */
   public ContentUtil(Cloud cloud) {
      super();
      this.cloud = cloud;
   }
   
   
   public void addSchrijver(Node content) {
      addSchrijver(content, cloud.getUser().getIdentifier());
   }

   public void addSchrijver(Node content, String username) {
      log.debug("user " + username);
      AuthorizationHelper auth = new AuthorizationHelper(cloud);
      Node user = auth.getUserNode(username);
      RelationManager schrijver = cloud.getRelationManager("contentelement", "users", "schrijver");
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
      Node rubriek = cloud.getNode(rubrieknr);
      RelationManager creatierubriek = cloud.getRelationManager("contentelement", "rubriek", "creatierubriek");
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
      RubriekHelper rubriekHelper = new RubriekHelper(cloud);
      List list = rubriekHelper.getPathToRoot( cloud.getNode(creatierubriek));
      if (list.size() >= 3) {
         Node hoofdrubriek = (Node) list.get(2);
         log.debug("Hoofdrubriek "+hoofdrubriek);
         RelationManager man = cloud.getRelationManager("contentelement","rubriek","hoofdrubriek");
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
      RubriekHelper rubriekHelper = new RubriekHelper(cloud);
      List list = rubriekHelper.getPathToRoot( cloud.getNode(creatierubriek));
      if (!list.isEmpty()) {
         Node subsiteRubriek = null;
         RelationManager subsite = cloud.getRelationManager("contentelement","rubriek","subsite");
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

	/**
	 * pools are grouped in topics
	 * each topic is related to rubriek
    * for all objects in a rubriek each pool an object is related to, should also be related to the topic of this rubriek
    * pre-condition: each rubriek has at most one related topic
	 *
    * @param content - content element
    * @param creatierubriek - creatierubriek.
    */
   public void updateTopics(Node content, String creatierubriek) {
		log.debug("trying to update topics");
      Node rubriek = cloud.getNode(creatierubriek);
		NodeList nlTopics = rubriek.getRelatedNodes("topics","related",null);
		Node rubriekTopic = null;
		if(nlTopics.size()==0) {
		   rubriekTopic = cloud.getNodeManager("topics").createNode();
		   rubriekTopic.setStringValue("title", rubriek.getStringValue("naam"));
		   rubriekTopic.commit();
		   rubriek.createRelation(rubriekTopic,cloud.getRelationManager("related")).commit();
		   log.debug("created new topic and related it to rubriek " + rubriek.getStringValue("naam"));
		}
		if(nlTopics.size()>0) {
		   rubriekTopic = nlTopics.getNode(0);
		}
		NodeList nlPools = content.getRelatedNodes("pools","posrel",null);
		int nlPoolsSize = nlPools.size();
		for(int i=0; i < nlPoolsSize; i++) {
			Node pool = nlPools.getNode(i);
			pool = makeUnique(pool,"name"); // for doing anything, make sure this pool is unique
		   log.debug("checking pool " + pool.getNumber());
			nlTopics = cloud.getList(
								pool.getStringValue("number"),
								"pools,posrel,topics",
								"topics.number",
								"topics.number = '" + rubriekTopic.getNumber() + "'",
								null,null,null,false
							);
			if(nlTopics.size()==0) {
				log.debug("create relation between pool " + pool.getNumber() + " and topic " + rubriekTopic.getNumber());
			   pool.createRelation(rubriekTopic,cloud.getRelationManager("posrel")).commit();
			}
		}
   }
   
  /**
    * check if this node is unique on field (case-insensitive)
    * if it is the case then merge the node with its sibling
	 *
    * @param node node to be checked
    * @param field field to should be a unique key (case-insensitive)
    */
   public Node makeUnique(Node node, String field) {
      String sConstraint = "UPPER(" + field + ") = '" + node.getStringValue(field).toUpperCase() + "' AND number!='" + node.getNumber() + "'";
      log.debug(sConstraint);
      NodeList nl = node.getNodeManager().getList(sConstraint,null,null);
      for (NodeIterator it = nl.nodeIterator(); it.hasNext();) {
         Node sibling = it.nextNode();
         log.debug("nodes " + node.getNumber() + " and " + sibling.getNumber() + " have identical " + field + " " + node.getStringValue(field) );
         if(sibling!=null && sibling.getNumber()!=node.getNumber()) {
            node = mergeNodes(node,sibling);
         }
      }
      return node;
   }
   
  /**
    * merges all relations from origin to target 
    * and then deletes the origin
	 *
    * @param origin
    * @param target
    */
   public Node mergeNodes(Node origin, Node target) {
      log.debug("merging " + origin.getNumber() + " into " + target.getNumber());
      RelationList rl = origin.getRelations();
      for (RelationIterator it = rl.relationIterator(); it.hasNext();) {
         Relation rel = it.nextRelation();
         Node rel_source = rel.getSource();
         Node rel_destination = rel.getDestination();
         if(rel_source.getNumber()==origin.getNumber()) {
            // origin is source, create a relation with the destination of the relation
            createRelation(target,rel_destination,rel.getRelationManager());
         } else {
            // origin is target, create a relation with the source of the relation
            createRelation(rel_source,target,rel.getRelationManager());
         }
         rel.delete(true);
      }
      try { 
         origin.delete();
      } catch (Exception e) {
         log.error("node " + origin.getNumber() + " still contains relations, so it can not be deleted");
      }
      return target;
   }

  /**
    * create relation, but only if it does not already exist
	 *
    * @param origin
    * @param target
    */
   public void createRelation(Node source, Node destination, RelationManager rm) {
      if(!hasRelation(source,destination,rm)) {
         source.createRelation(destination,rm).commit();
      }
   }

  /**
    * returns true if there exists a relation from source to destination of type rm 
	 *
    * @param source
    * @param destination
    * @param rm
    */
   public boolean hasRelation(Node source, Node destination, RelationManager rm) {
      
      NodeList nl = source.getRelatedNodes(destination.getNodeManager(),rm.getName(),null);
      int dNumber = destination.getNumber();
      for (NodeIterator it = nl.nodeIterator(); it.hasNext();) {
         if(it.nextNode().getNumber()==dNumber) {
           log.debug("the relation between " + source.getNumber() + " and " + dNumber + " exists");
           return true;
         }
      }
      return false;
   }
   
}

