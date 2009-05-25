/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.applications.mmbob;

import java.lang.Thread;
import org.mmbase.util.logging.*;


/**
 * This hook will be used by the jvm and tries to sync all MMBob nodes before shutting down
 *
 * @author Gerard van Enk
 * @version $Id: ForumMMBaseSyncerShutdown.java,v 1.1 2005-02-22 15:34:14 gerard Exp $
 */
public class ForumMMBaseSyncerShutdown extends Thread {
    /**
     * The ForumMMBaseSyncer to be used for syncing when the jvm goes down
     */
    private ForumMMBaseSyncer managedClass;

    /**
     * instantiaties the shutdown hook with the Syncer.
     *
     * @param managedClass The ForumMMBaseSyncer to be used for syncing when the jvm goes down
     */
    public ForumMMBaseSyncerShutdown(ForumMMBaseSyncer managedClass) {
        super();
        this.managedClass = managedClass;
    }


    /**
     * runs when jvm goes down and tries to sync the MMBob nodes
     */
    public void run() {
        //cannot use logging, because it may already have been shutdown
        System.out.println("ForumMMBaseSyncerShutdown thread started");
        try {
            managedClass.shutdownSync();
        } catch (Exception ee) {
            //cannot use logging, because it may already have been shutdown
            System.out.println(ee.getMessage());
        }
    }
}
