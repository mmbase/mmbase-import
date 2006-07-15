/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.database;

import org.mmbase.module.core.MMBaseContext;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * JDBCProbe checks all JDBC connection every X seconds to find and
 * remove bad connections works using a callback into JDBC.
 *
 *
 * @version $Id: JDBCProbe.java,v 1.9.2.1 2005-11-28 18:41:40 pierre Exp $
 * @author Daniel Ockeloen

*/
public class JDBCProbe implements Runnable {
    private static final Logger log = Logging.getLoggerInstance(JDBCProbe.class);
    
    private JDBC parent = null;
    private long checkTime;


    public JDBCProbe(JDBC parent) {
        this(parent, 30);
    }

    public JDBCProbe(JDBC parent, int ct) {
        this.parent = parent;
        checkTime = ct * 1000;
	MMBaseContext.startThread(this, "JDBCProbe");
    }
    
    /**
     * admin probe, try's to make a call to all the maintainance calls.
     */
    public void run () {
        log.info("JDBC probe starting");
        while (true) {
            try { 
                Thread.sleep(checkTime);
            } catch(InterruptedException e) { 
                log.info("Interrupted " + e.getMessage());
                return;
            }

            try {
                parent.checkTime();
            } catch (Exception e) {
                log.error(e.getMessage());
                
            }
        }
    }
}
