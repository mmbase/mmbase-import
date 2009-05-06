/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.util.*;
import java.util.concurrent.*;
import javax.naming.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * @javadoc
 *
 * @author Nico Klasens
 * @since MMBase 1.8.1
 * @version $Id$
 */
public class ApplicationContextReader {

    private static Logger log = Logging.getLoggerInstance(ApplicationContextReader.class);

    private static final Map<String, Map<String, String>> cache = new ConcurrentHashMap<String, Map<String, String>>();


    /**
     * As {@link #getProperties(String)} but caching, so it may conserve some cpu cycles, and
     * withouth throwing the exception
     *
     * @since MMBase-1.8.7
     */
    public static Map<String, String> getCachedProperties(String path) {
        Map<String, String> m = cache.get(path);
        if (m == null) {
            try {
                m = getProperties(path);
            } catch (javax.naming.NamingException ne) {
                log.error(ne);
                m =  Collections.emptyMap();
            }
            cache.put(path, m);
        }
        return m;
    }

    /**
     * @javadoc
     */
    public static Map<String, String> getProperties(String path) throws NamingException {
        if (path == null || "".equals(path)) {
            throw new IllegalArgumentException("Path is empty");
        }
        Map<String, String> properties = new HashMap<String, String>();
        Context env = getContext();
        if (env != null) {
            NamingEnumeration<NameClassPair> ne = env.list(path);
            while (ne.hasMoreElements()) {
                NameClassPair element = ne.nextElement();
                String contextName = element.getName();

                String lookupName = env.composeName(contextName, path);
                Object value = env.lookup(lookupName);
                if (value instanceof Context) {
                    Map<String, String> subProps = getProperties(path + "/" + contextName);
                    for (Map.Entry<String, String> entry : subProps.entrySet()) {
                        properties.put(contextName + "/" + entry.getKey(), entry.getValue());
                    }
                } else {
                    properties.put(contextName, value.toString());
                }
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
