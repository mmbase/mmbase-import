/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;

import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;

/**
 * The absic implementation for a Transaction cLoud.
 * A Transaction cloud is a cloud which buffers allc hanegs made to nodes -
 * which means that chanegs are committed only if you commit the transaction itself.
 * This mechanism allows you to rollback changes if something goes wrong.
 * @author Pierre van Rooden
 * @version $Id: BasicTransaction.java,v 1.15 2003-12-17 09:16:44 michiel Exp $
 */
public class BasicTransaction extends BasicCloud implements Transaction {
    private static final Logger log = Logging.getLoggerInstance(BasicTransaction.class);

    /**
     * The id of the transaction for use with the transaction manager.
     */
    protected String transactionContext;
    /**
     * The name of the transaction as used by the user.
     */
    protected String transactionName = null;

    /*
     * Constructor to call from the CloudContext class.
     * Package only, so cannot be reached from a script.
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
                String message = e.getMessage();
                log.error(message);
                throw new BridgeException(message, e);
            }
        }
    }

    public boolean commit() {
        if (transactionContext==null) {
            throw new BridgeException("No valid transaction : " + name);
        }
        // if this is a transaction within a transaction (theoretically possible)
        // leave the committing to the 'parent' transaction
        if (parentCloud instanceof Transaction) {
            // do nothing
        } else {
            try {
                // BasicCloudContext.transactionManager.commit(account, transactionContext);
                BasicCloudContext.transactionManager.commit(userContext.getUserContext(), transactionContext);
            } catch (TransactionManagerException e) {
                // do we drop the transaction here or delete the trans context?
                // return false;
                String message = e.getMessage();
                log.error(message);
                throw new BridgeException(message, e);
            }
        }
        // remove the transaction from the parent cloud
        parentCloud.transactions.remove(transactionName);
        // clear the transactioncontext
        transactionContext=null;
        return true;
    }

    public void cancel() {
        if (transactionContext==null) {
            throw new BridgeException("No valid transaction : " + name);
        }
        // if this is a transaction within a transaction (theoretically possible)
        // call the 'parent' transaction to cancel everything
        if (parentCloud instanceof Transaction) {
            ((Transaction)parentCloud).cancel();
        } else {
            try {
            //   BasicCloudContext.transactionManager.cancel(account, transactionContext);
                BasicCloudContext.transactionManager.cancel(userContext.getUserContext(), transactionContext);
            } catch (TransactionManagerException e) {
                // do we drop the transaction here or delete the trans context?
                String message = e.getMessage();
                log.error(message);
                throw new BridgeException(message,e);
            }
        }
        // remove the transaction from the parent cloud
        parentCloud.transactions.remove(transactionName);
        // clear the transactioncontext
        transactionContext=null;
    }

    /*
     * Transaction-notification: add a new temporary node to a transaction.
     * @param currentObjectContext the context of the object to add
     */
    void add(String currentObjectContext) {
        try {
            BasicCloudContext.transactionManager.addNode(transactionContext, account,currentObjectContext);
        } catch (TransactionManagerException e) {
            String message = e.getMessage();
            log.error(message);
            throw new BridgeException(message,e);
        }
    }

    /*
     * Transaction-notification: remove a temporary (not yet committed) node in a transaction.
     * @param currentObjectContext the context of the object to remove
     */
    void remove(String currentObjectContext) {
        try {
            BasicCloudContext.transactionManager.removeNode(transactionContext,account,currentObjectContext);
        } catch (TransactionManagerException e) {
            String message = e.getMessage();
            log.error(message);
            throw new BridgeException(message,e);
        }
    }

    /*
     * Transaction-notification: remove an existing node in a transaction.
     * @param currentObjectContext the context of the object to remove
     */
    void delete(String currentObjectContext) {
        try {
            BasicCloudContext.transactionManager.deleteObject(transactionContext,account,currentObjectContext);
        } catch (TransactionManagerException e) {
            String message = e.getMessage();
            log.error(message);
            throw new BridgeException(message,e);
        }
    }

    /*
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
     * If this Transaction is scheduled to be garbage collected, the transaction is canceled and cleaned up.
     * Unless it has already been committed/canceled, ofcourse, and
     * unless the parentcloud of a transaction is a transaction itself.
     * In that case, the parent transaction should cancel!
     * This means that a transaction is always cleared - if it 'times out', or is not properly removed, it will
     * eventually be removed from the MMBase cache.
     */
    protected void finalize() {
        if ((transactionContext!=null) && !(parentCloud instanceof Transaction)) {
            cancel();
        }
    }
}

