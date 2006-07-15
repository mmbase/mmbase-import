/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

/**
 * MMBaseProbe is a thread-like object that gets instantiated by MMbase.
 * It calls the callback method {@link MMBase#doProbeRun} in MMbase, which in turn probes the builders.
 * After the probe has been preformed, it schedules itself to be destroyed after an alotted time (10 minutes),
 * which also clears the reference in MMBase and prompts that module to create a new probe instance.
 * This way, maintanance is scheduled to run every ten minutes.
 * @javadoc
 *
 * @author Daniel Ockeloen
 * @author Pierer van Rooden (javadoc)
 * @version $Id: MMBaseProbe.java,v 1.10 2003-07-21 07:31:55 keesj Exp $
 */
public class MMBaseProbe implements Runnable {

    /**
     * @javadoc
     * @scope private
     */
    Thread kicker = null;
    /**
     * @javadoc
     * @scope private
     */
    MMBase parent=null;
    /**
     * @javadoc
     * @scope private
     */
    String name;
    /**
     * @javadoc
     * @scope private
     */
    String input;
    /**
     * @javadoc
     * @scope private
     */
    int len;

    /**
     * Constructor, which ties this probe object to an MMBase module
     */
    public MMBaseProbe(MMBase parent) {
        this.parent=parent;
        init();
    }

    /**
     * Initializes the probe and starts a probe thread.
     */
    public void init() {
        this.start();
    }


    /**
     * Starts a thread to perform the probes.
     */
    public void start() {
        /* Start up the main thread */
        if (kicker == null) {
            kicker = new Thread(this,"MMBaseProbe");
            kicker.setDaemon(true);
            kicker.start();
        }
    }

    /**
     * Stops the probing thread.
     * Uses deprecated methods (suspend/stop), should be changed or removed.
     */
    public void stop() {
        /* Stop thread */
        kicker.interrupt();
        kicker = null;
    }

    /**
     * Calls a callback method in the MMBase module.
     * After the maintenance is performed, it sleeps itself for ten minutes before terminating.
     * During this time is functions as a placeholder, preventing another probe to be started
     * and allowing for scheduling of the probe task.
     */
    public void run() {
        parent.doProbeRun();
        if (kicker!=null) {
            try {
                Thread.sleep(10*60*1000);
            } catch (InterruptedException e) { return;}
        }
        parent.probe=null;
    }
}
