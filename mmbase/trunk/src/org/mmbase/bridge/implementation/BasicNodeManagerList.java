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
 * @version $Id: BasicNodeManagerList.java,v 1.8 2002-10-03 12:28:10 pierre Exp $
 */
public class BasicNodeManagerList extends BasicList implements NodeManagerList {
    private static Logger log = Logging.getLoggerInstance(BasicNodeManagerList.class.getName());

    protected BasicCloud cloud;

    /**
     * ...
     */
    BasicNodeManagerList() {
        super();
    }

    BasicNodeManagerList(Collection c, BasicCloud cloud) {
        super(c);
        this.cloud=cloud;
    }

    protected Object validate(Object o) throws ClassCastException {
        if (o instanceof String) {
            return o;
        } else {
            return (NodeManager)o;
        }
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

        public NodeManager nextNodeManager() {
            return (NodeManager)next();
        }
    }

}
