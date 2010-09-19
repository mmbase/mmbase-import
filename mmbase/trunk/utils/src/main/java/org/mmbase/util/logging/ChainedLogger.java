/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.logging;

import java.util.*;
import java.io.*;
import java.util.concurrent.*;

/**
 * Straight forward implemented which simply delegates every log-statement to a list of other other loggers.
 *
 * @author	Michiel Meeuwissen
 * @since	MMBase-1.9.1
 * @version $Id$
 */
public class ChainedLogger implements Logger, Externalizable {

    private static final Logger LOG = Logging.getLoggerInstance(ChainedLogger.class);

    private final List<Logger> loggers = new CopyOnWriteArrayList<Logger>();

    public ChainedLogger() {
    }
    public ChainedLogger(Logger... ls) {
        for (Logger l : ls) {
            addLogger(l);
        }
    }

    public List<Logger> getLoggers() {
        return Collections.unmodifiableList(loggers);
    }


    public ChainedLogger addLogger(Logger l) {
        loggers.add(l);
        return this;
    }

    public boolean containsLogger(Logger l) {
        return loggers.contains(l);
    }

    public boolean removeLogger(Logger l) {
        return loggers.remove(l);
    }


    @Override
    public void trace (Object m) {
        for (Logger log : loggers) {
            log.trace(m);
        }
    }

    @Override
    public void trace (Object m, Throwable t) {
        for (Logger log : loggers) {
            log.trace(m, t);
        }
    }

    @Override
    public void debug (Object m) {
        for (Logger log : loggers) {
            log.debug(m);
        }
    }

    @Override
    public void debug (Object m, Throwable t) {
        for (Logger log : loggers) {
            log.debug(m, t);
        }
    }

    @Override
    public void service (Object m) {
        for (Logger log : loggers) {
            log.service(m);
        }
    }

    @Override
    public void service (Object m, Throwable t) {
        for (Logger log : loggers) {
            log.service(m, t);
        }
    }

    @Override
    public void info (Object m) {
        for (Logger log : loggers) {
            log.info(m);
        }
    }

    @Override
    public void info (Object m, Throwable t) {
        for (Logger log : loggers) {
            log.info(m, t);
        }
    }

    @Override
    public void warn (Object m) {
        for (Logger log : loggers) {
            log.warn(m);
        }
    }

    @Override
    public void warn (Object m, Throwable t) {
        for (Logger log : loggers) {
            log.warn(m, t);
        }
    }

    @Override
    public void error (Object m) {
        for (Logger log : loggers) {
            log.error(m);
        }
    }

    @Override
    public void error (Object m, Throwable t) {
        for (Logger log : loggers) {
            log.error(m, t);
        }
    }

    @Override
    public void fatal (Object m) {
        for (Logger log : loggers) {
            log.fatal(m);
        }
    }

    @Override
    public void fatal (Object m, Throwable t) {
        for (Logger log : loggers) {
            log.fatal(m, t);
        }
    }

    @Override
    public boolean isTraceEnabled() {
        for (Logger log : loggers) {
            if (log.isTraceEnabled()) return true;
        }
        return false;
    }

    @Override
    public boolean isDebugEnabled() {
        for (Logger log : loggers) {
            if (log.isDebugEnabled()) return true;
        }
        return false;
    }

    @Override
    public boolean isServiceEnabled() {
        for (Logger log : loggers) {
            if (log.isServiceEnabled()) return true;
        }
        return false;
    }

    @Override
    public boolean isEnabledFor(Level l) {
        for (Logger log : loggers) {
            if (log.isEnabledFor(l)) return true;
        }
        return false;
    }

    @Override
    public void setLevel(Level p) {
        for (Logger log : loggers) {
            log.setLevel(p);
        }
    }
    @Override
    public String toString() {
        return "" + loggers;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        List<Logger> serializableLoggers = (List<Logger>) in.readObject();
        loggers.addAll(serializableLoggers);
    }

    @Override
    public void writeExternal(ObjectOutput stream) throws IOException {
        List<Logger> serializableLoggers = new ArrayList<Logger>();
        for (Logger l : loggers) {
            if (l instanceof Serializable) {
                serializableLoggers.add(l);
            } else {
                LOG.warn(" " + l + " is not serializable");
            }
        }
        stream.writeObject(serializableLoggers);
    }


}
