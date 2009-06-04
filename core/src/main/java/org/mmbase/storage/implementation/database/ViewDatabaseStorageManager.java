/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.implementation.database;

import java.sql.*;
import java.util.*;

import org.mmbase.bridge.Field;
import org.mmbase.bridge.NodeManager;
import org.mmbase.core.CoreField;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.storage.StorageError;
import org.mmbase.storage.StorageException;
import org.mmbase.storage.util.Index;
import org.mmbase.storage.util.Scheme;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @javadoc
 *
 * @version $Id$
 * @since MMBase-1.8
 */
public class ViewDatabaseStorageManager extends DatabaseStorageManager {

    private static final Logger log = Logging.getLoggerInstance(ViewDatabaseStorageManager.class);

    /**
     * Determine if the basic storage elements exist
     * Basic storage elements include the 'object' storage (where all objects and their types are registered).
     * @return <code>true</code> if basic storage elements exist
     * @throws StorageException if an error occurred while querying the storage
     */
    @Override public boolean exists() throws StorageException {
        return viewExists(factory.getMMBase().getRootBuilder());
    }

    /**
     * Determine if a storage element exists for storing the given builder's objects
     * @param builder the builder to check
     * @return <code>true</code> if the storage element exists, false if it doesn't
     * @throws StorageException if an error occurred while querying the storage
     */
    @Override public boolean exists(MMObjectBuilder builder) throws StorageException {
        return viewExists(builder);
    }

    /**
     * Create the basic elements for this storage
     * @throws StorageException if an error occurred during the creation of the object storage
     */
    @Override public void create() throws StorageException {
        if(!viewExists(factory.getMMBase().getRootBuilder())) {
            viewCreate(factory.getMMBase().getRootBuilder());
            createSequence();
        }
    }

    /**
     * Create a storage element to store the specified builder's objects.
     * @param builder the builder to create the storage element for
     * @throws StorageException if an error occurred during the creation of the storage element
     */
    @Override public void create(MMObjectBuilder builder) throws StorageException {
        if(!viewExists(builder)){
             viewCreate(builder);
        }
    }

    @Override public void create(final MMObjectNode node, final MMObjectBuilder builder) throws StorageException {
        boolean localTransaction = !inTransaction;
        if (localTransaction) {
            beginTransaction();
        }
        try {
            if (factory.hasOption("database-supports-insert-triggers")) {
                // no need for any fancy looping over parents; we just insert everything in this view
                super.create(node, builder);
            } else {
                // insert in parent tables (from parents to childs) (especially because foreign keys on object's number may exist)
                Iterator<MMObjectBuilder> i = builder.getAncestors().iterator();
                while(i.hasNext()) {
                    MMObjectBuilder b = i.next();
                    createObject(node, b);
                }
                createObject(node, builder);
            }
            if (localTransaction) {
                commit();
            }
        } catch (StorageException se) {
            if (localTransaction && inTransaction) rollback();
            throw se;
        }
    }

