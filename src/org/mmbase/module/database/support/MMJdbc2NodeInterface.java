/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.database.support;

import java.sql.*;

import org.mmbase.storage.StorageException;

import org.mmbase.storage.search.SearchQueryHandler;
import org.mmbase.module.core.*;
import org.mmbase.module.database.*;
import org.mmbase.util.XMLDatabaseReader;

/**
 * MMJdbc2NodeInterface interface needs to be implemented to support a new database
 * It is used to abstract the query's needed for mmbase for each database.
 * @deprecated This code is scheduled for removal once MMBase has been fully converted to the new
 *             StorageManager implementation.
 * @author Vpro
 * @author Pierre van Rooden
 * @version $Id: MMJdbc2NodeInterface.java,v 1.26 2004-01-27 12:04:47 pierre Exp $
 */
public interface MMJdbc2NodeInterface extends SearchQueryHandler {
    /**
     * Returns whether this database support layer allows for buidler to be a parent builder
     * (that is, other builders can 'extend' this builder and its database tables).
     *
     * @since MMBase-1.6
     * @param builder the builder to test
     * @return true if the builder can be extended
     */
    public boolean isAllowedParentBuilder(MMObjectBuilder builder);

    /**
     * Registers a builder as a parent builder (that is, other buidlers can 'extend' this
     * builder and its database tables).
     * At the least, this code should check whether the builder is allowed as a parent builder,
     * and throw an exception if this is not possible.
     * This method can be overridden to allow for optimization of code regarding such builders.
     *
     * @since MMBase-1.6
     * @param parent the parent builder to register
     * @param child the builder to register as the parent's child
     * @throws UnsupportedDatabaseOperationException when the databse layer does not allow extension of this builder
     */
    public void registerParentBuilder(MMObjectBuilder parent, MMObjectBuilder child)
        throws StorageException;

    /**
     * Sets the value of the field with name 'fieldName' in the node. Using the given database
     * result set. The value of the field will be taken form the i-th collumn of the result set.
     * @param node      The node from which a field must be set
     * @param fieldName The name of the field which must be set
     * @param resultSet The resultset from which the value of the field must be obtained
     * @param i         The integer indicating from which position the value must be gotten from the resultSet record.
     */
    public MMObjectNode decodeDBnodeField(MMObjectNode node, String fieldName, ResultSet resultSet, int i);


    /**
     * @see #decodeDBnodeField(MMObjectNode, String, ResultSet, int)
     * @param prefix When using cluster nodes, the key of the value map of the node must be prefixed (e.g. with 'news.')
     */
    public MMObjectNode decodeDBnodeField(MMObjectNode node, String fieldName, ResultSet resultSet, int i, String prefix);

    /**
     * Converts an MMNODE expression to an SQL expression. Returns the
     * result as an SQL where-clause, but with the leading "WHERE " left out.
     *
     * @param where The MMNODE expression.
     * @param bul The builder for the type of nodes that is queried.
     * @return The SQL expression.
     * @see org.mmbase.module.core.MMObjectBuilder#convertMMNode2SQL(String)
     */
    public String getMMNodeSearch2SQL(String where,MMObjectBuilder bul);
    /**
     * @javadoc
     */
    public String getShortedText(String tableName,String fieldname,int number);
    /**
     * @javadoc
     */
    public byte[] getShortedByte(String tableName,String fieldname,int number);
    /**
     * @javadoc
     */
    public byte[] getDBByte(ResultSet rs,int idx);
    /**
     * @javadoc
     */
    public String getDBText(ResultSet rs,int idx);
    /**
     * @javadoc
     */
    public int insert(MMObjectBuilder bul,String owner, MMObjectNode node);
    /**
     * @javadoc
     */
    public boolean commit(MMObjectBuilder bul,MMObjectNode node);
    /**
     * @javadoc
     */
    public void removeNode(MMObjectBuilder bul,MMObjectNode node);
    /**
     * Gives an unique number for a node to be inserted.
     * This method should be implemneted to work with multiple mmbase instances working on
     * the same storage.
     * @return unique number
     */
    public int getDBKey();
    /**
     * @javadoc
     */
    public void init(MMBase mmb,XMLDatabaseReader parser);
    /**
     * @javadoc
     */
    public void setDBByte(int i, PreparedStatement stmt,byte[] bytes);
    /**
     * Tells if a table already exists
     * @return true if table exists, false if table doesn't exists
     */
    public boolean created(String tableName);
    /**
     * @javadoc
     */
    public boolean create(MMObjectBuilder bul);
    /**
     * @javadoc
     */
    public boolean createObjectTable(String baseName);
     /**
     * @javadoc
     */
    public MultiConnection getConnection(JDBCInterface jdbc) throws SQLException;
     /**
     * @javadoc
     */
    public String getDisallowedField(String allowedfield);
     /**
     * @javadoc
     */
    public String getAllowedField(String disallowedfield);
     /**
     * @javadoc
     */
    public String getNumberString();
    /**
     * @javadoc
     */
    public String getOwnerString();
    /**
     * @javadoc
     */
    public String getOTypeString();
    /**
     * @javadoc
     */
    public boolean drop(MMObjectBuilder bul);
    /**
     * @javadoc
     */
    public boolean updateTable(MMObjectBuilder bul);
    /**
     * @javadoc
     */
    public boolean addField(MMObjectBuilder bul,String dbname);
    /**
     * @javadoc
     */
    public boolean removeField(MMObjectBuilder bul,String dbname);
    /**
     * @javadoc
     */
    public boolean changeField(MMObjectBuilder bul,String dbname);
}
