/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;

import java.util.*;

import org.mmbase.security.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.*;
import org.mmbase.datatypes.DataType;
import org.mmbase.storage.search.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.*;

import org.w3c.dom.Document;

/**
 * Basic implementation of Node. Wraps MMObjectNodes, adds security.
 *
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @version $Id$
 * @see org.mmbase.bridge.Node
 * @see org.mmbase.module.core.MMObjectNode
 */
public class BasicNode extends org.mmbase.bridge.util.AbstractNode implements Node, SizeMeasurable {


    private static final Logger log = Logging.getLoggerInstance(BasicNode.class);

    /**
     * Reference to the NodeManager
     */
    protected BasicNodeManager nodeManager;

    /**
     * Reference to the Cloud.
     */
    final protected BasicCloud cloud;


    /**
     * Reference to actual MMObjectNode object.
     */
    protected MMObjectNode noderef;

    /**
     * Temporary node ID.
     * This is necessary since there is otherwise no sure (and quick) way to determine
     * whether a node is in 'edit' mode (i.e. has a temporary node).
     * Basically, a temporarynodeid is either -1 (invalid), or a negative number smaller than -1
     * (a temporary number assigned by the system).
     */
    protected int temporaryNodeId = -1;

    /**
     * The account this node is edited under.
     * This is needed to check whether people have not switched users during an edit.
     */
    protected String account = null;



    BasicNode(BasicCloud cloud) {
        this.cloud = cloud;
    }
    /**
     * Instantiates a node, linking it to a specified node manager.
     * Use this constructor if the node you create uses a NodeManager that is not readily available
     * from the cloud (such as a temporary nodemanager for a result list).
     * @param node the MMObjectNode to base the node on
     * @param nodeManager the NodeManager to use for administrating this Node
     * @throws IllegalArgumentException If node is null
     */
    BasicNode(MMObjectNode node, BasicNodeManager nodeManager) {
        cloud = nodeManager.cloud;
        this.nodeManager = nodeManager;
        setNode(node);
        init();
    }

    /**
     * Instantiates a node, linking it to a specified cloud
     * The NodeManager for the node is requested from the Cloud.
     * @param node the MMObjectNode to base the node on
     * @param cloud the cloud to which this node belongs
     * @throws IllegalArgumentException If node is null
     */
    BasicNode(MMObjectNode node, BasicCloud cloud) {
        this.cloud =  cloud;
        setNode(node);
        setNodeManager(node);
        init();
    }

    /**
     * Instantiates a new node (for insert), using a specified nodeManager.
     * @param node a temporary MMObjectNode that is the base for the node
     * @param cloud the cloud to create the node in
     * @param id the id of the node in the temporary cloud
     */
    BasicNode(MMObjectNode node, BasicCloud cloud, int id) {
        this.cloud = cloud;
        setNode(node);
        setNodeManager(node);
        temporaryNodeId = id;
        init();
        checkCreate();
    }

    /**
     * Set nodemanager for node
     * @param node node to derive nodemanager from.
     * @since MMBase-1.8
     */
    protected void setNodeManager(MMObjectNode node) {
        nodeManager = cloud.getBasicNodeManager(node.getBuilder());
        assert(nodeManager != null);
    }

    /**
     * Initializes state in case of a transaction.
     */
    protected void init() {
        // check whether the node is currently in transaction
        // and intialize temporaryNodeId if that is the case
        if (temporaryNodeId == -1 && cloud.contains(getNode())) {
            temporaryNodeId = getNode().getNumber();
        }
    }


    public int getByteSize() {
        return getByteSize(new SizeOf());
    }

    public int getByteSize(SizeOf sizeof) {
        return sizeof.sizeof(getNode());
    }

    /**
     * Obtains a reference to the underlying MMObjectNode.
     * If the underlying node was deleted, this returns a virtual node with
     * no info except the (original) node number.
     * @return the underlying MMObjectNode
     * @throws NotFoundException if no node was specified.
     */
    protected final MMObjectNode getNode() {
        return noderef;
    }

    /**
     * Invalidates the reference to the underlying MMObjectNode,
     * replacing it with a virtual node that only inherits the number field.
     * @since MMBase-1.6.4
     */
    protected void invalidateNode() {
        org.mmbase.module.core.VirtualNode n = new org.mmbase.module.core.VirtualNode(noderef.getBuilder());
        n.setValue("number", noderef.getNumber());
        n.clearChanged();
        noderef = n;
    }

