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
 * @version $Id: BasicNodeManagerList.java,v 1.9 2002-10-15 15:28:29 pierre Exp $
 */
public class BasicNodeManagerList extends BasicNodeList implements NodeManagerList {
    private static Logger log = Logging.getLoggerInstance(BasicNodeManagerList.class.getName());

    protected Cloud cloud;

    /**
     * ...
     */
    BasicNodeManagerList() {
        super();
    }

    BasicNodeManagerList(Collection c, Cloud cloud) {
        super(c,cloud);
    }

    protected Object validate(Object o) throws ClassCastException {
        if (o instanceof String) {
            return o;
        } else if (o instanceof MMObjectNode) {
            MMObjectBuilder bul=((MMObjectNode) o).getBuilder();
            if (bul instanceof org.mmbase.module.corebuilders.TypeDef) {
                return o;
            } else {
                throw new IllegalArgumentException("requires a node manager (typedef) node");
            }
        } else{
            return (NodeManager)o;
        }
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

    /**
     *
     */
    public NodeManagerList subNodeManagerList(int fromIndex, int toIndex) {
        return new BasicNodeManagerList(subList(fromIndex, toIndex),cloud);
    }

    public class BasicNodeManagerIterator extends BasicNodeIterator implements NodeManagerIterator {

        BasicNodeManagerIterator(BasicList list) {
            super(list);
        }

        public NodeManager nextNodeManager() {
            return (NodeManager)next();
        }
    }

}
