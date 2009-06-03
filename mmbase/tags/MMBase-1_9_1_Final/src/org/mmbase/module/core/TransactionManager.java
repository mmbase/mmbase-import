/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

import java.util.*;
import org.mmbase.module.corebuilders.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.security.*;

/**
 * The MMBase transaction manager manages a group of changes.
 * @javadoc
 *
 * @author Rico Jansen
 * @version $Id$
 */
public class TransactionManager {

    private static final Logger log = Logging.getLoggerInstance(TransactionManager.class);

    static final String        EXISTS_NO         = "no";
    private static final int   I_EXISTS_NO       = 0;
    static final String        EXISTS_YES        = "yes";
    private static final int   I_EXISTS_YES      = 1;
    public static final String        EXISTS_NOLONGER   = "nolonger";
    private static final int   I_EXISTS_NOLONGER = 2;

    private TemporaryNodeManager tmpNodeManager;
    private TransactionResolver transactionResolver;

    protected Map<String, Collection<MMObjectNode>> transactions = new HashMap<String, Collection<MMObjectNode>>();

    public static TransactionManager instance;

    /**
     * @since MMBase-1.9
     */
    public static TransactionManager getInstance() {
        if (instance == null) {
            instance = new TransactionManager();
        }
        return instance;
    }

    private TransactionManager() {
        // singleton
    }

    public TemporaryNodeManager getTemporaryNodeManager() {
        if (tmpNodeManager == null) {
            tmpNodeManager = new TemporaryNodeManager(MMBase.getMMBase());
        }
        return tmpNodeManager;

    }
    private TransactionResolver getTransactionResolver() {
        if (transactionResolver == null) {
            transactionResolver = new TransactionResolver(MMBase.getMMBase());
        }
        return transactionResolver;
    }

    /**
     * Returns transaction with given name.
     * @param transactionName The name of the transaction to return
     * @exception TransactionManagerExcpeption if the transaction with given name does not exist
     * @return Collection containing the nodes in this transaction (as {@link org.mmbase.module.core.MMObjectNode}s).
     */
    synchronized public  Collection<MMObjectNode> getTransaction(String transactionName) throws TransactionManagerException {
        Collection<MMObjectNode> transaction = transactions.get(transactionName);
        if (transaction == null) {
            throw new TransactionManagerException("Transaction " + transactionName + " does not exist (existing are " + transactions.keySet() + ")");
        } else {
            return transaction;
        }
    }

    /**
     * Return a an unmodifable Map with all transactions. This map can be used to explore the
     * existing transactions.
     *
     * @since MMBase-1.9
     */
    public Map<String, Collection<MMObjectNode>> getTransactions() {
        return Collections.unmodifiableMap(transactions);
    }

    /**
     * Creates transaction with given name.
     * @param transactionName The name of the transaction to return
     * @exception TransactionManagerExcpeption if the transaction with given name existed already
     * @return Collection containing the nodes in this transaction (so, this is an empty collection)
     */
    synchronized public Collection<MMObjectNode> createTransaction(String transactionName) throws TransactionManagerException {
        if (!transactions.containsKey(transactionName)) {
            List<MMObjectNode> transactionNodes = new ArrayList<MMObjectNode>();
            transactions.put(transactionName, transactionNodes);
            return transactionNodes;
        } else {
            throw new TransactionManagerException("Transaction " + transactionName + " already exists");
        }
    }

    /**
     * Removes the transaction with given name
     * @return the collection with nodes from the removed transaction or <code>null</code> if no transaction with this name existed
     */
    synchronized protected Collection<MMObjectNode> deleteTransaction(String transactionName) {
        return transactions.remove(transactionName);
    }


    public String addNode(String transactionName, String owner, String tmpnumber) throws TransactionManagerException {
        Collection<MMObjectNode> transaction = getTransaction(transactionName);
        MMObjectNode node = getTemporaryNodeManager().getNode(owner, tmpnumber);
        if (node != null) {
            if (!transaction.contains(node)) {
                try {
                    transaction.add(node);
                } catch (UnsupportedOperationException uoe) {
                    transactions.remove(transactionName);
                    throw new TransactionManagerException("Cannot add node " + node + " to transaction '" + transactionName + "'. It probably was committed already. This transaction is removed now.", uoe);
                }
//            } else {
//                throw new TransactionManagerException(
//                    "Node " + tmpnumber + " not added as it was already in transaction " + transactionName);
            }
        } else {
            throw new TransactionManagerException("Node " + tmpnumber + " doesn't exist.");
        }
        return tmpnumber;
    }

