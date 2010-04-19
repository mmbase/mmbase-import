/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.util;

import java.util.*;
import java.util.concurrent.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.implementation.*;
import org.mmbase.security.*;
import org.mmbase.util.functions.*;

/**
 * This abstract implementation of Cloud implements all methods which are based on implementations of
 * other methods, and implements things which many cloud implementataion likely have to do
 * themselves, like properties.
 *
 * @author  Michiel Meeuwissen
 * @version $Id: MapNode.java 36154 2009-06-18 22:04:40Z michiel $
 * @since   MMBase-1.9.2
 */

public abstract class AbstractCloud implements Cloud {

    private final Map<Object, Object> properties = new ConcurrentHashMap<Object, Object>();
    protected final Map<String, Transaction> transactions = new HashMap<String, Transaction>();
    private Locale locale = Locale.getDefault();

    private final String name;
    private final UserContext userContext;

    public AbstractCloud(String n,  UserContext uc) {
        name = n;
        userContext = uc;
    }


    @Override
    public abstract Node getNode(int number);


    @Override
    public Node getNode(String number) throws NotFoundException {
        try {
            int n = Integer.parseInt(number);
            return getNode(n);
        } catch (NumberFormatException nfe) {
            return getNodeByAlias(number);
        }
    }


    @Override
    public Node getNodeByAlias(String alias) throws NotFoundException {
        throw new NotFoundException("No node with alias '" + alias + "' (aliases not supported)");
    }

    @Override
    public Relation getRelation(int number) throws NotFoundException {
        return (Relation) getNode(number);
    }
    @Override
    public Relation getRelation(String number) throws NotFoundException {
        return (Relation) getNode(number);
    }

    @Override
    public boolean hasNode(int number) {
        try {
            return getNode(number) != null;
        } catch (NotFoundException e) {
            return false;
        }
    }

    @Override
    public boolean hasNode(String number) {
        try {
            return hasNode(Integer.parseInt(number));
        } catch (Exception e) {
            return false;
        }
    }
    @Override
    public boolean hasRelation(int number) {
        return hasNode(number) && getNode(number) instanceof Relation;
    }
    @Override
    public boolean hasRelation(String number) {
        return hasNode(number) && getNode(number) instanceof Relation;
    }

    @Override
    public boolean mayRead(int number) {
        getNode(number);
        return true;
    }

    @Override
    public boolean may(org.mmbase.security.Action action, org.mmbase.util.functions.Parameters parameters) {
        return true;
    }

    @Override
    public boolean mayRead(String number) {
        getNode(number);
        return true;
    }

    @Override
    public abstract NodeManagerList getNodeManagers();

    @Override
    public abstract NodeManager getNodeManager(String name);

    @Override
    public boolean hasNodeManager(String name) {
        try {
            return getNodeManager(name) != null;
        } catch (NotFoundException e) {
            return false;
        }
    }

    /**
     * On default we don't associate number id's with node managers
     */
    @Override
    public NodeManager getNodeManager(int nodeManagerId) throws NotFoundException {
        throw new NotFoundException();
    }


    /**
     * On default we don't associated number id's with node managers
     */
    @Override
    public RelationManager getRelationManager(int relationManagerId) throws NotFoundException {
        throw new NotFoundException();
    }
    @Override
    public RelationManager getRelationManager(String roleName) throws NotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public RelationManager getRelationManager(String sourceManagerName, String destinationManagerName, String roleName) throws NotFoundException {
        return getRelationManager(getNodeManager(sourceManagerName), getNodeManager(destinationManagerName), roleName);
    }


    @Override
    public RelationManager getRelationManager(NodeManager sourceManager, NodeManager destinationManager, String roleName) throws NotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean hasRelationManager(String sourceManagerName, String destinationManagerName, String roleName) {
        return hasRelationManager(getNodeManager(sourceManagerName), getNodeManager(destinationManagerName), roleName);
    }

    @Override
    public boolean hasRelationManager(NodeManager sourceManager, NodeManager destinationManager, String roleName) {
        try {
            getRelationManager(sourceManager, destinationManager, roleName);
            return true;
        } catch (NotFoundException nfe) {
            return false;
        }
    }

