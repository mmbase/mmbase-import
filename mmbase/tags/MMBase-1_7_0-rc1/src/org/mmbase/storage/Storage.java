/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage;

import org.mmbase.module.core.*;
import org.mmbase.storage.search.*;

/**
 * The Storage interface defines a storage device - a location where MMBase objects are kept,
 * typically a database.
 * The interface contains methods that can be used to query the storage, insert, update, or remove objects,
 * or to change object definitions (adding fields, etc.).
 *
 * @deprecated This code is scheduled for removal once MMBase has been fully converted to the new
 *             StorageManager implementation.
 * @author Pierre van Rooden
 * @since MMBase-1.6
 * @version $Id: Storage.java,v 1.4 2004-01-27 12:04:45 pierre Exp $
 */
public interface Storage extends SearchQueryHandler {

    /**
     * Returns whether this storage layer allows for builder to be a parent builder
     * (that is, other builders can 'extend' this builder and its storage device).
     *
     * @since MMBase-1.6
     * @param builder the builder to test
     * @return true if the builder can be extended
     */
    public boolean isAllowedParentBuilder(MMObjectBuilder builder);

    /**
     * Registers a builder as a parent builder (that is, other builders can 'extend' this
     * builder and its storage device).
     * At the least, this code should check whether the builder is allowed as a parent builder,
     * and throw an exception if this is not possible.
     *
     * @since MMBase-1.6
     * @param parent the parent builder to register
     * @param child the builder to register as the parent's child
     * @throws StorageException when the support layer does not allow extension of this builder
     */
    public void registerParentBuilder(MMObjectBuilder parent, MMObjectBuilder child)
        throws StorageException;

    /**
     * Returns whether rollback on storage level is supported.
     * @return true if transactions are supported
     */
    public boolean supportsRollback();

    /**
     * Retrieve a large text for a specified object field.
     * Implement this method to allow for optimization of storing and retrieving large texts.
     * @param node the node to retrieve the text from
     * @param fieldname the name of the field to retrieve
     * @return the retrieved text
     */
    public String getText(MMObjectNode node,String fieldname);


    /**
     * Retrieve a large binary object (byte array) for a specified object field.
     * Implement this method to allow for optimization of storing and retrieving binary objects.
     * @param node the node to retrieve the byte array from
     * @param fieldname the name of the field to retrieve
     * @return the retrieved byte array
     */
    public byte[] getBytes(MMObjectNode node,String fieldname);

    /**
     * Returns a newly created transaction object.
     * @return the new transaction
     * @throws StorageException if the transaction could not be created
     */
    public Transaction createTransaction() throws StorageException;

    /**
     * Gives an unique number for a node to be inserted.
     * This method should work with multiple mmbases
     * @return unique number
     */
    public int createKey();

    /**
     * Gives an unique number for a node to be inserted.
     * This method should work with multiple mmbases
     * @param trans the transaction to use for obtaining the key
     * @return unique number
     * @throws StorageException if an error occurred during key generation
     */
    public int createKey(Transaction trans) throws StorageException;

    /**
     * This method inserts a new object, and registers the change.
     * Only fields with states of DBSTATE_PERSISTENT or DBSTATE_SYSTEM are stored.
     * @param node The node to insert
     * @return The (new) number for this node, or -1 if an error occurs.
     */
    public int insert(MMObjectNode node);

    /**
     * This method inserts a new object within a transaction, and registers the change.
     * Only fields with states of DBSTATE_PERSISTENT or DBSTATE_SYSTEM are stored.
     * @param node The node to insert
     * @param trans the transaction to perform the insert in
     * @return The (new) number for this node, or -1 if an error occurs.
     * @throws StorageException if an error occurred during insert
     */
    public int insert(MMObjectNode node, Transaction trans) throws StorageException;

    /**
     * Commit this node to the specified builder.
     * @param node The node to commit
     * @return true of succesful, false otherwise
     */
    public boolean commit(MMObjectNode node);

    /**
     * Commit this node to the specified builder within a transaction.
     * @param node The node to commit
     * @param trans the transaction to perform the insert in
     * @return true of succesful, false otherwise
     * @throws StorageException if an error occurred during commit
     */
    public boolean commit(MMObjectNode node, Transaction trans) throws StorageException;

