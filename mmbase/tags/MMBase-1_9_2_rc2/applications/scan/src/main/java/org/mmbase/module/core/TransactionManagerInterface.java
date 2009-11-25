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
 * @version $Id$
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
