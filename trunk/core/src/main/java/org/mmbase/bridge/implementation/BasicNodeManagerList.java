/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;

import java.util.Collection;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;

/**
 * A list of node managers
 *
 * @author Pierre van Rooden
 * @version $Id$
 */
public class BasicNodeManagerList extends AbstractNodeList<NodeManager> implements NodeManagerList {
    
    private static final Logger log = Logging.getLoggerInstance(BasicNodeManagerList.class);

    BasicNodeManagerList() {
        super();
    }

    BasicNodeManagerList(Collection c, Cloud cloud) {
        super(c, cloud);
    }

    
    protected NodeManager convert(Object o) {
        if (o == null) return null;
        if (o instanceof CharSequence) {
            return cloud.getNodeManager(o.toString());
        }
        return super.convert(o).toNodeManager();
    }


    public NodeManager getNodeManager(int index) {
        return get(index);
    }

    public NodeManagerIterator nodeManagerIterator() {
        return new BasicNodeManagerIterator();
    };

    public NodeManagerList subNodeManagerList(int fromIndex, int toIndex) {
        return new BasicNodeManagerList(subList(fromIndex, toIndex), cloud);
    }

    protected class BasicNodeManagerIterator extends BasicIterator implements NodeManagerIterator {

        public NodeManager nextNodeManager() {
            return next();
        }

        public NodeManager previousNodeManager() {
            return previous();
        }
    }

}
