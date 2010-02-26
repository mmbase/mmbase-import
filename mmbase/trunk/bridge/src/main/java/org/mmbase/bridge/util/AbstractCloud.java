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


    public abstract Node getNode(int number);


    public Node getNode(String number) throws NotFoundException {
        return getNode(Integer.parseInt(number));
    }


    public Node getNodeByAlias(String alias) throws NotFoundException {
        throw new NotFoundException();
    }

    public Relation getRelation(int number) throws NotFoundException {
        return (Relation) getNode(number);
    }
    public Relation getRelation(String number) throws NotFoundException {
        return (Relation) getNode(number);
    }

    public boolean hasNode(int number) {
        try {
            return getNode(number) != null;
        } catch (NotFoundException e) {
            return false;
        }
    }

    public boolean hasNode(String number) {
        try {
            return hasNode(Integer.parseInt(number));
        } catch (Exception e) {
            return false;
        }
    }
    public boolean hasRelation(int number) {
        return hasNode(number) && getNode(number) instanceof Relation;
    }
    public boolean hasRelation(String number) {
        return hasNode(number) && getNode(number) instanceof Relation;
    }

    public boolean mayRead(int number) {
        getNode(number);
        return true;
    }

    public boolean may(org.mmbase.security.Action action, org.mmbase.util.functions.Parameters parameters) {
        return true;
    }

    public boolean mayRead(String number) {
        getNode(number);
        return true;
    }

    public abstract NodeManagerList getNodeManagers();

    public abstract NodeManager getNodeManager(String name);

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
    public NodeManager getNodeManager(int nodeManagerId) throws NotFoundException {
        throw new NotFoundException();
    }


    /**
     * On default we don't associated number id's with node managers
     */
    public RelationManager getRelationManager(int relationManagerId) throws NotFoundException {
        throw new NotFoundException();
    }
    public RelationManager getRelationManager(String roleName) throws NotFoundException {
        throw new UnsupportedOperationException();
    }

    public RelationManager getRelationManager(String sourceManagerName, String destinationManagerName, String roleName) throws NotFoundException {
        return getRelationManager(getNodeManager(sourceManagerName), getNodeManager(destinationManagerName), roleName);
    }


    public RelationManager getRelationManager(NodeManager sourceManager, NodeManager destinationManager, String roleName) throws NotFoundException {
        throw new UnsupportedOperationException();
    }

    public final boolean hasRelationManager(String sourceManagerName, String destinationManagerName, String roleName) {
        return hasRelationManager(getNodeManager(sourceManagerName), getNodeManager(destinationManagerName), roleName);
    }

    public boolean hasRelationManager(NodeManager sourceManager, NodeManager destinationManager, String roleName) {
        try {
            getRelationManager(sourceManager, destinationManager, roleName);
            return true;
        } catch (NotFoundException nfe) {
            return false;
        }
    }

    public boolean hasRole(String roleName) {
        try {
            getRelationManager(roleName);
            return true;
        } catch (NotFoundException nfe) {
            return false;
        }
    }

    public boolean hasRelationManager(String roleName) {
        return hasRole(roleName);
    }


    public RelationManagerList getRelationManagers() {
        throw new UnsupportedOperationException();
    }


    public RelationManagerList getRelationManagers(String sourceManagerName, String destinationManagerName,  String roleName) throws NotFoundException {
        return getRelationManagers(getNodeManager(sourceManagerName), getNodeManager(destinationManagerName), roleName);

    }

    public RelationManagerList getRelationManagers(NodeManager sourceManager, NodeManager destinationManager,
                                                   String roleName) throws NotFoundException {
        throw new UnsupportedOperationException();
    }

    public abstract CloudContext getCloudContext();


    public Transaction createTransaction() {
        return createTransaction(null, false);
    }


    public Transaction createTransaction(String name) throws AlreadyExistsException {
        return createTransaction(name, false);
    }


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


    public Transaction getTransaction(String name) {
        Transaction tran = transactions.get(name);
        if (tran == null) {
            tran = createTransaction(name, false);
        }
        return tran;
    }


    public String getName() {
        return name;
    }

    public String getDescription() {
        return getName();
    }

    public UserContext getUser() {
        return userContext;
    }

    //javadoc inherited
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


    public NodeList getList(Query query) {
        return new AbstractNodeList(BridgeCollections.EMPTY_NODELIST, this);
    }


    public Query createQuery() {
        return new BasicQuery(this);
    }

    public Query createAggregatedQuery() {
        throw new UnsupportedOperationException();
    }


    public NodeQuery createNodeQuery() {
        return new BasicNodeQuery(this);
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Locale getLocale() {
        return locale;
    }


    public Object getProperty(Object key) {
        return properties.get(key);
    }


    public void setProperty(Object key, Object value) {
        properties.put(key, value);
    }
    public Map<Object, Object> getProperties() {
        return properties;
    }

    public Collection<Function<?>> getFunctions(String setName) {
        return Collections.emptySet();
    }


    public Function<?> getFunction(String setName, String functionName) {
        throw new NotFoundException();
    }


    public NodeList createNodeList() {
        return new BasicNodeList(BridgeCollections.EMPTY_NODELIST, this);
    }


    public RelationList createRelationList() {
        return new BasicRelationList(BridgeCollections.EMPTY_RELATIONLIST, this);
    }

    public NodeManagerList createNodeManagerList() {
        return new BasicNodeManagerList(BridgeCollections.EMPTY_NODEMANAGERLIST, this);
    }


    public RelationManagerList createRelationManagerList() {
        return new BasicRelationManagerList(BridgeCollections.EMPTY_RELATIONMANAGERLIST, this);
    }


    public StringList getPossibleContexts() {
        return new BasicStringList();
    }


    public Cloud getNonTransactionalCloud() {
        return this;
    }


    public void shutdown() {
    }

}

