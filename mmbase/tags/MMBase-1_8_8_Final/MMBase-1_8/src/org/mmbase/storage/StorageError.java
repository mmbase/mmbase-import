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
 * @version $Id$
 */
public class StorageError extends Error {

    //javadoc is inherited
    public StorageError() {
        super();
    }

    //javadoc is inherited
    public StorageError(String message) {
        super(message);
    }

    //javadoc is inherited
    public StorageError(Throwable cause) {
        super(cause.getMessage(), cause);
    }

    //javadoc is inherited
    public StorageError(String message, Throwable cause) {
        super(message, cause);
    }

}
