/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.database;

/**
 * This class defines the scheme names and defaults used by the default database storage manager classes.
 * Specific storage managers may add their own schemes, or not use schemes at all.
 *
 * @author Pierre van Rooden
 * @since MMBase-1.7
 * @version $Id: Schemes.java,v 1.4 2003-08-01 08:08:40 pierre Exp $
 */
public final class Schemes {

    /**
     *  Name of the scheme for creating a row type (i.e.. for an OO-database such as Informix)
     *  The parameters accepted are:
     *  <lu>
     *    <li>{0} the storage manager (StorageManager), or the basename for tables (String)</li>
     *    <li>{1} the builder to create the row type for</li>
     *    <li>{2} the field definitions (excluding index definitions)</li>
     *    <li>{3} the builder that this rowtype extends from</li>
     *  </ul>
     *
     * This attribute is optional, and there is no default for this scheme.
     * An example (for Informix):
     * <p>
     *  <code> CREATE ROW TYPE {1}_t ({2}) EXTENDS {3}_t </code>
     * </p>
     */
    public static final String CREATE_ROW_TYPE = "create.rowtype.scheme";

    /**
     *  Name of the scheme for creating a table
     *  The parameters accepted are:
     *  <lu>
     *    <li>{0} the storage manager (StorageManager), or the basename for tables (String)</li>
     *    <li>{1} the builder to create the table for</li>
     *    <li>{2} the field definitions (excluding simple index definitions)</li>
     *    <li>{3} the simple index definitions</li>
     *    <li>{4} the field definitions, including simple index definitions</li>
     *    <li>{5} the composite index definitions</li>
     *    <li>{6} the builder that this table extends from</li>
     *  </ul>
     *
     * You can set up your scheme to create extended tables (i.e. in Postgresql).
     * You also can define indexes or fields seperate (i.e. in HSQL or in a create table after a create row type in Informix)
     * or in one go (as you might do with MySQL).
     */
    public static final String CREATE_TABLE = "create.table.scheme";

    /**
     *  The default scheme for creating a table.
     */
    public static final String CREATE_TABLE_DEFAULT = "CREATE TABLE {1} {4} {5}";

    /**
     *  Name of the scheme for creating a row type (i.e.. for an OO-database such as Informix)
     *  The parameters accepted are:
     *  <lu>
     *    <li>{0} the storage manager (StorageManager), or the basename for tables (String)</li>
     *    <li>{1} the builder to create the row type for</li>
     *    <li>{2} the field definitions (excluding index definitions)</li>
     *  </ul>
     *
     * This attribute is optional, and there is no default for this scheme.
     * An example (for Informix):
     * <p>
     *  <code> CREATE ROW TYPE {1}_t ({2}) EXTENDS {3}_t </code>
     * </p>
     */
    public static final String CREATE_OBJECT_ROW_TYPE = "create.object.rowtype.scheme";

    /**
     *  Name of the scheme for creating a table
     *  The parameters accepted are:
     *  <lu>
     *    <li>{0} the storage manager (StorageManager), or the basename for tables (String)</li>
     *    <li>{1} the builder to create the table for</li>
     *    <li>{2} the field definitions (excluding simple index definitions)</li>
     *    <li>{3} the simple index definitions</li>
     *    <li>{4} the field definitions, including simple index definitions</li>
     *    <li>{5} the composite index definitions</li>
     *  </ul>
     *
     * You can set up your scheme to create extended tables (i.e. in Postgresql).
     * You also can define indexes or fields seperate (i.e. in HSQL or in a create table after a create row type in Informix)
     * or in one go (as you might do with MySQL).
     */
    public static final String CREATE_OBJECT_TABLE = "create.object.table.scheme";

    /**
     *  The default scheme for creating a table.
     */
    public static final String CREATE_OBJECT_TABLE_DEFAULT = "CREATE TABLE {1} {4} {5}";

    /**
     *  Name of the scheme for creating a primary key
     *  The parameters accepted are:
     *  <lu>
     *    <li>{0} the storage manager (StorageManager), or the basename for tables (String)</li>
     *    <li>{1} the builder to create the key for</li>
     *    <li>{2} a symbolic name for the key</li>
     *    <li>{3} the field to create the key for</li>
     *    <li>{4} the basic storage element (name of the object table)</li>
     *  </ul>
     */
    public static final String CREATE_PRIMARY_KEY = "create.primary.key";

    /**
     *  The default scheme for creating a prinary key
     */
    public static final String CREATE_PRIMARY_KEY_DEFAULT = "PRIMARY KEY ({3})";

    /**
     *  Name of the scheme for creating a secondary (unique) key
     *  The parameters accepted are:
     *  <lu>
     *    <li>{0} the storage manager (StorageManager), or the basename for tables (String)</li>
     *    <li>{1} the builder to create the key for</li>
     *    <li>{2} a symbolic name for the key</li>
     *    <li>{3} the field (or fieldlist) to create the key for</li>
     *    <li>{4} the basic storage element (name of the object table)</li>
     *  </ul>
     */
    public static final String CREATE_SECONDARY_KEY = "create.secondary.key";

    /**
     *  Name of the scheme for creating a foreign (referential) key
     *  The parameters accepted are:
     *  <lu>
     *    <li>{0} the storage manager (StorageManager), or the basename for tables (String)</li>
     *    <li>{1} the builder to create the key for</li>
     *    <li>{2} a symbolic name for the key</li>
     *    <li>{3} the field to create the key for</li>
     *    <li>{4} the basic storage element referenced (name of the object table)</li>
     *  </ul>
     */
    public static final String CREATE_FOREIGN_KEY = "create.foreign.key";

