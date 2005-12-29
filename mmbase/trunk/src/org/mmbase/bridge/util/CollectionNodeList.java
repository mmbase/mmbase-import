/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.util;

import org.mmbase.bridge.*;
import org.mmbase.bridge.implementation.BasicList;
import java.util.*;
import org.mmbase.util.Casting;
import org.mmbase.util.logging.*;

/**
 * A list of nodes, based on a Collection of Nodes
 *
 * @author Michiel Meeuwissen
 * @version $Id: CollectionNodeList.java,v 1.1 2005-12-29 19:08:01 michiel Exp $
 * @since MMBase-1.8
 */
public class CollectionNodeList extends BasicList implements NodeList {

    private static final Logger log = Logging.getLoggerInstance(CollectionNodeList.class);
    protected Cloud cloud;
    protected NodeManager nodeManager = null;



    public CollectionNodeList(Collection c, NodeManager nodeManager) {
        super(c);
        this.nodeManager = nodeManager;
        this.cloud = nodeManager.getCloud();
    }


    public CollectionNodeList(Collection c, Cloud cloud) {
        super(c);
        this.cloud = cloud;
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
        } else if (o instanceof Map) {
            if (nodeManager != null) {
                node = new MapNode((Map) o, nodeManager);
            } else {
                node = new MapNode((Map) o, cloud);
            }
        } else {
            // last desperate try
            node = cloud.getNode(Casting.toString(o));
        }
        set(index, node);
        return node;
    }

    protected Object validate(Object o) throws ClassCastException {
        if (o instanceof String || o instanceof Map || o instanceof Integer) {
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
        return new CollectionNodeList(subList(fromIndex, toIndex), cloud);
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
