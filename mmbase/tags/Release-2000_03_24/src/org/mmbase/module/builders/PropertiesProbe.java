/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module.builders;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;
import java.sql.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;

/**
 * admin module, keeps track of all the worker pools
 * and adds/kills workers if needed (depending on
 * there load and info from the config module).
 *
 * @version 28 Nov 1998
 * @author Daniel Ockeloen
 */
public class PropertiesProbe implements Runnable {

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
			kicker.start();
		}
	}
	
	/**
	 * Stops the admin Thread.
	 */
	public void stop() {
		/* Stop thread */
		kicker.setPriority(Thread.MIN_PRIORITY);  
		kicker.suspend();
		kicker.stop();
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
		kicker.setPriority(Thread.MIN_PRIORITY+1);  
		while (kicker!=null) {
			try {Thread.sleep(10000);} catch (InterruptedException e){}
			if (parent.getMachineName().equals("test1")) doExpire();
		}
	}

	private void doExpire() {
		try {
			MultiConnection con=parent.mmb.getConnection();
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("select parent from "+parent.mmb.baseName+"_"+parent.tableName+" where key='LASTVISIT' AND value<10536");
			int max=0;
			while (rs.next() && max<1000) {
				int number=rs.getInt(1);
				System.out.println("Properties -> Want delete on : "+number);
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
