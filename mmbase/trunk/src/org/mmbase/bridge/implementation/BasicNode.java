/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;
// import org.mmbase.security.*;
import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import java.util.*;

/**
 * Describes an object in the cloud.
 *
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 */
public class BasicNode implements Node {

    public static final int ACTION_ADD = 1;      // add a node
    public static final int ACTION_EDIT = 2;     // edit node, or change aliasses
    public static final int ACTION_REMOVE = 3;   // remove node
    public static final int ACTION_LINK = 4;     // add a relation to a node
    public static final int ACTION_COMMIT = 10;   // commit a node after changes
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

  	/**
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
  	        temporaryNodeId=noderef.getIntValue("number") ;
  	    }
  	}

    /**
    * Instantiates a new node (for insert), using a specified nodeManager.
    * @param node a temporary MMObjectNode that is the base for the node
    * @param nodeManager the node manager to create the node with
    * @param id the id of the node in the temporary cloud
    */
  	BasicNode(MMObjectNode node, NodeManager nodeManager, int id) {
  	    this(node, nodeManager);
  	    temporaryNodeId=id;
  	    Edit(ACTION_ADD);
  	    isnew=true;
  	}
  	
  	protected MMObjectNode getNode() {
  	    if (noderef==null) {
	        throw new BridgeException("Node is invalidated or removed.");
	    }
	    return noderef;
  	}
  	
  	/**
     * Retrieves the cloud where this node is part of.
     */
    public Cloud getCloud() {
        return nodeManager.getCloud();
    }

	/**
     * Retrieves the NodeManager of this node
     */
    public NodeManager getNodeManager() {
        return nodeManager;
    }
	
	/**
     * Retrieves the node ID
     */
    public int getNodeNumber() {
        int i=getIntValue("number");
        // new node, thus return temp id.
        // note that temp id is equal to "number" if the node is edited
        if (i==-1) {
            i = temporaryNodeId;
        }
        return i;
    }
	
