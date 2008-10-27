/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/ 
package org.mmbase.applications.vprowizards.spring;

import javax.servlet.http.HttpServletRequest;

import org.mmbase.bridge.Transaction;

public interface CloudFactory {
	/**
	 * create or reuse some transaction.
	 * @param request
	 * @return an mmbase transaction object.
	 */
	public Transaction createTransaction( HttpServletRequest request);
}


