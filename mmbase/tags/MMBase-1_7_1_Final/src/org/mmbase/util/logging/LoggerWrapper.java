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
 * @version $Id: LoggerWrapper.java,v 1.1 2003-07-18 14:54:42 michiel Exp $
 **/

public  class LoggerWrapper implements Logger {

    private static Set wrappers = new HashSet();


    // package 
    static Set getWrappers() {
        return Collections.unmodifiableSet(wrappers);
    }

    private Logger log;
    private String name;

    // package 
    LoggerWrapper(Logger log, String name) {
        this.log  = log;
        this.name = name;
        wrappers.add(this);
    }

    // package
    String getName() {
        return name;
    }
    // package
    Logger setLogger(Logger log) {
        Logger org = this.log;
        this.log = log;
        return org;
    }

    final public void trace   (Object m) {
        log.trace(m);
    }
    final public void debug   (Object m) {
        log.debug(m);
    }

    final public void service (Object m) {
        log.service(m);
    }

    final public void info    (Object m) {
        log.info(m);
    }

    final public void warn    (Object m) {
        log.warn(m);
    }

    final public void error   (Object m) {
        log.error(m);
    }
    final public void fatal   (Object m) {
        log.fatal(m);
    }

    final public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    final public boolean isServiceEnabled() {
        return log.isServiceEnabled();
    }
    final public void setLevel(Level p) {
        log.setLevel(p);
    }

    final public void setPriority(Level p) {
        log.setPriority(p);
    }
		
}
