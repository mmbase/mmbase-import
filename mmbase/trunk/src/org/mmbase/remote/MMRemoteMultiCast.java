/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

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
    private boolean debug       = RemoteBuilder.debug;
    private void debug( String msg ) { System.out.println( classname +":"+ msg ); }

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
		if( debug ) debug("MMRemoteMultiCast("+machineName+","+host+","+port+")");

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
			debug("run(): ERROR: ");
			e.printStackTrace();
		}
	}

	/**
	 * admin probe, try's to make a call to all the maintainance calls.
	 */
	public void doWork() {
		InetAddress ia=null;
		String s = null;
		StringTokenizer tok;

		debug("doWork(): started...");
		try {
			ia = InetAddress.getByName(multicastaddress);
		} catch(Exception e) {
			debug("doWork(): ERROR: On address("+multicastaddress+"): ");
			e.printStackTrace();
		}
		try {
			MulticastSocket ms = new MulticastSocket(mport);
			ms.joinGroup(ia);
			DatagramPacket dp;

			while (true) {
				try {
					dp = new DatagramPacket(new byte[dpsize], dpsize);
					ms.receive(dp);
					s=new String(dp.getData(),0,0,dp.getLength());
					nodesTospawn.append(s);
				} catch (Exception f) {
					debug("doWork(): ERROR: On address("+multicastaddress+","+mport+"): while receiving("+s+"): ");
					f.printStackTrace();
				}
			}

		} catch(Exception e) {
			debug("doWork(): ERROR: On address("+multicastaddress+","+mport+"): while receiving("+s+"): ");
			e.printStackTrace();
		}
	}

	public boolean handleMsg(String machine,String vnr,String id,String tb,String ctype) {
		if( debug ) debug("handleMessage("+machine+","+vnr+","+id+","+tb+","+ctype+")");

		String mapper=tb+"/"+id;
		RemoteBuilder serv=(RemoteBuilder)listeners.get(mapper);
		if (serv==null)
		{
			debug("handleMessage("+machine+","+vnr+","+id+","+tb+","+ctype+"): ERROR: could not get RemoteBuilder("+mapper+")!");
		 	return(true);
		}

		if (machine.equals(machineName)) {
			try { 
				if (!ctype.equals("g") && !ctype.equals("s")) {
					new MMRemoteMultiCastProbe(this,serv,id,tb,ctype,false);
				}
				else
					if( debug ) debug("handleMessage("+machine+","+vnr+","+id+","+tb+","+ctype+"): Warning: Unknown type '"+ctype+"' (not g/s)");

			} catch(Exception e) {	
				debug("handleMessage("+machine+","+vnr+","+id+","+tb+","+ctype+"): ERROR: while received from local: ");
				e.printStackTrace();
			}
		
		} else {
			try { 
				new MMRemoteMultiCastProbe(this,serv,id,tb,ctype,true);
			} catch(Exception e) {
				debug("handleMessage("+machine+","+vnr+","+id+","+tb+","+ctype+"): ERROR: while received from remote: ");
				e.printStackTrace();
			}
		}
		return(true);
	}


	public boolean handleXML(String machine,String vnr,String id,String tb,String ctype,String xml) {
		if( debug ) debug("handleMessage("+machine+","+vnr+","+id+","+tb+","+ctype+","+xml+")");
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
		if( debug ) debug("changedNode("+nodenr+","+tableName+","+type+")");

		String chars=machineName+","+(follownr++)+","+nodenr+","+tableName+","+type;
		nodesTosend.append(chars);
		return(true);
	}


	public boolean commitNode(String nodenr,String tableName,String xml) {
		if( debug ) debug("commitNode("+nodenr+","+tableName+","+xml+")");

		String chars=machineName+","+(follownr++)+","+nodenr+","+tableName+",s,"+xml;
		nodesTosend.append(chars);
		return(true);
	}


	public boolean getNode(String nodenr,String tableName) {
		if( debug ) debug("geNode("+nodenr+","+tableName+")");

		String chars=machineName+","+(follownr++)+","+nodenr+","+tableName+",g";
		// extra sleep to allow the database to save its indexes
		nodesTosend.append(chars);
		return(true);
	}
	
	public boolean addListener(String buildername,String nodenr,RemoteBuilder serv) {
		if( debug ) debug("addListener("+buildername+","+nodenr+","+serv+")");

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
