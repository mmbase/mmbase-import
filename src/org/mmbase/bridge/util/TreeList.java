/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.util;

import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;
import org.mmbase.util.logging.*;

import java.util.*;

/**
 * Queries a Tree from MMBase. A Tree is presented as a List of MultiLevel results (ClusterNodes),
 * combined with a smart iterator which iterates through the elements these lists as if it was one
 * list ordered as a Tree.
 *
 *
 * @author  Michiel Meeuwissen
 * @version $Id: TreeList.java,v 1.1 2003-12-17 20:55:23 michiel Exp $
 * @since   MMBase-1.7
 */

public  class TreeList extends AbstractSequentialBridgeList implements NodeList {
    private static final Logger log = Logging.getLoggerInstance(TreeList.class);

    public static final String REAL_NODES = "realnodes";

    protected Cloud    cloud;
    protected PathElement[]  pathElement;
    protected int maxNumberOfSteps;

    protected final List queries = new ArrayList();
    protected final List results = new ArrayList();
    protected boolean foundEnd = false;
    protected int topQuery = 0;
    protected int numberOfSteps;
    private   int size;


    /**
     * @param q              The 'base' query defining the minimal depth of the tree elements
     * @param pathElement    The pathElement structure defines one 'relationStep', an array of them
     *                       defines by which the branches will grow everytime
     * @param maxDepth       You must supply a maximal depth of the nodes, because MMBase is basicly a network rather then a tree, so
     *                        tree representations could be infinitely deep.
     */

    public TreeList(NodeQuery q, PathElement[] pathElement, int maxDepth) {
        log.debug("treelist");
        if (q.getOffset() > 0) {
            throw new UnsupportedOperationException("Don't know how to implement that");
        }
        cloud = q.getCloud();
        queries.add(q);
        results.add(null);  // determin when needed

        fixSortOrders(q);

        size = Queries.count(q);
        this.pathElement = pathElement;
        maxNumberOfSteps = 2 * maxDepth - 1; // dont consider relation steps.

        numberOfSteps = q.getSteps().size();

        if (maxNumberOfSteps < numberOfSteps) {
            throw new IllegalArgumentException("Query is already deeper than maxdepth");
        }
    }


