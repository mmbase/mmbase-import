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

    // reference to the Node Type (which also references the Cloud)
    protected NodeType nodeType;

    // reference to mmbase
    protected org.mmbase.module.core.MMBase mmb;

    // reference to actual object
    protected MMObjectNode node;

    protected boolean isnew = false;

  	BasicNode(MMObjectNode node, Cloud cloud) {
  	    this.nodeType=cloud.getNodeType(node.parent.oType);
  	    this.node=node;
  	    this.mmb = ((BasicCloudContext)cloud.getCloudContext()).mmb;
  	}
  	
  	BasicNode(MMObjectNode node, NodeType nodeType) {
  	    this.nodeType=nodeType;
  	    this.node=node;
  	    this.isnew=true;
  	    this.mmb = ((BasicCloudContext)nodeType.getCloud().getCloudContext()).mmb;
  	}
  	
  	/**
     * Retrieves the cloud where this node is part of.
     */
    public Cloud getCloud() {
        return nodeType.getCloud();
    }

	/**
     * Retrieves the type of this node
     */
    public NodeType getNodeType() {
        return nodeType;
    }
	
	/**
     * Retrieves the node ID
     */
    public int getNodeID() {
        return node.getIntValue("number");
    }
	
	/** 
	 * Set the value of certain attribute
	 * @param attribute name of field
	 * @param value of attribute
	 */
	public void setValue(String attribute, Object value) {
	    node.setValue(attribute,value);
	}

	/**
	 * Set the value of certain attribute
	 * @param attribute name of field
	 * @param value of attribute
	 */
	public void setIntValue(String attribute, int value) {
	    node.setValue(attribute,new Integer(value));
	}

	/**
	 * Set the value of certain attribute
	 * @param attribute name of field
	 * @param value of attribute
	 */
	public void setFloatValue(String attribute, float value) {
	    node.setValue(attribute,new Float(value));
	}

	/**
	 * Set the value of certain attribute
	 * @param attribute name of field
	 * @param value of attribute
	 */
	public void setDoubleValue(String attribute, double value) {
	    node.setValue(attribute,new Double(value));
	}

	/**
	 * Set the value of certain attribute
	 * @param attribute name of field
	 * @param value of attribute
	 */
	public void setByteValue(String attribute, byte[] value) {
	    node.setValue(attribute,value);
	}

	/**
	 * Set the value of certain attribute
	 * @param attribute name of field
	 * @param value of attribute
	 */
	public void setLongValue(String attribute, long value) {
	    node.setValue(attribute,new Long(value));
	}

	/**
	 * Set the value of certain attribute
	 * @param attribute name of field
	 * @param value of attribute
	 */
	public void setStringValue(String attribute, String value) {
	    node.setValue(attribute,value);
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
	    node.parent.removeNode(node);
	};

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
	    node.parent.removeRelations(node);
	}

	/**
 	 * Removes all relations of certain type of this node
	 * @param type of relation
	 */
	public void removeRelations(String type) {
	
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
	 * @return all relations of Node
	 */
	public Iterator getRelations() {	
	    Vector relvector=new Vector();
	    Enumeration e=node.getRelations() ;
	    if (e!=null) {
	        while (e.hasMoreElements()) {
	            MMObjectNode mmnode=(MMObjectNode)e.nextElement();
	            Relation node = new BasicRelation(mmnode, nodeType.getCloud());
	            relvector.add(node);
	        }
        }
        return relvector.iterator();
	};

	/**
	 *gets all relations of a certain type
	 * @param type of relation
	 * @return all relations of the Node of a certain type
	 */
	public Iterator getRelations(String type) {
	    Vector relvector=new Vector();
	    int rType=mmb.getRelDef().getGuessedNumber(type);
	    Enumeration e=node.getRelations() ;
	    if (e!=null) {
	        while (e.hasMoreElements()) {
	            MMObjectNode mmnode=(MMObjectNode)e.nextElement();
	            if (mmnode.getIntValue("rnumber")==rType) {
	                Relation node = new BasicRelation(mmnode, nodeType.getCloud());
	                relvector.add(node);
	            }
	        }
	    }
        return relvector.iterator();
	};

	/**
	 * Count the relations attached to the Node
	 * @return number of relations
	 */
	public int countRelations() {
	    return node.getRelationCount();
	};

	/**
	 * Count the relations of a specific type attached to the Node 
	 * @return number of relations of a specific type
	 */
	public int countRelations(String type) {
	    // this doesn't work, obviously...
	    int count=0;
	    int rType=mmb.getRelDef().getGuessedNumber(type);
	    Enumeration e=node.getRelations() ;
	    if (e!=null) {
	        while (e.hasMoreElements()) {
	            MMObjectNode mmnode=(MMObjectNode)e.nextElement();
	            if (mmnode.getIntValue("rnumber")==rType) {
	                count++;
	            }
	        }
	    }
        return count;
	};

	/**
	 * Retrieve all related Nodes
	 * @return all related Nodes
	 */
	public Iterator getRelatedNodes() {
	    Vector relvector=new Vector();
	    Enumeration e=node.getRelatedNodes().elements();
	    if (e!=null) {
	        while (e.hasMoreElements()) {
	            MMObjectNode mmnode=(MMObjectNode)e.nextElement();
	            Node node = new BasicNode(mmnode, nodeType.getCloud());
	            relvector.add(node);
	        }
	    }
        return relvector.iterator();
	};

	/**
	 * Retrieve all related nodes of a certain type
	 * @return all related nodes of a certain type
	 */
	public Iterator getRelatedNodes(String type) {
	    Vector relvector=new Vector();
	    Enumeration e=node.getRelatedNodes(type).elements();
	    if (e!=null) {
	        while (e.hasMoreElements()) {
	            MMObjectNode mmnode=(MMObjectNode)e.nextElement();
	            Node node = new BasicNode(mmnode, nodeType.getCloud());
	            relvector.add(node);
	        }
	    }
        return relvector.iterator();
    }

	/**
     * Retrieves the aliases of this node
     * @return an Iterator with the alias names
     */
    public Iterator getAliases() {
	    Vector relvector=new Vector();
	    OAlias alias=mmb.OAlias;
	    if (alias!=null) {
	        for(Enumeration e=alias.search("WHERE "+"destination"+"="+getNodeID()); e.hasMoreElements();) {
	            MMObjectNode mmnode=(MMObjectNode)e.nextElement();
	            relvector.add(node.getStringValue("name"));
	        }
	    }
        return relvector.iterator();
    };

	/**
     * Add an alias for this node
     * @param aliasName the name of the alias (need to be unique)
     */
    public void addAlias(String aliasName) {
        node.parent.createAlias(getNodeID(),aliasName);
    }

    /**
     * Remove an alias of this node
     * @param aliasName the name of the alias
     */
    public void removeAlias(String aliasName) {
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
	 * @param relationtype The type of relation you want to use
	 * @return the added relation
     */
    public Relation addRelation(Node destinationNode, RelationType relationType) {
        // check on insert : cannot craete relation is not committed
	    if (isnew) {
	        return null;
	    } else {
	        Relation relation = relationType.addRelation(this,destinationNode);
            return relation;
        }
    };
}
