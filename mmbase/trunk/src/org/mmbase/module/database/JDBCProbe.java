/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.database;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * JDBCProbe checks all JDBC connection every X seconds to find and
 * remove bad connections works using a callback into JDBC.
 *
 *
 * @version $Id: JDBCProbe.java,v 1.8 2003-05-03 09:49:09 kees Exp $
 * @author Daniel Ockeloen

*/
public class JDBCProbe implements Runnable {
    private static Logger log = Logging.getLoggerInstance(JDBCProbe.class.getName());
    
    private boolean mustRun = true;
    private JDBC parent=null;
    private String name;
    private String input;
    private int len;
    private long checkTime;


    public JDBCProbe(JDBC parent) {
        this(parent, 30);
    }

    public JDBCProbe(JDBC parent, int ct) {
        this.parent=parent;
        checkTime = ct * 1000;
	Thread t = new Thread(this,"JDBCProbe");
	t.setDaemon(true);
	t.start();
    }
    
    /**
     * admin probe, try's to make a call to all the maintainance calls.
     */
    public void run () {
        log.info("JDBC probe starting");
        while (mustRun) {
            try{ Thread.sleep(checkTime); } catch(InterruptedException e) { }

            try {
                parent.checkTime();
            } catch (Exception e) {
		//added logg here
            }
        }
    }
}
