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
	$Id: TransactionManagerInterface.java,v 1.2 2000-11-08 16:24:13 vpro Exp $

	$Log: not supported by cvs2svn $
	Revision 1.1  2000/08/14 19:19:05  rico
	Rico: added the temporary node and transaction support.
	      note that this is rather untested but based on previously
	      working code.
	
*/

/**
 * @author Rico Jansen
 * @version $Id: TransactionManagerInterface.java,v 1.2 2000-11-08 16:24:13 vpro Exp $
 */
public interface TransactionManagerInterface {
	public String create(Object user,String transactionname);
	public String addNode(String owner,String transactionname,String tmpnumber);
	public String removeNode(String owner,String transactionname,String tmpnumber);
	public String cancel(Object user,String transactionname);
	public String commit(Object user,String transactionname);
	public String findUserName(Object user);
	public Vector getNodes(Object user,String transactionname);
}
