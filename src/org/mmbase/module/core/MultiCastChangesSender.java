/* -*- tab-width: 4; -*-

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

import org.mmbase.util.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * MultiCastChangesSender is a thread object sending the nodes found in the
 * sending queue over the multicast 'channel'
 *
 * @version 12-May-1999
 * @author Rico Jansen
 */
public class MultiCastChangesSender implements Runnable {

    private static Logger log = Logging.getLoggerInstance(MultiCastChangesSender.class.getName()); 

	Thread kicker = null;
	MMBaseMultiCast parent=null;
	Queue nodesTosend;
	InetAddress ia;
	MulticastSocket ms;
	int mport;
	int dpsize;

	public MultiCastChangesSender(MMBaseMultiCast parent,Queue nodesTosend) {
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
			kicker = new Thread(this,"MulticastSender");
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
		kicker.suspend();
		kicker.stop();
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
                log.error(Logging.stackTrace(e));
			}
			doWork();
		} catch (Exception e) {
            log.error(Logging.stackTrace(e));
		}
	}

	private void doWork() {
		byte[] data;
		DatagramPacket dp;
		String chars;
		while(kicker!=null) {
			chars=(String)nodesTosend.get();
			parent.incount++;
			data = new byte[chars.length()];
			chars.getBytes(0,chars.length(), data, 0);		
			dp = new DatagramPacket(data, data.length, ia,mport);
			try {
				ms.send(dp, (byte)1);
			} catch (IOException e) {
				log.error("can't send message");
                log.error(Logging.stackTrace(e));
			}
			parent.outcount++;
		}
	}
}
