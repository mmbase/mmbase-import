/*
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.logging;

/**
 * A very simple implementation of Logger. It writes everything to
 * standard output or standard error (the configure string can contain
 * `stderr' or `stdout' (default)).  It does not know categories (and
 * therefore is a Singleton class). It is possible to configure what
 * should be logged as well (with a level-string token in the
 * configure string).
 *
 * @author  Michiel Meeuwissen
 * @version $Id: StringBufferLogger.java,v 1.2 2005-09-12 15:07:42 pierre Exp $
 * @since   MMBase-1.4
 */

public class StringBufferLogger implements Logger {

    private StringBuffer buffer = new StringBuffer();

    private int level = Level.INFO_INT;

    public StringBufferLogger() {
    }

    public StringBufferLogger(Level level) {
        this.level = level.toInt();
    }

    public void setLevel(Level p) {
        level = p.toInt();
    }

    public StringBuffer getStringBuffer() {
        return buffer;
    }

    protected final void log(String s) {
        buffer.append(s).append('\n');
    }

    protected void log(String s, Level level) {
        log(s);
    }

    /**
     * @since MMBase-1.8
     */
    protected void log (String s, Throwable t) {
        log(s + "\n"  + Logging.stackTrace(t));
    }
    /**
     * @since MMBase-1.8
     */
    protected void log(String s, Level level, Throwable t) {
        log(s, t);
    }

    public void trace (Object m) {
        if (level <= Level.TRACE_INT) {
            log("TRACE " + m,  Level.TRACE);
        }
    }
    public void trace (Object m, Throwable t) {
        if (level <= Level.TRACE_INT) {
            log("TRACE " + m,  Level.TRACE, t);
        }
    }
    public void debug (Object m) {
        if (level <= Level.DEBUG_INT) {
            log("DEBUG " + m, Level.DEBUG);
        }
    }
    public void debug (Object m, Throwable t) {
        if (level <= Level.DEBUG_INT) {
            log("DEBUG " + m, Level.DEBUG, t);
        }
    }

    public void service (Object m) {
        if (level <= Level.SERVICE_INT) {
            log("SERVICE " + m, Level.SERVICE);
        }
    }
    public void service (Object m, Throwable t) {
        if (level <= Level.SERVICE_INT) {
            log("SERVICE " + m, Level.SERVICE, t);
        }
    }
    public void info    (Object m) {
        if (level <= Level.INFO_INT) {
            log("" + m, Level.INFO);
        }
    }
    public void info    (Object m, Throwable t) {
        if (level <= Level.INFO_INT) {
            log("" + m, Level.INFO, t);
        }
    }
    public void warn    (Object m) {
        if (level <= Level.WARN_INT) {
            log("WARN " + m, Level.WARN);
        }
    }
    public void warn    (Object m, Throwable t) {
        if (level <= Level.WARN_INT) {
            log("WARN " + m, Level.WARN, t);
        }
    }
    public void error   (Object m) {
        if (level <= Level.ERROR_INT) {
            log("ERROR " + m, Level.ERROR);
        }
    }
    public void error   (Object m, Throwable t) {
        if (level <= Level.ERROR_INT) {
            log("ERROR " + m, Level.ERROR, t);
        }
    }
    public void fatal   (Object m) {
        if (level <= Level.FATAL_INT) {
            log("FATAL " + m, Level.FATAL);
        }
    }
    public void fatal   (Object m, Throwable t) {
        if (level <= Level.FATAL_INT) {
            log("FATAL " + m, Level.FATAL, t);
        }
    }

    public boolean isDebugEnabled() {
        return level <= Level.DEBUG_INT;
    }

    public boolean isServiceEnabled() {
        return level <= Level.SERVICE_INT;
    }

}
