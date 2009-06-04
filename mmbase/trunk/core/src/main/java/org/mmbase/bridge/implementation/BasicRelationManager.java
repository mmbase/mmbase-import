/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;

import java.util.List;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.BridgeCollections;
import org.mmbase.security.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.storage.search.SearchQueryException;
import org.mmbase.util.logging.*;

/**
 * @javadoc
 *
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 * @version $Id$
 */
public class BasicRelationManager extends BasicNodeManager implements RelationManager {
    private static final Logger log = Logging.getLoggerInstance(BasicRelationManager.class);

    public MMObjectNode relDefNode;
    private MMObjectNode typeRelNode;

    /**
     * Creates a new Relation manager (for insert).
     * The type of manager (a strictly constrained manager or a role manager)
     * is dependend on the type of the passed node (from either the reldef of typerel
     * builder).
     * @param node the node on which to base the relation manager
     * @param cloud the cloud for which to create the manager
     * @param id the id of the node in the temporary cloud
     */
    BasicRelationManager(MMObjectNode node, BasicCloud cloud, int nodeId) {
        super(node, cloud, nodeId);
    }

    /**
     * Creates a new instance of Relation manager.
     * The type of manager (a strictly constrained manager or a role manager)
     * is dependend on the type of the passed node (from either the reldef of typerel
     * builder).
     * @param node the node on which to base the relation manager
     * @param cloud the cloud for which to create the manager
     */
    BasicRelationManager(MMObjectNode node, BasicCloud cloud) {
        super(node, cloud);
    }

    @Override
    public final boolean isRelationManager() {
        return true;
    }
    @Override
    public final  RelationManager toRelationManager() {
        return this;
    }

    /**
     * Initializes the NodeManager: determines the MMObjectBuilder from the
     * passed node (reldef or typerel), and fills temporary variables to maintain status.
     */
    @Override
    protected void initManager() {
        MMObjectBuilder bul = noderef.getBuilder();
        if (bul instanceof RelDef) {
            relDefNode = noderef;
        } else if (bul instanceof TypeRel) {
            typeRelNode = noderef;
            relDefNode = typeRelNode.getBuilder().getNode(typeRelNode.getIntValue("rnumber"));
            if (relDefNode == null) {
                log.warn("No node found for 'rnumber'" + typeRelNode.getIntValue("rnumber"));
            }
        } else {
            throw new RuntimeException("The builder of node " + noderef.getNumber() + " is not reldef or typerel, but " + bul.getTableName() + " cannot instantiate a relation manager with this");
        }

        RelDef relDef = (RelDef) relDefNode.getBuilder();
        if (relDef != null) {
            builder = relDef.getBuilder(relDefNode.getNumber());
        } else {
            log.warn("builder of " + relDefNode + " was  null");
        }
        super.initManager();
    }


    @Override
    protected void setNodeManager(MMObjectNode node) {
        int nodeNumber = node.getNumber();
        if (nodeNumber >= 0 && nodeNumber == getNode().getBuilder().getNumber()) { // this is the typedef itself
            nodeManager = this;
        } else {
            super.setNodeManager(node);
        }
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

    int getBuilder() {
        return relDefNode.getIntValue("builder");
    }

    public NodeManager getSourceManager() {
        if (typeRelNode == null) {
            throw new BridgeException("This relationmanager does not contain source information.");
        }
        int nr = typeRelNode.getIntValue("snumber");
        return cloud.getNodeManager(nr);
    }

    public NodeManager getDestinationManager() {
        if (typeRelNode == null) {
            throw new BridgeException("This relationmanager does not contain destination information.");
        }
        int nr = typeRelNode.getIntValue("dnumber");
        return cloud.getNodeManager(nr);
    }


    @Override
    protected final BasicNode createBasicNode() {
        return createBasicRelation();
    }

    /**
     * BasicRelationManager is garantueed to create BasicRelations. Extension therefore most override this and not {@link #createBasicNode}.
     * @since MMBase-1.8
     */
    protected BasicRelation createBasicRelation() {
        if(relDefNode == null) {
            throw new RuntimeException("reldef node is null");
        }
        NodeAndId n = createMMObjectNode();
        BasicRelation relation =  new BasicRelation(n.node, cloud, n.id);
        relation.setValueWithoutChecks("rnumber", relDefNode.getNumber());
        return relation;
    }

    public Relation createRelation(Node sourceNode, Node destinationNode) {
        //
        // checks whether all components are part of the same cloud/transaction
        // maybe should be made more flexible?
        //
        if (sourceNode.getCloud() != cloud) {
            throw new BridgeException("Relationmanager and source node are not in the same transaction or in different clouds." + sourceNode.getCloud() + " != " + cloud);
        }
        if (destinationNode.getCloud() != cloud) {
            throw new BridgeException("Relationmanager and destination node are not in the same transaction or in different clouds.");
        }
        if (!(cloud instanceof Transaction)  && (sourceNode.isNew() || destinationNode.isNew())) {
            throw new BridgeException("Cannot add a relation to a new node that has not been committed.");
        }

       BasicRelation relation = createBasicRelation();
       relation.setSource(sourceNode);
       relation.setDestination(destinationNode);
       relation.checkValid();
       // relation.commit();
       return relation;
    }

    public RelationList getRelations(Node node) {
        try {
            // XXX: no caching is done here?
            InsRel insRel = (InsRel) builder;
            List<MMObjectNode> result = insRel.getRelationNodes(node.getNumber());
            return new BasicRelationList(result, this);
        } catch (SearchQueryException ex) {
            log.error(ex);
            return BridgeCollections.EMPTY_RELATIONLIST;
        }
    }

    public boolean mayCreateRelation(Node sourceNode, Node destinationNode) {
        return cloud.check(Operation.CREATE, builder.getNumber(),
                           sourceNode.getNumber(), destinationNode.getNumber());
    }
    @Override
    public String toString() {
        return "RelationManager " +
            (typeRelNode != null ? getSourceManager().getName() : "???") +
            " -" + (relDefNode != null ? getForwardRole() : "???") + "-> " +
            (typeRelNode != null ? getDestinationManager().getName() : "???") +
            " ( " + getNode().getNumber() + ")";
    }
}
