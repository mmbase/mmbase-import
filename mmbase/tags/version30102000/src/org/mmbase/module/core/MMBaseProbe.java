/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;


/**
 * MMBaseProbe is a thread object that gets activated by MMbase
 * maintainance call every X seconds and takes this signal to call all the 
 * builders probeCalls this is done my a callback in MMbase.
 *
 * @author Daniel Ockeloen
 * @version $Id: MMBaseProbe.java,v 1.4 2000-10-05 11:35:43 vpro Exp $
 */
public class MMBaseProbe implements Runnable {

	Thread kicker = null;
	MMBase parent=null;
	String name;
	String input;
	int len;

	public MMBaseProbe(MMBase parent) {
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
			kicker = new Thread(this,"MMBaseProbe");
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
	public void run() {
		parent.doProbeRun();
		try {
			Thread.sleep(10*60*1000);
		} catch (InterruptedException e) {}
		parent.probe=null;
	}
}