    /**
     * Sets the reference to the underlying MMObjectNode.
     * @param n the node to set a reference to.
     * @throws IllegalArgumentException is n is null
     * @since MMBase-1.6.4
     */
    protected void setNode(MMObjectNode n) {
        if (n == null) {
            throw new IllegalArgumentException("Passed Node is null");
        }
        noderef = n;
    }

    public Cloud getCloud() {
        return cloud;
    }

    public NodeManager getNodeManager() {
        return nodeManager;
    }

    public void setNodeManager(NodeManager nm) {
        cloud.check(Operation.WRITE, getNode().getNumber());
        cloud.check(Operation.CREATE, nm.getNumber());

        noderef.setBuilder(BasicCloudContext.mmb.getBuilder(nm.getName()));
        setNodeManager(noderef);
    }


    @Override
    public int getNumber() {
        int i = getNode().getNumber();
        // new node, thus return temp id.
        // note that temp id is equal to "number" if the node is edited
        if (i == -1) {
            i = temporaryNodeId;
        }
        return i;
    }

    /**
     * Returns whether this is a new (not yet committed) node.
     * @return is a new node
     */
    @Override
    public boolean isNew() {
        return getNode().isNew();
    }

    @Override
    public boolean isChanged(String fieldName) {
        return getNode().getChanged().contains(fieldName);
    }

    @Override
    public boolean isChanged() {
        return getNode().isChanged();
    }
    @Override
    public Set<String> getChanged() {
        return Collections.unmodifiableSet(getNode().getChanged());
    }


    protected void checkAccount()  {
        if (account == null) {
            account = cloud.getAccount();
        } else if (!account.equals(cloud.getAccount())) {
            throw new BridgeException("User context changed. Cannot proceed to edit this node .");
        }
    }


    protected void checkDelete() {
        checkAccount();
        int realNumber = getNode().getNumber();
        if (realNumber != -1) {
            cloud.verify(Operation.DELETE, realNumber);
        }
        if (temporaryNodeId == -1) {
            temporaryNodeId = cloud.add(this);
        }
    }
    /**
     * @inheritDoc
     */
    @Override
    protected void checkWrite() {
        checkAccount();
        int realNumber = getNode().getNumber();
        if (realNumber != -1 && temporaryNodeId == -1) {
            cloud.verify(Operation.WRITE, realNumber);
        }
        if (temporaryNodeId == -1) {
            temporaryNodeId = cloud.add(this);
        }
    }

    protected void checkCreate() {
        checkAccount();
    }
    protected void checkCommit() {
        checkAccount();
    }


    /**
     * Protected method to be able to set rnumber when creating a relation.
     * @param fieldName name of field
     * @param value new value of field
     * @since MMBase-1.7
     */
    @Override
    protected void setValueWithoutChecks(String fieldName, Object value) {
        cloud.setValue(this, fieldName, value);
    }
    @Override
    protected Integer toNodeNumber(Object v) {
        if (v == null) {
            return null;
        } else if (v instanceof Node) {
            return Integer.valueOf(((Node)v).getNumber());
        } else if (v instanceof MMObjectNode) {
            return Integer.valueOf(((MMObjectNode)v).getNumber());
        } else {
            // giving up
            return Integer.valueOf(cloud.getNode(v.toString()).getNumber());
        }
    }

    @Override
    protected void setSize(String fieldName, long size) {
        getNode().setSize(fieldName, size);
    }

    @Override
    public boolean isNull(String fieldName) {
        return getNode().isNull(fieldName);
    }

    public long getSize(String fieldName) {
        return getNode().getSize(fieldName);
    }

    /**
     * Like getObjectValue, but skips any processing that MMBase would normally perform on a field.
     * You can use this to get data from a field for validation purposes.
     * @param fieldName name of field
     * @return the value without processing
     * @since MMBase-1.8
     */
    public Object getValueWithoutProcess(String fieldName) {
        // an exception is made for 'owner' field in setValueWithoutProcess, so for symmetry, we
        // must make the same exception here (and also in (getStringValue).
        if ("owner".equals(fieldName)) {
            return getContext();
        }
        Object result = getNode().getValue(fieldName);
        if (result instanceof MMObjectNode) {
            MMObjectNode mmnode = (MMObjectNode) result;
            result = cloud.makeNode(mmnode, "" + mmnode.getNumber());
        }
        return result;
    }
    //TODO, silly get-methods could be removed (because in AbstractNode), (calling
    //getValueWithoutProcess) but they depend on noderef now, so I don't dare to do that right ahead.

