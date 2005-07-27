/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package nl.eo.chat;

import java.io.*;
import java.net.*;

/**
 * A pool element can be managed by a pool.
 *
 * @author Jaco de Groot
 */
abstract class PoolElement implements Runnable {
    protected boolean suspended = false;
    protected int number;
    protected MessagePool pool;
    protected Logger log;

    PoolElement() {
    }

    public boolean isSuspended() {
        return suspended;
    }

    public synchronized void proceed() {
        suspended = false;
        notify();
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public void setLogger(Logger log) {
        this.log = log;
        log.debug("Logger registered with PoolElement "+this.getClass().toString());
    }

    public void setPool(MessagePool pool) {
        this.pool = pool;
    }


}


