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
   private AuthorizationHelper ah;
   private ApplicationHelper ap;

   /**
    * @param cloud
    */
   public ContentHelper(Cloud cloud) {
      super();
      this.cloud = cloud;
      this.ah = new AuthorizationHelper(cloud);
      this.ap = new ApplicationHelper(cloud);
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
    Returns a list of ApplicationHelper.getContentTypes that are parents of this node
  */
   public NodeList usedInItems(String sNodeNumber){

      NodeList nlUsedInItems = null;

      Node node = cloud.getNode(sNodeNumber);
      String otype = node.getStringValue("otype");
      String thisType = getNameWithOtype(otype);
            
      ApplicationHelper ap = new ApplicationHelper(cloud);
      
      String paginaNumber = ap.getDefaultPage(sNodeNumber, thisType);
      if(paginaNumber!=null) {
         nlUsedInItems = cloud.getNodeManager("pagina").getList("number='" + paginaNumber + "'",null,null);
      }
      
      ArrayList cTypes = ap.getContentTypes(true);
      cTypes.add("rubriek");
      
      TreeMap tmPathToRubriek = new TreeMap();
      tmPathToRubriek.put("pagina", "posrel");
      if(ap.isInstalled("NatMM")) {
         tmPathToRubriek.put("images", "contentrel");
         tmPathToRubriek.put("panno", "posrel");
         tmPathToRubriek.put("shorty", "rolerel");
         tmPathToRubriek.put("teaser", "rolerel");
      }
      
      for(int ct=0; ct < cTypes.size(); ct++) {
        String parentType = (String) cTypes.get(ct);
        // subitems do not count for used in items, lets assumet that subitems are always connected with direction "destination"
        NodeManager thisTypeNodeManager = cloud.getNodeManager(thisType);
        if (thisTypeNodeManager.getAllowedRelations(parentType, null, "source").size() > 0) {
          NodeList nl = null;
          if (parentType.equals("rubriek")){ 
            if (tmPathToRubriek.containsKey(thisType)) {
               log.debug("checking if there exists a relation between " + thisType + " " + sNodeNumber + " and rubriek");
               nl = node.getRelatedNodes(parentType, (String) tmPathToRubriek.get(thisType),"source");
            }
          } else {
            nl = node.getRelatedNodes(parentType,null,"source");
          }
          if (nl != null && nl.size() > 0) {
            if (nlUsedInItems==null) {
               nlUsedInItems = nl;
            }
            else {
               nlUsedInItems.addAll(nl);
            }
          }
        }
      }
      return nlUsedInItems;
   }

  /*
    Returns ArrayList with unused contentelements, related to this account
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
            NodeList nlElements = cloud.getList(rubriek.getStringValue("number"),
              "rubriek,creatierubriek,contentelement","contentelement.number",null,null,null,null,false);
            for (int j = 0; j < nlElements.size(); j++){
               if (usedInItems(nlElements.getNode(j).getStringValue("contentelement.number"))==null){
                  alUnusedItems.add(nlElements.getNode(j).getStringValue("contentelement.number"));
               }
            }
         }
      }
      return alUnusedItems;
   }

   /*
   * Add the default relations to a contentelement, if they are not present
   * First check if this type of contentelements has a default page
   * If not check the possible paths to find its page
   * If both options fail use the archive page to add the default relations
   */
   public void addDefaultRelations(String sContentElement, HashMap pathsFromPageToElements, ArrayList containerTypes) {

      String sType = cloud.getNode(sContentElement).getNodeManager().getName();
      // container types are not used in /editors/beheerbibliotheek/ and do not have to have default relations
      if(!bRelationsExists(sContentElement) && !containerTypes.contains(sType)) {
      
         boolean hasDefaultRelations = false;
         String sPaginaNumber = ap.getDefaultPage(sContentElement,sType);
           
         if(sPaginaNumber!=null) {
            log.debug(sType + " " + sContentElement + " is related to pagina " + sPaginaNumber);
            hasDefaultRelations = createRelationToPage(sContentElement,sPaginaNumber);
         } 
         for (Iterator it=pathsFromPageToElements.keySet().iterator();it.hasNext() && !hasDefaultRelations; ) {
            String objecttype = (String) it.next();
            if ((objecttype.replaceAll("#","")).equals(sType)) {
              String path = (String) pathsFromPageToElements.get(objecttype);
              hasDefaultRelations = addDefaultRelationByPath(sContentElement, path);
            }
         }
         if(!hasDefaultRelations) {
            createRelationToPage(sContentElement,cloud.getNode("archief").getStringValue("number")); 
         }
      }
   }

   /* Try to add the default relations for a contentelement on basis of a path
   */
   public boolean addDefaultRelationByPath(String objectNumber, String pathFromPageToElements) {
      
      boolean hasDefaultRelations = false;        
      // finding page related to the contentelement
      NodeList nl = cloud.getList(objectNumber,pathFromPageToElements,"pagina.number",null,null,null,null,false);
      if (nl.size()>0){
         String sPaginaNumber = nl.getNode(0).getStringValue("pagina.number");
         log.debug(cloud.getNode(objectNumber).getNodeManager().getName() + objectNumber 
            + " is related to pagina " + sPaginaNumber + " via " + pathFromPageToElements);
         hasDefaultRelations = createRelationToPage(objectNumber, sPaginaNumber);
      }
      return hasDefaultRelations;
   }


   /* Use sPaginaNumber to add the default relations to objectNumber
   */
   public boolean createRelationToPage(String objectNumber, String sPaginaNumber) {

      boolean hasDefaultRelations = false;

      // finding breadcrumbs
      Vector breadcrumbs = null;
      if (sPaginaNumber!=null){
         breadcrumbs = PaginaHelper.getBreadCrumbs(cloud,sPaginaNumber);
         log.debug("page " + sPaginaNumber + " has breadcrumbs " + breadcrumbs);
      }
      
      if (breadcrumbs != null && breadcrumbs.size()>1) {
        
         // breadcrumbs should have at least size 2: [creatierubriek,...,subsite,root]
         createRelation(objectNumber,"creatierubriek",(String) breadcrumbs.get(0));
         int hoofdRubriekLevel = (breadcrumbs.size()==2 ?  2 : 3);
         createRelation(objectNumber,"hoofdrubriek",(String) breadcrumbs.get(breadcrumbs.size()-hoofdRubriekLevel));
         createRelation(objectNumber,"subsite",(String) breadcrumbs.get(breadcrumbs.size()-2));
         hasDefaultRelations = true;
         
      } else {
         
         // there is a relation to a page, however the page has no valid breadcrumbs
         log.error(sPaginaNumber + " is not connected to root, pagina " + sPaginaNumber 
            + " and all objects from breadcrumbs " + breadcrumbs + " will be removed");
         for(int i=0; i<breadcrumbs.size(); i++) {
            cloud.getNode((String) breadcrumbs.get(i)).delete(true);
         }
         cloud.getNode(sPaginaNumber).delete(true);
      }
      return hasDefaultRelations;
   }

   void createRelation(String objectNumber, String role, String rubriekNumber) {
      if(!relationExists(objectNumber, role)) {
         Node nRubriek = cloud.getNode(rubriekNumber);
         Node nElement = cloud.getNode(objectNumber);
         nRubriek.createRelation(nElement, cloud.getRelationManager(role)).commit();
         log.debug("added " + role + " relation between " + nElement.getNumber() + " and rubriek " + rubriekNumber);
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
      Node nElement = cloud.getNode(objectNumber);
      NodeList nl = nElement.getRelatedNodes("users","schrijver",null);
      if (nl.size()==0) {
         // try to find user with: contentelement.owner=users.account
         Node nUser = null;
         try {
            nUser = ah.getUserNode(nElement.getStringValue("owner")); 
         } catch (Exception e) {
            log.debug("there is no user with account " + nElement.getStringValue("owner") + " in this base");
         }
         if (nUser!=null) {
            // create relation contentelement-schrijver-user 
            (cloud.getNode(objectNumber)).createRelation(nUser,cloud.getRelationManager("schrijver")).commit();
            String otype = nElement.getStringValue("otype");
            log.debug("added " + nUser.getStringValue("account") + " as schrijver to " + getNameWithOtype(otype) + " " + objectNumber);
         }
      }
   }

}
