/* -*- tab-width: 4; -*-

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders; 

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @version $Id: StatisticsProbe.java,v 1.7 2003-05-08 06:01:20 kees Exp $
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
	 */
	public void doWork() {
		while (kicker!=null) {
			parent.checkDirty();
			try {Thread.sleep(60*1000);} catch (InterruptedException e){}
		}
	}
}
