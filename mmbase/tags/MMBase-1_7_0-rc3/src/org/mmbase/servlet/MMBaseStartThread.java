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
 * @version $Id: MMBaseStartThread.java,v 1.3 2004-03-03 22:31:52 michiel Exp $
 * @author Michiel Meeuwissen
 * @since MMBase-1.7
 */
class MMBaseStartThread extends Thread {

    private   static final Logger log = Logging.getLoggerInstance(MMBaseStartThread.class);
    private MMBaseStarter starter;
    MMBaseStartThread(MMBaseStarter s) {
        super("MMBase Start Thread");
        starter = s;
        setDaemon(true); // if init never ends, don't hinder destroy
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
                    log.fatal("Could not find the MMBase module!" + e.getMessage());
                    starter.setInitException(new ServletException(e));
                }
            }
            
        }
    }
    
}
