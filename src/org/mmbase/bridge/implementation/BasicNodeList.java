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
import org.mmbase.module.corebuilders.InsRel;
import org.mmbase.util.logging.*;

/**
 * A list of nodes
 *
 * @author Pierre van Rooden
 * @version $Id: BasicNodeList.java,v 1.16 2002-10-03 12:28:10 pierre Exp $
 */
public class BasicNodeList extends BasicList implements NodeList {
    private static Logger log = Logging.getLoggerInstance(BasicNodeList.class.getName());
    protected BasicCloud cloud;
    protected NodeManager nodemanager = null;

    BasicNodeList() {
        super();
    }

    BasicNodeList(Collection c, BasicCloud cloud) {
        super(c);
        this.cloud=cloud;
    }

    BasicNodeList(Collection c, BasicCloud cloud, NodeManager nodemanager) {
        super(c);
        this.nodemanager = nodemanager;
        this.cloud=cloud;
    }

    /**
     *
     */
    public Object convert(Object o, int index) {
        if (o instanceof Node) {
            return o;
        }
        MMObjectNode coreNode = (MMObjectNode) o;
        MMObjectBuilder coreBuilder = coreNode.getBuilder();
        Node node = null;
        NodeManager manager = nodemanager;
        if(manager == null)  {
            manager = cloud.getNodeManager(coreBuilder.getTableName());
        }
        if(coreBuilder instanceof InsRel) {
            // we are an relation,.. this means we have to create a relation..
            node = new BasicRelation(coreNode, manager);
        } else {
            // 'normal' node
            node = new BasicNode(coreNode, manager);
        }
        set(index, node);
        return node;
    }

    protected Object validate(Object o) throws ClassCastException {
        if (o instanceof MMObjectNode) {
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
