/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

import java.util.*;
import org.mmbase.module.corebuilders.*;

/*
	$Id: TransactionManagerInterface.java,v 1.1 2000-08-14 19:19:05 rico Exp $

	$Log: not supported by cvs2svn $
*/

/**
 * @author Rico Jansen
 * @version $Id: TransactionManagerInterface.java,v 1.1 2000-08-14 19:19:05 rico Exp $
 */
public interface TransactionManagerInterface {
	public String create(Object user,String transactionname);
	public String addNode(String transactionname,String tmpnumber);
	public String removeNode(String transactionname,String tmpnumber);
	public String cancel(Object user,String transactionname);
	public String commit(Object user,String transactionname);
	public String findUserName(Object user);
	public Vector getNodes(Object user,String transactionname);
}
