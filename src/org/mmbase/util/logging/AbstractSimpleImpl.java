/*
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.logging;

/**
 * Base class for simple Logger implementations (no patterns and so
 * on). A static protected level is provided, so even all categories has to be the same.
 *
 *
 * @author Michiel Meeuwissen 
 * @since  MMBase-1.7
 */

abstract public class AbstractSimpleImpl  {

    // could of cause be generalized to an abstract 'getLevel()', but this would 
    // nog be too good for performance I think.
    protected static int  level = Level.INFO_INT;


    /**
     * @deprecated use setLevel
     */
    public void setPriority(Level p) {
        setLevel(p);
    }
    public void setLevel(Level p) {
        level = p.toInt();
    }

    abstract public void log (String s);

    public void trace (Object m) {
        if (level <= Level.TRACE_INT) {
            log("TRACE " + m);
        }
    }
    public void debug (Object m) {
        if (level <= Level.DEBUG_INT) {
            log("DEBUG " + m);
        }
    }

    public void service (Object m) {
        if (level <= Level.SERVICE_INT) {
            log("SERVICE " + m);
        }
    }
    public void info    (Object m) {
        if (level <= Level.INFO_INT) {
            log("INFO " + m);
        }
    }
    public void warn    (Object m) {
        if (level <= Level.WARN_INT) {
            log("WARN " + m);
        }
    }
    public void error   (Object m) {
        if (level <= Level.ERROR_INT) {
            log("ERROR " + m);
        }
    }
    public void fatal   (Object m) {
        if (level <= Level.FATAL_INT) {
            log("FATAL " + m);
        }
    }

    public boolean isDebugEnabled() {
        return (level <= Level.DEBUG_INT);
    }
    public boolean isServiceEnabled() {
        return (level <= Level.SERVICE_INT);
    }

}
