package com.finalist.newsletter.util;

/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */

import org.mmbase.bridge.Field;
import org.mmbase.bridge.FieldIterator;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.Relation;
import org.mmbase.bridge.RelationIterator;
import org.mmbase.bridge.RelationManager;
import org.mmbase.bridge.StringList;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public class CloneUtil {

   /** MMbase logging system */
   private static Logger log = Logging.getLoggerInstance(CloneUtil.class.getName());

   public static void cloneAliasses(Node localNode, Node destNode) {
      StringList list = localNode.getAliases();
      for (int x = 0; x < list.size(); x++) {
         destNode.createAlias(list.getString(x));
      }
   }

   /**
    * Clone a node to a cloud, including any fields without keeping
    * administrative information
    * 
    * @param localNode
    *           the node to clone
    * @return the newly created node in the other cloud
    */
   public static Node cloneNode(Node localNode) {
      if (isRelation(localNode)) {
         return cloneRelation(localNode);
      } else {
         NodeManager localNodeManager = localNode.getNodeManager();
         NodeManager nodeManager = localNode.getCloud().getNodeManager(localNodeManager.getName());
         Node newNode = nodeManager.createNode();

         FieldIterator fields = localNodeManager.getFields().fieldIterator();
         while (fields.hasNext()) {
            Field field = fields.nextField();
            String fieldName = field.getName();

            if (field.getState() == Field.STATE_PERSISTENT) {
               if (!(fieldName.equals("owner") || fieldName.equals("number") || fieldName.equals("otype") || (fieldName.indexOf("_") == 0))) {
                  cloneNodeField(localNode, newNode, field);
               }
            }
         }
         newNode.commit();

         return newNode;
      }
   }

   public static Node cloneNode(Node localNode, String childNodeType) {
      if (isRelation(localNode)) {
         return cloneRelation(localNode);
      } else if (childNodeType == null || childNodeType.length() == 0) {
         throw new NullPointerException("clone node #" + localNode.getNumber() + " returned null");
      } else {
         NodeManager localNodeManager = localNode.getNodeManager();
         NodeManager childNodeManager = localNode.getCloud().getNodeManager(childNodeType);
         Node newNode = childNodeManager.createNode();

         FieldIterator fields = localNodeManager.getFields().fieldIterator();
         while (fields.hasNext()) {
            Field field = fields.nextField();
            String fieldName = field.getName();

            if (field.getState() == Field.STATE_PERSISTENT) {
               if (!(fieldName.equals("owner") || fieldName.equals("number") || fieldName.equals("otype") || (fieldName.indexOf("_") == 0))) {
                  cloneNodeField(localNode, newNode, field);
               }
            }
         }
         newNode.commit();

         return newNode;
      }
   }

   /**
    * cloneNodeField copies node fields from one node to an other
    * 
    * @param sourceNode
    *           the source node
    * @param destinationNode
    *           destination node
    * @param field
    *           the field to clone
    */
   public static void cloneNodeField(Node sourceNode, Node destinationNode, Field field) {
      String fieldName = field.getName();
      int fieldType = field.getType();

      if (destinationNode.getNodeManager().hasField(fieldName) == true) {
         switch (fieldType) {
         case Field.TYPE_BINARY:
            destinationNode.setByteValue(fieldName, sourceNode.getByteValue(fieldName));
            break;
         case Field.TYPE_BOOLEAN:
            destinationNode.setBooleanValue(fieldName, sourceNode.getBooleanValue(fieldName));
            break;
         case Field.TYPE_DATETIME:
            destinationNode.setDateValue(fieldName, sourceNode.getDateValue(fieldName));
            break;
         case Field.TYPE_DOUBLE:
            destinationNode.setDoubleValue(fieldName, sourceNode.getDoubleValue(fieldName));
            break;
         case Field.TYPE_FLOAT:
            destinationNode.setFloatValue(fieldName, sourceNode.getFloatValue(fieldName));
            break;
         case Field.TYPE_INTEGER:
            destinationNode.setIntValue(fieldName, sourceNode.getIntValue(fieldName));
            break;
         case Field.TYPE_LONG:
            destinationNode.setLongValue(fieldName, sourceNode.getIntValue(fieldName));
            break;
         case Field.TYPE_NODE:
            destinationNode.setNodeValue(fieldName, sourceNode.getNodeValue(fieldName));
            break;
         case Field.TYPE_STRING:
            destinationNode.setStringValue(fieldName, sourceNode.getStringValue(fieldName));
            break;
         default:
            destinationNode.setValue(fieldName, sourceNode.getValue(fieldName));
         }
      }
   }

   /**
    * Clone a node with all relations
    * 
    * @param localNode
    *           the node to be cloned
    * @return the cloned copy of localNode
    */
   public static Node cloneNodeWithRelations(Node localNode) {
      log.debug("clone node (number,type)" + localNode.getNumber() + "," + localNode.getNodeManager().getName() + ")");

      Node newNode = cloneNode(localNode);
      if (newNode == null) {
         throw new NullPointerException("clone node #" + localNode.getNumber() + " returned null");
      } else {
         if (log.isDebugEnabled()) {
            log
                  .debug("cloned the node to the new cloud new node(number,type)" + newNode.getNumber() + "," + newNode.getNodeManager().getName()
                        + ")");
         }
      }
      cloneRelations(localNode, newNode);

      return newNode;
   }

   public static Node cloneNodeWithRelations(Node localNode, String childNodeType) {
      log.debug("clone node (number,type)" + localNode.getNumber() + "," + localNode.getNodeManager().getName() + ")");

      Node newNode = cloneNode(localNode, childNodeType);
      if (newNode == null) {
         throw new NullPointerException("clone node #" + localNode.getNumber() + " returned null");
      } else {
         if (log.isDebugEnabled()) {
            log
                  .debug("cloned the node to the new cloud new node(number,type)" + newNode.getNumber() + "," + newNode.getNodeManager().getName()
                        + ")");
         }
      }
      cloneRelations(localNode, newNode);

      return newNode;
   }

   public static Node cloneRelation(Node localRelation) {
      Node sourceNode = localRelation.getNodeValue("snumber");
      Node destinationNode = localRelation.getNodeValue("dnumber");

      return cloneRelation(localRelation, sourceNode, destinationNode);
   }

   public static Node cloneRelation(Node localRelation, Node sourceNode, Node destinationNode) {
      RelationManager relationManager = null;
      if (localRelation instanceof Relation) {
         Relation localRel = (Relation) localRelation;
         relationManager = localRel.getRelationManager();
      } else {
         Node relationTypeNode = localRelation.getNodeValue("rnumber");
         String relName = relationTypeNode.getStringValue("sname");
         relationManager = localRelation.getCloud().getRelationManager(sourceNode.getNodeManager().getName(),
               destinationNode.getNodeManager().getName(), relName);

      }
      Relation newRelation = relationManager.createRelation(sourceNode, destinationNode);
      FieldIterator fields = localRelation.getNodeManager().getFields().fieldIterator();
      while (fields.hasNext()) {
         Field field = fields.nextField();
         String fieldName = field.getName();

         if (field.getState() == Field.STATE_PERSISTENT) {
            if (!(fieldName.equals("owner") || fieldName.equals("number") || fieldName.equals("otype") || (fieldName.indexOf("_") == 0)
                  || fieldName.equals("snumber") || fieldName.equals("dir") || fieldName.equals("dnumber") || fieldName.equals("rnumber"))) {
               cloneNodeField(localRelation, newRelation, field);
            }
         }
      }
      newRelation.commit();

      return newRelation;
   }

   public static void cloneRelations(Node localNode, Node newNode) {
      RelationIterator ri = localNode.getRelations().relationIterator();
      if (ri.hasNext()) {
         log.debug("the local node has relations");
      }
      while (ri.hasNext()) {
         Relation rel = ri.nextRelation();
         if (rel.getSource().getNumber() == localNode.getNumber()) {
            cloneRelation(rel, newNode, rel.getDestination());
         } else {
            if (rel.getDestination().getNumber() == localNode.getNumber()) {
               cloneRelation(rel, rel.getSource(), newNode);
            }
         }
      }
   }

   public static void cloneRelations(Node localNode, Node newNode, String relationName, String managerName) {
      RelationIterator ri = localNode.getRelations(relationName, managerName).relationIterator();
      if (ri.hasNext()) {
         log.debug("the local node has relations");
      }
      while (ri.hasNext()) {
         Relation rel = ri.nextRelation();
         if (rel.getSource().getNumber() == localNode.getNumber()) {
            cloneRelation(rel, newNode,  rel.getDestination());
         } 
         else {
            if (rel.getDestination().getNumber() == localNode.getNumber()) {
               cloneRelation(rel, rel.getSource(), newNode);
            }
         }
      }
   }

   /**
    * quick test to see if node is a relation by testing fieldnames
    * 
    * @param node
    *           Possible relation
    * @return <code>true</code> when relation fields present
    */
   protected static boolean isRelation(Node node) {
      FieldIterator fi = node.getNodeManager().getFields().fieldIterator();
      int count = 0;

      while (fi.hasNext()) {
         String name = fi.nextField().getName();

         if (name.equals("rnumber") || name.equals("snumber") || name.equals("dnumber")) {
            count++;
         }
      }

      if (count == 3) {
         return true;
      }

      return false;
   }

   private CloneUtil() {
      // utility
   }

}
