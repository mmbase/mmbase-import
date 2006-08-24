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
               NodeList nl = node.getRelatedNodes(relatedType);
               if (nl.size() > 0) {
                  if (!isList) {
                     nlUsedItems = nl;
                     isList = true;
                  } else {
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

   public void addDefaultRelations(String objectNumber, int iTypeIndex) {

      if(!bRelationsExists(objectNumber)) {
         //finding number of page related to the contentelement
         String sPaginaNumber = "";
         if (iTypeIndex == NatMMConfig.CONTENTELEMENTS.length){
            sPaginaNumber = cloud.getNodeByAlias("agenda").getStringValue("number");
         }
         else {
            NodeList nl = cloud.getList(objectNumber,NatMMConfig.PATHS_FROM_PAGE_TO_ELEMENTS[iTypeIndex],
            "pagina.number",null,null,null,null,true);
            if (nl.size()>0){
               sPaginaNumber = nl.getNode(0).getStringValue("pagina.number");
            }
         }
         Vector breadcrumbs = null;
         if (!sPaginaNumber.equals("")){
            //get breadcrumbs;
            breadcrumbs = PaginaHelper.getBreadCrumbs(cloud,sPaginaNumber);
         }

         String[] sRelTypes = {"creatierubriek", "hoofdrubriek", "subsite"};
         Node nElement = cloud.getNode(objectNumber);
         if (breadcrumbs != null) {
            if (breadcrumbs.size()==3){
               for (int i = 0; i < sRelTypes.length; i++) {
                  Relation thisRelation = null;
                  Node nRubriek = cloud.getNode( (String) breadcrumbs.get(i));
                  thisRelation = nRubriek.createRelation(nElement,
                  cloud.getRelationManager(sRelTypes[i]));
                  thisRelation.commit();
               }
            } else if (breadcrumbs.size()==2){
               for (int i = 1; i < sRelTypes.length; i++) {
                  Relation thisRelation = null;
                  Node nRubriek = cloud.getNode( (String) breadcrumbs.get(i-1));
                  thisRelation = nRubriek.createRelation(nElement,
                  cloud.getRelationManager(sRelTypes[i]));
                  thisRelation.commit();
               }
               Relation thisRelation = null;
               Node nRubriek = cloud.getNode( (String) breadcrumbs.get(0));
               thisRelation = nRubriek.createRelation(nElement,
               cloud.getRelationManager(sRelTypes[0]));
               thisRelation.commit();
            }
         }
         else {
            //use rubriek with alias "archive" as creatierubriek and hoofdrubriek and the parent of archive as subsite
            Node nArchive = cloud.getNodeByAlias("archive");
            for (int i = 0; i < 2; i++) {
               Relation thisRelation = null;
               thisRelation = nArchive.createRelation(nElement,cloud.getRelationManager(sRelTypes[i]));
               thisRelation.commit();
            }
            NodeList nParent = nArchive.getRelatedNodes("rubriek", "parent","DESTINATION");
            if (nParent.size() > 0) {
               Node nArchiveParent = nParent.getNode(0);
               Relation thisRelation = null;
               thisRelation = nArchiveParent.createRelation(nElement,cloud.getRelationManager(sRelTypes[2]));
               thisRelation.commit();
            }
         }
      }
   }

   boolean bRelationsExists(String objectNumber){
      NodeList nlSubsite = cloud.getList(objectNumber,"contentelement,subsite,rubriek",
      "contentelement.number",null,null,null,null,true);
      NodeList nlHoofdrubriek = cloud.getList(objectNumber,"contentelement,hoofdrubriek,rubriek",
      "contentelement.number",null,null,null,null,true);
      NodeList nlCreatierubriek = cloud.getList(objectNumber,"contentelement,creatierubriek,rubriek",
      "contentelement.number",null,null,null,null,true);
      if (nlSubsite.size() + nlHoofdrubriek.size() + nlCreatierubriek.size()==0){
         return false;
      } else {
         if (nlSubsite.size() > 0){
            log.info("relation contentelement,subsite,rubriek for node " +
            objectNumber + " already exists");
         }
         if (nlHoofdrubriek.size() > 0){
            log.info("relation contentelement,hoofdrubriek,rubriek for node " +
            objectNumber + " already exists");
         }
         if (nlCreatierubriek.size() > 0){
            log.info("relation contentelement,creatierubriek,rubriek for node " +
            objectNumber + " already exists");
         }
         return true;
      }

   }

   public void addSchrijver(String objectNumber) {
      NodeList nl = cloud.getList(objectNumber,"contentelement,schrijver,users",
      "users.number",null,null,null,null,true);
      if (nl.size()==0) {
         /*try to find user with:
               contentelement.owner=users.account */
         nl = cloud.getList("","contentelement,schrijver,users",
         "users.number","contentelement.owner = users.account",null,null,null,true);
         if (nl.size()>0) {
            //create relation contentelement-schrijver-user
            Node nUser = cloud.getNode(nl.getNode(0).getStringValue("users.number"));
            Relation thisRelation = null;
            thisRelation = (cloud.getNode(objectNumber)).createRelation(nUser,cloud.getRelationManager("schrijver"));
            thisRelation.commit();
         }
      }
   }

}
