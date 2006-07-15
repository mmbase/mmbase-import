/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.util.*;
import java.text.*;
import org.mmbase.util.logging.*;

/**
 * Class with support for creation of date strings in GMT format.
 * Date strings in this format are used in http headers.
 *
 */
public class RFC1123 {

    //logger
    static Logger log = Logging.getLoggerInstance(RFC1123.class.getName());


    private static DateFormat formatter; 

    static {
        formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    /**
     * Abbreviated names of week days
     * @deprecated
     */
    private static String days[]={ "Sun, ","Mon, ","Tue, ","Wed, ","Thu, ","Fri, ","Sat, ","Sun, " };

    /**
     * Create a date string in GMT format.
     * Uses basic date functionality
     * @deprecated use {@link #makeDate} instead
     */
    public static String makeDateV1(Date d) {
        return days[d.getDay()]+d.toGMTString();
    }
    /**
     * Create a date string in GMT format.
     * @deprecated use {@link #makeDate} instead
     */
    public static String makeDateV2(Date d) {
        return makeDate(d);
    }

    /**
     * Create a date string in GMT format.
     */
    public static String makeDate(Date d) {
        return formatter.format(d);
    }

    
    /**
     * Method to call this class from the commandline for testing.
     */
    public static void main(String args[]) {
        log.info("Date "+makeDateV1(new Date()));
        log.info("Date "+makeDateV2(new Date()));
        log.info("Date(corr) "+makeDateV1(new Date(DateSupport.currentTimeMillis())));
        log.info("Date(corr) "+makeDateV2(new Date(DateSupport.currentTimeMillis())));
    }

}
