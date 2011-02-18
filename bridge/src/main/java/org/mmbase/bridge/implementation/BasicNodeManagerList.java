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

    public BasicNodeManagerList(Collection c, Cloud cloud) {
        super(c, cloud);
    }


    @Override
    protected NodeManager convert(final Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof CharSequence) {
            return cloud.getNodeManager(o.toString());
        } else if (o instanceof NodeManager) {
            return (NodeManager) o;
        }
        Node superResult = super.convert(o);
        if (superResult == null) {
            log.warn(o.getClass().getName() + " "  + o + " converted to null ", new Exception());
            return null;
        }
        return superResult.toNodeManager();
    }


    @Override
    public NodeManager getNodeManager(int index) {
        return get(index);
    }

    @Override
    public NodeManagerIterator nodeManagerIterator() {
        return new BasicNodeManagerIterator();
    }

    public NodeManagerList subNodeManagerList(int fromIndex, int toIndex) {
        return new BasicNodeManagerList(subList(fromIndex, toIndex), cloud);
    }

    protected class BasicNodeManagerIterator extends BasicIterator implements NodeManagerIterator {

        @Override
        public NodeManager nextNodeManager() {
            return next();
        }

        @Override
        public NodeManager previousNodeManager() {
            return previous();
        }
    }

}
