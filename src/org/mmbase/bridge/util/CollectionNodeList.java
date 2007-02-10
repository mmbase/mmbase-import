/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.util;

import org.mmbase.bridge.*;
import java.util.*;
import org.mmbase.util.Casting;
import org.mmbase.util.logging.*;

/**
 * A (fixed-size) list of nodes, based on a Collection of Nodes. If the collection is a List it
 * will mirror its' changes. Otherwise, no, because the complete collection is inserted in its own
 * one.
 *
 * @author Michiel Meeuwissen
 * @version $Id: CollectionNodeList.java,v 1.8 2007-02-10 15:47:42 nklasens Exp $
 * @since MMBase-1.8
 */
public class CollectionNodeList extends AbstractCollectionNodeList<Node> implements NodeList {

    private static final Logger log = Logging.getLoggerInstance(CollectionNodeList.class);

    public CollectionNodeList(Collection c, NodeManager nodeManager) {
        super(c, nodeManager);
    }

    public CollectionNodeList(Collection c, Cloud cloud) {
        super(c, cloud);
    }
    
    public CollectionNodeList(Collection c) {
        super(c, (Cloud) null);
    }

    public Node getNode(int index) {
        return get(index);
    }

    public NodeList subNodeList(int fromIndex, int toIndex) {
        return new CollectionNodeList(subList(fromIndex, toIndex), cloud);
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
