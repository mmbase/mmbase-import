/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package org.mmbase.util.logging.commons;

import java.util.*;


import org.apache.commons.logging.LogFactory;

/**
 * Commons logging for MMBase
 * 
 * @author Wouter Heijke
 * @version $Id$
 */
public class CommonsLogFactory {

    private static Map<String, CommonsLog> logInstances = new Hashtable<String, CommonsLog>();

    private CommonsLogFactory() {
        System.out.println("CommonsLogger");
    }

    public static CommonsLog getLoggerInstance(String name) {
        CommonsLog logInstance = logInstances.get(name);
        if (logInstance != null) {
            return logInstance;
        }

        logInstance = new CommonsLog(LogFactory.getLog(name));

        logInstances.put(name, logInstance);
        return logInstance;
    }
}
