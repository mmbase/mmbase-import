/* -*- tab-width: 4; -*-
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */
package org.mmbase.module;

import java.util.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * admin module, keeps track of all the worker pools
 * and adds/kills workers if needed (depending on
 * there load and info from the config module).
 *
 * @version $Id: ModuleProbe.java,v 1.8 2003-03-26 10:15:20 kees Exp $
 * @author Daniel Ockeloen
 */
public class ModuleProbe implements Runnable {
    
    private static Logger log = Logging.getLoggerInstance(ModuleProbe.class.getName());
    
    private Thread kicker = null;
    private Map mods;
    
    public ModuleProbe(Map mods) {
        this.mods=mods;
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
            log.service("Starting main thread of ModuleProbe");
            kicker = new Thread(this,"ModuleProbe");
            kicker.setDaemon(true);
            kicker.start();
        }
    }
    
    /**
     * Stops the admin Thread.
     */
    public void stop() {
        /* Stop thread */
        log.service("Stopping main thread of ModuleProbe");
        kicker.interrupt();
        kicker = null;
    }
    
    /**
     * admin probe, try's to make a call to all the maintainance calls.
     */
    public void run() {
        while (kicker != null) {
            try {
                Thread.sleep(60*1000);
            } catch (InterruptedException e){
                return;
            }
            if (mods != null) {
                for (Iterator i = mods.entrySet().iterator(); i.hasNext();) {
                    Map.Entry entry = (Map.Entry) i.next();
                    try {
                        Module mod = (Module) entry.getValue();
                        mod.maintainance();
                    } catch(Exception er) {
                        log.error("error on maintainance call : " + entry.getKey());
                    }
                }
            }
        }
    }
}
