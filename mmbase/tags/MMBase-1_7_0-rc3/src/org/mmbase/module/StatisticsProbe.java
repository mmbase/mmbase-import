/* -*- tab-width: 4; -*-

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module;


import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

//import vpro.james.coremodules.security.PropertyUtil;

/**
 * admin module, keeps track of all the worker pools
 * and adds/kills workers if needed (depending on
 * there load and info from the config module).
 *
 * @version $Id: StatisticsProbe.java,v 1.7 2003-05-08 06:09:32 kees Exp $
 * @author Daniel Ockeloen
 */
public class StatisticsProbe implements Runnable {

    private static Logger log = Logging.getLoggerInstance(StatisticsProbe.class.getName()); 

	Thread kicker = null;
	Stats parent=null;

	public StatisticsProbe(Stats parent) {
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
	 * admin probe, try's to make a call to all the maintainance calls.
	 */
	public void doWork() {
		while (kicker!=null) {
			parent.checkDirty();
			try {Thread.sleep(60*1000);} catch (InterruptedException e){}
		}
		// parent.probe=null;
	}
}
