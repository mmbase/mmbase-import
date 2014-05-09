/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage;

/**
 * This exception gets thrown if a query resulted nothing, in contrary to the expectation (for example in 'getNode').
 * @since  MMBase-1.8
 * @author Michiel Meeuwissen
 * @version $Id$
 */
public class StorageNotFoundException extends StorageException {

    //javadoc is inherited
    public StorageNotFoundException() {
        super();
    }

    //javadoc is inherited
    public StorageNotFoundException(String message) {
        super(message);
    }

    //javadoc is inherited
    public StorageNotFoundException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

    //javadoc is inherited
    public StorageNotFoundException(String message, Throwable cause) {
        super(message,cause);
    }

}
