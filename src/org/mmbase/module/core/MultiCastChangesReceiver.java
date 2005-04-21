/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module.core;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

import org.mmbase.util.*;

/**
 * MultiCastChangesReceiver is a thread object that reads the receive queue
 * and spawns them to call the objects (listeners) who need to know.
 *
 * @version 12-May-1999
 * @author Rico Jansen
 */
public class MultiCastChangesReceiver implements Runnable {

	Thread kicker = null;
	MMBaseMultiCast parent=null;
	Queue nodesToSpawn;
	InetAddress ia;
	MulticastSocket ms;
	int mport;
	int dpsize;

	public MultiCastChangesReceiver(MMBaseMultiCast parent,Queue nodesToSpawn) {
		this.parent=parent;
		this.nodesToSpawn=nodesToSpawn;
		init();
	}

	public void init() {
		this.start();	
	}

	public void start() {
		/* Start up the main thread */
		if (kicker == null) {
			kicker = new Thread(this,"MulticastReceiver");
			kicker.start();
		}
	}
	
	public void stop() {
		kicker.setPriority(Thread.MIN_PRIORITY);  
		kicker.suspend();
		kicker.stop();
		kicker = null;
	}

	/**
	 * admin probe, try's to make a call to all the maintainance calls.
	 */
	public void run() {
		while(kicker!=null) {
			try {
				doWork();
			} catch (Exception e) {
				System.out.println("MultiCastChangesReceiver -> ");
				e.printStackTrace();
			}
		}
	}

	private void doWork() {
		String chars;
		String machine,vnr,id,tb,ctype;
		StringTokenizer tok;
		while(kicker!=null) {
			chars=(String)nodesToSpawn.get();
			parent.spawncount++;
			tok=new StringTokenizer(chars,",");
			if (tok.hasMoreTokens()) {
				machine=tok.nextToken();	
				if (tok.hasMoreTokens()) {
					vnr=tok.nextToken();	
					if (tok.hasMoreTokens()) {
						id=tok.nextToken();	
						if (tok.hasMoreTokens()) {
							tb=tok.nextToken();	
							if (tok.hasMoreTokens()) {
								ctype=tok.nextToken();	
								if (!ctype.equals("s")) {
									parent.handleMsg(machine,vnr,id,tb,ctype);
								} else {
									String xml=tok.nextToken("");	
									parent.commitXML(machine,vnr,id,tb,ctype,xml);
								}
							}
						}
					}
				}
			}
		}
	}
}
