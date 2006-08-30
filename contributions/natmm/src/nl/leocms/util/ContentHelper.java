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
import java.util.*;
import nl.leocms.util.*;
import nl.leocms.authorization.AuthorizationHelper;
import nl.leocms.authorization.UserRole;
import nl.leocms.authorization.Roles;
import nl.leocms.applications.*;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Various utilities for ContentElements
 *
 */
public class ContentHelper {

   /** MMbase logging system */
   private static Logger log = Logging.getLoggerInstance(ContentHelper.class.getName());

   private Cloud cloud;

   /**
    * @param cloud
    */
   public ContentHelper(Cloud cloud) {
      super();
      this.cloud = cloud;
   }

   public String getNameWithOtype(String otype) {
      String nodes = "typedef";
      String where = "number='"+otype+"'";
      String showField = "typedef.name";
      NodeList list = cloud.getList("", nodes, showField, where, "", null, null, true );
      NodeIterator it = list.nodeIterator();
      if (it!=null) {
         while (it.hasNext()) {
            Node node = it.nextNode();
            return node.getStringValue("typedef.name");
         }
      }
      return "onbekend type";
   }

   public String getOtypeWithName(String name) {
      String nodes = "typedef";
      String where = "name='"+name+"'";
      String showField = "typedef.number";
      NodeList list = cloud.getList("", nodes, showField, where, "", null, null, true );
      NodeIterator it = list.nodeIterator();
      if (it!=null) {
         while (it.hasNext()) {
            Node node = it.nextNode();
            return node.getStringValue("typedef.number");
         }
      }
      return "onbekend type";
   }

   public String getTitleField(NodeManager objectmanager) {
      String titleField = null;
      String [] titleFields = { "titel", "naam", "title", "name", "filename" };
      for(int f=0; f<titleFields.length && titleField==null; f++) {
         if(objectmanager.hasField(titleFields[f])) {
           titleField = titleFields[f];
         }
      }
      if(titleField==null) {
         log.error("No title field has been found for objecttype " + objectmanager.getName());
      }
      return titleField;
   }

   public String getTitleField(String otype) {
      return getTitleField(cloud.getNodeManager(otype));
   }

   public String getTitleField(Node object) {
      return getTitleField(object.getNodeManager());
   }

	/*
	* return a comma seperated list of owners of this contentelement
	*/
	public String getOwners(String objectNumber) {
		StringBuffer sbOwners = new StringBuffer();
		NodeList owners = cloud.getList(objectNumber, "object,rolerel,users", "users.number", null, "rolerel.pos", "UP", null, true );
		for(int u=0; u<owners.size(); u++) {
			if(u>0) {
				sbOwners.append(',');
			}
			sbOwners.append(owners.getNode(u).getStringValue("users.number"));
      }
      return sbOwners.toString();
	}

  /*
    Returns a list of nodes that are related to this node
  */
   public NodeList usedInItems(String sNodeNumber){
      Node node = cloud.getNode(sNodeNumber);
      String otype = node.getStringValue("otype");
      String thisType = (String) getNameWithOtype(otype);
      ArrayList cTypes = ContentTypeHelper.getContentTypes();
      cTypes.add("dossier");
      ApplicationHelper ap = new ApplicationHelper();
      TreeMap tmPathToRubriek = new TreeMap();
      if(ap.isInstalled(cloud,"NatMM")) {
         tmPathToRubriek.put("images", "contentrel");
         tmPathToRubriek.put("panno", "posrel");
         tmPathToRubriek.put("shorty", "rolerel");
         tmPathToRubriek.put("teaser", "rolerel");
      }
      NodeList nlUsedItems = null;
      boolean isList = false;
      for(int ct=0; ct < cTypes.size(); ct++) {
         String relatedType = ( (String) cTypes.get(ct)).toLowerCase();
         if (! (thisType.equals("artikel") &&
                (relatedType.equals("paragraaf") || relatedType.equals("images")))
             &&
             ! (thisType.equals("evenement") && relatedType.equals("evenement"))) {
            NodeManager thisTypeNodeManager = cloud.getNodeManager(thisType);
            if (thisTypeNodeManager.getAllowedRelations(relatedType, null, null).size() > 0) {
               NodeList nl = null;
               if (relatedType.equals("rubriek")&&ap.isInstalled(cloud,"NatMM")){ //add exception to the rubriek
                  log.info("trying to find relations between " + thisType + " " +
                  sNodeNumber + " and rubriek");
                  if (tmPathToRubriek.containsKey(thisType)){
                     nl = cloud.getList(sNodeNumber, thisType + "," +
                     tmPathToRubriek.get(thisType) + ",rubriek",thisType + ".number",
                     null, null, null, null, true);
                  }
               } else {
                  nl = node.getRelatedNodes(relatedType);
               }
               if (nl != null && nl.size() > 0) {
                  if (!isList) {
                     nlUsedItems = nl;
                     isList = true;
                  }
                  else {
                     nlUsedItems.addAll(nl);
                  }
               }

            }
         }
      }
      return nlUsedItems;
   }

