/*
 *  This software is OSI Certified Open Source Software.
 *  OSI Certified is a certification mark of the Open Source Initiative.
 *  The license (Mozilla version 1.0) can be read at the MMBase site.
 *  See http://www.MMBase.org/license
 */
package org.mmbase.applications.packaging.providerhandlers;

import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.*;
import org.mmbase.module.builders.Versions;
import org.mmbase.applications.packaging.packagehandlers.*;
import org.mmbase.applications.packaging.bundlehandlers.*;
import org.mmbase.applications.packaging.*;
import org.xml.sax.*;

import java.io.*;
import java.util.*;
import java.util.jar.*;

import org.w3c.dom.*;

/**
 * DiskProvider, Handler for Disk Providers. gets packages and bundles from
 * the provider and feeds them to the package and bundle managers.
 *
 * @author     Daniel Ockeloen (MMBased)
 * @created    July 20, 2004
 */
public class PackageDiscovery implements Runnable {
    private static Logger log = Logging.getLoggerInstance(PackageDiscovery.class);
    private Thread kicker;
    private int delay = 10;
    private int runtimes = 5;
    private int runtimecount = 1;


    public PackageDiscovery() { 
	start();
    }

    public void resetSleepCounter() {
        runtimecount = 1;    
	delay = 10;
        if (kicker == null) {
	    start();
        } else {
            kicker.interrupt(); 
        }	
    }


    /**
     * Starts the main Thread.
     */
    public void start() {
        /*
         *  Start up the main thread
         */
        if (kicker == null) {
            kicker = new Thread(this, "package discovery thread");
            kicker.start();
        }
    }


    /**
     * Main loop, exception protected
     */
    public void run() {
        while (kicker != null && runtimes>runtimecount) {
            try {
                getPackages();
                runtimecount++;
                Thread.sleep(delay * 1000);
		delay += delay * 2; // wait double the time next time 
            } catch (InterruptedException e) {
            } catch (Exception e) {
                log.error("problem in package discovery thread");
	    }
        }
	kicker = null;
    }

    private void getPackages() {
        // get all the providers lines up for a call
        Iterator i = ProviderManager.getProviders();
        while (i.hasNext()) {
            ProviderInterface pi = (ProviderInterface)i.next();
            try {
	        pi.getPackages();
                PackageManager.removeOfflinePackages(pi);
            } catch (Exception e) {
                log.error("Something went wring in package discovery : "+pi.getPath());
                e.printStackTrace();
            }
        } 
    }

}

