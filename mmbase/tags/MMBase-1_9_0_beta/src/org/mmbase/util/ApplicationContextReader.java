/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.util.*;
import javax.naming.*;

/**
 * @javadoc
 *
 * @author Nico Klasens
 * @since MMBase 1.8.1
 * @version $Id: ApplicationContextReader.java,v 1.4 2007-02-24 21:57:50 nklasens Exp $
 */
public class ApplicationContextReader {

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
                String value = env.lookup(lookupName).toString();
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
