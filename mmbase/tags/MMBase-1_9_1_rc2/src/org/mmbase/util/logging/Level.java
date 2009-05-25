/*
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.logging;
/**
 * This class is a kind of `enum' type, for logging priorities. It has
 * static instances and only a private constructor. And a function to
 * translate to an int, which is handy for use in a switch.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */

public final class Level implements java.io.Serializable {

    /**
     * A possible result of {@link #toInt}
     */

    public final static int
        TRACE_INT   = 5000,
        DEBUG_INT   = 10000,
        SERVICE_INT = 15000,
        INFO_INT    = 20000,
        WARN_INT    = 30000,
        ERROR_INT   = 40000,
        FATAL_INT   = 50000,
        OFF_INT     = Integer.MAX_VALUE;


    /**
     * A constant. Main use is for the method {@link Logger#setLevel}
     */
    public final static Level
        TRACE   = new Level(TRACE_INT, "TRACE"),
        DEBUG   = new Level(DEBUG_INT, "DEBUG"),
        SERVICE = new Level(SERVICE_INT, "SERVICE"),
        INFO    = new Level(INFO_INT, "INFO"),
        WARN    = new Level(WARN_INT, "WARN"),
        ERROR   = new Level(ERROR_INT, "ERROR"),
        FATAL   = new Level(FATAL_INT, "FATAL"),
        OFF     = new Level(OFF_INT, "OFF");

    private int level;
    private String string;

    private Level(int p, String s) {
        level = p;
        string = s;
    }

    public static Level toLevel(String level) {

        String s = level.toUpperCase();
        if (s.equals("TRACE") )   return TRACE;
        if (s.equals("DEBUG") )   return DEBUG;
        if (s.equals("SERVICE") ) return SERVICE;
        if (s.equals("INFO") )    return INFO;
        if (s.equals("WARN") )    return WARN;
        if (s.equals("ERROR") )   return ERROR;
        if (s.equals("FATAL") )   return FATAL;
        if (s.equals("OFF") )     return OFF;

        return DEBUG;

    }

    /**
     * Makes an integer from this object.
     */
    public final int toInt() {
        return level;
    }

    public final String toString() {
        return string;
    }

}
