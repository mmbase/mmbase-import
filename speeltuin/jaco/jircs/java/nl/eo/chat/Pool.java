/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package nl.eo.chat;

import java.util.Vector;

/**
 * A pool to manage all pool element classes of a certain type.
 *
 * @author Jaco de Groot
 */
public class Pool {
    int min;
    int max;
    int poolSize;
    Vector free = new Vector();
    // A trhead will be in init after it is retrieved with getObject() and until
    // proceed is called.
    Vector init = new Vector();
    Vector busy = new Vector();
    Class c;

    public Pool(Class c, int min, int max) {
        this.c = c;
        this.min = min;
        this.max = max;
        poolSize = 0;
        for (int i = 0; i < min; i++) {
            try {
                addThread();
            } catch(Exception e) {
                Server.log.error("Could not create instance of " + c.getName() + ".");
            }
        }
    }

    public synchronized Object getObject() {
        int size = free.size();
        if (size == 0) {
            checkBusyThreads();
            size = free.size();
            if (size == 0 && (poolSize < max || max == -1)) {
                // Add an object to the pool.
                try {
                    addThread();
                } catch(Exception e) {
                    Server.log.error("Could not create instance of " + c.getName() + ".");
                    return null;
                }
                checkBusyThreads();
                size = free.size();
            }
            Server.log.debug(c.getName() + " pool: " + free.size() + " free, " + init.size() + " init, " + busy.size() + " busy, " + poolSize + " total.");
            while (size == 0) {
                Server.log.debug("Pool " + c.getName() + " waiting for free threads.");
                try {
                    Thread.currentThread().sleep(1000);
                } catch(InterruptedException e) {
                }
                checkBusyThreads();
                size = free.size();
            }
        }
        Object object = free.remove(0);
        init.add(object);
        return object;
    }

    public synchronized void proceed(PoolElement poolElement) {
        if (!init.remove(poolElement)) {
            Server.log.error(c.getName() + " pool error: This thread should have been part of the init Vector.");
        }
        poolElement.proceed();
        if (!busy.add(poolElement)) {
            Server.log.error(c.getName() + " pool error: This thread should not already be part of the busy Vector.");
        }
    }

    private void addThread() throws InstantiationException, IllegalAccessException {
        PoolElement poolElement = (PoolElement)c.newInstance();
        poolSize++;
        poolElement.setNumber(poolSize);
        new Thread(poolElement).start();
        busy.add(poolElement);
        Server.log.info("New " + c.getName() + " pool size: " + poolSize);
    }

    private void checkBusyThreads() {
        for (int i = 0; i < busy.size(); i++) {
            PoolElement poolElement = (PoolElement)busy.elementAt(i);
            if (poolElement.isSuspended()) {
                busy.remove(i);
                i--;
                free.add(poolElement);
            }
        }
    }

    // For the shutdown thread.
    public synchronized int getBusyThreads() {
        checkBusyThreads();
        return busy.size();
    }


}
