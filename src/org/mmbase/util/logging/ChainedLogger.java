/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.logging;

import java.io.*;
import java.util.*;

/**
 * Straight forward implemented which simply delegated every log-statement to a list of other other loggers.
 *
 * @author	Michiel Meeuwissen
 * @since	MMBase-1.9.1
 * @version $Id: ChainedLogger.java,v 1.1 2009-02-10 09:28:22 michiel Exp $
 */
public class ChainedLogger implements Logger {

    private final List<Logger> loggers = new ArrayList<Logger>();
    public ChainedLogger(Logger... ls) {
        for (Logger l : ls) {
            addLogger(l);
        }
    }

    public ChainedLogger addLogger(Logger l) {
        loggers.add(l);
        return this;
    }


    public void trace (Object m) {
        for (Logger log : loggers) {
            log.trace(m);
        }
    }

    public void trace (Object m, Throwable t) {
        for (Logger log : loggers) {
            log.trace(m, t);
        }
    }

    public void debug (Object m) {
        for (Logger log : loggers) {
            log.debug(m);
        }
    }

    public void debug (Object m, Throwable t) {
        for (Logger log : loggers) {
            log.debug(m, t);
        }
    }

    public void service (Object m) {
        for (Logger log : loggers) {
            log.service(m);
        }
    }

    public void service (Object m, Throwable t) {
        for (Logger log : loggers) {
           log.service(m, t);
        }
    }

    public void info (Object m) {
        for (Logger log : loggers) {
            log.info(m);
        }
    }

    public void info (Object m, Throwable t) {
        for (Logger log : loggers) {
            log.info(m, t);
        }
    }

    public void warn (Object m) {
        for (Logger log : loggers) {
            log.warn(m);
        }
    }

    public void warn (Object m, Throwable t) {
        for (Logger log : loggers) {
            log.warn(m, t);
        }
    }

    public void error (Object m) {
        for (Logger log : loggers) {
            log.error(m);
        }
    }

    public void error (Object m, Throwable t) {
        for (Logger log : loggers) {
            log.error(m, t);
        }
    }

    public void fatal (Object m) {
        for (Logger log : loggers) {
            log.fatal(m);
        }
    }

    public void fatal (Object m, Throwable t) {
        for (Logger log : loggers) {
            log.fatal(m, t);
        }
    }

    public boolean isTraceEnabled() {
        for (Logger log : loggers) {
            if (log.isTraceEnabled()) return true;
        }
        return false;
    }

    public boolean isDebugEnabled() {
        for (Logger log : loggers) {
            if (log.isDebugEnabled()) return true;
        }
        return false;
    }

    public boolean isServiceEnabled() {
        for (Logger log : loggers) {
            if (log.isServiceEnabled()) return true;
        }
        return false;
    }

    public void setLevel(Level p) {
        for (Logger log : loggers) {
            log.setLevel(p);
        }
    }

}
