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
import org.mmbase.util.logging.*;

/**
 * @author Rico Jansen
 */
public class ImagePusher implements Runnable {

    private static Logger log = Logging.getLoggerInstance(ImagePusher.class.getName());
	Thread kicker = null;
	int sleepTime=4444;
	ImageMaster parent;

	Queue files2copy=new Queue(128);
	FileCopier filecopier=new FileCopier(files2copy);

	public ImagePusher(ImageMaster parent) {
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
			kicker = new Thread(this,"Image");
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
		Vector procfiles;
		aFile2Copy file;
		Hashtable files;
	
		log.info("ImagePusher Active");
		while (kicker!=null) {
			try { Thread.sleep(sleepTime); } catch (InterruptedException e) {}
			if (parent.files.size()>0) {
				synchronized(parent.files) {
					procfiles=parent.files;
					parent.files=new Vector();
				}
				log.service("ImagePusher processing "+procfiles.size()+" files");
				files=killdups(procfiles);
				for (Enumeration e =files.keys();e.hasMoreElements();) {
					file=(aFile2Copy)e.nextElement();
					files2copy.append(file);
				}
			}
		}
	}

	private Hashtable killdups(Vector files) {
		Hashtable hfiles=new Hashtable(files.size());
		aFile2Copy file;
		for (Enumeration e=files.elements();e.hasMoreElements();) {
			file=(aFile2Copy)e.nextElement();
			hfiles.put(file,"feep");
		}
		log.info("ImagePusher -> "+files.size()+" - "+hfiles.size()+" dups "+(files.size()-hfiles.size()));
		return(hfiles);
	}
}
