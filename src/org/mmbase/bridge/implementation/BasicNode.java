/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;

import org.mmbase.bridge.util.xml.DocumentConverter;
import java.util.*;
import org.mmbase.security.*;
import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.SizeMeasurable;
import org.mmbase.util.SizeOf;

import org.w3c.dom.Element;
import org.w3c.dom.Document;

/**
 * Basic implementation of Node. Wraps MMObjectNodes, adds security.
 *
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @version $Id: BasicNode.java,v 1.110 2003-12-02 16:13:21 michiel Exp $
 * @see org.mmbase.bridge.Node
 * @see org.mmbase.module.core.MMObjectNode
 */
public class BasicNode implements Node, Comparable, SizeMeasurable {

    public static final int ACTION_CREATE = 1; // create a node
    public static final int ACTION_EDIT   = 2; // edit node, or change aliasses
    public static final int ACTION_DELETE = 3; // delete node
    public static final int ACTION_COMMIT = 10; // commit a node after changes

    private static final Logger log = Logging.getLoggerInstance(BasicNode.class);

    private boolean changed = false;

    /**
     * Reference to the NodeManager
     * @scope private
     */
    protected NodeManager nodeManager;

    /**
     * Reference to the Cloud.
     * @scope private
     */
    protected BasicCloud cloud;

    /**
     * Reference to MMBase root.
     * @scope private
     */
    protected MMBase mmb;

    /**
     * Reference to actual MMObjectNode object.
     * @scope private
     */
    protected MMObjectNode noderef;

    /**
     * Temporary node ID.
     * This is necessary since there is otherwise no sure (and quick) way to determine
     * whether a node is in 'edit' mode (i.e. has a temporary node).
     * Basically, a temporarynodeid is either -1 (invalid), or a negative number smaller than -1
     * (a temporary number assigend by the system).
     * @scope private
     */
    protected int temporaryNodeId = -1;

    /**
     * The account this node is edited under.
     * This is needed to check whether people have not switched users during an edit.
     * @scope private
     */
    protected String account = null;

    /**
     * Determines whether this node was created for insert.
     * @scope private
     */
    protected boolean isnew = false;

    /**
     * Instantiates a node, linking it to a specified node manager.
     * Use this constructor if the node you create uses a NodeManager that is not readily available
     * from the cloud (such as a temporary nodemanager for a result list).
     * @param node the MMObjectNode to base the node on
     * @param nodeManager the NodeManager to use for administrating this Node
     */
    BasicNode(MMObjectNode node, NodeManager nodeManager) {
        this.nodeManager = nodeManager;
        setNode(node);
        init();
    }

    /**
     * Instantiates a node, linking it to a specified cloud
     * The NodeManager for the node is requested from the Cloud.
     * @param node the MMObjectNode to base the node on
     * @param Cloud the cloud to which this node belongs
     */
    BasicNode(MMObjectNode node, Cloud cloud) {
        this.cloud = (BasicCloud)cloud;
        setNode(node);
        init();
    }

    /**
     * Instantiates a new node (for insert), using a specified nodeManager.
     * @param node a temporary MMObjectNode that is the base for the node
     * @param nodeManager the node manager to create the node with
     * @param id the id of the node in the temporary cloud
     */
    BasicNode(MMObjectNode node, Cloud cloud, int id) {
        this.cloud = (BasicCloud)cloud;
        setNode(node);
        temporaryNodeId = id;
        isnew = true;
        init();
        edit(ACTION_CREATE);
    }

