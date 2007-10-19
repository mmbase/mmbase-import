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
package nl.leocms.authorization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nl.leocms.util.RubriekHelper;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeIterator;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.Relation;
import org.mmbase.bridge.RelationIterator;
import org.mmbase.bridge.RelationList;
import org.mmbase.bridge.RelationManager;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * @author Edwin van der Elst
 * Date :Oct 7, 2003
 * 
 */
public class AuthorizationHelper {

   private static final Logger log = Logging.getLoggerInstance(AuthorizationHelper.class);

   // utility, node.equals() werkt niet goed bij remotecloud 
   private static void addToList(List l, Node user) {
      for (int i = 0; i < l.size(); i++) {
         Node n = (Node) l.get(i);
         if (n.getNumber() == user.getNumber()) {
            return;
         }
      }
      l.add(user);
   }

   private static void removeFromList(List ret, Node destinationUser) {
      // Dit werkt niet: equals() niet betrouwbaar
      // ret.remove(relation.getDestination());
      for (int i = 0; i < ret.size(); i++) {
         Node user = (Node) ret.get(i);
         if (user.getNumber() == destinationUser.getNumber()) {
            ret.remove(i);
         }
      }
   }

   private Cloud cloud;

   public AuthorizationHelper(Cloud c) {
      cloud = c;
   }

   private void addRolesForUser(Map roles, Node rubriek, Node user, int parentRole) {
      int rol = parentRole;
      boolean inherited = true;

      RelationManager rolerelManager = cloud.getRelationManager("rubriek", "users", "rolerel");
      RelationList list = rolerelManager.getRelations(rubriek); // alle users 
      for (RelationIterator ri = list.relationIterator(); ri.hasNext();) {
         Relation relation = ri.nextRelation();
         if (relation.getDestination().getNumber() == user.getNumber()) {
            rol = relation.getIntValue("rol");
            inherited = false;
            break;
         }
      }

      Integer rubriekKey = new Integer(rubriek.getNumber());
      if (roles.containsKey(rubriekKey)) {
         throw new RuntimeException("recursie in rubrieken : " + rubriekKey);
      }
      roles.put(rubriekKey, new UserRole(rol, inherited));
      NodeList childs = rubriek.getRelatedNodes("rubriek", "parent", "DESTINATION");
      for (NodeIterator i = childs.nodeIterator(); i.hasNext();) {
         Node child = i.nextNode();
         addRolesForUser(roles, child, user, rol);
      }
   }



   /**
    * Bepaal voor ALLE rubrieken de rechten voor een user
    * 
    * @param user
    * @return Map - key = rubrieknodenumber, value = UserRole object  
    */
   public Map getRolesForUser(Node user) {
      Map ret = new HashMap();
      Node root = cloud.getNode("root");
      addRolesForUser(ret, root, user, Roles.NONE);
      return ret;
   }
   
   
   
   /**
    * Bepaal voor een pagina de rechten voor een user
    * dmv de rubirek waarin de pagina zit
    * @param user
    * @param rubriek
    * @return UserRole - rights of a user  
    */
   public UserRole getRoleForUserWithPagina(Node userNode, String paginaNodeNumber) {
      NodeList nodeList= cloud.getList(paginaNodeNumber,
            "pagina,posrel,rubriek", 
            "rubriek.number", 
            null, null, null, "SOURCE", true);
      if ((nodeList != null) && (nodeList.size() > 0)) {
         Node tempNode = nodeList.getNode(0);
         String rubriekNodeNumber = tempNode.getStringValue("rubriek.number");
         Node rubriekNode = cloud.getNode(rubriekNodeNumber);
        return getRoleForUser(userNode, rubriekNode, false);
      }
      return new UserRole(Roles.NONE, true);
   }
   
   
   /**
    * Bepaal voor een rubriek de rechten voor een user
    * 
    * @param user
    * @param rubriek
    * @return UserRole - rights of a user  
    */
   public UserRole getRoleForUser(String userNodeNumber, String rubriekNodeNumber) {
      Node userNode = cloud.getNode(userNodeNumber);
      Node rubriekNode = cloud.getNode(rubriekNodeNumber);
      return getRoleForUser(userNode, rubriekNode, false);
   }
   
      
   /**
    * Bepaal voor een rubriek de rechten voor een user
    * 
    * @param user
    * @param rubriek
    * @return UserRole - rights of a user  
    */
   public UserRole getRoleForUser(Node user, Node rubriek) {
      return getRoleForUser(user, rubriek, false);
   }
   
