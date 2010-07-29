/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.implementation.database;

/**
 * This class defines the attributes names used by the default database storage manager classes.
 * Specific storage managers may ignore or add their own attributes.
 *
 * @author Pierre van Rooden
 * @since MMBase-1.7
 * @version $Id$
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
     * Option: <code>database-supports-transactions</code>.
     * When true, the database supports transactions.
     * The default is determined form the database, but you can override it.
     */
    public static final String SUPPORTS_TRANSACTIONS = "database-supports-transactions";

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
     * The default is the WEB-INF/data directory of the mmbase web application.
     * Note that if you specify a relative url, it is taken from the web application's webroot.
     */
    public static final String BINARY_FILE_PATH = "database-binary-file-path";

    /**
     * Option: <code>database-force-encode-text</code>.
     * If true, the database layer will explicitly decode/encode strings using the MMBase encoding when
     * storing and retrieving text from the database.
     * The default is <code>false</code>
     */
    public static final String FORCE_ENCODE_TEXT = "database-force-encode-text";

    /**
     * If the database is ISO-8859-1, then you can switch this option to true, to store CP1252 in it.
     */
    public static final String LIE_CP1252        = "database-lie-cp1252";


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
     * The default is <code>true</code>
     */
    public static final String SUPPORTS_COMPOSITE_INDEX = "database-supports-composite-index";

    /**
     * Attribute: <code>database-transaction-isolation-level</code>.
     * The transaction isolation level used for connections to the database.
     * This determines the level of transaction support.
     * The default is determined from the database metadata.
     */
    public static final String TRANSACTION_ISOLATION_LEVEL = "database-transaction-isolation-level";

    /**
     * Option: <code>database-supports-data-definition</code>.
     * If true, the data definiton (table structure) can be changed using ALTER TABLE statements.
     * Some databses (such as Informix) may have trouble with ALTER TABLE statements on OO-tables.
     * Turn this option false for tehse databses.
     * The default is <code>true</code>
     */
    public static final String SUPPORTS_DATA_DEFINITION = "database-supports-data-definition";

    /**
     * Option: <code>database-remove-empty-definitions</code>.
     * If this option is true, empty parenthesis in a table definition are removed.
     * When you create a new table that extends form another table, but which doesn't add fields,
     * you may get a statement that looks like: <br />
     * <code>CREATE TABLE table1 () UNDER table2</code><br />
     * This statement will fail udner a database such as Informix, which does not accept empty
     * parenthesis, but instead expects: <br />
     * <code>CREATE TABLE table1 UNDER table2</code><br />
     * On the other hand, a database such as Postgresql DOES expect the parenthesis (and fails if
     * they are ommitted.)
     * The default is <code>false</code>
     */
    public static final String REMOVE_EMPTY_DEFINITIONS = "database-remove-empty-definitions";

    /**
     * Option: <code>sequence-buffer-size</code>.
     * The sequence buffer size is the number of keys that MMBase caches in the storage layer.
     * You can use this to minimize the nr of times MMBase accesses the database to generate a new
     * object number.
     * When not set, the value is assumed to be 1.
     */
    public static final String SEQUENCE_BUFFER_SIZE = "sequence-buffer-size";

    /**
     * Option: <code>trim-strings</code>.
     * Some text fields for some databases need to be trimmed.
     * The default is <code>false</code>
     */
    public static final String TRIM_STRINGS = "trim-strings";

}
