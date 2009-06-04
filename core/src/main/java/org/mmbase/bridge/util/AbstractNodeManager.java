/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.util;
import java.util.*;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.mmbase.bridge.*;
import org.mmbase.bridge.implementation.BasicFieldList;
import org.mmbase.util.functions.Function;

/**
 * Abstract implementation of NodeManager, to minimalize the implementation of a virtual one. Must
 * methods throw UnsupportOperationException (like in {@link
 * org.mmbase.bridge.implementation.VirtualNodeManager}).
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @see org.mmbase.bridge.NodeManager
 * @since MMBase-1.8
 */
public abstract class AbstractNodeManager extends AbstractNode implements NodeManager {

    protected Map<String, Object> values = new HashMap<String, Object>();
    protected final Cloud cloud;
    protected AbstractNodeManager(Cloud c) {
        cloud = c;
    }

    @Override
    protected void setValueWithoutChecks(String fieldName, Object value) {
        values.put(fieldName, value);
    }
    public Object getValueWithoutProcess(String fieldName) {
        return values.get(fieldName);
    }
    protected void edit(int action) {
        // go ahead
    }

    @Override
    protected void setSize(String fieldName, long size) {
        // never mind
    }
    public long getSize(String fieldName) {
        // never mind
        return 2;
    }
    public NodeManager getNodeManager() {
        return cloud.getNodeManager("typedef");
    }

    public void setNodeManager(NodeManager nm) {
        if (! nm.getName().equals("typedef")) {
            throw new IllegalArgumentException("Cannot change the node manager of node managers");
        }
    }

    public Cloud getCloud() {
        return cloud;
    }


    public Node createNode() { throw new UnsupportedOperationException();}
    public NodeList getList(String where, String sorted, boolean direction) { throw new UnsupportedOperationException(); }
    public NodeList getList(String where, String sorted, String direction) { throw new UnsupportedOperationException(); }


    public FieldList createFieldList() {
        return new BasicFieldList(Collections.emptyList(), this);
    }

    public NodeList createNodeList() {
        return new CollectionNodeList(BridgeCollections.EMPTY_NODELIST, this);
    }

    public RelationList createRelationList() {
        return new CollectionRelationList(BridgeCollections.EMPTY_RELATIONLIST, this);
    }

    public boolean mayCreateNode() {
        return false;
    }

    public NodeList getList(NodeQuery query) { throw new UnsupportedOperationException(); }

    public NodeQuery createQuery() { throw new UnsupportedOperationException(); }
    public NodeList getList(String command, Map parameters, ServletRequest req, ServletResponse resp){ throw new UnsupportedOperationException();}

    public NodeList getList(String command, Map parameters){ throw new UnsupportedOperationException();}


    public RelationManagerList getAllowedRelations() { return BridgeCollections.EMPTY_RELATIONMANAGERLIST; }
    public RelationManagerList getAllowedRelations(String nodeManager, String role, String direction) { return BridgeCollections.EMPTY_RELATIONMANAGERLIST; }

    public RelationManagerList getAllowedRelations(NodeManager nodeManager, String role, String direction) { return BridgeCollections.EMPTY_RELATIONMANAGERLIST; }

    public String getInfo(String command) { return getInfo(command, null,null);}

    public String getInfo(String command, ServletRequest req,  ServletResponse resp){ throw new UnsupportedOperationException();}


    protected abstract Map<String, Field> getFieldTypes();


    public boolean hasField(String fieldName) {
        Map<String, Field> fieldTypes = getFieldTypes();
        return fieldTypes.isEmpty() || fieldTypes.containsKey(fieldName);
    }

    public final FieldList getFields() {
        return getFields(NodeManager.ORDER_NONE);
    }

    public final FieldList getFields(int order) {
        return new BasicFieldList(getFieldTypes().values(), this);
    }

    public Field getField(String fieldName) throws NotFoundException {
        Field f = getFieldTypes().get(fieldName);
        if (f == null) throw new NotFoundException("Field '" + fieldName + "' does not exist in NodeManager '" + getName() + "'.(" + getFieldTypes() + ")");
        return f;
    }

    public String getGUIName() {
        return getGUIName(NodeManager.GUI_SINGULAR);
    }

    public String getGUIName(int plurality) {
        return getGUIName(plurality, null);
    }

    public String getGUIName(int plurality, Locale locale) {
        return getName();
    }

    public String getName() {
        return "virtual_manager";
    }
    public String getDescription() {
        return getDescription(null);
    }

    public String getDescription(Locale locale) {
        return "";
    }

    public NodeManager getParent() {
        return null;
    }


    public String getProperty(String name) {
        return null;
    }
    public Map<String, String> getProperties() {
        return Collections.emptyMap();
    }

    public NodeManagerList getDescendants() {
        return BridgeCollections.EMPTY_NODEMANAGERLIST;
    }

    public Collection<Function<?>>  getFunctions() {
        return Collections.emptyList();
    }

}
