/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.tests;
import junit.framework.TestCase;
import java.io.File;
import org.mmbase.util.logging.Logging;


/**
 * This class contains static methods for MMBase tests.
 * 
 * @author Michiel Meeuwissen
 */
public abstract class MMBaseTest extends TestCase {

    public MMBaseTest() {
        super();
    }
    public MMBaseTest(String name) {
        super(name);
    }

    /**
     * If your test needs a running MMBase. Call this.
     */
    static public void startMMBase() throws Exception {
        org.mmbase.module.core.MMBaseContext.init();
        org.mmbase.module.core.MMBase.getMMBase();
    }
    /**
     * If no running MMBase is needed, then you probably want at least to initialize logging.
     */
    static public void startLogging() throws Exception {
        startLogging("log.xml");
    }
    /**
     * If no running MMBase is needed, then you probably want at least to initialize logging.
     */
    static public void startLogging(String configure) throws Exception {
        Logging.configure(System.getProperty("mmbase.config") + File.separator + "log" + File.separator + configure);
    }

}
