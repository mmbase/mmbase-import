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
 * Abstract implementation of NodeManager, to minimalize the implementation of a virtual one. Most
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
    @Override
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
    @Override
    public long getSize(String fieldName) {
        // never mind
        return 2;
    }
    @Override
    public NodeManager getNodeManager() {
        return cloud.getNodeManager("typedef");
    }

    @Override
    public void setNodeManager(NodeManager nm) {
        if (! nm.getName().equals("typedef")) {
            throw new IllegalArgumentException("Cannot change the node manager of node managers");
        }
    }

    @Override
    public Cloud getCloud() {
        return cloud;
    }


    @Override
    public boolean isNodeManager() {
        return true;
    }
    @Override
    public NodeManager toNodeManager() {
        return this;
    }
    @Override
    public Node createNode() { throw new UnsupportedOperationException();}


    @Override
    public NodeList getList(String constraints, String sorted, String directions) {
        NodeQuery query = createQuery();
        Queries.addConstraints(query, constraints);
        Queries.addSortOrders(query, sorted, directions);
        NodeList list = getList(query);
        list.setProperty("constraints", constraints);
        list.setProperty("orderby",     sorted);
        list.setProperty("directions",  directions);
        return list;
    }


    @Override
    public FieldList createFieldList() {
        return new BasicFieldList(Collections.emptyList(), this);
    }

    @Override
    public NodeList createNodeList() {
        return new CollectionNodeList(BridgeCollections.EMPTY_NODELIST, this);
    }

    @Override
    public RelationList createRelationList() {
        return new CollectionRelationList(BridgeCollections.EMPTY_RELATIONLIST, this);
    }

    @Override
    public boolean mayCreateNode() {
        return false;
    }

    @Override
    public NodeList getList(NodeQuery query) {
        if (query == null) query = createQuery();
        return getCloud().getList(query);
    }

    @Override
    public NodeQuery createQuery() {
        return new org.mmbase.bridge.implementation.BasicNodeQuery(this);
    }
    @Override
    public NodeList getList(String command, Map parameters, ServletRequest req, ServletResponse resp){ throw new UnsupportedOperationException();}

    @Override
    public NodeList getList(String command, Map parameters){ throw new UnsupportedOperationException();}


    @Override
    public RelationManagerList getAllowedRelations() { return BridgeCollections.EMPTY_RELATIONMANAGERLIST; }
    @Override
    public RelationManagerList getAllowedRelations(String nodeManager, String role, String direction) { return BridgeCollections.EMPTY_RELATIONMANAGERLIST; }

    @Override
    public RelationManagerList getAllowedRelations(NodeManager nodeManager, String role, String direction) { return BridgeCollections.EMPTY_RELATIONMANAGERLIST; }

    @Override
    public String getInfo(String command) { return getInfo(command, null,null);}

    @Override
    public String getInfo(String command, ServletRequest req,  ServletResponse resp){ throw new UnsupportedOperationException();}


    protected abstract Map<String, Field> getFieldTypes();


    @Override
    public boolean hasField(String fieldName) {
        Map<String, Field> fieldTypes = getFieldTypes();
        return fieldTypes.isEmpty() || fieldTypes.containsKey(fieldName);
    }

    @Override
    public final FieldList getFields() {
        return getFields(NodeManager.ORDER_NONE);
    }

    @Override
    public final FieldList getFields(int sortOrder) {
        if (sortOrder == ORDER_NONE) {
            return new BasicFieldList(getFieldTypes().values(), this);
        } else {
            List<Field> orderedFields = new ArrayList<Field>();
            for (Field field : getFieldTypes().values()) {
                // include only fields which have been assigned a valid position, and are
                if (
                    ((sortOrder == ORDER_CREATE) && (field.getStoragePosition() > -1)) ||
                    ((sortOrder == ORDER_EDIT) && (field.getEditPosition() > -1)) ||
                    ((sortOrder == ORDER_SEARCH) && (field.getSearchPosition() > -1)) ||
                    ((sortOrder == ORDER_LIST) && (field.getListPosition() > -1))
                    ) {
                    orderedFields.add(field);
                }
            }
            Fields.sort(orderedFields, sortOrder);

            return new BasicFieldList(orderedFields, this);
        }
    }

    @Override
    public Field getField(String fieldName) throws IllegalArgumentException {
        Field f = getFieldTypes().get(fieldName);
        if (f == null) throw new IllegalArgumentException("Field '" + fieldName + "' does not exist in NodeManager '" + getName() + "'.(" + getFieldTypes() + ")");
        return f;
    }

    @Override
    public String getGUIName() {
        return getGUIName(NodeManager.GUI_SINGULAR);
    }

    @Override
    public String getGUIName(int plurality) {
        return getGUIName(plurality, null);
    }

    @Override
    public String getGUIName(int plurality, Locale locale) {
        return getName();
    }

    @Override
    public String getName() {
        return "virtual_manager";
    }
    @Override
    public String getDescription() {
        return getDescription(null);
    }

    @Override
    public String getDescription(Locale locale) {
        return "";
    }

    @Override
    public NodeManager getParent() {
        return null;
    }


    @Override
    public String getProperty(String name) {
        return getProperties().get(name);
    }
    @Override
    public Map<String, String> getProperties() {
        return Collections.emptyMap();
    }


    @Override
    public NodeManagerList getDescendants() {
        NodeManagerList descendants = getCloud().createNodeManagerList();
        String name = getName();
        for (NodeManager nm  : getCloud().getNodeManagers()) {
            try {
                NodeManager parent = nm.getParent();
                if (parent != null && name.equals(parent.getName())) {
                    if (! descendants.contains(nm)) {
                        descendants.add(nm);
                        for (NodeManager sub : nm.getDescendants()) {
                            descendants.add(sub);
                        }
                    }
                }
            } catch (NotFoundException nfe) {
                // never mind, getParent may do that, it simply means that it is object or so.
            }
        }
        return descendants;
    }

    @Override
    public Collection< Function<?>>  getFunctions() {
        return Collections.emptyList();
    }


    @Override
    public String toString() {
        return getName() + " " +  getFieldTypes().keySet();
    }

}