    @Override
    public boolean getBooleanValue(String fieldName) {
        Boolean result = Boolean.valueOf(noderef.getBooleanValue(fieldName));
        if (nodeManager.hasField(fieldName)) { // gui(..) stuff could not work.
            Field field = nodeManager.getField(fieldName);
            log.debug("" + field.getDataType().getProcessor(DataType.PROCESS_GET, Field.TYPE_STRING));
            result = Casting.toBoolean(field.getDataType().getProcessor(DataType.PROCESS_GET, Field.TYPE_BOOLEAN).process(this, field, result));
        }
        return result.booleanValue();
    }

    @Override
    public Date getDateValue(String fieldName) {
        Date result =  noderef.getDateValue(fieldName);
        if (nodeManager.hasField(fieldName)) { // gui(..) stuff could not work.
            Field field = nodeManager.getField(fieldName);
            result = Casting.toDate(field.getDataType().getProcessor(DataType.PROCESS_GET, Field.TYPE_DATETIME).process(this, field, result));
        }
        return result;
    }

    @Override
    public List getListValue(String fieldName) {
        List result =  noderef.getListValue(fieldName);
        if (nodeManager.hasField(fieldName)) { // gui(..) stuff could not work.
            Field field = nodeManager.getField(fieldName);
            result = Casting.toList(field.getDataType().getProcessor(DataType.PROCESS_GET, Field.TYPE_LIST).process(this, field, result));
        }

        return result;
    }


    @Override
    public Node getNodeValue(String fieldName) {
        if (fieldName == null || fieldName.equals("number")) {
            return this;
        }
        Node result = null;
        MMObjectNode mmobjectNode = getNode().getNodeValue(fieldName);
        if (mmobjectNode != null) {
            MMObjectBuilder builder = mmobjectNode.getBuilder();
            if (builder instanceof TypeDef) {
                result =  new BasicNodeManager(mmobjectNode, cloud);
            } else if (builder instanceof RelDef || builder instanceof TypeRel) {
                result =  new BasicRelationManager(mmobjectNode, cloud);
            } else if (builder instanceof InsRel) {
                result =  new BasicRelation(mmobjectNode, cloud); //.getNodeManager(noderes.getBuilder().getTableName()));
            } else {
                result = cloud.makeNode(mmobjectNode, mmobjectNode.getStringValue("number")); //.getNodeManager(noderes.getBuilder().getTableName()));
            }
        }
        if (nodeManager.hasField(fieldName)) { // only if this is actually a field of this node-manager, otherewise it might be e.g. a request for an 'element' of a cluster node
            Field field = nodeManager.getField(fieldName);
            result = Casting.toNode(field.getDataType().getProcessor(DataType.PROCESS_GET, Field.TYPE_NODE).process(this, field, result), getCloud());
        }

        return result;
    }

    @Override
    public int getIntValue(String fieldName) {
        Integer result = Integer.valueOf(getNode().getIntValue(fieldName));
        if (nodeManager.hasField(fieldName)) { // gui(..) stuff could not work.
            Field field = nodeManager.getField(fieldName);
            result  = Casting.toInteger(field.getDataType().getProcessor(DataType.PROCESS_GET, Field.TYPE_INTEGER).process(this, field, result));
            // Casting on this position. Should it not be done in all get<..>Value's?
        }
        return result.intValue();

    }

    @Override
    public float getFloatValue(String fieldName) {
        Float result = getNode().getFloatValue(fieldName);
        if (nodeManager.hasField(fieldName)) { // gui(..) stuff could not work.
            Field field = nodeManager.getField(fieldName);
            result = Casting.toFloat(field.getDataType().getProcessor(DataType.PROCESS_GET, Field.TYPE_FLOAT).process(this, field, result));
        }
        return result.floatValue();
    }

    @Override
    public long getLongValue(String fieldName) {
        Long result = getNode().getLongValue(fieldName);
        if (nodeManager.hasField(fieldName)) { // gui(..) stuff could not work.
            Field field = nodeManager.getField(fieldName);
            result = Casting.toLong(field.getDataType().getProcessor(DataType.PROCESS_GET, Field.TYPE_LONG).process(this, field, result));
        }
        return result.longValue();
    }

