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
import org.mmbase.util.logging.*;

/**
 * A list of node managers
 *
 * @author Pierre van Rooden
 * @version $Id: BasicNodeManagerList.java,v 1.5 2002-01-31 10:05:12 pierre Exp $
 */
public class BasicNodeManagerList extends BasicList implements NodeManagerList {
    private static Logger log = Logging.getLoggerInstance(BasicNodeManagerList.class.getName());

    protected Cloud cloud;

    /**
    * ...
    */
    BasicNodeManagerList(Collection c, Cloud cloud) {
        super(c);
        this.cloud=cloud;
    }

    /**
    *
    */
    public Object convert(Object o, int index) {
        if (o instanceof NodeManager) {
            return o;
        }
        NodeManager nm = cloud.getNodeManager((String)o);
        set(index, nm);
        return nm;
    }

    /**
    *
    */
    public NodeManager getNodeManager(int index) {
        return (NodeManager) get(index);
    }

    /**
    *
    */
    public NodeManagerIterator nodeManagerIterator() {
        return new BasicNodeManagerIterator(this);
    };

    public class BasicNodeManagerIterator extends BasicIterator implements NodeManagerIterator {

        BasicNodeManagerIterator(BasicList list) {
            super(list);
        }

        public void set(Object o) {
            if (! (o instanceof NodeManager)) {
                String message;
                message = "Object must be of type NodeManager.";
                log.error(message);
                throw new BridgeException(message);
            }
            list.set(index, o);
        }
        public void add(Object o) {
            if (! (o instanceof NodeManager)) {
                String message;
                message = "Object must be of type NodeManager.";
                log.error(message);
                throw new BridgeException(message);
            }
            list.add(index, o);
        }

        public void set(NodeManager m) {
            list.set(index, m);
        }

        public void add(NodeManager m) {
            list.add(index, m);
        }


        public NodeManager nextNodeManager() {
            return (NodeManager)next();
        }

    }

}
