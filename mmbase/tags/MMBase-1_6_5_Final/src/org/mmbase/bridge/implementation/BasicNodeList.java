/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;

import java.util.Collection;
import java.util.NoSuchElementException;
import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.util.logging.*;

/**
 * A list of nodes
 *
 * @author Pierre van Rooden
 * @version $Id: BasicNodeList.java,v 1.17 2002-10-15 15:28:29 pierre Exp $
 */
public class BasicNodeList extends BasicList implements NodeList {
    private static Logger log = Logging.getLoggerInstance(BasicNodeList.class.getName());
    protected Cloud cloud;
    protected NodeManager nodeManager = null;

    BasicNodeList() {
        super();
    }

    BasicNodeList(Collection c, Cloud cloud) {
        super(c);
        this.cloud=cloud;
    }

    BasicNodeList(Collection c, NodeManager nodeManager) {
        super(c);
        this.nodeManager = nodeManager;
        this.cloud=nodeManager.getCloud();
    }

    /**
     *
     */
    protected Object convert(Object o, int index) {
        if (o instanceof Node) {
            return o;
        }
        Node node = null;
        if (o instanceof String) { // a string indicates a nodemanager by name
            node = cloud.getNodeManager((String)o);
        } else {
            MMObjectNode coreNode = (MMObjectNode) o;
            MMObjectBuilder coreBuilder = coreNode.getBuilder();
            if (coreBuilder instanceof TypeDef) {
                // nodemanager node
                node = new BasicNodeManager(coreNode, cloud);
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
            } else {
                // 'normal' node
                if(nodeManager == null)  {
                    node = new BasicNode(coreNode, cloud);
                } else {
                    node = new BasicNode(coreNode, nodeManager);
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
        return new BasicNodeIterator(this);
    }


    public class BasicNodeIterator extends BasicIterator implements NodeIterator {
        BasicNodeIterator(BasicList list) {
            super(list);
        }

        public Node nextNode() {
            return (Node)next();
        }
    }
}
