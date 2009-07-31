/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders.vwms;

import org.mmbase.module.core.MMBaseContext;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;

/**
 * A background process that starts file copy commands.
 * The process uses a {@link Queue} that holds {@link aFile2Copy} objects.
 * The copier is started by file transfer VWMs such as PageMaster, who maintain
 * the queue by filling it with information on files to transfer.
 * FileCopier does not copy files itself, instead, it starts a {@link SCPcopy} class for each
 * file to be copied.
 *
 * @author Rico Jansen
 * @author Pierer van Rooden (javadocs)
 */
public class FileCopier implements Runnable {

    // logger
    private static Logger log = Logging.getLoggerInstance(FileCopier.class.getName());

    /**
     * The thread reference.
     * Setting this object to null stops the thread.
     */
    Thread kicker = null;
    /**
     * Sleeptime for this thread. unused as sleeping is arranged by the Queue object.
     */
    int sleepTime=8888;
    /**
     * Queue of {@link aFile2Copy} objects that need to be handled.
     */
    Queue files;

    /**
     * Constructor for the Filecopier
     * @param files the {@link Queue} that holds (or will hold) the files to copy.
     *      The queue is maintained by the calling class.
     */
    public FileCopier(Queue files) {
        this.files=files;
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
            kicker = MMBaseContext.startThread(this,"FileCopier");
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
     * Main loop, exception-protected.
     * @see #doWork
     */
    public void run () {
        if (kicker!=null) {
            try {
                doWork();
            } catch(Exception e) {
                log.error(Logging.stackTrace(e));
            }
        }
    }

    /**
     * Main work loop.
     * Checks the Queue, and if a new object is available, creates a new {@link SCPcopy} object to
     * handle the transfer.
     */
    public void doWork() {
        //String sshpath="/sr/local/bin";
        aFile2Copy afile;

        log.debug("Active");
        while (kicker!=null) {
            try {
                afile=(aFile2Copy)files.get();
                if (afile!=null) {
                    log.info("Copying "+afile.srcpath+"/"+afile.filename);
                    SCPcopy scpcopy=new SCPcopy(afile.sshpath,afile.dstuser,afile.dsthost,afile.dstpath);
                    scpcopy.copy(afile.srcpath,afile.filename);
                } else {
                    log.error("afile is null ?");
                }
            } catch (InterruptedException e) {
                log.debug(Thread.currentThread().getName() +" was interruped.");
                break;
            }
        }
    }
}
