/*
 This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 */
package org.mmbase.applications.crontab;

import java.sql.*;
import javax.sql.DataSource;
import org.mmbase.storage.implementation.database.GenericDataSource;
import org.mmbase.module.core.MMBase;
import org.mmbase.util.logging.*;

/**
 * If periodicly some SQL query must be executed (e.g. database administration tasks), then the
 * following Job can be used.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */

public class MMBaseQueryJob extends AbstractCronJob  {
    private static final Logger log = Logging.getLoggerInstance(MMBaseQueryJob.class);

    private String sql;
    protected void init() {
        // determin what needs to be done in run().
        sql = cronEntry.getConfiguration();

    }

    public final void run() {
        try {
            MMBase mmbase = MMBase.getMMBase();

            // why is there no 'getDataSource' method?
            DataSource dataSource =  ((org.mmbase.storage.implementation.database.DatabaseStorageManagerFactory) mmbase.getStorageManagerFactory()).getDataSource();
            // some hackery
            Connection connection;
            if (dataSource instanceof GenericDataSource) {
                // we don't want a pooled query, because they are killed.
                connection = ((GenericDataSource) dataSource).getDirectConnection();
            } else {
                connection = dataSource.getConnection();
            }
            long start = System.currentTimeMillis();
            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery(sql);
            log.service("Executed " + sql + " in " + (System.currentTimeMillis() - start) + " ms");
            ResultSetMetaData meta;

            try {
                meta = results.getMetaData();
                StringBuffer heading = new StringBuffer();
                for (int i = 1; i <= meta.getColumnCount(); i++)  {
                    heading.append(results.getMetaData().getColumnName(i));
                    if (i < meta.getColumnCount()) heading.append("|");
                }
                log.service(heading.toString());
                while(results.next()) {
                    for (int i = 1; i <= meta.getColumnCount(); i++)  {
                        heading.append(results.getString(i));
                        if (i < meta.getColumnCount()) heading.append("|");
                    }
                }
            } catch (Exception e) {
                // perhaps the result set simply is not available for use any more (this class is
                // mainly used for administrative tasks). That is no error.
                log.service(e.getMessage() + ": Cannot show results of '" + sql + "'");
            }
            if (results != null) results.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
