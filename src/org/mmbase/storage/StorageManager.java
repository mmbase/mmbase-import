/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage;

import org.mmbase.module.core.*;
import org.mmbase.core.CoreField;
import java.io.InputStream;

/**
 * The StorageManager interface defines how to access a storage device.
 * It contains methods that can be used to query the storage, insert, update, or remove objects,
 * or to change object definitions (adding fields, etc.).
 *
 * @author Pierre van Rooden
 * @since MMBase-1.7
 * @version $Id$
 */
public interface StorageManager<SMF extends StorageManagerFactory> {

    /**
     * Returns the version of this factory implementation.
     * The factory uses this number to verify whether it can handle storage configuration files
     * that list version requirements.
     * @return the version as an integer
     */
    double getVersion();

    /**
     * Initializes the manager.
     * Called by a StorageManagerFactory when instantiating the manager with the getStorageManager() method.
     * @param factory the StorageManagerFactory instance that created this storage manager.
     * @throws StorageConfigurationException if the initialization failed
     */
    void init(SMF factory) throws StorageException;

    /**
     * Starts a transaction on this StorageManager instance.
     * All commands passed to the instance will be treated as being in this transaction.
     * If transactions are not supported by the storage, no actual storage transaction is started, but the code continues as if it has.
     * @throws StorageException if the transaction could not be created
     */
    void beginTransaction() throws StorageException;

    /**
     * Closes any transaction that was started and commits all changes.
     * If transactions are not supported by the storage, nothing really happens (as changes are allready committed), but the code continues as if it has.
     * @throws StorageException if a transaction is not currently active, or an error occurred while committing
     */
    void commit() throws StorageException;

    /**
     * Cancels any transaction that was started and rollback changes if possible.
     * If transactions are not supported by the storage, nothing really happens (as changes are allready committed),
     * but the code continues as if it has (through in that case it will return false).
     * @return <code>true</code> if changes were rolled back, <code>false</code> if the transaction was
     * canceled but the storage does not support rollback
     * @throws StorageException if a transaction is not currently active, or an error occurred during rollback
     */
    boolean rollback() throws StorageException;

    /**
     * Gives an unique number for a new node, to be inserted in the storage.
     * This method should work with multiple mmbases
     * @return unique number
     */
    int createKey() throws StorageException;

    /**
     * Retrieve a large text for a specified object field.
     * Implement this method to allow for optimization of storing and retrieving large texts.
     * @param node the node to retrieve the text from
     * @param field the Type of the field to retrieve
     * @return the retrieved text
     * @throws StorageException if an error occurred while retrieving the text value
     */
    String getStringValue(MMObjectNode node, CoreField field) throws StorageException;

    /**
     * Retrieve a large binary object (byte array) for a specified object field.
     * Implement this method to allow for optimization of storing and retrieving binary objects.
     * @param node the node to retrieve the byte array from
     * @param field the CoreField of the field to retrieve
     * @return the retrieved byte array
     * @throws StorageException if an error occurred while retrieving the binary value
     */
    byte[] getBinaryValue(MMObjectNode node, CoreField field) throws StorageException;


    /**
     * @since MMBase-1.8
     */
    InputStream getInputStreamValue(MMObjectNode node, CoreField field) throws StorageException;

    /**
     * This method creates a new object in the storage, and registers the change.
     * Only fields with states of DBSTATE_PERSISTENT or DBSTATE_SYSTEM are stored.
     * @param node The node to insert
     * @return The (new) number for this node, or -1 if an error occurs.
     * @throws StorageException if an error occurred during insert
     */
    int create(MMObjectNode node) throws StorageException;

    /**
     * Commit this node to the specified builder.
     * @param node The node to commit
     * @throws StorageException if an error occurred during commit
     */
    void change(MMObjectNode node) throws StorageException;

    /**
     * Delete a node
     * @param node The node to delete
     * @throws StorageException if an error occurred during delete
     */
    void delete(MMObjectNode node) throws StorageException;

    /**
     * Select a node from a specified builder
     * @param builder The builder to select from
     * @param number the number of the node
     * @return the MMObjectNode that was found, or null f it doesn't exist
     * @throws StorageException if an error occurred during the get
     */
    MMObjectNode getNode(MMObjectBuilder builder, int number) throws StorageException;

    /**
     * Returns the nodetype for a specified nodereference
     * @param number the number of the node
     * @return int the object type or -1 if not found
     * @throws StorageException if an error occurred during selection
     */
    int getNodeType(int number) throws StorageException;

    /**
     * Create a storage element to store the specified builder's objects.
     * @param builder the builder to create the storage element for
     * @throws StorageException if an error occurred during the creation of the storage element
     */
    void create(MMObjectBuilder builder) throws StorageException;

    /**
     * Create the basic elements for this storage
     * @throws StorageException if an error occurred during the creation of the object storage
     */
    void create() throws StorageException;

    /**
     * Changes the storage of a builder to match its new configuration.
     * @param builder the builder whose storage to change
     */
    void change(MMObjectBuilder builder) throws StorageException;

    /**
     * Drops the storage of this builder.
     * @param builder the builder whose storage to drop
     */
    void delete(MMObjectBuilder builder) throws StorageException;

    /**
     * Determine if a storage element exists for storing the given builder's objects
     * @param builder the builder to check
     * @return <code>true</code> if the storage element exists, false if it doesn't
     * @throws StorageException if an error occurred while querying the storage
     */
    boolean exists(MMObjectBuilder builder) throws StorageException;

    /**
     * Determine if the basic storage elements exist
     * Basic storage elements include the 'object' storage (where all objects and their types are registered).
     * @return <code>true</code> if basic storage elements exist
     * @throws StorageException if an error occurred while querying the storage
     */
    boolean exists() throws StorageException;

    /**
     * Return the number of objects of a builder in the storage
     * @param builder the builder whose objects to count
     * @return the number of objects the builder has
     * @throws StorageException if the storage element for the builder does not exists
     */
    int size(MMObjectBuilder builder) throws StorageException;

    /**
     * Return the total number of objects in the storage
     * @return the number of objects
     * @throws StorageException if the basic storage elements do not exist
     */
    int size() throws StorageException;

    /**
     * Creates a field and adds it to the storage of this builder.
     * @param field the CoreField of the field to add
     */
    void create(CoreField field) throws StorageException;

    /**
     * Changes a field to the storage of this builder.
     * @param field the CoreField of the field to change
     */
    void change(CoreField field) throws StorageException;

    /**
     * Deletes a field from the storage of this builder.
     * @param field the CoreField of the field to delete
     */
    void delete(CoreField field) throws StorageException;

    /**
     * Checks for null values for a field from the storage of this builder.
     * @param node the node to check the value for
     * @param field the CoreField
     * @return <code>true</code> when value is null in storage
     * @throws StorageException if an error occurred during the get
     */
    boolean isNull(MMObjectNode node, CoreField field) throws StorageException;

}
