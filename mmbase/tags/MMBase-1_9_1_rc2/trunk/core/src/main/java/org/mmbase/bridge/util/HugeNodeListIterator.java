/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.util;

import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;
import org.mmbase.cache.CachePolicy;
import org.mmbase.util.logging.*;

import java.util.*;

/**
 * Iterates the big result of a query. It avoids using a lot of memory (which you would need if you
 * get the complete NodeList first), and pollution of the (node) cache. In this current
 * implementation the Query is 'batched' to avoid reading in all nodes in memory, and the queries
 * are marked with {@link CachePolicy#NEVER}.
 *
 * @author  Michiel Meeuwissen
 * @version $Id$
 * @since   MMBase-1.8
 */

public class HugeNodeListIterator implements NodeIterator {

    public static final int DEFAULT_BATCH_SIZE = 10000;

    // log
    private static final Logger log = Logging.getLoggerInstance(HugeNodeListIterator.class);

    protected NodeIterator nodeIterator;
    protected Node nextNode;
    protected Node previousNode;

    protected Query originalQuery;
    protected int batchSize = DEFAULT_BATCH_SIZE;

    protected int nextIndex = 0;

    /**
     * Constructor for this Iterator.
     *
     * @param query     The query which is used as a base for the querie(s) to be executed.
     * @param batchSize The (approximate) size of the sub-queries, should be a reasonably large
     *                   number, like 10000 or so.
     */
    public HugeNodeListIterator(Query query, int batchSize) {
        this.batchSize = batchSize;
        init(query);
    }

    /**
     * Constructor for this Iterator. The 'batchSize' is taken from the query's 'maxnumber'
     * properties, or, it that is not set, it is defaulted to 10000.
     *
     * @param query The query which is used as a base for the querie(s) to be executed.
     */
    public HugeNodeListIterator(Query query) {
        if (query.getMaxNumber() != SearchQuery.DEFAULT_MAX_NUMBER) {
            batchSize = query.getMaxNumber();
        } // else leave on default;
        init(query);
    }

    /**
     * Called by constructors only
     */
    private void init(Query query) {
        if (query.getOffset() > 0) {
            throw new UnsupportedOperationException("Not implemented for queries with offset");
        }
        Queries.sortUniquely(query);
        originalQuery = query;
        executeNextQuery((Query) originalQuery.clone());
    }


    /**
     * Executes the given query, taking into account the fact wether it is NodeQuery or not, and
     * applying the 'batchSize'. The result is available in the 'nodeIterator' member.
     */
    protected void executeQuery(Query currentQuery) {
        currentQuery.setMaxNumber(batchSize);
        if (log.isDebugEnabled()) {
            log.trace("Running query: " + currentQuery);
        }
        NodeList list;
        currentQuery.setCachePolicy(CachePolicy.NEVER);
        if (originalQuery instanceof NodeQuery) {
            NodeQuery nq = (NodeQuery) currentQuery;
            list = nq.getNodeManager().getList(nq);
        } else {
            list = currentQuery.getCloud().getList(currentQuery);
        }
        if (log.isDebugEnabled()) {
            log.trace("Query result: " + list.size() + " nodes");
        }
        nodeIterator = list.nodeIterator();
    }

