/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;

import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;

/**
 * A list of relation managers
 *
 * @author Pierre van Rooden
 * @version $Id: BasicRelationManagerList.java,v 1.7 2002-09-23 14:31:04 pierre Exp $
 */
public class BasicRelationManagerList extends BasicNodeManagerList implements RelationManagerList {
    private static Logger log = Logging.getLoggerInstance(BasicRelationManagerList.class.getName());

    /**
     * ...
     */
    BasicRelationManagerList(Cloud cloud) {
        super(cloud);
    }

    BasicRelationManagerList(Collection c, Cloud cloud) {
        super(c,cloud);
    }

    protected Object validate(Object o) throws ClassCastException {
        if (o instanceof MMObjectNode) {
            return o;
        } else {
            return (RelationManager)o;
        }
    }

    /**
     *
     */
    public Object convert(Object o, int index) {
        if (o instanceof RelationManager) {
            return o;
        }
        RelationManager rm = new BasicRelationManager((MMObjectNode)o,cloud);
        set(index, rm);
        return rm;
    }

    /**
    *
    */
    public RelationManager getRelationManager(int index) {
        return (RelationManager) get(index);
    }

    /**
    *
    */
    public RelationManagerIterator relationManagerIterator() {
        return new BasicRelationManagerIterator(this);
    };

    public class BasicRelationManagerIterator extends BasicNodeManagerIterator implements RelationManagerIterator {

        BasicRelationManagerIterator(BasicList list) {
            super(list);
        }

        public RelationManager nextRelationManager() {
            return (RelationManager)next();
        }

    }

}
