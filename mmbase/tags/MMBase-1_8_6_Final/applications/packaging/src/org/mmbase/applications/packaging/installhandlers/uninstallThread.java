/*
 *  This software is OSI Certified Open Source Software.
 *  OSI Certified is a certification mark of the Open Source Initiative.
 *  The license (Mozilla version 1.0) can be read at the MMBase site.
 *  See http://www.MMBase.org/license
 */
package org.mmbase.applications.packaging.installhandlers;

import org.mmbase.applications.packaging.UninstallManager;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @author     Daniel Ockeloen
 *
 * background hanlder for sending email, a call backthread
 * that is used to send email (one thread per active email
 * node)
 * @created    July 20, 2004
 */
public class uninstallThread implements Runnable {

    // logger
    private static Logger log = Logging.getLoggerInstance(uninstallThread.class);

    // Thread
    Thread kicker = null;


    /**
     * create a background thread with given email node
     */
    public uninstallThread() {
        init();
    }


    /**
     * init the thread
     */
    public void init() {
        this.start();
    }


    /**
     * Starts the main Thread.
     */
    public void start() {
        /*
         *  Start up the main thread
         */
        if (kicker == null) {
            kicker = new Thread(this, "uninstallthread");
            kicker.start();
        }
    }


    /**
     * Main run, exception protected
     */
    public void run() {
        try {
            doWork();
        } catch (Exception e) {
            log.error("run(): ERROR: Exception in uninstallThread thread!");
            log.error(Logging.stackTrace(e));
        }
    }


    /**
     * Main work handelr
     */
    public void doWork() {
        UninstallManager.performUninstall();
    }

}

