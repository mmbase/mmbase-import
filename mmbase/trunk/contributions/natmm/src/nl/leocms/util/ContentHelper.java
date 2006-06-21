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

}
