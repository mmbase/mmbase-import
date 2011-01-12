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
    private static final long serialVersionUID = 0L;

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

    /**
     * @since MMBase-2.0
     */
    public static Level[] getLevels() {
        return new Level[] { TRACE, DEBUG, SERVICE, INFO, WARN,ERROR, FATAL, OFF};
    }

    private int level;
    private String string;

    private Level(int p, String s) {
        level = p;
        string = s;
    }

    public static Level toLevel(String level) {

        String s = level.toUpperCase();
        if ("TRACE".equals(s))   return TRACE;
        if ("DEBUG".equals(s))   return DEBUG;
        if ("SERVICE".equals(s)) return SERVICE;
        if ("INFO".equals(s))    return INFO;
        if ("WARN".equals(s))    return WARN;
        if ("ERROR".equals(s))   return ERROR;
        if ("FATAL".equals(s))   return FATAL;
        if ("OFF".equals(s))     return OFF;

        return DEBUG;

    }

    /**
     * Like valueOf of real enumerations.
     * @since MMBase-1.9.1
     */
    public static Level valueOf(String level) {
        return toLevel(level);
    }


    /**
     * Makes an integer from this object.
     */
    public int toInt() {
        return level;
    }

    @Override
    public String toString() {
        return string;
    }

}
