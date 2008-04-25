/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
*/
package org.mmbase.security.implementation.aselect;

import org.aselect.system.logging.SystemLogger;
import java.util.logging.Logger;
import org.mmbase.util.logging.java.MMBaseLogger;

/**
 *
 * @author Michiel Meeuwissen
 */
public class MMBaseSystemLogger extends SystemLogger  {

    private static final org.mmbase.util.logging.Logger log = org.mmbase.util.logging.Logging.getLoggerInstance("org.aselect");
    private Logger javaLogger = new MMBaseLogger(log);
    
    public MMBaseSystemLogger() {
    }
    
    public void init(String xLogDir, String xLogFileNamePrefix, String xLoggerNamespace, int xFileLimit, int xNumberOfFiles) throws Exception {
        // ignored, left to mmbase configuration.
    }
    
    
    public Logger getLogger() {
        return javaLogger;
    }
    
    public void setDebug(boolean debug) {
        if (debug) {
            log.setLevel(org.mmbase.util.logging.Level.DEBUG);
        } else {
            log.setLevel(org.mmbase.util.logging.Level.SERVICE);
        }
    }

}
