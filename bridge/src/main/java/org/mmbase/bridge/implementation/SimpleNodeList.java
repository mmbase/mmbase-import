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
 * @author Michiel Meeuwissen
 * @version $Id: BasicNodeList.java 38033 2009-08-19 16:59:15Z michiel $
 * @since MMBase-2.0
 */
public class SimpleNodeList extends AbstractNodeList<Node> implements NodeList  {

    SimpleNodeList() {
        super();
    }

    public SimpleNodeList(Collection c, Cloud cloud) {
        super(c, cloud);
    }

    public SimpleNodeList(Collection c, NodeManager nodeManager) {
        super(c, nodeManager);
    }


    public NodeList subNodeList(int fromIndex, int toIndex) {
        if (nodeManager != null) {
            return new SimpleNodeList(subList(fromIndex, toIndex), nodeManager);
        } else {
            return new SimpleNodeList(subList(fromIndex, toIndex), cloud);
        }
    }

    public NodeIterator nodeIterator() {
        return new SimpleNodeIterator();
    }


    protected class SimpleNodeIterator extends BasicIterator implements NodeIterator {

        public Node nextNode() {
            return next();
        }

        public Node previousNode() {
            return previous();
        }
    }



}
