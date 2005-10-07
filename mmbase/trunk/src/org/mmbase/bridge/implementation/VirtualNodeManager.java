/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;

import java.util.*;
import org.mmbase.bridge.*;
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
 * @version $Id: VirtualNodeManager.java,v 1.34 2005-10-07 18:46:12 michiel Exp $
 */
public class VirtualNodeManager extends BasicNodeManager {
    private static final  Logger log = Logging.getLoggerInstance(VirtualNodeManager.class);

    VirtualNodeManager(MMObjectBuilder builder, BasicCloud cloud) {
        super(builder, cloud);
    }

    VirtualNodeManager(BasicCloud cloud) {
        super(new VirtualBuilder(BasicCloudContext.mmb), cloud);
    }


    /**
     * Instantiated a Virtual NodeManager, and tries its best to find reasonable values for the field-types.
     * 
     * @todo What if the value is NULL
     * @deprecated It does not work properly, if possible use {@link #VirtualNodeManager(query, cloud)}
     */

    VirtualNodeManager(MMObjectNode node, BasicCloud cloud) {
        super(node.getBuilder(), cloud);
        // determine fields and field types
        Map values = node.getValues();
        synchronized(values) {
            Iterator i = values.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry entry = (Map.Entry) i.next();
                String fieldName = (String) entry.getKey();
                Object value = entry.getValue();
                if (value == null) value = new Object();
                DataType fieldDataType = DataTypes.createDataType("field", value.getClass());
                int type = Fields.classToType(value.getClass());
                CoreField fd = Fields.createField(fieldName, type, Field.TYPE_UNKNOWN, Field.STATE_VIRTUAL, fieldDataType);
                fd.finish();
                Field ft = new BasicField(fd, this);
                fieldTypes.put(fieldName, ft);
            }
        }
    }

    /**
     * @since MMBase-1.8
     */
    VirtualNodeManager(Query query, BasicCloud cloud) {
        this(cloud);
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


    /**
     * Initializes the node.
     * Sets nodemanager to typedef, and creates a virtual node for this manager.
     */
    protected void init() {
        if (cloud == null) {
            nodeManager = (BasicNodeManager) ContextProvider.getDefaultCloudContext().getCloud("mmbase").getNodeManager("typedef");
        } else {
            nodeManager = cloud.getBasicNodeManager("typedef");
        }
        noderef = new VirtualNode(BasicCloudContext.mmb.getTypeDef());
        super.init();
    }

    /**
     * Initializes the NodeManager
     */
    protected void initManager() {
        noderef.setValue("name",        builder.getTableName());
        noderef.setValue("description", builder.getDescription());
        super.initManager();
    }

    /**
     * Gets a new (initialized) node.
     * Throws an exception since this type is virtual, and creating nodes is not allowed.
     */
    public Node createNode() {
        throw new BridgeException("Cannot create a node from a virtual node type.");
    }

    /**
     * Search nodes of this type.
     * Throws an exception since this type is virtual, and searching is not allowed.
     */
    public NodeList getList(String where, String sorted, boolean direction) {
        throw new BridgeException("Cannot perform search on a virtual node type.");
    }
}
