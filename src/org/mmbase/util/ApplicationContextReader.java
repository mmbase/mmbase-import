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

/**
 * @javadoc
 *
 * @author Nico Klasens
 * @since MMBase 1.8.1
 * @version $Id: ApplicationContextReader.java,v 1.2 2006-07-06 14:34:22 pierre Exp $
 */
public class ApplicationContextReader {

    private static Logger log = Logging.getLoggerInstance(ApplicationContextReader.class);

    /**
     * @javadoc
     */
    public static Map getProperties(String path) throws NamingException {
        if (path == null || "".equals(path)) {
            throw new IllegalArgumentException("Path is empty");
        }
        Map properties = new HashMap();
        Context env = getContext();
        if (env != null) {
            NamingEnumeration ne = env.list(path);
            while (ne.hasMoreElements()) {
                NameClassPair element = (NameClassPair) ne.nextElement();
                String contextName = element.getName();
                String lookupName = env.composeName(contextName, path);
                Object value = env.lookup(lookupName);
                properties.put(contextName, value);
            }
        }
        return properties;
    }

    /**
     * @javadoc
     */
    public static Context getContext() throws NamingException {
        InitialContext context = new InitialContext();
        return (Context) context.lookup("java:comp/env");
    }

}
