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
     * Returns the cloud this node belongs to.
     */
    public Cloud getCloud();

	/**
     * Returns the node manager of this node.
     */
    public NodeManager getNodeManager();
	
	/**
     * Returns the node number
     */
    public int getNumber();
	
	/** 
     * Sets the value of the specified field using an object.
     * For example a field of type <code>int</code> can be set using an
     * <code>Integer</code>.
     * This change will not be visible to the cloud until the commit method is
     * called.
     *
	 * @param fieldname  the name of the field to be updated
	 * @param value      the new value for the given field
	 */
	public void setValue(String fieldname, Object value); 

	/** 
     * Sets the value of the specified field using an <code>int</code>.
     * This change will not be visible to the cloud until the commit method is
     * called.
     *
	 * @param fieldname  the name of the field to be updated
	 * @param value      the new value for the given field
	 */
	public void setIntValue(String fieldname, int value); 

	/** 
     * Sets the value of the specified field using a <code>float</code>.
     * This change will not be visible to the cloud until the commit method is
     * called.
     *
	 * @param fieldname  the name of the field to be updated
	 * @param value      the new value for the given field
	 */
	public void setFloatValue(String fieldname, float value); 

	/** 
     * Sets the value of the specified field using a <code>double</code>.
     * This change will not be visible to the cloud until the commit method is
     * called.
     *
	 * @param fieldname  the name of the field to be updated
	 * @param value      the new value for the given field
	 */
	public void setDoubleValue(String fieldname, double value); 

	/** 
     * Sets the value of the specified field using a <code>byte array</code>.
     * This change will not be visible to the cloud until the commit method is
     * called.
     *
	 * @param fieldname  the name of the field to be updated
	 * @param value      the new value for the given field
	 */
	public void setByteValue(String fieldname, byte[] value); 

	/** 
     * Sets the value of the specified field using a <code>long</code>.
     * This change will not be visible to the cloud until the commit method is
     * called.
     *
	 * @param fieldname  the name of the field to be updated
	 * @param value      the new value for the given field
	 */
	public void setLongValue(String fieldname, long value); 

    /** 
     * Sets the value of the specified field using a <code>String</code>.
     * This change will not be visible to the cloud until the commit method is
     * called.
     *
     * @param fieldname  the name of the field to be updated
     * @param value      the new value for the given field
     */
	public void setStringValue(String fieldname, String value); 

    /** 
     * Returns the value of the specified field as an object. For example a
     * field of type <code>int</code> is returned as an <code>Integer</code>.
     *
     * @param fieldname  the name of the field to be returned
     * @return           the value of the specified field
     */
	public Object getValue(String fieldname);

    /** 
     * Returns the value of the specified field as an <code>int</code>.
     *
     * @param fieldname  the name of the field to be returned
     * @return           the value of the specified field
     */
	public int getIntValue(String fieldname);

    /** 
     * Returns the value of the specified field as a <code>float</code>.
     *
     * @param fieldname  the name of the field to be returned
     * @return           the value of the specified field
     */
	public float getFloatValue(String fieldname);

    /** 
     * Returns the value of the specified field as a <code>long</code>.
     *
     * @param fieldname  the name of the field to be returned
     * @return           the value of the specified field
     */
	public long getLongValue(String fieldname);

    /** 
     * Returns the value of the specified field as a <code>double</code>.
     *
     * @param fieldname  the name of the field to be returned
     * @return           the value of the specified field
     */
	public double getDoubleValue(String fieldname);

    /** 
     * Returns the value of the specified field as a <code>byte array</code>.
     *
     * @param fieldname  the name of the field to be returned
     * @return           the value of the specified field
     */
	public byte[] getByteValue(String fieldname);

    /** 
     * Returns the value of the specified field as a <code>String</code>.
     *
     * @param fieldname  the name of the field to be returned
     * @return           the value of the specified field
     */
	public String getStringValue(String fieldname);

	/**
	* Commit the node to the database.
	* Makes this node and/or the changes made to this node visible to the cloud.
    * If this method is called for the first time on this node it will make
    * this node visible to the cloud, otherwise the modifications made to
    * this node using the set methods will be made visible to the cloud.
    * This action fails if the current node is not in edit mode.
    * If the node is in a transaction, nothing happens - actual committing occurs through the transaction.
	*/
	public void commit();

	/**
	 * Cancel changes to a node
	 * This fails if the current node is not in edit mode.
	 * If the node is in a transaction, nothing happens - actual committing occurs through the transaction.
	 */
	public void cancel();
	
	/**
	 * Removes the Node
	 */
	public void remove(); 

	/**
	 * Converts the node to a string
	 */
	 public String toString();

	/**
	 * Checks whether the Node has any relations
	 * @return <code>true</code> if the node has relations
	 */
	public boolean hasRelations();
		
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
	public RelationList getRelations();

	/**
	 * Gets all relations of a certain type
	 * @param type of relation
	 * @return a <code>List</code> of all relations of the Node of a certain type
	 */
	public RelationList getRelations(String type);
	
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
	public NodeList getRelatedNodes();

	/**
	 * Retrieve all related nodes maintained by a given NodeManager
	 * @param type name of the NodeManager of the related nodes
	 * @return a <code>List</code> of all related nodes of the given manager
	 */
	public NodeList getRelatedNodes(String type);

	/**
	 * Count all related nodes maintained by a given NodeManager
	 * @param type name of the NodeManager of the related nodes
	 * @return number of related nodes of a specific type
	 */
	public int countRelatedNodes(String type);
	
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
	 * @param relationManager The relation manager you want to use
	 * @return the added relation
     */
    public Relation createRelation(Node destinationNode, RelationManager relationManager);
}
