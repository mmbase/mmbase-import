/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders.vwms;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

import org.mmbase.util.*;
import org.mmbase.util.logging.*;

/**
 * A background process that registers pagechanges and pushes
 * for reloads of those pages, for proxy caching (and mirroring).
 *
 * @author Rico Jansen
 * @author Pierre van Rooden (javadocs)
 * @version 10 Apr 2001
 */
public class JudasURLpusher implements Runnable {
    //Logger
    private static Logger log = Logging.getLoggerInstance(JudasURLpusher.class.getName());

    /**
     * The thread reference.
     * Setting this object to null stops the thread.
     */
    Thread kicker = null;
    /**
     * Sleeptime for this thread. set to 5 seconds
     */
    int sleepTime=5000;
    /**
     * Instance that started the thread
     */
    Judas parent;

    /**
     * Table of urls scheduled for relaod.
     * This fast-access list is used to remove duplicates to boost performance.
     */
    Hashtable priurls=new Hashtable();
    /**
     * List of urls scheduled for reload, ordered by priority.
     */
    SortedVector prilist=new SortedVector();

    /**
     * Constructor of the URL pusher class.
     * @param parent the instance that starts the class
     */
    public JudasURLpusher(Judas parent) {
        this.parent=parent;
        init();
    }

    /**
     * Intializes the class.
     * Starts the thread for this class.
     */
    public void init() {
        this.start();
    }

    /**
     * Starts the main Thread.
     */
    public void start() {
        /* Start up the main thread */
        if (kicker == null) {
            kicker = new Thread(this,"Judas");
            kicker.start();
        }
    }

    /**
     * Stops the main Thread.
     */
    public void stop() {
        /* Stop thread */
        kicker = null;
    }

    /**
     * Main loop, exception protected
     * @see #doWork
     */
    public void run () {
        while (kicker!=null) {
            try {
                doWork();
            } catch(Exception e) {
                log.error(e.getMessage());
                log.error(Logging.stackTrace(e));
            }
        }
    }

    /**
     * Main work loop.
     * Periodically checks the list of urls to be reloaded.
     * If any urls with a maximum priority exist, they are pushed to be reloaded.
     * After this, the priority of all remaining urls are increased by one (to be scheduled for
     * handling next time).
     */
    public void doWork() {
        PriorityURL priurl;

        log.info("Active");
        while (kicker!=null) {
            try { Thread.sleep(sleepTime); } catch (InterruptedException e) {}
            synchronized(priurls) {
                log.debug("Current urllist size "+prilist.size()+"=="+priurls.size());
                if (prilist.size()>0) {
                    do {
                        priurl=(PriorityURL)prilist.firstElement();
                        log.debug("PriURL : "+priurl);
                        if (priurl.getPriority()==PriorityURL.MAX_PRIORITY) {
                            parent.pushReload(priurl.getURL());
                            prilist.removeElementAt(0);
                            priurls.remove(priurl.getURL());
                        }
                    } while (prilist.size()>0 && priurl.getPriority()==PriorityURL.MAX_PRIORITY);

                    for (Enumeration e=prilist.elements();e.hasMoreElements();) {
                        priurl=(PriorityURL)e.nextElement();
                        priurl.increasePriority();
                    }
                }
            }
        }
    }

    /**
     * Adds a url to be scheduled for reload.
     * The url has to be a relative url eg. /3voor12/bla/index.shtml?123+456 .
     * If no parameters are given, the url STILL has to have a '?' character eg. /3voor12/test.shtml?
     * Uses default priority for scheduling reload.
     * The urls are added to a queue that is periodically emptied by the workloop.
     * Any duplicate urls are removed from the queue to improve performance.
     * @param url the url to reload
     */
    public void addURL(String url) {
        addURL(url,PriorityURL.DEF_PRIORITY);
    }

    /**
     * Adds a url to be scheduled for reload.
     * The url has to be a relative url eg. /3voor12/bla/index.shtml?123+456 .
     * If no parameters are given, the url STILL has to have a '?' character eg. /3voor12/test.shtml?
     * The urls are added to a queue that is periodically emptied by the workloop.
     * Any duplicate urls are removed from the queue to improve performance.
     * @param url the url to reload
     * @param priority priority at which the url needs to be reloaded
     */
    public void addURL(String url,int priority) {
        PriorityURL priurl;

        synchronized(priurls) {
            priurl=(PriorityURL)priurls.get(url);
            if (priurl!=null) {
                log.debug("URL already in queue "+priurl);
                if (priurl.getPriority()<priority) {
                    priurl.setPriority(priority);
                    prilist.Sort();
                }
            } else {
                priurl=new PriorityURL(url,priority);
                priurls.put(url,priurl);
                prilist.addSorted(priurl);
            }
        }
    }
}
