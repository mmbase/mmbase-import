/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

/**
 * A Transaction is an environment that allows for the editing of nodes
 * within a 'safe' context. Either all edits in a transaction are comitted, or all fail.
 * A Transaction acts as a cloud - all that can be done in a cloud can be done in a transaction.
 * I.e. A Node retrieved using the Transaction's getNode method resides in the Transaction -
 * if you change or remove the node, you can later roll it back by calling the Transaction's
 * cancel method.
 *
 * @author Pierre van Rooden
 */
public interface Transaction extends Cloud {

  	/**
     * Commits this transaction.
     * This has no effect if the transaction itself was 'nested' in another transaction.
     * In that case, nothing happens until the 'outer' Transaction commits.
     * This routine also removes the transaction as an 'active' transaction (it cannot be opened again).
     * @return <code>true<>/code> if the commit succeeded, <code>false</code> otherwise
     */
    public boolean commit();

  	/**
     * Cancels this transaction.
     * If the transaction itself was 'nested' in another transaction, that 'outer' transaction is also canceled.
     * This routine also removes the transaction (and all outer transactions) as an 'active' transaction (it cannot be opened again).
     */
    public void cancel();

}