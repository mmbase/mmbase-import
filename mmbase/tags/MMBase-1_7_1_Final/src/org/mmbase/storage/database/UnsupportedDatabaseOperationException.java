/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.database;

import org.mmbase.storage.*;

/**
 * Unsupported Database Operation exception.
 * This exception is thrown when the database support layer is unable to
 * perform an operation due to the limitations of the database used.
 *
 * @deprecated This code is scheduled for removal once MMBase has been fully converted to the new
 *             StorageManager implementation.
 * @since MMBase-1.6
 * @author Pierre van Rooden
 * @version $Id: UnsupportedDatabaseOperationException.java,v 1.3 2004-01-27 12:04:47 pierre Exp $
 */
public class UnsupportedDatabaseOperationException extends StorageException {

    //javadoc is inherited
    public UnsupportedDatabaseOperationException() {
        super();
    }

    //javadoc is inherited
    public UnsupportedDatabaseOperationException(String message) {
        super(message);
    }

    //javadoc is inherited
    public UnsupportedDatabaseOperationException(Throwable cause) {
        super(cause);
    }

    //javadoc is inherited
    public UnsupportedDatabaseOperationException(String message, Throwable cause) {
        super(message, cause);
    }

}

