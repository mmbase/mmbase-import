/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;

import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.util.logging.*;

/**
 * This class represents a virtual node type information object.
 * It has the same functionality as BasicNodeType, but it's nodes are vitrtual - that is,
 * constructed based on the results of a search over multiple node managers.
 * As such, it is not possible to search on this node type, nor to create new nodes.
 * It's sole function is to provide a type definition for the results of a search.
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 * @version $Id: VirtualNodeManager.java,v 1.19 2004-01-16 14:51:57 michiel Exp $
 */
public class VirtualNodeManager extends BasicNodeManager {
    private static final Logger log = Logging.getLoggerInstance(VirtualNodeManager.class);

    VirtualNodeManager(MMObjectBuilder builder, BasicCloud cloud) {
        super(builder, cloud);
    }

    VirtualNodeManager(BasicCloud cloud) {
        super(new VirtualBuilder(BasicCloudContext.mmb), cloud);
    }

    VirtualNodeManager(MMObjectNode node, BasicCloud cloud) {
        this(cloud);
        // determine fields and field types
        for (Enumeration e = node.values.keys(); e.hasMoreElements(); ) {
            String fieldName=(String)e.nextElement();
            Object value = node.values.get(fieldName);
            int fieldType = Field.TYPE_UNKNOWN;
            if (value instanceof MMObjectNode) {
                fieldType = Field.TYPE_NODE;
            } else if (value instanceof String) {
                fieldType = Field.TYPE_STRING;
            } else if (value instanceof Integer) {
                fieldType = Field.TYPE_INTEGER;
            } else if (value instanceof  byte[]) {
                fieldType = Field.TYPE_BYTE;
            } else if (value instanceof  Float) {
                fieldType = Field.TYPE_FLOAT;
            } else if (value instanceof  Double) {
                fieldType = Field.TYPE_DOUBLE;
            } else if (value instanceof  Long) {
                fieldType = Field.TYPE_LONG;
            }
            FieldDefs fd = new FieldDefs(fieldName, "field", -1, -1, fieldName, fieldType, -1, Field.STATE_VIRTUAL);
            Field ft = new BasicField(fd,this);
            fieldTypes.put(fieldName,ft);
        }
    }


    /**
     * Initializes the node.
     * Sets nodemanager to typedef, and creates a virtual node for this manager.
     */
    protected void init() {
        if (cloud == null) {
            nodeManager = ContextProvider.getDefaultCloudContext().getCloud("mmbase").getNodeManager("typedef");
        } else {
            nodeManager = cloud.getNodeManager("typedef");
        }
        noderef = new VirtualNode(BasicCloudContext.mmb.getTypeDef());
        super.init();
    }

    /**
     * Initializes the NodeManager
     */
    protected void initManager() {
        noderef.setValue("name", builder.getTableName());
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
