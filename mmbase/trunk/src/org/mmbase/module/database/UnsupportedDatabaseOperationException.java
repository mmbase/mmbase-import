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
 * @version $Id: UnsupportedDatabaseOperationException.java,v 1.1 2002-03-21 10:06:02 pierre Exp $
 */
public class UnsupportedDatabaseOperationException extends UnsupportedOperationException {

    public UnsupportedDatabaseOperationException(String s) {
        super(s);
    }
}

