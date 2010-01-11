/*
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.logging.java;

import org.mmbase.util.logging.*;
import java.util.logging.LogRecord;
import java.util.logging.Level;


/**
 * Since java 1.4 there is a Logger implemented in java itself, if you have code which requests a
 * java.util.logging.Logger object to which it will log to, and you want it to log the MMBase logger
 * then, you can offer it an instance of this class, which wraps an MMBase Logger object in a
 * java.util.logging.Logger object.
 *
 * For the correspondence between levels of java logging and mmbase logging see javadoc of 
 * {@link org.mmbase.util.logging.java.Impl}.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.8
 * @see   org.mmbase.util.logging.java.Impl
 */


public class MMBaseLogger extends java.util.logging.Logger {
    
    Logger log = null;

    /**
     * Instantiates a java Logger wich logs to the MMBase logger with category {@link java.util.logging.LogRecord#getLoggerName}.
     */
    public MMBaseLogger() {
        super(null, null);
    }

    /**
     * Instantiated a java Logger wich logs to the given MMBase logger.
     */
    public MMBaseLogger(Logger log) {
        super(null, null);
        this.log = log;
    }

    /**
     * See {@link java.util.logging.Logger#log(LogRecord)}.
     */
    @Override
    public void log(LogRecord record) {

        Logger l;
        if (log == null) {
            l = Logging.getLoggerInstance(record.getLoggerName());
        } else {
            l = log;
        }

        String message = record.getMessage();
        int level = record.getLevel().intValue();
        if (level >= Level.SEVERE.intValue()) {
            l.error(message);
        } else if (level >= Level.WARNING.intValue()) {
            l.warn(message);
        } else if (level >= Level.INFO.intValue()) {            
            l.info(message);
        } else if (level >= Level.CONFIG.intValue()) {
            l.service(message);
        } else if (level >= Level.FINE.intValue()) {
            l.debug(message);
        } else {
            l.trace(message);
        }            
    }

}

