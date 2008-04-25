/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

/*
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.logging.log4j;
import  org.apache.log4j.Level;
import  org.apache.log4j.Priority;

/** 
 *    LoggerLevel The new Level class for Log4jImpl. It extends
 *    the log4j Level with 2 extra levels, namely `SERVICE' and
 *    `TRACE'.
 *
 * @author Michiel Meeuwissen
 **/

public class Log4jLevel extends Level {


    final static int SERVICE_INT   = 15000;
    final static int TRACE_INT     = 5000;

    // OFF            (from log4j.Level)
    // FATAL
    // ERROR
    // WARN
    // INFO
    public static final Log4jLevel SERVICE = new Log4jLevel(SERVICE_INT, "SERVICE", 5);
    // DEBUG    
    public static final Log4jLevel TRACE   = new Log4jLevel(TRACE_INT,   "TRACE",   7);


    protected  Log4jLevel(int level, String strLevel, int syslogEquiv) {
        super(level, strLevel, syslogEquiv);
    }

    public static  Level toLevel(String sArg) {
        if(sArg == null)
            return Log4jLevel.TRACE;

        String stringVal = sArg.toUpperCase();

        if(stringVal.equals("TRACE"))   return Log4jLevel.TRACE;
        if(stringVal.equals("SERVICE")) return Log4jLevel.SERVICE;
        return Level.toLevel(sArg);
    }
        
    public static Level toLevel(int i) throws  IllegalArgumentException {
        switch(i) {
        case TRACE_INT:   return Log4jLevel.TRACE;
        case SERVICE_INT: return Log4jLevel.SERVICE;
        default:
            return Level.toLevel(i);
        }
    }
    
    public static Priority[] getAllPossibleLog4jPriorities() {
        return new Priority[] {OFF, FATAL, ERROR, WARN, INFO, SERVICE, DEBUG, TRACE};
    }
   
    public static Level toLog4jLevel(String sArg) { // needed?
        Level result;
        result = Level.toLevel(sArg, null);
        if (result != null) {
            return result;
        }
        String s = sArg.toUpperCase();
        if (s.equals("SERVICE")) return SERVICE;
        if (s.equals("TRACE"))   return TRACE;
        return DEBUG;
    }

}
