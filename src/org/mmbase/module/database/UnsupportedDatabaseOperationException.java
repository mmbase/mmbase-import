/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.database;

/**
 * Unsupported Database Operation exception.
 * This exception is thrown when the database support layer is unable to
 * perform an operation due to the limitations of the database used.
 *
 * @since MMBase-1.6
 * @author Pierre van Rooden
 * @version $Id: UnsupportedDatabaseOperationException.java,v 1.2 2002-04-08 11:59:27 pierre Exp $
 */
public class UnsupportedDatabaseOperationException extends StorageException {

    public UnsupportedDatabaseOperationException(String s) {
        super(s);
    }
}

