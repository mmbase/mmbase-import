/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.util;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * Utility to clone mmbase bridge nodes.
 * @since MMBase-1.9.0
 * @version $Id$
 */
public class CloneUtil {

    private static final Logger log = Logging.getLoggerInstance(CloneUtil.class);

    private CloneUtil() {
        // utility
    }

    /**
     * Clone a node with all relations
     *
     * @param localNode the node to be cloned
     * @return  the cloned copy of localNode
     */
    public static Node cloneNodeWithRelations(Node localNode) {
      log.debug("clone node (number,type)" +
                localNode.getNumber() + "," + localNode.getNodeManager().getName() + ")");

      Node newNode = cloneNode(localNode);
      if (newNode == null) {
         throw new NullPointerException("clone node #" + localNode.getNumber() + " returned null");
      }
      else {
         if (log.isDebugEnabled()) {
            log.debug("cloned the node to the new cloud new node(number,type)" +
                   newNode.getNumber() + "," + newNode.getNodeManager().getName() + ")");
         }
      }
      cloneRelations(localNode, newNode);

      return newNode;
    }

    /**
     * Clone a node to a cloud, including any fields without keeping administrative information
     *
     * @param localNode the node to clone
     * @return the newly created node in the other cloud
     */
    public static Node cloneNode(Node localNode) {
       if (isRelation(localNode)) {
          return cloneRelation(localNode);
       }
       else {
         NodeManager localNodeManager = localNode.getNodeManager();
         NodeManager nodeManager = localNode.getCloud().getNodeManager(localNodeManager.getName());
         Node newNode = nodeManager.createNode();

         FieldIterator fields = localNodeManager.getFields().fieldIterator();
         while (fields.hasNext()) {
            Field field = fields.nextField();
            String fieldName = field.getName();

            if (field.getState() == Field.STATE_PERSISTENT) {
                if (!(fieldName.equals("owner") || fieldName.equals("number") ||
                      fieldName.equals("otype") ||
                      (fieldName.indexOf("_") == 0))) {
                   cloneNodeField(localNode, newNode, field);
                }
            }
         }
         newNode.commit();

         return newNode;
       }
    }

    /**
     * cloneNodeField copies node fields from one node to the other
     * @param sourceNode the source node
     * @param destinationNode destination node
     * @param field the field to clone
     */
    public static void cloneNodeField(Node sourceNode, Node destinationNode,
       Field field) {
       String fieldName = field.getName();
       int fieldType = field.getType();

       switch (fieldType) {
       case Field.TYPE_BINARY:
          destinationNode.setByteValue(fieldName,
             sourceNode.getByteValue(fieldName));
          break;
       case Field.TYPE_BOOLEAN:
           destinationNode.setBooleanValue(fieldName,
              sourceNode.getBooleanValue(fieldName));
           break;
       case Field.TYPE_DATETIME:
           destinationNode.setDateValue(fieldName,
              sourceNode.getDateValue(fieldName));
           break;
       case Field.TYPE_DOUBLE:
          destinationNode.setDoubleValue(fieldName,
             sourceNode.getDoubleValue(fieldName));
          break;
       case Field.TYPE_FLOAT:
          destinationNode.setFloatValue(fieldName,
             sourceNode.getFloatValue(fieldName));
          break;
       case Field.TYPE_INTEGER:
          destinationNode.setIntValue(fieldName,
             sourceNode.getIntValue(fieldName));
          break;
       case Field.TYPE_LONG:
          destinationNode.setLongValue(fieldName,
             sourceNode.getIntValue(fieldName));
          break;
       case Field.TYPE_NODE:
           destinationNode.setNodeValue(fieldName,
              sourceNode.getNodeValue(fieldName));
           break;
       case Field.TYPE_STRING:
          destinationNode.setStringValue(fieldName,
             sourceNode.getStringValue(fieldName));
          break;
       default:
          destinationNode.setValue(fieldName, sourceNode.getValue(fieldName));
       }
    }

    /**
     * Clone relation of the source node to the destination node.
     * @param sourceRelation source relation
     * @return cloned relation
     */
    public static Relation cloneRelation(Node sourceRelation) {
        Node sourceNode = sourceRelation.getNodeValue("snumber");
        Node destinationNode = sourceRelation.getNodeValue("dnumber");

        return cloneRelation(sourceRelation, sourceNode, destinationNode);
    }