    @Override
    public double getDoubleValue(String fieldName) {
        Double result = getNode().getDoubleValue(fieldName);
        if (nodeManager.hasField(fieldName)) { // gui(..) stuff could not work.
            Field field = nodeManager.getField(fieldName);
            result = Casting.toDouble(field.getDataType().getProcessor(DataType.PROCESS_GET, Field.TYPE_DOUBLE).process(this, field, result));
        }
        return result.doubleValue();
    }

    @Override
    public byte[] getByteValue(String fieldName) {
        byte[] result = getNode().getByteValue(fieldName);
        if (nodeManager.hasField(fieldName)) { // gui(..) stuff could not work.
            Field field = nodeManager.getField(fieldName);
            result = Casting.toByte(field.getDataType().getProcessor(DataType.PROCESS_GET, Field.TYPE_BINARY).process(this, field, result));
        }
        return result;
    }
    @Override
    public java.io.InputStream getInputStreamValue(String fieldName) {
        java.io.InputStream result = getNode().getInputStreamValue(fieldName);
        if (nodeManager.hasField(fieldName)) { // gui(..) stuff could not work.
            Field field = nodeManager.getField(fieldName);
            result = Casting.toInputStream(field.getDataType().getProcessor(DataType.PROCESS_GET, Field.TYPE_BINARY).process(this, field, result));
        }
        return result;
    }

    @Override
    public String getStringValue(String fieldName) {
        if ("owner".equals(fieldName)) {
            return getContext();
        }
        String result = getNode().getStringValue(fieldName);
        if (nodeManager.hasField(fieldName)) { // gui(..) stuff could not work.
            Field field = nodeManager.getField(fieldName);
            result = Casting.toString(field.getDataType().getProcessor(DataType.PROCESS_GET, Field.TYPE_STRING).process(this, field, result));
        }
        return result;
    }

    @Override
    public Document getXMLValue(String fieldName) {
        Document result = getNode().getXMLValue(fieldName);
        if (nodeManager.hasField(fieldName)) { // gui(..) stuff could not work.
            Field field = nodeManager.getField(fieldName);
            result = Casting.toXML(field.getDataType().getProcessor(DataType.PROCESS_GET, Field.TYPE_XML).process(this, field, result));
        }
        return result;
    }



    @Override
    public void commit() {
        if (isNew()) {
            cloud.verify(Operation.CREATE, BasicCloudContext.mmb.getTypeDef().getIntValue(getNodeManager().getName()));
        }
        checkCommit();

        Object prev = getCloud().getProperty(CLOUD_COMMITNODE_KEY);
        try {
            getCloud().setProperty(CLOUD_COMMITNODE_KEY, Integer.valueOf(getNumber())); // Validation code wants to know that we are commiting right now.
            Collection<String> errors = validate();
            if (errors.size() > 0) {
                String mes = "node " + getNumber() + noderef.getChanged() + ", builder '" + nodeManager.getName() + "' " + errors.toString();
                if (! Casting.toBoolean(getCloud().getProperty(Cloud.PROP_IGNOREVALIDATION))) {
                    noderef.cancel();
                    throw new IllegalArgumentException(mes);
                }
            }
        } finally {
            getCloud().setProperty(CLOUD_COMMITNODE_KEY, prev);
        }

        cloud.processCommitProcessors(this);
        if (log.isDebugEnabled()) {
            log.debug("committing " + noderef.getChanged() + " " + noderef.getValues());
        }
        // ignore commit in transaction (transaction commits)
        if (!(cloud instanceof Transaction)) { // sigh sigh sigh.
            log.debug("not in a transaction so actually committing now");
            MMObjectNode node = getNode();
            if (isNew()) {
                log.debug("new");
                node.insert(cloud.getUser());
                // cloud.createSecurityInfo(getNumber());
            } else {
                log.debug("not new");
                node.commit(cloud.getUser());
                //cloud.updateSecurityInfo(getNumber());
            }
            // remove the temporary node
            BasicCloudContext.tmpObjectManager.deleteTmpNode(account, "" + temporaryNodeId);
            temporaryNodeId = -1;
        }
    }

    @Override
    public void cancel() {
        checkCommit();
        // when in a transaction, let the transaction cancel
        if (cloud instanceof Transaction) {
            ((Transaction)cloud).cancel();
        } else {
            // remove the temporary node
            BasicCloudContext.tmpObjectManager.deleteTmpNode(account, "" + temporaryNodeId);
            if (isNew()) {
                invalidateNode();
            } else {
                noderef.cancel();
            }
            temporaryNodeId = -1;
        }
    }


