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
    Returns a list of nodes that are related to this node
  */
   public NodeList usedInItems(String sNodeNumber){

      NodeList nlUsedInItems = null;

      Node node = cloud.getNode(sNodeNumber);
      String otype = node.getStringValue("otype");
      String thisType = (String) getNameWithOtype(otype);
            
      ApplicationHelper ap = new ApplicationHelper(cloud);
      
      String paginaNumber = ap.getDefaultPage(thisType);
      if(paginaNumber!=null) {
         nlUsedInItems = cloud.getNodeManager("pagina").getList("number='" + paginaNumber + "'",null,null);
      }
      
      TreeMap tmPathToRubriek = new TreeMap();
      if(ap.isInstalled("NatMM")) {
         tmPathToRubriek.put("images", "contentrel");
         tmPathToRubriek.put("panno", "posrel");
         tmPathToRubriek.put("shorty", "rolerel");
         tmPathToRubriek.put("teaser", "rolerel");
      }
      
      ArrayList cTypes = ap.getContentTypes();
      for(int ct=0; ct < cTypes.size(); ct++) {
         String relatedType = ( (String) cTypes.get(ct)).toLowerCase();
         // subitems do not count for used in items
         if (   ! (thisType.equals("artikel")  && relatedType.equals("images"))
             && ! (thisType.equals("artikel")  && relatedType.equals("paragraaf"))
             && ! (thisType.equals("vacature") && relatedType.equals("attachments"))
             && ! (thisType.equals("evenement")&& relatedType.equals("evenement")) ) {
            NodeManager thisTypeNodeManager = cloud.getNodeManager(thisType);
            if (thisTypeNodeManager.getAllowedRelations(relatedType, null, null).size() > 0) {
               NodeList nl = null;
               if (relatedType.equals("rubriek")&&ap.isInstalled("NatMM")){ 
                  //add exception for objects related to rubriek
                  log.info("trying to find relations between " + thisType + " " + sNodeNumber + " and rubriek");
                  if (tmPathToRubriek.containsKey(thisType)){
                     nl = node.getRelatedNodes(relatedType, (String) tmPathToRubriek.get(thisType),null);
                  }
               } else {
                  nl = node.getRelatedNodes(relatedType);
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
      }
      return nlUsedInItems;
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

   /*
   * Add the default relations to a contentelement, if they are not present
   * First check if this type of contentelements has a default page
   * If not check the possible paths to find its page
   * If both options fail use the archive page to add the default relations
   */
   public void addDefaultRelations(String sContentElement, HashMap pathsFromPageToElements) {

      String sType = cloud.getNode(sContentElement).getNodeManager().getName();   
      if(!bRelationsExists(sContentElement)&&!sType.equals("paragraaf")) {
      
         boolean hasDefaultRelations = false;
         String sPaginaNumber = ap.getDefaultPage(sType);
           
         if(sPaginaNumber!=null) {
            log.info(sType + " " + sContentElement + " is related to pagina " + sPaginaNumber);
            hasDefaultRelations = createRelationToPage(sContentElement,sPaginaNumber);
         } 
         for (Iterator it=pathsFromPageToElements.keySet().iterator();it.hasNext() && !hasDefaultRelations; ) {
            String objecttype = (String) it.next();
            if (objecttype.equals(sType)) {
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
      NodeList nl = cloud.getList(objectNumber,pathFromPageToElements,
         "pagina.number",null,null,null,null,true);
      if (nl.size()>0){
         String sPaginaNumber = nl.getNode(0).getStringValue("pagina.number");
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
      
      if (breadcrumbs != null && breadcrumbs.size()>2) {
        
         // breadcrumbs should have at least size 3: [creatierubriek,...,subsite,root]
         createRelation(objectNumber,"creatierubriek",(String) breadcrumbs.get(0));
         createRelation(objectNumber,"hoofdrubriek",(String) breadcrumbs.get(breadcrumbs.size()-3));
         createRelation(objectNumber,"subsite",(String) breadcrumbs.get(breadcrumbs.size()-2));
         hasDefaultRelations = true;
         
      } else {
         
         // there is a relation to a page, however the page has no valid breadcrumbs
         log.error(sPaginaNumber + " is not connected to root, all objects from breadcrumbs " + breadcrumbs + " will be removed");
         for(int i=0; i<breadcrumbs.size(); i++) {
            cloud.getNode((String) breadcrumbs.get(i)).delete(true);
         }
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
            log.info("there is no user with account " + nElement.getStringValue("owner") + " in this base");
         }
         if (nUser!=null) {
            // create relation contentelement-schrijver-user 
            (cloud.getNode(objectNumber)).createRelation(nUser,cloud.getRelationManager("schrijver")).commit();
            String otype = nElement.getStringValue("otype");
            log.info("added " + nUser.getStringValue("account") + " as schrijver to " + getNameWithOtype(otype) + " " + objectNumber);
         }
      }
   }

}
