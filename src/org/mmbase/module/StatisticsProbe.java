package org.mmbase.module;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

//import vpro.james.coremodules.security.PropertyUtil;

/**
 * admin module, keeps track of all the worker pools
 * and adds/kills workers if needed (depending on
 * there load and info from the config module).
 *
 * @version 27 Mar 1997
 * @author Daniel Ockeloen
 */
public class StatisticsProbe implements Runnable {

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
			System.out.println("Problem in Statistics thread");
			e.printStackTrace();
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
