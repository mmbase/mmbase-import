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
 * @version $Id: Schemes.java,v 1.1 2003-07-31 09:53:37 pierre Exp $
 */
public final class Schemes {

    /**
     *  Name of the scheme for creating a row type (i.e.. for an OO-database such as Informix)
     *  The parameters accepted are:
     *  <lu>
     *    <li>{0} the builder to create the row type for</li>
     *    <li>{1} the field definitions (excluding index definitions)</li>
     *    <li>{2} the builder that this rowtype extends from</li>
     *  </ul>
     *
     * This attribute is optional, and there is no default for this scheme.
     * An example (for Informix):
     * <p>
     *  <code> CREATE ROW TYPE {0}_t ({1}) EXTENDS {2}_t </code>
     * </p>
     */
    public static final String CREATE_ROW_TYPE_SCHEME = "create.rowtype.scheme";
    
    /**
     *  Name of the scheme for creating a table
     *  The parameters accepted are:
     *  <lu>
     *    <li>{0} the builder to create the table for</li>
     *    <li>{1} the field definitions (excluding index definitions)</li>
     *    <li>{2} the field definitions, including index definitions</li>
     *    <li>{3} the index definitions</li>
     *    <li>{4} the builder that this table extends from</li>
     *  </ul>
     *
     * You can set up your scheme to craete extended tables (i.e. in Postgresql). 
     * You also can define indexes or fields seperate (i.e. in HSQL or in a craete table after a create row type in Informix)
     * or in one go (as you might do with MySQL).
     */
    public static final String CREATE_TABLE_SCHEME = "create.table.scheme";
    
    /**
     *  The default scheme for creating a table.
     */
    public static final String CREATE_TABLE_SCHEME_DFP = "CREATE TABLE {0} {1}";
    
    /**
     *  Name of the scheme for selecting a node type.
     *  The parameters accepted are:
     *  <lu>
     *    <li>{0} the builder to delete the node from (MMObjectBuilder), or the builder table name (String)</li>
     *    <li>{1} the 'number' field (FieldDefs), or the database field name (String)</li>
     *    <li>{2} the number of the object to update (Integer)</li>
     *  </ul>
     */
    public static final String DELETE_NODE_SCHEME = "delete.node.scheme";
    
    /**
     *  The default scheme for selecting a node type.
     */
    public static final String DELETE_NODE_SCHEME_DFP = "DELETE FROM {0} WHERE {1} = {2,number}";
    
    /**
     *  Name of the scheme for reading a binary field from a node
     *  The parameters accepted are:
     *  <lu>
     *    <li>{0} the builder to delete the node from (MMObjectBuilder), or the builder table name (String)</li>
     *    <li>{1} the binary field (FieldDefs), or the binary field name (String)</li>
     *    <li>{2} the 'number' field (FieldDefs), or the database field name (String)</li>
     *    <li>{3} the number of the object to update (Integer)</li>
     *  </ul>
     */
    public static final String GET_BINARY_DATA_SCHEME = "get.binary.data.scheme";
    
    /**
     *  The default scheme for reading a binary field
     */
    public static final String GET_BINARY_DATA_SCHEME_DFP = "SELECT {1} FROM {0} WHERE {2} = {3,number}";
    
    /**
     *  Name of the scheme for reading a text field from a node
     *  The parameters accepted are:
     *  <lu>
     *    <li>{0} the builder to delete the node from (MMObjectBuilder), or the builder table name (String)</li>
     *    <li>{1} the text field (FieldDefs), or the text field name (String)</li>
     *    <li>{2} the 'number' field (FieldDefs), or the database field name (String)</li>
     *    <li>{3} the number of the object to update (Integer)</li>
     *  </ul>
     */
    public static final String GET_TEXT_DATA_SCHEME = "get.text.data.scheme";
    
    /**
     *  The default scheme for reading a text field
     */
    public static final String GET_TEXT_DATA_SCHEME_DFP = "SELECT {1} FROM {0} WHERE {2} = {3,number}";
    
    /**
     *  Name of the scheme for inserting a node
     *  The parameters accepted are:
     *  <lu>
     *    <li>{0} the builder to update (MMObjectBuilder), or the builder table name (String)</li>
     *    <li>{1} A comma-separated list of fieldnames to update'</li>
     *    <li>{2} A comma-separated list of value-placeholders to update (a value placehodler takes the format '?')</li>
     *    <li>{3} the 'number' field (FieldDefs), or the database field name (String)</li>
     *    <li>{4} the number of the object to update (Integer)</li>
     *  </ul>
     */
    public static final String INSERT_NODE_SCHEME = "update.node.scheme";
    
    /**
     *  The default scheme for inserting a node type.
     */
    public static final String INSERT_NODE_SCHEME_DFP = "INSERT INTO {0} ({1}) VALUES ({2}) WHERE {3} = {4,number}";

    /**
     *  Name of the scheme for selecting a node.
     *  The parameters accepted are:
     *  <lu>
     *    <li>{0} the builder to query (MMObjectBuilder), or the builder table name (String)</li>
     *    <li>{1} the 'number' field (FieldDefs), or the database field name (String)</li>
     *    <li>{2} the number to locate (Integer)</li>
     *  </ul>
     */
    public static final String SELECT_NODE_SCHEME = "select.node.scheme";
    
    /**
     *  The default scheme for selecting a node.
     */
    public static final String SELECT_NODE_SCHEME_DFP = "SELECT * FROM {0} WHERE {1} = {2,number}";

    /**
     *  Name of the scheme for selecting a node type.
     *  The parameters accepted are:
     *  <lu>
     *    <li>{0} the MMBase module (MMBase), or the object table name (String)</li>
     *    <li>{1} the 'number' field (FieldDefs), or the database field name (String)</li>
     *    <li>{2} the number to locate (Integer)</li>
     *  </ul>
     */
    public static final String SELECT_NODE_TYPE_SCHEME = "select.nodetype.scheme";
    
    /**
     *  The default scheme for selecting a node type.
     */
    public static final String SELECT_NODE_TYPE_SCHEME_DFP = "SELECT otype FROM {0} WHERE {1} = {2,number}";

    /**
     *  Name of the scheme for updating a node type.
     *  The parameters accepted are:
     *  <lu>
     *    <li>{0} the builder to update (MMObjectBuilder), or the builder table name (String)</li>
     *    <li>{1} A comma-separated list of fields to update, in the format 'fieldname = ?'</li>
     *    <li>{2} the 'number' field (FieldDefs), or the database field name (String)</li>
     *    <li>{3} the number of the object to update (Integer)</li>
     *  </ul>
     */
    public static final String UPDATE_NODE_SCHEME = "update.node.scheme";
    
    /**
     *  The default scheme for updating a node type.
     */
    public static final String UPDATE_NODE_SCHEME_DFP = "UPDATE {0} SET {1} WHERE {2} = {3,number}";

}