    /**
     * Executes the given query, and prepares to do 'next', so setting 'nextNode' and 'previousNode'.
     */
    protected void executeNextQuery(Query q) {
        executeQuery(q);
        previousNode = nextNode;
        if (nodeIterator.hasNext()) {
            nextNode = nodeIterator.nextNode();
        } else {
            nextNode = null;
        }
    }
    /**
     * Executes the given query, and prepares to do 'previous', so setting 'nextNode' and
     * 'previousNode', and winds the new nodeIterator to the end.
     */
    protected void executePreviousQuery(Query q) {
        executeQuery(q);
        nextNode = previousNode;
        while (nodeIterator.hasNext()) {
            nodeIterator.next();
        }
        if (nodeIterator.hasPrevious()) {
            previousNode = nodeIterator.nextNode();
        } else {
            previousNode = null;
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean hasNext() {
        return nextNode != null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasPrevious() {
        return previousNode != null;
    }


    /**
     * {@inheritDoc}
     */
    public Node next() {
        return nextNode();
    }

    /**
     * {@inheritDoc}
     */
    public Node previous() {
        return previousNode();
    }
    /**
     * {@inheritDoc}
     */
    public int previousIndex() {
        return nextIndex - 1;
    }
    /**
     * {@inheritDoc}
     */
    public int nextIndex() {
        return nextIndex;
    }

    /**
     * Used by nextNode and previousNode. Does a field-by-field compare of two Node objects, on
     * the fields used to order the nodes.
     * This is used to determine whether a node comes after or before another - allowing
     * the node iterator to skip nodes it already 'had'.
     * @return -1 if node1 is smaller than node 2, 0 if both nodes are equals, and +1 is node 1 is greater than node 2.
     */
    protected int compares(Node node1, Node node2) {
        return Queries.compare(node1, node2, originalQuery.getSortOrders());
    }

    /**
     * {@inheritDoc}
     *
     * Implementation calculates also the next next Node, and gives back the 'old' next Node, from
     * now on known as 'previousNode'.
     */
    public  Node nextNode() {
        if (nextNode != null) {
            nextIndex++;
            previousNode = nextNode;
            if (nodeIterator.hasNext()) {
                nextNode = nodeIterator.nextNode();
            } else {
                Query currentQuery = (Query) originalQuery.clone();

                // We don't use offset to determin the 'next' batch of query results
                // because there could have been deletions/insertions.
                // We use the sort-order to apply a constraint.
                SortOrder order = originalQuery.getSortOrders().get(0);
                Object value = Queries.getSortOrderFieldValue(previousNode, order);
                Constraint cons;
                if (order.getDirection() == SortOrder.ORDER_ASCENDING) {
                    cons = currentQuery.createConstraint(order.getField(), FieldCompareConstraint.GREATER_EQUAL, value);
                } else {
                    cons = currentQuery.createConstraint(order.getField(), FieldCompareConstraint.LESS_EQUAL, value);
                }
                Queries.addConstraint(currentQuery, cons);

                executeNextQuery(currentQuery);

                // perhaps the sort-order did not find a unique result, skip some nodes in that case.
                // XXX This goes wrong if (which is unlikely) there follow more nodes than 'batchSize'.
                while(nextNode != null && compares(nextNode, previousNode) <= 0) {
                    if (nodeIterator.hasNext()) {
                        nextNode = nodeIterator.nextNode();
                    } else {
                        nextNode = null;
                    }
                }
            }

            return previousNode; // looks odd, but really is wat is meant.
        } else {
            throw new NoSuchElementException("No next element");
        }
    }

    /**
     * {@inheritDoc}
     *
     * Implementation is analogous to nextNode.
     */
    public  Node previousNode() {
        if (previousNode != null) {
            nextNode = previousNode;
            nextIndex --;
            if (nodeIterator.hasPrevious()) {
                previousNode = nodeIterator.previousNode();
            } else {
                Query currentQuery = (Query) originalQuery.clone();
                SortOrder order = originalQuery.getSortOrders().get(0);
                Object value = Queries.getSortOrderFieldValue(nextNode, order);
                Constraint cons;
                if (order.getDirection() == SortOrder.ORDER_ASCENDING) {
                    cons = currentQuery.createConstraint(order.getField(), FieldCompareConstraint.LESS_EQUAL, value);
                } else {
                    cons = currentQuery.createConstraint(order.getField(), FieldCompareConstraint.GREATER_EQUAL, value);
                }
                Queries.addConstraint(currentQuery, cons);
                executePreviousQuery(currentQuery);
                while(previousNode != null && compares(nextNode, previousNode) >= 0) {
                    if (nodeIterator.hasPrevious()) {
                        previousNode = nodeIterator.previousNode();
                    } else {
                        previousNode = null;
                    }
                }

            }
            return nextNode;
        } else {
            throw new NoSuchElementException("No previous element");
        }
    }

   /**
     * @throws UnsupportedOperationException
     */
    public void remove() {
        throw new UnsupportedOperationException("Optional operation 'remove' not implemented");
    }

    /**
     * @throws UnsupportedOperationException
     */
    public void add(Node o) {
        throw new UnsupportedOperationException("Optional operation 'add' not implemented");
    }

    /**
     * @throws UnsupportedOperationException
     */
    public void set(Node o) {
        throw new UnsupportedOperationException("Optional operation 'set' not implemented");
    }

    /**
     * Main only for testing.
     */
    public static void main(String[] args) {
        Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase");
        NodeQuery q = cloud.getNodeManager("object").createQuery();
        HugeNodeListIterator nodeIterator = new HugeNodeListIterator(q, 20);
        int i = 0;
        while (nodeIterator.hasNext()) {
            Node node = nodeIterator.nextNode();
            System.out.println("" + (i++) + ": " + node.getNumber() + " " + node.getNodeManager().getName() + " " + node.getFunctionValue("gui", null).toString());
        }

    }

}
