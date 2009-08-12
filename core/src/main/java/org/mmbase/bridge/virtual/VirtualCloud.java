/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.virtual;

import java.util.*;
import java.util.concurrent.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.*;
import org.mmbase.bridge.implementation.*;
import org.mmbase.security.*;
import org.mmbase.datatypes.DataType;
import org.mmbase.util.*;
import org.mmbase.util.functions.*;

/**
 * Straight forward (partial) implementation of Cloud, which maintains everything in memory.
 *
 * @author  Michiel Meeuwissen
 * @version $Id: MapNode.java 36154 2009-06-18 22:04:40Z michiel $
 * @since   MMBase-1.9.2
 * @todo    EXPERIMENTAL
 */

public class VirtualCloud implements Cloud {

    private final Map<Object, Object> properties = new ConcurrentHashMap<Object, Object>();
    private Locale locale = Locale.getDefault();

    private final String name;
    private final VirtualCloudContext cloudContext;
    private final UserContext userContext;

    VirtualCloud(String n, VirtualCloudContext cc, UserContext uc) {
        name = n;
        cloudContext = cc;
        userContext = uc;
    }


    Node getNode(final Map<String, Object> m, final NodeManager nm) {
        return new MapNode(new HashMap<String, Object>(m), nm) {
            @Override
            public  void commit() {
                if (! m.containsKey("number")) {
                    int number = VirtualCloudContext.addNode(values, nm.getName());
                    values.put("number", number);
                }
                m.putAll(values);
            }
        };
    }

    public Node getNode(int number) throws NotFoundException {
        Map<String, Object> n = cloudContext.nodes.get(number);
        if (n == null) throw new NotFoundException();
        NodeManager nm = getNodeManager(cloudContext.nodeTypes.get(number));
        return getNode(n, nm);
    }

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
        return cloudContext.nodes.containsKey(number);
    }
    public boolean hasNode(String number) {
        try {
            return cloudContext.nodes.containsKey(Integer.parseInt(number));
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

    public NodeManagerList getNodeManagers() {
        List<NodeManager> list = new ArrayList<NodeManager>();
        for (String name : cloudContext.nodeManagers.keySet()) {
            list.add(getNodeManager(name));
        }
        return new BasicNodeManagerList(list, this);
    }

    public NodeManager getNodeManager(String name) throws NotFoundException {
        Map<String, DataType> nm = cloudContext.nodeManagers.get(name);
        if (nm == null) throw new NotFoundException(name);
        return new VirtualNodeManager(this, name, nm);
    }


    public boolean hasNodeManager(String name) {
        return cloudContext.nodeManagers.containsKey(name);
    }

    public NodeManager getNodeManager(int nodeManagerId) throws NotFoundException {
        throw new NotFoundException();
    }
    public RelationManager getRelationManager(int relationManagerId) throws NotFoundException {
        throw new NotFoundException();
    }

    public RelationManager getRelationManager(String sourceManagerName, String destinationManagerName, String roleName) throws NotFoundException {
        throw new UnsupportedOperationException();
    }


    public RelationManager getRelationManager(NodeManager sourceManager, NodeManager destinationManager, String roleName) throws NotFoundException {
        throw new UnsupportedOperationException();
    }


    public boolean hasRelationManager(String sourceManagerName, String destinationManagerName, String roleName) {
        return true;
    }


    /**
     * Returns whether the specified relation manager exists.
     *
     * @param sourceManager         name of the node manager of the source node
     * @param destinationManager    name of the node manager of the destination node
     * @param roleName              name of the role
     * @return                      <code>true</code> if the specified relation manager could be found
     * @since MMBase-1.7
     */
    public boolean hasRelationManager(NodeManager sourceManager, NodeManager destinationManager, String roleName) {
        return true;
    }



    public boolean hasRole(String roleName) {
        return roleName.equals("related") || roleName.equals("posrel");
    }

    public RelationManager getRelationManager(String roleName) throws NotFoundException {
        throw new UnsupportedOperationException();
    }

    public boolean hasRelationManager(String roleName) {
        return roleName.equals("related") || roleName.equals("posrel");
    }


    public RelationManagerList getRelationManagers() {
        throw new UnsupportedOperationException();
    }


    public RelationManagerList getRelationManagers(String sourceManagerName, String destinationManagerName,  String roleName) throws NotFoundException {
        throw new UnsupportedOperationException();

    }

    public RelationManagerList getRelationManagers(NodeManager sourceManager, NodeManager destinationManager,
                                                   String roleName) throws NotFoundException {
        throw new UnsupportedOperationException();
    }

    public CloudContext getCloudContext() {
        return cloudContext;
    }


    public Transaction createTransaction() {
        throw new UnsupportedOperationException();
    }


    public Transaction createTransaction(String name) throws AlreadyExistsException {
        throw new UnsupportedOperationException();
    }


    public Transaction createTransaction(String name, boolean overwrite) throws AlreadyExistsException {
        throw new UnsupportedOperationException();
    }

    public Transaction getTransaction(String name) {
        throw new UnsupportedOperationException();
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

    public NodeList getList(String startNodes, String nodePath, String fields,
            String constraints, String orderby, String directions,
                            String searchDir, boolean distinct) {
        return new BasicNodeList(BridgeCollections.EMPTY_NODELIST, this);

    }


    public NodeList getList(Query query) {
        return new BasicNodeList(BridgeCollections.EMPTY_NODELIST, this);
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

