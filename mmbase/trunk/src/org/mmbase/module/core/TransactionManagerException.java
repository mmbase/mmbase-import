/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

/**
 * @author John Balder, 3MPS
 * @version $Id: TransactionManagerException.java,v 1.4 2003-08-28 16:00:24 pierre Exp $
 */
public class TransactionManagerException extends Exception {

	/**
     * Constructs a <code>TransactionManagerException</code> with <code>null</code> as its
     * message.
	 * @param message a description of the exception
     * @since  MMBase-1.7
 	 */
	public TransactionManagerException () {
		super();
	}
	
    
    /**
     * Constructs a <code>TransactionManagerException</code> with the specified detail
     * message.
     *
     * @param message a description of the error
     */
    public TransactionManagerException(String message) {
        super(message);
    }

    /**
     * Constructs a <code>TransactionManagerException</code> with the detail
     * message of the original exception.
     * The cause can be retrieved with getCause().
     *
     * @param Throwable the cause of the error
     * @since  MMBase-1.7
     */
    public TransactionManagerException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a <code>TransactionManagerException</code> with the detail
     * message of the original exception.
     * The cause can be retrieved with getCause().
     *
     * @param message a description of the error
     * @param Throwable the cause of the error
     * @since  MMBase-1.7
     */
    public TransactionManagerException(String message, Throwable cause) {
        super(message,cause);
    }
}
