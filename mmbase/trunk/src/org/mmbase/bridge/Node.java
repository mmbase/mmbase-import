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
     * Returns the node manager for this node.
     */
    public NodeManager getNodeManager();

    /**
     * Returns the unique number for this node. Every node has a unique number
     * wich can be used to refer to it. In addition to this number a node can
     * have one or more aliases.
     *
     * @return the unique number for this node
     * @see    #createAlias(String alias)
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
     * Returns the value of the specified field as a <code>boolean</code>.
     *
     * @param fieldname  the name of the field to be returned
     * @return           the value of the specified field
     */
    public boolean getBooleanValue(String fieldname);

    /**
     * Returns the value of the specified field as a <code>Node</code>.
     * If the value is not itself a Node, this call attempts to convert the
     * original field value to a Node, by trying to retrieve a Node using
     * the field value as a Node number or alias.<br />
     * For instance, getNodeValue("destination"), when run on a OAlias object,
     * returns the referenced destination node (instead of teh number, which is
     * what it normally holds).<br />
     * Mosty, this call is used in cluster nodes (nodes retrived by using the
     * Cloud.getList method. A cluster node returns one of its compound nodes
     * when an appropriate nodemanager name (name from the nodepath) is specified.
     * I.e. getNodeValue("people") will return the people-node in the cluster.
     * If this fails, the method returns <code>null</code>.
     * <br />
     * Notes: the behavior of getNodeValue when called on a field that is not
     * intended to be a node reference is currently undefined and is not
     * encouraged.
     * <br />
     * Calling this method with field "number" or <code>null</code> lets the
     * Node return a reference to itself, regardless of the actual value of the
     * number field or status of the Node.
     *
     * @param fieldname  the name of the field to be returned
     * @return           the value of the specified field
     * @see Cloud#getList
     */
    public Node getNodeValue(String fieldname);

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
     * Removes the Node. Throws an exception if still has relations. Like delete(false).
     */
    public void delete();

    /**
     * Removes the Node.
     * @param deleteRelations a boolean. If true, then first all
     * existing relations with this node will be removed.
     */

    public void delete(boolean deleteRelations);

    /**
     * Converts the node to a string
     */
    public String toString();

    /**
     * Checks whether this node has any relations.
     *
     * @return <code>true</code> if the node has relations
     */
    public boolean hasRelations();

    /**
     * Removes all relation nodes attached to this node.
     */
    public void deleteRelations();

    /**
      * Removes all relation nodes with a certain relation manager that are
     * attached to this node.
     *
     * @param relationManager  the name of the relation manager the removed
     *                         relation nodes should have
     */
    public void deleteRelations(String relationManager);

    /**
     * Returns all relation nodes attached to this node.
     *
     * @return a list of relation nodes
     */
    public RelationList getRelations();

    /**
     * Returns all relation nodes attached to this node that have a specific
     * relation manager.
     *
     * @param relationManager  the name of the relation manager the returned
     *                         relation nodes should have
     * @return                 a list of relation nodes
     */
    public RelationList getRelations(String relationManager);

    /**
     * Returns the number of relations this node has with other nodes.
     *
     * @return the number of relations this node has with other nodes
     */
    public int countRelations();

    /**
     * Returns the number of relation nodes attached to this node that have a
     * specific relation manager.
     *
     * @return the number of relation nodes attached to this node that have a
     *         specific relation manager
     */
    public int countRelations(String relationManager);

    /**
     * Returns all related nodes.
     * The returned nodes are not the nodes directly attached to this node (the
     * relation nodes) but the nodes attached to the relation nodes of this
     * node.
     *
     * @return a list of all related nodes
     */
    public NodeList getRelatedNodes();

    /**
     * Returns all related nodes that have a specific node manager.
     * The returned nodes are not the nodes directly attached to this node (the
     * relation nodes) but the nodes attached to the relation nodes of this
     * node.
     *
     * @param nodeManager  the name of the node manager the returned nodes
     *                     should have
     * @return             a list of related nodes
     */
    public NodeList getRelatedNodes(String nodeManager);

    /**
     * Returns the number of related nodes that have a specific node manager.
     * The counted nodes are not the nodes directly attached to this node (the
     * relation nodes) but the nodes attached to the relation nodes of this
     * node.
     *
     * @param nodeManager  the name of the node manager the counted nodes
     *                     should have
     * @return             the number of related nodes that have a specific node
     *                     manager
     */
    public int countRelatedNodes(String type);

    /**
     * Returns all aliases for this node.
     *
     * @return a list of alias names for this node
     */
    public StringList getAliases();

    /**
     * Create an alias for this node. An alias can be used to refer to a node in
     * addition to his number.
     *
     * @param alias             the alias to be created for this node
     * @throws BridgeException  if the alias allready exists
     */
    public void createAlias(String alias);

    /**
     * Delete an alias for this node.
     *
     * @param alias  the alias to be removed for this node
     */
    public void deleteAlias(String alias);

    /**
     * Adds a relation between this node and a specified node to the cloud.
     *
     * @param destinationNode   the node to which you want to relate this node
     * @param relationManager   the relation manager you want to use
     * @return                  the added relation
     * @throws BridgeException  if the relation manager is not the right one
     *                          for this type of relation
     */
    public Relation createRelation(Node destinationNode,
                                   RelationManager relationManager);

    /**
     * set the Context of the current Node
     *
     * @param context	    	    The context to which the current node should belong,
     * @throws BridgeException      Dunno?
     * @throws SecurityException    When not the approperate rights (change context)
     */
    public void setContext(String context);

    /**
     * get the Context of the current Node
     *
     * @return the current context of the node
     * @throws BridgeException      Dunno?
     * @throws SecurityException    When not the approperate rights (read rights)
     */
    public String getContext();

    /**
     * get the Contextes which can be set to this specific node
     *
     * @return the contextes from which can be chosen
     * @throws BridgeException      Dunno?
     * @throws SecurityException    When not the approperate rights (read rights)
     */
    public StringList getPossibleContexts();

    /**
     * Check write rights on this node.
     *
     * @return                      whether the node may be changed by the current user
     */

    public boolean mayWrite();

    /**
     * Check delete rights on this node.
     *
     * @return                      whether the node may be deleted by the current user
     */

    public boolean mayDelete();

    /**
     * Check link rights on this node.
     *
     * @return                      whether the current user may link to this node
     */

    public boolean mayLink();

    /**
     * Check context-change rights on this node.
     *
     * @return                      whether the current user may change the context of this node
     */
    public boolean mayChangeContext();

}
