/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage;

/**
 * This error gets thrown when something goes seriously - and likely unrecoverably - wrong in the storage layer.
 * This includes database connection failures at startup, non-existing vital resources, etc.
 * In general, a StorageError should indicate that a storage layer is unuseable. 
 * This will normally mean MMBase will fail to start. 
 *
 * @since  MMBase-1.7
 * @author Pierre van Rooden
 * @version $Id: StorageError.java,v 1.2 2003-08-28 16:00:24 pierre Exp $
 */
public class StorageError extends Error {

    /**
     * Constructs a <code>StorageException</code> with <code>null</code> as its
     * message.
     */
    public StorageError() {
        super();
    }

    /**
     * Constructs a <code>StorageException</code> with the specified detail
     * message.
     *
     * @param message a description of the error
     */
    public StorageError(String message) {
        super(message);
    }

    /**
     * Constructs a <code>StorageException</code> with the detail
     * message of the original exception.
     * The cause can be retrieved with getCause().
     *
     * @param Throwable the cause of the error
     */
    public StorageError(Throwable cause) {
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
    public StorageError(String message, Throwable cause) {
        super(message,cause);
    }

}
