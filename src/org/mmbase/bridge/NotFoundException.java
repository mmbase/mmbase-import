/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge;

/**
 * This exception gets thrown when an object is not found in the bridge.
 * @author Michiel Meeuwissen
 */
public class NotFoundException extends BridgeException {

    /**
     * Constructs a <code>NotFoundException</code> with <code>null</code> as its
     * message.
     */
    public NotFoundException() {
        super();
    }

    /**
     * Constructs a <code>NotFoundException</code> with the specified detail
     * message.
     *
     * @param message a description of the error
     */
    public NotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a <code>NotFoundException</code> with the detail
     * message of the original exception.
     * The cause can be retrieved with getCause().
     *
     * @param Throwable the cause of the error
     */
    public NotFoundException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a <code>NotFoundException</code> with the detail
     * message of the original exception.
     * The cause can be retrieved with getCause().
     *
     * @param message a description of the error
     * @param Throwable the cause of the error
     */
    public NotFoundException(String message, Throwable cause) {
        super(message,cause);
    }

}
