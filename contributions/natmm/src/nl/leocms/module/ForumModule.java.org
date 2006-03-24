/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is LeoCMS.
 *
 * The Initial Developer of the Original Code is
 * 'De Gemeente Leeuwarden' (The dutch municipality Leeuwarden).
 *
 * See license.txt in the root of the LeoCMS directory for the full license.
 */
package nl.leocms.module;

import java.io.IOException;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import org.mmbase.module.Module;
import org.mmbase.module.core.MMBase;
import org.mmbase.module.database.MultiConnection;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @author Hillebrand Gelderblom
 * 
 */
public class ForumModule extends Module implements Runnable {
	/** MMBase logging system */
	private static Logger log = Logging.getLoggerInstance(ForumModule.class.getName());

	private String sqlExists = null;
	private String sqlScript = null;

	/** The mmbase. */
	private MMBase mmb = null;

	/**
	 * @see org.mmbase.module.Module#onload()
	 */
	public void onload() {
	}

	/**
	 * @see org.mmbase.module.Module#init()
	 */
	public void init() {
		mmb = (MMBase) Module.getModule("MMBASEROOT");
		// Initialize the module.
		sqlExists = getInitParameter("sql_exists");
		if (sqlExists == null) {
			 throw new IllegalArgumentException("Sql Exists=");
		}
		sqlScript = getInitParameter("sql_script");
		if (sqlScript == null) {
			 throw new IllegalArgumentException("Sql Script=");
		}
		// Start thread to wait for mmbase to be up and running.
		new Thread(this).start();
	}
	
	/**
	 * Tests whether a table is created.
	 * @return <code>true</code> if the table exists, <code>false</code> otherwise
	 */
	public boolean created() {
		MultiConnection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			if (mmb == null) log.info("mmbase null");
			con = mmb.getConnection();
			if (con == null) log.info("connection null");
         DatabaseMetaData meta = con.getMetaData();
         ResultSet tables = meta.getTables(null, con.getMetaData().getUserName(), sqlExists, null);
         while (tables.next()) {
            String tableName = tables.getString("TABLE_NAME");
            log.debug("Examining table: " + tableName);
            if (tableName.equalsIgnoreCase(sqlExists)) {
               log.info("forum tables allready created!!");
               return true;
            }
         }
		} catch (SQLException e) {
			log.debug("table does not exist " + e);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				log.debug("ResultSet close failed");
			}
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				log.debug("Statement close failed");
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				log.debug("Connection close failed");
			}
		}
      log.info("Tables for forum don't exist yet !!");
		return false;
	}

	/**
	 * Creates a new tables in the current database.
	 */
	public boolean create() {
		MultiConnection con = null;
		PreparedStatement stmt = null;
		try {
         SqlScriptReader scriptReader = new SqlScriptReader(sqlScript);
         List queries = scriptReader.getQueries();
			con = mmb.getConnection();
         for (Iterator it = queries.iterator(); it.hasNext();) {
            String query = (String) it.next();
            log.debug("Executing query: " + query);
            stmt = con.prepareStatement(query);
            stmt.executeUpdate();
         }
		} catch (SQLException e) {
			log.warn("Database error " + Logging.stackTrace(e));
			return false;
		} catch (IOException e) {
			log.warn("Reading of scriptfile " + sqlScript + " failed " + Logging.stackTrace(e));
			return false;
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				log.debug("Statement close failed");
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				log.debug("Connection close failed");
			}
		}
		return true;
	}

	/**
	 * Wait for mmbase to be up and running,
	 * then execute the tests.
	 */
	public void run() {
		// Wait for mmbase to be up & running.
		while (!mmb.getState()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
		}
				
		if (!created()) {
			 log.info("Creating tables");
			 
			 if (!create()) {
				  // can't create table.
				  // Throw an exception
				  throw new RuntimeException("Cannot create tables.");
			 }
		}
	}
}