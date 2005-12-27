/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;

import java.util.Collection;
import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;

/**
 * A list of nodes
 *
 * @author Pierre van Rooden
 * @version $Id: BasicNodeList.java,v 1.41 2005-12-27 21:53:20 michiel Exp $
 */
public class BasicNodeList extends BasicList implements NodeList {

    protected Cloud cloud;
    protected NodeManager nodeManager = null;

    BasicNodeList() {
        super();
    }

    BasicNodeList(Collection c, Cloud cloud) {
        super(c);
        this.cloud = cloud;
    }

    BasicNodeList(Collection c, NodeManager nodeManager) {
        super(c);
        this.nodeManager = nodeManager;
        this.cloud = nodeManager.getCloud();
    }



    /**
     */
    protected Object convert(Object o, int index) {
        if (o instanceof Node || o == null) {
            return o;
        }
        Node node = null;
        if (o instanceof String) { // a string indicates a nodemanager by name
            node = cloud.getNodeManager((String)o);
        } else if (o instanceof MMObjectBuilder) { // a builder
            node = cloud.getNodeManager(((MMObjectBuilder)o).getTableName());
        } else {
            MMObjectNode coreNode = (MMObjectNode) o;
            MMObjectBuilder coreBuilder = coreNode.getBuilder();
            if (coreBuilder instanceof TypeDef) {
                String builderName = coreNode.getStringValue("name");
                if (cloud.hasNodeManager(builderName)) {
                    node = cloud.getNodeManager(builderName);
                } else {
                    node = cloud.getNode(coreNode.getNumber());
                }
            } else if (coreBuilder instanceof RelDef) {
                node = cloud.getRelationManager(coreNode.getStringValue("sname"));
            } else if (coreBuilder instanceof TypeRel) {
                int snumber = coreNode.getIntValue("snumber");
                int dnumber = coreNode.getIntValue("dnumber");
                int rnumber = coreNode.getIntValue("rnumber");
                NodeManager nm1 = (NodeManager) cloud.getNode(snumber);
                NodeManager nm2 = (NodeManager) cloud.getNode(dnumber);
                Node role = cloud.getNode(rnumber);
                node = cloud.getRelationManager(nm1.getName(), nm2.getName(), role.getStringValue("sname"));
            } else if(coreBuilder instanceof InsRel) {
                node = cloud.getNode(coreNode.getNumber());
            } else if (coreNode instanceof org.mmbase.module.core.VirtualNode) {
                MMObjectBuilder builder = coreNode.getBuilder();
                if (builder instanceof VirtualBuilder) {
                    node = new VirtualNode((org.mmbase.module.core.VirtualNode) coreNode, cloud);
                } else {
                    node = new VirtualNode(cloud, (org.mmbase.module.core.VirtualNode) coreNode, cloud.getNodeManager(builder.getObjectType()));
                }
            } else {
                node = cloud.getNode(coreNode.getNumber());
            }
        }
        set(index, node);
        return node;
    }

    protected Object validate(Object o) throws ClassCastException {
        if (o instanceof MMObjectNode || o instanceof String) {
            return o;
        } else {
            return (Node)o;
        }
    }

    /**
     *
     */
    public Node getNode(int index) {
        return (Node)get(index);
    }

    /**
     *
     */
    public NodeList subNodeList(int fromIndex, int toIndex) {
        return new BasicNodeList(subList(fromIndex, toIndex),cloud);
    }

    /**
     *
     */
    public NodeIterator nodeIterator() {
        return new BasicNodeIterator();
    }


    protected class BasicNodeIterator extends BasicIterator implements NodeIterator {

        public Node nextNode() {
            return (Node)next();
        }

        public Node previousNode() {
            return (Node)previous();
        }
    }
}
