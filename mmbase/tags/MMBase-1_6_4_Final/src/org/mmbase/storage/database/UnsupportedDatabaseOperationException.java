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
 * @since MMBase-1.6
 * @author Pierre van Rooden
 * @version $Id: UnsupportedDatabaseOperationException.java,v 1.1 2002-09-16 15:07:38 pierre Exp $
 */
public class UnsupportedDatabaseOperationException extends StorageException {

    public UnsupportedDatabaseOperationException(String s) {
        super(s);
    }
}

