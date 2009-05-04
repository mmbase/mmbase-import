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
 * @version $Id$
 */
public class StorageConfigurationException extends StorageException {

    //javadoc is inherited
    public StorageConfigurationException() {
        super();
    }

    //javadoc is inherited
    public StorageConfigurationException(String message) {
        super(message);
    }

    //javadoc is inherited
    public StorageConfigurationException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

    //javadoc is inherited
    public StorageConfigurationException(String message, Throwable cause) {
        super(message,cause);
    }

}
