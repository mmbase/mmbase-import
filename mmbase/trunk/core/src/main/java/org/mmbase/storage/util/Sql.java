/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.util;

import java.sql.*;
import javax.sql.*;


import org.mmbase.module.core.MMBase;
import org.mmbase.storage.implementation.database.Attributes;
import org.mmbase.util.logging.*;


/**
 * Meant to be used as an 'AfterDeployment' job of an app1 xml. Automatically execute some SQL.
 * E.g.
  &lt;afterdeployment&gt;
    &lt;runnable version="10" class="org.mmbase.storage.util.Sql"&gt;
      &lt;param name="query"&gt;alter table $PREFIX_blocks add refreshpolicy TEXT&lt;/param&gt;
    &lt;/runnable&gt;
    &lt;runnable version="10" class="org.mmbase.storage.util.Sql"&gt;
      &lt;param name="query"&gt;alter table $PREFIX_blocks add refreshpolicyparam TEXT&lt;/param&gt;
    &lt;/runnable&gt;
  &lt;/afterdeployment&gt;
 *
 * @author Michiel Meeuwissen
 * @version $Id: StorageReader.java 36284 2009-06-22 20:58:09Z michiel $
 * @since MMBase-1.9.3
 */
public class Sql implements Runnable {

    private static final Logger log = Logging.getLoggerInstance(Sql.class);
    private String query;

    public void setQuery(String q) {
        query = q;
    }

    @Override
    public void run() {
        Connection con = null;
        Statement stmt = null;
        try {
            DataSource dataSource = (DataSource) MMBase.getMMBase().getStorageManagerFactory().getAttribute(Attributes.DATA_SOURCE);
            con = dataSource.getConnection();
            stmt = con.createStatement();
            query = query.replace("$PREFIX", MMBase.getMMBase().getBaseName());
            ResultSet rs = stmt.executeQuery(org.mmbase.util.transformers.Xml.XMLUnescape(query));
            {
                StringBuilder head = new StringBuilder();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++)  {
                    if (i > 1) {
                        head.append(",");
                    }
                    head.append(rs.getMetaData().getColumnName(i));
                }
                log.info(head);
            }
            int seq = 0;
            while(true) {
                boolean valid = rs.next();
                seq ++;
                int max = Integer.MAX_VALUE;
                if (seq >= max) break;
                if (! valid) break;
                StringBuilder line = new StringBuilder();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    if (i > 1) {
                        line.append(",");
                    }
                    line.append(rs.getString(i)).append(',');
                }
            }
        } catch (SQLException  sqe) {
            throw new RuntimeException(sqe);
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
        return getClass().getName() + ":" + query;
    }
}
