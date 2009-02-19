/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

/**
 * This Exception is thrown when something goes wrong the MMBase transaction manager.
 *
 * @author John Balder, 3MPS
 * @version $Id: TransactionManagerException.java,v 1.6 2003-09-04 11:05:33 pierre Exp $
 */
public class TransactionManagerException extends Exception {

    //javadoc is inherited
    public TransactionManagerException () {
        super();
    }

    //javadoc is inherited
    public TransactionManagerException(String message) {
        super(message);
    }

    //javadoc is inherited
    public TransactionManagerException(Throwable cause) {
        super(cause);
    }

    //javadoc is inherited
    public TransactionManagerException(String message, Throwable cause) {
        super(message,cause);
    }
}
