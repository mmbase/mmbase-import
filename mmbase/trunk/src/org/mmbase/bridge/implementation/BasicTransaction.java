/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;
import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.TypeDef;
import java.util.*;

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
public class BasicTransaction extends BasicCloud implements Transaction {

    /**
    * The id of the transaction for use with the transaction manager.
    */
	protected String transactionContext;
	/**
	* The name of the transaction as used by the user.
	*/
	protected String transactionName = null;

    /**
    * Constructor to call from the CloudContext class.
    * (package only, so cannot be reached from a script)
    * @param transactionName name of the transaction (assigned by the user)
    * @param cloud The cloud this transaction is working on
    */
    BasicTransaction(String transactionName, BasicCloud cloud) {
        super(transactionName, cloud);
        this.transactionName=transactionName;

        // if the parent cloud is itself a transaction,
        // do not create a new one, just use that context instead!
        // this allows for nesting of transactions without loosing performance
        // due to additional administration
        if (parentCloud instanceof BasicTransaction) {
            transactionContext= ((BasicTransaction)parentCloud).transactionContext;
        } else {
            try {
                // XXX: the current transaction manager does not allow multiple transactions with the
                // same name for different users
                // We solved this here, but this should really be handled in the Transactionmanager.
                transactionContext = BasicCloudContext.transactionManager.create(account, account+"_"+transactionName);
            } catch (TransactionManagerException e) {
                throw new BasicBridgeException(e);
            }
        }
    }

  	/**
    * Commits this transaction.
    * This has no effect if the transaction itself was 'nested' in another transaction.
    * In that case, nothing happens until the 'outer' Transaction commits.
    * This routine also removes the transaction as an 'active' transaction (it cannot be opened again).
    * @return <code>true<>/code> if the commit succeeded, <code>false</code> otherwise
    */
    public boolean commit() {
        if (transactionContext==null) {
            throw new BasicBridgeException("No valid transaction : "+name);
        }
        // if this is a transaction within a transaction (theoretically possible)
        // leave the committing to the 'parent' transaction
        if (parentCloud instanceof Transaction) {
            // do nothing
        } else {
            try {
    		    BasicCloudContext.transactionManager.commit(account, transactionContext);
            } catch (TransactionManagerException e) {
                // do we drop the transaction here or delete the trans context?
                // return false;
                throw new BasicBridgeException(e);
            }
        }
        // remove the transaction from the parent cloud
        parentCloud.transactions.remove(transactionName);
        // clear the transactioncontext
        transactionContext=null;
        return true;
    }

  	/**
    * Cancels this transaction.
    * If the transaction itself was 'nested' in another transaction, that 'outer' transaction is also canceled.
    * This routine also removes the transaction (and all outer transactions) as an 'active' transaction (it cannot be opened again).
    */
    public void cancel() {
        if (transactionContext==null) {
            throw new BasicBridgeException("No valid transaction : "+name);
        }
        // if this is a transaction within a transaction (theoretically possible)
        // call the 'parent' transaction to cancel everything
        if (parentCloud instanceof Transaction) {
            ((Transaction)parentCloud).cancel();
        } else {
            try {
	    	    BasicCloudContext.transactionManager.cancel(account, transactionContext);
            } catch (TransactionManagerException e) {
                // do we drop the transaction here or delete the trans context?
                throw new BasicBridgeException(e);
            }
        }
        // remove the transaction from the parent cloud
        parentCloud.transactions.remove(transactionName);
        // clear the transactioncontext
        transactionContext=null;
    }

    /**
    * Transaction-notification: add a new temporary node to a transaction.
    * @param currentObjectContext the context of the object to add
    */
    void add(String currentObjectContext) {
        try {
		     BasicCloudContext.transactionManager.addNode(transactionContext, account,currentObjectContext);
        } catch (TransactionManagerException e) {
            throw new BasicBridgeException(e);
        }
    }

    /**
    * Transaction-notification: remove a temporary (not yet committed) node in a transaction.
    * @param currentObjectContext the context of the object to remove
    */
    void remove(String currentObjectContext) {
        try {
		     BasicCloudContext.transactionManager.removeNode(transactionContext,account,currentObjectContext);
        } catch (TransactionManagerException e) {
            throw new BasicBridgeException(e);
        }
    }

    /**
    * Transaction-notification: remove an existing node in a transaction.
    * @param currentObjectContext the context of the object to remove
    */
    void delete(String currentObjectContext) {
        try {
		     BasicCloudContext.transactionManager.deleteObject(transactionContext,account,currentObjectContext);
        } catch (TransactionManagerException e) {
            throw new BasicBridgeException(e);
        }
    }

    /**
    * Transaction-notification: ceheck whether a node exists in a transaction.
    * @param node the node to check
    */
    boolean contains(MMObjectNode node) {
        // additional check, so transaction can still get nodes after it has committed.
        if (transactionContext==null) {
            return false;
        }
        Vector v = BasicCloudContext.transactionManager.getNodes(account,transactionContext);
        return (v!=null) && (v.indexOf(node)!=-1);
    }

    /**
    * If this Transaction is scheduled to be garbage collected,
    * the transaction is canceled and cleaned up (unless it has already been committed/canceled, ofcourse, and
    * unless the parentcloud of a transaction is a transaction itself... in that case, the parent transaction should cancel!).
    * This means that a transaction is always cleared - if it 'times out', or is not properly removed, it will
    * eventually be removed from the MMBase cache.
    */
    protected void finalize() {
        if ((transactionContext!=null) && !(parentCloud instanceof Transaction)) {
            cancel();
        }
    }
}

