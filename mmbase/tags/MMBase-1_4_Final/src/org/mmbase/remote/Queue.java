/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/*
$Id: Queue.java,v 1.7 2001-04-20 14:15:49 michiel Exp $

$Log: not supported by cvs2svn $
Revision 1.6  2001/04/13 13:47:05  michiel
michiel: new logging system, indentation

Revision 1.5  2000/12/19 13:33:38  vpro
Davzev: Added cvs comments

*/
package org.mmbase.remote;

import java.util.*;
import java.lang.*;

//import org.mmbase.util.logging.Logger;
//import org.mmbase.util.logging.Logging;

/**
 * @version $Revision: 1.7 $ $Date: 2001-04-20 14:15:49 $
 * @author ?
 */
public class Queue { 
    //Logging removed automaticly by Michiel, and replace with __-methods
    private static String __classname = Queue.class.getName();


    boolean __debug = false;
    private static void __debug(String s) { System.out.println(__classname + ":" + s); }
    //private static Logger log = Logging.getLoggerInstance(Queue.class.getName());

    QueueElement head,tail;
    int flip=0;int len=0;

    //private Object[] items;
    //public Object[] items;
    public Vector items;
    //private Object isNotFull = new Object(); 

    private int first;   // pointer to last item in queue
    private int count;   // current # of items in queue
    private int maxcount;  // max # of items to be allowed in queue
    private int queuesize;  // actual size of items[] array
    public int appendTimeoutTime; // how long to wait for an append() or get()
    public int getTimeoutTime; // before checking to see if we should give up
    long get1,get2;;
    
    /**
     * Default size of 32 for the queue if none is specified.
     */
    public static int DEFAULT_QUEUE_SIZE = 32; // seems reasonable, I guess

    /**
     * Default timeout of 0 for a blocking append call
     */ 
    public static int DEFAULT_APPEND_TIMEOUT = 0; // by default we don't care about timeouts

    /**
     * Default timeout of 0 for a blocking get call
     */
    public static int DEFAULT_GET_TIMEOUT = 0; // by default we don't care about timeouts

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
     *           the appendTimeout() method is called before retrying.
     * @param getTimeout If we can't get() something within this many
     *          milliseconds, the getTimeout() method is called.
     * @see Queue#appendTimeout
     * @see Queue#getTimeout
     */
    public Queue(int size, int appendTimeout, int getTimeout) {

        appendTimeoutTime = appendTimeout;
        getTimeoutTime = getTimeout;

        queuesize = size;  // initial size of the queue
        items = new Vector(queuesize);

        first = count = 0;
        maxcount = size;  // max elements in queue
    }

    public void newQueue(int size, int appendTimeout, int getTimeout) {

        appendTimeoutTime = appendTimeout;
        getTimeoutTime = getTimeout;

        queuesize = size;  // initial size of the queue
        items = new Vector(queuesize);

        first = count = 0;
        maxcount = size;  // max elements in queue
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
        return(len); 
    }

    /** 
     * Appends the given item to the queue. This method calls a
     * synchronized append method, so that it won't interfere with a get
     * call. The method will block if the queue is full, and it won't
     * block otherwise.
     *
     * @param item The item to be appended to the queue */
    public synchronized void append(Object item) {
        if (__debug) {
            /*log.debug*/__debug("Queue:put(): Appending item:"+item);
        }
        // put a object in the vector and wait on it
        // it should be able to block if full
        QueueElement p=new QueueElement();
        p.obj=item;

        if (tail==null) {
            head=p; 
        } else {
            tail.next=p;
        } 
        p.next=null;
        tail=p;
        len++;
        flip++;
        if (flip>99) {
            ///*log.debug*/__debug("Qeueu len="+len+" ("+this+")");
            flip=0;
        }
        ///*log.debug*/__debug("Qeueu len="+len+" ("+this+")");
        notify(); // scream that a new one has reached us.
    }

    /**
  * Pulls an item off of the queue. This method will block until
  * something is found. This method is synchronized so it doesn't
  * interfere with the append call.
  *
  * @return The bottom object of the queue. 
  */

    public synchronized Object get() {
        if (__debug) {
            /*log.debug*/__debug("Queue:get(): Pulling item off of queue");
        }
        try {
            while(head==null) {
                wait();
            }
        } catch(InterruptedException e) {
            return(null);
        }
        QueueElement p=head;
        head=head.next;
        if (head==null) {
            tail=null;
        }
        len--;
        return(p.obj);
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
          if (newsize < maxcount) { // shrinking the queue
          maxcount = newsize;
 
          } else if (newsize > maxcount) { // growing the queue
 
          if (newsize <= queuesize) {  // queue size is still bigger 
          maxcount = newsize;    // than the new size
  
          } else {      // (newsize > queuesize)
          Object[] newItems = new Object[newsize];
  
          for (int x = 1; x <= count; x++) {
          newItems[x - 1] = items[(first + x - 1) % queuesize];
          }
  
          items = newItems;
          queuesize = newsize;
          maxcount = newsize;
          first = 0;
          }

          isNotFull.notify(); // let any stuck appends proceed
          }
          }
        */
        queuesize = newsize;
        maxcount = newsize;
    }
 
}
