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
 * MMBaseMultiCastProbe a thread object started to handle all nofity's needed when
 * one is received.
 *
 * @version $Revision: 1.4 $ $Date: 2001-05-04 13:48:52 $
 * @author Daniel Ockeloen
 */
public class MMBaseMultiCastProbe implements Runnable {

	Thread kicker = null;
	MMBaseMultiCast parent=null;
	MMObjectBuilder bul=null;
	String machine; 
	String id;
	String tb;
	String ctype;
	boolean remote;

	public MMBaseMultiCastProbe(MMBaseMultiCast parent,MMObjectBuilder bul,String machine,
	        String id,String tb,String ctype,boolean remote) {
		this.parent=parent;
		this.bul=bul;
		this.machine=machine;
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
			bul.nodeRemoteChanged(machine,id,tb,ctype);
			parent.checkWaitingNodes(id);	
		} else {
			bul.nodeLocalChanged(machine,id,tb,ctype);
			parent.checkWaitingNodes(id);	
		}
	}
}
