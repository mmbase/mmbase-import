/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge;

/**
 * This exception gets thrown when something goes wrong on the MMCI.
 *
 * @author Pierre van Rooden
 * @version $Id: BridgeException.java,v 1.13 2003-08-28 16:00:24 pierre Exp $
 */
public class BridgeException extends RuntimeException {

    /**
     * Constructs a <code>BridgeException</code> with <code>null</code> as its
     * message.
     * @since  MMBase-1.6
     */
    public BridgeException() {
        super();
    }

    /**
     * Constructs a <code>BridgeException</code> with the specified detail
     * message.
     *
     * @param message a description of the error
     */
    public BridgeException(String message) {
        super(message);
    }

    /**
     * Constructs a <code>BridgeException</code> with the detail
     * message of the original exception.
     * The cause can be retrieved with getCause().
     *
     * @param Throwable the cause of the error
     * @since  MMBase-1.6
     */
    public BridgeException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a <code>BridgeException</code> with the detail
     * message of the original exception.
     * The cause can be retrieved with getCause().
     *
     * @param message a description of the error
     * @param Throwable the cause of the error
     * @since  MMBase-1.6
     */
    public BridgeException(String message, Throwable cause) {
        super(message,cause);
    }

}
