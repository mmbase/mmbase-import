/* -*- tab-width: 4; -*-

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

import org.mmbase.util.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * admin module, keeps track of all the worker pools
 * and adds/kills workers if needed (depending on
 * there load and info from the config module).
 *
 * @version 27 Mar 1997
 * @author Daniel Ockeloen
 */
public class ModuleProbe implements Runnable {

    private static Logger log = Logging.getLoggerInstance(ModuleProbe.class.getName()); 

	Thread kicker = null;
	String name;
	String input;
	int len;
	Hashtable mods;

	public ModuleProbe(Hashtable mods) {
		this.mods=mods;	
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
			kicker = new Thread(this,"ModuleProbe");
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
		while (kicker!=null) {
			try {
				Thread.sleep(60*1000);
			} catch (InterruptedException e){
			}
			if (mods!=null) {
				for (Enumeration m=mods.keys();m.hasMoreElements();) {
					String key=(String)m.nextElement();
					try {
						Module mod=(Module)mods.get(key);
						mod.maintainance();
					} catch(Exception er) {
						log.error("error on maintainance call : " + key);
					}		
				}
			}
		}
	}
}
