/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.remote;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

/**
 * Builds a MultiCast Thread to receive  and send 
 * changes from other MMBase Servers.
 *
 * @version 12 May 1999
 * @author Daniel Ockeloen
 * @author Rico Jansen
 */
public class MMRemoteMultiCast implements Runnable,MMProtocolDriver {

    private String  classname   = getClass().getName();
    private boolean debug       = false;

    private void debug( String msg ) {
        if( debug )
            System.out.println( classname +":"+ msg );
    }

	Thread kicker = null;
	int follownr=1;
	private Vector waitingNodes = new Vector();
	private Queue nodesTosend=new Queue(64);
	private Queue nodesTospawn=new Queue(64);
	public int incount=0;
	public int outcount=0;
	public int spawncount=0;
	private MMRemoteMultiCastChangesSender mcs;
	private MMRemoteMultiCastChangesReceiver mcr;

	public static String multicastaddress="ALL-SYSTEMS.MCAST.NET";
	public static int dpsize=64*1024;
	public static int mport=4242;
	private String machineName="unknown";

	private Hashtable listeners=new Hashtable();

	public MMRemoteMultiCast(String machineName,String host, int port) {
		this.mport=port;
		this.multicastaddress=host;	
		this.machineName=machineName;
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
			kicker = new Thread(this,"MMRemoteMultiCast");
			kicker.start();
			mcs=new MMRemoteMultiCastChangesSender(this,nodesTosend);
			mcr=new MMRemoteMultiCastChangesReceiver(this,nodesTospawn);
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
		try {
			kicker.setPriority(Thread.NORM_PRIORITY+1);  
			doWork();
		} catch(Exception e) {
			System.out.println("MMBaseMultiCast -> ");
			e.printStackTrace();
		}
	}

	/**
	 * admin probe, try's to make a call to all the maintainance calls.
	 */
	public void doWork() {
		InetAddress ia=null;
		String s;
		StringTokenizer tok;

		System.out.println("MMRemoteMultiCast started");
		try {
			ia = InetAddress.getByName(multicastaddress);
		} catch(Exception e) {
			System.out.println("MMRemoteMultiCast -> ");
			e.printStackTrace();
		}
		try {
			MulticastSocket ms = new MulticastSocket(mport);
			ms.joinGroup(ia);
			while (true) {
				try {
					DatagramPacket dp = new DatagramPacket(new byte[dpsize], dpsize);
					ms.receive(dp);
					s=new String(dp.getData(),0,0,dp.getLength());
					nodesTospawn.append(s);
				} catch (Exception f) {
					System.out.println("MMBaseMultiCast -> ");
					f.printStackTrace();
				}
			}
		} catch(Exception e) {
			System.out.println("MMBaseMultiCast -> ");
			e.printStackTrace();
		}
	}

	public boolean handleMsg(String machine,String vnr,String id,String tb,String ctype) {
		// System.out.println("M='"+machine+"' vnr='"+vnr+"' id='"+id+"' tb='"+tb+"' ctype='"+ctype+"'");

		String mapper=tb+"/"+id;
		RemoteBuilder serv=(RemoteBuilder)listeners.get(mapper);
		if (serv==null) return(true);

		if (machine.equals(machineName)) {
			try { 
				if (!ctype.equals("g") && !ctype.equals("s")) {
					new MMRemoteMultiCastProbe(this,serv,id,tb,ctype,false);
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		
		} else {
			try { 
				new MMRemoteMultiCastProbe(this,serv,id,tb,ctype,true);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return(true);
	}


	public boolean handleXML(String machine,String vnr,String id,String tb,String ctype,String xml) {
		// System.out.println("M='"+machine+"' vnr='"+vnr+"' id='"+id+"' tb='"+tb+"' ctype='"+ctype+"' "+xml);
		if (machine.equals(machineName)) {
			// do nothing its for myself !!
		} else {
			String mapper=tb+"/"+id;
			RemoteBuilder serv=(RemoteBuilder)listeners.get(mapper);
			if (serv==null) return(true);
			serv.gotXMLValues(xml);
		}
		return(true);
	}

	public boolean changedNode(int nodenr,String tableName,String type) {
		String chars=machineName+","+(follownr++)+","+nodenr+","+tableName+","+type;
		nodesTosend.append(chars);
		return(true);
	}


	public boolean commitNode(String nodenr,String tableName,String xml) {
		String chars=machineName+","+(follownr++)+","+nodenr+","+tableName+",s,"+xml;
		nodesTosend.append(chars);
		return(true);
	}


	public boolean getNode(String nodenr,String tableName) {
		String chars=machineName+","+(follownr++)+","+nodenr+","+tableName+",g";
		// extra sleep to allow the database to save its indexes
		nodesTosend.append(chars);
		return(true);
	}
	
	public boolean addListener(String buildername,String nodenr,RemoteBuilder serv) {
		listeners.put(buildername+"/"+nodenr,serv);	
		return(true);
	}

	public int getLocalPort() {
		return(mport);
	}

	public String getLocalHost() {
		return(multicastaddress);
	}
	
	public String getProtocol() {
		return("multicast");
	}

	public String toString() {
		return "";
	}
	
}
