/* -*- tab-width: 4; -*-

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders; 

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @version 27 Mar 1997
 * @author Daniel Ockeloen
 */
public class StatisticsProbe implements Runnable {

    private static Logger log = Logging.getLoggerInstance(StatisticsProbe.class.getName()); 

	Thread kicker = null;
	Statistics parent=null;

	public StatisticsProbe(Statistics parent) {
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
			kicker = new Thread(this,"StatisticsProbe");
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
		try {
			doWork();
		} catch (Exception e) {
			log.error("Problem in Statistics thread");
			log.error(Logging.stackTrace(e));
		}
	}

	/**
	 */
	public void doWork() {
		while (kicker!=null) {
			parent.checkDirty();
			try {Thread.sleep(60*1000);} catch (InterruptedException e){}
		}
		// parent.probe=null;
	}
}
