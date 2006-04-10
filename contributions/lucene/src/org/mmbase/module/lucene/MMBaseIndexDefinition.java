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
 * @version $Id: MMBaseIndexDefinition.java,v 1.9 2006-04-10 10:49:47 michiel Exp $
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

    public Node getNode(Cloud userCloud, String identifier) {
        if (userCloud.hasNode(identifier)) {
            if (log.isTraceEnabled()) {
                log.trace("a node (" + identifier + ")");
            }
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
                    if (log.isServiceEnabled()) {
                        if (i % 100 == 0) {
                            log.service("mmbase cursor " + i + " (now at id=" + entry.getIdentifier() + ")");
                        } else if (log.isDebugEnabled()) {
                            log.trace("mmbase cursor " + i + " (now at id=" + entry.getIdentifier() + ")");
                        }
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
     * Creates an (Huge)NodeListIterator for this index definition
     * @param id A node number. If used, the query will be limited. This is used to update the index on change of that node.
     * @return the query result as a NodeIterator object
     */
    protected NodeIterator getNodeIterator(String id) {
        try {
            Query q = (Query) query.clone();
            String elementNumberFieldName = "number";
            if (isMultiLevel) {
                elementNumberFieldName = elementManager.getName() + ".number";
            }
            if (id != null) {
                Integer number = new Integer(id);
                Node node = query.getCloud().getNode(number.intValue());
                NodeManager nm = node.getNodeManager();
                Iterator i = q.getSteps().iterator();
                Constraint comp = null;
                while(i.hasNext()) {
                    Step step = (Step) i.next();
                    NodeManager stepManager = query.getCloud().getNodeManager(step.getTableName());
                    if (stepManager.equals(nm) || stepManager.getDescendants().contains(nm)) {
                        StepField numberField = q.createStepField(step, "number");
                        Constraint constraint = q.createConstraint(numberField, number);
                        comp = q.createConstraint(comp, CompositeConstraint.LOGICAL_OR, constraint);
                    }
                }
                if (comp == null) return BridgeCollections.EMPTY_NODELIST.nodeIterator();
                Queries.addConstraint(q, comp);

            }
            StepField elementNumberField = q.createStepField(elementNumberFieldName);
            q.addSortOrder(elementNumberField, SortOrder.ORDER_DESCENDING); // this sort order makes it possible to filter out duplicates.
            if (log.isDebugEnabled()) {
                log.debug("Query for node '" + id + "': " + q.toSql());
            }
            return new HugeNodeListIterator(q, maxNodesInQuery);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return null;
        }
    }

    public String toString() {
        return super.toString() + fields + "SUB[" + subQueries + "]";
    }

}