    /**
     * Delete a node
     * @param node The node to delete
     * @return <code>true</code> if succesful
     */
    public boolean delete(MMObjectNode node);

    /**
     * Delete a node within a transaction
     * @param node The node to delete
     * @param trans the transaction to perform the insert in
     * @throws StorageException if an error occurred during delete
     * @return <code>true</code> if succesful
     */
    public boolean delete(MMObjectNode node, Transaction trans) throws StorageException;

    /**
     * Select a node from a specified builder
     * @param builder The builder to select from
     * @param number the number of the node
     * @return the MMObjectNode that was found, or null f it doesn't exist
     */
    public MMObjectNode getNode(MMObjectBuilder builder, int number);

    /**
     * Select a node from a specified builder
     * @param builder The builder to select from
     * @param number the number of the node
     * @param trans the transaction to perform the insert in
     * @throws StorageException if an error occurred during the get
     * @return the MMObjectNode that was found, or null f it doesn't exist
     */
    public MMObjectNode getNode(MMObjectBuilder builder, int number, Transaction trans) throws StorageException;

    /**
     * Returns the nodetype for a specified nodereference
     * @param number the number of the node
     * @return int the object type or -1 if not found
     */
    public int getNodeType(int number);

    /**
     * Returns the nodetype for a specified nodereference
     * @param number the number of the node
     * @param trans the transaction to perform the insert in
     * @return int the object type or -1 if not found
     * @throws StorageException if an error occurred during selection
     */
    public int getNodeType(int number, Transaction trans) throws StorageException;

    /**
     * Create a storage for the specified builder.
     * @param builder the builder to create the storage for
     * @return true if the storage was succesfully created
     */
    public boolean create(MMObjectBuilder builder);

    /**
     * Create a storage for the specified builder.
     * @param builder the builder to create the storage for
     * @param trans the transaction to perform the create in
     * @return true if the storage was succesfully created
     * @throws StorageException if an error occurred during the creation fo the table
     */
    public boolean create(MMObjectBuilder builder, Transaction trans) throws StorageException;

    /**
     * Create the object storage (the storage where to register all objects).
     * @return true if the storage was succesfully created
     */
    public boolean createObjectStorage();

    /**
     * Create the object storage (the storage where to register all objects) within a transaction
     * @param trans the transaction to perform the create in
     * @return true if the storage was succesfully created
     * @throws StorageException if an error occurred during the caretion of the object storage
     */
    public boolean createObjectStorage(Transaction trans) throws StorageException;

    /**
     * Tells if a storage for the builder already exists
     * @param builder the builder to check
     * @return true if storage exists, false if storage doesn't exists
     */
    public boolean created(MMObjectBuilder builder);

    /**
     * Return number of objects in a builder
     * @param builder the builder whose objects to count
     * @return the number of objects the builder has, or -1 if the builder does not exist.
     */
    public int size(MMObjectBuilder builder);

    /**
     * Drops the storage of this builder.
     * @param builder the builder whose storage to drop
     * @return true if succesful
     */
    public boolean drop(MMObjectBuilder builder);

    /**
     * Adds a field to the storage of this builder.
     * @param builder the builder whose storage to change
     * @param fieldname the name fo the field to add
     * @return true if succesful
     */
    public boolean addField(MMObjectBuilder builder,String fieldname);

    /**
     * Deletes a field from the storage of this builder.
     * @param builder the builder whose storage to change
     * @param fieldname the name fo the field to delete
     * @return true if succesful
     */
    public boolean removeField(MMObjectBuilder builder,String fieldname);

    /**
     * Changes a field to the storage of this builder.
     * @param builder the builder whose storage to change
     * @param fieldname the name fo the field to change
     * @return true if succesful
     */
    public boolean changeField(MMObjectBuilder builder,String fieldname);

    /**
     * Changes the storage of a builder to match its new configuration.
     * @param builder the builder whose storage to change
     * @return true if succesful
     */
    public boolean updateStorage(MMObjectBuilder builder);

}
