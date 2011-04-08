/*
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.logging.log4j;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Level;
import java.util.*;

/**
 * Wraps a logger instance. This can be used for static logger instances which might be instatatied
 * before logging itself is configured. After configurating logging, all static 'wrappers' can then
 * be called to wrap another logger instance.
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-2.0
 * @version $Id$
 **/

public class Log4jWrapper implements Logger {


    private final org.apache.log4j.Logger  log;

    public Log4jWrapper(org.apache.log4j.Logger  log) {
        this.log = log;
    }

    @Override
    final public void trace   (Object m) {
        if (log != null) log.trace(m);
    }

    @Override
    final public void trace   (Object m, Throwable t) {
        if (log != null) log.trace(m, t);
    }

    @Override
    final public void debug   (Object m) {
        if (log != null) log.debug(m);
    }

    @Override
    final public void debug   (Object m, Throwable t) {
        if (log != null) log.debug(m, t);
    }

    @Override
    final public void service (Object m) {
        log.debug(m);
    }

    @Override
    final public void service (Object m, Throwable t) {
        log.debug(m, t);
    }

    @Override
    final public void info    (Object m) {
        log.info(m);
    }

    @Override
    final public void info    (Object m, Throwable t) {
        log.info(m, t);
    }

    @Override
    final public void warn    (Object m) {
        log.warn(m);
    }

    @Override
    final public void warn    (Object m, Throwable t) {
        log.warn(m, t);
    }

    @Override
    final public void error   (Object m) {
        log.error(m);
    }

    @Override
    final public void error   (Object m, Throwable t) {
        log.error(m, t);
    }

    @Override
    final public void fatal   (Object m) {
        log.fatal(m);
    }

    @Override
    final public void fatal   (Object m, Throwable t) {
        log.fatal(m, t);
    }

    @Override
    final public boolean isTraceEnabled() {
        return log.isTraceEnabled();
    }

    @Override
    final public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    @Override
    final public boolean isServiceEnabled() {
        return log.isDebugEnabled();
    }

    protected org.apache.log4j.Level getLevel(Level p) {
        switch (p.toInt()) {
        case Level.TRACE_INT:   return org.apache.log4j.Level.TRACE;
        case Level.DEBUG_INT:   return org.apache.log4j.Level.DEBUG;
        case Level.SERVICE_INT: return org.apache.log4j.Level.DEBUG;
        case Level.INFO_INT:    return org.apache.log4j.Level.INFO;
        case Level.WARN_INT:    return org.apache.log4j.Level.WARN;
        case Level.ERROR_INT:   return org.apache.log4j.Level.ERROR;
        case Level.FATAL_INT:   return org.apache.log4j.Level.FATAL;
        case Level.OFF_INT:     return org.apache.log4j.Level.FATAL;
        default: return null;
        }
    }

    @Override
    final public void setLevel(Level p) {
        log.setLevel(getLevel(p));
    }
    @Override
    final public boolean isEnabledFor(Level l) {
        return log.isEnabledFor(getLevel(l));
    }

    @Override
    public String toString() {
        return "Log4jWrapper[" + log + "]";
    }

}
