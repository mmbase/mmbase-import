/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

/**
 * Circular reference exception.
 * This exception is thrown when circularity is detected between two builders,
 * i.e. when the extend each other.
 *
 * @since MMBase-1.6
 * @author Pierre van Rooden
 * @version $Id: CircularReferenceException.java,v 1.3 2003-08-28 16:00:24 pierre Exp $
 */
public class CircularReferenceException extends BuilderConfigurationException {

    /**
     * Constructs a <code>CircularReferenceException</code> with <code>null</code> as its
     * message.
     * @since  MMBase-1.7
     */
    public CircularReferenceException() {
        super();
    }
    
    /**
     * Constructs a <code>CircularReferenceException</code> with the specified detail
     * message.
     *
     * @param message a description of the error
     */
    public CircularReferenceException(String message) {
        super(message);
    }

    /**
     * Constructs a <code>CircularReferenceException</code> with the detail
     * message of the original exception.
     * The cause can be retrieved with getCause().
     *
     * @param Throwable the cause of the error
     * @since  MMBase-1.7
     */
    public CircularReferenceException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a <code>CircularReferenceException</code> with the detail
     * message of the original exception.
     * The cause can be retrieved with getCause().
     *
     * @param message a description of the error
     * @param Throwable the cause of the error
     * @since  MMBase-1.7
     */
    public CircularReferenceException(String message, Throwable cause) {
        super(message,cause);
    }
    
}

