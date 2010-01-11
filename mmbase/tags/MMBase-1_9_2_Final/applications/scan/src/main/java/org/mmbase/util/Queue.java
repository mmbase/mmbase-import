/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.util.*;

/**
 * A list object that allows firstin-firstout retrieval of data.
 * When querying for data that is not (yet) available, the retrieve-process sleeps
 * until it is notified of a change.
 * In 1.5, this class need to be replaced with the java.util.concurrent.BlockingQueue&lt;E&gt; interface.
 *
 * @author vpro
 * @version $Id$
 * @deprecated Use edu.emory.mathcs.backport.java.util.concurrent.BlockingQueue, or some other Queue implementation.
 */
public class Queue {

    /**
     * Default size of 32 for the queue if none is specified.
     */
    public static int DEFAULT_QUEUE_SIZE = 32;

    /**
     * Default timeout of 0 for a blocking append call.
     * Not used
     */
    public static int DEFAULT_APPEND_TIMEOUT = 0;

    /**
     * Default timeout of 0 for a blocking get call.
     * Not used
     */
    public static int DEFAULT_GET_TIMEOUT = 0;

    /**
     * The time to wait until an attempt to append an item times out.
     * Not used
     */
    public int appendTimeoutTime;
    /**
     * The time to wait until an attempt to get an item times out.
     * Not used
     */
    public int getTimeoutTime;

    // the fields below should be private

    /**
     * The head element of the queue.
     * This is the element that will be returned first by a {@link #get}.
     */
    QueueElement head;
    /**
     * The tail element of the queue.
     * This is the element that was last added using {@link #append}.
     */
    QueueElement tail;

    /**
     * The real number of items in the queue.
     */
    int len = 0;

    /**
     * Max # of items to be allowed in queue.
     * Not really used.
     */
    private int queuesize;

    // fields below are not used any more, should be removed

    public Vector items;
    int flip = 0;
    long get1,get2;

    //private Object[] items;
    //public Object[] items;
    //private Object isNotFull = new Object();

    private int first;            // pointer to last item in queue
    /**
     * Constructs the queue with the default queue size set to
     * DEFAULT_QUEUE_SIZE, and the append timeout set to
     * DEFAULT_APPEND_TIMEOUT
     *
     * @see Queue#DEFAULT_QUEUE_SIZE
     * @see Queue#DEFAULT_APPEND_TIMEOUT
     */
    public Queue() {
        this(DEFAULT_QUEUE_SIZE, DEFAULT_APPEND_TIMEOUT, DEFAULT_GET_TIMEOUT);
    }

    /**
     * Constructs the queue, sets the max number of queueable
     * items to the given size, sets the append timeout
     * to DEFAULT_APPEND_TIMEOUT, and sets the get timeout to
     * DEFAULT_GET_TIMEOUT
     *
     * @param size The maximum size of the queue
     * @see Queue#DEFAULT_APPEND_TIMEOUT
     */
    public Queue(int size) {
        this(size, DEFAULT_APPEND_TIMEOUT, DEFAULT_GET_TIMEOUT);
    }

    /**
     * Constructs the queue, sets the max number of queueable
     * items to the given size, and sets the append() and get() timeouts
     * to the given values.
     *
     * @param size The maximum size of the queue
     * @param appendTimeout If we can't append() within this many milliseconds,
     *                      the appendTimeout() method is called before retrying.
     * @param getTimeout If we can't get() something within this many
     *                   milliseconds, the getTimeout() method is called.
     * @see Queue#appendTimeout
     * @see Queue#getTimeout
     */
    public Queue(int size, int appendTimeout, int getTimeout) {

        // better call newQueue...
        appendTimeoutTime = appendTimeout;
        getTimeoutTime = getTimeout;

        queuesize = size;        // initial size of the queue
        items = new Vector(queuesize);

        first = 0;
    }

