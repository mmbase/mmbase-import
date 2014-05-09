/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage;

/**
 * This exception gets thrown when an error occurred during instantiation of the StorageFactory.
 *
 * @since  MMBase-1.7
 * @author Pierre van Rooden
 * @version $Id$
 */
public class StorageFactoryException extends StorageException {

    /**
     * Constructs a <code>StorageFactoryException</code> with <code>null</code> as its
     * message.
     */
    public StorageFactoryException() {
        super();
    }

    /**
     * Constructs a <code>StorageFactoryException</code> with the specified detail
     * message.
     *
     * @param message a description of the error
     */
    public StorageFactoryException(String message) {
        super(message);
    }

    /**
     * Constructs a <code>StorageFactoryException</code> with the detail
     * message of the original exception.
     * The cause can be retrieved with getCause().
     *
     * @param cause the cause of the error
     */
    public StorageFactoryException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

    /**
     * Constructs a <code>StorageFactoryException</code> with the detail
     * message of the original exception.
     * The cause can be retrieved with getCause().
     *
     * @param message a description of the error
     * @param cause the cause of the error
     */
    public StorageFactoryException(String message, Throwable cause) {
        super(message, cause);
    }

}
