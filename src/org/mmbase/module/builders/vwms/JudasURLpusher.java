package org.mmbase.module.builders.vwms;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

/**
 * @author Rico Jansen
 */
public class JudasURLpusher implements Runnable {

	Thread kicker = null;
	int sleepTime=3333;
	Judas parent;

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
		if (kicker!=null) {
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
		Vector procurls;
		String url;
		Hashtable urls;
	
		System.out.println("JudasURLpusher Active");
		while (kicker!=null) {
			try { Thread.sleep(sleepTime); } catch (InterruptedException e) {}
			if (parent.urls.size()>0) {
				synchronized(parent.urls) {
					procurls=parent.urls;
					parent.urls=new Vector();
				}
				System.out.println("JudasURLpusher processing "+procurls.size()+" urls");
				urls=killdups(procurls);
				for (Enumeration e =urls.keys();e.hasMoreElements();) {
					url=(String)e.nextElement();
					parent.pushReload(url);
				}
			}
		}
	}

	private Hashtable killdups(Vector urls) {
		Hashtable hurls=new Hashtable(urls.size());
		String url;
		for (Enumeration e=urls.elements();e.hasMoreElements();) {
			url=(String)e.nextElement();
			hurls.put(url,"feep");
		}
		System.out.println("JudasURLpusher -> "+urls.size()+" - "+hurls.size()+" dups "+(urls.size()-hurls.size()));
		return(hurls);
	}
}
