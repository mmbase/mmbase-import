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
 * @todo This exception implements a few constructors also found in java 1.4.
 * These implementations need be adjusted for java 1.4 to enable excpetion chaining.
 * To adjust, replace the constructor bodies with the 1.4 commented-out code (so that these
 * tasks are delegated to Exception), and remove the private field cause and the methods
 * initCause() and getCause();
 *
 * @since  MMBase-1.7
 * @author Pierre van Rooden
 * @version $Id: StorageError.java,v 1.1 2003-08-21 09:59:27 pierre Exp $
 */
public class StorageError extends Error {

    private Throwable cause=null;

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
        super(cause==null ? null : org.mmbase.util.logging.Logging.stackTrace(cause));
        initCause(cause);
        // 1.4 code:
        // super(cause);
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
        super(message);
        initCause(cause);
        // 1.4 code:
        // super(message,cause);
    }

    /**
     * Sets the cause of the exception.
     *
     * @return the cause of the error
     */
    public Throwable initCause(Throwable cause) {
        if (cause==this) {
          throw new IllegalArgumentException("A throwable cannot be its own cause");
        }
        if (this.cause!=null) {
          throw new IllegalStateException("A cause can be set at most once");
        }
        this.cause=cause;
        return cause;
    }

    /**
     * Returns the cause of the exception.
     *
     * @return the cause of the exception
     */
    public Throwable getCause() {
        return cause;
    }


}
