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
 * @version $Id: BasicNodeList.java,v 1.36 2005-09-01 15:19:29 michiel Exp $
 */
public class BasicNodeList extends BasicList implements NodeList {

    protected BasicCloud cloud;
    protected BasicNodeManager nodeManager = null;

    BasicNodeList() {
        super();
    }

    BasicNodeList(Collection c, BasicCloud cloud) {
        super(c);
        this.cloud = cloud;
    }

    BasicNodeList(Collection c, BasicNodeManager nodeManager) {
        super(c);
        this.nodeManager = nodeManager;
        this.cloud = nodeManager.cloud;
    }



    /**
     * @todo This code should probably use Casting.toNode() instead
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
                // nodemanager node
                node = new BasicNodeManager(coreNode,  cloud);
            } else if (coreBuilder instanceof RelDef || coreBuilder instanceof TypeRel) {
                // relationmanager node
                node = new BasicRelationManager(coreNode, cloud);
            } else if(coreBuilder instanceof InsRel) {
                // relation node
                if(nodeManager == null)  {
                    node = new BasicRelation(coreNode, cloud);
                } else {
                    node = new BasicRelation(coreNode, nodeManager);
                }
            } else if (coreBuilder instanceof VirtualBuilder) {
                // this is a hack, because we prefer to use query to instantiate the VirtualNodeManager (fields can be determined properly then)
                Query query = (Query) getProperty(NodeList.QUERY_PROPERTY);
                if (query != null) {
                    node = new BasicNode(coreNode, new VirtualNodeManager(query, cloud));
                } else {
                    // result of node.getRelatedNodes() or so?
                    node = new BasicNode(coreNode, new VirtualNodeManager(coreNode, cloud));
                }

            } else {
                // 'normal' node
                if(nodeManager == null)  {
                    if (cloud == null) {
                        throw new BridgeException("Could not create a Node from object '" + o + "' because this List has no Cloud,");
                        // otherwise init of BasicNode throws NPE.

                    }
                    node = new BasicNode(coreNode, cloud);
                } else {                   
                    BasicNodeManager specificNodeManager = nodeManager.cloud.getBasicNodeManager(coreNode.getBuilder().getTableName());
                    node = new BasicNode(coreNode, specificNodeManager);
                }
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
