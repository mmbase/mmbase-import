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
 * admin module, keeps track of all the worker pools
 * and adds/kills workers if needed (depending on
 * there load and info from the config module).
 *
 * @version 23 Dec 1998
 * @author Daniel Ockeloen
 * @author David V van Zeventer
 */
public class pagemakerProbe implements Runnable {

	Thread kicker = null;
	pagemakers parent=null;
	int trackNr=-1;
	String filename=null;
	MMObjectNode node=null;

	public pagemakerProbe(pagemakers parent,MMObjectNode node) {
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
			kicker = new Thread(this,"pagemakers");
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
		String pagemakertype=node.getStringValue("pagemakertype");
		String state=node.getStringValue("state");
		String info=node.getStringValue("info");
		StringTagger tagger=new StringTagger(info);

		if (state.equals("calcpage")) {
			System.out.println("pagemaker calcpage");
			// set node busy while getting dir
			node.setValue("state","busy");
			node.commit();	
			parent.calcPage(node.getStringValue("info"));		
			// signal we are done
			node.setValue("state","waiting");
			node.commit();	
		} else if (state.equals("newpage")) {
			System.out.println("pagemaker newpage");
			// set node busy while getting dir
			node.setValue("state","busy");
			node.commit();	
			parent.newPage(node.getStringValue("info"));		
			// signal we are done
			node.setValue("state","waiting");
			node.commit();	
		}
	}
}