  /*
    Returns ArrayList with contentelements used by user account
	 See also: UpdateUnusedElements.getUnusedItems()
  */
   public ArrayList getUnusedItems(String account){
      ArrayList alUnusedItems = new ArrayList();
      NodeList nlRubrieks = cloud.getNodeManager("rubriek").getList(null,null,null);
      for(int i = 0; i < nlRubrieks.size(); i++){
			Node rubriek = nlRubrieks.getNode(i);
         AuthorizationHelper authHelper = new AuthorizationHelper(cloud);
         UserRole userRole = authHelper.getRoleForUser(authHelper.getUserNode(account), rubriek);
         if (userRole.getRol() >= Roles.SCHRIJVER) {
            NodeList nlElements = cloud.getList(rubriek.getStringValue("number"),"rubriek,creatierubriek,contentelement","contentelement.number",null,null,null,null,false);
            for (int j = 0; j < nlElements.size(); j++){
               if (usedInItems(nlElements.getNode(j).getStringValue("contentelement.number"))==null){
                  alUnusedItems.add(nlElements.getNode(j).getStringValue("contentelement.number"));
               }
            }
         }
      }
      return alUnusedItems;
   }

   public void addDefaultRelations(String objectNumber, String pathFromPageToElements) {

      if(!bRelationsExists(objectNumber)) {
                        
         Node nElement = cloud.getNode(objectNumber);
         String sPaginaNumber = null;
         Vector breadcrumbs = null;
         String archiveParent = null;
         
         // finding page related to the contentelement
         if (pathFromPageToElements.equals("evenementen")){
            sPaginaNumber = cloud.getNodeByAlias("agenda").getStringValue("number");
         }
         else {
            NodeList nl = cloud.getList(objectNumber,pathFromPageToElements,
               "pagina.number",null,null,null,null,true);
            if (nl.size()>0){
               sPaginaNumber = nl.getNode(0).getStringValue("pagina.number");
            }
         }
         // finding breadcrumbs
         if (sPaginaNumber!=null){
            breadcrumbs = PaginaHelper.getBreadCrumbs(cloud,sPaginaNumber);
            log.info("page " + sPaginaNumber + " has breadcrumbs " + breadcrumbs);
         } else {
            String otype = nElement.getStringValue("otype");
            log.info(getNameWithOtype(otype) + " " + objectNumber + " has no relation to a page");
         }
         // finding parent of archive         
         NodeList nParent = cloud.getNode("archive").getRelatedNodes("rubriek","parent","SOURCE");
         if (nParent.size() > 0) {
            archiveParent = nParent.getNode(0).getStringValue("number");
         } else {
            log.error("pagina archive does not have a parent rubriek");
         }
         
         if (breadcrumbs != null && breadcrumbs.size()>3) {
            // breadcrumbs should have at least size 3: [creatierubriek,...,subsite,root]
            
            createRelation(objectNumber,nElement,"creatierubriek",(String) breadcrumbs.get(0));
            createRelation(objectNumber,nElement,"hoofdrubriek",(String) breadcrumbs.get(breadcrumbs.size()-3));
            createRelation(objectNumber,nElement,"subsite",(String) breadcrumbs.get(breadcrumbs.size()-2));                        
         }
         else {

            // use rubriek with alias "archive" as creatierubriek and hoofdrubriek and the parent of archive as subsite
            createRelation(objectNumber,nElement,"creatierubriek","archive");
            createRelation(objectNumber,nElement,"hoofdrubriek","archive");
            createRelation(objectNumber,nElement,"subsite",archiveParent);
         }
      }
   }

   void createRelation(String objectNumber, Node nElement, String role, String rubriekNumber) {
      if(!relationExists(objectNumber, role)) {
         Node nRubriek = cloud.getNode(rubriekNumber);
         nRubriek.createRelation(nElement, cloud.getRelationManager(role)).commit();
         log.info("added " + role + " relation between " + objectNumber + " and rubriek " + rubriekNumber);
      }
   }

   boolean relationExists(String objectNumber, String role) {
      NodeList nl = cloud.getList(objectNumber,"contentelement," + role + ",rubriek",
         "contentelement.number",null,null,null,null,true);
      if (nl.size()==0){
         return false;
      } else {
         log.debug("relation contentelement," + role + ",rubriek for node " + objectNumber + " already exists");
         return true;
      }
   }                

   boolean bRelationsExists(String objectNumber) {
      return relationExists(objectNumber,"creatierubriek")
         && relationExists(objectNumber,"hoofdrubriek")
         && relationExists(objectNumber,"subsite");
   }

   public void addSchrijver(String objectNumber) {
      NodeList nl = cloud.getList(objectNumber,"contentelement,schrijver,users",
      "users.number",null,null,null,null,true);
      if (nl.size()==0) {
         // try to find user with: contentelement.owner=users.account
         nl = cloud.getList("","contentelement,schrijver,users",
            "users.number","contentelement.owner = users.account",null,null,null,true);
         if (nl.size()>0) {
            //create relation contentelement-schrijver-user
            Node nUser = cloud.getNode(nl.getNode(0).getStringValue("users.number"));
            (cloud.getNode(objectNumber)).createRelation(nUser,cloud.getRelationManager("schrijver")).commit();
         }
      }
   }

}
