/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.util;

import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.cache.*;

import java.util.*;



/**
 * Iterates the big result of a query. The query is 'batched' to avoid reading in all nodes in
 * memory, and the batches are removed from the query-caches.
 *
 *
 * @author  Michiel Meeuwissen
 * @version $Id: HugeNodeListIterator.java,v 1.1 2004-04-02 09:38:16 michiel Exp $
 * @since   MMBase-1.8
 */

public class HugeNodeListIterator implements NodeIterator {

    protected static MultilevelCache multilevelCache  = MultilevelCache.getCache(); 
    protected static NodeListCache nodeListCache      = NodeListCache.getCache();

    protected NodeIterator nodeIterator;
    protected Node         nextNode;
    protected Node         previousNode;

    protected Query   originalQuery;
    protected int     batchSize = 10000;

    protected int     nextIndex = 0;

    protected boolean nodeQueries;
    

    /**
     * Constructor for this Iterator.
     *
     * @param query      The query which is used as a base for the querie(s) to be executed.
     * @param batchSize The (approximate) size of the sub-queries, should be a reasonably large
     * number, like 10000 or so.
     */
    public HugeNodeListIterator(Query query, int batchSize) {
        this(query);
        this.batchSize = batchSize;
    }

    /**
     * Constructor for this Iterator. The 'batchSize' is taken from the query's 'maxnumber'
     * properties, or, it that is not set, it is defaulted to 10000.
     *
     * @param query      The query which is used as a base for the querie(s) to be executed.
     */
    public HugeNodeListIterator(Query query) {
        if (query.getOffset() > 0) {
            throw new UnsupportedOperationException("Not implemented for queries with offset");
        }
        Queries.sortUniquely(query);
        if (query.getMaxNumber() != SearchQuery.DEFAULT_MAX_NUMBER) {
            batchSize = query.getMaxNumber();
        }
        originalQuery = query;
        nodeQueries = originalQuery instanceof NodeQuery;
        executeNextQuery((Query) originalQuery.clone());
    }

    
    /**
     * Executed the given query, taking into account the fact wether it is NodeQuery or not, and
     * applying the 'batchSize'. The result is available in the 'nodeIterator' member.
     */
    protected void executeQuery(Query currentQuery) {
        currentQuery.setMaxNumber(batchSize);
        if (nodeQueries) {
            NodeQuery nq = (NodeQuery) currentQuery;
            nodeIterator = nq.getNodeManager().getList(nq).nodeIterator();
            nodeListCache.remove(nq);
        } else {
            nodeIterator = currentQuery.getCloud().getList(currentQuery).nodeIterator();
            multilevelCache.remove(currentQuery);
        }
    }

    /** 
     * Executed the given query, and prepares to do 'next', so setting 'nextNode' and 'previousNode'.
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
     * Executed the given query, and prepares to do 'previous', so setting 'nextNode' and
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
        return previous();
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
     * Used by nextNode and previousNode. Does a field-by-field compare of two Node object to check
     * if they are equal. One would expect the equals-member function of Node to be useable for
     * this, but that seems not to be the case.
     */
    protected boolean equals(Node node1, Node node2) {
        Iterator i = node1.getNodeManager().getFields().iterator();
        while (i.hasNext()) {
            Field f = (Field) i.next();
            String name = f.getName();
            if (! node1.getValue(name).equals(node2.getValue(name))) {
                return false;
            }            
        }
        return true;

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
                SortOrder order = (SortOrder) originalQuery.getSortOrders().get(0);
                Constraint cons;
                if (order.getDirection() == SortOrder.ORDER_ASCENDING) {
                    cons = currentQuery.createConstraint(order.getField(), FieldCompareConstraint.GREATER_EQUAL, previousNode.getValue(order.getField().getFieldName()));
                } else {
                    cons = currentQuery.createConstraint(order.getField(), FieldCompareConstraint.LESS_EQUAL, previousNode.getValue(order.getField().getFieldName()));
                }
                Queries.addConstraint(currentQuery, cons);
                executeNextQuery(currentQuery); 

                // perhaps the sort-order did not find a unique result, skip some nodes in that case.
                // XXX This wrong if there follow more nodes than 'batchSize'.
                while(nextNode != null && equals(nextNode, previousNode)) {
                    if (nodeIterator.hasNext()) {
                        nextNode = nodeIterator.nextNode();
                    } else {
                        nextNode = null;
                    }
                }
            }

            return previousNode; // looks odd, but really is wat is meant.
        } else {
            throw new NoSuchElementException("No such element");
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
                SortOrder order = (SortOrder) originalQuery.getSortOrders().get(0);
                Constraint cons;
                if (order.getDirection() == SortOrder.ORDER_ASCENDING) {
                    cons = currentQuery.createConstraint(order.getField(), FieldCompareConstraint.LESS_EQUAL, nextNode.getValue(order.getField().getFieldName()));
                } else {
                    cons = currentQuery.createConstraint(order.getField(), FieldCompareConstraint.GREATER_EQUAL, nextNode.getValue(order.getField().getFieldName()));
                }
                Queries.addConstraint(currentQuery, cons);
                executePreviousQuery(currentQuery);
                while(previousNode != null && equals(nextNode, previousNode)) {
                    if (nodeIterator.hasPrevious()) {
                        previousNode = nodeIterator.previousNode();
                    } else {
                        previousNode = null;
                    }
                }
                
            }
            return nextNode;
        } else {
            throw new NoSuchElementException("No such element");
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



}