    /**
     * This method inserts a new object in a specific builder, and registers the change.
     * This method makes it easier to implement relational databases, where you may need to update the node
     * in more than one builder.
     * Call this method for all involved builders if you use a relational database.
     * @param node The node to insert. The node already needs to have a (new) number assigned
     * @param builder the builder to store the node
     * @throws StorageException if an error occurred during creation
     */
    protected void createObject(MMObjectNode node, MMObjectBuilder builder) throws StorageException {
        List<CoreField> createFields = new ArrayList<CoreField>();
        List<CoreField> builderFields = builder.getFields(NodeManager.ORDER_CREATE);
        for (CoreField field : builderFields) {
            if (field.inStorage() && (!this.isInheritedField(field) || field.getName().equals(this.getNumberField().getName()))) {
                createFields.add(field);
            }
        }
        String tablename = getTableName(builder);
        create(node, createFields, tablename);
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
            if (factory.hasOption("database-supports-update-triggers")) {
                super.change(node, builder);
            } else {
                do {
                    changeObject(node,builder);
                    builder = builder.getParentBuilder();
                } while (builder!=null);
            }
            if (localTransaction) {
                commit();
            }
        } catch (StorageException se) {
            if (localTransaction && inTransaction) rollback();
            throw se;
        }
    }

    private void changeObject(MMObjectNode node, MMObjectBuilder builder) {
        List<CoreField> changeFields = new ArrayList<CoreField>();
        // obtain the node's changed fields
        Collection<String> fieldNames = node.getChanged();
        for (String key : fieldNames) {
            CoreField field = builder.getField(key);
            if ((field != null) && field.inStorage() && !isInheritedField(field)) {
                changeFields.add(field);
            }
        }
        change(node, builder, getTableName(builder), changeFields);
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
            if (factory.hasOption("database-supports-delete-triggers")) {
                super.delete(node, builder);
            } else {
                do {
                    deleteObject(node, builder);
                    builder = builder.getParentBuilder();
                } while (builder!=null);
            }
            if (localTransaction) {
                commit();
            }
        } catch (StorageException se) {
            if (localTransaction && inTransaction) rollback();
            throw se;
        }
    }

    private void deleteObject(MMObjectNode node, MMObjectBuilder builder) {
        List<CoreField> blobFileField = new ArrayList<CoreField>();
        List<CoreField> builderFields = builder.getFields(NodeManager.ORDER_CREATE);
        for (CoreField field : builderFields) {
            if (field.inStorage() && !isInheritedField(field)) {
                if (factory.hasOption(Attributes.STORES_BINARY_AS_FILE) && (field.getType() == Field.TYPE_BINARY)) {
                    blobFileField.add(field);
                }
            }
        }
        String tablename = getTableName(builder);
        super.delete(node, builder, blobFileField, tablename);
    }

    protected String getFieldName(CoreField field) {
        return (String)factory.getStorageIdentifier(field);
    }

    public boolean isInheritedField(CoreField field) {
        MMObjectBuilder inheritedBuilder = field.getParent().getParentBuilder();
        if(inheritedBuilder == null) {
            // no parent, thus cannot inherit anything
            return false;
        }
        return (inheritedBuilder.getField(field.getName()) != null);
    }

    public CoreField getNumberField() {
        return factory.getMMBase().getRootBuilder().getField("number");
    }

    public String getTableName(MMObjectBuilder builder) {
        if (builder.getParentBuilder() == null) {
            return (String) factory.getStorageIdentifier(builder);
        } else {
            return getTableName((String) factory.getStorageIdentifier(builder));
        }
    }

    public String getTableName(String viewname) {
        String id = (String)factory.getStorageIdentifier(viewname + "_table");
        String toCase = (String)factory.getAttribute(org.mmbase.storage.Attributes.STORAGE_IDENTIFIER_CASE);
        if ("lower".equals(toCase)) {
            return id.toLowerCase();
        } else if ("upper".equals(toCase)) {
            return id.toUpperCase();
        } else {
            return id;
        }
    }

    public String getViewName(MMObjectBuilder builder) {
        return getViewName((String)factory.getStorageIdentifier(builder));
    }
    public String getViewName(String viewname) {
        return viewname;
    }

    /**
     * Override the default version. An index should only be created if
     * all the fields are in this builder. Otherwise this will fail horrably.
     */
    @Override protected void create(Index index) throws StorageException {
        for (int i=0; i<index.size(); i++) {
            CoreField f = (CoreField)index.get(i);
            if (!isPartOfBuilderDefinition(f)) {
                return;
            }
        }

        super.createIndex(index, getTableName(index.getParent()));
    }

    @Override protected boolean exists(Index index) throws StorageException {
        return super.exists(index, getTableName(index.getParent()));
    }

    public boolean viewExists(MMObjectBuilder builder) {
        return exists(getViewName(builder));
    }

    public boolean viewCreate(MMObjectBuilder builder) {
        MMObjectBuilder inheritedBuilder = builder.getParentBuilder();
        // create the inherited builder first
        if(inheritedBuilder != null) {
            if(!viewExists(inheritedBuilder)) {
                // create the builder we inherit from
                if(!viewCreate(inheritedBuilder)) {
                    // we could not start create everyting!
                    return false;
                }
            }
        }
        String tablename = getTableName(builder);
        List<CoreField> fields = builder.getFields(NodeManager.ORDER_CREATE);

        if (!super.exists(getTableName(builder))) {
            List<CoreField> tableFields = new ArrayList<CoreField>();
            for (CoreField field : fields) {
                // is it a database field, and not of the parent(except the number field)?
                if (isPartOfBuilderDefinition(field) || field.getName().equals(getNumberField().getName())) {
                    tableFields.add(field);
                }
            }
            // Create the table
            createTable(builder, tableFields, tablename);

            //TODO rewrite verify check with views
            //verify(builder);
        }

        if (builder.getParentBuilder() != null) {
            createView(builder, inheritedBuilder, fields, tablename);
        }
        return true;
    }

    private void createView(MMObjectBuilder builder, MMObjectBuilder inheritedBuilder, List<CoreField> fields, String tablename) throws StorageError {
        log.debug("Creating a view for " + builder);
        Scheme viewScheme = factory.getScheme(Schemes.CREATE_VIEW, Schemes.CREATE_VIEW_DEFAULT);
        Scheme createInsertTriggerScheme = null;
        Scheme createDeleteTriggerScheme = null;
        Scheme createUpdateTriggerScheme = null;
        if (factory.hasOption("database-supports-insert-triggers")) {
            createInsertTriggerScheme = factory.getScheme(Schemes.CREATE_INSERT_TRIGGER, Schemes.CREATE_INSERT_TRIGGER_DEFAULT);
            if (createInsertTriggerScheme == null) {
                log.warn("Database supports insert-triggers, but no trigger scheme defined! Ignoring insert-trigger!!");
            }
        }
        if (factory.hasOption("database-supports-delete-triggers")) {
            createDeleteTriggerScheme = factory.getScheme(Schemes.CREATE_DELETE_TRIGGER, Schemes.CREATE_DELETE_TRIGGER_DEFAULT);
            if (createDeleteTriggerScheme == null) {
                log.warn("Database supports delete-triggers, but no trigger scheme defined! Ignoring delete-trigger!!");
            }
        }
        if (factory.hasOption("database-supports-update-triggers")) {
            createUpdateTriggerScheme = factory.getScheme(Schemes.CREATE_UPDATE_TRIGGER, Schemes.CREATE_UPDATE_TRIGGER_DEFAULT);
            if (createUpdateTriggerScheme == null) {
                log.warn("Database supports update-triggers, but no trigger scheme defined! Ignoring update-trigger!!");
            }
        }

        String viewname = getViewName(builder);

        StringBuilder createViewFields = new StringBuilder();
        for (CoreField field : fields) {
            if (field.inStorage() && (field.getType() != Field.TYPE_BINARY || !factory.hasOption(Attributes.STORES_BINARY_AS_FILE))) {
                if (createViewFields.length() > 0) {
                    createViewFields.append(", ");
                }
                createViewFields.append(getFieldName(field));
            }
        }

        StringBuilder createTableFields = new StringBuilder();
        List<String> myFieldNames     = new ArrayList<String>();
        List<String> parentFieldNames = new ArrayList<String>();

        for (CoreField field : fields) {
            if (field.inStorage() && (field.getType() != Field.TYPE_BINARY || !factory.hasOption(Attributes.STORES_BINARY_AS_FILE))) {

                if (createTableFields.length() > 0) {
                    createTableFields.append(", ");
                }
                if(isInheritedField(field)) {
                    if(inheritedBuilder == null)
                        throw new StorageError("Cannot have a inherited field while we dont extend inherit from a builder!");
                    createTableFields.append(getViewName(inheritedBuilder) + "." + getFieldName(field));
                    parentFieldNames.add(getFieldName(field));
                } else {
                    createTableFields.append(tablename + "." + getFieldName(field));
                    myFieldNames.add(getFieldName(field));
                }

                if (isInheritedField(field) && field.getName().equals(getNumberField().getName())) {
                    myFieldNames.add(getFieldName(field));
                }
            }
        }

        String query = "";
        try {
            getActiveConnection();
            // create the view
            query = viewScheme.format(new Object[] { this, viewname, tablename, createViewFields.toString(), createTableFields.toString(), getFieldName(getNumberField()), inheritedBuilder, factory.getDatabaseName() });
            // remove parenthesis with empty field definitions -
            // unfortunately Schemes don't take this into account
            if (factory.hasOption(Attributes.REMOVE_EMPTY_DEFINITIONS)) {
                query = query.replaceAll("\\(\\s*\\)", "");
            }

            long startTime = getLogStartTime();
            PreparedStatement s = null;
            try {
                s = activeConnection.prepareStatement(query);
                s.executeUpdate();
            }
            finally {
                if (s != null) {
                    s.close();
                }
            }
            logQuery(query, startTime);

            if (createInsertTriggerScheme != null) {
                //insert into mm_typedef_t (m_number, otype, owner) values (:NEW.m_number, :NEW.otype, :NEW.owner);
                //insert into mm_object (m_number, name, description) values (:NEW.m_number, :NEW.name, :NEW.description);
                StringBuilder myFields = new StringBuilder();
                StringBuilder myValues = new StringBuilder();
                StringBuilder parentFields = new StringBuilder();
                StringBuilder parentValues = new StringBuilder();
                for (int i=0; i<myFieldNames.size(); i++) {
                    if (i > 0) {
                        myFields.append(", ");
                        myValues.append(", ");
                    }
                    myFields.append(myFieldNames.get(i));
                    myValues.append(":NEW." + myFieldNames.get(i));
                }
                for (int i=0; i<parentFieldNames.size(); i++) {
                    if (i > 0) {
                        parentFields.append(", ");
                        parentValues.append(", ");
                    }
                    parentFields.append(parentFieldNames.get(i));
                    parentValues.append(":NEW." + parentFieldNames.get(i));
                }
                Object triggerName = factory.getStorageIdentifier(viewname + "_INSERT");
                query = createInsertTriggerScheme.format(new Object[]{this, viewname, tablename, inheritedBuilder, myFields.toString(), myValues.toString(), parentFields.toString(), parentValues.toString(), triggerName});

                if (factory.hasOption(Attributes.REMOVE_EMPTY_DEFINITIONS)) {
                    query = query.replaceAll("\\(\\s*\\)", "");
                }

                long startTime2 = getLogStartTime();
                PreparedStatement s2 = null;
                try {
                    s2 = activeConnection.prepareStatement(query);
                    s2.executeUpdate();
                }
                finally {
                    if (s2 != null) {
                        s2.close();
                    }
                }

                logQuery(query, startTime2);
            }

            if (createDeleteTriggerScheme != null) {
                Object triggerName = factory.getStorageIdentifier(viewname + "_DELETE");
                query = createDeleteTriggerScheme.format(new Object[]{this, viewname, tablename, inheritedBuilder, getFieldName(getNumberField()), triggerName});
                if (factory.hasOption(Attributes.REMOVE_EMPTY_DEFINITIONS)) {
                    query = query.replaceAll("\\(\\s*\\)", "");
                }

                long startTime2 = getLogStartTime();
                PreparedStatement s3 = null;
                try {
                    s3 = activeConnection.prepareStatement(query);
                    s3.executeUpdate();
                }
                finally {
                    if (s3 != null) {
                        s3.close();
                    }
                }
                logQuery(query, startTime2);
            }

            if (createUpdateTriggerScheme != null) {
                StringBuilder myAssignments = new StringBuilder();
                StringBuilder parentAssignments = new StringBuilder();
                for (int i=0; i<myFieldNames.size(); i++) {
                    if (i > 0) {
                        myAssignments.append(", ");
                    }
                    myAssignments.append(myFieldNames.get(i));
                    myAssignments.append(" = :NEW.");
                    myAssignments.append(myFieldNames.get(i));
                }
                for (int i=0; i<parentFieldNames.size(); i++) {
                    if (i > 0) {
                        parentAssignments.append(", ");
                    }
                    parentAssignments.append(parentFieldNames.get(i));
                    parentAssignments.append(" = :NEW.");
                    parentAssignments.append(parentFieldNames.get(i));
                }
                Object triggerName = factory.getStorageIdentifier(viewname + "_UPDATE");
                query = createUpdateTriggerScheme.format(new Object[]{this, viewname, tablename, inheritedBuilder, myAssignments.toString(), parentAssignments.toString(), getFieldName(getNumberField()), triggerName});

                if (factory.hasOption(Attributes.REMOVE_EMPTY_DEFINITIONS)) {
                    query = query.replaceAll("\\(\\s*\\)", "");
                }

                long startTime2 = getLogStartTime();
                PreparedStatement s4 = null;
                try {
                    s4 = activeConnection.prepareStatement(query);
                    s4.executeUpdate();
                }
                finally {
                    if (s4 != null) {
                        s4.close();
                    }
                }
                logQuery(query, startTime2);
            }

            addToTableNameCache(viewname);
        } catch (SQLException se) {
            throw new StorageException(se.getMessage() + " in query:" + query, se);
        } finally {
            releaseActiveConnection();
        }
    }

}
