/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package org.mmbase.util;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
/**
 * Also called counting semaphores, Dijkstra semaphores are used to control access to
 * a set of resources. A Dijkstra semaphore has a count associated with it and each
 * acquire() call reduces the count. A thread that tries to acquire() a Dijkstra
 * semaphore with a zero count blocks until someone else calls release() thus increasing
 * the count.
 * <b>When to use</b>
 * Recommended when applications require a counting semaphore. Implementing
 * a counting semaphore using wait()/notify() and counters within your application
 * code makes your code less readable and quickly increases the complexity
 * (especially when you have the need for multiple counting semaphores). Can also
 * be used to port code from POSIX environment.
 *
 * @author Karthik Rangaraju
 * @author Michiel Meeuwissen
 * @since  MMBase-1.6
 * @version $Id$
 */
public class DijkstraSemaphore {

    private static final Logger log = Logging.getLoggerInstance(DijkstraSemaphore.class);

    private int count;
    private int maxCount;
    private Object starvationLock = new Object();

    /**
     * Creates a Dijkstra semaphore with the specified max count and initial count set
     * to the max count (all resources released)
     * @param pMaxCount is the max semaphores that can be acquired
     */
    public DijkstraSemaphore(int pMaxCount) {
        this(pMaxCount, pMaxCount);
    }

    /**
     * Creates a Dijkstra semaphore with the specified max count and an initial count
     * of acquire() operations that are assumed to have already been performed.
     * @param pMaxCount is the max semaphores that can be acquired
     * @param pInitialCount is the current count (setting it to zero means all semaphores
     * have already been acquired). 0 <= pInitialCount <= pMaxCount
     */
    public DijkstraSemaphore(int pMaxCount, int pInitialCount) {
        count = pInitialCount;
        maxCount = pMaxCount;
    }

    /**
     * If the count is non-zero, acquires a semaphore and decrements the count by 1,
     * otherwise blocks until a release() is executed by some other thread.
     * @throws InterruptedException if the thread is interrupted when blocked
     * @see #tryAcquire()
     * @see #acquireAll()
     */
    public synchronized void acquire() throws InterruptedException {
        // Using a spin lock to take care of rogue threads that can enter
        // before a thread that has exited the wait state acquires the monitor
        while (count == 0) {
            long startwait = 0;
            if (log.isDebugEnabled()) {
                startwait = System.currentTimeMillis();
            }
            wait();
            if (startwait != 0) { 
                log.debug("Waited " + (System.currentTimeMillis() - startwait) + " ms for a resource");
            }
        }
        count--;
        synchronized (starvationLock) {
            if (count == 0) {
                starvationLock.notify();
            }
        }
    }

    /**
     * Non-blocking version of acquire().
     * @return true if semaphore was acquired (count is decremented by 1), false
     * otherwise
     */
    public synchronized boolean tryAcquire() {
        if (count != 0) {
            count--;
            synchronized (starvationLock) {
                if (count == 0) {
                    starvationLock.notify();
                }
            }
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Releases a previously acquires semaphore and increments the count by one. Does not
     * check if the thread releasing the semaphore was a thread that acquired the
     * semaphore previously. If more releases are performed than acquires, the count is
     * not increased beyond the max count specified during construction.
     * @see #release(int pCount)
     * @see #releaseAll()
     */
    public synchronized void release() {
        count++;
        if (count > maxCount) {
            count = maxCount;
        }
        notify();
    }

    /**
     * Same as release() except that the count is increased by pCount instead of 1. The
     * resulting count is capped at max count specified in the constructor
     * @param pCount is the amount by which the counter should be incremented
     * @see #release()
     */
    public synchronized void release(int pCount) {
        while (count < maxCount && pCount != 0){
            release();
            pCount --;
        }
    }

    /**
     * Tries to acquire all the semaphores thus bringing the count to zero.
     * @throws InterruptedException if the thread is interrupted when blocked on this call
     * @see #acquire()
     * @see #releaseAll()
     */
    public synchronized void acquireAll() throws InterruptedException {
        while(count != 0){
            acquire();
        }
    }

    /**
     * Releases all semaphores setting the count to max count.
     * Warning: If this method is called by a thread that did not make a corresponding
     * acquireAll() call, then you better know what you are doing!
     * @see #acquireAll()
     */
    public synchronized void releaseAll() {
        release(maxCount);
    }

    /**
     * This method blocks the calling thread until the count drops to zero.
     * The method is not stateful and hence a drop to zero will not be recognized
     * if a release happens before this call. You can use this method to implement
     * threads that dynamically increase the resource pool or that log occurences
     * of resource starvation. Also called a reverse-sensing semaphore
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    public void starvationCheck() throws InterruptedException {
        synchronized (starvationLock) {
            if (count != 0) {
                starvationLock.wait();
            }
        }
    }
}

