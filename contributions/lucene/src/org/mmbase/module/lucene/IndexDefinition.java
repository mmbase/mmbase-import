/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.lucene;

import java.util.List;
import java.util.ArrayList;
import org.w3c.dom.*;

import org.mmbase.bridge.util.xml.query.*;

/**
 * Defines a query and possible options for the fields to index.
 *
 * @author Pierre van Rooden
 * @version $Id: IndexDefinition.java,v 1.4 2005-07-27 13:59:58 pierre Exp $
 **/
class IndexDefinition extends QueryDefinition {

    /**
     * The default maximum number of nodes that are returned by a call to the searchqueryhandler.
     */
    public static final int MAX_NODES_IN_QUERY = 200;

    /**
     * The maximum number of nodes that are returned by a call to the searchqueryhandler.
     */
    int maxNodesInQuery = MAX_NODES_IN_QUERY;

    /**
     * Subqueries for this index. The subqueries are lists whose starting element is the element node from the
     * current index result.
     */
    List subQueries = new ArrayList();

    IndexDefinition(QueryConfigurer configurer) {
        super(configurer);
    }

    /**
     * Constructor, copies all data from the specified QueryDefinition object.
     */
    IndexDefinition(IndexDefinition queryDefinition) {
        super(queryDefinition);
        this.maxNodesInQuery = queryDefinition.maxNodesInQuery;
        this.subQueries = queryDefinition.subQueries;
    }

}

