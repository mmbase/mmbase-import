/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.util;

import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.bridge.implementation.AbstractNodeList;
import org.mmbase.util.Casting;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * An AbstractCollectionNodeList implements a {@link org.mmbase.bridge BridgeList} of {@link
 * org.mmbase.bridge.Node}s, based on a collection of objects of perhaps other type, which are
 * implicitely {@link #convert}ed when necessary.
 *
 * @since MMBase-1.8
 * @version $Id$
 * @author Nico Klasens
 */

public abstract class AbstractCollectionNodeList<E extends Node> extends AbstractBridgeList<E> {
    private static final Logger log = Logging.getLoggerInstance(AbstractCollectionNodeList.class);

    protected Cloud cloud;
    protected final NodeManager nodeManager;
    protected final List wrappedCollection;

    public AbstractCollectionNodeList(Collection<? extends Node> c, NodeManager nodeManager) {
        this.nodeManager = nodeManager;
        this.cloud = nodeManager.getCloud();
        this.wrappedCollection = convertedList(c, cloud);
    }

    public AbstractCollectionNodeList(Collection<? extends Node> c, Cloud cloud) {
        this.nodeManager = null;
        this.cloud = cloud;
        this.wrappedCollection = convertedList(c, cloud);
    }
    public AbstractCollectionNodeList(Collection c) {
        this(c, (Cloud) null);
    }


    @Override
    public int size() {
        return wrappedCollection.size();
    }

    @Override
    public E get(int index) {
        return (E) convert(wrappedCollection.get(index), index);
    }

    @Override
    public E set(int index, E o) {
        E prev = get(index);
        wrappedCollection.set(index, o);
        return prev;
    }

    @Override
    public void add(int index, E o) {
        if (o == null) throw new IllegalArgumentException();
        wrappedCollection.add(index, o);
    }

    @Override
    public E remove(int index) {
        return super.remove(index);
    }

    public Collection<Node> getCollection() {
        return wrappedCollection;
    }

    private static List convertedList(Collection c, Cloud cloud) {
        if (c instanceof List) {
            return (List<Node>) c;
        } else {
            if (cloud == null) {
                cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase", "class", null);
            }
            NodeList l = cloud.createNodeList();
            l.addAll(c);
            return l;
        }
    }

    protected Object convert(Object o, int index) {
        if (o instanceof Node || o == null) {
            return o;
        }
        Node node = null;
        try {
            if (o instanceof String &&
                ! org.mmbase.datatypes.StringDataType.INTEGER_PATTERN.matcher((String) o).matches()) { // a string indicates a nodemanager by name
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
                    // hackery
                    node = AbstractNodeList.convertWithBridgeToNode(cloud, nodeManager, o);
                    if (node == null) {
                        node = AbstractNodeList.convertMMObjectNodetoBridgeNode(cloud, nodeManager, o);
                    }
                } else {
                // even more desperate!
                    node = getCloud().getNode(Casting.toString(o));
                }
            }
        } catch (Throwable t) {
            // letting the exception go, would cause infinite loops here and there because 'next' cancels like that.
            log.error(t.getMessage(), t);
        }
        wrappedCollection.set(index, node);
        return node;
    }


    protected Cloud getCloud() {
        if (cloud == null) {
            cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase", "class", null);
        }
        return cloud;
    }

}