    @Override
    public boolean hasRole(String roleName) {
        try {
            getRelationManager(roleName);
            return true;
        } catch (NotFoundException nfe) {
            return false;
        }
    }

    @Override
    public boolean hasRelationManager(String roleName) {
        return hasRole(roleName);
    }


    @Override
    public RelationManagerList getRelationManagers() {
        throw new UnsupportedOperationException();
    }


    @Override
    public RelationManagerList getRelationManagers(String sourceManagerName, String destinationManagerName,  String roleName) throws NotFoundException {
        return getRelationManagers(getNodeManager(sourceManagerName), getNodeManager(destinationManagerName), roleName);

    }

    @Override
    public RelationManagerList getRelationManagers(NodeManager sourceManager, NodeManager destinationManager,
                                                   String roleName) throws NotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public abstract CloudContext getCloudContext();


    @Override
    public Transaction createTransaction() {
        return createTransaction(null, false);
    }


    @Override
    public Transaction createTransaction(String name) throws AlreadyExistsException {
        return createTransaction(name, false);
    }


    @Override
    public Transaction createTransaction(String name, boolean overwrite) throws AlreadyExistsException {
        if (transactions.containsKey(name)) {
            throw new AlreadyExistsException("Transaction '" + name + "' already exists");
        }
        Transaction trans = newTransaction(name);
        transactions.put(name, trans);
        return trans;
    }

    protected Transaction newTransaction(String name) {
        throw new UnsupportedOperationException();
    }


    @Override
    public Transaction getTransaction(String name) {
        Transaction tran = transactions.get(name);
        if (tran == null) {
            tran = createTransaction(name, false);
        }
        return tran;
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return getName();
    }

    @Override
    public UserContext getUser() {
        return userContext;
    }

    //javadoc inherited
    @Override
    public NodeList getList(
        String startNodes,
        String nodePath,
        String fields,
        String constraints,
        String orderby,
        String directions,
        String searchDir,
        boolean distinct) {

        if ((nodePath==null) || nodePath.equals("")) throw new BridgeException("Node path cannot be empty - list at least one nodemanager.");
        Query query = Queries.createQuery(this, startNodes, nodePath, fields, constraints, orderby, directions, searchDir, distinct);
        return getList(query);
    }


    @Override
    public NodeList getList(Query query) {
        return new SimpleNodeList(BridgeCollections.EMPTY_NODELIST, this);
    }


    @Override
    public Query createQuery() {
        return new BasicQuery(this);
    }

    @Override
    public Query createAggregatedQuery() {
        throw new UnsupportedOperationException();
    }


    @Override
    public NodeQuery createNodeQuery() {
        return new BasicNodeQuery(this);
    }

    @Override
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    @Override
    public Locale getLocale() {
        return locale;
    }


    @Override
    public Object getProperty(Object key) {
        return properties.get(key);
    }


    @Override
    public void setProperty(Object key, Object value) {
        properties.put(key, value);
    }
    @Override
    public Map<Object, Object> getProperties() {
        return properties;
    }

    @Override
    public Collection<Function<?>> getFunctions(String setName) {
        return Collections.emptySet();
    }


    @Override
    public Function<?> getFunction(String setName, String functionName) {
        throw new NotFoundException();
    }


    @Override
    public NodeList createNodeList() {
        return new SimpleNodeList(BridgeCollections.EMPTY_NODELIST, this);
    }


    @Override
    public RelationList createRelationList() {
        return new BasicRelationList(BridgeCollections.EMPTY_RELATIONLIST, this);
    }

    @Override
    public NodeManagerList createNodeManagerList() {
        return new BasicNodeManagerList(BridgeCollections.EMPTY_NODEMANAGERLIST, this);
    }


    @Override
    public RelationManagerList createRelationManagerList() {
        return new BasicRelationManagerList(BridgeCollections.EMPTY_RELATIONMANAGERLIST, this);
    }


    @Override
    public StringList getPossibleContexts() {
        return new BasicStringList();
    }


    @Override
    public Cloud getNonTransactionalCloud() {
        return this;
    }


    @Override
    public void shutdown() {
    }

}

