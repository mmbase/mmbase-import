/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

/**
 * @version $Id: MMEventsProbe.java,v 1.6 2003-05-08 06:01:22 kees Exp $
 * @author Daniel Ockeloen
 */
public class MMEventsProbe implements Runnable {

    Thread kicker = null;
    MMEvents parent = null;

    public MMEventsProbe(MMEvents parent) {
        this.parent = parent;
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
            kicker = new Thread(this, "mmevents");
            kicker.setDaemon(true);
            kicker.start();
        }
    }

    /**
     * Stops the admin Thread.
     */
    public void stop() {
        /* Stop thread */
        kicker.interrupt();
        kicker = null;
    }

    /**
     * admin probe, try's to make a call to all the maintainance calls.
     */
    public void run() {
        while (kicker != null) {
            parent.probeCall();
        }
    }
}