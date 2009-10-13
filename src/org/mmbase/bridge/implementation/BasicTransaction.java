/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;

import java.util.*;
import java.io.*;
import org.mmbase.bridge.*;
import org.mmbase.security.*;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;

/**
 * The basic implementation for a Transaction cLoud.
 * A Transaction cloud is a cloud which buffers allc hanegs made to nodes -
 * which means that chanegs are committed only if you commit the transaction itself.
 * This mechanism allows you to rollback changes if something goes wrong.
 * @author Pierre van Rooden
 * @version $Id$
 */
public class BasicTransaction extends BasicCloud implements Transaction {

    private static final long serialVersionUID = 1;

    private static final Logger log = Logging.getLoggerInstance(BasicTransaction.class);
    /**
     * The id of the transaction for use with the transaction manager.
     */
    protected String transactionName;

    private boolean canceled = false;
    private boolean committed  = false;


    protected  BasicCloud parentCloud;

    /*
     * Constructor to call from the CloudContext class.
     * Package only, so cannot be reached from a script.
     * @param transactionName name of the transaction (assigned by the user)
     * @param cloud The cloud this transaction is working on
     */
    BasicTransaction(String name, BasicCloud cloud) {
        super(name, cloud);
        this.parentCloud = cloud;

        // if the parent cloud is itself a transaction,
        // do not create a new one, just use that context instead!
        // this allows for nesting of transactions without loosing performance
        // due to additional administration
        if (parentCloud instanceof BasicTransaction) {
            transactionName = ((BasicTransaction)parentCloud).transactionName;
        } else {
            try {
                // XXX: the current transaction manager does not allow multiple transactions with the
                // same name for different users
                // We solved this here, but this should really be handled in the Transactionmanager.
                transactionName = account + "_" + name;
                BasicCloudContext.transactionManager.createTransaction(transactionName);
            } catch (TransactionManagerException e) {
                throw new BridgeException(e.getMessage(), e);
            }
        }
    }

    /**
     */
    String getAccount() {
        return transactionName;
    }


    public synchronized boolean commit() {
        if (canceled) {
            throw new BridgeException("Cannot commit transaction'" + name + "' (" + transactionName +"), it was already canceled.");
        }
        if (committed) {
            throw new BridgeException("Cannot commit transaction'" + name + "' (" + transactionName +"), it was already committed.");
        }
        log.debug("Committing transaction " + transactionName);

        parentCloud.transactions.remove(getName());  // hmpf

        // if this is a transaction within a transaction (theoretically possible)
        // leave the committing to the 'parent' transaction
        if (parentCloud instanceof Transaction) {
            // do nothing
        } else {
            try {
                Collection col = BasicCloudContext.transactionManager.getTransaction(transactionName);
                // BasicCloudContext.transactionManager.commit(account, transactionName);
                BasicCloudContext.transactionManager.commit(userContext, transactionName);
                // This is a hack to call the commitprocessors which are only available in the bridge.
                // The EXISTS_NOLONGER check is required to prevent committing of deleted nodes.
                Iterator i = col.iterator();
                while (i.hasNext()) {
                    MMObjectNode n = (MMObjectNode) i.next();
                    if (!TransactionManager.EXISTS_NOLONGER.equals(n.getStringValue("_exists"))) {
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
            throw new BridgeException("Cannot cancel transaction'" + name + "' (" + transactionName +"), it was already canceled.");
        }
        if (committed) {
            throw new BridgeException("Cannot cancel transaction'" + name + "' (" + transactionName +"), it was already committed.");
        }

        // if this is a transaction within a transaction (theoretically possible)
        // call the 'parent' transaction to cancel everything
        if (parentCloud instanceof Transaction) {
            ((Transaction)parentCloud).cancel();
        } else {
            try {
                BasicCloudContext.transactionManager.cancel(userContext, transactionName);
            } catch (TransactionManagerException e) {
                // do we drop the transaction here or delete the trans context?
                throw new BridgeException(e.getMessage(), e);
            }
        }
        // remove the transaction from the parent cloud
        parentCloud.transactions.remove(getName());
        canceled = true;
    }

    /*
     * Transaction-notification: add a new temporary node to a transaction.
     * @param currentObjectContext the context of the object to add
     */
    void add(String currentObjectContext) {
        try {
            BasicCloudContext.transactionManager.addNode(transactionName, getAccount(), currentObjectContext);
        } catch (TransactionManagerException e) {
            throw new BridgeException(e.getMessage(), e);
        }
    }

    int add(BasicNode node) {
        String id = "" + node.getNumber();
        String currentObjectContext = BasicCloudContext.tmpObjectManager.getObject(getAccount(), id, id);
        // store new temporary node in transaction
        add(currentObjectContext);
        node.setNode(BasicCloudContext.tmpObjectManager.getNode(getAccount(), id));
        //  check nodetype afterwards?
        return  node.getNumber();
    }

    /*
     * Transaction-notification: remove a temporary (not yet committed) node in a transaction.
     * @param currentObjectContext the context of the object to remove
     */
    void remove(String currentObjectContext) {
        try {
            BasicCloudContext.transactionManager.removeNode(transactionName, getAccount(), currentObjectContext);
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
            BasicCloudContext.transactionManager.deleteObject(transactionName, getAccount(), currentObjectContext);
        } catch (TransactionManagerException e) {
            throw new BridgeException(e.getMessage(), e);
        }
    }

    boolean contains(MMObjectNode node) {
        // additional check, so transaction can still get nodes after it has committed.
        if (transactionName == null) {
            return false;
        }
        try {
            Collection transaction = BasicCloudContext.transactionManager.get(getAccount(), transactionName);
            return transaction.contains(node);
        } catch (TransactionManagerException tme) {
            throw new BridgeException(tme.getMessage(), tme);
        }
    }


    BasicNode makeNode(MMObjectNode node, String nodeNumber) {
        if (committed) {
            return parentCloud.makeNode(node, nodeNumber);
        } else {
            return super.makeNode(node, nodeNumber);
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
        log.debug("Canceling transaction " + this + " because this object is garbage collected");
        if ((transactionName != null) && !(parentCloud instanceof Transaction)) {
            cancel();
        }
    }

    public boolean isCanceled() {
        return canceled;
    }
    public boolean isCommitted() {
        return committed;
    }

    public Object getProperty(Object key) {
        Object value = super.getProperty(key);
        if (value == null) {
            return parentCloud.getProperty(key);
        } else {
            return value;
        }
    }
    public Map getProperties() {
        Map ret = new HashMap();
        ret.putAll(parentCloud.getProperties());
        ret.putAll(super.getProperties());
        return Collections.unmodifiableMap(ret);
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


    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        _readObject(in);
        transactionName = (String) in.readObject();
        canceled = in.readBoolean();
        committed = in.readBoolean();
        parentCloud = (BasicCloud) in.readObject();
    }


    private void writeObject(ObjectOutputStream out) throws IOException {
        _writeObject(out);
        out.writeObject(transactionName);
        out.writeBoolean(canceled);
        out.writeBoolean(committed);
        out.writeObject(parentCloud);
    }

    public String toString() {
        UserContext uc = getUser();
        return  "BasicTransaction" + count + " '" + getName() + "' of " + parentCloud.toString();
    }

    /*
    public Cloud getNonTransactionalCloud() {
        return parentCloud.getNonTransactionalCloud();
    }
    */

}

