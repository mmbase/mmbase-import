package org.mmbase.remote;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

/**
 *
 * @version 27 Mar 1997
 * @author Daniel Ockeloen
 */
public class MMRemoteProbe implements Runnable {

	private String 		classname = getClass().getName();
	private boolean 	debug 	  = false;
	private void		debug( String msg ) { System.out.println( classname +":"+ msg ); }

	Thread kicker = null;
	MMProtocolDriver con=null;
	String servicenr;
	Vector runningServices;

	public MMRemoteProbe(Vector runningServices,MMProtocolDriver con,String servicenr) {
		if( debug ) debug("MMRemoteProbe(): "+runningServices+","+con+","+servicenr+")"); 

		this.con=con;
		this.servicenr=servicenr;
		this.runningServices=runningServices;
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
			kicker = new Thread(this,"MMRemoteProbe");
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


	public void run() {
		while (kicker!=null) {
			try {
				kicker.setPriority(Thread.NORM_PRIORITY+1);  
				doWork();
			} catch(Exception e) {
				debug("run(): ERROR: while doWork(): ");
				e.printStackTrace();
			}
		}
	}

	/**
	 */
	public void doWork() {
		try {
			if (debug) debug("doWork()");
			con.commitNode(servicenr,"mmservers",toXML());
			Thread.sleep(60*1000);
		} catch(Exception e) {
			debug("doWork(): ERROR: while commitNode("+servicenr+",mmservers,toXML()) : ");
			e.printStackTrace();
		}
		callMaintainances();
	}
	
	public String toXML() {
		String host="";
		try {
			host=InetAddress.getLocalHost().getHostName();
		} catch(Exception e) { debug("toXML(): ERROR: Could not get localhost address!"); }
		String body="<?xml version=\"1.0\"?>\n";
		body+="<!DOCTYPE mmnode.mmservers SYSTEM \"http://openbox.vpro.nl/mmnode/mmservers.dtd\">\n";
		body+="<mmservers>\n";
		body+="<number>"+servicenr+"</number>\n";
		body+="<state>1</state>\n";
		body+="<atime>"+(int)(System.currentTimeMillis()/1000)+"</atime>\n";
		body+="<host>"+con.getProtocol()+"://"+con.getLocalHost()+":"+con.getLocalPort()+"</host>\n";
		body+="</mmservers>\n";
		return(body);
	}

	void callMaintainances() {
		Enumeration f=runningServices.elements();
		for (;f.hasMoreElements();) {
			RemoteBuilder serv=(RemoteBuilder)f.nextElement();
			serv.maintainance();
		}
	}
}
