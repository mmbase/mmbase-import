/* -*- tab-width: 4; -*-

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.sql.*;

import org.mmbase.module.database.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * admin module, keeps track of all the worker pools
 * and adds/kills workers if needed (depending on
 * there load and info from the config module).
 *
 * @version $Id: PropertiesProbe.java,v 1.8 2003-05-08 06:01:22 kees Exp $
 * @author Daniel Ockeloen
 */
public class PropertiesProbe implements Runnable {

    private static Logger log = Logging.getLoggerInstance(PropertiesProbe.class.getName()); 

	Thread kicker = null;
	Properties parent=null;

	public PropertiesProbe(Properties parent) {
		this.parent=parent;
		init();
	}

	public void init() {
		this.start();	
	}


	/**
	 * Starts the admin Thread.
	 */
	public void start() {
		/* Start up the main thread */
		if (kicker == null) {
			kicker = new Thread(this,"cdplayer");
			kicker.setDaemon(true);
			kicker.start();
		}
	}
	
	/**
	 * Stops the admin Thread.
	 */
	public void stop() {
		/* Stop thread */
		kicker.interrupt();
		kicker = null;
	}

	/**
	 */
	public void run () {
		while (kicker!=null) {
			try {
				doWork();
			} catch(Exception e) {
			}	
		}
	}

	/**
	 */
	public void doWork() {  
		while (kicker!=null) {
			try {Thread.sleep(10000);} catch (InterruptedException e){ return ;}
			if (parent.getMachineName().equals("test1")) doExpire();
		}
	}

	private void doExpire() {
		try {
			MultiConnection con=parent.mmb.getConnection();
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("select "+parent.mmb.getDatabase().getAllowedField("parent")+" from "+parent.mmb.baseName+"_"+parent.tableName+" where "+parent.mmb.getDatabase().getAllowedField("key")+"='LASTVISIT' AND value<10536");
			int max=0;
			while (rs.next() && max<1000) {
				int number=rs.getInt(1);
				log.info("Want delete on : " + number);
				deleteProperties(number);
				deleteUser(number);
				max++;
			}
			stmt.close();
			con.close();
		} catch (Exception e) {}
	}


	private void deleteProperties(int id) {
		try {
			MultiConnection con=parent.mmb.getConnection();
			Statement stmt=con.createStatement();
			stmt.executeUpdate("delete from "+parent.mmb.baseName+"_"+parent.tableName+" where parent="+id);
			stmt.close();
			con.close();
		} catch (Exception e) {}
	}


	private void deleteUser(int id) {
		try {
			MultiConnection con=parent.mmb.getConnection();
			Statement stmt=con.createStatement();
			stmt.executeUpdate("delete from "+parent.mmb.baseName+"_users where number="+id);
			stmt.close();
			con.close();
		} catch (Exception e) {}
	}


}
