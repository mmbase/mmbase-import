/* -*- tab-width: 4; -*-

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.database;

import java.sql.*;
import java.math.*;
import java.net.*;
import java.io.*;
import java.util.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * JDBC Pool, a dummy interface to multiple real connection
 */

public class MultiPool
{


    private static Logger log = Logging.getLoggerInstance(MultiPool.class.getName()); 
    
    Vector pool=new Vector();
    Vector busypool=new Vector();
    int conPos=0;
    int conMax=4;
    int totalConnections=0;
    int maxQuerys=500;
    String url;
    String name;
    String password;
    String dbm;
    Object synobj=new Object();
    DatabaseSupport databasesupport;
    
    
    boolean doReconnect=true;


   MultiPool(DatabaseSupport databasesupport,String url, String name, String password,int conMax) throws SQLException {
		this(databasesupport,url,name,password,conMax,500);

	}
  /**
   * Establish connection to the JDBC Pool(s)
   */
   MultiPool(DatabaseSupport databasesupport,String url, String name, String password,int conMax,int maxQuerys) throws SQLException {
		this.conMax=conMax;
		this.url=url;
		this.name=name;
		this.password=password;
		this.maxQuerys=maxQuerys;
		this.databasesupport=databasesupport;

		// put connections on the pool
		for (int i=0;i<conMax;i++) {
			if (name.equals("url") && password.equals("url")) {
				Connection con=DriverManager.getConnection(url);
				initConnection(con);
				pool.addElement(new MultiConnection(this,con));
			} else {
				Connection con=DriverManager.getConnection(url,name,password);
				initConnection(con);
				pool.addElement(new MultiConnection(this,con));
			}
		}
		dbm=getDBMfromURL(url);
   } 

   /**
	* Check the connections
	*/
	public void checkTime() {
		MultiConnection con,bcon;
		int nowTime;
		int diff;

//		log.debug("JDBC -> Starting the pool check ("+this+") : busy="+busypool.size()+" free="+pool.size());
		nowTime=(int)(System.currentTimeMillis()/1000);
		synchronized(synobj) {
			for (Enumeration e=busypool.elements();e.hasMoreElements();) {
				con=(MultiConnection)e.nextElement();
				diff=nowTime-con.getStartTime();
				if (diff>5) {
					 if (log.isDebugEnabled()) {
                         log.debug("Checking a busy connection "+con);
                     }
				}
				if (diff<30) {
					// below 30 we still wait
				} else if (diff<120) {
					// between 30 and 120 we putback 'zero' connections
					if (con.lastSql==null || con.lastSql.length()==0) {
						log.warn("null connection putBack");
						putBack(con);
					}
				} else {
					// above 120 we close the connection and open a new one
					MultiConnection newcon=null;
	
					if (log.isDebugEnabled()) {
						log.debug("KILLED SQL "+con.lastSql+" time "+diff);
					}
					try {
						Connection realcon=DriverManager.getConnection(url,name,password);
						initConnection(realcon);
						newcon=new MultiConnection(this,realcon);
						if (log.isDebugEnabled()) {
							log.debug("WOW added JDBC connection now ("+pool.size()+")");
						}
					} catch(Exception re) {
						log.error("ERROR Can't add connection to pool");
					}
					busypool.removeElement(con);
					try {
						con.realclose();
					} catch(Exception re) {
						log.error("Can't close a connection !!!");
					}
					if (newcon!=null) pool.addElement(newcon);
				}	
			}
			if ((busypool.size()+pool.size())>conMax) {
				int i;
				if (log.isDebugEnabled()) {
					log.debug("JDBC -> Warning number of connections exceeds conMax "+(busypool.size()+pool.size()));
				}
				// Check if there are dups in the pools
				for(Enumeration e=busypool.elements();e.hasMoreElements();) {
					bcon=(MultiConnection)e.nextElement();
					i=pool.indexOf(bcon);
					if (i>=0) {
						if (log.isDebugEnabled()) {
							log.debug("duplicate connection found at "+i);
						}
						pool.removeElementAt(i);
					}
				}
				
				while(((busypool.size()+pool.size())>conMax) && pool.size()>2) {
					// Remove too much ones.
					con=(MultiConnection)pool.elementAt(0);
					if (log.isDebugEnabled()) {	
						log.debug("removing connection "+con);
					}
					pool.removeElementAt(0);
				}
			}
		}
//		log.error("JDBC -> the pool check done");
	}

   /**
   * get a free connection from the pool
   */
	//public MultiConnection getFree() {
	public MultiConnection getFree() {
		MultiConnection con=null;
		totalConnections++;
		while (pool.size()==0) {
			try{
				Thread.sleep(10000);
			} catch(InterruptedException e){
				log.warn("getFree sleep INT");
			}
			if (log.isDebugEnabled()) {
				log.debug("sleep on "+this);
			}
		}
		synchronized(synobj) {
			con=(MultiConnection)pool.elementAt(0);
			con.claim();
			pool.removeElementAt(0);
			busypool.addElement(con);
		}
		return(con);
	}

   /**
   * putback the used connection in the pool
   */
	public void putBack(MultiConnection con) {
		if (!pool.contains(con)) {
			synchronized(synobj) {
				con.release();
				if (busypool.contains(con)) {
					if (doReconnect && (con.getUsage()>maxQuerys)) {
						MultiConnection oldcon;
						if (log.isDebugEnabled()) {
							log.debug("Re-Opening connection");
						}
						oldcon=con;
						try {
							oldcon.realclose();
						} catch(Exception re) {
							log.error("Can't close a connection !!!");
						}
						try {
							if (name.equals("url") && password.equals("url")) {
								Connection realcon=DriverManager.getConnection(url);
								initConnection(realcon);
								con=new MultiConnection(this,realcon);
							} else {
								Connection realcon=DriverManager.getConnection(url,name,password);
								initConnection(realcon);
								con=new MultiConnection(this,realcon);
							}
						} catch(Exception re) {
							log.error("Can't add connection to pool");
						}
						pool.addElement(con);
						busypool.removeElement(oldcon);
						oldcon=null;
					} else {
						pool.addElement(con);
						busypool.removeElement(con);
					}
				} else {
					log.error("can't remove from putback");
				}
			}
		} else {
		    log.error("trying to putback connection 2 times");
			try {
				throw new Exception("putBack");
			} catch (Exception e) {
				log.error(Logging.stackTrace(e));
			}
		}
	}




   /**
   * get the pool size
   */
	public int getSize() {
		return(pool.size());
	}

   /**
   * get the number of statements performed
   */
	public int getTotalConnectionsCreated() {
		return(totalConnections);
	}

	public Enumeration elements() {
		return(pool.elements());
	}


	public Enumeration busyelements() {
		return(busypool.elements());
	}

	public String toString() {
		return("dbm="+dbm+",name="+name+",conmax="+conMax);
	}
	private String getDBMfromURL(String url) {
		return url;
	}

	private boolean checkConnection(Connection conn) {
		Statement statement;
		boolean rtn;
		try {
			statement=conn.createStatement();
			statement.executeQuery("select count(*) from systables");
	        statement.close();
			rtn=true;
		} catch (Exception e) {
			rtn=false;
			log.error("checkConnection failed");
			log.error(Logging.stackTrace(e));
		}
		return(rtn);
	}

	protected void initConnection(Connection conn) {
		databasesupport.initConnection(conn);
	}
}
