/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders.vwms;

import java.util.*;
import java.io.*;
import java.lang.*;
import org.mmbase.util.*;
import org.mmbase.module.core.*;
import org.mmbase.module.builders.*;

/**
 * @author Daniel Ockeloen
 */

public class Vwm  implements VwmInterface,VwmProbeInterface,Runnable {
	public String classname = getClass().getName();
	public void debug( String msg ) { System.out.println( classname +":"+ msg ); }

	// probe
	VwmProbe probe;

	// The creation node of this VWM
	MMObjectNode wvmnode;

	// Thread
	Thread kicker=null;

	// sleeptime (in seconds) 
	int sleeptime;

	// name of this bot, set by its subclass
	protected String name="Unknown";

	// what clients are using this vwm now
	Vector clients = new Vector();

	// its parent MMObjectBuilder that controlls all the Vwms
	protected Vwms Vwms;

	/*
	* init this bot is called by Vwms
	*/
	public void init(MMObjectNode vwmnode, Vwms Vwms) {
		this.wvmnode=vwmnode;
		this.name=vwmnode.getStringValue("name");
		this.sleeptime=wvmnode.getIntValue("maintime");
		this.Vwms=Vwms;
		probe = new VwmProbe(this);
		this.start();
	}


	/**
	 * Starts the admin Thread.
	 */
	public void start() {
		/* Start up the main thread */
		if (kicker == null) {
			kicker = new Thread(this,"Vwm : "+name);
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
	public void run() {
		kicker.setPriority(Thread.MIN_PRIORITY+1);  
		while (kicker!=null) {
			try {
				probeCall();	
			} catch(Exception e) {
				System.out.println("Vwm : Got a Exception in my probeCall : ");
				e.printStackTrace();	
			}
			try {Thread.sleep(sleeptime*1000);} catch (InterruptedException e){}
		}
	}



	/*
	* add a client to the listen queue of the wvm
	*/
	public boolean addClient(VwmCallBackInterface client) {
		if (clients.contains(client)) {
			System.out.println("Vwm : "+name+" allready has the client : "+client+".");
			return(false);
		} else {
			clients.addElement(client);
			return(true);
		}	
	}

	/*
	* release a client fromthe listen queue of the wvm
	*/
	public boolean releaseClient(VwmCallBackInterface client) {
		if (clients.contains(client)) {
			clients.removeElement(client);
		} else {
			System.out.println("Vwm : "+name+" got a release call from : "+client+" but have no idea who he is.");
		}	
		return(true);
	}

	/*
	* Probe callback
	*/
	public boolean probeCall() {
		System.out.println("Vwm probe call : "+name);
		return(false);
	}

	public boolean putTask(MMObjectNode node) {
//		System.out.println("Vwm : I "+name+" got a puttask");
		probe.putTask(node);
		return(true);
	}

	public String getName() {
		return(name);
	}

	public boolean performTask(MMObjectNode node) {
		System.out.println("Vwm ERROR : performTask not implemented in : "+name);
		node.setValue("status",5);
		node.commit();

		Vwms.sendMail(name,"performTask not implemented","");
		return(false);
	}

	protected boolean claim(MMObjectNode node) {
		boolean rtn=false;

		node.setValue("status",2);
		node.setValue("claimedcpu",Vwms.getMachineName());
		rtn=node.commit();

		return(rtn);
	}

	protected boolean rollback(MMObjectNode node) {
		boolean rtn=false;

		node.setValue("status",1);
		rtn=node.commit();

		return(rtn);
	}

	protected boolean failed(MMObjectNode node) {
		boolean rtn=false;

		node.setValue("status",5);
		rtn=node.commit();

		return(rtn);
	}

	protected boolean performed(MMObjectNode node) {
		boolean rtn=false;

		node.setValue("status",3);
		rtn=node.commit();

		return(rtn);
	}

	protected Hashtable parseProperties(String props) {
		java.util.Properties p;
		StringBufferInputStream b=new  StringBufferInputStream(props);
		p=new java.util.Properties();
		try {p.load(b);} catch(IOException e) {}
		return(p);
	}

	public MMObjectNode getVwmNode() {
		return(wvmnode);
	}

    public boolean nodeRemoteChanged(String number,String builder,String ctype)
	{
		return(false);
	}

    public boolean nodeLocalChanged(String number,String builder,String ctype)
	{
		return(false);
	}
}
