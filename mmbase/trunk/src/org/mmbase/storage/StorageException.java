/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage;

/**
 * This exception gets thrown when something goes wrong in the storage layer.
 *
 * @since  MMBase-1.7
 * @author Pierre van Rooden
 * @version $Id: StorageException.java,v 1.4 2003-08-28 16:00:24 pierre Exp $
 */
public class StorageException extends Exception {

    /**
     * Constructs a <code>StorageException</code> with <code>null</code> as its
     * message.
     */
    public StorageException() {
        super();
    }

    /**
     * Constructs a <code>StorageException</code> with the specified detail
     * message.
     *
     * @param message a description of the error
     */
    public StorageException(String message) {
        super(message);
    }

    /**
     * Constructs a <code>StorageException</code> with the detail
     * message of the original exception.
     * The cause can be retrieved with getCause().
     *
     * @param Throwable the cause of the error
     */
    public StorageException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a <code>StorageException</code> with the detail
     * message of the original exception.
     * The cause can be retrieved with getCause().
     *
     * @param message a description of the error
     * @param Throwable the cause of the error
     */
    public StorageException(String message, Throwable cause) {
        super(message,cause);
    }

}
