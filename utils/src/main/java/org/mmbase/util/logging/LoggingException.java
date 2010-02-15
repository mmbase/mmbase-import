/*
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.logging;

/**
 * Exceptions thrown by logging can be wrapped in this. Odd logging
 * implementation like 'ExceptionImpl' do this.
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.7
 * @see    ExceptionImpl
 */

public class LoggingException extends RuntimeException {
    private static final long serialVersionUID = 0L;
    private Level level;

    //javadoc is inherited
	public LoggingException () {
		super();
	}

    //javadoc is inherited
    public LoggingException(String message) {
        super(message);
    }

    //javadoc is inherited
    public LoggingException(Throwable cause) {
        super(cause);
    }

    //javadoc is inherited
    public LoggingException(String message, Throwable cause) {
        super(message,cause);
    }

	/**
	 * Create the exception.
	 * @param message a description of the exception
	 * @param level the level of logging at which the exception occurred
 	 */
    public LoggingException(String message, Level level) {
        super(message);
        this.level = level;
    }

    /**
     * Create the exception.
     * @param cause the cause of the exception
     * @param level the level of logging at which the exception occurred
     */
    public LoggingException(Throwable cause, Level level) {
        super(cause);
        this.level = level;
    }

 	/**
	 * Returns the level of logging at which the exception occurred
 	 */
    public Level getLevel() {
        return level;
    }
}
