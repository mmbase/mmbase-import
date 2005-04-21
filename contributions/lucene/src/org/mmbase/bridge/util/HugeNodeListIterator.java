/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.util;

import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;
import org.mmbase.cache.*;
import org.mmbase.util.logging.*;

import java.util.*;

/**
 * Iterates the big result of a query. It avoids using a lot of memory (which you would need if you
 * get the complete NodeList first), and pollution of the (node) cache. In this current
 * implementation the Query is 'batched' to avoid reading in all nodes in memory, and the batches
 * are removed from the query-caches.
 *
 * @author  Michiel Meeuwissen
 * @version $Id: HugeNodeListIterator.java,v 1.1 2005-04-21 18:16:38 pierre Exp $
 * @since   MMBase-1.8
 */

public class HugeNodeListIterator implements NodeIterator {

    public static final int DEFAULT_BATCH_SIZE = 10000;

    private static final Logger log = Logging.getLoggerInstance(HugeNodeListIterator.class);

    // will not work through RMMCI, because caches are accessed.
    protected static MultilevelCache multilevelCache  = MultilevelCache.getCache();
    protected static NodeListCache nodeListCache      = NodeListCache.getCache();

    protected NodeIterator nodeIterator;
    protected Node         nextNode;
    protected Node         previousNode;

    protected Query   originalQuery;
    protected int     batchSize = DEFAULT_BATCH_SIZE;

    protected int     nextIndex = 0;

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
     * @param query      The query which is used as a base for the querie(s) to be executed.
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
        if (originalQuery instanceof NodeQuery) {
            NodeQuery nq = (NodeQuery) currentQuery;
            nodeIterator = nq.getNodeManager().getList(nq).nodeIterator();
            nodeListCache.remove(nq);
        } else {
            nodeIterator = currentQuery.getCloud().getList(currentQuery).nodeIterator();
            multilevelCache.remove(currentQuery);
        }
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
    public Object next() {
        return nextNode();
    }

    /**
     * {@inheritDoc}
     */
    public Object previous() {
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
                for (Iterator orders = originalQuery.getSortOrders().iterator(); orders.hasNext();) {
                    SortOrder order = (SortOrder) orders.next();
                    String fieldName = order.getField().getFieldName();
                    if (fieldName.equals("number")) {
                        Constraint cons;
                        if (order.getDirection() == SortOrder.ORDER_ASCENDING) {
                            cons = currentQuery.createConstraint(order.getField(), FieldCompareConstraint.GREATER, new Integer(previousNode.getIntValue(fieldName)));
                        } else {
                            cons = currentQuery.createConstraint(order.getField(), FieldCompareConstraint.LESS, new Integer(previousNode.getIntValue(fieldName)));
                        }
                        Queries.addConstraint(currentQuery, cons);
                    }
                }
                executeNextQuery(currentQuery);

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
                // TODO: we can probably determine beforehand whether another query is needed (based on the number of results of
                // the last query). Perhasp we should test on this and so skip the running of a (superfluous) final query?
                Query currentQuery = (Query) originalQuery.clone();
                // We don't use offset to determin the 'next' batch of query results
                // because there could have been deletions/insertions.
                // We use the sort-order to apply a constraint.
                for (Iterator orders = originalQuery.getSortOrders().iterator(); orders.hasNext();) {
                    SortOrder order = (SortOrder) orders.next();
                    String fieldName = order.getField().getFieldName();
                    if (fieldName.equals("number")) {
                        Constraint cons;
                        if (order.getDirection() == SortOrder.ORDER_ASCENDING) {
                            cons = currentQuery.createConstraint(order.getField(), FieldCompareConstraint.LESS, new Integer(nextNode.getIntValue(order.getField().getFieldName())));
                        } else {
                            cons = currentQuery.createConstraint(order.getField(), FieldCompareConstraint.GREATER, new Integer(nextNode.getIntValue(order.getField().getFieldName())));
                        }
                        Queries.addConstraint(currentQuery, cons);
                    }
                }
                executePreviousQuery(currentQuery);
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
    public void add(Object o) {
        throw new UnsupportedOperationException("Optional operation 'add' not implemented");
    }

    /**
     * @throws UnsupportedOperationException
     */
    public void set(Object o) {
        throw new UnsupportedOperationException("Optional operation 'set' not implemented");
    }

    /**
     * Main only for testing.
     */
    public static void main(String[] args) {
        Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase");
        NodeQuery q = cloud.getNodeManager("object").createQuery();
        HugeNodeListIterator nodeIterator = new HugeNodeListIterator(q);
        while (nodeIterator.hasNext()) {
            Node node = nodeIterator.nextNode();
            System.out.println(node.getFunctionValue("gui", null).toString());
        }

    }


}
