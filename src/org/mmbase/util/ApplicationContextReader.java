/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.util.*;

import javax.naming.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


public class ApplicationContextReader {

    /** MMbase logging system */
    private static Logger log = Logging.getLoggerInstance(ApplicationContextReader.class.getName());
    
    public static Map getProperties(String path) {
        if (path == null || "".equals(path)) {
            throw new IllegalArgumentException("Path is empty");
        }
        Map properties = new HashMap();
        Context env = getContext();
        if (env != null) {
            try {
                NamingEnumeration ne = env.list(path);
                while (ne.hasMoreElements()) {
                    NameClassPair element = (NameClassPair) ne.nextElement();
                    String contextName = element.getName();
                    String lookupName = env.composeName(contextName, path);
                    Object value = env.lookup(lookupName);
                    properties.put(contextName, value);
                }
            }
            catch (NamingException e) {
                log.warn("" + e.getMessage(), e);
            }
        }
        return properties;
    }

    public static Context getContext() {
        try {
            InitialContext context = new InitialContext();
            return (Context) context.lookup("java:comp/env");
         }
         catch (NamingException ne) {
            log.debug("Error looking up server/LiveOrStaging", ne);
         }
         return null;
    }
    
}
