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
 * @javadoc
 *
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 * @version $Id: BasicRelationManager.java,v 1.21 2003-04-08 14:35:42 pierre Exp $
 */
public class BasicRelationManager extends BasicNodeManager implements RelationManager {
    private static Logger log = Logging.getLoggerInstance(BasicRelationManager.class.getName());

    public MMObjectNode relDefNode;
    private MMObjectNode typeRelNode;
    private int snum;
    private int dnum;

    /**
     * Creates a new Relation manager (for insert).
     * The type of manager (a strictly constrained manager or a role manager)
     * is dependend on the type of the passed node (from either the reldef of typerel
     * builder).
     * @param node the node on which to base the relation manager
     * @param cloud the cloud for which to create the manager
     * @param id the id of the node in the temporary cloud
     */
    BasicRelationManager(MMObjectNode node, Cloud cloud, int nodeId) {
        super(node,cloud,nodeId);
    }

    /**
     * Creates a new instance of Relation manager.
     * The type of manager (a strictly constrained manager or a role manager)
     * is dependend on the type of the passed node (from either the reldef of typerel
     * builder).
     * @param node the node on which to base the relation manager
     * @param cloud the cloud for which to create the manager
     */
    BasicRelationManager(MMObjectNode node, Cloud cloud) {
        super(node,cloud);
    }

    /**
     * Initializes the NodeManager: determines the MMObjectBuilder from the
     * passed node (reldef or typerel), and fillls temporary variables to maintain status.
     */
    protected void initManager() {
        if (noderef.getBuilder() instanceof RelDef) {
            relDefNode= noderef;
        } else {
            typeRelNode = noderef;
            snum=typeRelNode.getIntValue("snumber");
            dnum=typeRelNode.getIntValue("dnumber");
            relDefNode= typeRelNode.getBuilder().getNode(typeRelNode.getIntValue("rnumber"));
        }
        builder=((RelDef)relDefNode.getBuilder()).getBuilder(relDefNode.getNumber());
        super.initManager();
    }

    public Node createNode() {
        Node relation = super.createNode();
        if(relation == null) {
            throw new RuntimeException("relation node is null");
        }
        if(relDefNode == null) {
            throw new RuntimeException("reldef node is null");
        }
        ((BasicNode)relation)._setValue("rnumber", new Integer(relDefNode.getNumber()));
        return relation;
    }

    public String getForwardRole() {
        return relDefNode.getStringValue("sname");
    }

    public String getReciprocalRole() {
        return relDefNode.getStringValue("dname");
    }

    public String getForwardGUIName() {
        return relDefNode.getStringValue("sguiname");
    }

    public String getReciprocalGUIName() {
        return relDefNode.getStringValue("dguiname");
    }

    public int getDirectionality() {
        return relDefNode.getIntValue("dir");
    }

    public int getBuilder() {
        return relDefNode.getIntValue("builder");
    }

    public NodeManager getSourceManager() {
        if (typeRelNode==null) {
            throw new BridgeException("This relationmanager does not contain source information.");
        }
        int nr=typeRelNode.getIntValue("snumber");
        return cloud.getNodeManager(nr);
    }

    public NodeManager getDestinationManager() {
        if (typeRelNode==null) {
            throw new BridgeException("This relationmanager does not contain destination information.");
        }
        int nr=typeRelNode.getIntValue("dnumber");
        return cloud.getNodeManager(nr);
    }

    public Relation createRelation(Node sourceNode, RelationManager relationManager) {
        return super.createRelation(sourceNode, relationManager);
    }

    public Relation createRelation(Node sourceNode, Node destinationNode) {
        //
        // checks whether all components are part of the same cloud/transaction
        // maybe should be made more flexible?
        //
        if (sourceNode.getCloud() != cloud) {
            throw new BridgeException("Relationmanager and source node are not in the same transaction or in different clouds.");
        }
        if (destinationNode.getCloud() != cloud) {
            throw new BridgeException("Relationmanager and destination node are not in the same transaction or in different clouds.");
        }
        if (!(cloud instanceof Transaction)  &&
                (((BasicNode)sourceNode).isNew() || ((BasicNode)destinationNode).isNew())) {
            throw new BridgeException("Cannot add a relation to a new node that has not been committed.");
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
        return new BasicRelationList(result, this);
    }

    public boolean mayCreateRelation(Node sourceNode, Node destinationNode) {
        return cloud.check(Operation.CREATE, builder.oType,
                           sourceNode.getNumber(), destinationNode.getNumber());
    }
}
