/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

import java.util.*;

/**
 * @author Rico Jansen
 * @version $Id: TransactionManagerInterface.java,v 1.1 2006-10-03 18:31:46 michiel Exp $
 */
public interface TransactionManagerInterface {
    public String create(Object user,String transactionname)
        throws TransactionManagerException;
    public String addNode(String owner,String transactionname,String tmpnumber)
        throws TransactionManagerException;
    public String removeNode(String owner,String transactionname,String tmpnumber)
        throws TransactionManagerException;
    public String deleteObject(String owner,String transactionname,String tmpnumber)
        throws TransactionManagerException;
    public String cancel(Object user,String transactionname)
        throws TransactionManagerException;
    public String commit(Object user,String transactionname)
        throws TransactionManagerException;
    public String findUserName(Object user);
    public Vector getNodes(Object user,String transactionname);
}
