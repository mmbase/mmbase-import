/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

/**
 * @author John Balder, 3MPS
 * @version $Id: TransactionManagerException.java,v 1.2 2000-12-14 11:04:11 wwwtech Exp $
 */
public class TransactionManagerException extends Exception {
	
	public TransactionManagerException(String error) {
		super(error);
	}
}
