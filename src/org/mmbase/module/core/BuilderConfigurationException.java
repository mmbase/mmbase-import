/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

/**
 * Builder configuration exception.
 * This exception is thrown when there is a (unrecoverable) foault in teh configuration
 * of the builder file, i.e. a required builder file does not exist, a core builder is
 * inactive, circularity is detected between two builders, etc.
 *
 * @since MMBase-1.6
 * @author Pierre van Rooden
 * @version $Id: BuilderConfigurationException.java,v 1.2 2003-08-28 16:00:24 pierre Exp $
 */
public class BuilderConfigurationException extends RuntimeException {

    /**
     * Constructs a <code>BuilderConfigurationException</code> with <code>null</code> as its
     * message.
     * @since  MMBase-1.7
     */
    public BuilderConfigurationException() {
        super();
    }

    /**
     * Constructs a <code>BuilderConfigurationException</code> with the specified detail
     * message.
     *
     * @param message a description of the error
     */
    public BuilderConfigurationException(String message) {
        super(message);
    }

    /**
     * Constructs a <code>BuilderConfigurationException</code> with the detail
     * message of the original exception.
     * The cause can be retrieved with getCause().
     *
     * @param Throwable the cause of the error
     * @since  MMBase-1.7
     */
    public BuilderConfigurationException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a <code>BuilderConfigurationException</code> with the detail
     * message of the original exception.
     * The cause can be retrieved with getCause().
     *
     * @param message a description of the error
     * @param Throwable the cause of the error
     * @since  MMBase-1.7
     */
    public BuilderConfigurationException(String message, Throwable cause) {
        super(message,cause);
    }

}

