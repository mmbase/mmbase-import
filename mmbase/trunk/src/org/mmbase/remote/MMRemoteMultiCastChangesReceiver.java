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

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * MultiCastChangesReceiver is a thread object that reads the receive queue
 * and spawns them to call the objects (listeners) who need to know.
 *
 * @version 12-May-1999
 * @author Rico Jansen
 */
public class MMRemoteMultiCastChangesReceiver implements Runnable {

    private static Logger log = Logging.getLoggerInstance(MMRemoteMultiCastChangesReceiver.class.getName());

    Thread 	       kicker = null;
    MMRemoteMultiCast  parent=null;
    Queue              nodesToSpawn;
    InetAddress        ia;
    MulticastSocket    ms;
    int                mport;
    int                dpsize;

    public MMRemoteMultiCastChangesReceiver(MMRemoteMultiCast parent,Queue nodesToSpawn) {
        this.parent=parent;
        this.nodesToSpawn=nodesToSpawn;
        init();
    }

    public void init() {
        this.start(); 
    }

    public void start() {
        /* Start up the main thread */
        if (kicker == null) {
            kicker = new Thread(this,"MMRemoteMulticastReceiver");
            kicker.start();
        }
    }
 
    public void stop() {
        kicker.setPriority(Thread.MIN_PRIORITY);  
        kicker = null;
    }

    /**
     * admin probe, try's to make a call to all the maintainance calls.
     */
    public void run() {
        try {
            doWork();
        } catch (Exception e) {
            log.error("run():" + e.toString());
            log.error(Logging.stackTrace(e));
        }
    }

    private void doWork() {
        String chars;
        String machine,vnr,id,tb,ctype;
        StringTokenizer tok;
        while(kicker!=null) {
            chars=(String)nodesToSpawn.get();
            parent.spawncount++;
            tok=new StringTokenizer(chars,",");
            if (tok.hasMoreTokens()) {
                machine=tok.nextToken(); 
                if (tok.hasMoreTokens()) {
                    vnr=tok.nextToken(); 
                    if (tok.hasMoreTokens()) {
                        id=tok.nextToken(); 
                        if (tok.hasMoreTokens()) {
                            tb=tok.nextToken(); 
                            if (tok.hasMoreTokens()) {
                                ctype=tok.nextToken(); 
                                if (!ctype.equals("x")) {
                                    parent.handleMsg(machine,vnr,id,tb,ctype);
                                } else {
                                    if( tok.hasMoreTokens() ) {
                                        String xml=tok.nextToken(""); 
                                        parent.handleXML(machine,vnr,id,tb,ctype,xml);
                                    } else { log.warn("doWork("+chars+"): '' not defined!"); } 
                                } 
                            } else { log.warn("doWork("+chars+"): 'ctype' not defined!"); } 
                        } else { log.warn("doWork("+chars+"): 'tb' not defined!"); } 
                    } else { log.warn("doWork("+chars+"): 'id' not defined!"); } 
                } else { log.warn("doWork("+chars+"): 'vnr' not defined!"); } 
            } else { log.warn("doWork("+chars+"): 'machine' not defined!"); } 
        } 
    }
}
