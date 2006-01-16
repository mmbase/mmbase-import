/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.lucene;

import java.util.*;
import org.mmbase.util.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.*;
import org.mmbase.bridge.util.xml.query.*;
import org.mmbase.storage.search.*;
import org.mmbase.util.logging.*;

import org.apache.lucene.analysis.Analyzer;

/**
 * An MMBase Lucene Index is configured by an MMBase Queries, and 'supbqueries' thereof. Also its
 * fields can have extra attributes specific to Lucene searching.
 *
 * @author Pierre van Rooden
 * @version $Id: MMBaseIndexDefinition.java,v 1.3 2006-01-16 19:24:20 pierre Exp $
 **/
class MMBaseIndexDefinition extends QueryDefinition implements IndexDefinition {
    static private final Logger log = Logging.getLoggerInstance(MMBaseIndexDefinition.class);
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

    protected Analyzer analyzer;

    IndexEntry parent;

    MMBaseIndexDefinition(IndexEntry parent) {
        this.parent = parent;
    }


    public void setAnalyzer(Analyzer a) {
        analyzer = a;
    }
    public Analyzer getAnalyzer() {
        return analyzer;
    }

    /**
     * Constructor, copies all data from the specified QueryDefinition object.

    MMBaseIndexDefinition(MMBaseIndexDefinition queryDefinition) {
        super(queryDefinition);
        this.maxNodesInQuery = queryDefinition.maxNodesInQuery;
        this.subQueries = queryDefinition.subQueries;
    }
     */

    public Node getNode(Cloud userCloud, String identifier) {
        if (userCloud.hasNode(identifier)) {
            log.trace("a node");
            if (userCloud.mayRead(identifier)) {
                return userCloud.getNode(identifier);
            } else {
                return null;
            }
        } else {
            return null;
        }

    }

    public IndexEntry getParent() {
        return parent;
    }

    /**
     * Converts an MMBase Node Iterator to an Iterator of IndexEntry-s.
     */
    protected CloseableIterator getCursor(final NodeIterator nodeIterator, final Collection f) {
        return new CloseableIterator() {
                int i = 0;
                public boolean hasNext() {
                    return nodeIterator != null && nodeIterator.hasNext();
                }
                public void remove() {
                    nodeIterator.remove();
                }
                public Object next() {
                    Node node = nodeIterator.nextNode();
                    MMBaseEntry entry = new MMBaseEntry(node, f, isMultiLevel, elementManager, subQueries);
                    i++;
                    if (i % 100 == 0) {
                        log.service("mmbase cursor " + i + " (now at id=" + entry.getIdentifier() + ")");
                    } else {
                        log.trace("mmbase cursor " + i + " (now at id=" + entry.getIdentifier() + ")");
                    }
                    return entry;
                }
                public void close() {
                    // no need for closing
                }
            };
    }

    public CloseableIterator getCursor() {
        String id = parent != null ? parent.getIdentifier() : null;
        return getCursor(getNodeIterator(id), fields);
    }


    public CloseableIterator getSubCursor(String identifier) {
        return getCursor(getNodeIterator(identifier), fields);
    }

    /**
     * Runs a query for the given cursor, an returns a NodeIterator to run over the nodes.
     * This implementation uses a HugeNodeListIterator.
     * @param cursor the cursor with query and offset information
     * @return the query result as a NodeIterator object
     * @throws SearchQueryException is the query to create the index out of failed
     */
    protected NodeIterator getNodeIterator(String id) {
        try {
            Query q = (Query) query.clone();
            String elementNumberFieldName = "number";
            if (isMultiLevel) {
                elementNumberFieldName = elementManager.getName()+".number";
            }
            if (id != null) {
                String numberFieldName = isMultiLevel ? ((Step) q.getSteps().get(0)).getAlias()+".number" : elementNumberFieldName;
                Constraint constraint = Queries.createConstraint(q, numberFieldName, FieldCompareConstraint.EQUAL, new Integer(id));
                Queries.addConstraint(q, constraint);
            }
            StepField elementNumberField = q.createStepField(elementNumberFieldName);
            q.addSortOrder(elementNumberField, SortOrder.ORDER_DESCENDING);
            return new HugeNodeListIterator(q, maxNodesInQuery);
        } catch (Exception sqe) {
            log.error(sqe);
            return null;
        }
    }

    public String toString() {
        return super.toString() + fields + "SUB[" + subQueries + "]";
    }

}

