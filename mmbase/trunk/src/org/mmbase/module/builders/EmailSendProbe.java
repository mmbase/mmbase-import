/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.*;

import org.mmbase.module.core.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;

/**
 * Email send probe, keeps a queue of queued messages
 * and uses a wait/nofity on its internal thread as 
 * a way to block until the next event or notify of
 * a possible queue change.
 *
 * @version $Id: EmailSendProbe.java,v 1.8 2003-05-07 21:06:47 kees Exp $
 * @author Daniel Ockeloen
 */
public class EmailSendProbe implements Runnable {

    private static Logger log = Logging.getLoggerInstance(EmailSendProbe.class.getName());
	// The internal thread it wait/notify's on
	Thread kicker = null;
	
	// parent ref. we need to be able to call back on a action (perform call)
	Email parent=null;

	// Sorted list of the memory queued email objects
	public SortedVector tasks= new SortedVector(new MMObjectCompare("mailtime"));

	// we also need a probe to fill the memory queue every x time
	EmailQueueProbe queueprobe=null;

	// the (default) size of the memory queue
	public int internalqueuesize=500;

	// the maximum age of queued messages
	public int maxtasktime=60*60;

	// number of queued messages in the database
	// only valid after first run
	public int dbqueued=-1;

	// time interval we check the database for 
	// both putting messages in queue mode and
	// filling the memory queue
	public int queueprobetime=5*60;

	// Active Node, as in the first node in the queue
	// if we are not in running mode we are probably
	// blocked on this nodes mailtime
	MMObjectNode anode=null;

	/**
	* construct the probe 
	*/
	public EmailSendProbe(Email parent) {
		this.parent=parent;
		init();
		// start a second probe that calls us back 
		// every x seconds to update the queue
		queueprobe=new EmailQueueProbe(this,queueprobetime);
	}


	/**
	* init/start the probe
	*/
	public void init() {
		this.start();	
	}


	/**
	 * Starts the thread
	 */
	public void start() {
		// Start up the main thread 
		if (kicker == null) {
			kicker = new Thread(this,"emailsendprobe");
			kicker.setDaemon(true);
			kicker.start();
		}
	}
	
	/**
	 * Stops the admin Thread.
	 */
	public void stop() {
		// stop the thread
		kicker.interrupt();
		kicker = null;
	}

	/**
	 * blocks on the first task in the queue
	 * 
	 * this is the core of the queue, the idea is that
	 * we block on the first node that needs to be mailed
	 * do a wait on it until its ready. If no node is found
	 * we block for a hour. if we reach a time we perform
	 * a call on our parent and remove ourselfs from the queue
	 */
	public synchronized void run() {
		// this is a lowlevel thingie  
		while (kicker!=null) {
				// try to select a new first node
				if (tasks.size()>0) {
					anode=(MMObjectNode)tasks.elementAt(0);
				} else {
					anode=null;
				}

				// we might be interupted to we need to catch
				try {
					// nothing in the queue wait alot then
					if (anode==null) {
						// so no task in the future wait a long time then
						wait(3600*1000);
					} else {
						// get the current time
						int ttime=(int)((System.currentTimeMillis()/1000)); 
						// het the wanted time
						int ntime=anode.getIntValue("mailtime");
						// are we ready or do we need to wait some more ?
						if (ttime>=ntime) {
							// time has come handle this task now !
							try {
								// remove node from the queue
								tasks.removeElement(anode);
								// call our parent
								parent.performTask(anode);
				
							} catch (Exception e) {
								log.error("emailsendprobe : performTask failed"+anode);
								tasks.removeElement(anode);
								// oke set node on error
								anode.setValue("mailstatus",Email.STATE_FAILED);
								anode.commit();
							}
				
							// if its empty check for queued
							// in the database
							if (tasks.size()==0) {
								checkQueue();
							}
						} else {
							// figure out how long we need to wait
							int sleeptime=ntime-ttime;
							// check for 0 to not fall in the wait(0) trap (see specs)
							if (sleeptime!=0) {
								// wait for that amount of time
								wait(sleeptime*1000);
							}
						}
					}
				} catch (InterruptedException e){
					return;
				}
		}
	}

	public synchronized boolean putTask(MMObjectNode node) {
		if (node.getIntValue("mailstatus")!=Email.STATE_QUEUED) {
			node.setValue("mailstatus",Email.STATE_QUEUED);	
			node.commit();
		}
		
		if (node.getIntValue("mailstatus")==Email.STATE_SPAMGARDE) {
			return(true);
		}

		if (!containsTask(node)) {
			if (tasks.size()<internalqueuesize) {
				tasks.addSorted(node);
			} else {
			}
		} else {
			replaceTask(node);
		}
		// is the active node
		if (tasks.size()==0 || node==tasks.elementAt(0)) {
			notify();
		}
		return(true);
	}

	public boolean containsTask(MMObjectNode node) {
		int number=node.getIntValue("number");
		Enumeration e=tasks.elements();
		while (e.hasMoreElements()) {
			MMObjectNode node2=(MMObjectNode)e.nextElement();
			if (node2.getIntValue("number")==number) {
				return(true);
			}
		}
		return(false);
	}


	public boolean replaceTask(MMObjectNode node) {
		int number=node.getIntValue("number");
		Enumeration e=tasks.elements();
		if (e.hasMoreElements()) {
			MMObjectNode node2=(MMObjectNode)e.nextElement();
			if (node2.getIntValue("number")==number) {
				tasks.removeElement(node2);
				tasks.addSorted(node);
				return(true);
			}
		}
		return(false);
	}

	public synchronized void checkQueue() {
		int ttime=(int)((System.currentTimeMillis()/1000)); 

		// get the events for the next 5 min so we can place them
		// in the queue if needed
		Enumeration e=parent.search("mailtime=S"+(ttime+maxtasktime),"mailtime",true);
		dbqueued=0;
		while (e.hasMoreElements()) {
			MMObjectNode qnode=(MMObjectNode)e.nextElement();
			if (!containsTask(qnode)) {
				putTask(qnode);
			}
			dbqueued++;
		}
	}
}