    @Override
    public void delete(boolean deleteRelations) {
        checkDelete();
        cloud.processDeleteProcessors(this);
        if (isNew()) {
            // remove from the Transaction
            // note that the node is immediately destroyed !
            // possibly older edits will fail if they refernce this node
            cloud.remove("" + temporaryNodeId);

            // remove a temporary node (no true instantion yet, no relations)
            BasicCloudContext.tmpObjectManager.deleteTmpNode(account, "" + temporaryNodeId);
        } else {
            // remove a node that is edited, i.e. that already exists
            // check relations first!
            if (deleteRelations) {
                // option set, remove relations
                deleteRelations(-1);
            } else {
                // option unset, fail if any relations exit
                if (getNode().hasRelations()) {
                    throw new BridgeException("This node (" + getNode().getNumber() + ") cannot be deleted. It still has relations attached to it.");
                }
            }
            // remove aliases
            deleteAliases(null);
            // in transaction:
            if (cloud instanceof BasicTransaction) {   // WTF WTF
                // let the transaction remove the node (as well as its temporary counterpart).
                // note that the node still exists until the transaction completes
                // a getNode() will still retrieve the node and make edits possible
                // possibly 'older' edits will fail if they reference this node
                ((BasicTransaction)cloud).delete("" + temporaryNodeId);
            } else {
                // remove the node
                if (temporaryNodeId != -1) {
                    BasicCloudContext.tmpObjectManager.deleteTmpNode(account, "" + temporaryNodeId);
                }

                MMObjectNode node = getNode();
                //node.getBuilder().removeNode(node);
                node.remove(cloud.getUser());
                //cloud.removeSecurityInfo(number);
            }
        }
        // the node does not exist anymore, so invalidate all references.
        temporaryNodeId = -1;
        invalidateNode();
    }

    @Override
    public String toString() {
        //return getNode().toString() + "(" + getNode().getClass().getName() + ")";
        return getNode().toString();
        //return "" + super.toString() + " " + getNode().getNumber();
    }

    /**
     * Recursively deletes relations to relations
     * @param relation relation to be removed
     * @since MMBase-1.8.5
     */
    private void deleteRelation(MMObjectNode relation) {
        // first delete Relations to this this relation.
        // SHOULD security not be checked first?
        try {
            for (MMObjectNode subRelation : BasicCloudContext.mmb.getInsRel().getRelationNodes(relation.getNumber(), false)) {
                deleteRelation(subRelation);
            }
        } catch (SearchQueryException sqe) {
            log.error(sqe);
        }
        cloud.remove(relation);
    }


    /**
     * Removes all relations of a certain type.
     *
     * @param type  the type of relation (-1 = don't care)
     */
    private void deleteRelations(int type) {
        List<MMObjectNode> relations;
        try {
            if (type == -1) {
                relations = BasicCloudContext.mmb.getInsRel().getRelationNodes(getNode().getNumber(), false);
            } else {
                relations = BasicCloudContext.mmb.getInsRel().getRelationNodes(getNode().getNumber());
            }
        } catch (SearchQueryException  sqe) {
            log.error(sqe.getMessage()); // should not happen
            return;
        }
        // check first
        checkAccount();
        for (MMObjectNode node : relations) {
            cloud.verify(Operation.DELETE, node.getNumber());
        }

        // then delete
        for (MMObjectNode node : relations) {
            if ((type == -1) || (node.getIntValue("rnumber") == type)) {
                deleteRelation(node);
            }
        }

    }

    @Override
    public void deleteRelations(String type) throws NotFoundException {
        if ("object".equals(type)) {
            deleteRelations(-1);
        }
        else {
            RelDef reldef = BasicCloudContext.mmb.getRelDef();
            int rType = reldef.getNumberByName(type);
            if (rType == -1) {
                throw new NotFoundException("Relation with role : " + type + " does not exist.");
            } else {
                deleteRelations(rType);
            }
        }
    }


