/*
 * ClassName: TransactionHandlerException.java
 * 
 * Date: dec. 1st. 2001
 *
 * Copyright notice: 
 * This software is OSI Certified Open Source Software.
 * OSI Certified is a certification mark of the Open Source Initiative.
 *
 * The license (Mozilla version 1.0) can be read at the MMBase site.
 * See http://www.MMBase.org/license
 */

package org.mmbase.applications.xmlimporter;
	
/**
 * Creates a new transactionHandler exception.
 *
 * @author Rob van Maris: Finalist IT Group
 * @version 1.0
 */
public class TransactionHandlerException extends Exception {
	String code = "";
	String fieldId = "";
	String fieldOperator = "";
	String objectOperator = "";
	String objectId = "";
	String transactionOperator = "";
	String transactionId = "";
	String exceptionPage = "";

        /**
         * Creates a new transactionHandler exception.
         * @param s -  Text to serve as message in the exception.
         */        
	public TransactionHandlerException(String s) { 
		super(s); 
	}
}
	
