/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.util;

import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.util.*;
import org.mmbase.util.functions.*;

/**
 * A bridge Node based on a {@link java.util.Map}. It can come in handy sometimes to be able to present any {@link
 * java.util.Map} as an MMBase Node. E.g. because then it can be accessed in MMBase taglib using
 * mm:field tags. Don't confuse this with {@link NodeMap}.

 * @author  Michiel Meeuwissen
 * @version $Id$
 * @since   MMBase-1.8
 */

public class MapNode<V> extends AbstractNode implements Node {

    /**
     * This is normally, but not always, a VirtualBuilder. It is not for some builders which have
     * besides real nodes also virtual nodes, like typedef (cluster nodes) and typerel (allowed relations because of inheritance).
     */
    final protected NodeManager nodeManager;
    final protected Map<String, V> values;
    final protected Map<String, Long> sizes = new HashMap<String, Long>();
    final protected Map<String, V> wrapper;
    final protected Map<String, V> originals = new HashMap<String, V>();
    boolean implicitCreate = false;

    /**
     * This constructor explicitely specifies the node manager of the Node. This is used for {#getNodeManager} and {#getCloud}.
     */
    public MapNode(Map<String, V> v, NodeManager nm) {
        values = v;
        wrapper = new LinkMap<String, V>(values, originals, LinkMap.Changes.CONSERVE);
        nodeManager = nm;
    }
    /**
     * A node with a 'virtual' nodemanager will be constructed. This virtual node manager will have
     * fields which are guessed based on the keys and values of the given map.
     */
    public MapNode(Map<String, V> v, Cloud cloud, boolean implicitCreate) {
        this(v, createVirtualNodeManager(cloud, v, implicitCreate));

    }

    public MapNode(Map<String, V>  v, Cloud cloud) {
        this(v, cloud, false);

    }
    /**
     * This allows you to create a Node object even without having a Cloud object. 'Class' security
     * is used to acquire a Cloud, because every bridge node must be associated with some Cloud
     * object.
     */
    public MapNode(Map<String, V> v) {
        this(v, guessCloud());
    }
    private static final Cloud guessCloud() {
        try {
            return ContextProvider.getDefaultCloudContext().getCloud("mmbase", "class", null);
        } catch (Exception e) {
            return null;
        }

    }

    protected static NodeManager createVirtualNodeManager(Cloud cloud, final Map map, boolean implicitCreate) {
        return new MapNodeManager(cloud, map, implicitCreate);
    }

    public Cloud getCloud() {
        return nodeManager.getCloud();
    }

    public NodeManager getNodeManager() {
        return nodeManager;
    }

    @Override
    public int getNumber() {
        return Casting.toInt(values.get("number"));
    }

    @Override
    public boolean isNew() {
        return false;
    }

    @Override
    public boolean isChanged(String fieldName) {
        return originals.containsKey(fieldName);
    }
    @Override
    public boolean isChanged() {
        return ! originals.isEmpty();
    }


    protected void edit(int i) {
        // always ok.
    }
    public Object getValueWithoutProcess(String fieldName) {
        return values.get(fieldName);
    }
    @Override
    public void setValueWithoutProcess(String fieldName, Object value) {
        wrapper.put(fieldName, (V) value);
    }
    @Override
    public void setValueWithoutChecks(String fieldName, Object value) {
        wrapper.put(fieldName, (V) value);
    }

    @Override
    public boolean isNull(String fieldName) {
        return values.get(fieldName) == null;
    }
    @Override
    protected void setSize(String fieldName, long size) {
        sizes.put(fieldName, size);
    }

    public long getSize(String fieldName) {
        Long size = sizes.get(fieldName);
        if (size != null) {
            return size.longValue();
        } else {
            int s =  SizeOf.getByteSize(values.get(fieldName));
            sizes.put(fieldName, (long) s);
            return s;
        }
    }

    @Override
    public void commit() {
        throw new UnsupportedOperationException("Cannot commit map node");
    }

    @Override
    public void cancel() {
    }


    @Override
    public void delete(boolean deleteRelations) {
        throw new UnsupportedOperationException("Cannot delete map node");
    }

    @Override
    public String toString() {
        return "Map Node" + values;
    }

    @Override
    public void deleteRelations(String type) throws NotFoundException {
    }

    @Override
    public RelationList getRelations(String role, NodeManager nodeManager, String searchDir) throws NotFoundException {
        return BridgeCollections.EMPTY_RELATIONLIST;
    }
    @Override
    public RelationList getRelations(String role, String nodeManager) throws NotFoundException {
        return BridgeCollections.EMPTY_RELATIONLIST;
    }


    @Override
    public boolean hasRelations() {
        return false;
    }

    @Override
    public int countRelatedNodes(NodeManager otherNodeManager, String role, String direction) {
        return 0;

    }

    @Override
    public NodeList getRelatedNodes(NodeManager nodeManager, String role, String searchDir) {
        return BridgeCollections.EMPTY_NODELIST;
    }

    @Override
    public int countRelatedNodes(String type) {
        return 0;
    }

    @Override
    public StringList getAliases() {
        return BridgeCollections.EMPTY_STRINGLIST;
    }

    @Override
    public void createAlias(String aliasName) {
        throw new UnsupportedOperationException("Map nodes have no aliases");
    }

    @Override
    public void deleteAlias(String aliasName) {
        throw new UnsupportedOperationException("Map nodes have no aliases");
    }

    @Override
    public Relation createRelation(Node destinationNode, RelationManager relationManager) {
        throw new UnsupportedOperationException("Map nodes have no relations");
    }


    @Override
    public void setContext(String context) {
        throw new UnsupportedOperationException("Map nodes have no security context");
    }

    // javadoc inherited (from Node)
    @Override
    public String getContext() {
        throw new UnsupportedOperationException("Virtual nodes have no security context");
    }


    // javadoc inherited (from Node)
    @Override
    public StringList getPossibleContexts() {
        return BridgeCollections.EMPTY_STRINGLIST;
    }

    @Override
    public boolean mayWrite() {
        return true;
    }

    @Override
    public boolean mayDelete() {
        return false;
    }

    @Override
    public boolean mayChangeContext() {
        return false;
    }

    public Collection<Function<?>>  getFunctions() {
        return  nodeManager.getFunctions();
    }


    @Override
    protected Function getNodeFunction(String functionName) {
        return nodeManager.getFunction(functionName);
    }
}

