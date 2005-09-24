/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.implementation.database;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

public class ViewDatabaseStorageManager extends DatabaseStorageManager {

    private static final Logger log = Logging.getLoggerInstance(ViewDatabaseStorageManager.class);
    
    /**
     * Determine if the basic storage elements exist
     * Basic storage elements include the 'object' storage (where all objects and their types are registered).
     * @return <code>true</code> if basic storage elements exist
     * @throws StorageException if an error occurred while querying the storage
     */
    public boolean exists() throws StorageException {
        return viewExists(factory.getMMBase().getRootBuilder());
    }
    
    /**
     * Determine if a storage element exists for storing the given builder's objects
     * @param builder the builder to check
     * @return <code>true</code> if the storage element exists, false if it doesn't
     * @throws StorageException if an error occurred while querying the storage
     */
    public boolean exists(MMObjectBuilder builder) throws StorageException {
        return viewExists(builder);
    }

    /**
     * Create the basic elements for this storage
     * @return <code>true</code> if the storage was succesfully created
     * @throws StorageException if an error occurred during the creation of the object storage
     */
    public void create() throws StorageException {
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
    public void create(MMObjectBuilder builder) throws StorageException {
        if(!viewExists(builder)){
             viewCreate(builder);
        }
        
    }
  
    public void create(final MMObjectNode node, final MMObjectBuilder builder) throws StorageException {
        boolean localTransaction = !inTransaction;
        if (localTransaction) {
            beginTransaction();
        }
        try {
            // insert in parent tables (from parents to childs) (especially because foreign keys on object's number may exist)
            java.util.Iterator i = builder.getAncestors().iterator();
            while(i.hasNext()) {
                MMObjectBuilder b = (MMObjectBuilder) i.next();
                createObject(node, b);
            }
            createObject(node, builder);
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
        List createFields = new ArrayList();
        List builderFields = builder.getFields(NodeManager.ORDER_CREATE);
        for (Iterator f = builderFields.iterator(); f.hasNext();) {
            CoreField field = (CoreField)f.next();
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
    public void change(MMObjectNode node, MMObjectBuilder builder) throws StorageException {
        boolean localTransaction = !inTransaction;
        if (localTransaction) {       
            beginTransaction();
        }
        try {
            do {
                changeObject(node,builder);
                builder = builder.getParentBuilder();
            } while (builder!=null);
            if (localTransaction) {
                commit();
            }
        } catch (StorageException se) {
            if (localTransaction && inTransaction) rollback();
            throw se;
        }
    }

    private void changeObject(MMObjectNode node, MMObjectBuilder builder) {
        List changeFields = new ArrayList();
        // obtain the node's changed fields
        List fieldNames = node.getChanged();
        for (Iterator f = fieldNames.iterator(); f.hasNext();) {
            String key = (String)f.next();
            CoreField field = builder.getField(key);
            if ((field != null) && field.inStorage() && !isInheritedField(field)) {
                changeFields.add(field);
            }
        }
        change(node, builder, getTableName(builder), changeFields, fieldNames);
    }

    /**
     * Deletes a node in the passed builder and all its parent builders.
     * @param node The node to delete
     * @param builder the builder to delete the node in
     * @throws StorageException if an error occurred during delete
     */
    public void delete(MMObjectNode node, MMObjectBuilder builder) throws StorageException {
        boolean localTransaction = !inTransaction;
        if (localTransaction) {
            beginTransaction();
        }
        
        try {
            do {
                deleteObject(node, builder);
                builder = builder.getParentBuilder();
            } while (builder!=null);
            if (localTransaction) {
                commit();
            }
        } catch (StorageException se) {
            if (localTransaction && inTransaction) rollback();
            throw se;
        }
    }
    
    private void deleteObject(MMObjectNode node, MMObjectBuilder builder) {
        List blobFileField = new ArrayList();
        List builderFields = builder.getFields(NodeManager.ORDER_CREATE);
        for (Iterator f = builderFields.iterator(); f.hasNext();) {
            CoreField field = (CoreField)f.next();
            if (field.inStorage() && !isInheritedField(field)) {
                if (factory.hasOption(Attributes.STORES_BINARY_AS_FILE) && (field.getType() == Field.TYPE_BINARY)) {
                    blobFileField.add(field);
                }
            }
        }
        String tablename = getTableName(builder);
        super.delete(node, builder, blobFileField, tablename);
    }

    public String getFieldName(CoreField field) {
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
        }
        else {
            return getTableName((String) factory.getStorageIdentifier(builder));
        }
    }
    public String  getTableName(String viewname) {
        String id = viewname + "_table";
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

    protected void create(Index index) throws StorageException {
        super.createIndex(index, getTableName(index.getParent()));
    }
    
    public boolean viewExists(MMObjectBuilder builder) {     
        return exists(getViewName(builder));
    }
    protected String getNewConstrainName(CoreField field, String type) {     
        return field.getParent().getTableName() + "_" + field.getName() + "_" + type;
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
        List fields = builder.getFields(NodeManager.ORDER_CREATE);

        if (!super.exists(getTableName(builder))) {
            List tableFields = new ArrayList();
            for (Iterator f = fields.iterator(); f.hasNext();) {
                CoreField field = (CoreField)f.next();
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

    private void createView(MMObjectBuilder builder, MMObjectBuilder inheritedBuilder, List fields, String tablename) throws StorageError {
        log.debug("Creating a view for " + builder);
        Scheme viewScheme = factory.getScheme(Schemes.CREATE_VIEW, Schemes.CREATE_VIEW_DEFAULT);
        String viewname = getViewName(builder);
        
        StringBuffer createViewFields = new StringBuffer();
        for (Iterator f = fields.iterator(); f.hasNext();) {
            CoreField field = (CoreField)f.next();
            if (field.inStorage() && (field.getType() != Field.TYPE_BINARY || !factory.hasOption(Attributes.STORES_BINARY_AS_FILE))) {
                if (createViewFields.length() > 0) {
                    createViewFields.append(", ");
                }
                createViewFields.append(getFieldName(field));
            }
        }
   
        StringBuffer createTableFields = new StringBuffer();
        for (Iterator f = fields.iterator(); f.hasNext();) {
            CoreField field = (CoreField)f.next();
            if (field.inStorage() && (field.getType() != Field.TYPE_BINARY || !factory.hasOption(Attributes.STORES_BINARY_AS_FILE))) {
   
                if (createTableFields.length() > 0) {
                    createTableFields.append(", ");
                }
                if(isInheritedField(field)) {
                    if(inheritedBuilder == null) 
                        throw new StorageError("Cannot have a inherited field while we dont extend inherit from a builder!");
                    createTableFields.append(getViewName(inheritedBuilder) + "." + getFieldName(field));
                }
                else {
                    createTableFields.append(tablename + "." + getFieldName(field));
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
   
            Statement s = activeConnection.createStatement();
            logQuery(query);
            s.executeUpdate(query);
            s.close();
   
            tableNameCache.add(viewname.toUpperCase());
        } catch (SQLException se) {
            throw new StorageException(se.getMessage() + " in query:" + query, se);
        } finally {
            releaseActiveConnection();
        }
    }

}
