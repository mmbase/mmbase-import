/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.implementation;

import java.util.Collection;
import java.util.Map;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.MapNode;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
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
public abstract class AbstractNodeList<E extends Node> extends BasicList<E> {

    private static final Logger log = Logging.getLoggerInstance(AbstractNodeList.class);

    protected final Cloud cloud;
    protected final NodeManager nodeManager;

    public AbstractNodeList() {
        super();
        this.cloud = null;
        this.nodeManager = null;
    }

    public AbstractNodeList(Collection c) {
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

    protected E convert(Object o) {
        if (o == null) {
            log.debug("Null");
            return null;
        }

        if (log.isDebugEnabled()) {
            log.debug("Converting " + o.getClass());
        }

        Node node = convertWithBridgeToNode(cloud, nodeManager, o);
        if (node == null) {
            log.debug("Could not convert with bridge");
            if (o instanceof MMObjectBuilder) { // a builder
                node = cloud.getNodeManager(((MMObjectBuilder)o).getTableName());
            } else {
                MMObjectNode coreNode = (MMObjectNode) o;
                node = convertMMObjectNodetoBridgeNode(cloud, nodeManager, coreNode);
            }
            //log.info("Found " + node.getClass() + " in " + getClass());
        }
        if (node == null) {
            throw new RuntimeException("Could not convert " + o.getClass() + " " + o);
        }

        return (E) node;
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

    public static Node convertMMObjectNodetoBridgeNode(Cloud cloud, NodeManager nodeManager, Object o) {
        if (o == null) return null;
        if (! (o instanceof MMObjectNode)) return null;
        MMObjectNode coreNode = (MMObjectNode) o;
        Node node;
        MMObjectBuilder coreBuilder = coreNode.getBuilder();
        if (coreBuilder instanceof TypeDef) {
            String builderName = coreNode.getStringValue("name");
            if (cloud.hasNodeManager(builderName)) {
                try {
                    node = cloud.getNodeManager(builderName);
                } catch (Throwable t) {
                    node = getNode(cloud, coreNode);
                }
            } else {
                node = getNode(cloud, coreNode);
            }
        } else if (coreBuilder instanceof RelDef) {
            node = cloud.getRelationManager(coreNode.getStringValue("sname"));
        } else if (coreBuilder instanceof TypeRel) {
            int snumber = coreNode.getIntValue("snumber");
            int dnumber = coreNode.getIntValue("dnumber");
            int rnumber = coreNode.getIntValue("rnumber");
            NodeManager nm1;
            if (cloud.hasNode(snumber)) {
                nm1 = castToNodeManager(cloud, cloud.getNode(snumber));
            } else {
                log.warn("Source of typerel " + coreNode.getNumber() + " is " + (coreNode.isNull("snumber") ? "NULL" : "" + snumber));
                nm1 = cloud.getNodeManager("object");
            }
            NodeManager nm2;
            if (cloud.hasNode(dnumber)) {
                nm2 =  castToNodeManager(cloud, cloud.getNode(dnumber));
            } else {
                log.warn("Destination of typerel " + coreNode.getNumber() + " is " + (coreNode.isNull("dnumber") ? "NULL" : "" + dnumber));
                nm2 = cloud.getNodeManager("object");
            }
            Node role;
            if (cloud.hasNode(rnumber)) {
                role = cloud.getNode(rnumber);
            } else {
                log.warn("Role of typerel " + coreNode.getNumber() + " is " + (coreNode.isNull("rnumber") ? "NULL" : "" + rnumber));
                role = cloud.getNode(BasicCloudContext.mmb.getRelDef().getNumberByName("related"));
            }
            node = cloud.getRelationManager(nm1.getName(), nm2.getName(), role.getStringValue("sname"));
        } else if(coreBuilder instanceof InsRel) {
            node = getNode(cloud, coreNode);
        } else if (coreNode instanceof org.mmbase.module.core.VirtualNode) {
            MMObjectBuilder builder = coreNode.getBuilder();
            if (builder instanceof VirtualBuilder) {
                if (nodeManager != null) {
                    node = new VirtualNode(cloud, (org.mmbase.module.core.VirtualNode) coreNode, nodeManager);
                } else {
                    if (cloud != null) {
                        node = new VirtualNode((org.mmbase.module.core.VirtualNode) coreNode, cloud);
                    } else {
                        // last resort: use an anonymous cloud
                        // ? use class security?
                        node = new VirtualNode((org.mmbase.module.core.VirtualNode) coreNode, ContextProvider.getDefaultCloudContext().getCloud("mmbase"));
                    }
                }
            } else {
                node = new VirtualNode(cloud, (org.mmbase.module.core.VirtualNode) coreNode, cloud.getNodeManager(builder.getObjectType()));
            }
        } else {
            node =  getNode(cloud, coreNode);
        }
        return node;
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

    /**
     * @since MMBase-1.8.4
     */
    protected static Node getNode(Cloud cloud, MMObjectNode coreNode) {
        int n = coreNode.getNumber();
        if (n == -1) {
            String[] na  = coreNode.getStringValue("_number").split("_");
            if (na.length == 2) {
                if (cloud.hasNode(na[1])) {
                    return cloud.getNode(na[1]);
                } else {
                    return new BasicNode(coreNode, (BasicCloud) cloud);
                }
            } else {
                throw new RuntimeException("Could not make a Node of " + coreNode);
            }
        } else {
            if (cloud.hasNode(n)) {
                return cloud.getNode(n);
            } else {
                return new BasicNode(coreNode, (BasicCloud) cloud);
            }
        }
    }

}
