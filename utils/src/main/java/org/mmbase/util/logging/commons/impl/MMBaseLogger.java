/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.logging.commons.impl;

import org.apache.commons.logging.Log;
import org.mmbase.util.logging.Logger;

/**
 * Adaptor class to convert jakarta-commons Log calls to MMBase Logger calls.<br/>
 * <br/>
 *
 * @author Kees Jongenburger
 */
public class MMBaseLogger implements Log {

    protected final Logger logger;

    /**
     * creates a new adaptor
     * @param logger the logger to use
     */
    MMBaseLogger(Logger logger) {
        this.logger = logger;
    }

    /**
     * @return if debug is enabled in the MMBaseLogger
     */
    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    /**
     * @return if service is enabled in the MMBaseLogger
     */
    @Override
    public boolean isErrorEnabled() {
        return logger.isServiceEnabled();
    }

    /**
     * @return is service is enabled in the MMBase logger
     */
    @Override
    public boolean isFatalEnabled() {
        return logger.isServiceEnabled();
    }
    /**
     * @return is service is enabled in the MMBase logger
     */
    @Override
    public boolean isInfoEnabled() {
        return logger.isServiceEnabled();
    }

    /**
     * @return if debug is enabled in the MMBaseLogger
     */
    @Override
    public boolean isTraceEnabled() {
        return logger.isDebugEnabled();
    }

    /**
     * @return is service is enabled in the MMBaseLogger
     */
    @Override
    public boolean isWarnEnabled() {
        return logger.isServiceEnabled();
    }

    /**
     * calls {@link org.mmbase.util.logging.Logger#trace(java.lang.Object)}
     */
    @Override
    public void trace(Object object) {
        logger.trace(object);
    }

    /**
     * calls {@link org.mmbase.util.logging.Logger#debug(java.lang.Object)}
     */
    @Override
    public void debug(Object object) {
        logger.debug(object);
    }

    /**
     * calls {@link org.mmbase.util.logging.Logger#info(java.lang.Object)}
     */
    @Override
    public void info(Object object) {
        logger.info(object);
    }

    /**
     * calls {@link org.mmbase.util.logging.Logger#warn(java.lang.Object)}
     */
    @Override
    public void warn(Object object) {
        logger.warn(object);
    }

    /**
     * calls {@link org.mmbase.util.logging.Logger#error(java.lang.Object)}
     */
    @Override
    public void error(Object object) {
        logger.error(object);
    }

    /**
     * calls {@link org.mmbase.util.logging.Logger#fatal(java.lang.Object)}
     */
    @Override
    public void fatal(Object object) {
        logger.fatal(object);

    }

    /**
     * calls {@link org.mmbase.util.logging.Logger#debug(java.lang.Object, java.lang.Throwable)}
     */
    @Override
    public void debug(Object object, Throwable throwable) {
        logger.debug(object, throwable);
    }

    /**
     * calls {@link org.mmbase.util.logging.Logger#error(java.lang.Object, java.lang.Throwable)}
     */
    @Override
    public void error(Object object, Throwable throwable) {
        logger.error(object, throwable);
    }

    /**
     * calls {@link org.mmbase.util.logging.Logger#fatal(java.lang.Object, java.lang.Throwable)}
     */
    @Override
    public void fatal(Object object, Throwable throwable) {
        logger.fatal(object, throwable);
    }

    /**
     * calls {@link org.mmbase.util.logging.Logger#info(java.lang.Object, java.lang.Throwable)}
     */
    @Override
    public void info(Object object, Throwable throwable) {
        logger.info(object, throwable);
    }

    /**
     * calls {@link org.mmbase.util.logging.Logger#trace(java.lang.Object, java.lang.Throwable)}
     */
    @Override
    public void trace(Object object, Throwable throwable) {
        logger.trace(object, throwable);
    }

    /**
     * calls {@link org.mmbase.util.logging.Logger#warn(java.lang.Object, java.lang.Throwable)}
     */
    @Override
    public void warn(Object object, Throwable throwable) {
        logger.warn(object, throwable);
    }

}
