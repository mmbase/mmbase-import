/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module;

import org.mmbase.util.*;

/**
 * interface for TransactionHandler
 *
 * @move to org.mmbase.applications.xmlimporter.TransactionHandlerInterface
 * @author  John balder
 * @version $Id$
 */
public interface TransactionHandlerInterface {
	
	public void handleTransaction(String template, sessionInfo session, scanpage sp);

}
