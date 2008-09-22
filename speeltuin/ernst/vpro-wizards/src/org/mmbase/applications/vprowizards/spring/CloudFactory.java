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


