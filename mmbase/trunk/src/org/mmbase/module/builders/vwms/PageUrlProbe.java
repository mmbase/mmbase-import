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
public class PageUrlProbe implements Runnable {
private Page parent;

	Thread kicker = null;

	public PageUrlProbe(Page parent) {
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
			kicker = new Thread(this,"PageUrlProbe");
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

	public void run () {
		while (kicker!=null) {
		try {
			doWork();
		} catch(Exception e) {
			System.out.println("Exception in PageUrlProbe");
		}
		}
	}

	public synchronized void doWork() {
		while  (kicker!=null) {
			try {
				wait(60*1000);
			} catch(Exception e) {
				e.printStackTrace();
			}
			parent.checkDirtyUrls();
		}
	}

	public synchronized void newUrl() {
		System.out.println("PageProbe -> Got notify");
		notify();
	}
}
