/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module.database;

import java.util.*;

/**
 * The interface used to talk to databases
 * Any class that implements this interface can be used as
 * to allow you to talk to your database (hitlisted use jdbc).
 * @see org.mmbase.module.database.DBMdispatcher
 */
public interface DBMaccessInterface {
	/**
	 * Initialize the class
	 */
	public void init(Hashtable properties);

	/**
	 * Execute a SQL query returning Vectors (columns) in a Vector (rows)
	 */
	public Vector query(String query);

	/**
	 * Execute a SQL query returning Vectors (columns) in a Vector (rows)
	 */
	public Vector query_nol(String query);

	/**
	 * Execute a SQL query returning a Vector containg all columns and rows
	 * after each other.
	 */
	public Vector queryflat(String query);

	/**
	 * Open the connection to the database.
	 * Note: this should not be done in init in the class
	 */
	public boolean start_connection();

	/**
	 * Close the connection to the database
	 */
	public void stop_connection();

	/**
	 * Retry the connection, DBMdispatcher thinks the connection is
	 * hosed, so do the right thing the make it work again.
	 */
	public boolean retry_connection();


	/**
	 * Get an object from the database
	 */
	public String get_object(String u);

	/**
	 * Put an object in the database
	 */
	public boolean put_object(String u,String l);

	/**
	 * Delete a binary in the database
	 */
	public boolean delbinary(String handle);

	/**
	 * Get a binary from the database.
	 */
	public byte[] getbinary(String handle);

	/**
	 * Puts a binary in the database
	 */
	public String putbinary(byte[] data);

	/**
	 * Reset/Set the debug flag in the class
	 */
	public boolean setdebug(boolean debug);

	/**
	 * Get the current debug flag state
	 */
	public boolean getdebug();

	/**
	 * Return the state of the connection
	 */
	public boolean state_up();
}
