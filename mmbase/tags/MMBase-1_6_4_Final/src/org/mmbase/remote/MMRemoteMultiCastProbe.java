/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.remote;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

//import org.mmbase.util.logging.Logger;
//import org.mmbase.util.logging.Logging;


/**
 * admin module, keeps track of all the worker pools
 * and adds/kills workers if needed (depending on
 * there load and info from the config module).
 *
 * @version 27 Mar 1997
 * @author Daniel Ockeloen
 */
public class MMRemoteMultiCastProbe implements Runnable { 
    //Logging removed automaticly by Michiel, and replace with __-methods
    private static String __classname = MMRemoteMultiCastProbe.class.getName();


    boolean __debug = false;
    private static void __debug(String s) { System.out.println(__classname + ":" + s); }
    //private static Logger log = Logging.getLoggerInstance(MMRemoteMultiCastProbe.class.getName());

    Thread kicker = null;
    MMRemoteMultiCast parent=null;
    RemoteBuilder serv=null;
    String id;
    String tb;
    String ctype;
    boolean remote;

    public MMRemoteMultiCastProbe(MMRemoteMultiCast parent,RemoteBuilder serv,String id,String tb, String ctype, boolean remote) throws Exception {
        this.parent=parent;
        this.serv=serv;
        this.id=id;
        this.tb=tb;
        this.ctype=ctype;
        this.remote=remote;
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
            kicker = new Thread(this,"MMBaseProbe");
            kicker.start();
        }
    }
    
    /**
     * Stops the admin Thread.
     */
    public void stop() {
        /* Stop thread */
        kicker.setPriority(Thread.MIN_PRIORITY);  
        kicker.suspend();
        kicker.stop();
        kicker = null;
    }

    /**
     * admin probe, try's to make a call to all the maintainance calls.
     */
    public void run() {
        if (remote) {
            serv.nodeRemoteChanged(id,tb,ctype);
            //parent.checkWaitingNodes(id);    
        } else {
            serv.nodeLocalChanged(id,tb,ctype);
            //parent.checkWaitingNodes(id);    
        }
    }
}
