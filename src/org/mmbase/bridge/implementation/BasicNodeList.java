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
import org.mmbase.module.core.*;
import org.mmbase.bridge.util.MapNode;
import org.mmbase.module.corebuilders.*;
import org.mmbase.util.logging.*;

/**
 * A list of nodes
 *
 * @author Pierre van Rooden
 * @version $Id$
 */
public class BasicNodeList extends BasicList implements NodeList {

    private static final Logger log = Logging.getLoggerInstance(BasicNodeList.class);
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
     * since MMBase 1.8
     */
    protected NodeManager castToNodeManager(Node n) {
        if (n instanceof NodeManager) {
            return (NodeManager) n;
        } else {
            log.error("Node " + n.getNumber() + " is not a node manager (but a " + n.getNodeManager() + "), taking it Object for now");
            return cloud.getNodeManager("object");
        }
    }


    /**
     */
    protected Object convert(Object o, int index) {
        if (o instanceof Node || o == null) {
            return o;
        }
        Node node = null;
        if (o instanceof String) { // a string indicates a nodemanager by name, or, if numeric, a node number..
            String s = (String) o;
            if (org.mmbase.datatypes.StringDataType.NON_NEGATIVE_INTEGER_PATTERN.matcher(s).matches()) {
                node = cloud.getNode(s);
            } else {
                if (cloud.hasNodeManager(s)) {
                    node = cloud.getNodeManager(s);
                } else { // an alias?
                    if (cloud.hasNode(s)) {
                        node = cloud.getNode(s);
                    } else {
                        log.warn("No such node '" + s + "'. Converting to null");
                        node = null;
                    }
                }
            }
        } else if (o instanceof MMObjectBuilder) { // a builder
            node = cloud.getNodeManager(((MMObjectBuilder)o).getTableName());
        } else if (o instanceof Map) {
            if (nodeManager == null) {
                node = new MapNode((Map) o, cloud);
            } else {
                node = new MapNode((Map) o, nodeManager);
            }
        } else if (o instanceof Number) {
            node = cloud.getNode(((Number) o).intValue());
        } else {
            MMObjectNode coreNode = (MMObjectNode) o;
            MMObjectBuilder coreBuilder = coreNode.getBuilder();
            if (coreBuilder instanceof TypeDef) {
                String builderName = coreNode.getStringValue("name");
                if (cloud.hasNodeManager(builderName)) {
                    try {
                        node = cloud.getNodeManager(builderName);
                    } catch (Throwable t) {
                        node = cloud.getNode(coreNode.getNumber());
                    }
                } else {
                    node = cloud.getNode(coreNode.getNumber());
                }
            } else if (coreBuilder instanceof RelDef) {
                node = cloud.getRelationManager(coreNode.getStringValue("sname"));
            } else if (coreBuilder instanceof TypeRel) {
                int snumber = coreNode.getIntValue("snumber");
                int dnumber = coreNode.getIntValue("dnumber");
                int rnumber = coreNode.getIntValue("rnumber");
                NodeManager nm1;
                if (cloud.hasNode(snumber)) {
                    nm1 = castToNodeManager(cloud.getNode(snumber));
                } else {
                    log.warn("Source of typerel " + coreNode.getNumber() + " is " + (coreNode.isNull("snumber") ? "NULL" : "" + snumber));
                    nm1 = cloud.getNodeManager("object");
                }
                NodeManager nm2;
                if (cloud.hasNode(dnumber)) {
                    nm2 =  castToNodeManager(cloud.getNode(dnumber));
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
                node = cloud.getNode(coreNode.getNumber());
            } else if (coreNode instanceof org.mmbase.module.core.VirtualNode) {
                MMObjectBuilder builder = coreNode.getBuilder();
                if (builder instanceof VirtualBuilder) {
                    if (nodeManager != null) {
                        node = new VirtualNode(cloud, (org.mmbase.module.core.VirtualNode) coreNode, nodeManager);
                    } else {
                        node = new VirtualNode((org.mmbase.module.core.VirtualNode) coreNode, cloud);
                    }
                } else {
                    node = new VirtualNode(cloud, (org.mmbase.module.core.VirtualNode) coreNode, cloud.getNodeManager(builder.getObjectType()));
                }
            } else {
                int n = coreNode.getNumber();
                if (cloud.hasNode(n)) {
                    node = cloud.getNode(n);
                } else {
                    log.warn("No node with number " + n + " converting to null");
                    node = null;
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
        if (nodeManager != null) {
            return new BasicNodeList(subList(fromIndex, toIndex), nodeManager);
        } else {
            return new BasicNodeList(subList(fromIndex, toIndex), cloud);
        }
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
