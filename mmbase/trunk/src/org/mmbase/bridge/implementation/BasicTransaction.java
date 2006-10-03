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
 * The basic implementation for a Transaction cLoud.
 * A Transaction cloud is a cloud which buffers allc hanegs made to nodes -
 * which means that chanegs are committed only if you commit the transaction itself.
 * This mechanism allows you to rollback changes if something goes wrong.
 * @author Pierre van Rooden
 * @version $Id: BasicTransaction.java,v 1.26 2006-10-03 18:30:11 michiel Exp $
 */
public class BasicTransaction extends BasicCloud implements Transaction {

    private static final Logger log = Logging.getLoggerInstance(BasicTransaction.class);
    /**
     * The id of the transaction for use with the transaction manager.
     */
    protected final String transactionContext;

    private boolean canceled = false;
    private boolean committed  = false;
    /**
     * The name of the transaction as used by the user.
     */
    protected final String transactionName;

    protected final BasicCloud parentCloud;

    /*
     * Constructor to call from the CloudContext class.
     * Package only, so cannot be reached from a script.
     * @param transactionName name of the transaction (assigned by the user)
     * @param cloud The cloud this transaction is working on
     */
    BasicTransaction(String transactionName, BasicCloud cloud) {
        super(transactionName, cloud);
        this.transactionName = transactionName;
        this.parentCloud = cloud;

        // if the parent cloud is itself a transaction,
        // do not create a new one, just use that context instead!
        // this allows for nesting of transactions without loosing performance
        // due to additional administration
        if (parentCloud instanceof BasicTransaction) {
            transactionContext = ((BasicTransaction)parentCloud).transactionContext;
        } else {
            try {
                // XXX: the current transaction manager does not allow multiple transactions with the
                // same name for different users
                // We solved this here, but this should really be handled in the Transactionmanager.
                transactionContext = account + "_" + transactionName;
                BasicCloudContext.transactionManager.createTransaction(transactionContext);
            } catch (TransactionManagerException e) {
                throw new BridgeException(e.getMessage(), e);
            }
        }
    }


    public synchronized boolean commit() {
        if (canceled) {
            throw new BridgeException("Cannot commit transaction'" + name + "' (" + transactionContext +"), it was already canceled.");
        }
        if (committed) {
            throw new BridgeException("Cannot commit transaction'" + name + "' (" + transactionContext +"), it was already committed.");
        }
        log.debug("Committing transaction " + transactionContext);

        parentCloud.transactions.remove(transactionName);  // hmpf

        // if this is a transaction within a transaction (theoretically possible)
        // leave the committing to the 'parent' transaction
        if (parentCloud instanceof Transaction) {
            // do nothing
        } else {
            try {
                Collection<MMObjectNode> col = BasicCloudContext.transactionManager.getTransaction(transactionContext);
                // BasicCloudContext.transactionManager.commit(account, transactionContext);
                BasicCloudContext.transactionManager.commit(userContext, transactionContext);
                // This is a hack to call the commitprocessors which are only available in the bridge.
                // The EXISTS_NOLONGER check is required to prevent committing of deleted nodes.
                for (MMObjectNode n : col) {
                    if (! n.isChanged()) continue;
                    if (!TransactionManager.EXISTS_NOLONGER.equals(n.getStringValue(MMObjectBuilder.TMP_FIELD_EXISTS))) {
                        Node node = parentCloud.makeNode(n, "" + n.getNumber());
                        node.commit();
                    }
                }
            } catch (TransactionManagerException e) {
                // do we drop the transaction here or delete the trans context?
                // return false;
                throw new BridgeException(e.getMessage(), e);
            }
        }

        committed = true;
        return true;
    }

    public synchronized void cancel() {
        if (canceled) {
            throw new BridgeException("Cannot cancel transaction'" + name + "' (" + transactionContext +"), it was already canceled.");
        }
        if (committed) {
            throw new BridgeException("Cannot cancel transaction'" + name + "' (" + transactionContext +"), it was already committed.");
        }

        // if this is a transaction within a transaction (theoretically possible)
        // call the 'parent' transaction to cancel everything
        if (parentCloud instanceof Transaction) {
            ((Transaction)parentCloud).cancel();
        } else {
            try {
            //   BasicCloudContext.transactionManager.cancel(account, transactionContext);
                BasicCloudContext.transactionManager.cancel(userContext, transactionContext);
            } catch (TransactionManagerException e) {
                // do we drop the transaction here or delete the trans context?
                throw new BridgeException(e.getMessage(), e);
            }
        }
        // remove the transaction from the parent cloud
        parentCloud.transactions.remove(transactionName);
        canceled = true;
    }

    /*
     * Transaction-notification: add a new temporary node to a transaction.
     * @param currentObjectContext the context of the object to add
     */
    void add(String currentObjectContext) {
        try {
            BasicCloudContext.transactionManager.addNode(transactionContext, account, currentObjectContext);
        } catch (TransactionManagerException e) {
            throw new BridgeException(e.getMessage(), e);
        }
    }

    /*
     * Transaction-notification: remove a temporary (not yet committed) node in a transaction.
     * @param currentObjectContext the context of the object to remove
     */
    void remove(String currentObjectContext) {
        try {
            BasicCloudContext.transactionManager.removeNode(transactionContext, account, currentObjectContext);
        } catch (TransactionManagerException e) {
            throw new BridgeException(e.getMessage(), e);
        }
    }

    void delete(String currentObjectContext, MMObjectNode node) {
        delete(currentObjectContext);
    }
    /*
     * Transaction-notification: remove an existing node in a transaction.
     * @param currentObjectContext the context of the object to remove
     */
    void delete(String currentObjectContext) {
        try {
            BasicCloudContext.transactionManager.deleteObject(transactionContext, account, currentObjectContext);
        } catch (TransactionManagerException e) {
            throw new BridgeException(e.getMessage(), e);
        }
    }

    boolean contains(MMObjectNode node) {
        // additional check, so transaction can still get nodes after it has committed.
        if (transactionContext == null) {
            return false;
        }
        try {
            Collection transaction = BasicCloudContext.transactionManager.get(account, transactionContext);
            return transaction.contains(node);
        } catch (TransactionManagerException tme) {
            throw new BridgeException(tme.getMessage(), tme);
        }
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
        if ((transactionContext != null) && !(parentCloud instanceof Transaction)) {
            cancel();
        }
    }

    public boolean isCanceled() {
        return canceled;
    }
    public boolean isCommitted() {
        return committed;
    }

    /**
     * @see org.mmbase.bridge.Transaction#getCloudName()
     */
    public String getCloudName() {
        if (parentCloud instanceof Transaction) {
            return ((Transaction) parentCloud).getCloudName();
        }
        else {
            return parentCloud.getName();
        }
    }
}