    /**
     * Clone relation of the source node to the destination node.
     * @param sourceRelation source relation
     * @param sourceNode source node
     * @param destNode destination node
     * @return cloned relation
     */
    public static Relation cloneRelation(Node sourceRelation, Node sourceNode, Node destNode) {
        RelationManager relationManager = null;
        if (sourceRelation instanceof Relation) {
            Relation localRel = (Relation) sourceRelation;
            relationManager = localRel.getRelationManager();
        }
        else {
            Node relationTypeNode = sourceRelation.getNodeValue("rnumber");
            String relName = relationTypeNode.getStringValue("sname");
            relationManager = sourceRelation.getCloud().getRelationManager(sourceNode.getNodeManager().getName(),
                                               destNode.getNodeManager().getName(),
                                               relName);
        }
        Relation newRelation = relationManager.createRelation(sourceNode, destNode);

        FieldIterator fields = sourceRelation.getNodeManager().getFields().fieldIterator();
        while (fields.hasNext()) {
            Field field = fields.nextField();
            String fieldName = field.getName();

            if (field.getState() == Field.STATE_PERSISTENT) {
                if (!(fieldName.equals("owner") || fieldName.equals("number")
                        || fieldName.equals("otype") || (fieldName.indexOf("_") == 0)
                        || fieldName.equals("snumber") || fieldName.equals("dir")
                        || fieldName.equals("dnumber") || fieldName.equals("rnumber"))) {
                    cloneNodeField(sourceRelation, newRelation, field);
                }
            }
        }
        newRelation.commit();

        return newRelation;
    }

    /**
     * Clone relations of the source node to the destination node. In other words,
     * create new relations between the destination node and the nodes which the source node has relations to.
     * @param sourceNode source node
     * @param destNode destination node
     */
    public static void cloneRelations(Node sourceNode, Node destNode) {
       RelationIterator ri = sourceNode.getRelations().relationIterator();
       if (ri.hasNext()) {
          log.debug("the local node has relations");
       }
       while (ri.hasNext()) {
          Relation rel = ri.nextRelation();
          if (rel.getSource().getNumber() == sourceNode.getNumber()) {
              cloneRelation(rel, destNode, rel.getDestination());
          } else {
             if (rel.getDestination().getNumber() == sourceNode.getNumber()) {
                 cloneRelation(rel, rel.getSource(), destNode);
             }
          }
       }
    }

    /**
     * Clone relations of the source node to the destination node. In other words,
     * create new relations between the destination node and the nodes which the source node has relations to.
     * @param sourceNode source node
     * @param destNode destination node
     * @param relationName name of relation
     * @param managerName manager of the other nodes which the relations are replicated for.
     */
    public static void cloneRelations(Node sourceNode, Node destNode, String relationName, String managerName) {
        RelationIterator ri = sourceNode.getRelations(relationName, managerName).relationIterator();
        if (ri.hasNext()) {
           log.debug("the local node has relations");
        }
        while (ri.hasNext()) {
           Relation rel = ri.nextRelation();
           if (rel.getSource().getNumber() == sourceNode.getNumber()) {
               cloneRelation(rel, destNode, rel.getDestination());
           } else {
              if (rel.getDestination().getNumber() == sourceNode.getNumber()) {
                  cloneRelation(rel, rel.getSource(), destNode);
              }
           }
        }
     }

    /**
     * Clone aliases to the destination node.
     * This only works when the node is in a different cloud
     * @param localNode local node
     * @param destNode destination/remote node
     */
    public static void cloneAliasses(Node localNode, Node destNode) {
        StringList list = localNode.getAliases();
        for (int x = 0; x < list.size(); x++) {
           destNode.createAlias(list.getString(x));
        }
     }

    /**
     * quick test to see if node is a relation by testing fieldnames
     * @param node Possible relation
     * @return <code>true</code> when relation fields present
     */
    protected static boolean isRelation(Node node) {
       FieldIterator fi = node.getNodeManager().getFields().fieldIterator();
       int count = 0;

       while (fi.hasNext()) {
          String name = fi.nextField().getName();

          if (name.equals("rnumber") || name.equals("snumber") ||
                name.equals("dnumber")) {
             count++;
          }
       }

       if (count == 3) {
          return true;
       }

       return false;
    }

}
