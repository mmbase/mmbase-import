/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge;

/**
 * This exception gets thrown when an attempt is made to create a transaction with a name that already exists
 * @author Pierre van Rooden
 * @version $Id$
 * @since  MMBase-1.6
 */
public class AlreadyExistsException extends BridgeException {

    //javadoc is inherited
    public AlreadyExistsException() {
        super();
    }

    //javadoc is inherited
    public AlreadyExistsException(String message) {
        super(message);
    }

    //javadoc is inherited
    public AlreadyExistsException(Throwable cause) {
        super(cause);
    }

    //javadoc is inherited
    public AlreadyExistsException(String message, Throwable cause) {
        super(message,cause);
    }

}
