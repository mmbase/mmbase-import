/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.util;

import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;
//import org.mmbase.util.logging.*;

import java.util.*;

/**
 * Queries a Tree from MMBase. A Tree is presented as a List of MultiLevel results. (ClusterNodes).
 *
 *
 * @author  Michiel Meeuwissen
 * @version $Id: TreeList.java,v 1.1 2003-12-03 20:27:10 michiel Exp $
 * @since   MMBase-1.7?
 */

public  class TreeList extends AbstractSequentialList implements List {
    //    private static final Logger log = Logging.getLoggerInstance(TreeList.class);

    protected Cloud    cloud;
    protected PathElement[]  steps;
    protected int maxDepth;

    protected final List queries = new ArrayList();
    protected final List results = new ArrayList();
    protected boolean foundEnd = false;
    protected int topQuery = 0;
    protected int depth;
    protected int size;


    /**
     * 
     */

    public TreeList(Query q, PathElement[] steps, int maxDepth) {
        cloud = q.getCloud();
        queries.add(q);
        results.add(null);  // determin when needed
        size = Queries.count(q);
        this.steps  = steps;
        this.maxDepth = maxDepth;
        depth = q.getSteps().size();
        if (maxDepth < depth) {
            throw new IllegalArgumentException("Query is already deeper than maxdepth");
        }
    }


    // javadoc inherited
    public int size() {
        // need all queries for that
        while (! foundEnd) {
            addPathElement(); // this will increase size too.
        }
        return size;
    }

    /**
     * Generates a new query (and does a count on it)
     */
    protected void addPathElement() {
        if (depth + (steps.length * 2) > maxDepth) {
            foundEnd = true;
        } else {
            Query newQuery = ((Query) queries.get(topQuery)).cloneWithoutFields();
            Iterator i = newQuery.getSteps().iterator();
            // need all 'number' fields
            while (i.hasNext()) {
                Step step = (Step) i.next();
                newQuery.addField(step, cloud.getNodeManager(step.getTableName()).getField("number"));
            }

            for (int j = 0; j < steps.length; j++) {
                PathElement pathElement = steps[j];
                newQuery.addRelationStep(pathElement.nodeManager, pathElement.role, pathElement.searchDir);
            }
            depth = newQuery.getSteps().size();
            queries.add(newQuery);
            results.add(null); // determin when needed
            topQuery++;
            // add the fields..
            int count = Queries.count(newQuery);
            if (count == 0) {
                foundEnd = true;
                return;
            }
            size += count;
        }
    }

    /**
     * Executes one query if that did not happen yet, and stores the result in the 'results' List
     * @return NodeList
     * @throws IndexOutOfBoundsException if there is no such query
     */
    protected NodeList getList(int queryNumber) {
        if (queryNumber < 0) {
            throw new IndexOutOfBoundsException("No query for '" + queryNumber + "'");
        }
        while (queryNumber > queries.size() && (!foundEnd)) {
            addPathElement();
        }
        if (queryNumber > queries.size()) {
            throw new IndexOutOfBoundsException("No query for '" + queryNumber + "'. Size: " + queries.size());
        }

        NodeList nodeList = (NodeList) results.get(queryNumber);
        if (nodeList == null) {
            Query query = (Query) queries.get(queryNumber);
            nodeList = cloud.getList(query);
            results.set(queryNumber, nodeList);
        }
        return nodeList;
    }

    // javadoc inherited
    public ListIterator listIterator(int ind) {
        return  new TreeIterator(ind);
    }

    /**
     * The TreeIterator!
     */
    protected class TreeIterator implements ListIterator {

        private List nodeIterators = new ArrayList();
        private List topNodes      = new ArrayList();
        private List bottomNodes   = new ArrayList();
        private int current;
        private int nextIndex;


        TreeIterator(int i) {
	    if (i < 0 || (i > 0 && i > TreeList.this.size())) {
		throw new IndexOutOfBoundsException("Index: " + i +  ", Size: " + TreeList.this.size());
            }
            NodeIterator iterator = TreeList.this.getList(0).nodeIterator();
            nodeIterators.add(iterator);
            topNodes.add(iterator.next());
            bottomNodes.add(null);
            current = 0;
            nextIndex = 0;
            while(nextIndex < i) next();

        }
        public boolean hasNext() {
            return nextIndex < TreeList.this.size();
        }
        
        public Object next() {
            NodeIterator nli = (NodeIterator) (nodeIterators.get(current));
            // TODO!
            nextIndex++;
            return nli.next();
        }
        
        public boolean hasPrevious() {
            return nextIndex > 0;
        }
        
        public Object previous() {
            nextIndex--;
            throw new UnsupportedOperationException("unfinished");
        }
        public int nextIndex() {
            return nextIndex;
        }


        public int previousIndex() {
            return nextIndex - 1;
        }

        public void remove() {
            throw new UnsupportedOperationException("TreeList is not modifiable");
        }
        
        public void set(Object o) {
            throw new UnsupportedOperationException("TreeList is not modifiable");
        }
        
        public void add(Object o) {
            throw new UnsupportedOperationException("TreeList is not modifiable");
        }
        
    }
    



    
    /*
    public TreeElement {
        Node node;
        int level;
        TreeElement(Node n, int l) {
            node = n;
            level = l;
        }
        public Node getNode() { return node; }
        public int  getLevel() { return level; }
    }
    */


    public static class PathElement {
        NodeManager nodeManager;
        String      role;
        String      searchDir;
        String      fields;

        public PathElement(NodeManager nm, String r, String sd) {
            nodeManager = nm;
            role = r;
            searchDir = sd;
        }
    }

}
