/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.database;

/**
 * Storage exception.
 * This exception is thrown when the storage layer is unable to
 * perform an operation.
 *
 * @since MMBase-1.6
 * @author Pierre van Rooden
 * @version $Id: StorageException.java,v 1.1 2002-04-08 11:59:27 pierre Exp $
 */
public class StorageException extends Exception {

    public StorageException(String s) {
        super(s);
    }
}

