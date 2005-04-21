/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/

package org.mmbase.module.database;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

import org.mmbase.module.*;


/**
 * Routine to dispatch query's to several database connections.
 * This is the main access point for database connections. 
 * All modules/servlets that refer to the database go through this one.
 *
 * Current dependencies on this module are illustragate.java and 
 * sw_mod.java (in sharedworkspace)
 *
 * @author      Rico Jansen
 * @version 13 Feb 1997
 */
public class DBMdispatcher extends Module implements DBMaccessInterface {

	public boolean state_up = false;
	DBMaccessInterface connections[];
	Class dbmaccess;
	String dbmaccessname="org.mmbase.module.database.DBMaccess";
	boolean connections_busy[];
	int current=0;
	int currenttry=0;
	Hashtable partbuffer=new Hashtable();
	Hashtable properties;
	Hashtable props;

	int openconnects;
	int openfailures;

	/* Default values */
	int connects=1;
	int maxretries=10;
	boolean debug=false;

	/**
	 * Placeholder to satisfy the interface.
	 */
	public void init(Hashtable fake) {
	}

	public void onload() {
	}

	/**
	 * Oh dear somebody changed the properties 
	 */
	public synchronized void reload() {
		getproperties();
		setdebug(debug);
	}

	/**
	 * (re)Read the properties and create the class we use for accessing
	 * the database.
	 */
	void getproperties() {
		int tmpi;
		String tmpstr;

		/* Get the list of properties */
		properties=getInitParameters(); 

		tmpi=Integer.parseInt(getInitParameter("connections"));
		if (tmpi>0 && tmpi<=32) {
			connects=tmpi;
		}
		tmpi=Integer.parseInt(getInitParameter("retries"));
		if (tmpi>1) { // should try atleast once
			maxretries=tmpi;
		}
		tmpstr=getInitParameter("debug");
		if (tmpstr!=null) {
			debug=tmpstr.equalsIgnoreCase("true");
		}
		tmpstr=getInitParameter("Connection");
		if (tmpstr!=null) {
			dbmaccessname=tmpstr;
		}
		try {
			dbmaccess=Class.forName(dbmaccessname);
		} catch (Exception e) {
			System.out.println("Can't get at dbmaccess");
			e.printStackTrace();
		}

		props=getConnectionProperties();
	}