    /**
     * Initializes the node.
     * Determines nodemanager and cloud (depending on information available),
     * Sets references to MMBase modules and initializes state in case of a transaction.
     */
    protected void init() {

        if (cloud == null) {
            cloud = (BasicCloud) nodeManager.getCloud();
        }

        if (nodeManager == null) {
            // determine nodemanager, unless the node is the 'typedef' node
            // (needs to point towards itself)
            if (getNode().getBuilder().oType != getNode().getNumber()) {
                nodeManager = cloud.getNodeManager(getNode().getBuilder().getTableName());
            } else {
                nodeManager = (NodeManager)this;
            }
        }

        mmb = BasicCloudContext.mmb;

        // check whether the node is currently in transaction
        // and intialize temporaryNodeId if that is the case
        if ((cloud instanceof BasicTransaction) && (((BasicTransaction)cloud).contains(getNode()))) {
            if (temporaryNodeId == -1) {
                temporaryNodeId = getNode().getNumber();
            }
        }
    }

    public boolean isRelation() {
        return this instanceof Relation;
    }

    public Relation toRelation() {
        return (Relation)this;
    }

    public boolean isNodeManager() {
        return this instanceof NodeManager;
    }

    public NodeManager toNodeManager() {
        return (NodeManager)this;
    }

    public boolean isRelationManager() {
        return this instanceof RelationManager;
    }

