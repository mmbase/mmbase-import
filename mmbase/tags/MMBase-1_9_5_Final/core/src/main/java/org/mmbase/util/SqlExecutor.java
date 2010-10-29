/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.util.*;
import java.sql.*;
import javax.sql.*;
import java.util.*;
import java.util.regex.*;
import org.mmbase.util.logging.*;

/**
 * Generic tool to execute some SQL
 *
 * @since MMBase-1.9.5
 * @author Michiel Meeuwissen
 * @version $Id: FullBackupDataWriter.java 34900 2009-05-01 16:29:42Z michiel $
 */


public class SqlExecutor implements Runnable {

    private static final Logger LOG = Logging.getLoggerInstance(SqlExecutor.class);

    private String query;
    private String update;
    private String onlyIfQuery;
    private Pattern ignore = Pattern.compile("");

    protected DataSource dataSource;
    protected String prefix;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    public void setPrefix(String pref) {
        this.prefix = pref;
    }
    public DataSource getDataSource() {
        return dataSource;
    }
    public String getPrefix() {
        if (prefix == null) {
            return "$PREFIX";
        } else {
            return prefix;
        }
    }

    public void setIgnoreException(String e) {
        ignore = Pattern.compile(e);
    }

    public void setQuery(String q) {
        if (update != null) throw new IllegalStateException();
        query = q;
    }
    public void setUpdate(String u) {
        if (query != null) throw new IllegalStateException();
        update = u;
    }

    /**
     * A query returning either true of false.
     * E.g.  <param name="onlyIf"><![CDATA[select 1 = (select count(*) from mm_versions where m_type='application' and
     * name='Limburg' and m_version < 7);]]></param>
     */

    public void setOnlyIf(String q) {
        onlyIfQuery = q;
    }

    protected void executeQuery(Statement stmt, String q) throws SQLException {
        q = q.replace("$PREFIX", getPrefix());
        LOG.info(" Executing " + q);
        ResultSet rs = stmt.executeQuery(q);
        StringBuilder header = new StringBuilder();
        for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++)  {
            if (i > 1) {
                header.append("|");
            }
            header.append(rs.getMetaData().getColumnName(i));
        }
        LOG.info(header);
        int seq = 0;
        while(true) {
            boolean valid = rs.next();
            if (! valid) break;
            seq ++;
            StringBuilder line = new StringBuilder();
            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                if (i > 1) {
                    line.append("|");
                }
                line.append(rs.getString(i));
            }
            LOG.info(seq + ":" + line);
        }
    }
   protected void executeUpdate(Statement stmt, String u) throws SQLException {
        u = u.replace("$PREFIX", getPrefix());
        LOG.info(" Executing update " + u);
        int result = stmt.executeUpdate(u);
        LOG.service("Result :" + result);
   }

    protected boolean executeOnlyIf(Connection con, String q) throws SQLException {
        if (q == null) return true;
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            q = q.replace("$PREFIX", getPrefix());
            LOG.debug(" Executing query " + q);
            ResultSet rs = stmt.executeQuery(q);
            rs.next();
            boolean res = rs.getBoolean(1);
            LOG.debug("Result: " + res);
            return res;
        } catch (SQLException sqe) {
            LOG.error(sqe.getMessage() + " from " + q);
            throw sqe;
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (Exception g) {}
        }
    }



    public void run() {
        Connection con = null;
        Statement stmt = null;
        try {
            DataSource ds = getDataSource();
            con = ds.getConnection();
            if (executeOnlyIf(con, onlyIfQuery)) {
                stmt = con.createStatement();
                if (query != null) {
                    executeQuery(stmt, query);
                } else if (update != null) {
                    executeUpdate(stmt, update);
                } else {
                    throw new IllegalStateException("Both query and update properties are unset");
                }
            } else {
                LOG.debug("Skipped because of " + onlyIfQuery);
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable t) {
            if (ignore.matcher(t.getMessage()).matches()) {
                LOG.info("Ignoring " + t.getMessage());
            } else {
                throw new RuntimeException(t.getMessage(), t);
            }
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (Exception g) {}
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception g) {}
        }
    }

    @Override
    public String toString() {
        if (update != null) {
            return update;
        } else if (query != null) {
            return query;
        } else {
            return "No query yet";
        }
    }
}