	/**
	 * Create the table of connections to the database.
	 */
	void setup_report() {
		int i;

		System.out.println("Setting up "+connects+" connections");
		connections=new DBMaccessInterface[connects];
		connections_busy=new boolean[connects];
		for (i=0;i<connects;i++) {
			try {
				connections[i]=(DBMaccessInterface)dbmaccess.newInstance();
				connections[i].init(props);
				connections_busy[i]=false;
			} catch (Exception e) {
				System.out.println("Can't get at dbmaccess");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Initialize, read the properties, setup the connections and open them
	 */
	public void init() {
		if (!state_up) {
			openconnects=0;
			openfailures=0;
			getproperties();
			setup_report();
			setdebug(debug);
			start_connection();
		}
	}

	public void unload() {
		stop_connection();
	}

	public void shutdown() {
		stop_connection();
	}

	/**
	 * This method is used by the class implementing the database-connection
	 * to get at its properties (they are prefixed with Connection. in the
	 * propertie file to distinguish them from the properties of this module)
	 */
	private Hashtable getConnectionProperties() {
		String key,value,shortkey;
		Hashtable p=new Hashtable();

		for (Enumeration e=properties.keys();e.hasMoreElements();) {
			key=(String)e.nextElement();
			if (key.indexOf("Connection.")==0) {
				shortkey=key.substring(11);
				if (shortkey.equals("Module")) {
					value=(String)properties.get(key);
					if (value!=null) p.put(shortkey,getModule(value));
				} else {
					value=(String)properties.get(key);
					if (value!=null) p.put(shortkey,value);
				}
			}
		}
		if (debug) System.out.println("Properties Dispatcher "+properties);
		if (debug) System.out.println("Properties Connection "+p);
		return p;
	}

	/**
	 * Open all the connections
	 */
	public synchronized boolean start_connection() {
		int i;

		if (!state_up) {
			for (i=0;i<connects;i++) {
				openconnects++;
				state.put("Open Connects",""+openconnects);
				connections[i].start_connection();
			}
			state_up=true;
		}
		return(state_up);
	}

	/**
	 * Close the connections
	 */
	public synchronized void stop_connection() {
		int i;

		if (state_up) {
			for (i=0;i<connects;i++) {
				connections[i].stop_connection();
			}
			state_up=false;
		}
	}
	
	/**
	 * Recover connections if they go down
	 */
	public synchronized boolean retry_connection() {
		return(retry_connection(false));
	}

	/**
	 * Recover connection, forcing if necessary 
	 */
	public synchronized boolean retry_connection(boolean force) {
		int i;
		boolean rtn=true;

		for(i=0;i<connects;i++) {
			if (connections[i].state_up()==false || force) {
				if (connections[i].retry_connection()==false) {
					System.err.println("************ DB RECONNECT FAILURE **********");
					System.out.println("************ DB RECONNECT FAILURE **********");
					openfailures++;
					state.put("Open Failures",""+openfailures);
					rtn=false;
				} else {
					openconnects++;
					state.put("Open Connects",""+openconnects);
					releaseconnection(i);
				}
			}
		}
		state.put("Open Connects",""+openconnects);
		return(rtn);
	}

	/**
	 * Claim a connection, should propagate failures upward
	 */
	int claimconnection() {
		int con;

		con=Sync_claimconnection();
		if (con>=0) {
			if (connections[con].state_up()==false) {
				if (connections[con].retry_connection()==false) {
					System.err.println("************ DB RECONNECT FAILURE **********");
					System.out.println("************ DB RECONNECT FAILURE **********");
				}
			}
		} else {
			// All connections busy
			retry_connection(true);
		}
		return(con);
	}

	/**
	 * The actual claiming of the connection
	 */
	synchronized int Sync_claimconnection() {
		current++;
		current%=connects;
		while(connections_busy[current]) {
			try{Thread.sleep(250);}catch(InterruptedException e){}
			current++;
			current%=connects;
			currenttry++;
			if (currenttry>=maxretries) {
				System.out.println("DBMDispatcher: Maxretries reached, breaking off");
				currenttry=0;
				return(-1);
			}
		}
		currenttry=0;
		connections_busy[current]=true;
		return(current);
	}
	
	/**
	 * Release the connection
	 */
	void releaseconnection(int connectnum) {
		connections_busy[connectnum]=false;
	}

	/**
	 * Put an (old) object in the database.
	 * Caching the data as well.
	 */
	public boolean put_object(String name,String obj) {
		int con;
		boolean rtn=false;

		partbuffer.put(name,(Object)obj);
		con=claimconnection();
		if (con>=0) {
			rtn=connections[con].put_object(name,obj);
			if (!connections[con].state_up()) {
				connections[con].retry_connection();
				rtn=connections[con].put_object(name,obj);
			}
			releaseconnection(con);
		}
		return(rtn);
	}

	/**
	 * Get an (old) object from the database.
	 * Caching the data as well.
	 */
	public String get_object(String name) {
		int con;
		String rtn;

		rtn=(String)partbuffer.get(name);
		if (rtn==null) {
			con=claimconnection();
			if (con>=0) {
				rtn=connections[con].get_object(name);
				if (!connections[con].state_up()) {
					connections[con].retry_connection();
					rtn=connections[con].get_object(name);
				}
				releaseconnection(con);
				if (rtn!=null) partbuffer.put(name,(Object)rtn);
			}
		}
		return(rtn);
	}

	/**
	 * Process the query, it returns a Vector which contains the rows
	 * which are Vector's again, which contain the columns which are Strings
	 *
	 * @see util.Vector
	 */
	public Vector query(String query) {
		int con;
		Vector rtn=null;

		con=claimconnection();
		if (con>=0) {
			rtn=connections[con].query(query);
			if (!connections[con].state_up()) {
				connections[con].retry_connection();
				rtn=connections[con].query(query);
			}
			releaseconnection(con);
		}
		return(rtn);
	}


	/** door Daniel 
	 * Process the query, it returns a Vector which contains the rows
	 * which are Vector's again, which contain the columns which are Strings
	 *
	 * @see util.Vector
	 */
	public Vector query_nol(String query) {
		int con;
		Vector rtn=null;

		con=claimconnection();
		if (con>=0) {
			rtn=connections[con].query_nol(query);
			if (!connections[con].state_up()) {
				connections[con].retry_connection();
				rtn=connections[con].query_nol(query);
			}
			releaseconnection(con);
		}
		return(rtn);
	}

	/**
	 * Process the query, it returns a Vector which contains the rows
	 * and columns after each other
	 *
	 * @see util.Vector
	 */
	public Vector queryflat(String query) {
		int con;
		Vector rtn=null;

		con=claimconnection();
		if (con>=0) {
			rtn=connections[con].queryflat(query);
			if (!connections[con].state_up()) {
				connections[con].retry_connection();
				rtn=connections[con].queryflat(query);
			}
			releaseconnection(con);
		}
		return(rtn);
	}

	/**
	 * Method for retrieving binaries from the database.
	 */
	public byte[] getbinary(String handle) {
		int con;
		byte[] rtn=null;

		con=claimconnection();
		if (con>=0) {
			rtn=connections[con].getbinary(handle);
			if (!connections[con].state_up()) {
				connections[con].retry_connection();
				rtn=connections[con].getbinary(handle);
			}
			releaseconnection(con);
		}
		return(rtn);
	}

	/**
	 * Put binaries in the database
	 */
	public String putbinary(byte data[]) {
		int con;
		String rtn=null;

		con=claimconnection();
		if (con>=0) {
			rtn=connections[con].putbinary(data);
			if (!connections[con].state_up()) {
				connections[con].retry_connection();
				rtn=connections[con].putbinary(data);
			}
			releaseconnection(con);
		}
		return(rtn);
	}

	/**
	 * Delete binaries from the database
	 */
	public boolean delbinary(String handle) {
		int con;
		boolean rtn=false;

		con=claimconnection();
		if (con>=0) {
			rtn=connections[con].delbinary(handle);
			if (!connections[con].state_up()) {
				connections[con].retry_connection();
				rtn=connections[con].delbinary(handle);
			}
			releaseconnection(con);
		}
		return(rtn);
	}

	/**
	 * Enable/Disable debugging
	 */
	public boolean setdebug(boolean debug) {
		this.debug=debug;
		for (int i=0;i<connects;i++) {
			try {
				connections[i].setdebug(debug);
			} catch (ArrayIndexOutOfBoundsException e) {}
		}
		return(this.debug);
	}

	/**
	 * Get the current debug state
	 */
	public boolean getdebug() {
		return(debug);
	}

	/**
	 * Return the state of the connections
	 */
	public boolean state_up() {
		return(state_up);
	}
}


