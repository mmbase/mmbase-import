/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

/**
 * @author John Balder, 3MPS
 * @version $Id: TransactionManagerException.java,v 1.3 2001-08-24 07:31:34 pierre Exp $
 */
public class TransactionManagerException extends Exception {

    public TransactionManagerException(String error) {
        super(error);
    }
}
