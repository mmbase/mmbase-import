/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;
import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.util.logging.*;



/**
 * 'Basic' implementation of bridge NodeQuery. Wraps a 'NodeSearchQuery' from core.
 *
 * @author Michiel Meeuwissen
 * @version $Id: BasicNodeQuery.java,v 1.3 2003-07-29 17:05:00 michiel Exp $
 * @since MMBase-1.7
 * @see org.mmbase.storage.search.implementation.NodeSearchQuery
 */
public class BasicNodeQuery extends BasicQuery implements NodeQuery {
    

    private static Logger log = Logging.getLoggerInstance(BasicNodeQuery.class);

    protected NodeManager nodeManager;

    BasicNodeQuery(BasicNodeManager nodeManager) {
        super(nodeManager.getCloud());
        this.nodeManager = nodeManager;
        query = new NodeSearchQuery(nodeManager.getMMObjectBuilder());
    }

    public NodeManager getNodeManager() {
        return nodeManager;
    }

    // overridden from BasicQuery (a node query does not have '.' in its field names)
    public StepField createStepField(String fieldName) {
        return ((NodeSearchQuery) query).getField(((BasicField) nodeManager.getField(fieldName)).field);
    }



}
