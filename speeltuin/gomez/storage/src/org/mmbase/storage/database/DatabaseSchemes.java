/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.database;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @since MMBase-1.7
 * @version $Id: DatabaseSchemes.java,v 1.2 2003-07-28 12:57:42 pierre Exp $
 */
public final class DatabaseSchemes {

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
     *  Name of the scheme for selecting a node type.
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
     *  The default scheme for selecting a node type.
     */
    public static final String UPDATE_NODE_SCHEME_DFP = "UPDATE {0} SET {1} WHERE {2} = {3,number}";

}
