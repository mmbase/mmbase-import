/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;
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

    public static final int ACTION_EDIT = 1;     // edit node, or change aliasses
    public static final int ACTION_REMOVE = 2;   // remove node
    public static final int ACTION_ADDRELATION = 3; // add relations
    public static final int ACTION_REMOVERELATION = 4; // remove relations


    // reference to the NodeManager (which also references the Cloud)
    protected NodeManager nodeManager;

    // reference to mmbase
    protected org.mmbase.module.core.MMBase mmb;

    // reference to actual object
    protected MMObjectNode node;

    protected boolean isnew = false;

  	BasicNode(MMObjectNode node, Cloud cloud) {
  	    this.nodeManager=cloud.getNodeManager(node.parent.oType);
  	    this.node=node;
  	    this.mmb = ((BasicCloudContext)cloud.getCloudContext()).mmb;
  	}
  	
  	BasicNode(MMObjectNode node, NodeManager nodeManager) {
  	    this.nodeManager=nodeManager;
  	    this.node=node;
  	    this.isnew=true;
  	    this.mmb = ((BasicCloudContext)nodeManager.getCloud().getCloudContext()).mmb;
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
    public int getNodeID() {
        return node.getIntValue("number");
    }
	
    // Edit his node
    // Check whether edits are allowed and prepare a node for edits if needed
    // @param action The action to perform. Not yet used.
    protected void Edit(int action) {
	    if (nodeManager instanceof TemporaryNodeManager) {
            throw new SecurityException("Cannot edit a temporary node.");
	    }

    }
	
	/**
	 * Set the value of certain attribute
	 * @param attribute name of field
	 * @param value of attribute
	 */
	public void setValue(String attribute, Object value) {
	    Edit(ACTION_EDIT);
	    node.setValue(attribute,value);
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
	    return node.getValue(attribute);
	}

	/**
	 * Retrieves the value of certain attribute
	 * @param name of attribute you want
	 * @return value of attribute
	 */
	public int getIntValue(String attribute) {
	    return node.getIntValue(attribute);
	}

	/**
	 * Retrieves the value of certain attribute
	 * @param name of attribute you want
	 * @return value of attribute
	 */
	public float getFloatValue(String attribute) {
	    return node.getFloatValue(attribute);
	}

	/**
	 * Retrieves the value of certain attribute
	 * @param name of attribute you want
	 * @return value of attribute
	 */
	public long getLongValue(String attribute) {
	    return node.getLongValue(attribute);
	}


	/**
	 * Retrieves the value of certain attribute
	 * @param name of attribute you want
	 * @return value of attribute
	 */
	public double getDoubleValue(String attribute) {
	    return node.getDoubleValue(attribute);
	}

	/**
	 * Retrieves the value of certain attribute
	 * @param name of attribute you want
	 * @return value of attribute
	 */
	public byte[] getByteValue(String attribute) {
	    return node.getByteValue(attribute);
	}


	/**
	 * Retrieves the value of certain attribute
	 * @param name of attribute you want
	 * @return value of attribute
	 */
	public String getStringValue(String attribute) {
	    return node.getStringValue(attribute);
	}

	/**
	 * Commit the node to the database
	 */
	public void commit() {
	    if (isnew) {
	        node.insert("system");
	        isnew=false;
	    } else {
	        node.commit();
	    }
	};

	/**
	 * Removes the Node
	 */
	public void remove() {
	    remove(false);
	};

	/**
	 * Removes the Node.
	 * @param removeRelations determines whether attached relations are autiomatically deleted. if <code>false</code>,
	 *        the remove fails if any relations exist.
	 */
	public void remove(boolean removeRelations) {
        Edit(ACTION_REMOVE);
        removeRelations();
	    node.parent.removeNode(node);
	}
	
	/**
	 * Converts the node to a string
	 */
	 public String toString() {
	    return node.toString();
	 };

	/**
	 * Removes all relations of the node
	 */
	public void removeRelations() {
        Edit(ACTION_REMOVERELATION);
	    node.parent.removeRelations(node);
	}

	/**
 	 * Removes all relations of certain type of this node
	 * @param type of relation
	 */
	public void removeRelations(String type) {
        Edit(ACTION_REMOVERELATION);
	
	    // This should be handled in a core class,
	    // for the moment we implement it here
	
	    RelDef reldef=mmb.getRelDef();
	    int rType=reldef.getGuessedNumber(type);
	    Enumeration e=node.getRelations() ;
	    if (e!=null) {
	        while (e.hasMoreElements()) {
	            MMObjectNode node=(MMObjectNode)e.nextElement();
	            if (node.getIntValue("rnumber")==rType) {
                    reldef.removeNode(node);
                }
	        }
	    }
	};

	/**
	 * Retrieve all relations of this node
	 * @return a code>List</code> of all relations of Node
	 */
	public List getRelations() {	
	    Vector relvector=new Vector();
	    Enumeration e=node.getRelations() ;
	    if (e!=null) {
	        while (e.hasMoreElements()) {
	            MMObjectNode mmnode=(MMObjectNode)e.nextElement();
	            Relation node = new BasicRelation(mmnode, nodeManager.getCloud());
	            relvector.add(node);
	        }
        }
        return relvector;
	};

	/**
	 *gets all relations of a certain type
	 * @param type of relation
	 * @return a code>List</code> of all relations of the Node of a certain type
	 */
	public List getRelations(String type) {
	    Vector relvector=new Vector();
	    int rType=mmb.getRelDef().getGuessedNumber(type);
	    Enumeration e=node.getRelations() ;
	    if (e!=null) {
	        while (e.hasMoreElements()) {
	            MMObjectNode mmnode=(MMObjectNode)e.nextElement();
	            if (mmnode.getIntValue("rnumber")==rType) {
	                Relation node = new BasicRelation(mmnode, nodeManager.getCloud());
	                relvector.add(node);
	            }
	        }
	    }
        return relvector;
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
	public List getRelatedNodes() {
	    Vector relvector=new Vector();
	    Enumeration e=node.getRelatedNodes().elements();
	    if (e!=null) {
	        while (e.hasMoreElements()) {
	            MMObjectNode mmnode=(MMObjectNode)e.nextElement();
	            Node node = new BasicNode(mmnode, nodeManager.getCloud());
	            relvector.add(node);
	        }
	    }
        return relvector;
	};

	/**
	 * Retrieve all related nodes maintained by a given NodeManager
	 * @param type name of the NodeManager of the related nodes
	 * @return a <code>List</code> of all related nodes of the given manager
	 */
	public List getRelatedNodes(String type) {
	    Vector relvector=new Vector();
	    Enumeration e=node.getRelatedNodes(type).elements();
	    if (e!=null) {
	        while (e.hasMoreElements()) {
	            MMObjectNode mmnode=(MMObjectNode)e.nextElement();
	            Node node = new BasicNode(mmnode, nodeManager.getCloud());
	            relvector.add(node);
	        }
	    }
        return relvector;
    }

	/**
	 * Count all related nodes maintained by a given NodeManager
	 * @param type name of the NodeManager of the related nodes
	 * @return number of related nodes of a specific type
	 */
	public int countRelatedNodes(String type) {
	    return node.getRelationCount(type);
	};
	
	/**
     * Retrieves the aliases of this node
     * @return a code>List</code> with the alias names
     */
    public List getAliases() {
	    Vector aliasvector=new Vector();
	    OAlias alias=mmb.OAlias;
	    if (alias!=null) {
	        for(Enumeration e=alias.search("WHERE "+"destination"+"="+getNodeID()); e.hasMoreElements();) {
	            MMObjectNode mmnode=(MMObjectNode)e.nextElement();
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
        node.parent.createAlias(getNodeID(),aliasName);
    }

    /**
     * Remove an alias of this node
     * @param aliasName the name of the alias
     */
    public void removeAlias(String aliasName) {
        Edit(ACTION_EDIT);
	    OAlias alias=mmb.OAlias;
	    if (alias!=null) {
	        for(Enumeration e=alias.search("WHERE (destination"+"="+getNodeID()+") AND (name='"+aliasName+"')"); e.hasMoreElements();) {
	            MMObjectNode mmnode=(MMObjectNode)e.nextElement();
	            alias.removeNode(node);
	        }
	    }
    };

    /**
     * Adds a relation to this node
     * @param destinationNode the node to which you want to relate this node
	 * @param relationManager The relation manager you want to use
	 * @return the added relation
     */
    public Relation addRelation(Node destinationNode, RelationManager relationManager) {
        Edit(ACTION_ADDRELATION);
        // check on insert : cannot create relation is not committed
	    if (isnew) {
	        return null;
	    } else {
	        Relation relation = relationManager.addRelation(this,destinationNode);
            return relation;
        }
    };
}