    /**
     *  Name of the scheme for selecting a node type.
     *  The parameters accepted are:
     *  <lu>
     *    <li>{0} the storage manager (StorageManager), or the basename for tables (String)</li>
     *    <li>{1} the builder to delete the node from (MMObjectBuilder), or the builder table name (String)</li>
     *    <li>{2} the 'number' field (FieldDefs), or the database field name (String)</li>
     *    <li>{3} the number of the object to update (Integer)</li>
     *  </ul>
     */
    public static final String DELETE_NODE = "delete.node.scheme";

    /**
     *  The default scheme for selecting a node type.
     */
    public static final String DELETE_NODE_DEFAULT = "DELETE FROM {1} WHERE {2} = {3,number}";

    /**
     *  Name of the scheme for reading a binary field from a node
     *  The parameters accepted are:
     *  <lu>
     *    <li>{0} the storage manager (StorageManager), or the basename for tables (String)</li>
     *    <li>{1} the builder to delete the node from (MMObjectBuilder), or the builder table name (String)</li>
     *    <li>{2} the binary field (FieldDefs), or the binary field name (String)</li>
     *    <li>{3} the 'number' field (FieldDefs), or the database field name (String)</li>
     *    <li>{4} the number of the object to update (Integer)</li>
     *  </ul>
     */
    public static final String GET_BINARY_DATA = "get.binary.data.scheme";

    /**
     *  The default scheme for reading a binary field
     */
    public static final String GET_BINARY_DATA_DEFAULT = "SELECT {2} FROM {1} WHERE {3} = {4,number}";

    /**
     *  Name of the scheme for reading a text field from a node
     *  The parameters accepted are:
     *  <lu>
     *    <li>{0} the storage manager (StorageManager), or the basename for tables (String)</li>
     *    <li>{1} the builder to delete the node from (MMObjectBuilder), or the builder table name (String)</li>
     *    <li>{2} the text field (FieldDefs), or the text field name (String)</li>
     *    <li>{3} the 'number' field (FieldDefs), or the database field name (String)</li>
     *    <li>{4} the number of the object to update (Integer)</li>
     *  </ul>
     */
    public static final String GET_TEXT_DATA = "get.text.data.scheme";

    /**
     *  The default scheme for reading a text field
     */
    public static final String GET_TEXT_DATA_DEFAULT = "SELECT {2} FROM {1} WHERE {3} = {4,number}";

    /**
     *  Name of the scheme for inserting a node
     *  The parameters accepted are:
     *  <lu>
     *    <li>{0} the storage manager (StorageManager), or the basename for tables (String)</li>
     *    <li>{1} the builder to update (MMObjectBuilder), or the builder table name (String)</li>
     *    <li>{2} A comma-separated list of fieldnames to update'</li>
     *    <li>{3} A comma-separated list of value-placeholders to update (a value placehodler takes the format '?')</li>
     *  </ul>
     */
    public static final String INSERT_NODE = "update.node.scheme";

    /**
     *  The default scheme for inserting a node type.
     */
    public static final String INSERT_NODE_DEFAULT = "INSERT INTO {1} ({2}) VALUES ({3})";

    /**
     *  Name of the scheme for selecting a node.
     *  The parameters accepted are:
     *  <lu>
     *    <li>{0} the storage manager (StorageManager), or the basename for tables (String)</li>
     *    <li>{1} the builder to query (MMObjectBuilder), or the builder table name (String)</li>
     *    <li>{2} the 'number' field (FieldDefs), or the database field name (String)</li>
     *    <li>{3} the number to locate (Integer)</li>
     *  </ul>
     */
    public static final String SELECT_NODE = "select.node.scheme";

    /**
     *  The default scheme for selecting a node.
     */
    public static final String SELECT_NODE_DEFAULT = "SELECT * FROM {1} WHERE {2} = {3,number}";

    /**
     *  Name of the scheme for selecting a node type.
     *  The parameters accepted are:
     *  <lu>
     *    <li>{0} the storage manager (StorageManager), or the basename for tables (String)</li>
     *    <li>{1} the MMBase module (MMBase), or the object table name (String)</li>
     *    <li>{2} the 'number' field (FieldDefs), or the database field name (String)</li>
     *    <li>{3} the number to locate (Integer)</li>
     *  </ul>
     */
    public static final String SELECT_NODE_TYPE = "select.nodetype.scheme";

    /**
     *  The default scheme for selecting a node type.
     */
    public static final String SELECT_NODE_TYPE_DEFAULT = "SELECT otype FROM {1} WHERE {2} = {3,number}";

    /**
     *  Name of the scheme for updating a node type.
     *  The parameters accepted are:
     *  <lu>
     *    <li>{0} the storage manager (StorageManager), or the basename for tables (String)</li>
     *    <li>{1} the builder to update (MMObjectBuilder), or the builder table name (String)</li>
     *    <li>{2} A comma-separated list of fields to update, in the format 'fieldname = ?'</li>
     *    <li>{3} the 'number' field (FieldDefs), or the database field name (String)</li>
     *    <li>{4} the number of the object to update (Integer)</li>
     *  </ul>
     */
    public static final String UPDATE_NODE = "update.node.scheme";

    /**
     *  The default scheme for updating a node type.
     */
    public static final String UPDATE_NODE_DEFAULT = "UPDATE {1} SET {2} WHERE {3} = {4,number}";

}
