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
 * @version $Id: TreeList.java,v 1.3 2003-12-18 20:24:16 michiel Exp $
 * @since   MMBase-1.7
 */

public  class TreeList extends AbstractSequentialBridgeList implements NodeList {
    private static final Logger log = Logging.getLoggerInstance(TreeList.class);

    public static final String REAL_NODES = "realnodes";

    protected Cloud    cloud;

    protected final List queries = new ArrayList();
    protected final List results = new ArrayList();

    protected int topQuery = 0;
    protected int numberOfSteps;
    private   int size;

    protected boolean foundEnd = false;


    /**
     * @param q              The 'base' query defining the minimal depth of the tree elements
     */

    public TreeList(NodeQuery q) {

        if (q.getOffset() > 0) {
            throw new UnsupportedOperationException("Don't know how to implement that");
        }
        cloud = q.getCloud();

        queries.add(q);
        results.add(null);  // determin when needed

        fixSortOrders(q);

        size = Queries.count(q);
        numberOfSteps = q.getSteps().size();

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
        return size;
    }


    /**
     * Grows branches of the Tree, which means that one new query will be created which is one
     * relationStep longer than the longest one until now.
     * This new relationStep is returned, which can be used to create new constraints.
     *
     * @return null if not relationstep is added because that would not increase the number of results.
     */
    
    public RelationStep grow(NodeManager nodeManager, String role, String searchDir) {

        if (foundEnd) return null;

        NodeQuery lastQuery = (NodeQuery) queries.get(topQuery);
        NodeQuery newQuery  = (NodeQuery) lastQuery.cloneWithoutFields();        

        // add relations step
        RelationStep step = newQuery.addRelationStep(nodeManager, role, searchDir);
        Step nextStep = step.getNext();

        // make sure it is uniquely sorted (this should happen later, I think)
        {
            StepField sf1 = newQuery.createStepField(step, "number");        
            newQuery.addSortOrder(sf1, SortOrder.ORDER_DESCENDING); 
            StepField sf2 = newQuery.createStepField(nextStep, "number");        
            newQuery.addSortOrder(sf2, SortOrder.ORDER_DESCENDING); 
        }

        // make sure every step has a unique alias
        newQuery.setAlias(step, step.getTableName() + (numberOfSteps - 1));
        newQuery.setAlias(nextStep, nodeManager.getName() + numberOfSteps);

        // new number of steps
        numberOfSteps = newQuery.getSteps().size();

       // the new step must be the 'node' step
        newQuery.setNodeStep(nextStep);

         // from all other we need the 'number' fields
        Iterator i = newQuery.getSteps().iterator();
        while (i.hasNext()) {
            Step s = (Step) i.next();
            if (!i.hasNext()) break;   // skip the last step (already added as node-step)
            newQuery.addField(s, cloud.getNodeManager(s.getTableName()).getField("number"));
            if (i.hasNext()) i.next(); // skip relation steps;
        }


        queries.add(newQuery);
        results.add(null); // determin when needed
        topQuery++;

        // add the fields..
        int count = Queries.count(newQuery);
        if (count == 0) {
            foundEnd = true;
        }
        size += count;
        return step;
        
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
        return new TreeItr(ind);
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
    protected class TreeItr implements TreeIterator {

        private List nodeIterators     = new ArrayList(); // an iterator for each query result
        private NodeList nextNodes     = TreeList.this.cloud.getCloudContext().createNodeList();
                                    // contains 'next' nodes for each query result (needed for 'next()')
        private NodeList previousNodes = TreeList.this.cloud.getCloudContext().createNodeList();
                                     // contains 'previous' nodes for eacht query result (needed for 'previous()')
        private int  currentIterator;                 // number of current iterator which is iterated
        private int  nextIndex;


        TreeItr(int i) {
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
         * Makes sure that query with given index has an iterator, a 'next' node and a 'previous' node.
         * @return true it such  query existed, false, otherwise
         */
        protected final boolean prepare(int index) {

            for(int i = nodeIterators.size(); i <= index; i++) {
                NodeList nl = (NodeList) TreeList.this.getList(i);
                NodeIterator iterator = null;
                if (nl != null) { 
                    iterator = nl.nodeIterator();
                }
                nodeIterators.add(iterator);
                previousNodes.add(null);
                if (iterator == null) {
                    nextNodes.add(null);
                    return false;
                } else {                    
                    if (iterator.hasNext()) {
                        nextNodes.add(iterator.nextNode());
                    } else {
                        nextNodes.add(null);
                    }
                }
            }
            return true;
        }


        /**
         * Uses the new 'next' node of the iterator with the given index.
         * This means that it become the previous node and that a new 'next' node will be determined
         */
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
                Node node = getRealNode(currentIterator);
                useNext(currentIterator);
                return node;
            }

            prepare(currentIterator + 1); 
            Node nextListNextNode = nextNodes.getNode(currentIterator + 1);

            if (nextListNextNode == null) {
                if (currentIterator > 0) {
                    currentIterator--;
                    return getNextNode();
                } else {
                    Node node = getRealNode(0);
                    useNext(0);
                    return node;
                }
            }

            List steps = ((Query) TreeList.this.queries.get(currentIterator)).getSteps();

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
                currentIterator++;
                Node node = getRealNode(currentIterator);
                useNext(currentIterator);
                return node;
            } else {
                if (currentIterator > 0) {
                    currentIterator--;
                    return getNextNode();
                } else {                    
                    Node node = getRealNode(0);
                    useNext(0);
                    return node;
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
    

    /**
     * For testing only. Based on RMMCI, but search-query stuff does not work (yet) through RMMCI.
     */

    protected static NodeQuery getQuery(String[] args) {
        String startNodes = "";
        if (args.length > 0) {
            startNodes = args[0];
        } 


        String port = "1111";
        if (args.length > 1) {
            port = args[1];
        } 

        String bindName = "remotecontext";
        if (args.length > 2) {
            bindName = args[2];
        } 
       
        Cloud cloud =  ContextProvider.getCloudContext("rmi://127.0.0.1:" + port + "/" + bindName).getCloud("mmbase");

        NodeManager object = cloud.getNodeManager("object");

        NodeQuery q = object.createQuery();
        Queries.addStartNodes(q, startNodes);
        return q;
    }

    public static void  main(String[] args) {

        NodeQuery q = getQuery(args);
        Cloud    cloud = q.getCloud();

        NodeManager object = cloud.getNodeManager("object");

        TreeList tree = new TreeList(q);

        tree.grow(object, null, "destination");
        tree.grow(object, null, "destination");

        Iterator i = tree.iterator();
        while (i.hasNext()) {
            Node n = (Node) i.next();
            System.out.println(n.toString());
        }

        
        
    }


}
