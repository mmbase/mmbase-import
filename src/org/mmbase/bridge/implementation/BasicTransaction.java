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
import org.mmbase.security.UserContext;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;

/**
 * The basic implementation for a Transaction cLoud.
 * A Transaction cloud is a cloud which buffers all changes made to nodes -
 * which means that changes are committed only if you commit the transaction itself.
 * This mechanism allows you to rollback changes if something goes wrong.
 * @author Pierre van Rooden
 * @version $Id$
 */
public class BasicTransaction extends BasicCloud implements Transaction {

    private static final Logger log = Logging.getLoggerInstance(BasicTransaction.class);
    /**
     * The id of the transaction for use with the transaction manager. This is the name as speficied
     * by the user, prefixed by the account name.
     */
    protected String transactionName; // not final because of deserialization

    private boolean canceled = false;
    private boolean committed  = false;


    protected BasicCloud parentCloud; // not final because of deserialization

    private final Set<String> commitProcessed = new HashSet<String>();
    private final Set<String> deleteProcessed = new HashSet<String>();

    /**
     * @since MMBase 1.9
     */
    protected final Collection<MMObjectNode> getCoreNodes() {
        // if the parent cloud is itself a transaction,
        // do not create a new one, just use that context instead!
        // this allows for nesting of transactions without loosing performance
        // due to additional administration
        if (parentCloud instanceof BasicTransaction) {
            return ((BasicTransaction)parentCloud).getCoreNodes();
        } else {
            try {
                // XXX: the current transaction manager does not allow multiple transactions with the
                // same name for different users
                // We solved this here, but this should really be handled in the Transactionmanager.
                log.debug("using transaction " + transactionName);
                Collection<MMObjectNode> cn = BasicCloudContext.transactionManager.getTransactions().get(transactionName);
                if (cn == null) {
                    cn = BasicCloudContext.transactionManager.createTransaction(transactionName);
                }
                return cn;
            } catch (TransactionManagerException e) {
                throw new BridgeException(e.getMessage(), e);
            }
        }
    }

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
            // XXX: the current transaction manager does not allow multiple transactions with the
            // same name for different users
            // We solved this here, but this should really be handled in the Transactionmanager.
            transactionName = account + "_" + name;
            log.debug("using transaction " + transactionName);
            getCoreNodes(); // will call 'createTransaction
        }
    }

    /**
     */
    @Override
    String getAccount() {
        // should be something different than for normal clouds, so use the transaction-name
        return transactionName;
    }

    public NodeList getNodes() {
        return new BasicNodeList(getCoreNodes(), this);
    }


    boolean verify() {
        return BasicCloudContext.transactionManager.getTransactions().containsKey(transactionName);
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
            log.debug("Ignoring commit, because parent is transaction");
            // do nothing
        } else {
            try {
                assert BasicCloudContext.transactionManager.getTransaction(transactionName).size() == getNodes().size();

                if (log.isDebugEnabled()) {
                    log.debug("Resolving transaction '" + transactionName + "' with " + getCoreNodes().size() + " nodes");
                }

                BasicCloudContext.transactionManager.resolve(transactionName);

                List<Node> nodes = getNodes();

                if (log.isDebugEnabled()) {
                    log.debug("Committing " + nodes.size() + " nodes");
                }


                if (log.isDebugEnabled()) {
                    log.debug("Commitprocessing " + nodes.size() + " nodes");
                }

                // Now calling commit processors. They have real nodes numbers now.

                for (Node n : nodes) {
                    if (n == null) {
                        log.warn("Found null in transaction");
                        continue;
                    }
                    if (! n.isChanged() && ! n.isNew()) {
                        log.debug("Node not changed, not calling commit processors");
                        continue;
                    }
                    if (TransactionManager.EXISTS_NOLONGER.equals(n.getStringValue("_exists"))) {
                        log.debug("Deleted");
                        processDeleteProcessors(n);
                    } else {
                        log.debug("Committing");
                        processCommitProcessors(n);
                    }
                }

                if (log.isDebugEnabled()) {
                    log.debug("Resolving transaction '" + transactionName + "' again with " + getCoreNodes().size() + " nodes");
                }

                BasicCloudContext.transactionManager.resolve(transactionName);


                if (log.isDebugEnabled()) {
                    log.debug("Commit transaction '" + transactionName + "' with " + getCoreNodes().size() + " nodes");
                }


                BasicCloudContext.transactionManager.commit(userContext, transactionName);


            } catch (TransactionManagerException e) {
                // do we drop the transaction here or delete the trans context?
                // return false;
                throw new BridgeException(e.getMessage() + " for transaction with " + getNodes(), e);
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
            //   BasicCloudContext.transactionManager.cancel(account, transactionContext);
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
     */
    @Override
    int add(BasicNode node) {
        String id = "" + node.getNumber();
        String currentObjectContext = BasicCloudContext.tmpObjectManager.getObject(getAccount(), id);
        // store new temporary node in transaction
        add(currentObjectContext);
        node.setNode(BasicCloudContext.tmpObjectManager.getNode(getAccount(), id));
        //  check nodetype afterwards?
        return  node.getNumber();
    }

    /*
     * Transaction-notification: add a new temporary node to a transaction.
     * @param currentObjectContext the context of the object to add
     */
    @Override
    void add(String currentObjectContext) {
        try {
            BasicCloudContext.transactionManager.addNode(transactionName, getAccount(), currentObjectContext);
        } catch (TransactionManagerException e) {
            throw new BridgeException(e.getMessage(), e);
        }
    }


    @Override
    void createAlias(BasicNode node, String aliasName) {
        checkAlias(aliasName);
        try {
            String aliasContext = BasicCloudContext.tmpObjectManager.createTmpAlias(aliasName, getAccount(), "a" + node.temporaryNodeId, "" + node.temporaryNodeId);
            BasicCloudContext.transactionManager.addNode(transactionName, getAccount(), aliasContext);
        } catch (TransactionManagerException e) {
            throw new BridgeException(e.getMessage(), e);
        }
    }
    /*
     * Transaction-notification: remove a temporary (not yet committed) node in a transaction.
     * @param currentObjectContext the context of the object to remove
     */
    @Override
    void remove(String currentObjectContext) {
        try {
            BasicCloudContext.transactionManager.removeNode(transactionName, getAccount(), currentObjectContext);
        } catch (TransactionManagerException e) {
            throw new BridgeException(e.getMessage(), e);
        }
    }
    @Override
    void remove(MMObjectNode node) {
        String oMmbaseId = "" + node.getValue("number");
        String currentObjectContext = BasicCloudContext.tmpObjectManager.getObject(getAccount(),  oMmbaseId);
        add(currentObjectContext);
        delete(currentObjectContext);
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

    @Override
    boolean contains(MMObjectNode node) {
        // additional check, so transaction can still get nodes after it has committed.
        if (transactionName == null) {
            return false;
        }
        try {
            Collection<MMObjectNode> transaction = BasicCloudContext.transactionManager.getTransaction(transactionName);
            return transaction.contains(node);
        } catch (TransactionManagerException tme) {
            throw new BridgeException(tme.getMessage(), tme);
        }
    }

    @Override
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
    @Override
    protected void finalize() {
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
    @Override
    public Object getProperty(Object key) {
        Object value = super.getProperty(key);
        if (value == null) {
            return parentCloud.getProperty(key);
        } else {
            return value;
        }
    }
    @Override
    @SuppressWarnings("unchecked")
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


    @Override
    public String toString() {
        UserContext uc = getUser();
        return  "BasicTransaction " + count +  "'" + getName() + "' of " + (uc != null ? uc.getIdentifier() : "NO USER YET") + " @" + Integer.toHexString(hashCode());
    }

    @Override
    public Cloud getNonTransactionalCloud() {
        return parentCloud.getNonTransactionalCloud();
    }


    @Override
    protected void processDeleteProcessors(Node n) {
        String tempId = n.getStringValue("_number");
        if (! deleteProcessed.contains(tempId)) {
            super.processDeleteProcessors(n);
            deleteProcessed.add(tempId);
        } else {
        }
    }

    @Override
    protected  void processCommitProcessors(Node n) {
        String tempId = n.getStringValue("_number");
        if (! commitProcessed.contains(tempId)) {
            super.processCommitProcessors(n);
            commitProcessed.add(tempId);
        } else {
            log.debug("Not commit processing " + n + " because that happend already (Call to commit?");
        }
    }
    @Override
    protected void setValue(BasicNode node, String fieldName, Object value) {
        super.setValue(node, fieldName, value);
        // changed, so commit processor _must_ be called again
        String tempId = node.getStringValue("_number");
        commitProcessed.remove(tempId);
    }
}

