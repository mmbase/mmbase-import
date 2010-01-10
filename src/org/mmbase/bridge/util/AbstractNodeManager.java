/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.util;
import javax.servlet.*;
import java.util.*;
import java.io.*;

import org.mmbase.bridge.*;
import org.mmbase.bridge.implementation.BasicFieldList;
import org.mmbase.util.logging.*;
import org.mmbase.util.*;

/**
 * Abstract implementation of NodeManager, to minimalize the implementation of a virtual one. Must
 * methods throw UnsupportOperationException (like in {@link
 * org.mmbase.bridge.implementation.VirtualNodeManager}.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @see org.mmbase.bridge.NodeManager
 * @since MMBase-1.8
 */
public abstract class AbstractNodeManager extends AbstractNode implements NodeManager {
    private static final Logger log = Logging.getLoggerInstance(AbstractNodeManager.class);


    protected Map values = new HashMap();
    protected final Cloud cloud;
    protected AbstractNodeManager(Cloud c) {
        cloud = c;
    }

    protected void setValueWithoutChecks(String fieldName, Object value) {
        values.put(fieldName, value);
    }
    public Object getValueWithoutProcess(String fieldName) {
        return values.get(fieldName);
    }
    protected void edit(int action) {
        // go ahead
    }

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
    public Cloud getCloud() {
        return cloud;
    }


    public Node createNode() { throw new UnsupportedOperationException();}
    public NodeList getList(String where, String sorted, boolean direction) { throw new UnsupportedOperationException(); }
    public NodeList getList(String where, String sorted, String direction) { throw new UnsupportedOperationException(); }


    public FieldList createFieldList() {
        return new BasicFieldList(Collections.EMPTY_LIST, this);
    }

    public NodeList createNodeList() {
        return new CollectionNodeList(Collections.EMPTY_LIST, this);
    }

    public RelationList createRelationList() {
        return new CollectionRelationList(Collections.EMPTY_LIST, this);
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


    protected abstract Map getFieldTypes();


    public  boolean hasField(String fieldName) {
        Map fieldTypes = getFieldTypes();
        return fieldTypes.isEmpty() || fieldTypes.containsKey(fieldName);
    }

    public final FieldList getFields() {
        return getFields(NodeManager.ORDER_NONE);
    }

    public final FieldList getFields(int order) {
        return new BasicFieldList(getFieldTypes().values(), this);
    }

    public Field getField(String fieldName) throws NotFoundException {
        Field f = (Field) getFieldTypes().get(fieldName);
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
    public Map getProperties() {
        return Collections.EMPTY_MAP;
    }

    public NodeManagerList getDescendants() {
        return BridgeCollections.EMPTY_NODEMANAGERLIST;
    }

    public Collection  getFunctions() {
        return Collections.EMPTY_LIST;
    }

}
