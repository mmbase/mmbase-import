/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders.vwms;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

import org.mmbase.util.*;

/**
 * @author Rico Jansen
 */
public class JudasURLpusher implements Runnable {
	private String classname = getClass().getName();
	private void debug( String msg ) { System.out.println( classname +":"+ msg ); }
	private boolean debug=false;

	Thread kicker = null;
	int sleepTime=5000;
	Judas parent;

	Hashtable priurls=new Hashtable();
	SortedVector prilist=new SortedVector();

	public JudasURLpusher(Judas parent) {
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
			kicker = new Thread(this,"Judas");
			kicker.start();
		}
	}
	
	/**
	 * Stops the main Thread.
	 */
	public void stop() {
		/* Stop thread */
		kicker = null;
	}

	/**
	 * Main loop, exception protected
	 */
	public void run () {
		while (kicker!=null) {
			try {
				doWork();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Main work loop
	 */
	public void doWork() {
		PriorityURL priurl;

		debug("Active");
		while (kicker!=null) {
			try { Thread.sleep(sleepTime); } catch (InterruptedException e) {}
			synchronized(priurls) {
				if (debug) debug("Current urllist size "+prilist.size()+"=="+priurls.size());
				if (prilist.size()>0) {
					do {
						priurl=(PriorityURL)prilist.firstElement();
						debug("PriURL : "+priurl);
						if (priurl.getPriority()==PriorityURL.MAX_PRIORITY) {
							parent.pushReload(priurl.getURL());
							prilist.removeElementAt(0);
							priurls.remove(priurl.getURL());
						}
					} while (prilist.size()>0 && priurl.getPriority()==PriorityURL.MAX_PRIORITY);
	
					for (Enumeration e=prilist.elements();e.hasMoreElements();) {
						priurl=(PriorityURL)e.nextElement();
						priurl.increasePriority();
					}
				}
			}
		}
	}




	public void addURL(String url) {
		addURL(url,PriorityURL.DEF_PRIORITY);
	}

	public void addURL(String url,int priority) {
		PriorityURL priurl;
		
		synchronized(priurls) {
			priurl=(PriorityURL)priurls.get(url);
			if (priurl!=null) {
				if (priurl.getPriority()<priority) {
					priurl.setPriority(priority);
					prilist.Sort();
				}
			} else {
				priurl=new PriorityURL(url,priority);
				priurls.put(url,priurl);
				prilist.addSorted(priurl);
			}
		}
	}
}
