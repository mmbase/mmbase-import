package org.mmbase.application.multilngual.module;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.mmbase.cache.Cache;
import org.mmbase.module.Module;
import org.mmbase.module.core.MMBase;
import org.mmbase.module.database.MultiConnection;
import org.mmbase.util.logging.*;

/**
 * @author Nico Klasens
 * 
 */
public class TranslationModule extends Module implements Runnable {
	/** MMBase logging system */
	private static Logger log =
		Logging.getLoggerInstance(TranslationModule.class.getName());

	private String sqlExistsTable = null;
	private String sqlCreateTable = null;
	private String sqlSelect = null;
	private String sqlDelete = null;
	private String sqlUpdate = null;
	private String sqlInsert = null;

	public static final int CACHE_SIZE = 20000;

	/**
	 * The cache that contains the last X types of all requested objects
	 * @since 1.7
	 */
	public static Cache cache;

	static {
		 cache = new Cache(CACHE_SIZE) {
			  public String getName()        { return "MultiLingual"; }
			  public String getDescription() { return "Cache for MultiLingual";}
		 };
		cache.putCache();
	}
	
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
		mmb = (MMBase) org.mmbase.module.Module.getModule("MMBASEROOT");
		// Initialize the module.
		sqlExistsTable = getInitParameter("sql_exists");
		if (sqlExistsTable == null)
		{
			 throw new IllegalArgumentException("Sql Exists Table=");
		}
		sqlCreateTable = getInitParameter("sql_create");
		if (sqlCreateTable == null)
		{
			 throw new IllegalArgumentException("Sql Create Table=");
		}
		sqlSelect = getInitParameter("sql_select");
		if (sqlSelect == null)
		{
			 throw new IllegalArgumentException("Sql Select");
		}
		
		sqlDelete = getInitParameter("sql_delete");
		if (sqlDelete == null)
		{
			 throw new IllegalArgumentException("Sql Delete");
		}
		
		sqlUpdate = getInitParameter("sql_update");
		if (sqlUpdate == null)
		{
			 throw new IllegalArgumentException("Sql Update");
		}
		