    @Override
    public RelationList getRelations(String role, String otherNodeManager) throws NotFoundException {
        if (isNew()) {
            // new nodes have no relations
            return BridgeCollections.EMPTY_RELATIONLIST;
        }

        if ("".equals(otherNodeManager)) otherNodeManager = null;
        NodeManager otherManager = otherNodeManager == null ? cloud.getNodeManager("object") : cloud.getNodeManager(otherNodeManager);

        TypeRel typeRel = BasicCloudContext.mmb.getTypeRel();
        RelationList r1 = BridgeCollections.EMPTY_RELATIONLIST;
        RelationList r2 = BridgeCollections.EMPTY_RELATIONLIST;
        if (role == null) {
            int allowedOtherNumber = "object".equals(otherManager.getName()) ? 0 : otherManager.getNumber();
            if (!typeRel.getAllowedRelations(nodeManager.getNumber(), allowedOtherNumber, 0,
                    RelationStep.DIRECTIONS_DESTINATION).isEmpty())
                r1 = getRelations(role, otherManager, "destination");
            if (!typeRel.getAllowedRelations(nodeManager.getNumber(), allowedOtherNumber, 0,
                    RelationStep.DIRECTIONS_SOURCE).isEmpty())
                r2 = getRelations(role, otherManager, "source");
        }
        else {
            RelDef relDef = BasicCloudContext.mmb.getRelDef();
            int rnumber = relDef.getNumberByName(role);
            if (typeRel.contains(nodeManager.getNumber(), otherManager.getNumber(), rnumber, TypeRel.INCLUDE_PARENTS_AND_DESCENDANTS))
                r1 = getRelations(role, otherManager, "destination");
            if (typeRel.contains(otherManager.getNumber(), nodeManager.getNumber(), rnumber, TypeRel.INCLUDE_PARENTS_AND_DESCENDANTS))
                r2 = getRelations(role, otherManager, "source");
        }


        if (r2.size() == 0) {
            return r1;
        } else if (r1.size() == 0) {
            return r2;
        } else {
            // perhaps it would be better for performance to have some 'ChainedRelationList' implementation.
            RelationList result = cloud.getCloudContext().createRelationList();
            result.addAll(r1);
            result.addAll(r2);
            return result;
        }
    }

    /**
     * Returns a list of relations of the given node.
     * @param role role of the relation
     * @param nodeManager node manager on the other side of the relation
     * @param searchDir direction of the relation
     * @return list of relations
     * @throws NotFoundException
     *
     * See {@link Queries#createRelationNodesQuery(Node, NodeManager, String, String)}
     *  Should perhaps be implemented with that
     */
    @Override
    public RelationList getRelations(String role, NodeManager nodeManager, String searchDir) throws NotFoundException {
        if (isNew()) {
            // new nodes have no relations
            return org.mmbase.bridge.util.BridgeCollections.EMPTY_RELATIONLIST;
        }
        if (searchDir == null || "BOTH".equalsIgnoreCase(searchDir)) return getRelations(role, nodeManager);
        if (nodeManager == null) nodeManager = cloud.getNodeManager("object");
        NodeQuery query = Queries.createRelationNodesQuery(this, nodeManager, role, searchDir);
        NodeManager nm = query.getNodeManager();
        assert query.getNodeStep() instanceof RelationStep;
        // assert nm instanceof RelationManager; cannot assert his, because if the role is null, no relation manager can be created (the nodemanager will be insrel).
        return new CollectionRelationList(nm.getList(query), cloud);
    }

    @Override
    public boolean hasRelations() {
        return getNode().hasRelations();
    }


    @Override
    public int countRelatedNodes(NodeManager otherNodeManager, String role, String direction) {
        if (isNew()) return 0;
        if (otherNodeManager == null || otherNodeManager.getName().equals("object")) {
            // can be done on only insrel, which is often much quicker.
            NodeManager insrel;
            if (role != null) {
                insrel = cloud.getRelationManager(role);
            } else {
                insrel = cloud.getNodeManager("insrel");
            }
            NodeQuery query = insrel.createQuery();

            if (insrel instanceof BasicRelationManager) {
                MMObjectNode relDefNode = ((BasicRelationManager) insrel).relDefNode;
                if (relDefNode != null) {
                    StepField rnumber = query.getStepField(insrel.getField("rnumber"));
                    query.setConstraint(query.createConstraint(rnumber, Integer.valueOf(relDefNode.getNumber())));
                }
            }

            int dir = RelationStep.DIRECTIONS_BOTH;
            if (direction != null) {
                dir = ClusterBuilder.getSearchDir(direction);
            }

            StepField snumber = query.getStepField(insrel.getField("snumber"));
            StepField dnumber = query.getStepField(insrel.getField("dnumber"));

            Integer number = Integer.valueOf(getNumber());

            switch(dir) {
            case RelationStep.DIRECTIONS_DESTINATION: {
                Queries.addConstraint(query, query.createConstraint(snumber, number));
                break;
            }
            case RelationStep.DIRECTIONS_SOURCE: {
                Queries.addConstraint(query, query.createConstraint(dnumber, number));
                break;
            }
            case RelationStep.DIRECTIONS_BOTH:
            case RelationStep.DIRECTIONS_EITHER: {
                Constraint sourceConstraint = query.createConstraint(snumber, number);
                Constraint destinationConstraint = query.createConstraint(dnumber, number);
                Queries.addConstraint(query, query.createConstraint(sourceConstraint, CompositeConstraint.LOGICAL_OR, destinationConstraint));
                break;
            }
            default:
                log.debug("Unknown relation direction" + dir);
                break;
            }
            return Queries.count(query);
        } else {
            BasicQuery count = (BasicQuery) cloud.createAggregatedQuery();
            count.addStep(nodeManager);
            Step step = count.addRelationStep(otherNodeManager, role, direction, false).getPrevious();
            count.addNode(step, this);
            count.addAggregatedField(step, nodeManager.getField("number"), AggregatedField.AGGREGATION_TYPE_COUNT);
            Node result = cloud.getList(count).get(0);
            return result.getIntValue("number");
        }
    }

