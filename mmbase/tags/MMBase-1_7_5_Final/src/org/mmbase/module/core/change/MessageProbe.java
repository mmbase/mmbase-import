/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core.change;

import org.mmbase.module.core.MMObjectBuilder;

/**
 * MessageProbe a thread object started to handle all nofity's needed when
 * one is received.
 * @javadoc
 *
 * @author Daniel Ockeloen
 * @version $Id: MessageProbe.java,v 1.1.2.1 2004-10-09 10:51:45 nico Exp $
 */
public class MessageProbe implements Runnable {

    /**
     * @javadoc
     * @scope private
     */
    Thread kicker = null;
    /**
     * @javadoc
     * @scope private
     */
    SharedStorage parent=null;
    /**
     * @javadoc
     * @scope private
     */
    MMObjectBuilder bul=null;
    /**
     * @javadoc
     * @scope private
     */
    String machine;
    /**
     * @javadoc
     * @scope private
     */
    String id;
    /**
     * @javadoc
     * @scope private
     */
    String tb;
    /**
     * @javadoc
     * @scope private
     */
    String ctype;
    /**
     * @javadoc
     * @scope private
     */
    boolean remote;

    /**
     * @javadoc
     */
    public MessageProbe(SharedStorage parent,MMObjectBuilder bul,String machine,
            String id,String tb,String ctype,boolean remote) {
        this.parent=parent;
        this.bul=bul;
        this.machine=machine;
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
            kicker = new Thread(this,"MessageProbe");
            kicker.setDaemon(true);
            kicker.start();
        }
    }

    /**
     * Stops the admin Thread.
     */
    public void stop() {
        /* Stop thread */
        kicker.setPriority(Thread.MIN_PRIORITY);
        kicker = null;
    }

    /**
     * @javadoc
     */
    public void run() {
        if (remote) {
            bul.nodeRemoteChanged(machine,id,tb,ctype);
            parent.checkWaitingNodes(id);
        } else {
            bul.nodeLocalChanged(machine,id,tb,ctype);
            parent.checkWaitingNodes(id);
        }
    }
}
