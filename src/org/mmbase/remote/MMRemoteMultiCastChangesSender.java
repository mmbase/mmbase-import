package org.mmbase.remote;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

/**
 * MultiCastChangesSender is a thread object sending the nodes found in the
 * sending queue over the multicast 'channel'
 *
 * @version 12-May-1999
 * @author Rico Jansen
 */
public class MMRemoteMultiCastChangesSender implements Runnable {

    private String  classname   = getClass().getName();
    private boolean debug       = RemoteBuilder.debug;
    private void debug( String msg ) { System.out.println( classname +":"+ msg ); }

	Thread kicker = null;
	MMRemoteMultiCast parent=null;
	Queue nodesTosend;
	InetAddress ia;
	MulticastSocket ms;
	int mport;
	int dpsize;

	public MMRemoteMultiCastChangesSender(MMRemoteMultiCast parent,Queue nodesTosend) {
		this.parent=parent;
		this.nodesTosend=nodesTosend;
		init();
	}

	public void init() {
		this.start();	
	}

	public void start() {
		/* Start up the main thread */
		if (kicker == null) {
			kicker = new Thread(this,"MMRemoteMulticastSender");
			kicker.start();
		}
	}
	
	public void stop() {
		/* Stop thread */
		try {
			ms.leaveGroup(ia);
			ms.close();		
		} catch (Exception e) {
		}
		kicker.setPriority(Thread.MIN_PRIORITY);  
		kicker = null;
	}

	/**
	 * admin probe, try's to make a call to all the maintainance calls.
	 */
	public void run() {
		try {
			try {
				mport=parent.mport;
				dpsize=parent.dpsize;
				ia = InetAddress.getByName(parent.multicastaddress);
				ms = new MulticastSocket();
				ms.joinGroup(ia);
			} catch(Exception e) {
				debug("run(): ERROR: " + e.toString());
				e.printStackTrace();
			}
			doWork();
		} catch (Exception e) {
			debug("run(): ERROR: " + e.toString());
			e.printStackTrace();
		}
	}

	private void doWork() {
		byte[] data;
		DatagramPacket dp;
		String chars;
		try {
		while(kicker!=null) {
			chars=(String)nodesTosend.get();
			debug("run():sending("+chars+")");
			parent.incount++;
			data = new byte[chars.length()];
			chars.getBytes(0,chars.length(), data, 0);		
			dp = new DatagramPacket(data, data.length, ia,mport);
			try {
				ms.send(dp, (byte)1);
			} catch (IOException e) {
				debug("doWork(): ERROR: Can't send message!" + e.toString());
				e.printStackTrace();
			}
		}
		} catch(Exception e) {
			debug("doWork(): ERROR: " + e.toString());
			e.printStackTrace();
		}
	}
}
