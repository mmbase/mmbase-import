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
 * @version $Id$
 */
public class StorageInaccessibleException extends StorageException {

    //javadoc is inherited
    public StorageInaccessibleException() {
        super();
    }

    //javadoc is inherited
    public StorageInaccessibleException(String message) {
        super(message);
    }

    //javadoc is inherited
    public StorageInaccessibleException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

    //javadoc is inherited
    public StorageInaccessibleException(String message, Throwable cause) {
        super(message, cause);
    }

}
