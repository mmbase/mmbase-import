/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package org.mmbase.util.logging.commons;

import org.apache.commons.logging.Log;
import org.mmbase.util.logging.AbstractSimpleImpl;
import org.mmbase.util.logging.Logger;

/**
 * Commons logging for MMBase, contains a single Log instance
 * 
 * @author Wouter Heijke
 * @version $Revision: 1.1 $
 */
public class CommonsLog extends AbstractSimpleImpl implements Logger {

	private Log log;

	public CommonsLog(Log commonsLog) {
		log = commonsLog;
	}

	protected final void log(String s) {
		if (log.isTraceEnabled()) {
			log.trace(s);
		} else if (log.isDebugEnabled()) {
			log.debug(s);
		} else if (log.isInfoEnabled()) {
			log.info(s);
		} else if (log.isWarnEnabled()) {
			log.warn(s);
		} else if (log.isErrorEnabled()) {
			log.error(s);
		} else if (log.isFatalEnabled()) {
			log.fatal(s);
		}
	}

	public void debug(Object message) {
		log.debug(message);
	}

	public void error(Object message) {
		log.error(message);
	}

	public void fatal(Object message) {
		log.fatal(message);
	}

	public void info(Object message) {
		log.info(message);
	}

	public void service(Object message) {
		log.info(message);
	}

	public void trace(Object message) {
		log.trace(message);
	}

	public void warn(Object message) {
		log.warn(message);
	}

	public void debug(Object message, Throwable t) {
		log.debug(message, t);
	}

	public void error(Object message, Throwable t) {
		log.error(message, t);
	}

	public void fatal(Object message, Throwable t) {
		log.fatal(message, t);
	}

	public void info(Object message, Throwable t) {
		log.info(message, t);
	}

	public void trace(Object message, Throwable t) {
		log.trace(message, t);
	}

	public void warn(Object message, Throwable t) {
		log.warn(message, t);
	}

	public boolean isDebugEnabled() {
		return log.isDebugEnabled();
	}

	public boolean isServiceEnabled() {
		// TODO map this via a properties file
		return log.isInfoEnabled();
	}

	public boolean isErrorEnabled() {
		return log.isErrorEnabled();
	}

	public boolean isFatalEnabled() {
		return log.isFatalEnabled();
	}

	public boolean isInfoEnabled() {
		return log.isInfoEnabled();
	}

	public boolean isTraceEnabled() {
		return log.isTraceEnabled();
	}

	public boolean isWarnEnabled() {
		return log.isWarnEnabled();
	}

}
