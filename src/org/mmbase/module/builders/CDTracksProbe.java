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
 * @version 27 Mar 1997
 * @author Daniel Ockeloen
 */
public class CDTracksProbe implements Runnable {

	Thread kicker = null;
	CDTracks parent=null;
	int trackNr=-1;
	String filename=null;
	MMObjectNode caller=null;

	public CDTracksProbe(CDTracks parent,int trackNr,String filename,MMObjectNode caller) {
		this.parent=parent;
		this.trackNr=trackNr;
		this.filename=filename;
		this.caller=caller;
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
			kicker = new Thread(this,"cdtrackprobe");
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
		try {
		if (parent!=null) {
			caller.setValue("status",2);
			caller.commit();	
			parent.doWork(trackNr,filename);
			parent.wavAvailable(caller.getIntValue("id"));
			caller.setValue("status",3);
			caller.commit();	
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
