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


/**
 * MMBaseMultiCastProbe a thread object started to handle all nofity's needed when
 * one is received.
 *
 * @version 27 Mar 1997
 * @author Daniel Ockeloen
 */
public class MMBaseMultiCastProbe implements Runnable {

	Thread kicker = null;
	MMBaseMultiCast parent=null;
	MMObjectBuilder bul=null;
	String id;
	String tb;
	String ctype;
	boolean remote;

	public MMBaseMultiCastProbe(MMBaseMultiCast parent,MMObjectBuilder bul,String id,String tb, String ctype, boolean remote) {
		this.parent=parent;
		this.bul=bul;
		this.id=id;
		this.tb=tb;
		this.ctype=ctype;
		this.remote=remote;
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
	 */
	public void run() {
		if (remote) {
			bul.nodeRemoteChanged(id,tb,ctype);
			parent.checkWaitingNodes(id);	
		} else {
			bul.nodeLocalChanged(id,tb,ctype);
			parent.checkWaitingNodes(id);	
		}
	}
}