    /**
     * Get related nodes for this node
     * @param otherManager nodemanager on the other side of the relation
     * @param role name of the relation
     * @return List of related nodes
     * @since MMBase-1.8.2
     */
    protected NodeList getRelatedNodes(NodeManager otherManager, String role) {

        NodeList l1 = BridgeCollections.EMPTY_NODELIST;
        NodeList l2 = BridgeCollections.EMPTY_NODELIST;

        TypeRel typeRel = BasicCloudContext.mmb.getTypeRel();
        if (role == null) {
            int allowedOtherNumber = otherManager == null || "object".equals(otherManager.getName()) ? 0 : otherManager.getNumber();
            if (!typeRel.getAllowedRelations(nodeManager.getNumber(), allowedOtherNumber, 0,
                    RelationStep.DIRECTIONS_DESTINATION).isEmpty())
                l1 = getRelatedNodes(otherManager, role, "destination");
            if (!typeRel.getAllowedRelations(nodeManager.getNumber(), allowedOtherNumber, 0,
                    RelationStep.DIRECTIONS_SOURCE).isEmpty())
                l2 = getRelatedNodes(otherManager, role, "source");
        }
        else {
            RelDef relDef = BasicCloudContext.mmb.getRelDef();
            int rnumber = relDef.getNumberByName(role);
            if (typeRel.contains(nodeManager.getNumber(), otherManager.getNumber(), rnumber, TypeRel.INCLUDE_PARENTS_AND_DESCENDANTS))
                l1 = getRelatedNodes(otherManager, role, "destination");
            if (typeRel.contains(otherManager.getNumber(), nodeManager.getNumber(), rnumber, TypeRel.INCLUDE_PARENTS_AND_DESCENDANTS))
                l2 = getRelatedNodes(otherManager, role, "source");
        }
        if (l2.size() == 0) {
            return l1;
        } else if (l1.size() == 0) {
            return l2;
        } else {
            // perhaps it would be better for performance to have some 'ChainedRelationList' implementation.
            NodeList result = cloud.getCloudContext().createNodeList();
            result.addAll(l1);
            result.addAll(l2);
            return result;
        }
    }
    /**
     * @param otherManager node manager on the other side of the relation
     * @param role role of the relation
     * @param searchDir direction of the relation
     * @return List of related nodes
     * @see Queries#createRelatedNodesQuery Should perhaps be implemented with that.
     * @since MMBase-1.6
     */
    @Override
    public NodeList getRelatedNodes(NodeManager otherManager, String role, String searchDir) {
        if (log.isDebugEnabled()) {
            log.debug("type(" + otherManager.getName() + "), role(" + role + "), dir(" + searchDir + ")");
        }
        if (isNew()) {
            // new nodes have no relations
            return org.mmbase.bridge.util.BridgeCollections.EMPTY_NODELIST;
        }
        if (searchDir == null) searchDir = "BOTH";
        if ("BOTH".equalsIgnoreCase(searchDir)) {
            return getRelatedNodes(otherManager, role);
        }
        NodeQuery query = Queries.createRelatedNodesQuery(this, otherManager, role, searchDir);
        return query.getNodeManager().getList(query);
    }

    @Override
    public int countRelatedNodes(String type) {
        if (isNew()) return 0;
        return getNode().getRelationCount(type);
    }

