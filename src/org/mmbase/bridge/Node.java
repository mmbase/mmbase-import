/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;
import org.mmbase.module.core.*;
import java.util.List;

/**
 * Describes an object in the cloud.
 *
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 */
public interface Node {

  	/**
     * Retrieves the cloud where this node is part of.
     */
    public Cloud getCloud();

	/**
     * Retrieves the type of this node
     */
    public NodeType getNodeType();
	
	/**
     * Retrieves the node ID
     */
    public int getNodeID();
	
	/** 
	 * Set the value of certain attribute
	 * @param attribute name of field
	 * @param value of attribute
	 */
	public void setValue(String attribute, Object value); 

	/** 
	 * Set the value of certain attribute
	 * @param attribute name of field
	 * @param value of attribute
	 */
	public void setIntValue(String attribute, int value); 

	/** 
	 * Set the value of certain attribute
	 * @param attribute name of field
	 * @param value of attribute
	 */
	public void setFloatValue(String attribute, float value); 

	/** 
	 * Set the value of certain attribute
	 * @param attribute name of field
	 * @param value of attribute
	 */
	public void setDoubleValue(String attribute, double value); 

	/** 
	 * Set the value of certain attribute
	 * @param attribute name of field
	 * @param value of attribute
	 */
	public void setByteValue(String attribute, byte[] value); 

	/** 
	 * Set the value of certain attribute
	 * @param attribute name of field
	 * @param value of attribute
	 */
	public void setLongValue(String attribute, long value); 

	/** 
	 * Set the value of certain attribute
	 * @param attribute name of field
	 * @param value of attribute
	 */
	public void setStringValue(String attribute, String value); 

	/** 
	 * Retrieves the value of certain attribute
	 * @param name of attribute you want 
	 * @return value of attribute
	 */
	public Object getValue(String fieldName);

	/** 
	 * Retrieves the value of certain attribute
	 * @param name of attribute you want 
	 * @return value of attribute
	 */
	public int getIntValue(String fieldName);

	/** 
	 * Retrieves the value of certain attribute
	 * @param name of attribute you want 
	 * @return value of attribute
	 */
	public float getFloatValue(String fieldName);

	/** 
	 * Retrieves the value of certain attribute
	 * @param name of attribute you want 
	 * @return value of attribute
	 */
	public long getLongValue(String fieldName);

	/** 
	 * Retrieves the value of certain attribute
	 * @param name of attribute you want 
	 * @return value of attribute
	 */
	public double getDoubleValue(String fieldName);

	/** 
	 * Retrieves the value of certain attribute
	 * @param name of attribute you want 
	 * @return value of attribute
	 */
	public byte[] getByteValue(String fieldName);

	/** 
	 * Retrieves the value of certain attribute
	 * @param name of attribute you want 
	 * @return value of attribute
	 */
	public String getStringValue(String fieldName);

	/**
	 * Commit the node to the database
	 */
	public void commit();

	/**
	 * Removes the Node
	 */
	public void remove(); 

	/**
	 * Converts the node to a string
	 */
	 public String toString();

	/**
	 * Removes all relations of the node
	 */
	public void removeRelations();

	/**
 	 * Removes all relations of certain type of this node
	 * @param type of relation
	 */
	public void removeRelations(String type);

	/**
	 * Retrieve all relations of this node
	 * @return a <code>List</code> of all relations of Node
	 */
	public List getRelations();

	/**
	 *gets all relations of a certain type
	 * @param type of relation
	 * @return a <code>List</code> of all relations of the Node of a certain type
	 */
	public List getRelations(String type);
	
	/**
	 * Count the relations attached to the Node
	 * @return number of relations
	 */
	public int countRelations();

	/**
	 * Count the relations of a specific type attached to the Node 
	 * @return number of relations of a specific type
	 */
	public int countRelations(String type);

	/**
	 * Retrieve all related Nodes
	 * @return a <code>List</code> of all related Nodes
	 */
	public List getRelatedNodes();

	/**
	 * Retrieve all related nodes of a certain type
	 * @return a <code>List</code> of all related nodes of a certain type
	 */
	public List getRelatedNodes(String type);

	/**
     * Retrieves the aliases of this node
     * @return a <code>List</code> with the alias anmes
     */
    public List getAliases();

	/**
     * Add an alias for this node
     * @param aliasName the name of the alias (need to be unique)
     */
    public void addAlias(String aliasName);

    /**
     * Remove an alias of this node
     * @param aliasName the name of the alias
     */
    public void removeAlias(String aliasName);

    /**
     * Adds a relation to this node
     * @param destinationNode the node to which you want to relate this node
	 * @param relationtype The type of relation you want to use
	 * @return the added relation
     */
    public Relation addRelation(Node destinationNode, RelationType relationtype);
}
