/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.gui.html;

import java.lang.*;
import java.io.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;

public class pageProcess implements Runnable {

    private static Logger log =  Logging.getLoggerInstance(scanparser.class.getName());

	String uri;
	scanpage sp;
	scanparser parser;

	Thread kicker=null;

	pageProcess(scanparser parser,scanpage sp,String uri) {
		this.sp=sp;
		this.uri=uri;
		this.parser=parser;
	}

    /**
     * Starts the main Thread.
     */
    public void start() {
        /* Start up the main thread */
        if (kicker == null) {
            kicker = new Thread(this,"pageProcess "+uri);
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
     * @see doWork
     */
    public void run () {
		log.debug("Starting calc "+uri);
        try {
           doWork();
        } catch(Exception e) {
           log.error(e.getMessage());
           log.error(Logging.stackTrace(e));
        }
		parser.removeProcess(uri);
		log.debug("Done calc "+uri);
    }

	private void doWork() {
		parser.calcPage(uri,sp,0);
	}

	public String toString() {
		return(uri);
	}
}
