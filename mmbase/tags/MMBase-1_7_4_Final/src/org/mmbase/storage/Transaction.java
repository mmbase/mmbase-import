/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage;

/**
 * MMBase transaction object.
 * Used to maintain context (connection) between separate storage commands (typically database queries),
 * allowing rollback in complex transactions (if supported).
 * A transaction is obtained from a Storage object.
 *
 * @deprecated This code is scheduled for removal once MMBase has been fully converted to the new
 *             StorageManager implementation.
 * @author Pierre van Rooden
 * @since MMBase-1.6
 * @version $Id: Transaction.java,v 1.2 2004-01-27 12:04:45 pierre Exp $
 */
public interface Transaction {

    /**
     * Returns true if this transaction supports rollback.
     * Rollback of a transaction is defined as making undone ancy changes to the
     * persistent storage used (i.a. a database), since the transaction started.
     * @return true if the trsansaction supports rollback
     */
    public boolean supportsRollback();

    /**
     * Closes the transaction (and commits changes).
     * If closing fails, the function returns false, rather than throwing an exception
     * (though the error is logged).
     * @return true if closed successfully
     */
    public boolean commit();

    /**
     * Rolsl back (cancels) the transaction.
     * Some implementations perform rollback.
     * If cancelling fails, the function returns false, rather than throwing an exception
     * (though the error is logged).
     * @return true if cancelled successfully
     */
    public boolean rollback();


}