    public String removeNode(String transactionName, String owner, String tmpnumber) throws TransactionManagerException {
        Collection<MMObjectNode> transaction = getTransaction(transactionName);
        MMObjectNode node = getTemporaryNodeManager().getNode(owner, tmpnumber);
        if (node!=null) {
            if (transaction.contains(node)) {
                transaction.remove(node);
//            } else {
//                throw new TransactionManagerException("Node " + tmpnumber + " is not in transaction " + transactionName);
            }
        } else {
            throw new TransactionManagerException("Node " + tmpnumber + " doesn't exist.");
        }
        return tmpnumber;
    }

    public String deleteObject(String transactionName, String owner, String tmpnumber) throws TransactionManagerException    {
        Collection<MMObjectNode> transaction = getTransaction(transactionName);
        MMObjectNode node = getTemporaryNodeManager().getNode(owner, tmpnumber);
        if (node != null) {
            if (transaction.contains(node)) {
                // Mark it as deleted
                node.storeValue(MMObjectBuilder.TMP_FIELD_EXISTS, EXISTS_NOLONGER);
             } else {
                throw new TransactionManagerException("Node " + tmpnumber + " is not in transaction " + transactionName);
            }
        } else {
            throw new TransactionManagerException("Node " + tmpnumber + " doesn't exist.");
        }
        return tmpnumber;
    }

    public String cancel(Object user, String transactionName) throws TransactionManagerException {
        Collection<MMObjectNode> transaction = getTransaction(transactionName);
        // remove nodes from the temporary node cache
        MMObjectBuilder builder = MMBase.getMMBase().getTypeDef();
        for (MMObjectNode node : transaction) {
            builder.removeTmpNode(node.getStringValue(MMObjectBuilder.TMP_FIELD_NUMBER));
        }
        deleteTransaction(transactionName);
        if (log.isDebugEnabled()) {
            log.debug("Removed transaction (after cancel) " + transactionName + "\n" + transaction);
        }
        return transactionName;
    }

    /**
     * @todo Review this stuff..
     * @since MMBase-1.9
     */
    public boolean resolve(String transactionName) throws TransactionManagerException {

        // MM: I think we need an actual Transaction object! with e.g. a property 'resolved'.

        Collection<MMObjectNode> transaction = getTransaction(transactionName);
        if (transaction instanceof ArrayList) { // a bit of a trick to see if it is committed already
            try {
                getTransactionResolver().resolve(transaction);
            } catch (TransactionManagerException te) {
                throw new TransactionManagerException("Can't resolve transaction " + transactionName + " (it has " + transaction.size() + " nodes)", te);
            }
        } else {
            log.service("Committed already " + transaction.getClass());
            return false;
        }
        return true;
    }

    public String commit(Object user, String transactionName) throws TransactionManagerException {
        Collection<MMObjectNode> transaction = getTransaction(transactionName);
        try {
            resolve(transactionName);
            transactions.put(transactionName, Collections.unmodifiableCollection(transaction)); // makes it recognizable, and also the transaction is unusable after that
            if (!performCommits(user, transaction)) {
                throw new TransactionManagerException("Can't commit transaction " + transactionName);
            }


        } finally {
            // remove nodes from the temporary node cache
            MMObjectBuilder builder = MMBase.getMMBase().getTypeDef();
            for (MMObjectNode node : transaction) {
                builder.removeTmpNode(node.getStringValue(MMObjectBuilder.TMP_FIELD_NUMBER));
            }
            deleteTransaction(transactionName);
            if (log.isDebugEnabled()) {
                log.debug("Removed transaction (after commit) " + transactionName + "\n" + transaction);
            }
        }
        return transactionName;
    }


    private enum State {
        UNCOMMITED,
        COMMITED,
        FAILED,
        NODE ,
        RELATION
    }


    private class NodeState {
        public int exists;
        public State state;
        public boolean changed = true;
    }

