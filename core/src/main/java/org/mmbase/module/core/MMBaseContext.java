/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;
import org.mmbase.core.util.DaemonTask;
import org.mmbase.core.util.DaemonThread;

/**
 * @version $Id$
 * @deprecated Use org.mmbase.util.MMBaseContext
 */
public class MMBaseContext extends org.mmbase.util.MMBaseContext {

    /**
     * Starts a daemon thread using the MMBase thread group.
     * @param task the task to run as a thread
     * @param name the thread's name
     * @deprecated   Use {@link org.mmbase.util.ThreadPools#scheduler}.
     * @since MMBase-1.8
     */
    public static DaemonThread startThread(DaemonTask task, String name) {
        DaemonThread kicker = new DaemonThread(name);
        kicker.setTask(task);
        kicker.start();
        return kicker;
    }

}
