/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.database.support;

import java.sql.*;
import java.util.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.corebuilders.FieldDefs;
import org.mmbase.util.logging.*;

/**
 * Database transaction object.
 * Used to maintain context (connection) between separate database statements, allowing
 * for rollback in complex database transactions (if supported).
 * This class is a base class for making connections and submitting changes to the database.
 *
 * @author Pierre van Rooden
 * @since MMBase-1.6
 * @version $Id: DatabaseTransaction.java,v 1.1 2002-04-08 12:21:31 pierre Exp $
 */
public class DatabaseTransaction implements Transaction {

    /**
     * Logging instance
     */
    private static Logger log = Logging.getLoggerInstance(DatabaseTransaction.class.getName());
    private Connection con=null;
    private Statement stmt=null;
    private ResultSet res = null;
    private DatabaseStorage database = null;
    private boolean supportsRollback=false;

    /**
     * Instantiate a database transaction.
     * Attempts to obtain a connection with the database.
     * You can explicitly turn off rollback facilities (which might slow down database access)
     * for internal routines by specifying useRollbacka s false.
     * @param database the database for which to make the transaction
     * @param useRollback if true, the transaction should use rollback facilities if the database supports them
     * @throws StorageException when a connection could not be made
     */
    public DatabaseTransaction(DatabaseStorage database, boolean useRollback) throws StorageException {
        this.database=database;
        JDBCInterface jdbc=database.getJDBC();
        supportsRollback=useRollback && database.supportsRollback();
        try {
            con= jdbc.getConnection(jdbc.makeUrl());
            con.setAutoCommit(!supportsRollback);
        } catch (Exception e) {
            throw new StorageException(e.getMessage());
        }
    }

    /**
     * Queries the database metadata to test whether this database supports rollback.
     * Rollback of a transaction is defined as making undone any changes to the
     * persistent storage used (i.e. a database), since the transaction started.
     * @throws StorageException when the metadata could not be retrieved
     * @return true if the database supports rollback
     */
    public boolean databaseSupportsRollback() throws StorageException {
        boolean result=false;
        try {
            DatabaseMetaData metaDeta=con.getMetaData();
            result=metaDeta.supportsTransactions();
        } catch (Exception e) {
            throw new StorageException(e.getMessage());
        }
        return result;
    }

    /**
     * Returns true if this transaction supports rollback.
     * Rollback of a transaction is defined as making undone any changes to the
     * persistent storage used (i.e. a database), since the transaction started.
     * @return true if the transaction supports rollback
     */
    public boolean supportsRollback() {
        return supportsRollback;
    }

    /**
     * Queries the database metadata to test whether a given table exists.
     * @param tableName nam of the table to look for
     * @throws StorageException when the metadata could not be retrieved
     * @return true if the table exists
     */
    public boolean hasTable(String tableName) throws StorageException {
        boolean result=false;
        try {
            DatabaseMetaData metaDeta=con.getMetaData();
            ResultSet res=metaDeta.getTables(null,null,tableName,null);
            result=res.next();
        } catch (Exception e) {
            throw new StorageException(e.getMessage());
        }
        return result;
    }

    /**
     * Queries the database metadata to obtain a list of tables following a specified pattern.
     * @param baseName the prefix (base name) of the names of the tables to look for
     * @throws StorageException when the metadata could not be retrieved
     * @return a List of tablenames
     */
    public List getTables(String baseName) throws StorageException {
        List result=new Vector();
        try {
            DatabaseMetaData metaDeta=con.getMetaData();
            ResultSet res=metaDeta.getTables(null,null,baseName+metaDeta.getSearchStringEscape()+"_%",null);
            while (res.next()) {
                String tableName=res.getString("TABLE_NAME");
                result.add(tableName);
            }
        } catch (Exception e) {
            throw new StorageException(e.getMessage());
        }
        return result;
    }

    /**
     * Closes and commits the transaction (i.e. the connection).
     * This method catches any exceptions that occur.
     * If closing or committing fails, the function returns false, rather than throwing an exception
     * (though the error is logged).
     * @return true if committed and closed successfully
     */
    public boolean commit() {
        boolean result=true;
        if (con!=null) {
            if (supportsRollback()) {
                try {
                    con.commit();
                    con.setAutoCommit(true);
                } catch (SQLException e) {
                    log.error(e.toString());
                    result=false;
                }
            }
            try {
                con.close();
            } catch (SQLException e) {
                log.error(e.toString());
                result=false;
            }
        }
        return result;
    }

