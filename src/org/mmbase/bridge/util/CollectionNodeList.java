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
 * A (fixed-size) list of nodes, based on a Collection of Nodes. If the collection is a List it
 * will mirror its' changes. Otherwise, no, because the complete collection is inserted in its own
 * one.
 *
 * @author Michiel Meeuwissen
 * @version $Id: CollectionNodeList.java,v 1.6 2006-10-05 10:58:34 michiel Exp $
 * @since MMBase-1.8
 */
public class CollectionNodeList<E extends Node> extends AbstractBridgeList<E> implements NodeList<E> {

    private static final Logger log = Logging.getLoggerInstance(CollectionNodeList.class);
    protected Cloud cloud;
    protected NodeManager nodeManager = null;

    protected final List wrappedCollection;



    public CollectionNodeList(Collection c, NodeManager nodeManager) {
        this.nodeManager = nodeManager;
        this.cloud = nodeManager.getCloud();
        this.wrappedCollection = convertedList(c, cloud);
    }


    public CollectionNodeList(Collection c, Cloud cloud) {
        this.cloud = cloud;
        this.wrappedCollection = convertedList(c, cloud);
    }
    public CollectionNodeList(Collection c) {
        this(c, (Cloud) null);
    }

    protected Cloud getCloud() {
        if (cloud == null) {
            cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase", "class", null);
        }
        return cloud;
    }

    private static List convertedList(Collection c, Cloud cloud) {
        if (c instanceof List) {
            return (List) c;
        } else {
            if (cloud == null) {
                cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase", "class", null);
            }
            NodeList l = cloud.createNodeList();
            l.addAll(c);
            return l;
        }
    }


    public int size() {
        return wrappedCollection.size();
    }
    public E get(int index) {
        return (E) convert(wrappedCollection.get(index), index);
    }

    public E set(int index, E o) {
        E prev = get(index);
        wrappedCollection.set(index, o);
        return prev;
    }

    public boolean add(E o) {
        if (o == null) throw new IllegalArgumentException();
        return super.add(o);
    }

    /**
     */
    protected Object convert(Object o, int index) {
        if (o instanceof Node || o == null) {
            return (Node) o;
        }
        Node node = null;
        if (o instanceof String) { // a string indicates a nodemanager by name
            node = getCloud().getNodeManager((String)o);
        } else if (o instanceof Map) {
            if (nodeManager != null) {
                node = new MapNode((Map) o, nodeManager);
            } else {
                node = new MapNode((Map) o, getCloud());
            }
        } else {
            if (! (wrappedCollection instanceof NodeList)) {
                // last desperate try, depend on a nodelist of cloud (that know how to convert core objects..)
                NodeList nl = getCloud().createNodeList(); // hackery
                nl.add(o);
                node = nl.getNode(0);
            } else {
                // even more desperate!
                node = getCloud().getNode(Casting.toString(o));
            }
        }
        wrappedCollection.set(index, (E) node);
        return node;
    }

    /**
     *
     */
    public Node getNode(int index) {
        return get(index);
    }

    /**
     *
     */
    public NodeList<E> subNodeList(int fromIndex, int toIndex) {
        return new CollectionNodeList<E>(subList(fromIndex, toIndex), cloud);
    }

    /**
     *
     */
    public NodeIterator<E> nodeIterator() {
        return new BasicNodeIterator();
    }
    public Collection getCollection() {
        return wrappedCollection;
    }


    protected class BasicNodeIterator extends BasicIterator implements NodeIterator<E> {

        public Node nextNode() {
            return next();
        }

        public Node previousNode() {
            return previous();
        }
    }
}
