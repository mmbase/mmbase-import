/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

import java.net.*;
import java.util.StringTokenizer;

import org.mmbase.util.Queue;
import org.mmbase.util.logging.*;

/**
 * MultiCastChangesReceiver is a thread object that reads the receive queue
 * and spawns them to call the objects (listeners) who need to know.
 * @javadoc
 *
 * @author Rico Jansen
 * @version $Id: MultiCastChangesReceiver.java,v 1.11 2004-02-18 15:03:13 keesj Exp $
 */
public class MultiCastChangesReceiver implements Runnable {

    private static final Logger log = Logging.getLoggerInstance(MultiCastChangesReceiver.class);

    /**
     * @javadoc
     * @scope private
     */
    Thread kicker = null;
    /**
     * @javadoc
     * @scope private
     */
    MMBaseMultiCast parent=null;
    /**
     * @javadoc
     * @scope private
     */
    Queue nodesToSpawn;
    /**
     * @javadoc
     * @scope private
     */
    InetAddress ia;
    /**
     * @javadoc
     * @scope private
     */
    MulticastSocket ms;
    /**
     * @javadoc
     * @scope private
     */
    int mport;
    /**
     * @javadoc
     * @scope private
     */
    int dpsize;

    /**
     * @javadoc
     */
    public MultiCastChangesReceiver(MMBaseMultiCast parent,Queue nodesToSpawn) {
        this.parent=parent;
        this.nodesToSpawn=nodesToSpawn;
        init();
    }

    /**
     * @javadoc
     */
    public void init() {
        this.start();
    }

    /**
     * @javadoc
     */
    public void start() {
        /* Start up the main thread */
        if (kicker == null) {
            kicker = new Thread(this,"MulticastReceiver");
            kicker.setDaemon(true);
            kicker.start();
        }
    }

    /**
     * @javadoc
     */
    public void stop() {
        kicker.setPriority(Thread.MIN_PRIORITY);
        kicker = null;
    }

    /**
     * admin probe, try's to make a call to all the maintainance calls.
     */
    public void run() {
        while(kicker!=null) {
            try {
                doWork();
            } catch (Exception e) {
                log.error(Logging.stackTrace(e));
            }
        }
    }

    /**
     * @javadoc
     */
    private void doWork() {
        String chars;
        String machine,vnr,id,tb,ctype;
        StringTokenizer tok;
        while(kicker!=null) {
            chars=(String)nodesToSpawn.get();
            if (log.isDebugEnabled()) {
                log.debug("RECIEVE=>" + new String(chars));
            }
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
                                if (!ctype.equals("s")) {
                                    parent.handleMsg(machine,vnr,id,tb,ctype);
                                } else {
                                    if (tok.hasMoreTokens()) {
                                        String xml=tok.nextToken("");
                                        parent.commitXML(machine,vnr,id,tb,ctype,xml);
                                    } else log.error("doWork("+chars+"): 'xml' could not be extracted from this string!");
                                }
                            } else log.error("doWork("+chars+"): 'ctype' could not be extracted from this string!");
                        } else log.error("doWork("+chars+"): 'tb' could not be extracted from this string!");
                    } else log.error("doWork("+chars+"): 'id' could not be extracted from this string!");
                } else log.error("doWork("+chars+"): 'vnr' could not be extracted from this string!");
            } else log.error("doWork("+chars+"): 'machine' could not be extracted from this string!");
        }
    }
}
