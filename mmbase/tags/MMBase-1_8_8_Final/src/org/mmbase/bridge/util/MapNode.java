/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.util;

import java.util.*;
import java.io.InputStream;
import org.mmbase.bridge.*;
import org.mmbase.bridge.implementation.BasicFunctionValue;
import org.mmbase.bridge.implementation.BasicField;
import org.mmbase.core.CoreField;
import org.mmbase.core.util.Fields;
import org.mmbase.util.Casting;
import org.mmbase.util.SizeOf;
import org.mmbase.util.logging.*;
import org.mmbase.util.functions.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A bridge Node based on a Map. It can come in handy sometimes to be able to present any Map as an
 * MMBase Node. E.g. because then it can be accessed in MMBase taglib using mm:field tags.

 * @author  Michiel Meeuwissen
 * @version $Id$
 * @since   MMBase-1.8
 */

public class MapNode extends AbstractNode implements Node {

    private static final Logger log = Logging.getLoggerInstance(MapNode.class);

    /**
     * This is normally, but not always, a VirtualBuilder. It is not for some builders which have
     * besides real nodes also virtual nodes, like typedef (cluster nodes) and typerel (allowed relations because of inheritance).
     */
    final protected NodeManager nodeManager;
    final protected Map values;
    final protected Map sizes = new HashMap();
    final protected Map originalValues = new HashMap();

    /**
     * This constructor explicitely specifies the node manager of the Node. This is used for {#getNodeManager} and {#getCloud}.
     */
    public MapNode(Map v, NodeManager nm) {
        values = v;
        originalValues.putAll(values);
        nodeManager = nm;
    }
    /**
     * A node with a 'virtual' nodemanager will be constructed. This virtual node manager will have
     * fields which are guessed based on the keys and values of the given map.
     */
    public MapNode(Map v, Cloud cloud) {
        this(v, createVirtualNodeManager(cloud, v));

    }
    /**
     * This allows you to create a Node object even without having a Cloud object. 'Class' security
     * is used to acquire a Cloud, because every bridge node must be associated with some Cloud
     * object.
     */
    public MapNode(Map v) {
        this(v, ContextProvider.getDefaultCloudContext().getCloud("mmbase", "class", null));
    }

    protected static NodeManager createVirtualNodeManager(Cloud cloud, final Map map) {
        return new AbstractNodeManager(cloud) {
            Map fieldTypes = new HashMap();
            {
                Iterator i = map.entrySet().iterator();
                while (i.hasNext()) {
                    Map.Entry entry = (Map.Entry) i.next();
                    String fieldName = (String) entry.getKey();
                    Object value = entry.getValue();
                    CoreField fd = Fields.createField(fieldName, Fields.classToType(value == null ? Object.class : value.getClass()),
                                                      Field.TYPE_UNKNOWN, Field.STATE_VIRTUAL, null);
                    Field ft = new BasicField(fd, this);
                    fieldTypes.put(fieldName, ft);
                }
            }
            protected Map getFieldTypes() {
                return fieldTypes;
            }

        };
    }

    public Cloud getCloud() {
        return nodeManager.getCloud();
    }

    public NodeManager getNodeManager() {
        return nodeManager;
    }

    public int getNumber() {
        return Casting.toInt(values.get("number"));
    }

    public boolean isNew() {
        return false;
    }

    public boolean isChanged(String fieldName) {
        Object originalValue = originalValues.get(fieldName);
        Object value            = values.get(fieldName);
        return originalValue == null ? value == null : originalValue.equals(value);
    }
    public boolean isChanged() {
        return ! values.equals(originalValues);
    }


    protected void edit(int i) {
        // always ok.
    }
    public Object getValueWithoutProcess(String fieldName) {
        return values.get(fieldName);
    }
    public void setValueWithoutProcess(String fieldName, Object value) {
        values.put(fieldName, value);
    }
    public void setValueWithoutChecks(String fieldName, Object value) {
        values.put(fieldName, value);
    }

    public boolean isNull(String fieldName) {
        return values.get(fieldName) == null;
    }
    protected void setSize(String fieldName, long size) {
        sizes.put(fieldName, new Long(size));
    }

    public long getSize(String fieldName) {
        Long size = (Long) sizes.get(fieldName);
        if (size != null) {
            return size.longValue();
        } else {
            int s =  SizeOf.getByteSize(values.get(fieldName));
            sizes.put(fieldName, new Long(s));
            return s;
        }
    }

    public void commit() {
        throw new UnsupportedOperationException("Cannot commit map node");
    }

    public void cancel() {
    }


    public void delete(boolean deleteRelations) {
        throw new UnsupportedOperationException("Cannot delete map node");
    }

    public String toString() {
        return "Map Node" + values;
    }

    public void deleteRelations(String type) throws NotFoundException {
    }

    public RelationList getRelations(String role, NodeManager nodeManager, String searchDir) throws NotFoundException {
        return BridgeCollections.EMPTY_RELATIONLIST;
    }
    public RelationList getRelations(String role, String nodeManager) throws NotFoundException {
        return BridgeCollections.EMPTY_RELATIONLIST;
    }


    public boolean hasRelations() {
        return false;
    }

    public int countRelatedNodes(NodeManager otherNodeManager, String role, String direction) {
        return 0;

    }

    public NodeList getRelatedNodes(NodeManager nodeManager, String role, String searchDir) {
        return BridgeCollections.EMPTY_NODELIST;
    }

    public int countRelatedNodes(String type) {
        return 0;
    }

    public StringList getAliases() {
        return BridgeCollections.EMPTY_STRINGLIST;
    }

    public void createAlias(String aliasName) {
        throw new UnsupportedOperationException("Map nodes have no aliases");
    }

    public void deleteAlias(String aliasName) {
        throw new UnsupportedOperationException("Map nodes have no aliases");
    }

    public Relation createRelation(Node destinationNode, RelationManager relationManager) {
        throw new UnsupportedOperationException("Map nodes have no relations");
    }


    public void setContext(String context) {
        throw new UnsupportedOperationException("Map nodes have no security context");
    }

    // javadoc inherited (from Node)
    public String getContext() {
        throw new UnsupportedOperationException("Virtual nodes have no security context");
    }


    // javadoc inherited (from Node)
    public StringList getPossibleContexts() {
        return BridgeCollections.EMPTY_STRINGLIST;
    }

    public boolean mayWrite() {
        return true;
    }

    public boolean mayDelete() {
        return false;
    }

    public boolean mayChangeContext() {
        return false;
    }

    public Collection  getFunctions() {
        return  nodeManager.getFunctions();
    }


    protected Function getNodeFunction(String functionName) {
        return nodeManager.getFunction(functionName);
    }
}

