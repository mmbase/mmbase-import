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
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.util.logging.*;

/**
 *
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 */
public class BasicNode implements Node {
    private static Logger log = Logging.getLoggerInstance(BasicNode.class.getName());

    public static final int ACTION_CREATE = 1;   // create a node
    public static final int ACTION_EDIT = 2;     // edit node, or change aliasses
    public static final int ACTION_DELETE = 3;   // delete node
    public static final int ACTION_LINK = 4;     // add a relation to a node
    public static final int ACTION_COMMIT = 10;   // commit a node after changes

    private boolean changed = false;

    /**
     * Reference to the NodeManager
     */
    protected NodeManager nodeManager;

    /**
     * Reference to the Cloud.
     */
    protected BasicCloud cloud;

    /**
     * Reference to MMBase root.
     */
    protected MMBase mmb;

    /**
     * Reference to actual MMObjectNode object.
     */
    protected MMObjectNode noderef;

    /**
     * Temporary node ID.
     * this is necessary since there is otherwise no sure (and quick) way to determine
     * whether a node is in 'edit' mode (i.e. has a temporary node).
     */
    protected int temporaryNodeId=-1;

    /**
     * The account this node is edited under.
     * This is needed to check whether people have not switched users during an edit.
     */
    protected String account=null;

    /**
     * Determines whether this node was created for insert.
     */
    protected boolean isnew = false;

    /*
     * Instantiates a node, linking it to a specified node manager.
     */
    BasicNode(MMObjectNode node, NodeManager nodeManager) {
        this.nodeManager=nodeManager;
        cloud=(BasicCloud)nodeManager.getCloud();
        noderef=node;
        // create shortcut to mmbase
        mmb = ((BasicCloudContext)nodeManager.getCloud().getCloudContext()).mmb;
        // check whether the node is currently in transaction
        // and intialize temporaryNodeId if that is the case
        if ((cloud instanceof BasicTransaction) && ( ((BasicTransaction)cloud).contains(noderef))) {
            temporaryNodeId=noderef.getNumber();
        }
    }

    /*
     * Instantiates a new node (for insert), using a specified nodeManager.
     * @param node a temporary MMObjectNode that is the base for the node
     * @param nodeManager the node manager to create the node with
     * @param id the id of the node in the temporary cloud
     */
    BasicNode(MMObjectNode node, NodeManager nodeManager, int id) {
        this(node, nodeManager);
        temporaryNodeId=id;
        edit(ACTION_CREATE);
        isnew=true;
    }

    protected MMObjectNode getNode() {
        if (noderef==null) {
            String message;
            message = "Node is invalidated or removed.";
            log.error(message);
            throw new BridgeException(message);
        }
        return noderef;
    }

    public Cloud getCloud() {
        return nodeManager.getCloud();
    }

    public NodeManager getNodeManager() {
        return nodeManager;
    }

