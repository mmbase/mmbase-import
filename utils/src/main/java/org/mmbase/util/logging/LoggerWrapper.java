/*
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.logging;
import java.util.*;

/**
 * Wraps a logger instance. This can be used for static logger instances which might be instatatied
 * before logging itself is configured. After configurating logging, all static 'wrappers' can then
 * be called to wrap another logger instance.
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.7
 * @version $Id$
 **/

public class LoggerWrapper implements Logger {

    private static final Set<LoggerWrapper> wrappers = new HashSet<LoggerWrapper>();


    // package
    static Set<LoggerWrapper> getWrappers() {
        return Collections.unmodifiableSet(wrappers);
    }

    private Logger log;
    private final String name;

    // package
    LoggerWrapper(Logger log, String name) {
        this.name = name;
        setLogger(log);
        wrappers.add(this);
    }

    // package
    String getName() {
        return name;
    }

    // package
    Logger setLogger(Logger log) {
        if (log == null) {
            if (this.log == null) {
                log = SimpleImpl.getLoggerInstance(name);
            } else {
                log = this.log;
            }
            System.err.println("Tried to instantiate logger wrapper with null!");
            log.error("Tried to instantiate logger wrapper with null!", new Exception());
        }
        Logger org = this.log;
        this.log = log;
        return org;
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
        log.service(m);
    }

    @Override
    final public void service (Object m, Throwable t) {
        log.service(m, t);
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
        return log.isServiceEnabled();
    }

    @Override
    final public void setLevel(Level p) {
        log.setLevel(p);
    }
    @Override
    final public boolean isEnabledFor(Level l) {
        return log.isEnabledFor(l);
    }

    @Override
    public String toString() {
        return "LoggerWrapper[" + log + "]";
    }

}
