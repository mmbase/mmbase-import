/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders.vwms;

import java.util.*;

import org.mmbase.util.Queue;
import org.mmbase.util.logging.*;

/**
 * A class used to schedule images for transfer.
 * ImagePusher periodically (approx. every 4 seconds) checks it's parent's ({@link ImageMaster}) filelist,
 * and pushes the fields in that list on a {@link Queue}, which is, in turn, handled by a {@link FileCopier}
 * instance.<br />
 * The reason that this is done by ImagePusher, and not ImageMaster, is that this way double requests
 * for the same file can be filtered out. This seems useful, so perhaps {@link PageMaster} might need to use this
 * scheduling system too (or otherwise this should be build into FileCopier).
 *
 * @author Rico Jansen
 * @author Pierre van Rooden (javadocs)
 * @version $Id: ImagePusher.java,v 1.11 2007-06-21 15:50:23 nklasens Exp $
 */
public class ImagePusher implements Runnable {

    private static final Logger log = Logging.getLoggerInstance(ImagePusher.class);

    /**
     * The thread reference.
     * Setting this object to null stops the thread.
     */
    Thread kicker = null;
    /**
     * Sleeptime for this thread. unused as sleeping is arranged by the Queue object.
     */
    int sleepTime=4444;
    /**
     * The class that started the pusher ({@link ImageMaster}).
     * Used to retrieve the filelist that needs to be handled.
     */
    ImageMaster parent;

    /**
    * Queue containing the file-copy tasks that need to be performed by {@link FileCopier}
    */
    Queue files2copy=new Queue(128);

    /**
     * Thread that handles the actual file transfers.
     */
    FileCopier filecopier=new FileCopier(files2copy);

    /**
     * Constructor for the ImagePusher
     * @param parent The parent that contains and adds the files to handle.
     */
    public ImagePusher(ImageMaster parent) {
        this.parent=parent;
        init();
    }

    /**
     * Initializes the insatnce by starting a thread.
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
            kicker = new Thread(this,"Image");
            kicker.setDaemon(true);
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
     */
    public void run () {
        if (kicker!=null) {
            try {
                doWork();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Main work loop.
     * Periodically checks the files field of its parent. If new files need to be transferred,
     * the files in the parent are cleared. The original list is then purged of duplicates
     * before they are stored in the Queue. A seperate thread ({@link FileCopier}) then takes care
     * of the actual transfer.
     */
      public void doWork() {
        Vector<aFile2Copy> procfiles;
        aFile2Copy file;
        Hashtable files;

        log.info("ImagePusher Active");
        while (kicker!=null) {
            try { Thread.sleep(sleepTime); } catch (InterruptedException e) {}
            if (parent.files.size()>0) {
                synchronized(parent.files) {
                    procfiles=parent.files;
                    parent.files=new Vector<aFile2Copy>();
                }
                log.service("ImagePusher processing "+procfiles.size()+" files");
                files=killdups(procfiles);
                for (Enumeration<aFile2Copy> e =files.keys();e.hasMoreElements();) {
                    file=e.nextElement();
                    files2copy.append(file);
                }
            }
        }
    }

    /**
     * Removes duplicates from a list of files.
     * @param Vector files that need to be checked for duplicates
     * @return a <code>Hashtable</code> whose keys contain the files (without duplicates).
     * Would probably be a bit more clear if the keys are returned instead.
     */
    private Hashtable killdups(Vector<aFile2Copy> files) {
        Hashtable hfiles=new Hashtable(files.size());
        aFile2Copy file;
        for (Enumeration<aFile2Copy> e=files.elements();e.hasMoreElements();) {
            file=e.nextElement();
            hfiles.put(file,"feep");
        }
        log.info("ImagePusher -> "+files.size()+" - "+hfiles.size()+" dups "+(files.size()-hfiles.size()));
        return hfiles;
    }
}
