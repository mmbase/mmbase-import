/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;
import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import java.util.NoSuchElementException;
import java.util.Collection;

/**
 * A list of nodes
 *
 * @author Pierre van Rooden
 */
public class BasicNodeList extends BasicList implements NodeList {

    protected Cloud cloud;
    protected NodeManager nodemanager=null;

    /**
    * ...
    */
    public BasicNodeList(Collection c, Cloud cloud, NodeManager nodemanager) {
        super(c);
        this.cloud=cloud;
        this.nodemanager=nodemanager;
    }

    public BasicNodeList(Collection c, Cloud cloud) {
        this(c, cloud, null);
    }

    /**
    *
    */
    public Object get(int index) {
        Object o=getObject(index);
        if (o instanceof Node) {
            return (Node)o;
        }
        MMObjectNode mmn= (MMObjectNode)o;
        NodeManager nm = nodemanager;
        if (nm==null) {
            nm=cloud.getNodeManager(mmn.parent.getTableName());
        }
        Node n=new BasicNode(mmn,nm);
        objects[index]=n;
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
    };
    
    public class BasicNodeIterator extends BasicIterator implements NodeIterator {
    
        BasicNodeIterator(BasicList list) {
            super(list);
        }
    
        public Node nextNode() {
            return (Node)nextObject();
        }
    
    }
    
}