    /**
     * Re-uinitializes the queue, sets the max number of queueable
     * items to the given size, and sets the append() and get() timeouts
     * to the given values.
     *
     * @param size The maximum size of the queue
     * @param appendTimeout If we can't append() within this many milliseconds,
     *                      the appendTimeout() method is called before retrying.
     * @param getTimeout If we can't get() something within this many
     *                   milliseconds, the getTimeout() method is called.
     */
    public void newQueue(int size, int appendTimeout, int getTimeout) {

        appendTimeoutTime = appendTimeout;
        getTimeoutTime = getTimeout;

        queuesize = size;        // initial size of the queue
        items = new Vector(queuesize);

        first = 0;
    }

    /**
     * Returns the size of the queue
     */
    public int queueSize() {
        return queuesize;
    }

    /**
     * Returns the number of items currently in the queue.
     */
    public int count() {
        //return items.size();
        return len;
    }

    /**
     * Appends the given item to the queue. This method calls a
     * synchronized append method, so that it won't interfere with a get
     * call. The method will block if the queue is full, and it won't
     * block otherwise.
     *
     * @todo rename to put(), similar to java's BlockingQueue
     * @param item The item to be appended to the queue */
    public synchronized void append(Object item) {
        // put a object in the vector and wait on it
        // it should be able to block if full
        QueueElement p = new QueueElement();
        p.obj = item;

        if (tail == null) {
            head = p;
        } else {
            tail.next = p;
        }
        p.next = null;
        tail = p;
        len++;
        flip++;
        if (flip > 99) {
            flip = 0;
        }
        notify(); // scream that a new one has reached us.
    }

    /**
     * Pulls an item off of the queue. This method will block until
     * something is found. This method is synchronized so it doesn't
     * interfere with the append call.
     *
     * @todo rename to take(), similar to java's BlockingQueue
     * @return The bottom object of the queue.
     */
    public synchronized Object get() throws InterruptedException {
//        try {
        while(head == null) {
            wait();
        }
//        } catch(InterruptedException e) {
//            return null;
//        }
        QueueElement p = head;
        head = head.next;
        if (head == null) {
            tail = null;
        }
        len--;
        return p.obj;
    }

    /**
     * This is called every time we timeout while waiting to append
     * something to the queue. You can use this to figure out if
     * you want to increase the queue size. A real hacker could override
     * this to keep statistics, and use the resize() function to increase
     * the size of the queue after a bunch of timeouts. NOTE - <b> DON'T </b>
     * use the resize() method from within this method - the code will
     * hang forever. You should instead flag another thread to do the
     * resize.
     *
     * @see Queue#appendTimeoutTime
     */
    public void appendTimeout() {
        // no default behavior
    }

    /**
     * Pretty much the same thing as the getTimeout() method, but for
     * blocking get() timeouts. REMEMBER: DON'T call resize() from within
     * this method.
     *
     * @see Queue#getTimeoutTime
     */
    public void getTimeout() {
        // no default behavior
    }

    /**
     * Resizes the queue so that it can contain at most the given
     * number of items in it. Note - the queue may already have more
     * than the given number of items in it, if so, nothing will be
     * allowed in the queue until it shrinks to contain fewer than the
     * new maximum number of elements in it.
     *
     * @param newsize The new maximum size of the queue
     */

    public synchronized void resize(int newsize) {
        /*
        synchronized (isNotFull) {
            if (newsize < maxcount) {    // shrinking the queue
                maxcount = newsize;

            } else if (newsize > maxcount) { // growing the queue

                if (newsize <= queuesize) {        // queue size is still bigger
                    maxcount = newsize;             // than the new size

                } else {                     // (newsize > queuesize)
                    Object[] newItems = new Object[newsize];

                    for (int x = 1; x <= count; x++) {
                        newItems[x - 1] = items[(first + x - 1) % queuesize];
                    }

                    items = newItems;
                    queuesize = newsize;
                    maxcount = newsize;
                    first = 0;
                }

                isNotFull.notify();    // let any stuck appends proceed
            }
        }
        */
        queuesize = newsize;
    }

}
