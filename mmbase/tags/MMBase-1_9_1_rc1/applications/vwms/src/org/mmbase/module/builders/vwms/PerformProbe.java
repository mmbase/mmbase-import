/* -*- tab-width: 4; -*-

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders.vwms;

import org.mmbase.module.core.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @application VWMs
 * @author Rico Jansen
 * @version $Id: PerformProbe.java,v 1.8 2004-10-08 10:57:57 pierre Exp $
 */
public class PerformProbe implements Runnable {

    private static Logger log = Logging.getLoggerInstance(PerformProbe.class.getName());

    private VwmProbeInterface vwm;
    private MMObjectNode node;
    private int status;

    Thread kicker = null;

    public PerformProbe(VwmProbeInterface vwm, MMObjectNode node) {
        this.vwm = vwm;
        this.node = node;
        this.status = 1;
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
        log.service(
            "Creating and starting new thread for tasknr "
                + node.getIntValue("number")
                + " task "
                + node.getStringValue("task"));
        if (kicker == null) {
            kicker = new Thread(this, "Performprobe tasknr " + node.getIntValue("number"));
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
        try {
            status = 2;
            log.service(
                "Calling vwm "
                    + vwm.getName()
                    + " performTask for tasknr "
                    + node.getIntValue("number")
                    + " task "
                    + node.getStringValue("task"));
            vwm.performTask(node);
            status = 3;
        } catch (Exception e) {
            log.error("performTask failed" + e);
            log.error(e.getMessage());
            log.error(Logging.stackTrace(e));
            status = 5;
        }
    }

    public int getStatus() {
        return (status);
    }
}