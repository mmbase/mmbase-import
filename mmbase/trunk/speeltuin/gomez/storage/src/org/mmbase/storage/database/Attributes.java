/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.database;

/**
 * This class defines the attributes names used by the default database storage manager classes.
 * Specific storage managers may ignore or add their own attributes.
 *
 * @author Pierre van Rooden
 * @since MMBase-1.7
 * @version $Id: Attributes.java,v 1.3 2003-08-19 10:32:42 pierre Exp $
 */
public final class Attributes {

    
    /**
     * Attribute: <code>database-data-source</code>.
     * The data source object used by the storage layer.
     * This attribute is set by the storagelayer and returns a javax.sql.DataSource object.
     * You should not set or configure this attribute (but you can retrieve it).
     */
    public static final String DATA_SOURCE = "database-data-source";
    
    /**
     * Option: <code>database-supports-inheritance</code>.
     * When true, the database supports inheritance (you can extend tables). This option influences what fields
     * MMBase will add to a newly created table.
     * Note that you should specify this attribute if you have set up the schemes
     * {@link Schemes.CREATE_ROW_TYPE_SCHEME} and/or
     * {@link Schemes.CREATE_TABLE_SCHEME} to create tables that support inheritance.
     * The default is <code>false</code>
     */
    public static final String SUPPORTS_INHERITANCE = "database-supports-inheritance";

    /**
     * Option: <code>database-stores-binary-as-file</code>.
     * When true, binary data is stored on disk, rather than in the database.
     * If you set this option ou should also set the attribute {@link #BINARY_FILE_PATH}
     * The default is <code>false</code>
     */
    public static final String STORES_BINARY_AS_FILE = "database-stores-binary-as-file";

    /**
     * Attribute: <code>database-binary-file-path</code>.
     * The path to the directyory where binary files are to be stored if {@link #STORES_BINARY_AS_FILE} is true.
     * The default is <code>false</code>
     */
    public static final String BINARY_FILE_PATH = "database-binary-file-path";
    
    
    /**
     * Option: <code>database-supports-blob</code>.
     * When true, the driver/database used supports the JDBC getBlob() method.
     * The default is <code>false</code>
     */
    public static final String SUPPORTS_BLOB = "database-supports-blob";

    /**
     * Option: <code>database-supports-composite-index</code>.
     * When true, the database uses composite indices for 'key' fields.
     * When false, it uses single indices (a separate index for each field)
     * The default is <code>false</code>
     */
    public static final String SUPPORTS_COMPOSITE_INDEX = "database-supports-composite-index";

    /**
     * Attribute: <code>database-transaction-isolation-level</code>.
     * The transaction isolation level used for connections to the database.
     * This determines the level of transaction support.
     * The default is determined from the database metadata.
     */
    public static final String TRANSACTION_ISOLATION_LEVEL = "database.transaction-isolation-level";
    
    /**
     * Option: <code>database-supports-data-manipulation-transactions-only</code>.
     * If true, only data manipulation (INSERT, UPDATE) can be done within a transaction.
     * This option should be used to properly close connections before a data definiton query is run.  
     * The default is determined from the database metadata.
     * @todo: not used at the moment
     */
    public static final String SUPPORTS_DATA_MANIPULATION_TRANSACTIONS_ONLY = "database-supports-data-manipulation-transactions-only";
    
    /**
     * Option: <code>database-supports-alter-table-with-add-column</code>.
     * If true, you can use a ALTER TABLE statement to add fields.
     * This option should be used to determine wehter the create() method for fields can be run using this sql statement.  
     * The default is determined from the database metadata.
     * @todo: not used at the moment
     */
    public static final String SUPPORTS_ALTER_TABLE_WITH_ADD_COLUMN = "database-supports-alter-table-with-add-column";
    
    /**
     * Option: <code>database-supports-alter-table-with-drop-column</code>.
     * If true, you can use a ALTER TABLE statement to delete fields.
     * This option should be used to determine wehter the delete() method for fields can be run using this sql statement.  
     * The default is determined from the database metadata.
     * @todo: not used at the moment
     */
    public static final String SUPPORTS_ALTER_TABLE_WITH_DROP_COLUMN = "database-supports-alter-table-with-drop-column";

}
