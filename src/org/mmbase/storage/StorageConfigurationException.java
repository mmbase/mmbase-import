/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage;

/**
 * This exception gets thrown when an error occurred in the configuration of a StorageFactory or a
 * storage manager.
 * This can occur when configuration files are inaccesible, a wrong file format is used, or attributes
 * are missing.
 * @since  MMBase-1.7
 * @author Pierre van Rooden
 * @version $Id: StorageConfigurationException.java,v 1.1 2003-08-21 09:59:27 pierre Exp $
 */
public class StorageConfigurationException extends StorageException {

    /**
     * Constructs a <code>StorageConfigurationException</code> with <code>null</code> as its
     * message.
     */
    public StorageConfigurationException() {
        super();
    }

    /**
     * Constructs a <code>StorageConfigurationException</code> with the specified detail
     * message.
     *
     * @param message a description of the error
     */
    public StorageConfigurationException(String message) {
        super(message);
    }

    /**
     * Constructs a <code>StorageConfigurationException</code> with the detail
     * message of the original exception.
     * The cause can be retrieved with getCause().
     *
     * @param Throwable the cause of the error
     */
    public StorageConfigurationException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a <code>StorageConfigurationException</code> with the detail
     * message of the original exception.
     * The cause can be retrieved with getCause().
     *
     * @param message a description of the error
     * @param Throwable the cause of the error
     */
    public StorageConfigurationException(String message, Throwable cause) {
        super(message,cause);
    }

}