    public RelationManager toRelationManager() {
        return (RelationManager)this;
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
     * @throws NotFoundException if no node was specified. This generally means the
     */
    protected final MMObjectNode getNode() throws NotFoundException {
        return noderef;
    }

    /**
     * Invalidates the reference to the underlying MMObjectNode,
     * replacing it with a virtual node that only inherits the number field.
     * @since MMBase-1.6.4
     */
    protected void invalidateNode() {
        VirtualNode n = new VirtualNode(noderef.getBuilder());
        n.setValue("number", noderef.getNumber());
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
            String message = "Passed Node is null";
            log.error(message);
            throw new IllegalArgumentException(message);
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
     */
    boolean isNew() {
        return isnew;
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
        } else if (account != cloud.getAccount()) {
            throw new BridgeException("User context changed. Cannot proceed to edit this node .");
        }
        if (nodeManager instanceof VirtualNodeManager) {
            throw new BridgeException("Cannot make edits to a virtual node.");
        }

        int realnumber = getNode().getNumber();
        if (realnumber != -1) {
            if (action == ACTION_DELETE) {
                cloud.verify(Operation.DELETE, realnumber);
            }
            if ((action == ACTION_EDIT) && (temporaryNodeId == -1)) {
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
                if (cloud instanceof BasicTransaction) {
                    // store new temporary node in transaction
                     ((BasicTransaction)cloud).add(currentObjectContext);
                }
                setNode(BasicCloudContext.tmpObjectManager.getNode(account, "" + id));
                //  check nodetype afterwards?
                temporaryNodeId = id;
            }
        }
    }

    /**
     * @todo setting certain specific fields (i.e. snumber) should be directed to a dedicated
     *       method such as setSource(), where applicable.
     */
    public void setValue(String attribute, Object value) {
        edit(ACTION_EDIT);
        if ("number".equals(attribute) || "otype".equals(attribute) || "owner".equals(attribute)) {
            throw new BridgeException("Not allowed to change field " + attribute + ".");
        }
        if (this instanceof Relation) {
            if ("rnumber".equals(attribute)) {
                throw new BridgeException("Not allowed to change field " + attribute + ".");
            } else if ("snumber".equals(attribute) || "dnumber".equals(attribute)) {
                BasicRelation relation = (BasicRelation)this;
                relation.relationChanged = true;
            }
        }
        _setValue(attribute, value);
    }

    // Protected method to be able to set rnumber when creating a relation.
    protected void _setValue(String attribute, Object value) {
        String result = BasicCloudContext.tmpObjectManager.setObjectField(account, "" + temporaryNodeId, attribute, value);
        if ("unknown".equals(result)) {
            throw new BridgeException("Can't change unknown field '" + attribute + "'.");
        }
        changed = true;
    }

    public void setBooleanValue(String attribute, boolean value) {
        setValue(attribute, new Boolean(value));
    }

    public void setNodeValue(String attribute, Node value) {
        if (value instanceof BasicNode) {
            setValue(attribute, ((BasicNode)value).getNode());
        } else {
            setIntValue(attribute, value.getNumber());
        }
    }

    public void setIntValue(String attribute, int value) {
        setValue(attribute, new Integer(value));
    }

    public void setFloatValue(String attribute, float value) {
        setValue(attribute, new Float(value));
    }

    public void setDoubleValue(String attribute, double value) {
        setValue(attribute, new Double(value));
    }

    public void setByteValue(String attribute, byte[] value) {
        setValue(attribute, value);
    }

    public void setLongValue(String attribute, long value) {
        setValue(attribute, new Long(value));
    }

    public void setStringValue(String attribute, String value) {
        setValue(attribute, value);
    }

    public Object getValue(String attribute) {
        return getNode().getValue(attribute);
    }

    public boolean getBooleanValue(String attribute) {
        return getNode().getBooleanValue(attribute);
    }

    public Node getNodeValue(String attribute) {
        if (attribute == null || attribute.equals("number")) {
            return this;
        }
        MMObjectNode noderes = getNode().getNodeValue(attribute);
        if (noderes != null) {
            if (noderes.getBuilder() instanceof InsRel) {
                return new BasicRelation(noderes, cloud); //.getNodeManager(noderes.getBuilder().getTableName()));
            } else {
                return new BasicNode(noderes, cloud); //.getNodeManager(noderes.getBuilder().getTableName()));
            }
        } else {
            return null;
        }
    }

    public int getIntValue(String attribute) {
        return getNode().getIntValue(attribute);
    }

    public float getFloatValue(String attribute) {
        return getNode().getFloatValue(attribute);
    }

    public long getLongValue(String attribute) {
        return getNode().getLongValue(attribute);
    }

    public double getDoubleValue(String attribute) {
        return getNode().getDoubleValue(attribute);
    }

    public byte[] getByteValue(String attribute) {
        return getNode().getByteValue(attribute);
    }

    public String getStringValue(String attribute) {
        return getNode().getStringValue(attribute);
    }

    public FieldValue getFieldValue(String fieldName) throws NotFoundException {
        return new BasicFieldValue(this, getNodeManager().getField(fieldName));
    }

    public FieldValue getFieldValue(Field field) {
        return new BasicFieldValue(this, field);
    }

    public FieldValue getFunctionValue(String functionName, List arguments) {
        return new BasicFunctionValue(this, getNode(), getNode().getFunctionValue(functionName, arguments));
    }

    public Document getXMLValue(String fieldName) {
        return getNode().getXMLValue(fieldName);
    }

    public Element getXMLValue(String fieldName, Document tree) {
        Document doc = getXMLValue(fieldName);
        if (doc == null) {
            return null;
        }
        return (Element)tree.importNode(doc.getDocumentElement(), true);
    }

    public void setXMLValue(String fieldName, Document value) {
        // do conversion, if needed from doctype 'incoming' to doctype 'needed'
        DocumentConverter dc = DocumentConverter.getDocumentConverter(getNode().getBuilder().getField(fieldName).getDBDocType());
        setValue(fieldName, dc.convert(value, cloud));
    }

    public void commit() {
        if (isnew) {
            cloud.verify(Operation.CREATE, mmb.getTypeDef().getIntValue(getNodeManager().getName()));
        }
        edit(ACTION_COMMIT);
        // ignore commit in transaction (transaction commits)
        if (!(cloud instanceof Transaction)) {
            MMObjectNode node = getNode();
            if (isnew) {
                node.insert(((BasicUser)cloud.getUser()).getUserContext());
                // cloud.createSecurityInfo(getNumber());
                isnew = false;
            } else {
                node.commit(((BasicUser)cloud.getUser()).getUserContext());
                //cloud.updateSecurityInfo(getNumber());
            }
            // remove the temporary node
            BasicCloudContext.tmpObjectManager.deleteTmpNode(account, "" + temporaryNodeId);
            temporaryNodeId = -1;
            // invalid nodereference, so retrieve node anew
            setNode(mmb.getTypeDef().getNode(getNode().getNumber()));
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
            if (isnew) {
                isnew = false;
                invalidateNode();
            } else {
                // update the node, reset fields etc...
                setNode(mmb.getTypeDef().getNode(noderef.getNumber()));
            }
            temporaryNodeId = -1;
        }
        changed = false;
    }

    public void delete() {
        delete(false);
    }

    public void delete(boolean deleteRelations) {
        edit(ACTION_DELETE);
        if (isnew) {
            // remove from the Transaction
            // note that the node is immediately destroyed !
            // possibly older edits will fail if they refernce this node
            if (cloud instanceof Transaction) {
                ((BasicTransaction)cloud).remove("" + temporaryNodeId);
            }
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
                    throw new BridgeException(
                        "This node ("
                            + getNode().getNumber()
                            + ") cannot be deleted. It still has relations attached to it.");
                }
            }
            // remove aliases
            deleteAliases(null);
            // in transaction:
            if (cloud instanceof BasicTransaction) {
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
                int number = getNumber();
                //node.getBuilder().removeNode(node);
                node.remove( ((BasicUser)cloud.getUser()).getUserContext());
                //cloud.removeSecurityInfo(number);
            }
        }
        // the node does not exist anymore, so invalidate all references.
        temporaryNodeId = -1;
        invalidateNode();
    }

    public String toString() {
        //return getNode().toString() + "(" + getNode().getClass().getName() + ")";
        return getNode().toString();
    }

    /**
     * Removes all relations of a certain type.
     *
     * @param type  the type of relation (-1 = don't care)
     */
    private void deleteRelations(int type) {
        RelDef reldef = mmb.getRelDef();
        List relations = null;
        if (type == -1) {
            relations = mmb.getInsRel().getAllRelationsVector(getNode().getNumber());
        } else {
            relations = mmb.getInsRel().getRelationsVector(getNode().getNumber());
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
                        String currentObjectContext =
                            BasicCloudContext.tmpObjectManager.getObject(account, "" + oMmbaseId, oMmbaseId);
                        ((BasicTransaction)cloud).add(currentObjectContext);
                        ((BasicTransaction)cloud).delete(currentObjectContext);
                    } else {
                        int number = node.getNumber();
                        node.remove( ((BasicUser)cloud.getUser()).getUserContext());
                    }
                }
            }
        }
    }

    public void deleteRelations() {
        deleteRelations(-1);
    }

    public void deleteRelations(String type) throws NotFoundException {
        RelDef reldef = mmb.getRelDef();
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
        InsRel relbuilder = mmb.getInsRel();
        Enumeration e = null;
        if ((role != 1) || (otype != -1)) {
            if (role != -1) {
                relbuilder = mmb.getRelDef().getBuilder(role);
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

    public RelationList getRelations() {
        return getRelations(null, (String) null);
    }

    public RelationList getRelations(String role) {
        return getRelations(role, (String) null);
    }

    public RelationList getRelations(String role, String nodeManager) throws NotFoundException {
        int otype = -1;
        if (nodeManager != null) {
            otype = mmb.getTypeDef().getIntValue(nodeManager);
            if (otype == -1) {
                throw new NotFoundException("NodeManager " + nodeManager + " does not exist.");
            }
        }
        int rolenr = -1;
        if (role != null) {
            rolenr = mmb.getRelDef().getNumberByName(role);
            if (rolenr == -1) {
                throw new NotFoundException("Relation with role " + role + " does not exist.");
            }
        }
        return getRelations(rolenr, otype);
    }

    public RelationList getRelations(String role, NodeManager nodeManager) {
        if (nodeManager == null) {
            return getRelations(role);
        } else {
            return getRelations(role, nodeManager.getName());
        }
    }

    public RelationList getRelations(String role, NodeManager nodeManager, String searchDir) throws NotFoundException {
        // temporay implementation to get it working for now. Really we would want to make separate queries, I think.

        RelationList  relations = getRelations(role, nodeManager);

        int dir = ClusterBuilder.SEARCH_BOTH;
        if (searchDir != null) {
            dir = ClusterBuilder.getSearchDir(searchDir);
        }
        if (dir == ClusterBuilder.SEARCH_BOTH) return relations;

        RelationIterator it = relations.relationIterator();

        RelationList result = new BasicRelationList();

        while (it.hasNext()) {
            Relation relation = it.nextRelation();
            switch(dir) {
            case ClusterBuilder.SEARCH_DESTINATION:
                if(relation.getSource().getNumber() == getNumber()) {
                    result.add(relation);
                }
                break;
            case ClusterBuilder.SEARCH_SOURCE:
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

    public int countRelations() {
        return countRelatedNodes(cloud.getNodeManager("object"), null, "BOTH");
    }

    public int countRelations(String type) {
        //err
        return countRelatedNodes(cloud.getNodeManager("object"), type, "BOTH");
    }


    public int countRelatedNodes(NodeManager otherNodeManager, String role, String direction) {
        BasicQuery count = (BasicQuery) cloud.createAggregatedQuery();
        count.addStep(nodeManager);
        Step step = count.addRelationStep(otherNodeManager, role, direction, false).getPrevious();
        count.addNode(step, this);
        count.addAggregatedField(step, nodeManager.getField("number"), AggregatedField.AGGREGATION_TYPE_COUNT);
        Node result = (Node) cloud.getList(count).get(0);
        return result.getIntValue("number");
    }


    public NodeList getRelatedNodes() {
        return getRelatedNodes("object", null, null);
    }

    public NodeList getRelatedNodes(String type) {
        return getRelatedNodes(type, null, null);
    }

    public NodeList getRelatedNodes(NodeManager nodeManager) {
        return getRelatedNodes(nodeManager, null, null);
    }

    /**
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
        int dir = ClusterBuilder.SEARCH_BOTH;
        if (searchDir != null) {
            dir = ClusterBuilder.getSearchDir(searchDir);
        }
        // call list: note: role can be null
        List mmnodes = getNode().getRelatedNodes((nodeManager != null ? nodeManager.getName() : null), role, dir);

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
    public NodeList getRelatedNodes(String type, String role, String searchDir) {
        return getRelatedNodes(cloud.getNodeManager(type), role, searchDir);
    }

    public int countRelatedNodes(String type) {
        return getNode().getRelationCount(type);
    }

    public StringList getAliases() {
        NodeManager oalias = cloud.getNodeManager("oalias");
        NodeQuery q = oalias.createQuery();
        Constraint c = q.createConstraint(q.getStepField(oalias.getField("destination")), new Integer(getNumber()));
        q.setConstraint(c);
        NodeList aliases = cloud.getList(q);
        StringList result = new BasicStringList();
        NodeIterator i = aliases.nodeIterator();
        while (i.hasNext()) {
            result.add(i.nextNode().getStringValue("name"));
        }
        return result;
    }

    public void createAlias(String aliasName) {
        edit(ACTION_EDIT);
        if (cloud instanceof Transaction) {
            String aliasContext = BasicCloudContext.tmpObjectManager.createTmpAlias(aliasName, account, "a" + temporaryNodeId, "" + temporaryNodeId);
            ((BasicTransaction)cloud).add(aliasContext);
        } else if (isnew) {
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
        if (!isnew) {
            String sql = "WHERE (destination" + "=" + getNumber() + ")";
            if (aliasName != null) {
                sql += " AND (name='" + aliasName + "')";
            }
            // search existing aliases until the right one is found!
            OAlias alias = mmb.getOAlias();
            if (alias != null) {
                for (Enumeration e = alias.search(sql); e.hasMoreElements();) {
                    MMObjectNode node = (MMObjectNode)e.nextElement();
                    if (cloud instanceof Transaction) {
                        BasicTransaction tran = (BasicTransaction)cloud;
                        String oMmbaseId = "" + node.getValue("number");
                        String currentObjectContext =
                            BasicCloudContext.tmpObjectManager.getObject(account, "" + oMmbaseId, oMmbaseId);
                        ((BasicTransaction)cloud).add(currentObjectContext);
                        ((BasicTransaction)cloud).delete(currentObjectContext);
                    } else {
                        node.remove( ((BasicUser)cloud.getUser()).getUserContext());
                    }
                }
            }
        }
    }

    public void deleteAlias(String aliasName) {
        edit(ACTION_EDIT);
        deleteAliases(aliasName);
    }

    public Relation createRelation(Node destinationNode, RelationManager relationManager) {
        Relation relation = relationManager.createRelation(this, destinationNode);
        return relation;
    }


    // javadoc inherited (from Node)
    public void setContext(String context) {
        // set the context on the node (run after insert).
        getNode().setContext(((BasicUser)cloud.getUser()).getUserContext(),context, temporaryNodeId == -1);
    }

    // javadoc inherited (from Node)
    public String getContext() {
        return cloud.getContext(getNumber());
    }


    // javadoc inherited (from Node)
    public StringList getPossibleContexts() {
        return new BasicStringList(cloud.getPossibleContexts(getNumber()));
    }

    public boolean mayWrite() {
        if (isNew()) {
            return true;
        } else {
            return cloud.check(Operation.WRITE, getNode().getNumber());
        }
    }

    public boolean mayDelete() {
        if (isNew()) {
            return true;
        } else {
            return cloud.check(Operation.DELETE, getNode().getNumber());
        }
    }

    public boolean mayChangeContext() {
        if (isNew()) {
            return true;
        } else {
            return cloud.check(Operation.CHANGECONTEXT, getNode().getNumber());
        }
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
     * Compares this node to the passed object.
     * Returns 0 if they are equal, -1 if the object passed is a NodeManager and larger than this manager,
     * and +1 if the object passed is a NodeManager and smaller than this manager.
     * This is used to sort Nodes.
     * A node is 'larger' than another node if its GUI() result is larger (alphabetically, case sensitive)
     * than that of the other node. If the GUI() results are the same, the nodes are compared on number,
     * and (if needed) on their owning clouds.
     *
     * @param o the object to compare it with
     */
    public int compareTo(Object o) {
        Node n = (Node)o;
        String s1 = "";
        if (this instanceof NodeManager) {
            s1 = ((NodeManager)this).getGUIName();
        } else {
            s1 = getStringValue("gui()");
        }
        String s2 = "";
        if (n instanceof NodeManager) {
            s2 = ((NodeManager)n).getGUIName();
        } else {
            s2 = n.getStringValue("gui()");
        }
        int res = s1.compareTo(s2);
        if (res != 0) {
            return res;
        } else {
            int n1 = getNumber();
            int n2 = n.getNumber();
            if (n2 > n1) {
                return -1;
            } else if (n2 < n1) {
                return -1;
            } else {
                return ((Comparable)cloud).compareTo(n.getCloud());
            }
        }
    }

    /**
     * @since MMBase-1.6.2
     */
    public int hashCode() {
        return getNode().hashCode();
    }

    /**
     * Compares two nodes, and returns true if they are equal.
     * This effectively means that both objects are nodes, and they both have the same number and cloud
     * @param o the object to compare it with
     */
    public boolean equals(Object o) {
        return (o instanceof Node) && getNumber() == ((Node)o).getNumber() && cloud.equals(((Node)o).getCloud());

    }

}
