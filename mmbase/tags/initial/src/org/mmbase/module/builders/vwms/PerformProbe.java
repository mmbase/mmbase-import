/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module.builders.vwms;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;
import org.mmbase.module.builders.*;

/**
 * @author Rico Jansen
 */
public class PerformProbe implements Runnable {
private VwmProbeInterface vwm;
private MMObjectNode node;
private int status;

	Thread kicker = null;

	public PerformProbe(VwmProbeInterface vwm,MMObjectNode node) {
		this.vwm=vwm;
		this.node=node;
		this.status=1;
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
			kicker = new Thread(this,"Performprobe "+node.getIntValue("number"));
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
			status=2;
			vwm.performTask(node);
			status=3;
		} catch (Exception e) {
			System.out.println("PerformProbe : performTask failed"+e);
			e.printStackTrace();
			status=5;
		}
	}

	public int getStatus() {
		return(status);
	}
}
