/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;

import java.util.*;
import java.io.*;

import org.mmbase.security.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.datatypes.DataType;
import org.mmbase.storage.search.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.*;

import org.w3c.dom.Element;
import org.w3c.dom.Document;

/**
 * Basic implementation of Node. Wraps MMObjectNodes, adds security.
 *
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @version $Id: BasicNode.java,v 1.188 2005-12-27 22:15:37 michiel Exp $
 * @see org.mmbase.bridge.Node
 * @see org.mmbase.module.core.MMObjectNode
 */
public class BasicNode extends org.mmbase.bridge.util.AbstractNode implements Node, Comparable, SizeMeasurable {


    private static final Logger log = Logging.getLoggerInstance(BasicNode.class);

    private boolean changed = false;

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
     * (a temporary number assigend by the system).
     */
    private int temporaryNodeId = -1;

    /**
     * The account this node is edited under.
     * This is needed to check whether people have not switched users during an edit.
     */
    private String account = null;

    /**
     * Determines whether this node was created for insert.
     */
    private boolean isNew = false;


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
        isNew = true;
        init();
        edit(ACTION_CREATE);
    }

    /**
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
        if (cloud.contains(getNode()) && temporaryNodeId == -1) {
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
        changed = false;
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
    public boolean isNew() {
        return isNew;
    }

    public boolean isChanged(String fieldName) {
        return getNode().getChanged().contains(fieldName);
    }

    public boolean isChanged() {
        return getNode().isChanged();
    }

    /**
     * Edit this node.
     * Check whether edits are allowed and prepare a node for edits if needed.
     * The type of edit is determined by the action specified, and one of:<br />
     * ACTION_CREATE (create a node),<br />
     * ACTION_EDIT (edit node, or change aliasses),<br />
     * ACTION_DELETE (delete node),<br />
     * ACTION_COMMIT (commit a node after changes)
     *
     * @param action The action to perform.
     */
    protected void edit(int action) {
        if (account == null) {
            account = cloud.getAccount();
        } else if (!account.equals(cloud.getAccount())) {
            throw new BridgeException("User context changed. Cannot proceed to edit this node .");
        }

        int realnumber = getNode().getNumber();
        if (realnumber != -1) {
            if (action == ACTION_DELETE) {
                cloud.verify(Operation.DELETE, realnumber);
            } else if ((action == ACTION_EDIT) && (temporaryNodeId == -1)) {
                cloud.verify(Operation.WRITE, realnumber);
            }
        }

        // check for the existence of a temporary node
        if (temporaryNodeId == -1) {
            // when committing a temporary node id must exist (otherwise fail).
            if (action == ACTION_COMMIT) {
                // throw new BasicBridgeException("This node cannot be comitted (not changed).");
            }
            // when adding a temporary node id must exist (otherwise fail).
            // this should not occur (hence internal error notice), but we test it anyway.

            if (action == ACTION_CREATE) {
                String message = "This node cannot be added. It was not correctly instantiated (internal error).";
                log.error(message);
                throw new BridgeException(message);
            }

            // when editing a temporary node id must exist (otherwise create one)
            // This also applies if you remove a node in a transaction (as the transction manager requires a temporary node)
            //
            // XXX: If you edit a node outside a transaction, but do not commit or cancel the edits,
            // the temporarynode will not be removed. This is left to be fixed (i.e.through a time out mechanism?)
            if ((action == ACTION_EDIT) || ((action == ACTION_DELETE) && (getCloud() instanceof BasicTransaction))) {
                int id = getNumber();
                String currentObjectContext = BasicCloudContext.tmpObjectManager.getObject(account, "" + id, "" + id);
                // store new temporary node in transaction
                cloud.add(currentObjectContext);
                setNode(BasicCloudContext.tmpObjectManager.getNode(account, "" + id));
                //  check nodetype afterwards?
                temporaryNodeId = id;
            }
        }
    }

    /**
     * Protected method to be able to set rnumber when creating a relation.
     * @param fieldName name of field
     * @param value new value of field
     * @since MMBase-1.7
     */
    protected void setValueWithoutChecks(String fieldName, Object value) {
        String result = BasicCloudContext.tmpObjectManager.setObjectField(account, "" + temporaryNodeId, fieldName, value);
        if (TemporaryNodeManager.UNKNOWN == result) {
            throw new BridgeException("Can't change unknown field '" + fieldName + "', of node " + getNumber() + " of nodeManager '" + getNodeManager().getName() +"'");
        }
        changed = true;
    }
    protected Integer toNodeNumber(Object v) {
        if (v == null) {
            return null;
        } else if (v instanceof Node) {
            return new Integer(((Node)v).getNumber());
        } else if (v instanceof MMObjectNode) {
            return new Integer(((MMObjectNode)v).getNumber());
        } else {
            // giving up
            return new Integer(cloud.getNode(v.toString()).getNumber());
        }
    }

    protected void setSize(String fieldName, long size) {
        getNode().setSize(fieldName, size);
    }

    public boolean isNull(String fieldName) {
        return noderef.isNull(fieldName);
    }

    public long getSize(String fieldName) {
        return noderef.getSize(fieldName);
    }

    /**
     * Like getObjectValue, but skips any processing that MMBase would normally perform on a field.
     * You can use this to get data from a field for validation purposes.
     * @param fieldName name of field
     * @since MMBase-1.8
     */
    public Object getValueWithoutProcess(String fieldName) {
        Object result = getNode().getValue(fieldName);
        if (result instanceof MMObjectNode) {
            MMObjectNode mmnode = (MMObjectNode) result;
            result = cloud.makeNode(mmnode, "" + mmnode.getNumber());
        }
        return result;
    }
    //TODO, silly get-methods could be moved to AbstractNode, (calling getValueWithoutProcess) but they depend on noderef now, so I
    //don't dare do that right ahead.

    public boolean getBooleanValue(String fieldName) {
        Boolean result = Boolean.valueOf(noderef.getBooleanValue(fieldName));
        if (nodeManager.hasField(fieldName)) { // gui(..) stuff could not work.
            Field field = nodeManager.getField(fieldName);
            result = (Boolean) field.getDataType().getProcessor(DataType.PROCESS_GET, Field.TYPE_BOOLEAN).process(this, field, result);
        }
        return result.booleanValue();
    }

    public Date getDateValue(String fieldName) {
        Date result =  noderef.getDateValue(fieldName);
        if (nodeManager.hasField(fieldName)) { // gui(..) stuff could not work.
            Field field = nodeManager.getField(fieldName);
            result = (Date) field.getDataType().getProcessor(DataType.PROCESS_GET, Field.TYPE_DATETIME).process(this, field, result);
        }
        return result;
    }

    public List getListValue(String fieldName) {
        List result =  noderef.getListValue(fieldName);
        if (nodeManager.hasField(fieldName)) { // gui(..) stuff could not work.
            Field field = nodeManager.getField(fieldName);
            result = (List) field.getDataType().getProcessor(DataType.PROCESS_GET, Field.TYPE_LIST).process(this, field, result);
        }

        return result;
    }


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
                result = new BasicNode(mmobjectNode, cloud); //.getNodeManager(noderes.getBuilder().getTableName()));
            }
        }
        if (nodeManager.hasField(fieldName)) { // only if this is actually a field of this node-manager, otherewise it might be e.g. a request for an 'element' of a cluster node
            Field field = nodeManager.getField(fieldName);
            result = (Node) field.getDataType().getProcessor(DataType.PROCESS_GET, Field.TYPE_NODE).process(this, field, result);
        }

        return result;
    }

    public int getIntValue(String fieldName) {
        Integer result = new Integer(getNode().getIntValue(fieldName));
        if (nodeManager.hasField(fieldName)) { // gui(..) stuff could not work.
            Field field = nodeManager.getField(fieldName);
            result = (Integer) field.getDataType().getProcessor(DataType.PROCESS_GET, Field.TYPE_INTEGER).process(this, field, result);
        }
        return result.intValue();

    }

    public float getFloatValue(String fieldName) {
        Float result = new Float(getNode().getFloatValue(fieldName));
        if (nodeManager.hasField(fieldName)) { // gui(..) stuff could not work.
            Field field = nodeManager.getField(fieldName);
            result = (Float) field.getDataType().getProcessor(DataType.PROCESS_GET, Field.TYPE_FLOAT).process(this, field, result);
        }
        return result.floatValue();
    }

    public long getLongValue(String fieldName) {
        Long result = new Long(getNode().getLongValue(fieldName));
        if (nodeManager.hasField(fieldName)) { // gui(..) stuff could not work.
            Field field = nodeManager.getField(fieldName);
            result = (Long) field.getDataType().getProcessor(DataType.PROCESS_GET, Field.TYPE_LONG).process(this, field, result);
        }
        return result.longValue();
    }

    public double getDoubleValue(String fieldName) {
        Double result = new Double(getNode().getDoubleValue(fieldName));
        if (nodeManager.hasField(fieldName)) { // gui(..) stuff could not work.
            Field field = nodeManager.getField(fieldName);
            result = (Double) field.getDataType().getProcessor(DataType.PROCESS_GET, Field.TYPE_DOUBLE).process(this, field, result);
        }
        return result.doubleValue();
    }

    public byte[] getByteValue(String fieldName) {
        byte[] result = getNode().getByteValue(fieldName);
        if (nodeManager.hasField(fieldName)) { // gui(..) stuff could not work.
            Field field = nodeManager.getField(fieldName);
            result = (byte[]) field.getDataType().getProcessor(DataType.PROCESS_GET, Field.TYPE_BINARY).process(this, field, result);
        }
        return result;
    }
    public java.io.InputStream getInputStreamValue(String fieldName) {
        java.io.InputStream result = getNode().getInputStreamValue(fieldName);
        if (nodeManager.hasField(fieldName)) { // gui(..) stuff could not work.
            Field field = nodeManager.getField(fieldName);
            result = (java.io.InputStream) field.getDataType().getProcessor(DataType.PROCESS_GET, Field.TYPE_BINARY).process(this, field, result);
        }
        return result;
    }

    public String getStringValue(String fieldName) {
        String result = getNode().getStringValue(fieldName);
        if (nodeManager.hasField(fieldName)) { // gui(..) stuff could not work.
            Field field = nodeManager.getField(fieldName);
            result = (String) field.getDataType().getProcessor(DataType.PROCESS_GET, Field.TYPE_STRING).process(this, field, result);
        }
        return result;
    }

    public Document getXMLValue(String fieldName) {
        Document result = getNode().getXMLValue(fieldName);
        if (nodeManager.hasField(fieldName)) { // gui(..) stuff could not work.
            Field field = nodeManager.getField(fieldName);
            result = (Document) field.getDataType().getProcessor(DataType.PROCESS_GET, Field.TYPE_XML).process(this, field, result);
        }
        return result;
    }



    public void commit() {
        if (isNew) {
            cloud.verify(Operation.CREATE, BasicCloudContext.mmb.getTypeDef().getIntValue(getNodeManager().getName()));
        }
        edit(ACTION_COMMIT);
        processCommit();
        log.info("committing " + noderef.getChanged());
        Collection errors = validate();
        if (errors.size() > 0) {
            throw new IllegalArgumentException("node " + getNumber() + noderef.getChanged() + ", builder '" + nodeManager.getName() + "' " + errors.toString());
        }
        // ignore commit in transaction (transaction commits)
        if (!(cloud instanceof Transaction)) { // sigh sigh sigh.
            MMObjectNode node = getNode();
            if (isNew) {
                node.insert(cloud.getUser());
                // cloud.createSecurityInfo(getNumber());
                isNew = false;
            } else {
                node.commit(cloud.getUser());
                //cloud.updateSecurityInfo(getNumber());
            }
            // remove the temporary node
            BasicCloudContext.tmpObjectManager.deleteTmpNode(account, "" + temporaryNodeId);
            temporaryNodeId = -1;
            // invalid nodereference, so retrieve node anew
            MMObjectNode newNode = BasicCloudContext.mmb.getTypeDef().getNode(node.getNumber());
            if (newNode == null) {
                throw new RuntimeException("Could not find node " + node.getNumber());
            }
            log.info("Found new node after commit " + newNode);
            setNode(newNode);
        }
        changed = false;
    }

    public void cancel() {
        edit(ACTION_COMMIT);
        // when in a transaction, let the transaction cancel
        if (cloud instanceof Transaction) {
            ((Transaction)cloud).cancel();
        } else {
            // remove the temporary node
            BasicCloudContext.tmpObjectManager.deleteTmpNode(account, "" + temporaryNodeId);
            if (isNew) {
                isNew = false;
                invalidateNode();
            } else {
                // update the node, reset fields etc...
                setNode(BasicCloudContext.mmb.getTypeDef().getNode(noderef.getNumber()));
            }
            temporaryNodeId = -1;
        }
        changed = false;
    }


    public void delete(boolean deleteRelations) {
        edit(ACTION_DELETE);
        if (isNew) {
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
            cloud.delete(temporaryNodeId, getNode());
        }
        // the node does not exist anymore, so invalidate all references.
        temporaryNodeId = -1;
        invalidateNode();
    }

    public String toString() {
        //return getNode().toString() + "(" + getNode().getClass().getName() + ")";
        //return getNode().toString();
        return "" + super.toString() + " " + getNode().getNumber();
    }

    /**
     * Removes all relations of a certain type.
     *
     * @param type  the type of relation (-1 = don't care)
     */
    private void deleteRelations(int type) {
        List relations = null;
        try {
            if (type == -1) {
                relations = BasicCloudContext.mmb.getInsRel().getRelationNodes(getNode().getNumber(), false);
            } else {
                relations = BasicCloudContext.mmb.getInsRel().getRelationNodes(getNode().getNumber());
            }
        } catch (SearchQueryException  sqe) {
            log.error(sqe.getMessage()); // should not happen
        }
        if (relations != null) {
            // check first
            for (Iterator i = relations.iterator(); i.hasNext();) {
                MMObjectNode node = (MMObjectNode)i.next();
                cloud.verify(Operation.DELETE, node.getNumber());
            }
            // then delete
            for (Iterator i = relations.iterator(); i.hasNext();) {
                MMObjectNode node = (MMObjectNode)i.next();
                if ((type == -1) || (node.getIntValue("rnumber") == type)) {
                    if (cloud instanceof Transaction) {
                        String oMmbaseId = "" + node.getValue("number");
                        String currentObjectContext = BasicCloudContext.tmpObjectManager.getObject(account, "" + oMmbaseId, oMmbaseId);
                        cloud.add(currentObjectContext);
                        cloud.delete(currentObjectContext);
                    } else {
                        node.remove(cloud.getUser());
                    }
                }
            }
        }
    }

    public void deleteRelations(String type) throws NotFoundException {
        RelDef reldef = BasicCloudContext.mmb.getRelDef();
        int rType = reldef.getNumberByName(type);
        if (rType == -1) {
            throw new NotFoundException("Relation with role : " + type + " does not exist.");
        } else {
            deleteRelations(rType);
        }
    }

    /**
     * Returns an enumeration of MMObjectNodes, which respresent relations of this node
     * with specified role and otype
     * @param role the role (reldef) number, can be -1
     * @param otype the destination object type number, can be -1
     * @param usedirectionality if <code>true</code> teh result si filtered on unidirectional relations.
     *                          specify <code>false</code> if you want to show unidoerctional relations
     *                          from destination to source.
     * @return an Enumeration with the relations
     */
    private Enumeration getRelationEnumeration(int role, int otype, boolean usedirectionality) {
        InsRel relbuilder = BasicCloudContext.mmb.getInsRel();
        if ((role != 1) || (otype != -1)) {
            if (role != -1) {
                relbuilder = BasicCloudContext.mmb.getRelDef().getBuilder(role);
            }
            return relbuilder.getRelations(getNumber(), otype, role, usedirectionality);
        } else {
            return getNode().getRelations();
        }
    }

    /**
     * Returns a list of Relation objects, which represent relations of this node
     * with specified role and otype
     * @param role the role (reldef) number, can be -1
     * @param otype the destination object type number, can be -1
     * @return a RelationList with the relations
     */
    private RelationList getRelations(int role, int otype) {
        Enumeration e = getRelationEnumeration(role, otype, true);
        List relvector = new ArrayList();
        if (e != null) {
            while (e.hasMoreElements()) {
                MMObjectNode mmnode = (MMObjectNode)e.nextElement();
                if (cloud.check(Operation.READ, mmnode.getNumber())) {
                    relvector.add(mmnode);
                }
            }
        }
        return new BasicRelationList(relvector, cloud);
    }

    public RelationList getRelations(String role, String nodeManager) throws NotFoundException {
        int otype = -1;
        if (nodeManager != null) {
            otype = BasicCloudContext.mmb.getTypeDef().getIntValue(nodeManager);
            if (otype == -1) {
                throw new NotFoundException("NodeManager " + nodeManager + " does not exist.");
            }
        }
        int rolenr = -1;
        if (role != null) {
            rolenr = BasicCloudContext.mmb.getRelDef().getNumberByName(role);
            if (rolenr == -1) {
                throw new NotFoundException("Relation with role " + role + " does not exist.");
            }
        }
        return getRelations(rolenr, otype);
    }

    /**
     * Returns a list of relations of the given node.
     * @param role role of the relation
     * @param nodeManager node manager on the other side of the relation
     * @param searchDir direction of the relation
     * @return list of relations
     * @throws NotFoundException
     *
     * @see Queries#createRelationNodesQuery Should perhaps be implemented with that
     */
    public RelationList getRelations(String role, NodeManager nodeManager, String searchDir) throws NotFoundException {
        // temporay implementation to get it working for now. Really we would want to make separate queries, I think.

        RelationList  relations = getRelations(role, nodeManager);

        int dir = RelationStep.DIRECTIONS_BOTH;
        if (searchDir != null) {
            dir = ClusterBuilder.getSearchDir(searchDir);
        }
        if (dir == RelationStep.DIRECTIONS_BOTH) return relations;

        RelationIterator it = relations.relationIterator();

        RelationList result = cloud.createRelationList();

        while (it.hasNext()) {
            Relation relation = it.nextRelation();
            switch(dir) {
            case RelationStep.DIRECTIONS_DESTINATION:
                if(relation.getSource().getNumber() == getNumber()) {
                    result.add(relation);
                }
                break;
            case RelationStep.DIRECTIONS_SOURCE:
                if(relation.getDestination().getNumber() == getNumber()) {
                    result.add(relation);
                }
                break;
            default:
                result.add(relation); // er..
            }
        }
        return result;
    }

    public boolean hasRelations() {
        return getNode().hasRelations();
    }


    public int countRelatedNodes(NodeManager otherNodeManager, String role, String direction) {
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
                    query.setConstraint(query.createConstraint(rnumber, new Integer(relDefNode.getNumber())));
                }
            }

            int dir = RelationStep.DIRECTIONS_BOTH;
            if (direction != null) {
                dir = ClusterBuilder.getSearchDir(direction);
            }

            StepField snumber = query.getStepField(insrel.getField("snumber"));
            StepField dnumber = query.getStepField(insrel.getField("dnumber"));

            Integer number = new Integer(getNumber());

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
            Node result = (Node) cloud.getList(count).get(0);
            return result.getIntValue("number");
        }
    }


    /**
     * @param nodeManager node manager on the other side of the relation
     * @param role role of the relation
     * @param searchDir direction of the relation
     * @return List of related nodes
     * @see Queries#createRelatedNodesQuery Should perhaps be implemented with that.
     * @since MMBase-1.6
     */
    public NodeList getRelatedNodes(NodeManager nodeManager, String role, String searchDir) {
        if (log.isDebugEnabled()) {
            log.debug("type(" + nodeManager.getName() + "), role(" + role + "), dir(" + searchDir + ")");
        }

        // default directionalty to query for the bridge is SEARCH_BOTH;
        // SEARCH_EITHER is intended for SCAN - unfortunately, since SCAN does not provide directionality,
        // the Clusterbuidler has to assume SEARCH_EITHER as a default.
        // therefor we have to set SEARCH_BOTH manually
        int dir = RelationStep.DIRECTIONS_BOTH;
        if (searchDir != null) {
            dir = ClusterBuilder.getSearchDir(searchDir);
        }
        // call list: note: role can be null
        // XXX. Should perhaps not depend on core's getRelatedNodes becasue then the query remains unknown

        List mmnodes = isNew()
            ? new Vector()  // new nodes have no relations
            : getNode().getRelatedNodes((nodeManager != null ? nodeManager.getName() : null), role, dir);

        // remove the elements which may not be read:
        ListIterator li = mmnodes.listIterator();
        while (li.hasNext()) {
            MMObjectNode node = (MMObjectNode)li.next();
            if (!cloud.check(Operation.READ, node.getNumber())) {
                li.remove();
            }
        }
        if (nodeManager != null) {
            return new BasicNodeList(mmnodes, nodeManager);
        } else {
            return new BasicNodeList(mmnodes, cloud);
        }
    }

    public int countRelatedNodes(String type) {
        return getNode().getRelationCount(type);
    }

    public StringList getAliases() {
        NodeManager oalias = cloud.getNodeManager("oalias");
        NodeQuery q = oalias.createQuery();
        Constraint c = q.createConstraint(q.getStepField(oalias.getField("destination")), new Integer(getNumber()));
        q.setConstraint(c);
        NodeList aliases = oalias.getList(q);
        StringList result = new BasicStringList();
        NodeIterator i = aliases.nodeIterator();
        while (i.hasNext()) {
            Node alias = i.nextNode();
            result.add(alias.getStringValue("name"));
        }

        // There might be aliases in temporary nodes
        // This is quite a dirty (and probably also slow) hack
        // for bug #6185.
        // Usually the temporaryNodes hashtable shall not be
        // too full.
        if (cloud instanceof Transaction) {
            Map tnodes = MMObjectBuilder.temporaryNodes;
            for (Iterator e = tnodes.values().iterator(); e.hasNext();) {
                MMObjectNode mynode = (MMObjectNode)e.next();
                if (mynode.getName().equals("oalias")){
                    String dest = mynode.getStringValue("_destination");
                    if ((account + "_" + temporaryNodeId).equals(dest)) {
                        result.add(mynode.getStringValue("name"));
                    }
                }
            }
        }

        return result;
    }

    public void createAlias(String aliasName) {
        edit(ACTION_EDIT);
        if (cloud instanceof Transaction) {
            String aliasContext = BasicCloudContext.tmpObjectManager.createTmpAlias(aliasName, account, "a" + temporaryNodeId, "" + temporaryNodeId);
            ((BasicTransaction)cloud).add(aliasContext); // sigh
        } else if (isNew) {
            throw new BridgeException("Cannot add alias to a new node that has not been committed.");
        } else {
            if (!getNode().getBuilder().createAlias(getNumber(), aliasName)) {
                Node otherNode = cloud.getNode(aliasName);
                if (otherNode != null) {
                    throw new BridgeException("Alias " + aliasName + " could not be created. It is an alias for " + otherNode.getNodeManager().getName() + " node " + otherNode.getNumber() + " already");
                } else {
                    throw new BridgeException("Alias " + aliasName + " could not be created.");
                }
            }
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
        if (!isNew) {
            NodeManager oalias = cloud.getNodeManager("oalias");
            NodeQuery q = oalias.createQuery();
            Constraint c = q.createConstraint(q.getStepField(oalias.getField("destination")), new Integer(getNumber()));
            if (aliasName != null) {
                Constraint c2 = q.createConstraint(q.getStepField(oalias.getField("name")), aliasName);
                c = q.createConstraint (c,CompositeConstraint.LOGICAL_AND,c2);
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

    public void deleteAlias(String aliasName) {
        edit(ACTION_EDIT);
        deleteAliases(aliasName);
    }


    // javadoc inherited (from Node)
    public void setContext(String context) {
        // set the context on the node (run after insert).
        getNode().setContext(cloud.getUser(), context, temporaryNodeId == -1);
        changed = true;
    }

    // javadoc inherited (from Node)
    public String getContext() {
        return getNode().getContext(cloud.getUser());
    }


    // javadoc inherited (from Node)
    public StringList getPossibleContexts() {
        return new BasicStringList(getNode().getPossibleContexts(cloud.getUser()));
    }

    public boolean mayWrite() {
        return isNew || cloud.check(Operation.WRITE, getNode().getNumber());
    }

    public boolean mayDelete() {
        return isNew || cloud.check(Operation.DELETE, getNode().getNumber());
    }

    public boolean mayChangeContext() {
        return isNew || cloud.check(Operation.CHANGE_CONTEXT, getNode().getNumber());
    }

    /**
     * Reverse the buffers, when changed and not stored...
     */
    protected void finalize() {
        // When not commit-ed or cancelled, and the buffer has changed, the changes must be reversed.
        // when not done it results in node-lists with changes which are not performed on the database...
        // This is all due to the fact that Node doesnt make a copy of MMObjectNode, while editing...
        // my opinion is that this should happen, as soon as edit-ting starts,..........
        // when still has modifications.....
        if (changed) {
            if (!(cloud instanceof Transaction)) {
                // cancel the modifications...
                cancel();
            }
        }
    }


    /**
     * @see java.lang.Object#hashCode()
     *
     * @since MMBase-1.6.2
     */
    public int hashCode() {
        return getNode().hashCode();
    }

    public Collection  getFunctions() {
        return  getNode().getFunctions();
    }

    public Function getFunction(String functionName) {
        Function function = getNode().getFunction(functionName);
        if (function == null) {
            throw new NotFoundException("Function with name " + functionName + " does not exist on node " + getNode().getNumber() + " of type " + getNodeManager().getName() + "(known are " + getNode().getBuilder().getFunctions() + ")");
        }
        return new WrappedFunction(function) {
                public final Object getFunctionValue(Parameters params) {
                    params.set(Parameter.NODE, BasicNode.this);
                    params.set(Parameter.CLOUD, BasicNode.this.cloud);
                    return super.getFunctionValue(params);

                }
            };
    }

    public FieldValue getFunctionValue(String functionName, List parameters) {
         Function function = getFunction(functionName);
         Parameters params = function.createParameters();
         params.setAll(parameters);
         return new BasicFunctionValue(getCloud(), function.getFunctionValue(params));
    }

    public Parameters createParameters(String functionName) {
        return getNode().getFunction(functionName).createParameters();
    }

}