    /**
    * Edit this node.
    * Check whether edits are allowed and prepare a node for edits if needed.
    * The type of edit is determined by the action specified, and one of:<br>
    * ACTION_ADD (add a node),<br>
    * ACTION_EDIT (edit node, or change aliasses),<br>
    * ACTION_REMOVE (remove node),<br>
    * ACTION_LINK (add a relation),<br>
    * ACTION_COMMIT (commit a node after changes)
     * @param action The action to perform.
    */
    protected void Edit(int action) {
        if (account==null) {
            account = cloud.getAccount();
        } else if (account != cloud.getAccount()) {
            throw new BridgeException("User context changed. Cannot proceed to edit this node .");
        }
	    if (nodeManager instanceof VirtualNodeManager) {
            throw new BridgeException("Cannot make edits to a virtual node.");
	    }
	
	    int realnumber=noderef.getIntValue("number");
	    if (realnumber!=-1) {
	        if (action==ACTION_REMOVE) {
//	            cloud.assert(Operation.REMOVE,realnumber);
	        }
	        if (action==ACTION_LINK) {
//	            cloud.assert(Operation.LINK,realnumber);
	        }
	        if ((action==ACTION_EDIT) && (temporaryNodeId==-1)) {
//	            cloud.assert(Operation.WRITE,realnumber);
	        }
	    }
	
	    // check for the existence of a temporary node
	    if (temporaryNodeId==-1) {
            // when committing a temporary node id must exist (otherwise fail).
	        if (action == ACTION_COMMIT) {
                throw new BridgeException("This node cannot be comitted (not changed).");
    	    }	
            // when adding a temporary node id must exist (otherwise fail).
            // this should not occur (hence internal error notice), but we test it anyway.
	        if (action == ACTION_ADD) {
                throw new BridgeException("This node cannot be added. It was not correctly instantiated (internal error)");
	        }	

            // when editing a temporary node id must exist (otherwise create one)
            // This also applies if you remove a node in a transaction (as the transction manager requires a temporary node)
            //
            // XXX: If you edit a node outside a transaction, but do not commit or cancel the edits,
            // the temporarynode will not be removed. This is left to be fixed (i.e.through a time out mechanism?)
	        if ((action == ACTION_EDIT) ||
     	        ((action == ACTION_REMOVE) && (nodeManager instanceof BasicTransaction))) {
     	        int id = getNodeNumber();
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
	
	/**
	 * Set the value of certain attribute
	 * @param attribute name of field
	 * @param value of attribute
	 */
	public void setValue(String attribute, Object value) {
	    Edit(ACTION_EDIT);
	    BasicCloudContext.tmpObjectManager.setObjectField(account,""+temporaryNodeId, attribute, value);
	}

	/**
	 * Set the value of certain attribute
	 * @param attribute name of field
	 * @param value of attribute
	 */
	public void setIntValue(String attribute, int value) {
	    setValue(attribute,new Integer(value));
	}

	/**
	 * Set the value of certain attribute
	 * @param attribute name of field
	 * @param value of attribute
	 */
	public void setFloatValue(String attribute, float value) {
	    setValue(attribute,new Float(value));
	}

	/**
	 * Set the value of certain attribute
	 * @param attribute name of field
	 * @param value of attribute
	 */
	public void setDoubleValue(String attribute, double value) {
	    setValue(attribute,new Double(value));
	}

	/**
	 * Set the value of certain attribute
	 * @param attribute name of field
	 * @param value of attribute
	 */
	public void setByteValue(String attribute, byte[] value) {
	    setValue(attribute,value);
	}

	/**
	 * Set the value of certain attribute
	 * @param attribute name of field
	 * @param value of attribute
	 */
	public void setLongValue(String attribute, long value) {
	    setValue(attribute,new Long(value));
	}

	/**
	 * Set the value of certain attribute
	 * @param attribute name of field
	 * @param value of attribute
	 */
	public void setStringValue(String attribute, String value) {
	    setValue(attribute,value);
	}

	/**
	 * Retrieves the value of certain attribute
	 * @param name of attribute you want
	 * @return value of attribute
	 */
	public Object getValue(String attribute) {
	    return noderef.getValue(attribute);
	}

	/**
	 * Retrieves the value of certain attribute
	 * @param name of attribute you want
	 * @return value of attribute
	 */
	public int getIntValue(String attribute) {
	    return noderef.getIntValue(attribute);
	}

	/**
	 * Retrieves the value of certain attribute
	 * @param name of attribute you want
	 * @return value of attribute
	 */
	public float getFloatValue(String attribute) {
	    return noderef.getFloatValue(attribute);
	}

	/**
	 * Retrieves the value of certain attribute
	 * @param name of attribute you want
	 * @return value of attribute
	 */
	public long getLongValue(String attribute) {
	    return noderef.getLongValue(attribute);
	}


	/**
	 * Retrieves the value of certain attribute
	 * @param name of attribute you want
	 * @return value of attribute
	 */
	public double getDoubleValue(String attribute) {
	    return noderef.getDoubleValue(attribute);
	}

	/**
	 * Retrieves the value of certain attribute
	 * @param name of attribute you want
	 * @return value of attribute
	 */
	public byte[] getByteValue(String attribute) {
	    return noderef.getByteValue(attribute);
	}


	/**
	 * Retrieves the value of certain attribute
	 * @param name of attribute you want
	 * @return value of attribute
	 */
	public String getStringValue(String attribute) {
	    return noderef.getStringValue(attribute);
	}

	/**
	* Commit the node to the database.
	* Makes this node and/or the changes made to this node visible to the cloud.
    * If this method is called for the first time on this node it will make
    * this node visible to the cloud, otherwise the modifications made to
    * this node using the set methods will be made visible to the cloud.
    * This action fails if the current node is not in edit mode.
    * If the node is in a transaction, nothing happens - actual committing occurs through the transaction.
	*/
	public void commit() {
	    Edit(ACTION_COMMIT);
	    // ignore commit in transaction (transaction commits)
	    if (!(cloud instanceof Transaction)) {
	        MMObjectNode node= getNode();
	        if (isnew) {
//	            node.insert(cloud.getUserName());
                node.insert("bridge");
	            cloud.createSecurityInfo(getNodeNumber());
	            isnew=false;
	        } else {
	            node.commit();
	        }
	        // remove the temporary node
	        BasicCloudContext.tmpObjectManager.deleteTmpNode(account,""+temporaryNodeId);
	        temporaryNodeId=-1;
	    }	
	};

	/**
	 * Cancel changes to a node
	 * This fails if the current node is not in edit mode.
	 * If the node is in a transaction, nothing happens - actual committing occurs through the transaction.
	 */
	public void cancel() {
	    Edit(ACTION_COMMIT);
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
	            // should we update the node?, reset fields? etc...
	        }
	        temporaryNodeId=-1;
	    }	
	};
	
	/**
	 * Removes the Node
	 */
	public void remove() {
	    remove(true);
	};

	private void remove(boolean removeRelations) {
        Edit(ACTION_REMOVE);
        if (isnew) {
    	    // remove a temporary node (no true instantion yet, no relations)
	        BasicCloudContext.tmpObjectManager.deleteTmpNode(account,""+temporaryNodeId);
	        // remove from the Transaction
	        // note that the node is immediately destroyed !
	        // possibly older edits will fail if they refernce this node
	        if (cloud instanceof Transaction) {
	            ((BasicTransaction)cloud).remove(""+temporaryNodeId);
	        }
        } else {
            // remove a node that is edited, i.e. that already exists
            // check relations first!
            if (removeRelations) {
                // option set, remove relations
               deleteRelations(-1);
            } else {
                // option unset, fail if any relations exit
	            int relations = getNode().getRelationCount();
	            if(relations!=0) {
	                throw new BridgeException("This node cannot be removed. It has "+relations+" relations attached to it.");
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
	            node.parent.removeNode(node);
	        }
        }
        // the node does not exist anymore, so invalidate all references.
	    temporaryNodeId=-1;
        noderef=null;
	}
	
	/**
	 * Converts the node to a string
	 */
	 public String toString() {
	    return noderef.toString();
	 };

	/**
 	 * Removes all relations of certain type.
	 * @param type Type of relation (-1 = don't care)
	 */
	private void deleteRelations(int type) {
	    RelDef reldef=mmb.getRelDef();
	    Enumeration e = getNode().getRelations();
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
                        node.parent.removeNode(node);
                    }
                 }
            }
	    }
	};
	
	
	/**
	 * Removes all relations of the node.
	 */
	public void removeRelations() {
	    deleteRelations(-1);
	}

	/**
 	 * Removes all relations of this node of a certain type.
	 * @param type of relation
	 */
	public void removeRelations(String type) {
	    RelDef reldef=mmb.getRelDef();
    	int rType=reldef.getGuessedNumber(type);
    	if (rType==-1) {
    	    throw new BridgeException("Cannot find relation type.");
    	} else {
    	    deleteRelations(rType);
    	}
	};

	/**
	 * Retrieve all relations of this node
	 * @return a code>List</code> of all relations of Node
	 */
	private RelationList getRelations(int type) {	
	
	    Vector relvector=new Vector();
	    Enumeration e=getNode().getRelations() ;
	    NodeManager insrelman = cloud.getNodeManager("insrel");
	    if (e!=null) {
	        while (e.hasMoreElements()) {
	            MMObjectNode mmnode=(MMObjectNode)e.nextElement();
	            if ((type==-1) || (mmnode.getIntValue("rnumber")==type)) {
//	                if (cloud.check(Operation.READ, mmnode.getIntValue("number"))) {
	                    relvector.add(mmnode);
//	                }
	            }
	        }
        }
        return new BasicRelationList(relvector,cloud,insrelman);
	};
	
	/**
	 * Retrieve all relations of this node
	 * @return a code>List</code> of all relations of Node
	 */
	public RelationList getRelations() {	
	    return getRelations(-1);
	};

	/**
	 *gets all relations of a certain type
	 * @param type of relation
	 * @return a code>List</code> of all relations of the Node of a certain type
	 */
	public RelationList getRelations(String type) {
	    Vector relvector=new Vector();
	    int rType=mmb.getRelDef().getGuessedNumber(type);
    	if (rType==-1) {
    	    throw new BridgeException("Relation type "+type+" does not exist.");
    	} else {
    	    return getRelations(rType);
    	}
	};

	/**
	 * Checks whether the Node has any relations
	 * @return <code>true</code> if the node has relations
	 */
	public boolean hasRelations() {
	    return getNode().hasRelations();
	};

	
	/**
	 * Count the relations attached to the Node
	 * @return number of relations
	 */
	public int countRelations() {
	    return getRelations().size();
	};

	/**
	 * Count the relations of a specific type attached to the Node 
	 * @return number of relations of a specific type
	 */
	public int countRelations(String type) {
        return getRelations(type).size();
	};

	/**
	 * Retrieve all related Nodes
	 * @return a code>List</code> of all related Nodes
	 */
	public NodeList getRelatedNodes() {
	    Vector relvector=new Vector();
	    Enumeration e=getNode().getRelatedNodes().elements();
	    if (e!=null) {
	        while (e.hasMoreElements()) {
	            MMObjectNode mmnode=(MMObjectNode)e.nextElement();
//	            if (cloud.check(Operation.READ, mmnode.getIntValue("number"))) {
	                relvector.add(mmnode);
//	            }
	        }
	    }
        return new BasicNodeList(relvector,cloud);
	};

	/**
	 * Retrieve all related nodes maintained by a given NodeManager
	 * @param type name of the NodeManager of the related nodes
	 * @return a <code>List</code> of all related nodes of the given manager
	 */
	public NodeList getRelatedNodes(String type) {
	    Vector relvector=new Vector();
	    Enumeration e=getNode().getRelatedNodes(type).elements();
	    if (e!=null) {
	        while (e.hasMoreElements()) {
	            MMObjectNode mmnode=(MMObjectNode)e.nextElement();
//	            if (cloud.check(Operation.READ, mmnode.getIntValue("number"))) {
	                relvector.add(mmnode);
//	            }
	        }
	    }
        return new BasicNodeList(relvector,cloud);
    }

	/**
	 * Count all related nodes maintained by a given NodeManager
	 * @param type name of the NodeManager of the related nodes
	 * @return number of related nodes of a specific type
	 */
	public int countRelatedNodes(String type) {
	    return getNode().getRelationCount(type);
	};
	
	/**
     * Retrieves the aliases of this node
     * @return a code>List</code> with the alias names
     */
    public List getAliases() {
	    Vector aliasvector=new Vector();
	    OAlias alias=mmb.OAlias;
	    if (alias!=null) {
	        for(Enumeration e=alias.search("WHERE "+"destination"+"="+getNodeNumber()); e.hasMoreElements();) {
	            MMObjectNode node=(MMObjectNode)e.nextElement();
	            aliasvector.add(node.getStringValue("name"));
	        }
	    }
        return aliasvector;
    };

	/**
     * Add an alias for this node
     * @param aliasName the name of the alias (need to be unique)
     */
    public void addAlias(String aliasName) {
        Edit(ACTION_EDIT);
        if (cloud instanceof Transaction) {
	        NodeManager aliasManager=cloud.getNodeManager("oalias");
	        Node aliasNode=aliasManager.createNode();
	        aliasNode.setStringValue("name",aliasName);
	        // set the tmp field _destination to the temporaryNodeId of the node
	        // this will be resolved by the transaction manager
	        aliasNode.setValue("_destination", getValue("_number"));
	    } else if (isnew) {
            throw new BridgeException("Cannot add alias to a new node that has not been committed.");
        } else {
            getNode().parent.createAlias(getNodeNumber(),aliasName);
        }
    }

    /**
     * Delete one or all aliasses of this node
     * @param aliasName the name of the alias (null means all aliases)
     */
    private void deleteAliases(String aliasName) {
        Edit(ACTION_EDIT);
	    // A new node cannot have any aliases, except when in a transaction.
	    // However, there is no point in adding aliasses to a ndoe you plan to delete,
	    // so no attempt has been made to rectify this (cause its not worth all the trouble).
	    // If people remove a node for which they created aliases in the same transaction, that transaction will fail.
	    // Live with it.
	    if (!isnew) {
	        String sql = "WHERE (destination"+"="+getNodeNumber()+")";
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
                        alias.removeNode(node);
                    }
    	        }
    	    }
	    }
    };

    /**
     * Remove an alias of this node
     * @param aliasName the name of the alias
     */
    public void removeAlias(String aliasName) {
        deleteAliases(aliasName);
    };

    /**
     * Adds a relation to this node
     * @param destinationNode the node to which you want to relate this node
	 * @param relationManager The relation manager you want to use
	 * @return the added relation
     */
    public Relation createRelation(Node destinationNode, RelationManager relationManager) {
        Edit(ACTION_LINK);
        if (relationManager.getCloud() != cloud) {
            throw new BridgeException("Relation type and node are not in the same transaction or in different clouds");
        }
        if (destinationNode.getCloud() != cloud) {
            throw new BridgeException("Destination and node are not in the same transaction or in different clouds");
        }
        if (!(cloud instanceof Transaction)  && isnew) {
            throw new BridgeException("Cannot add a relation to a new node that has not been committed.");
	    }
	    Relation relation = relationManager.createRelation(this,destinationNode);
        return relation;
    };


    /**
    * Compares two objects, and returns true if they are equal.
    * This effectively means that both objects are nodes, and they both refer to the same objectnode
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
        return getNodeNumber();
    };
}
