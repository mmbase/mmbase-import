/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.implementation;

import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.MapNode;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * An abstract BasicList implementation which overrided {@link #convert} to make sure the list
 * contains {@link org.mmbase.bridge.Node}s.
 *
 * @since MMBase-1.9
 * @version $Id$
 * @author Nico Klasens
 */
public abstract class AbstractNodeList<E extends Node> extends BasicList<E>  {

    private static final Logger log = Logging.getLoggerInstance(AbstractNodeList.class);

    protected final Cloud cloud;
    protected final NodeManager nodeManager;

    AbstractNodeList() {
        super();
        this.cloud = null;
        this.nodeManager = null;
    }

    AbstractNodeList(Collection c) {
        super(c);
        this.cloud = null;
        this.nodeManager = null;
    }

    AbstractNodeList(Collection c, Cloud cloud) {
        super(c);
        this.cloud = cloud;
        this.nodeManager = null;
    }

    AbstractNodeList(Collection c, NodeManager nodeManager) {
        super(c);
        this.nodeManager = nodeManager;
        this.cloud = nodeManager.getCloud();
    }

    @Override
    protected E convert(Object o) {
        if (o == null) {
            log.debug("Null");
            return null;
        }

        if (log.isDebugEnabled()) {
            log.debug("Converting " + o.getClass());
        }

        Node node = convertWithBridgeToNode(cloud, nodeManager, o);
        return (E) node;
    }

    public Node getNode(int index) {
        return get(index);
    }

    /**
     * Converts an object to a Node, using only bridge.
     * @return a Node, or <code>null</code> if o is either <code>null</code> or could not be
     * converted.
     */
    public static Node convertWithBridgeToNode(Cloud cloud, NodeManager nodeManager, Object o) {
        if (o == null) {
            return null;
        } else if (o instanceof Node) {
            return (Node)o;
        } else if (o instanceof CharSequence) { // a string indicates a nodemanager by name, or, if numeric, a node number..
            String s = o.toString();
            if (org.mmbase.datatypes.StringDataType.NON_NEGATIVE_INTEGER_PATTERN.matcher(s).matches()) {
                return cloud.getNode(s);
            } else {
                if (cloud.hasNodeManager(s)) {
                    return cloud.getNodeManager(s);
                } else { // an alias?
                    if (cloud.hasNode(s)) {
                        return cloud.getNode(s);
                    } else {
                        log.warn("No such node '" + s + "'. Converting to null");
                        return null;
                    }
                }
            }
        } else if (o instanceof Map) {
            if (nodeManager == null) {
                return new MapNode((Map) o, cloud);
            } else {
                return new MapNode((Map) o, nodeManager);
            }
        } else if (o instanceof Number) {
            return cloud.getNode(((Number) o).intValue());
        } else {
            return null;
        }
    }

    /**
     * since MMBase 1.8
     */
    protected static NodeManager castToNodeManager(Cloud cloud, Node n) {
        if (n instanceof NodeManager) {
            return (NodeManager) n;
        } else {
            log.error("Node " + n.getNumber() + " is not a node manager (but a " + n.getNodeManager() + "), taking it Object for now");
            return cloud.getNodeManager("object");
        }
    }



}