		sqlInsert = getInitParameter("sql_insert");
		if (sqlInsert == null)
		{
			 throw new IllegalArgumentException("Sql Insert");
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
			stmt = con.prepareStatement(sqlExistsTable);
			if (stmt == null) log.info("stmt null");
			rs = stmt.executeQuery();
			rs.next();
			return true;
		} catch (Exception e) {
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
			} catch (Exception e) {
				log.debug("Statement close failed");
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				log.debug("Connection close failed");
			}
		}
		return false;
	}

	/**
	 * Creates a new builder table in the current database.
	 */
	public boolean create() {
		MultiConnection con = null;
		PreparedStatement stmt = null;
		try {
			con = mmb.getConnection();
			stmt = con.prepareStatement(sqlCreateTable);
			stmt.executeUpdate();
		} catch (Exception e) {
			log.warn("Database connection failed " + Logging.stackTrace(e));
			return false;
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				log.debug("Statement close failed");
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				log.debug("Connection close failed");
			}
		}
		return true;
	}
	
	/** get Cache key
	 * 
	 * @param nodeNumber number of node
	 * @param fieldname name of field of node
	 * @param languagecode iso language code
	 * @return translation of field if present else null
	 */
	private String getCacheKey(int nodeNumber, String fieldname, String languagecode, String countrycode) {
		return nodeNumber + "_" + fieldname + "_" + languagecode + "_" + countrycode;
	}

	/** Lookup the translation of the field in the table
	 * 
	 * @param nodeNumber number of node
	 * @param fieldname name of field of node
	 * @param languagecode iso language code
	 * @return translation of field if present else null
	 */
	public String lookupTranslation(int nodeNumber, String fieldname, String languagecode, String countrycode) {
		if (cache != null) {
			String cacheKey = getCacheKey(nodeNumber, fieldname, languagecode, countrycode);
			if (cache.containsKey(cacheKey)) {
				return (String) cache.get(cacheKey);
			}
		}

		MultiConnection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = mmb.getConnection();

			stmt = con.prepareStatement(sqlSelect);
			stmt.setInt(1,nodeNumber);
			stmt.setString(2,fieldname);
			stmt.setString(3,languagecode);
			stmt.setString(4,countrycode);
			
			rs = stmt.executeQuery();
			if (rs.next()) {
				String value = getStringValue(rs, 1);
				
				if (cache != null) {
					String cacheKey = getCacheKey(nodeNumber, fieldname, languagecode, countrycode);
					if (!cache.containsKey(cacheKey)) {
						cache.put(cacheKey,value);
					}
				}
				return value;
			}
		} catch (Exception e) {
			log.warn("Database connection failed" + e);
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
			} catch (Exception e) {
				log.debug("Statement close failed");
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				log.debug("Connection close failed");
			}
		}
		return null;
	}

	/** update the translation of the field in the table
	 * 
	 * @param nodeNumber number of node
	 * @param fieldname name of field of node
	 * @param languagecode iso language code
	 * @param countrycode iso language code
	 * @param fieldvalue value of field
	 */
	public void updateTranslation(
		int nodeNumber,
		String fieldname,
		String languagecode,
		String countrycode,
		String fieldvalue)
   {
		if (fieldvalue == null || "".equals(fieldvalue.trim())) {
			deleteTranslation(nodeNumber, fieldname, languagecode, countrycode);
		}
		else {
			// update field when fails to update try to insert
			
			boolean insert = false;
			
			MultiConnection con = null;
			PreparedStatement stmt = null;
			try {
				con = mmb.getConnection();

				stmt = con.prepareStatement(sqlUpdate);
				setStringValue(stmt,1,fieldvalue);
				stmt.setInt(2,nodeNumber);
				stmt.setString(3,fieldname);
				stmt.setString(4,languagecode);
				stmt.setString(5,countrycode);
			
				int updated = stmt.executeUpdate();
				if (updated == 0) {
					insert = true;
				}
				else {
					if (cache != null) {
						String cacheKey = getCacheKey(nodeNumber, fieldname, languagecode, countrycode);
						if (cache.containsKey(cacheKey)) {
							cache.remove(cacheKey);
						}
					}
				}
			} catch (Exception e) {
				log.warn("Database connection failed" + e);
			} finally {
				try {
					if (stmt != null) {
						stmt.close();
					}
				} catch (Exception e) {
					log.debug("Statement close failed");
				}
				try {
					if (con != null) {
						con.close();
					}
				} catch (Exception e) {
					log.debug("Connection close failed");
				}
			}
			
			if (insert) {			
				insertTranslation(nodeNumber, fieldname, languagecode, countrycode, fieldvalue);
			}
		}
	}

	/** delete the translation of the field in the table
	 * 
	 * @param nodeNumber number of node
	 * @param fieldname name of field of node
	 * @param languagecode iso language code
	 * @param countrycode iso language code 
	 */
	public void deleteTranslation(int nodeNumber, String fieldname, String languagecode, String countrycode) {
		MultiConnection con = null;
		PreparedStatement stmt = null;
		try {
			con = mmb.getConnection();

			stmt = con.prepareStatement(sqlDelete);
			stmt.setInt(1,nodeNumber);
			stmt.setString(2,fieldname);
			stmt.setString(3,languagecode);
			stmt.setString(4,countrycode);
			
			stmt.executeUpdate();

			if (cache != null) {
				String cacheKey = getCacheKey(nodeNumber, fieldname, languagecode, countrycode);
				if (!cache.containsKey(cacheKey)) {
					cache.remove(cacheKey);
				}
			}

		} catch (Exception e) {
			log.warn("Database connection failed" + e);
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				log.debug("Statement close failed");
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				log.debug("Connection close failed");
			}
		}
	}

	/** insert the translation of the field in the table
	 * 
	 * @param nodeNumber number of node
	 * @param fieldname name of field of node
	 * @param languagecode iso language code
	 * @param countrycode iso language code 
	 */
	public void insertTranslation(int nodeNumber, String fieldname, String languagecode, String countrycode, String fieldvalue) {
		MultiConnection con = null;
		PreparedStatement stmt = null;
		try {
			con = mmb.getConnection();

			stmt = con.prepareStatement(sqlInsert);
			stmt.setInt(1,nodeNumber);
			stmt.setString(2,fieldname);
			stmt.setString(3,languagecode);
			stmt.setString(4,countrycode);
			setStringValue(stmt,5,fieldvalue);
			
			stmt.executeUpdate();

		} catch (Exception e) {
			log.warn("Database connection failed" + e);
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				log.debug("Statement close failed");
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				log.debug("Connection close failed");
			}
		}
	}

	/** get string from the database
	 * 
	 * @param rs Result of translation query
	 * @param idx column index
	 * @return String from blob
	 */
	public String getStringValue(ResultSet rs,int idx) {
		byte[] returndata = null;
		BufferedInputStream bis = null;
		ByteArrayOutputStream baos = null;
		try {
			// The ByteArrayOutputStream buffers all bytes written to it
			// until we call getBytes() which returns to us an
			// array of bytes:
			baos = new ByteArrayOutputStream();
			// Create an input stream from the BLOB column.
			// By default, rs.getBinaryStream()
			// returns a vanilla InputStream instance.
			// We override this for efficiency
			// but you don't have to:
			bis = new BufferedInputStream(rs.getBinaryStream(idx));

			// Make sure its not a NULL value in the column:
			// rs.wasNull() reports whether the last column read had a
			// value of SQL NULL.
			if (!rs.wasNull()) {

				// A temporary buffer for the byte data:
				byte[] bindata = new byte[1024];
				// Used to return how many bytes are read with each read() of the 
				//	input stream:
				int bytesread = 0;

				while ((bytesread = bis.read(bindata, 0, bindata.length)) != -1) {
					// Write out 'bytesread' bytes to the writer instance:
					baos.write(bindata, 0, bytesread);
				}
				// When the read() method returns -1 we've hit the end of
				// the stream,
				// so now we can get our bytes out of the writer object:
				returndata = baos.toByteArray();
			}
		} catch (Exception e) {
			log.error("Stream read error");
			log.error(Logging.stackTrace(e));
		} finally {
			try {
				// Close the binary input stream:
				if (bis != null) {
					bis.close();
				}
			} catch (Exception e) {
				log.error("Stream close failed");
			}
			try {
				// Close the byte array output stream:
				if (baos != null) {
					baos.close();
				}
			} catch (Exception e) {
				log.error("Stream close failed");
			}
		}

		try {
			return new String(returndata, mmb.getEncoding());
		} catch (UnsupportedEncodingException e1) {
			log.error("text bytes are not ISO-8859-1");
		}

		return null;
	}


	/** set string to the database
	 * 
	 * @param stmt sql statment
	 * @param i index of the parameter
	 * @param body value to store
	 */
	public void setStringValue(PreparedStatement stmt, int i, String body) {
		 byte[] rawchars=null;
		 try {
			  rawchars=body.getBytes(mmb.getEncoding());
		 } catch (Exception e) {
			  log.error("String contains odd chars");
			  log.error(body);
			  log.error(Logging.stackTrace(e));
		 
		 }
		 ByteArrayInputStream stream = null;
		 try {
			stream = new ByteArrayInputStream(rawchars);
			  stmt.setBinaryStream(i,stream,rawchars.length);
			  
		 } catch (Exception e) {
			  log.error("Can't set binary stream");
			  log.error(Logging.stackTrace(e));
		 }
		 finally {
		 	try {
		 		if (stream != null) {
					stream.close();
		 		}
		 	}
		 	catch(Exception ioe) {
		 	}
		 }
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
			 log.info("Creating table translations");
			 
			 if (!create() ) {
				  // can't create table.
				  // Throw an exception
				  throw new RuntimeException("Cannot create table translations.");
			 }
		}
	}

}