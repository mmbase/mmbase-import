/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

import org.mmbase.module.TransactionHandlerException;

/**
 * @author John Balder, 3MPS
 * @version $Id: TransactionManagerException.java,v 1.1 2000-12-14 10:54:52 rico Exp $
 */
public class TransactionManagerException extends TransactionHandlerException {
	
	public TransactionManagerException(String error) {
		super(error);
	}
}
