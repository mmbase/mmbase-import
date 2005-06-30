/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.util.xml.query;

import java.util.Collection;
import org.w3c.dom.*;

import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;

/**
 * Defines a query and possible options for the fields to index.
 *
 * @author Pierre van Rooden
 * @version $Id: QueryDefinition.java,v 1.1 2005-06-30 12:37:54 pierre Exp $
 **/
public class QueryDefinition {

    /**
     * The query to run
     */
    public Query query = null;

    /**
     * If <code>true</code>, the query in this definition returns cluster nodes
     */
    public boolean isMultiLevel = false;

    /**
     * A collection of FieldDefinition objects, containing properties for the fields to index.
     */
    public Collection fields = null;

    /**
     * The NodeManager of the 'main' nodetype in this query (if appropriate).
     */
    public NodeManager elementManager = null;

    /**
     * The step in the query describing the 'main' nodetype (if appropriate).
     */
    public Step elementStep = null;

    /**
     * Constructor.
     */
    public QueryDefinition(Element queryElement) {
    }

    /**
     * Constructor, copies all data from the specified QueryDefinition object.
     */
    public QueryDefinition(QueryDefinition queryDefiniton) {
        this.query = queryDefiniton.query;
        this.fields = queryDefiniton.fields;
        this.isMultiLevel = queryDefiniton.isMultiLevel;
        this.elementManager = queryDefiniton.elementManager;
        this. elementStep = queryDefiniton.elementStep;
    }

}

