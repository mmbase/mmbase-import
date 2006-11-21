/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.servlet;

import org.mmbase.module.core.MMBase;
import org.mmbase.module.Module;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;
import javax.servlet.ServletException;

/**
 * Seperate thread to init MMBase. This is because init() of Servlets and Filters must take little
 * time, to not hold other web-apps.  Init of MMBase may take indefinitely if e.g. the database is down.
 *
 * @version $Id: MMBaseStartThread.java,v 1.6 2006-11-21 16:10:28 michiel Exp $
 * @author Michiel Meeuwissen
 * @since MMBase-1.7
 */
public class MMBaseStartThread extends Thread {

    private   static final Logger log = Logging.getLoggerInstance(MMBaseStartThread.class);
    public MMBaseStartThread(MMBaseStarter s) {
        super(new Job(s), "MMBase Start Thread");
        setDaemon(true); // if init never ends, don't hinder destroy
    }


    /**
     * @since MMBase-1.9
     */
    public static class Job implements Runnable {
        private final MMBaseStarter starter;
        public Job(MMBaseStarter s) {
            starter = s;
        }

        public void run() {
            synchronized(Module.class) {
                if (starter.getMMBase() == null) {
                    try {
                        MMBase mmb = MMBase.getMMBase();
                        if (mmb == null) {
                            throw new Exception("getMMBase gave null");
                        }
                        starter.setInitException(null); // no error.
                        starter.setMMBase(mmb);
                    } catch (Throwable e) {
                        log.fatal("Could not instantiate the MMBase module! " + e.getClass().getName() + " " + e.getMessage());
                        log.error(e);
                        starter.setInitException(new ServletException(e));
                    }
                }
            }
        }
    }

}
