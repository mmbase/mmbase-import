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
 * @version $Id: BasicNodeList.java,v 1.9 2002-03-20 13:16:47 eduard Exp $
 */
public class BasicNodeList extends BasicList implements NodeList {
    private static Logger log = Logging.getLoggerInstance(BasicNodeList.class.getName());

    protected Cloud cloud;
    protected NodeManager nodemanager=null;

    /**
    * ...
    */
    BasicNodeList(Collection c, Cloud cloud, NodeManager nodemanager) {
        super(c);
        this.cloud=cloud;
        this.nodemanager=nodemanager;
    }

    BasicNodeList(Collection c, Cloud cloud) {
        this(c, cloud, null);
    }

    /**
    *
    */
    public Object convert(Object o, int index) {
        if (o instanceof Node) {
            return o;
        }
        MMObjectNode mmn= (MMObjectNode)o;
        NodeManager nm = nodemanager;
        if (nm==null) {
            nm=cloud.getNodeManager(mmn.parent.getTableName());
        }
        Node n;
        if (mmn.parent instanceof InsRel) {
            n = new BasicRelation(mmn,nm);
        } else {
            n = new BasicNode(mmn,nm);
        }
        set(index, n);
        return n;
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


    /*
    public NodeList sort(String field, boolean order) {
           Vector nodesVector = new Vector(this);
           NodeComparator nodeComparator = new NodeComparator(field, order);
           java.util.Collections.sort(nodesVector, nodeComparator);
           return new BasicNodeList((Collection)nodesVector, cloud);
    }
    */

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


        public void set(Object o) {
            if (! (o instanceof Node)) {
                String message;
                message = "Object must be of type Node.";
                log.error(message);
                throw new BridgeException(message);
            }
            list.set(index, o);
        }
        public void add(Object o) {
            if (! (o instanceof Node)) {
                String message;
                message = "Object must be of type Node.";
                log.error(message);
                throw new BridgeException(message);
            }
            list.add(index, o);
        }


        // for efficiency reasons, we implement the same methods
        // without an 'instanceof' (a simple test program proved that
        // this is quicker)
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
