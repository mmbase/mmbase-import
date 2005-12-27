/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;

import javax.servlet.*;
import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.BridgeCollections;
import org.mmbase.datatypes.*;
import org.mmbase.core.CoreField;
import org.mmbase.core.util.Fields;
import org.mmbase.module.core.*;
import org.mmbase.storage.search.*;
import org.mmbase.util.logging.*;

/**
 * This class represents a virtual node type information object.
 * It has the same functionality as BasicNodeType, but it's nodes are vitrtual - that is,
 * constructed based on the results of a search over multiple node managers.
 * As such, it is not possible to search on this node type, nor to create new nodes.
 * It's sole function is to provide a type definition for the results of a search.
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 * @version $Id: VirtualNodeManager.java,v 1.36 2005-12-27 22:12:33 michiel Exp $
 */
public class VirtualNodeManager extends VirtualNode implements NodeManager {
    private static final  Logger log = Logging.getLoggerInstance(VirtualNodeManager.class);


    // field types
    final protected Map fieldTypes = new HashMap();

    final MMObjectBuilder builder;

    /**
     * Instantiated a Virtual NodeManager, and tries its best to find reasonable values for the field-types.
     */
    VirtualNodeManager(org.mmbase.module.core.VirtualNode node, Cloud cloud) {
        super(cloud, getNodeRef("clusterbuilder"), cloud.getNodeManager("typedef"));
        // determine fields and field types
        if (node.getBuilder() instanceof VirtualBuilder) {
            VirtualBuilder virtualBuilder = (VirtualBuilder) node.getBuilder();;

            Map fields = virtualBuilder.getFields(node);
            Iterator i = fields.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry entry = (Map.Entry) i.next();
                String fieldName = (String) entry.getKey();
                CoreField fd = (CoreField) entry.getValue();
                Field ft = new BasicField(fd, this);
                fieldTypes.put(fieldName, ft);
            }
            builder = null;
        } else {
            builder = node.getBuilder();
        }
    }

    /**
     * @since MMBase-1.8
     */
    VirtualNodeManager(Query query, Cloud cloud) {
        super(cloud, getNodeRef("clusterbuilder.queryresult"), cloud.getNodeManager("typedef"));
        if (query instanceof NodeQuery) {
            builder = BasicCloudContext.mmb.getBuilder(((NodeQuery) query).getNodeManager().getName());
        } else {
            builder = null;
            // code to solve the fields.
            Iterator steps = query.getSteps().iterator();
            while (steps.hasNext()) {
                Step step = (Step) steps.next();
                DataType nodeType  = DataTypes.getDataType("node");
                String name = step.getAlias();
                if (name == null) name = step.getTableName();
                CoreField fd = Fields.createField(name, Field.TYPE_NODE, Field.TYPE_UNKNOWN, Field.STATE_VIRTUAL, nodeType);
                fd.finish();
                Field ft = new BasicField(fd, this);
                fieldTypes.put(name, ft);
            }
            Iterator fields = query.getFields().iterator();
            while(fields.hasNext()) {
                StepField field = (StepField) fields.next();
                Step step = field.getStep();
                Field f = cloud.getNodeManager(step.getTableName()).getField(field.getFieldName());
                String name = field.getAlias();
                if (name == null) {
                    name = step.getAlias();
                    if (name == null) name = step.getTableName();
                    name += "." + field.getFieldName();
                }
                final String fieldName = name;
                fieldTypes.put(name, new BasicField(((BasicField)f).coreField , this)  { // XXX casting is wrong!!, but I don't have other solution right now
                        public String getName() {
                            return fieldName;
                        }
                    });

            }
        }
    }


    /*
     * Creates a virtual typedef object, which will server as a noderef for the VirtualNode which the VirtualNodeManager is.
     */
    protected static org.mmbase.module.core.VirtualNode getNodeRef(String name) {
        org.mmbase.module.core.VirtualNode noderef = new org.mmbase.module.core.VirtualNode(BasicCloudContext.mmb.getTypeDef());
        noderef.storeValue("name", name);
        return noderef;
    }

    /**
     * Gets a new (initialized) node.
     * Throws an exception since this type is virtual, and creating nodes is not allowed.
     */
    public Node createNode() {
        throw new UnsupportedOperationException("Cannot create a node from a virtual node type.");
    }

    /**
     * Search nodes of this type.
     * Throws an exception since this type is virtual, and searching is not allowed.
     */
    public NodeList getList(String where, String sorted, boolean direction) {
        throw new UnsupportedOperationException("Cannot perform search on a virtual node type.");
    }
    public NodeList getList(String where, String sorted, String direction) {
        throw new UnsupportedOperationException("Cannot perform search on a virtual node type.");
    }


    public FieldList createFieldList() {
        return new BasicFieldList(Collections.EMPTY_LIST, this);
    }

    public NodeList createNodeList() {
        return new BasicNodeList(Collections.EMPTY_LIST, this);
    }

    public RelationList createRelationList() {
        return new BasicRelationList(Collections.EMPTY_LIST, this);
    }

    public boolean mayCreateNode() {
        return false;
    }

    public NodeList getList(NodeQuery query) {
        throw new UnsupportedOperationException("Cannot perform search on a virtual node type.");
    }

    public NodeQuery createQuery() {
        throw new UnsupportedOperationException("Cannot perform search on a virtual node type.");
    }
    public NodeList getList(String command, Map parameters, ServletRequest req, ServletResponse resp){
        throw new UnsupportedOperationException("Cannot perform search on a virtual node type.");
    }

    public NodeList getList(String command, Map parameters){
        throw new UnsupportedOperationException("Cannot perform search on a virtual node type.");
    }

    public RelationManagerList getAllowedRelations() {
        return BridgeCollections.EMPTY_RELATIONMANAGERLIST;
    }
    public RelationManagerList getAllowedRelations(String nodeManager, String role, String direction) {
        return BridgeCollections.EMPTY_RELATIONMANAGERLIST;
    }

    public RelationManagerList getAllowedRelations(NodeManager nodeManager, String role, String direction) {
        return BridgeCollections.EMPTY_RELATIONMANAGERLIST;
    }

    public String getInfo(String command) {
        return getInfo(command, null,null);
    }

    public String getInfo(String command, ServletRequest req,  ServletResponse resp){
        throw new UnsupportedOperationException("Cannot perform search on a virtual node type.");

    }
    /**
     * Returns the fieldlist of this nodemanager after making sure the manager is synced with the builder.
     * @since MMBase-1.8
     */
    protected Map getFieldTypes() {
        return fieldTypes;
    }


    public boolean hasField(String fieldName) {
        return true;
    }

    public FieldList getFields() {
        return getFields(NodeManager.ORDER_NONE);
    }

    public FieldList getFields(int order) {
        if (builder != null) {
            return new BasicFieldList(builder.getFields(order), this);
        } else {
            return new BasicFieldList(getFieldTypes().values(), this);
        }
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
        if (locale==null) locale = cloud.getLocale();
        if (builder!=null) {
            if (plurality == NodeManager.GUI_SINGULAR) {
                return builder.getSingularName(locale.getLanguage());
            } else {
                return builder.getPluralName(locale.getLanguage());
            }
        } else {
            return getName();
        }
    }

    public String getName() {
        return noderef.getStringValue("name");
    }
    public String getDescription() {
        return getDescription(null);
    }

    public String getDescription(Locale locale) {
        if (locale==null) locale = cloud.getLocale();
        if (builder!=null) {
            return builder.getDescription(locale.getLanguage());
        } else {
            return "";
        }
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
}
