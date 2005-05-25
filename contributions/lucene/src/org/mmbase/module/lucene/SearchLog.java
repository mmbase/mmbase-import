/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.lucene;

import org.mmbase.util.logging.*;

/**
 * This simpel utility class is used to log the values being searched by the lucene logger.
 * Using a separate (static) class makes it easy to make a separate log (with separate templates) for values being searched.
 *
 * @author Pierre van Rooden
 * @version $Id: SearchLog.java,v 1.1 2005-05-25 08:53:56 pierre Exp $
 **/
public class SearchLog {

    private static final Logger log = Logging.getLoggerInstance(SearchLog.class);

    public static void log(String value) {
        log.service(value);
    }

}
