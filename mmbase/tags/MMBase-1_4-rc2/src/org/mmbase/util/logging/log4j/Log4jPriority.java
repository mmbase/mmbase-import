
/*
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.logging.log4j;
import  org.apache.log4j.Priority;

/** 
 *    LoggerPriority The new Priority class for Log4jImpl. It extends
 *    the log4j Priority with 2 extra priorities, namely `SERVICE' and
 *    `TRACE'.
 *
 * @author Michiel Meeuwissen
 **/

public class Log4jPriority extends Priority {


    final static int SERVICE_INT   = 15000;
    final static int TRACE_INT     = 5000;

    // FATAL          (from log4j.Priority)
    // ERROR
    // WARN
    // INFO
    public static final Log4jPriority SERVICE = new Log4jPriority(SERVICE_INT, "SERVICE", 5);
    // DEBUG    
    public static final Log4jPriority TRACE   = new Log4jPriority(TRACE_INT,   "TRACE",   7);


    protected  Log4jPriority(int level, String strLevel, int syslogEquiv) {
        super(level, strLevel, syslogEquiv);
    }

    public static  Priority toPriority(String sArg) {
        if(sArg == null)
            return Log4jPriority.TRACE;

        String stringVal = sArg.toUpperCase();

        if(stringVal.equals("TRACE"))   return Log4jPriority.TRACE;
        if(stringVal.equals("SERVICE")) return Log4jPriority.SERVICE;
        return Priority.toPriority(sArg);
    }
        
    public static Priority toPriority(int i) throws  IllegalArgumentException {
        switch(i) {
        case TRACE_INT:   return Log4jPriority.TRACE;
        case SERVICE_INT: return Log4jPriority.SERVICE;
        }
        return Priority.toPriority(i);
    }
    
    public static Priority[] getAllPossibleLog4jPriorities() {
        return new Priority[] {FATAL, ERROR, WARN, INFO, SERVICE, DEBUG, TRACE};
    }
   
    public static Priority toLog4jPriority(String sArg) { // needed?
        Priority result;
        result = Priority.toPriority(sArg, null);
        if (result != null) {
            return result;
        }
        String s = sArg.toUpperCase();
        if (s.equals("SERVICE")) return SERVICE;
        if (s.equals("TRACE"))   return TRACE;
        return DEBUG;
    }

}
