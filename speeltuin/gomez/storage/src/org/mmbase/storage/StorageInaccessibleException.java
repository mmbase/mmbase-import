/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage;

/**
 * This exception gets thrown when the storage is inaccessible, such as when files were moved, a database goes offline,
 * or user rights on the storage were insufficient to allow access.
 * @since  MMBase-1.7
 * @author Pierre van Rooden
 * @version $Id: StorageInaccessibleException.java,v 1.1 2003-07-21 09:31:03 pierre Exp $
 */
public class StorageInaccessibleException extends StorageException {

    /**
     * Constructs a <code>StorageInaccessibleException</code> with <code>null</code> as its
     * message.
     */
    public StorageInaccessibleException() {
        super();
    }

    /**
     * Constructs a <code>StorageInaccessibleException</code> with the specified detail
     * message.
     *
     * @param message a description of the error
     */
    public StorageInaccessibleException(String message) {
        super(message);
    }

    /**
     * Constructs a <code>StorageInaccessibleException</code> with the detail
     * message of the original exception.
     * The cause can be retrieved with getCause().
     *
     * @param Throwable the cause of the error
     */
    public StorageInaccessibleException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a <code>StorageInaccessibleException</code> with the detail
     * message of the original exception.
     * The cause can be retrieved with getCause().
     *
     * @param message a description of the error
     * @param Throwable the cause of the error
     */
    public StorageInaccessibleException(String message, Throwable cause) {
        super(message,cause);
    }

}
