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
 * @version $Id: BasicNodeList.java,v 1.11 2002-06-17 10:49:47 eduard Exp $
 */
public class BasicNodeList extends BasicList implements NodeList {
    private static Logger log = Logging.getLoggerInstance(BasicNodeList.class.getName());
    protected Cloud cloud;
    protected NodeManager nodemanager = null;
    
    BasicNodeList(Collection c, Cloud cloud) {
        super(c);
        this.cloud=cloud;
    }

    BasicNodeList(Collection c, Cloud cloud, NodeManager nodemanager) {
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
        Node node = null;
        MMObjectBuilder coreBuilder = coreNode.getBuilder();
        NodeManager nodeManager = cloud.getNodeManager(coreBuilder.getTableName());
        if(coreBuilder instanceof InsRel) {
            // we are an relation,.. this means we have to create a relation..
            node = new BasicRelation(coreNode, nodeManager);
        }
        else {
            // 'normal' node
            node = new BasicNode(coreNode, nodeManager);
        }
        set(index, node);
        return node;
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
                
        public void set(Node n) {
            list.set(index, n);
        }
        public void add(Node n) {
            list.add(index, n);
        }

        public Node nextNode() {
            return (Node)next();
        }
    }
}