    /**
     * Rolls back (cancels) the transaction.
     * If cancelling fails, the function returns false, rather than throwing an exception
     * (though the error is logged).
     * @return true if cancelled successfully
     */
    public boolean rollback() {
        boolean result=false;
        if (con!=null) {
            if (supportsRollback()) {
                try {
                    con.rollback();
                    con.setAutoCommit(true);
                    result=true;
                } catch (SQLException e) {
                    log.error(e.toString());
                }
            }
            try {
                con.close();
            } catch (SQLException e) {
                log.error(e.toString());
            }
        }
        return result;
    }

    /**
     * Execute a SQL statement on the database, and return the Resultset.
     * @param sql the sql query to execute
     * @return the ResultSet
     * @throws StorageException when the query failed
     */
    public ResultSet executeQuery(String sql) throws StorageException {
        stmt=null;
        try {
            if (log.isDebugEnabled()) log.trace("execute : "+sql);
            // should we use preparedstatement???
            stmt=con.createStatement();
            res=stmt.executeQuery(sql);
            return res;
        } catch (Exception e){
            throw new StorageException(e.getMessage());
        }
    }

    /**
     * Execute a SQL statement on the database.
     * The statement should be an 'update' sql query (one that does not return a resultset).
     * @param sql the sql query to execute.
     * @return true if the execution was succesfull
     * @throws StorageException when the statement failed
     */
    public boolean executeUpdate(String sql) throws StorageException {
        return executeUpdate(sql,null,null);
    }

    /**
     * Execute a SQL statement on the database.
     * The statement should be an 'update' sql query (one that does not return a resultset).
     * @param sql the sql query to execute.
     * @param fields the list of FieldDefs whose values should be added as the query parameters.
     * @param node the node that contains the field data
     * @return true if the execution was succesfull
     * @throws StorageException when the statement failed
     */
    public boolean executeUpdate(String sql, List fields,MMObjectNode node) throws StorageException {
        try {
            stmt=null;
            if (log.isDebugEnabled()) log.trace("Prepare statement : "+sql);
            stmt=con.prepareStatement(sql);
            stmt.setEscapeProcessing(false); // useful?? not used by prepped statements.
            if (fields!=null) {
                int nroffields=0;
                for (Iterator f=fields.iterator(); f.hasNext();) {
                    FieldDefs field=(FieldDefs)f.next();
                    nroffields++;
                    database.setValuePreparedStatement((PreparedStatement)stmt,node,field.getDBName(),nroffields);
                }
            }
            ((PreparedStatement)stmt).executeUpdate();
            return true;
        } catch (SQLException e) {
            throw new StorageException(e.getMessage());
        }
    }

    /**
     * Attempts to return a single integer result from the last retrieved resultset.
     * Use this method to return the result of a query which returns a resultset of
     * records containing one single numeric field
     * If the query failed, or returned an unexpected result, or if the resultset is exhausted,
     * the function returns -1. If this is due to an error then the error is logged.
     * @return the integer result
     */
    public int getIntegerResult() {
        int result=1;
        try {
            if ((res!=null) && res.next()) {
                result=res.getInt(1);
            }
        } catch (SQLException e) {
            log.error(e.toString());
        }
        return result;
    }

    /**
     * Attempts to return a single Node from the last retrieved resultset.
     * Use this method to return the result of a query which returns a resultset of
     * records containing fields from a builder table
     * If the query failed, or returned an unexpected result, or if the resultset is exhausted,
     * the function returns null. If this is due to an error then the error is logged.
     * @param builder the builder to use for creating the node
     * @return the node
     */
    public MMObjectNode getNodeResult(MMObjectBuilder builder) {
        MMObjectNode result=null;
        try {
            if ((res!=null) && res.next()) {
                result=builder.getNewNode("system");
                ResultSetMetaData rd=res.getMetaData();
                String fieldname;
                String fieldtype;
                for (int i=1;i<=rd.getColumnCount();i++) {
                    fieldname=database.mapToMMBaseFieldName(rd.getColumnName(i));
                    database.loadFieldFromTable(result,fieldname,res,i);
                }
                // clear the changed signal
                result.clearChanged();
            }
        } catch (SQLException e) {
            log.error(e.toString());
            result=null;
        }
        return result;
    }


}