    public int getNumber() {
        int i=getNode().getNumber();
        // new node, thus return temp id.
        // note that temp id is equal to "number" if the node is edited
        if (i==-1) {
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
     * The type of edit is determined by the action specified, and one of:<br>
     * ACTION_CREATE (create a node),<br>
     * ACTION_EDIT (edit node, or change aliasses),<br>
     * ACTION_DELETE (delete node),<br>
     * ACTION_LINK (add a relation),<br>
     * ACTION_COMMIT (commit a node after changes)
     *
     * @param action The action to perform.
     */
    protected void edit(int action) {
        if (account==null) {
            account = cloud.getAccount();
        } else if (account != cloud.getAccount()) {
            String message;
            message = "User context changed. Cannot proceed to edit this "
                      + "node .";
            log.error(message);
            throw new BridgeException(message);
        }
        if (nodeManager instanceof VirtualNodeManager) {
            String message;
            message = "Cannot make edits to a virtual node.";
            log.error(message);
            throw new BridgeException(message);
        }

        int realnumber=noderef.getNumber();
        if (realnumber!=-1) {
            if (action==ACTION_DELETE) {
                cloud.assert(Operation.DELETE,realnumber);
            }
            if (action==ACTION_LINK) {
                cloud.assert(Operation.LINK,realnumber);
            }
            if ((action==ACTION_EDIT) && (temporaryNodeId==-1)) {
                cloud.assert(Operation.WRITE,realnumber);
            }
        }

        // check for the existence of a temporary node
        if (temporaryNodeId==-1) {
            // when committing a temporary node id must exist (otherwise fail).
            if (action == ACTION_COMMIT) {
                // throw new BasicBridgeException("This node cannot be comitted (not changed).");
            }
            // when adding a temporary node id must exist (otherwise fail).
            // this should not occur (hence internal error notice), but we test it anyway.
            if (action == ACTION_CREATE) {
                String message;
                message = "This node cannot be added. It was not correctly "
                          + "instantiated (internal error).";
                log.error(message);
                throw new BridgeException(message);
            }

            // when editing a temporary node id must exist (otherwise create one)
            // This also applies if you remove a node in a transaction (as the transction manager requires a temporary node)
            //
            // XXX: If you edit a node outside a transaction, but do not commit or cancel the edits,
            // the temporarynode will not be removed. This is left to be fixed (i.e.through a time out mechanism?)
            if ((action == ACTION_EDIT) ||
                ((action == ACTION_DELETE) && (nodeManager instanceof BasicTransaction))) {
                int id = getNumber();
                String currentObjectContext = BasicCloudContext.tmpObjectManager.getObject(account,""+id, ""+id);
                if (cloud instanceof BasicTransaction) {
                    // store new temporary node in transaction
                    ((BasicTransaction)cloud).add(currentObjectContext);
                }
                noderef = BasicCloudContext.tmpObjectManager.getNode(account, ""+id);
                //  check nodetype afterwards?
                temporaryNodeId=id;
            }
        }
    }

    public void setValue(String attribute, Object value) {
        edit(ACTION_EDIT);
        if ("number".equals(attribute) || "otype".equals(attribute) || "owner".equals(attribute)
            //|| "snumber".equals(attribute) || "dnumber".equals(attribute) || "rnumber".equals(attribute)
            ) {
            String message;
            message = "Not allowed to change field " + attribute + ".";
            log.error(message);
            throw new BridgeException(message);
        }
        String result = BasicCloudContext.tmpObjectManager.setObjectField(account,""+temporaryNodeId, attribute, value);
        if ("unknown".equals(result)) {
            String message;
            message = "Can't change unknown field " + attribute + ".";
            log.error(message);
            throw new BridgeException(message);
        }
    changed = true;

    }

    public void setBooleanValue(String attribute, boolean value) {
        setValue(attribute,new Boolean(value));
    }

    public void setIntValue(String attribute, int value) {
        setValue(attribute,new Integer(value));
    }

    public void setFloatValue(String attribute, float value) {
        setValue(attribute,new Float(value));
    }

    public void setDoubleValue(String attribute, double value) {
        setValue(attribute,new Double(value));
    }

    public void setByteValue(String attribute, byte[] value) {
        setValue(attribute,value);
    }

    public void setLongValue(String attribute, long value) {
        setValue(attribute,new Long(value));
    }

    public void setStringValue(String attribute, String value) {
        setValue(attribute,value);
    }

    public Object getValue(String attribute) {
        return noderef.getValue(attribute);
    }

    public boolean getBooleanValue(String attribute) {
        return noderef.getBooleanValue(attribute);
    }

    public Node getNodeValue(String attribute) {
        if (attribute==null || attribute.equals("number")) return this;
        MMObjectNode noderes=noderef.getNodeValue(attribute);
        if (noderes!=null) {
            if (noderes.parent instanceof InsRel) {
                return new BasicRelation(noderes,cloud.getNodeManager(noderes.parent.getTableName()));
            } else {
                return new BasicNode(noderes,cloud.getNodeManager(noderes.parent.getTableName()));
            }
        } else {
            return null;
        }
    }

    public int getIntValue(String attribute) {
        return noderef.getIntValue(attribute);
    }

    public float getFloatValue(String attribute) {
        return noderef.getFloatValue(attribute);
    }

    public long getLongValue(String attribute) {
        return noderef.getLongValue(attribute);
    }

    public double getDoubleValue(String attribute) {
        return noderef.getDoubleValue(attribute);
    }

    public byte[] getByteValue(String attribute) {
        return noderef.getByteValue(attribute);
    }

    public String getStringValue(String attribute) {
        return noderef.getStringValue(attribute);
    }

    public void commit() {
        edit(ACTION_COMMIT);
        // ignore commit in transaction (transaction commits)
        if (!(cloud instanceof Transaction)) {
            MMObjectNode node= getNode();
            if (isnew) {
                node.insert(cloud.getUser().getIdentifier());
                //node.insert("bridge");
                cloud.createSecurityInfo(getNumber());
                isnew=false;
            } else {
                node.parent.safeCommit(node);
                cloud.updateSecurityInfo(getNumber());
            }
            // remove the temporary node
            BasicCloudContext.tmpObjectManager.deleteTmpNode(account,""+temporaryNodeId);
            temporaryNodeId=-1;
            // invalid nodereference: fix!
            noderef=mmb.getTypeDef().getNode(noderef.getNumber());
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
            BasicCloudContext.tmpObjectManager.deleteTmpNode(account,""+temporaryNodeId);
            if (isnew) {
                isnew=false;
                noderef=null;
            } else {
                // update the node, reset fields etc...
                noderef=mmb.getTypeDef().getNode(noderef.getNumber());
            }
            temporaryNodeId=-1;
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
                ((BasicTransaction)cloud).remove(""+temporaryNodeId);
            }
            // remove a temporary node (no true instantion yet, no relations)
            BasicCloudContext.tmpObjectManager.deleteTmpNode(account,""+temporaryNodeId);
        } else {
            // remove a node that is edited, i.e. that already exists
            // check relations first!
            if (deleteRelations) {
                // option set, remove relations
                deleteRelations(-1);
            } else {
                // option unset, fail if any relations exit
                if(getNode().hasRelations()) {
                    String message;
                    message = "This node (" + getNode().getNumber() + ") cannot be deleted. It still has relations attached to it.";
                    log.error(message);
                    throw new BridgeException(message);
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
                ((BasicTransaction)cloud).delete(""+temporaryNodeId);
            } else {
                // remove the node
                if (temporaryNodeId!=-1) {
                    BasicCloudContext.tmpObjectManager.deleteTmpNode(account,""+temporaryNodeId);
                }
                MMObjectNode node= getNode();
                int number=getNumber();
                node.parent.removeNode(node);
                cloud.removeSecurityInfo(number);
            }
        }
        // the node does not exist anymore, so invalidate all references.
        temporaryNodeId=-1;
        noderef=null;
    }

    public String toString() {
        if (noderef == null) {
            return "*deleted node*";
        }
        return noderef.toString();
    };

    /**
     * Removes all relations of a certain type.
     *
     * @param type  the type of relation (-1 = don't care)
     */
    private void deleteRelations(int type) {
        RelDef reldef=mmb.getRelDef();
        Enumeration e = null;
        if (type==-1) {
            e = getNode().getAllRelations();
        } else {
            e = getNode().getRelations();
        }
        if (e!=null) {
            while (e.hasMoreElements()) {
                MMObjectNode node = (MMObjectNode)e.nextElement();
                if ((type==-1) || (node.getIntValue("rnumber")==type)) {
                    if (cloud instanceof Transaction) {
                        String oMmbaseId = ""+node.getValue("number");
                        String currentObjectContext = BasicCloudContext.tmpObjectManager.getObject(account,""+oMmbaseId,oMmbaseId);
                        ((BasicTransaction)cloud).add(currentObjectContext);
                        ((BasicTransaction)cloud).delete(currentObjectContext);
                    } else {
                        int number=node.getNumber();
                        node.parent.removeNode(node);
                        cloud.removeSecurityInfo(number);
                    }
                }
            }
        }
    };

    public void deleteRelations() {
        deleteRelations(-1);
    }

    public void deleteRelations(String type) {
        RelDef reldef=mmb.getRelDef();
        int rType=reldef.getNumberByName(type);
        if (rType==-1) {
            String message;
            message = "Cannot find relation type.";
            log.error(message);
            throw new BridgeException(message);
        } else {
            deleteRelations(rType);
        }
    };

    public RelationList getRelations() {
        return getRelations(-1,-1);
    };

    private RelationList getRelations(int role) {
        return getRelations(role,-1);
    };

    private RelationList getRelations(int role, int otype) {
        InsRel relbuilder=mmb.getInsRel();
        Vector relvector=new Vector();
        Enumeration e=null;
        if ((role!=1) || (otype!=-1)) {
            if (role!=-1) {
                relbuilder=mmb.getRelDef().getBuilder(role);
            }
            e=relbuilder.getRelations(getNumber(),otype, role);
        } else {
            e=getNode().getRelations();
        }
        if (e!=null) {
            while (e.hasMoreElements()) {
                MMObjectNode mmnode=(MMObjectNode)e.nextElement();
                if (cloud.check(Operation.READ, mmnode.getNumber())) {
                    relvector.add(mmnode);
                }
            }
        }
        return new BasicRelationList(relvector,cloud,cloud.getNodeManager(relbuilder.getTableName()));
    };

    public RelationList getRelations(String role) {
        int rolenr=mmb.getRelDef().getNumberByName(role);
        if (rolenr==-1) {
            String message;
            message = "Relation type " + role + " does not exist.";
            log.error(message);
            throw new BridgeException(message);
        } else {
            return getRelations(rolenr);
        }
    };

    public RelationList getRelations(String role, String nodeManager) {
        if (nodeManager==null) return getRelations(role);
        int otype=mmb.getTypeDef().getIntValue(nodeManager);
        if (otype==-1) {
            String message;
            message = "NodeManager " + nodeManager + " does not exist.";
            log.error(message);
            throw new BridgeException(message);
        }
        int rolenr=-1;
        if (role!=null) {
            rolenr=mmb.getRelDef().getNumberByName(role);
            if (rolenr==-1) {
                String message;
                message = "Relation type " + role + " does not exist.";
                log.error(message);
                throw new BridgeException(message);
            }
        }
        return getRelations(rolenr,otype);
    };

    public boolean hasRelations() {
        return getNode().hasRelations();
    };

    public int countRelations() {
        return getRelations().size();
    };

    public int countRelations(String type) {
        return getRelations(type).size();
    };

    public NodeList getRelatedNodes() {
        Vector relvector=new Vector();
        Enumeration e=getNode().getRelatedNodes().elements();
        if (e!=null) {
            while (e.hasMoreElements()) {
                MMObjectNode mmnode=(MMObjectNode)e.nextElement();
                if (cloud.check(Operation.READ, mmnode.getNumber())) {
                    relvector.add(mmnode);
                }
            }
        }
        return new BasicNodeList(relvector,cloud);
    };

    public NodeList getRelatedNodes(String type) {
        Vector relvector=new Vector();
        Vector nv = getNode().getRelatedNodes(type);
        if (nv == null) {
            throw new BridgeException("Could not get related nodes of type '" + type + "', because that is not a known NodeManager");
        }
        Enumeration e = nv.elements();
        if (e!=null) {
            while (e.hasMoreElements()) {
                MMObjectNode mmnode=(MMObjectNode)e.nextElement();
                if (cloud.check(Operation.READ, mmnode.getNumber())) {
                    relvector.add(mmnode);
                }
            }
        }
        return new BasicNodeList(relvector,cloud);
    }

    public int countRelatedNodes(String type) {
        return getNode().getRelationCount(type);
    };

    public StringList getAliases() {
        Vector aliasvector=new Vector();
        OAlias alias=mmb.OAlias;
        if (alias!=null) {
            for(Enumeration e=alias.search("WHERE "+"destination"+"="+getNumber()); e.hasMoreElements();) {
                MMObjectNode node=(MMObjectNode)e.nextElement();
                aliasvector.add(node.getStringValue("name"));
            }
        }
        return new BasicStringList(aliasvector);
    };

    public void createAlias(String aliasName) {
        edit(ACTION_EDIT);
        if (cloud instanceof Transaction) {
            String aliasContext=BasicCloudContext.tmpObjectManager.createTmpAlias(aliasName,account,"a"+temporaryNodeId, ""+temporaryNodeId);
            ((BasicTransaction)cloud).add(aliasContext);
        } else if (isnew) {
            String message;
            message = "Cannot add alias to a new node that has not been "
                      + "committed.";
            log.error(message);
            throw new BridgeException(message);
        } else {
            getNode().parent.createAlias(getNumber(),aliasName);
        }
    }

    /**
     * Delete one or all aliases of this node
     * @param aliasName the name of the alias (null means all aliases)
     */
    private void deleteAliases(String aliasName) {
        edit(ACTION_EDIT);
        // A new node cannot have any aliases, except when in a transaction.
        // However, there is no point in adding aliasses to a ndoe you plan to delete,
        // so no attempt has been made to rectify this (cause its not worth all the trouble).
        // If people remove a node for which they created aliases in the same transaction, that transaction will fail.
        // Live with it.
        if (!isnew) {
            String sql = "WHERE (destination"+"="+getNumber()+")";
            if (aliasName!=null) {
                sql += " AND (name='"+aliasName+"')";
            }
            // search existing aliases until the right one is found!
            OAlias alias=mmb.getOAlias();
            if (alias!=null) {
                for(Enumeration e=alias.search(sql); e.hasMoreElements();) {
                    MMObjectNode node=(MMObjectNode)e.nextElement();
                    if (cloud instanceof Transaction) {
                        BasicTransaction tran=(BasicTransaction) cloud;
                        String oMmbaseId = ""+node.getValue("number");
                        String currentObjectContext = BasicCloudContext.tmpObjectManager.getObject(account,""+oMmbaseId,oMmbaseId);
                        ((BasicTransaction)cloud).add(currentObjectContext);
                        ((BasicTransaction)cloud).delete(currentObjectContext);
                    } else {
                        int number=node.getNumber();
                        alias.removeNode(node);
                        cloud.removeSecurityInfo(number);
                    }
                }
            }
        }
    };

    public void deleteAlias(String aliasName) {
        deleteAliases(aliasName);
    };

    public Relation createRelation(Node destinationNode, RelationManager relationManager) {
        Relation relation = relationManager.createRelation(this,destinationNode);
        return relation;
    };


    /**
     * Compares two objects, and returns true if they are equal.
     * This effectively means that both objects are nodes, and they both refer to the same objectnode
     *
     * @param o the object to compare it with
     */
    public boolean equals(Object o) {
        return (o instanceof Node) && (o.hashCode()==hashCode());
    };

    /**
     * Returns the object's hashCode.
     * This effectively returns th objectnode's number
     */
    public int hashCode() {
        return getNumber();
    };

    /**
     * set the Context of the current Node
     *
     * @param context	    	    The context to which the current node should belong,
     * @throws BridgeException      Dunno?
     * @throws SecurityException    When not the approperate rights (change context)
     */
    public void setContext(String context) {
        cloud.setContext(getNumber(), context);
    }

    /**
     * get the Context of the current Node
     *
     * @return the current context of the node
     * @throws BridgeException      Dunno?
     * @throws SecurityException    When not the approperate rights (read rights)
     */
    public String getContext() {
        return cloud.getContext(getNumber());
    }

    /**
     * get the Contextes which can be set to this specific node
     *
     * @return the contextes from which can be chosen
     * @throws BridgeException      Dunno?
     * @throws SecurityException    When not the approperate rights (read rights)
     */
    public StringList getPossibleContexts() {
        return new BasicStringList(cloud.getPossibleContexts(getNumber()));
    }


    public boolean mayWrite() {
        return cloud.check(Operation.WRITE, noderef.getNumber());
    }
    public boolean mayDelete() {
        return cloud.check(Operation.DELETE, noderef.getNumber());
    }
    public boolean mayLink() {
        return cloud.check(Operation.LINK, noderef.getNumber());
    }

    public boolean mayChangeContext() {
        return cloud.check(Operation.CHANGECONTEXT, noderef.getNumber());
    }

    /**
     *	Reverse the buffers, when changed and not stored...
     */
    protected void finalize() throws BridgeException {
        // When not commit-ed or cancelled, and the buffer has changed, the changes must be reversed.
    // when not done it results in node-lists with changes which are not performed on the database...
    // This is all due to the fact that Node doesnt make a copy of MMObjectNode, while editing...
    // my opinion is that this should happen, as soon as edit-ting starts,..........
        // when still has modifications.....
        if(changed) {
            if(!(cloud instanceof Transaction)) {
            // cancel the modifications...
            cancel();
        // The big question is, why did he throw an exeption over here? well there is no otherway to check if it was used in the
        // proper way.
        // Well in my opinion the working of the system should not depend on the fact if the garbage collecter remove's this object.
        // This since it is not defined when the finalize method is called
        // To bad only that nobody will ever see this exceptions :(
        String msg = "after modifications to the node, either the method commit or cancel must be called";
        log.error(msg);
                throw new BridgeException(msg);
        }
    }
    }
}