    /**
     * @since MMBase-1.9
     */
    private void commitNode(Object user, MMObjectNode node, NodeState state) {

        if (state.exists == I_EXISTS_YES ) {
            if (! state.changed) return;
            // Commit also if not changed, because the node may have been deleted or changed by
            // someone else. It is like this in the transaction it should be saved like this.
            // See also MMB-1680
            // TODO: what is the performance penalty here?


            // use safe commit, which locks the node cache
            boolean commitOK;
            if (user instanceof UserContext) {
                commitOK = node.commit((UserContext)user);
            } else {
                commitOK = node.parent.safeCommit(node);
            }
            if (commitOK) {
                state.state = State.COMMITED;
                node.storeValue(MMObjectBuilder.TMP_FIELD_RESOLVED, null);

            } else {
                state.state = State.FAILED;
            }
        } else if (state.exists == I_EXISTS_NO ) {
            int insertOK;
            if (user instanceof UserContext) {
                insertOK = node.insert((UserContext)user);
            } else {
                String username = findUserName(user);
                insertOK = node.parent.safeInsert(node, username);
            }
            if (insertOK > 0) {
                state.state = State.COMMITED;
                node.storeValue(MMObjectBuilder.TMP_FIELD_RESOLVED, null);
            } else {
                state.state = State.FAILED;
                String message = "When this failed, it is possible that the creation of an insrel went right, which leads to a database inconsistency..  stop now.. (transaction 2.0: [rollback?])";
                throw new RuntimeException(message);
            }
        }
    }


    boolean performCommits(Object user, Collection<MMObjectNode> nodes) {
        if (nodes == null || nodes.size() == 0) {
            log.debug("Empty list of nodes");
            return true;
        }

        Map<Integer, NodeState> stati = new HashMap<Integer, NodeState>();

        log.debug("Checking types and existance");

        for (MMObjectNode node : nodes) {
            // Nodes are uncommited by default
            NodeState state = new NodeState();
            state.state = State.UNCOMMITED;
            state.changed = node.isChanged() || node.isNew();
            String exists = (String) node.getValue(MMObjectBuilder.TMP_FIELD_EXISTS);
            if (exists == null) {
                throw new IllegalStateException("The _exists field does not exist on node "+node);
            } else if (exists.equals(EXISTS_NO)) {
                state.exists  = I_EXISTS_NO;
            } else if (exists.equals(EXISTS_YES)) {
                state.exists = I_EXISTS_YES;
            } else if (exists.equals(EXISTS_NOLONGER)) {
                state.exists = I_EXISTS_NOLONGER;
            } else {
                throw new IllegalStateException("Invalid value for _exists '" + exists + "' on node " + node);
            }
            stati.put(node.getNumber(), state);
        }

        // Now set the 'changed' flag of all node to or from a relation was made.
        // Related to MMB-1680
        for (MMObjectNode node : nodes) {
            if (node.getBuilder() instanceof InsRel) {
                NodeState state = stati.get(node.getNumber());
                if (state.changed) {
                    NodeState sstate = stati.get(node.getIntValue("snumber"));
                    if (sstate != null) sstate.changed = true;
                    NodeState dstate = stati.get(node.getIntValue("dnumber"));
                    if (dstate != null) dstate.changed = true;
                }

            }
        }

        log.debug("Commiting nodes");

        // First commit all the NODES
        for (MMObjectNode node : nodes) {
            if (!(node.getBuilder() instanceof InsRel)) {
                NodeState state = stati.get(node.getNumber());
                commitNode(user, node, state);
            }
        }

        log.debug("Commiting relations");

        // Then commit all the RELATIONS
        for (MMObjectNode node : nodes) {
            if (node.getBuilder() instanceof InsRel) {
                NodeState state = stati.get(node.getNumber());
                commitNode(user, node, state);
            }
        }

        log.debug("Deleting relations");

        // Then commit all the RELATIONS that must be deleted
        for (MMObjectNode node : nodes) {
            NodeState state = stati.get(node.getNumber());
            if (node.getBuilder() instanceof InsRel && state.exists == I_EXISTS_NOLONGER) {
                // no return information
                if (user instanceof UserContext) {
                    node.remove((UserContext)user);
                } else {
                    node.parent.removeNode(node);
                }
                state.state = State.COMMITED;
            }
        }

        log.debug("Deleting nodes");
        // Then commit all the NODES that must be deleted
        for (MMObjectNode node : nodes) {
            NodeState state = stati.get(node.getNumber());
            if (!(node.getBuilder() instanceof InsRel) && (state.exists== I_EXISTS_NOLONGER)) {
                // no return information
                if (user instanceof UserContext) {
                    node.remove((UserContext)user);
                } else {
                    node.parent.removeNode(node);
                }
                state.state = State.COMMITED;
            }
        }

        // check for failures
        boolean okay = true;
        for (MMObjectNode node : nodes) {
            NodeState state = stati.get(node.getNumber());
            if (state.state == State.FAILED) {
                okay = false;
                log.error("Failed node " + node.toString());
            }
        }
        return okay;
    }

    public String findUserName(Object user) {
        if (user instanceof UserContext) {
            return ((UserContext)user).getIdentifier();
        } else {
            return "";
        }
    }

}
