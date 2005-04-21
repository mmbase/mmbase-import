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

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;

/**
 * @author Daniel Ockeloen
 * @version 14-Jan-1998
 */
public class MMServersProbe implements Runnable {

	Thread kicker = null;
	MMServers parent=null;

	public MMServersProbe(MMServers parent) {
		this.parent=parent;
		init();
	}

	public void init() {
		this.start();	
	}


	/**
	 * Starts the main Thread.
	 */
	public void start() {
		/* Start up the main thread */
		if (kicker == null) {
			kicker = new Thread(this,"mmserversprobe");
			kicker.start();
		}
	}
	
	/**
	 * Stops the main Thread.
	 */
	public void stop() {
		/* Stop thread */
		kicker.setPriority(Thread.MIN_PRIORITY);  
		kicker.suspend();
		kicker.stop();
		kicker = null;
	}

	/**
	 * Main loop, exception protected
	 */
	public void run () {
		kicker.setPriority(Thread.MIN_PRIORITY+1);  
		while (kicker!=null) {
			try {
				doWork();
			} catch(Exception e) {
				System.out.println("MMServers -> Exception in mmservers thread");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Main work loop
	 */
	public void doWork() {
		kicker.setPriority(Thread.MIN_PRIORITY+1);  
		MMObjectNode node,node2;
		boolean needbreak=false;
		int id;

		try {Thread.sleep(30*1000);} catch (InterruptedException e){}
		while (kicker!=null) {
			parent.probeCall();
			try {Thread.sleep(60*1000);} catch (InterruptedException e){}
		}
	}


}
