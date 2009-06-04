/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.implementation.database;

import org.mmbase.module.core.*;
import org.mmbase.storage.StorageException;
import org.mmbase.core.CoreField;
import org.mmbase.bridge.Field;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * A JDBC implementation of a storage manager for relational databases.
 * @javadoc
 *
 * @author Pierre van Rooden
 * @since MMBase-1.7
 * @version $Id$
 */
public class RelationalDatabaseStorageManager extends DatabaseStorageManager {

    private static final Logger log = Logging.getLoggerInstance(RelationalDatabaseStorageManager.class);

    public RelationalDatabaseStorageManager() {
    }

    // javadoc is inherited
    @Override public double getVersion() {
        return 1.0;
    }

    @Override protected boolean tablesInheritFields() {
        return false;
    }


    /**
     * Actual implementation of {@link #create(MMObjectNode, MMObjectBuilder)} which wraps this in a
     * transction only, now.
     * @since MMBase-1.9.1
     */
    protected void simpleCreate(final MMObjectNode node, final MMObjectBuilder builder) throws StorageException {
       // insert in parent tables (from parents to childs) (especially because foreign keys on object's number may exist)
        for (MMObjectBuilder b : builder.getAncestors()) {
            for (CoreField f : b.getFields()) {
                if (f.getType() == Field.TYPE_BINARY) {
                    // if the value is an inputstream at the moment, convert it to a byte-array, because it must be stored again..
                    node.storeValue(f.getName(), org.mmbase.util.Casting.toByte(node.retrieveValue(f.getName())));
                }
            }
            super.create(node, b);
        }
        super.create(node, builder);
    }

    /**
     * Adds a node to the passed builder and all its parent builders.
     * @param node The node to insert. The node already needs to have a (new) number assigned
     * @param builder the builder to store the node
     * @throws StorageException if an error occurred during creation
     */
    @Override public void create(final MMObjectNode node, final MMObjectBuilder builder) throws StorageException {
        boolean localTransaction = !inTransaction;
        if (localTransaction) {
            beginTransaction();
        }
        try {
            simpleCreate(node, builder);
            if (localTransaction) commit();
        } catch (StorageException se) {
            if (localTransaction && inTransaction) rollback();
            throw se;
        }
    }

    /**
     * Changes a node in the passed builder and all its parent builders
     * @param node The node to change
     * @param builder the builder to change the node in
     * @throws StorageException if an error occurred during change
     */
    @Override public void change(MMObjectNode node, MMObjectBuilder builder) throws StorageException {
        boolean localTransaction = !inTransaction;
        if (localTransaction) {
            beginTransaction();
        }
        try {
            do {
                super.change(node, builder);
                builder = builder.getParentBuilder();
            } while (builder != null);
            if (localTransaction) commit();
        } catch (StorageException se) {
            if (localTransaction && inTransaction) rollback();
            throw se;
        }
    }

    /**
     * Deletes a node in the passed builder and all its parent builders.
     * @param node The node to delete
     * @param builder the builder to delete the node in
     * @throws StorageException if an error occurred during delete
     */
    @Override public void delete(MMObjectNode node, MMObjectBuilder builder) throws StorageException {
        boolean localTransaction = !inTransaction;
        if (localTransaction) {
            beginTransaction();
        }

        try {
            do {
                super.delete(node, builder);
                builder = builder.getParentBuilder();
            } while (builder!=null);
            if (localTransaction) commit();
        } catch (StorageException se) {
            if (localTransaction && inTransaction) rollback();
            throw se;
        }
    }

}

