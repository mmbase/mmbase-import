/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/*
	$Id: DayMarkersProbe.java,v 1.2 2000-05-30 08:32:46 wwwtech Exp $

	$Log: not supported by cvs2svn $
	Revision 1.1  2000/05/30 07:37:44  wwwtech
	Rico: added thread to keep daymarkers up to date for servers running longer then 1 day
	

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
 * @author Rico Jansen
 * @version $Id: DayMarkersProbe.java,v 1.2 2000-05-30 08:32:46 wwwtech Exp $
 */
public class DayMarkersProbe implements Runnable {

	private String classname = getClass().getName();
	private boolean debug = false;
	private void debug( String msg ) { System.out.println( classname+":"+msg ); }

	Thread kicker = null;
	DayMarkers parent=null;

	public DayMarkersProbe(DayMarkers parent) {
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
			kicker = new Thread(this,"daymarkersprobe");
			kicker.start();
		}
	}
	
	/**
	 * Stops the main Thread.
	 */
	public void stop() {
		/* Stop thread */
		kicker.setPriority(Thread.MIN_PRIORITY);  
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
				debug("run(): ERROR: Exception in mmservers thread!");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Main work loop
	 */
	public void doWork() {
		try {Thread.sleep(10*(60*1000));} catch (InterruptedException e){}
		while (kicker!=null) {
			parent.probeCall();
			try {
				Thread.sleep(60*(60*1000));
			} catch (InterruptedException e){
				System.out.println("DayMarkersProbe Interrupted");
			}
		}
	}


}
