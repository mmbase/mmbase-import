/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.database;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

import org.mmbase.util.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * JDBCProbe checks all JDBC connection every X seconds to find and
 * remove bad connections works using a callback into JDBC.
 *
 * @version 27 Mar 1997
 * @author Daniel Ockeloen
 */
public class JDBCProbe implements Runnable {
    private static Logger log = Logging.getLoggerInstance(JDBCProbe.class.getName());

	Thread kicker = null;
	JDBC parent=null;
	String name;
	String input;
	int len;

	public JDBCProbe(JDBC parent) {
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
			kicker = new Thread(this,"JDBCProbe");
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
	 * admin probe, try's to make a call to all the maintainance calls.
	 */
	public void run () {
		log.info("JDBC probe starting");
		while (kicker!=null) {
			try{
				Thread.sleep(30000);
			} catch(InterruptedException e) {
			}
			try {
				parent.checkTime();
			} catch (Exception e) {
			}
		}
	}
}
