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
 * @version $Id: StorageException.java,v 1.6 2003-11-18 14:58:33 michiel Exp $
 */
public class StorageException extends Exception {

    //javadoc is inherited
    public StorageException() {
        super();
    }

    //javadoc is inherited
    public StorageException(String message) {
        super(message);
    }

    //javadoc is inherited
    public StorageException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

    //javadoc is inherited
    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }

}
