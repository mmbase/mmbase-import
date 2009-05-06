/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;

import java.util.Collection;

import org.mmbase.bridge.*;

/**
 * A list of nodes
 *
 * @author Pierre van Rooden
 * @version $Id$
 */
public class BasicNodeList extends AbstractNodeList<Node> implements NodeList  {

    BasicNodeList() {
        super();
    }

    public BasicNodeList(Collection c, Cloud cloud) {
        super(c, cloud);
    }

    BasicNodeList(Collection c, NodeManager nodeManager) {
        super(c, nodeManager);
    }


    @Override
    protected Node convert(Object o) {
        if (o instanceof Node || o == null) {
            return (Node) o;
        }
        return super.convert(o); 
    }

    public Node getNode(int index) {
        return get(index);
    }

    public NodeList subNodeList(int fromIndex, int toIndex) {
        if (nodeManager != null) {
            return new BasicNodeList(subList(fromIndex, toIndex), nodeManager);
        } else {
            return new BasicNodeList(subList(fromIndex, toIndex), cloud);
        }
    }

    public NodeIterator nodeIterator() {
        return new BasicNodeIterator();
    }


    protected class BasicNodeIterator extends BasicIterator implements NodeIterator {

        public Node nextNode() {
            return next();
        }

        public Node previousNode() {
            return previous();
        }
    }
}
