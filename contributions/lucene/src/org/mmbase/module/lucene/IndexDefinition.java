/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.lucene;

import org.w3c.dom.*;

import org.mmbase.module.lucene.query.*;

/**
 * Defines a query and possible options for the fields to index.
 *
 * @author Pierre van Rooden
 * @version $Id: IndexDefinition.java,v 1.1 2005-04-21 14:28:43 pierre Exp $
 **/
class IndexDefinition extends QueryDefinition {

    /**
     * The default maximum number of nodes that are returned by a call to the searchqueryhandler.
     */
    public static final int MAX_NODES_IN_QUERY = 50;

    /**
     * The maximum number of nodes that are returned by a call to the searchqueryhandler.
     */
    int maxNodesInQuery = MAX_NODES_IN_QUERY;

    IndexDefinition(Element queryElement) {
        super(queryElement);
    }

    /**
     * Constructor, copies all data from the specified QueryDefinition object.
     */
    IndexDefinition(IndexDefinition queryDefinition) {
        super(queryDefinition);
        this.maxNodesInQuery = queryDefinition.maxNodesInQuery;
    }

}