    /**
     * Used in constructor to check the given query, every step must be sorted on number (at least)
     */
    protected void fixSortOrders(Query q) {
        List steps = new ArrayList(q.getSteps());
        Iterator i = q.getSortOrders().iterator();
        while (i.hasNext()) {
            SortOrder sortOrder = (SortOrder) i.next();
            if (sortOrder.getField().getFieldName().equals("number")) {
                Step step = sortOrder.getField().getStep();
                steps.remove(step);
            }
        }
        // add sort order on the remaining ones:
        i = steps.iterator();
        while (i.hasNext()) {
            Step step = (Step) i.next();
            q.addSortOrder(q.createStepField(step, "number"), SortOrder.ORDER_DESCENDING);
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
        if (numberOfSteps + (pathElement.length * 2) > maxNumberOfSteps) {
            foundEnd = true;
        } else {
            NodeQuery newQuery = (NodeQuery) ((NodeQuery) queries.get(topQuery)).cloneWithoutFields();
            Iterator i = newQuery.getSteps().iterator();
            // need all 'number' fields
            while (i.hasNext()) {
                Step step = (Step) i.next();
                newQuery.addField(step, cloud.getNodeManager(step.getTableName()).getField("number"));
                if (i.hasNext()) i.next(); // skip relation steps;
            }

            Step nextStep = null;
            for (int j = 0; j < pathElement.length; j++) {
                PathElement pathElementElement = pathElement[j];
                RelationStep step = newQuery.addRelationStep(pathElementElement.nodeManager, pathElementElement.role, pathElementElement.searchDir);
                newQuery.setAlias(step, step.getTableName() + (numberOfSteps - 1));
                nextStep = step.getNext();
                String tn = nextStep.getTableName();                                 
                newQuery.setAlias(nextStep, tn + (numberOfSteps));
                NodeManager nm = cloud.getNodeManager(tn);

                newQuery.addField(nextStep, nm.getField("otype"));
                StepField sf = newQuery.createStepField(nextStep, "number");

                newQuery.addSortOrder(sf, SortOrder.ORDER_DESCENDING); // order must be well defined!
            }



            numberOfSteps = newQuery.getSteps().size();
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
     * @return NodeList or null if queryNumber too big
     * @throws IndexOutOfBoundsException if queryNumber < 0
     */
    protected NodeList getList(int queryNumber) {
        if (queryNumber < 0) {
            throw new IndexOutOfBoundsException("No query for '" + queryNumber + "'");
        }
        while (queryNumber >= queries.size() && (!foundEnd)) {
            addPathElement();
        }
        if (queryNumber >= queries.size()) {
            return null;
        }

        NodeList nodeList = (NodeList) results.get(queryNumber);
        if (nodeList == null) {
            NodeQuery query = (NodeQuery) queries.get(queryNumber);
            nodeList = cloud.getList(query);
            results.set(queryNumber, nodeList);
        }
        return nodeList;
    }

    // javadoc inherited
    public ListIterator listIterator(int ind) {
        return  new TreeIterator(ind);
    }

    public NodeIterator nodeIterator() {
        return (NodeIterator) listIterator(0);
    }

    public TreeIterator treeIterator() {
        return (TreeIterator) listIterator(0);
    }

    // javadoc inherited
    public Node getNode(int i) {
        return (Node) get(i);
    }


    /**
     * Returns node 'index' of query result 'queryIndex' as a 'real' node (so not a cluster node)
     */
    protected Node getRealNode(int queryIndex, int index) {
        NodeList nodeList  = (NodeList) results.get(queryIndex);
        NodeList realNodes = (NodeList) nodeList.getProperty(REAL_NODES);
        if (realNodes == null) {
            NodeQuery nq = (NodeQuery) queries.get(queryIndex);
            realNodes = nq.getNodeManager().getList(nq); // We trust the query cache! (the query is performed already, but on Cloud)
            nodeList.setProperty(REAL_NODES, realNodes);
        }
        return realNodes.getNode(index);
    }

    
    public NodeList subNodeList(int start, int end) {
        throw new UnsupportedOperationException("SubNodeLists not implemented for TreeList");
    }


    public String toString() {
        int size = size();
        return "size: " + size + " " + queries.toString();
    }

    /**
     * The TreeIterator!
     */
    public class TreeIterator implements NodeIterator {

        private List nodeIterators     = new ArrayList(); // an iterator for each query result
        private NodeList nextNodes     = TreeList.this.cloud.getCloudContext().createNodeList();
                                    // contains 'next' nodes for each query result (needed for 'next()')
        private NodeList previousNodes = TreeList.this.cloud.getCloudContext().createNodeList();
                                     // contains 'previous' nodes for eacht query result (needed for 'previous()')
        private int  currentIterator;                 // number of current iterator which is iterated
        private int  nextIndex;


        TreeIterator(int i) {
	    if (i < 0 || (i > 0 && i > TreeList.this.size())) {
		throw new IndexOutOfBoundsException("Index: " + i +  ", Size: " + TreeList.this.size());
            }
            currentIterator = 0;
            nextIndex = 0;
            while(nextIndex < i) next(); // fast forward to request start index.

        }

        public boolean hasNext() {
            return nextIndex < TreeList.this.size();
        }



        /**
         * 
         */
        protected final boolean prepare(int index) {
            while (nodeIterators.size() <= index) {                
                NodeList nl = (NodeList) TreeList.this.getList(index);
                NodeIterator iterator = null;
                if (nl != null) iterator = nl.nodeIterator();
                nodeIterators.add(iterator);
                previousNodes.add(null);
                boolean complete = (iterator == null);
                if ((!complete)  && iterator.hasNext()) {
                    nextNodes.add(iterator.nextNode());
                } else {
                    nextNodes.add(null);
                }
                if (complete) { 
                    return false;
                }
                
            }
            return true;
        }

        protected final void useNext(int index) {
            Node node = nextNodes.getNode(index);
            previousNodes.set(index, node);
            NodeIterator iterator = (NodeIterator) nodeIterators.get(index);
            if (iterator.hasNext()) {
                nextNodes.set(index, iterator.nextNode());
            } else {
                nextNodes.set(index, null);
            }            
        }

        protected final Node getRealNode(int index) {
            ListIterator iterator = (ListIterator) nodeIterators.get(index);
            return TreeList.this.getRealNode(index, iterator.previousIndex());
        }

        public Node nextNode() {
            nextIndex++;
            return getNextNode();
        }

        /**
         * Depth of the last node fetched with next() or nextNode()
         */
        public int currentDepth() {
            return (((Query) TreeList.this.queries.get(currentIterator)).getSteps().size() + 1) / 2;            
        }

        public Object next() {
            return nextNode();
        }


        /**
         *
         * Implementation idea graphicly.
         <pre>
                        iterators
                            
              
              current-2  current-1  current       current+1                         [///]: used node
               [///]       [///]     [///]         [///]                            [|||]: last used node (lastNode)
                                                                                    [   ]: unused node               
         ...   [///]       [///]     [|||] _       [///]    previousNodes           [ * ]: considerd next node (nextListNextNode)
                                            \   
               [   ]       [   ]     [   ]   `---> [ * ]    nextNodes
         
               if (! [|||] contained by [ * ]) current--
         </pre>
         
         Everytime next is called, the last used node is compare with the next node of the
         next iterator (the arrow int the above scheme). If the last used node is 'contained' by
         this next node, then this next node of the next iterator will be 'next()' otherwise current
         is decreased by one and next is called recursively. This means that the next node is always
         one longer then the current one, equally long, or shorter.
        */        
        protected final Node getNextNode() {
            prepare(currentIterator);
            Node lastNode = previousNodes.getNode(currentIterator);
            if (lastNode == null) { // first!
                useNext(currentIterator);
                return getRealNode(currentIterator);
            }

            prepare(currentIterator + 1); 
            Node nextListNextNode = nextNodes.getNode(currentIterator + 1);

            if (nextListNextNode == null) {
                if (currentIterator > 0) {
                    currentIterator--;
                    return getNextNode();
                } else {
                    useNext(0);
                    return getRealNode(0);
                }
            }

            List steps = ((Query) TreeList.this.queries.get(currentIterator)).getSteps();
            boolean prefixAlias = (steps.size() > 1);
            boolean contains = true;
            Iterator i = steps.iterator();
            while (i.hasNext()) {
                Step step = (Step) i.next();
                String alias = step.getAlias(); 
                if (alias == null) alias = step.getTableName();
                String numberField = alias + ".number";
                if (lastNode.getIntValue(numberField) != nextListNextNode.getIntValue(numberField)) {
                    contains = false;
                    break;
                }
                if (i.hasNext()) i.next(); // skip relation step
            }

            if (contains) {
                useNext(++currentIterator);
                return getRealNode(currentIterator);
            } else {
                if (currentIterator > 0) {
                    currentIterator--;
                    return getNextNode();
                } else {
                    useNext(0);
                    return getRealNode(0);
                }
            }
        }
        
        public boolean hasPrevious() {
            return nextIndex > 0;
        }
        
        public Node previousNode() {
            nextIndex--;
            throw new UnsupportedOperationException("unfinished");
        }
        public Object previous() {
            return previousNode();
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
    

    public static class PathElement {
        NodeManager nodeManager;
        String      role;
        String      searchDir;
        String      fields;
        String      sortOrders;

        public PathElement(NodeManager nm, String r, String sd) {
            nodeManager = nm;
            role = r;
            searchDir = sd;
        }
    }

    /**
     * For testing only. Based on RMMCI, but search-query stuff does not work (yet) through RMMCI.
     */
    public static final void  main(String[] args) {

        String bindName;
        if (args.length > 1) {
            bindName = args[1];
        } else {
            bindName = "remotecontext";
        }
        String port;
        if (args.length > 0) {
            port = args[0];
        } else {
            port = "1111";
        }
       
        Cloud cloud =  ContextProvider.getCloudContext("rmi://127.0.0.1:" + port + "/" + bindName).getCloud("mmbase");
        NodeManager object = cloud.getNodeManager("object");

        NodeQuery q = object.createQuery();
        TreeList tree = new TreeList(q, new TreeList.PathElement [] {new TreeList.PathElement(object, null, "destination")}, 5);

        Iterator i = tree.iterator();
        while (i.hasNext()) {
            Node n = (Node) i.next();
            System.out.println(n.toString());
        }

        
        
    }

}
