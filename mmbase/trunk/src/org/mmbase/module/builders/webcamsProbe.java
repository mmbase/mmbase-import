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

import javax.servlet.http.*;

import org.mmbase.util.*;
import org.mmbase.module.core.*;

/**
 * admin module, keeps track of all the worker pools
 * and adds/kills workers if needed (depending on
 * there load and info from the config module).
 *
 * @version 23 Dec 1998
 * @author Daniel Ockeloen
 * @author David V van Zeventer
 */

/**
 * class is imcomplete and isn't used yet.
 */
public class webcamsProbe implements Runnable {

	Thread kicker = null;
	webcams parent=null;
	int trackNr=-1;
	String filename=null;
	MMObjectNode node=null;

	public webcamsProbe(webcams parent,MMObjectNode node) {
		this.parent=parent;
		this.node=node;
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
			kicker = new Thread(this,"webcams");
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
		kicker.setPriority(Thread.MIN_PRIORITY+1);  
		String name=node.getStringValue("name");
		String webcamtype=node.getStringValue("webcamtype");
		String state=node.getStringValue("state");
		String info=node.getStringValue("info");
		StringTagger tagger=new StringTagger(info);

		if (state.equals("getdir")) {
			System.out.println("WEBCAMSPROBE getdir"); 

			// set node busy while getting dir
			node.setValue("state","busy");
			node.commit();	
		
			node.setValue("info",parent.getDir(node));	
			// signal we are done
			node.setValue("state","waiting");
			node.commit();	
		} 
	}
}
