/*
 *  This software is OSI Certified Open Source Software.
 *  OSI Certified is a certification mark of the Open Source Initiative.
 *  The license (Mozilla version 1.0) can be read at the MMBase site.
 *  See http://www.MMBase.org/license
 */
package org.mmbase.applications.packaging.providerhandlers;

import java.util.Iterator;

import org.mmbase.applications.packaging.BundleManager;
import org.mmbase.applications.packaging.PackageManager;
import org.mmbase.applications.packaging.ProviderManager;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * DiskProvider, Handler for Disk Providers. gets packages and bundles from
 * the provider and feeds them to the package and bundle managers.
 *
 * @author     Daniel Ockeloen (MMBased)
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

    public void getPackages() {
        // get all the providers lines up for a call
        Iterator<ProviderInterface> i = ProviderManager.getProviders();
        while (i.hasNext()) {
            ProviderInterface pi = i.next();
            try {
	        pi.getPackages();
                PackageManager.removeOfflinePackages(pi);
                BundleManager.removeOfflineBundles(pi);
            } catch (Exception e) {
                log.error("Something went wring in package discovery : "+pi.getPath());
                e.printStackTrace();
            }
        } 
    }

}

