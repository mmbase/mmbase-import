/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;

import java.util.Vector;
import java.util.Iterator;
import org.mmbase.bridge.*;
import org.mmbase.security.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.util.logging.*;

/**
 *
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 */
public class BasicRelationManager extends BasicNodeManager implements RelationManager {
    private static Logger log = Logging.getLoggerInstance(BasicRelationManager.class.getName());

    private MMObjectNode typeRelNode = null;
    private MMObjectNode relDefNode = null;
    private int snum = 0;
    private int dnum = 0;
    int roleID  = 0;

    /**
     * Creates a new instance of Relation manager.
     * The type of manager (a strictly constrained manager or a role manager)
     * is dependend on type the passed node (from either the reldef of typerel
     * builder).
     * @param node the node on which to base the relation manager
     * @param cloud the cloud for which to create the manager
     */
    BasicRelationManager(MMObjectNode node, Cloud cloud) {
        RelDef reldef = ((BasicCloudContext)cloud.getCloudContext()).mmb.getRelDef();
        if (node.parent==reldef) {
            relDefNode = node;
            roleID=node.getNumber();
        } else {
            typeRelNode = node;
            snum=node.getIntValue("snumber");
            dnum=node.getIntValue("dnumber");
            roleID=node.getIntValue("rnumber");
            relDefNode= reldef.getNode(roleID);
        }
        builder=reldef.getBuilder(relDefNode);
        init(builder,cloud);
      }

    public Node createNode() {
        Node relation = super.createNode();
        ((BasicNode)relation)._setValue("rnumber", new Integer(roleID));
        return relation;
    }

    public String getForwardRole() {
        return relDefNode.getStringValue("sname");
    }

    public String getReciprocalRole() {
        return relDefNode.getStringValue("dname");
    }

    public int getDirectionality() {
        return relDefNode.getIntValue("dir");
    }

    public int getBuilder() {
        return relDefNode.getIntValue("builder");
    }

    public NodeManager getSourceManager() {
        if (typeRelNode==null) {
            String message;
            message = "This relationmanager does not contain source "
                      + "information.";
            log.error(message);
            throw new BridgeException(message);
        }
        int nr=typeRelNode.getIntValue("snumber");
        return cloud.getNodeManager(nr);
    }

    public NodeManager getDestinationManager() {
        if (typeRelNode==null) {
            String message;
            message = "This relationmanager does not contain destination "
                      + "information.";
            log.error(message);
            throw new BridgeException(message);
        }
        int nr=typeRelNode.getIntValue("dnumber");
        return cloud.getNodeManager(nr);
    }

    public Relation createRelation(Node sourceNode, Node destinationNode) {
        //
        // checks whether all components are part of the same cloud/transaction
        // maybe should be made more flexible?
        //
        if (sourceNode.getCloud() != cloud) {
            String message;
            message = "Relationmanager and source node are not in the same "
                      + "transaction or in different clouds.";
            log.error(message);
            throw new BridgeException(message);
        }
        if (destinationNode.getCloud() != cloud) {
            String message;
            message = "Relationmanager and destination node are not in the same "
                      + "transaction or in different clouds.";
            log.error(message);
            throw new BridgeException(message);
        }
        if (!(cloud instanceof Transaction)  &&
                (((BasicNode)sourceNode).isNew() || ((BasicNode)destinationNode).isNew())) {
            String message;
            message = "Cannot add a relation to a new node that has not been "
                      + "committed.";
            log.error(message);
            throw new BridgeException(message);
        }

       BasicRelation relation = (BasicRelation)createNode();
       relation.setSource(sourceNode);
       relation.setDestination(destinationNode);
       relation.checkValid();
       // relation.commit();
       return relation;
    }

    public RelationList getRelations(Node node) {
        // XXX: no caching is done here?
        Vector result = new Vector();
        InsRel insRel = (InsRel) builder; 
        for (Iterator i = insRel.getRelationsVector(node.getNumber()).iterator(); i.hasNext(); ) {
            MMObjectNode r = (MMObjectNode) i.next();
            result.add(r);            
        }
        return new BasicRelationList(result, cloud, this);
    }

    /**
     * Compares two relationmanagers, and returns true if they are equal.
     * This effectively means that both objects are relationmanagers, and they both use to the same builder type
     * @param o the object to compare it with
     */
    public boolean equals(Object o) {
        return (o instanceof RelationManager) && (o.hashCode()==hashCode());
    }

    /**
     * Returns the relationmanager's hashCode.
     * This effectively returns the object number of the typerel record
     */
    public int hashCode() {
        if (typeRelNode==null) {
          return relDefNode.getNumber();
        }
        return typeRelNode.getNumber();
    }

    public boolean mayCreateRelation(Node sourceNode, Node destinationNode) {
        return cloud.check(Operation.CREATE, builder.oType,
                           sourceNode.getNumber(), destinationNode.getNumber());
    }
}