   /**
    * Bepaal voor een rubriek de rechten voor een user
    * 
    * @param user
    * @param rubriek
    * @return UserRole - rights of a user  
    */
   public UserRole getRoleForUser(Node user, Node rubriek, boolean rightsInherited) {
      NodeList userNodeList= cloud.getList(""+rubriek.getNumber(),
            "rubriek,rolerel,users", 
            "rolerel.rol", 
            "[users.number] = " + user.getNumber(), null, null, null, true);
      if ((userNodeList != null) && (userNodeList.size() > 0)) {
         Node tempNode = userNodeList.getNode(0);
         int role = tempNode.getIntValue("rolerel.rol");
         return new UserRole(role, rightsInherited);
      }
      else {
         NodeList rubriekNodeList= cloud.getList(""+rubriek.getNumber(),
            "rubriek,parent,rubriek2", 
            "rubriek2.number", 
            null, null, null, "SOURCE", true);
         if ((rubriekNodeList != null) && (rubriekNodeList.size() > 0)) {
            Node tempNode = rubriekNodeList.getNode(0);
            String rubriekNumber = tempNode.getStringValue("rubriek2.number");
            return getRoleForUser(user, cloud.getNode(rubriekNumber), true);
         }
      }
      return new UserRole(Roles.NONE, rightsInherited);
   }
   


   public Node getUserNode(String account) {
      NodeManager manager = cloud.getNodeManager("users");
      NodeList list = manager.getList("account='"+account+"'",null,null);
      if (list.size()==1) {
         return list.getNode(0);
      } 
      else {
         throw new RuntimeException("User "+account+" not found"); 
      }
   }
   
   /**
    * 
    * @param rubriek - rubriek-node 
    * @param rights - minimaal gewenste rechten
    * @return List - bevat MMBase Nodes (users) 
    */
   public List getUsersWithRights(Node rubriek, int rights) {
      /**
       * Acties:
       * 1 - Bepaal 'path' naar root rubriek
       * 2 - Begin met een lege lijst
       * 3 - Voeg alle users die op deze rubriek de juiste rechten hebben toe aan de lijst (rolerel)
       * 4 - Ga naar de subrubriek
       * 5 - Verwijder alle users uit de lijst die op de subrubriek rechten 'geen' hebben
       * 6 - ga naar stap 3 
       * 
       */
      List path = new RubriekHelper(cloud).getPathToRoot(rubriek);
      List ret = new ArrayList(); // user nodes

      RelationManager rolerelManager = cloud.getRelationManager("rubriek", "users", "rolerel");

      Iterator iter = path.iterator();
      while (iter.hasNext()) {
         Node rub = (Node) iter.next();
         RelationList rolerels = rolerelManager.getRelations(rub);
         // = alle rolerel-relaties vanuit rubriek naar users

         RelationIterator rels = rolerels.relationIterator();
         while (rels.hasNext()) {
            Relation relation = rels.nextRelation();

            Node destinationUser = relation.getDestination();

            if (relation.getIntValue("rol") >= rights) {
               addToList(ret, destinationUser);
            }
            // iedereen met een lagere rol (bv. GEEN) weer UIT de lijst halen
            // equals werkt op NUMBER, misschien krijgen we namelijk verschillende instanties van Node met hetzeldfe Number
            if (relation.getIntValue("rol") < rights) {
               removeFromList(ret, destinationUser);
            }
         }
      }
      return ret;
   }

   public void setUserRights(Node user, Map rights) {
      RelationList list = user.getRelations("rolerel");
      for (RelationIterator i=list.relationIterator(); i.hasNext();) {
         i.nextRelation().delete(true);
      }
      RelationManager rolemanager = cloud.getRelationManager("rubriek","users","rolerel");
      Iterator keys = rights.keySet().iterator();
      while (keys.hasNext()) {
         Integer rubriekNumber = (Integer) keys.next();
         Relation relation = rolemanager.createRelation( cloud.getNode( rubriekNumber.intValue()), user);
         UserRole role = (UserRole) rights.get(rubriekNumber);
         relation.setIntValue("rol", role.getRol());
         relation.commit();
      }
      
   }

}