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
 * @version $Id: VirtualNodeManager.java,v 1.9 2002-01-31 10:05:13 pierre Exp $
 */
public class VirtualNodeManager extends BasicNodeManager {
    private static Logger log = Logging.getLoggerInstance(VirtualNodeManager.class.getName());

    VirtualNodeManager(MMObjectBuilder builder, Cloud cloud) {
        this.cloud=(BasicCloud)cloud;
        this.builder=builder;
        for(Iterator i=builder.getFields().iterator(); i.hasNext();){
            FieldDefs f=(FieldDefs)i.next();
            Field ft= new BasicField(f,this);
            fieldTypes.put(ft.getName(),ft);
        }
    }

    VirtualNodeManager(MMObjectNode node, Cloud cloud) {
        this.cloud=(BasicCloud)cloud;
        this.builder=node.parent;
        // determine fields and field types
        for (Enumeration e = node.values.keys(); e.hasMoreElements(); ) {
            String fieldName=(String)e.nextElement();
            Object value = node.values.get(fieldName);
            int fieldType = Field.TYPE_UNKNOWN;
            if (value instanceof String) {
                fieldType = Field.TYPE_STRING;
            }
            if (value instanceof Integer) {
                fieldType = Field.TYPE_INTEGER;
            }
            if (value instanceof  byte[]) {
                fieldType = Field.TYPE_BYTE;
            }
            if (value instanceof  Float) {
                fieldType = Field.TYPE_FLOAT;
            }
            if (value instanceof  Double) {
                fieldType = Field.TYPE_DOUBLE;
            }
            if (value instanceof  Long) {
                fieldType = Field.TYPE_LONG;
            }
            FieldDefs fd= new FieldDefs(fieldName, "field", -1, -1, fieldName, fieldType,-1, Field.STATE_VIRTUAL);
            Field ft = new BasicField(fd,this);
            fieldTypes.put(fieldName,ft);
        }
    }

    /**
     * Gets a new (initialized) node.
     * Throws an exception since this type is virtual, and creating nodes is not allowed.
     */
    public Node createNode() {
        String message;
        message = "Cannot create a node from a virtual node type.";
        log.error(message);
        throw new BridgeException(message);
    }

    /**
     * Search nodes of this type.
     * Throws an exception since this type is virtual, and searching is not allowed.
     */
    public NodeList getList(String where, String sorted, boolean direction) {
        String message;
        message = "Cannot perform search on a virtual node type.";
        log.error(message);
        throw new BridgeException(message);
    }
}