    @Override
    public StringList getAliases() {
        NodeManager oalias = cloud.getNodeManager("oalias");
        NodeQuery q = oalias.createQuery();
        Constraint c = q.createConstraint(q.getStepField(oalias.getField("destination")), Integer.valueOf(getNumber()));
        q.setConstraint(c);
        NodeList aliases = oalias.getList(q);
        StringList result = new BasicStringList();
        for (Node alias : aliases) {
            result.add(alias.getStringValue("name"));
        }
        if (isNew()) {
            // for bug #6185.  MMB-617

            if (getNode().aliases != null) {
                result.addAll(getNode().aliases);
            }
        }


        return result;
    }


    @Override
    public void createAlias(String aliasName) {
        checkWrite();
        if (isNew()) {
            cloud.checkAlias(aliasName);
            getNode().setAlias(aliasName);
        } else {
            cloud.createAlias(this, aliasName);
        }
    }

    /**
     * Delete one or all aliases of this node
     * @param aliasName the name of the alias (null means all aliases)
     */
    private void deleteAliases(String aliasName) {
        // A new node cannot have any aliases, except when in a transaction.
        // However, there is no point in adding aliasses to a ndoe you plan to delete,
        // so no attempt has been made to rectify this (cause its not worth all the trouble).
        // If people remove a node for which they created aliases in the same transaction, that transaction will fail.
        // Live with it.
        if (!isNew()) {
            NodeManager oalias = cloud.getNodeManager("oalias");
            NodeQuery q = oalias.createQuery();
            Constraint c = q.createConstraint(q.getStepField(oalias.getField("destination")), Integer.valueOf(getNumber()));
            if (aliasName != null) {
                Constraint c2 = q.createConstraint(q.getStepField(oalias.getField("name")), aliasName);
                c = q.createConstraint (c, CompositeConstraint.LOGICAL_AND, c2);
            }
            q.setConstraint(c);
            NodeList aliases = oalias.getList(q);
            NodeIterator i = aliases.nodeIterator();
            while (i.hasNext()) {
                Node alias = i.nextNode();
                alias.delete();
            }
        }
    }

    @Override
    public void deleteAlias(String aliasName) {
        checkWrite();
        deleteAliases(aliasName);
    }


    // javadoc inherited (from Node)
    @Override
    public void setContext(String context) {
        if (getNode().getNumber() > -1) {
            cloud.verify(Operation.CHANGE_CONTEXT, getNode().getNumber());
        } else {
            //TODO
        }
        // set the context on the node (run after insert).
        getNode().setContext(cloud.getUser(), context, temporaryNodeId == -1);
    }

    // javadoc inherited (from Node)
    @Override
    public String getContext() {
        return getNode().getContext(cloud.getUser());
    }


    // javadoc inherited (from Node)
    @Override
    public StringList getPossibleContexts() {
        return new BasicStringList(getNode().getPossibleContexts(cloud.getUser()));
    }

    @Override
    public boolean mayWrite() {
        return isNew() || cloud.check(Operation.WRITE, getNode().getNumber());
    }

    @Override
    public boolean mayDelete() {
        return isNew() || cloud.check(Operation.DELETE, getNode().getNumber());
    }

    @Override
    public boolean mayChangeContext() {
        return isNew() || cloud.check(Operation.CHANGE_CONTEXT, getNode().getNumber());
    }

    /**
     * Reverse the buffers, when changed and not stored...
     */
    @Override
    protected void finalize() {
        // When not commit-ed or cancelled, and the buffer has changed, the changes must be reversed.
        // when not done it results in node-lists with changes which are not performed on the database...
        // This is all due to the fact that Node doesnt make a copy of MMObjectNode, while editing...
        // my opinion is that this should happen, as soon as edit-ting starts,..........
        // when still has modifications.....
        if (isChanged()) {
            if (!(cloud instanceof Transaction)) {
                // cancel the modifications...
                cancel();
            }
        }
    }

    public Collection<Function<?>> getFunctions() {
        return  getNode().getFunctions();
    }

    @Override
    protected Function<?> getNodeFunction(String functionName) {
        return getNode().getFunction(functionName);
    }


    @Override
    public Parameters createParameters(String functionName) {
        return getNode().getFunction(functionName).createParameters();
    }
    @Override
    protected FieldValue createFunctionValue(Object result) {
        return new BasicFunctionValue(getCloud(), result);
    }

      /*
    public Object getOldValue(String fieldName) {
        return getNode().getOldValues().get(fieldName);
    }
    */

}
