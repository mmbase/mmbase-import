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
 * combined with a smart iterator which iterates through the elements of these lists as if it was one
 * list ordered as a Tree.
 *
 *
 * @author  Michiel Meeuwissen
 * @version $Id: TreeList.java,v 1.19 2006-02-10 18:00:47 michiel Exp $
 * @since   MMBase-1.7
 */

public class TreeList extends AbstractSequentialBridgeList implements NodeList {
    private static final Logger log = Logging.getLoggerInstance(TreeList.class);

    public static final String REAL_NODES = "realnodes";

    protected Cloud cloud;
    protected final List /*<Branch>*/ branches        = new ArrayList();

    protected int topQuery = 0;
    protected int numberOfSteps;
    private   int size;
    private boolean needsSizeCheck = false;

    protected boolean foundEnd = false;
    protected int     leafConstraintOffset = Integer.MAX_VALUE;

    /**
     * @param q              The 'base' query defining the minimal depth of the tree elements. The trunk of the tree.
     */

    public TreeList(NodeQuery q) {

        if (q.getOffset() > 0) {
            throw new UnsupportedOperationException("Don't know how to implement that");
        }
        cloud = q.getCloud();
        branches.add(new Branch(q));
        size = Queries.count(q);
        numberOfSteps = q.getSteps().size();

    }

    // javadoc inherited
    public int size() {
        sizeCheck();
        return size;
    }


    /**
     * Checks if the size of the List needs to be (re)determined, and if not, does so. After growing
     * a List the size needs recalculation.
     * @since MMBase-1.7.1
     */
    protected void sizeCheck() {
        if (needsSizeCheck) {
            int count;
            Branch branch = (Branch) branches.get(topQuery);
            if (branch.leafResult != null) {  // not quit sure that this can hapen
                count = branch.leafResult.size();
            } else {
                NodeQuery newQuery = branch.getLeafQuery();
                count = Queries.count(newQuery);
            }

            if (count == 0) {
                foundEnd = branch.leafConstraint == null || Queries.count(branch.getQuery()) == 0;
            }
            size += count;
            needsSizeCheck = false;
        }
    }

    /**
     * Grows branches of the Tree, which means that one new query will be created which is one
     * relationStep longer than the longest one until now.
     * This new relationStep is returned, which can be used to create new constraints.
     *
     * @return <code>null</code> if no relationstep is added because that would not increase the number of results.
     */

    public RelationStep grow(NodeManager nodeManager, String role, String searchDir) {

        sizeCheck();
        if (foundEnd) {
            return null;
        }
        needsSizeCheck = true;

        NodeQuery lastQuery = ((Branch)branches.get(topQuery)).getQuery();
        NodeQuery newQuery  = (NodeQuery)lastQuery.cloneWithoutFields();

        // add relations step
        RelationStep step = newQuery.addRelationStep(nodeManager, role, searchDir);
        Step nextStep = step.getNext();

        // make sure every step has a unique alias
        newQuery.setAlias(step, step.getTableName() + (numberOfSteps - 1));
        newQuery.setAlias(nextStep, nodeManager.getName() + numberOfSteps);

        // new number of steps
        numberOfSteps = newQuery.getSteps().size();

        // the new step must be the 'node' step
        newQuery.setNodeStep(nextStep);

        branches.add(new Branch(newQuery));
        topQuery++;

        return step;
    }

    /**
     * Returns the top most query, associated with the last call to {@link #grow}.
     * @since MMBase-1.8
     */
    public NodeQuery getLeafQuery() {
        return ((Branch) branches.get(topQuery)).getQuery();
    }

    /**
     * Sets a 'leaf constraint' on the last 'growed' step. A leaf constraint is a constraint which is only
     * used on leafs, so if the tree is grown further, the leaf constraint will not be passed to the branches.
     * @since MMBase-1.8
     */
    public void setLeafConstraint(Constraint constraint) {

        Branch branch = (Branch) branches.get(topQuery);
        if (branch.result != null) {
            throw new IllegalStateException("The query for branch " + topQuery + " was already executed");
        }
        if (topQuery < leafConstraintOffset) {
            leafConstraintOffset = topQuery;
        }
        leafConstraintOffset = 0;
        branch.leafConstraint = constraint;

    }

    /**
     * Executes one query if that did not happen yet, and stores the result in the 'results' List
     * @return NodeList or <code>null</code> if queryNumber too big
     * @throws IndexOutOfBoundsException if queryNumber < 0
     */
    protected NodeList getList(int queryNumber) {
        if (queryNumber < 0) {
            throw new IndexOutOfBoundsException("No query for '" + queryNumber + "'");
        }

        if (queryNumber >= branches.size()) {
            return null;
        }

        Branch branch = (Branch) branches.get(queryNumber);
        if (branch.result == null) {
            NodeQuery query = branch.getQuery();
            branch.result =  cloud.getList(query);
            if (branch.leafConstraint == null) {
                branch.leafResult = branch.result;
            }
        }
        return branch.result;
    }
    /**
     * Executes one query as a 'leaf' query.
     * @since MMBase-1.8
     */
    protected NodeList getLeafList(int queryNumber) {
        if (queryNumber < 0) {
            throw new IndexOutOfBoundsException("No query for '" + queryNumber + "'");
        }

        if (queryNumber >= branches.size()) {
            return null;
        }

        Branch branch = (Branch) branches.get(queryNumber);
        if (branch.leafResult == null) {
            NodeQuery query = branch.getLeafQuery();
            branch.leafResult =  cloud.getList(query);
            if (branch.leafConstraint == null) {
                branch.result = branch.leafResult;
            }
        }
        return branch.leafResult;
    }

    // javadoc inherited
    public ListIterator listIterator(int ind) {
        return treeIterator(ind);
    }

    public NodeIterator nodeIterator() {
        return treeIterator(0);
    }

    public TreeIterator treeIterator() {
        return treeIterator(0);
    }

    protected TreeIterator treeIterator(int ind) {
        return new TreeItr(ind);
    }

    // javadoc inherited
    public Node getNode(int i) {
        return (Node)get(i);
    }

    /**
     * Returns node 'index' of query result 'queryIndex' as a 'real' node (so not a cluster node)
     */
    protected Node getRealNode(int queryIndex, int index) {
        NodeList nodeList  = getLeafList(queryIndex);
        NodeList realNodes = (NodeList)nodeList.getProperty(REAL_NODES);
        if (realNodes == null) {
            Branch branch = (Branch) branches.get(queryIndex);
            NodeQuery nq = branch.getLeafQuery();
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
        return "size: " + size + " " + branches.toString();
    }


    /**
     * Structure to hold the information for every branch-depth.
     * @since MMBase-1.8
     */
    protected class Branch {
        final private NodeQuery query;
        NodeList result = null;
        NodeList leafResult = null;
        Constraint leafConstraint = null;
        private NodeQuery leafQuery = null;
        Branch(NodeQuery q) {
            query = q;
        }
        NodeQuery getQuery() {
            return query;
        }
        /*
        NodeQuery getLeafQuery() {
            Queries.sortUniquely(query);
            Queries.addSortedFields(query);
            query.markUsed();
            return query;
        }
        */
        NodeQuery getLeafQuery() {
            if (leafQuery != null) return leafQuery;
            Queries.sortUniquely(query);
            Queries.addSortedFields(query);
            query.markUsed();
            if (leafConstraint != null) {
                leafQuery = (NodeQuery) query.clone();
                Queries.addConstraint(leafQuery, leafConstraint);
                leafQuery.markUsed();
            } else {
                leafQuery = query;
            }
            return leafQuery;
        }

        public String toString() {
            return query.toString()  + (leafConstraint != null ? "[" + leafConstraint + "]" : "");
        }
    }

    /**
     * The TreeIterator contains the core-functionality of TreeList.
     */
    protected class TreeItr implements TreeIterator {

        private List nodeIterators = new ArrayList(); // an iterator for each query result
        private NodeList nextNodes = TreeList.this.cloud.createNodeList();
        // contains 'next' nodes for each query result (needed for 'next()')

        private NodeList previousNodes = TreeList.this.cloud.createNodeList();
        // contains 'previous' nodes for each query result (needed for 'previous()')

        private int currentIterator; // number of current iterator which is iterated
        private int nextIndex;       // the next index number, so this is 0 on the beginning, and <size> just before the last next()

        private boolean encounteredLeafConstraint = false;

        TreeItr(int i) {
            if (i < 0 || (i > 0 && i > TreeList.this.size())) {
                throw new IndexOutOfBoundsException("Index: " + i + ", Size: " + TreeList.this.size());
            }
            currentIterator = 0;
            nextIndex = 0;
            while (nextIndex < i) {
                next(); // fast forward to requested start index.
            }

        }

        public boolean hasNext() {
            if (TreeList.this.foundEnd) { // why bother
                return nextIndex < TreeList.this.size();
            } else {
                int i = 0;
                while (prepare(i)) {
                    NodeIterator iterator = (NodeIterator)nodeIterators.get(i);
                    Node nextNode = (Node) nextNodes.get(i);
                    if (nextNode != null) {
                        return true;
                    } else {
                        i++;
                    }
                }
                return false;
            }
        }

        /**
         * Makes sure that query with given index has an iterator, a 'next' node and a 'previous' node.
         * @return true it such  query existed, false, otherwise
         */
        protected final boolean prepare(int index) {
            for (int i = nodeIterators.size(); i <= index; i++) {
                NodeList nl = TreeList.this.getLeafList(i);
                if (TreeList.this.leafConstraintOffset <= i) {
                    encounteredLeafConstraint = true;
                }
                NodeIterator iterator = null;
                if (nl != null) {
                    iterator = nl.nodeIterator();
                }
                nodeIterators.add(iterator);
                previousNodes.add(null);     // just prepared iterator never has a previous node already
                if (iterator == null) {
                    nextNodes.add(null);
                    return false;
                } else {
                    if (iterator.hasNext()) {
                        nextNodes.add(iterator.nextNode());
                    } else {
                        nextNodes.add(null);
                        return false;
                    }
                }
            }
            return true;
        }

        /**
         * Uses the new 'next' node of the iterator with the given index.
         * This means that it becomes the previous node and that a new 'next' node will be determined
         */
        protected final void useNext(int index) {
            Node node = nextNodes.getNode(index);
            if (node == null) throw new NoSuchElementException("No such element " + index + " in " + nextNodes);
            previousNodes.set(index, node);
            NodeIterator iterator = (NodeIterator)nodeIterators.get(index);
            if (iterator.hasNext()) {
                Node nextNode = iterator.nextNode();
                nextNodes.set(index, nextNode);
            } else {
                nextNodes.set(index, null);
            }
        }

        /**
         * Returns the 'real' node, us the just used 'next' node of index.
         */
        protected final Node getRealNode(int index) {
            ListIterator iterator = (ListIterator)nodeIterators.get(index);
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
            Branch branch = (Branch) TreeList.this.branches.get(currentIterator);
            int depth = (branch.query.getSteps().size() + 1) / 2;
            if (nextIndex == 0) {
                return depth - 1;
            } else {
                return depth;
            }
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
         ...   [///]       [///]     [|||] _       [///]    previousNodes           [ * ]: considered next node (nextListNextNode)
                                            \
               [   ]       [   ]     [   ]   `---> [ * ]    nextNodes

               if (! [|||] contained by [ * ]) current--
         </pre>

         Every time next is called, the last used node is compared with the next node of the
         next iterator (the arrow in the above scheme). If the last used node is 'contained' by
         this next node, then this next node of the next iterator will be 'next()' otherwise current
         is decreased by one and next is called recursively. This means that the next node is always
         one longer than the current one, equally long, or shorter.

         If 'leaf constraints' are in use, then the implementation jumps to getNextLeafNode, which simply returns the 'smallest node' of all iterators.
        */
        protected final Node getNextNode() {
            prepare(currentIterator);
            if (encounteredLeafConstraint) {
                encounteredLeafConstraint = true;
                return getNextLeafNode();
            }

            final Branch currentBranch = (Branch) TreeList.this.branches.get(currentIterator);

            Node previousNode = previousNodes.getNode(currentIterator);
            if (previousNode == null) {  // first of iterator
                Node node = getRealNode(currentIterator);
                useNext(currentIterator);
                return node;
            }

            Node nextListNextNode =  prepare(currentIterator + 1) ? nextNodes.getNode(currentIterator + 1) : null;

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

            List sortOrders = currentBranch.getQuery().getSortOrders();
            final boolean contains = Queries.compare(previousNode, nextListNextNode, sortOrders) >= 0;

            if (log.isDebugEnabled()) {
                log.debug("comparing " + previousNode + " with " + nextListNextNode);
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

        /**
         * Simply returns the 'smallest' of all availabe nodes.
         * @since MMBase-1.8
         */
        protected final Node getNextLeafNode() {
            Node smallestAvailableNode = null;
            List smallestSortOrders = null;  // Sort-Orders list of smallest availabe node.
            int i = -1;
            while(prepare(++i)) {
                Node candidate = i < nextNodes.size() ? nextNodes.getNode(i) : null;
                if (candidate == null) continue;

                Branch branch = (Branch) TreeList.this.branches.get(i);
                List sortOrders = branch.getLeafQuery().getSortOrders();
                if (smallestAvailableNode == null) {
                    smallestAvailableNode = candidate;
                    smallestSortOrders    = sortOrders;
                    currentIterator       = i;
                } else {
                    int compare = Queries.compare(candidate, smallestAvailableNode, sortOrders.size() > smallestSortOrders.size() ? sortOrders : smallestSortOrders);
                    if (compare < 0) {
                        smallestAvailableNode = candidate;
                        smallestSortOrders   = sortOrders;
                        currentIterator      = i;
                    }
                }
            }
            if (smallestAvailableNode == null) {
                throw new NoSuchElementException();
            }
            Node node = getRealNode(currentIterator);
            useNext(currentIterator);
            return node;
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
     * For testing only. Based on RMMCI,
     * please use the System property to specify de cloud context
     * -Dmmbase.defaultcloudcontext=rmi://localhost:1111/remotecontext
     * @param args the start node (in one argument)
     */

    protected static NodeQuery getQuery(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: java -Dmmbase.defaultcloudcontext=rmi://localhost:1111/remotecontext " + TreeList.class.getName() + " <startnode>");
            System.exit(1);
        }

        String startNodes = args[0];
        Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase");

        NodeManager object = cloud.getNodeManager("object");

        NodeQuery q = cloud.createNodeQuery();
        Step step = q.addStep(object);
        q.setNodeStep(step);
        RelationStep relationStep = q.addRelationStep(object, "index", "destination");
        q.setNodeStep(relationStep.getNext());
        StepField pos = q.createStepField(relationStep, "pos");
        q.addSortOrder(pos, SortOrder.ORDER_ASCENDING);

        object.getList(q);

        Queries.addStartNodes(q, startNodes);
        return q;
    }

    public static void doTest(java.io.Writer writer, NodeQuery q) {
        Cloud cloud = q.getCloud();

        NodeManager object = cloud.getNodeManager("object");
        try {

            long startTime = System.currentTimeMillis();

            TreeList tree = new TreeList(q);

            writer.write("grow1:\n");writer.flush();
            RelationStep step = tree.grow(object, "index", "destination");
            NodeQuery top = tree.getLeafQuery();
            StepField pos = top.createStepField(step, "pos");
            top.addSortOrder(pos, SortOrder.ORDER_ASCENDING);

            writer.write("top " + top + " grow2:\n");writer.flush();
            tree.grow(object, "index", "destination");

            writer.write("GROWN, now using ================================================================================");writer.flush();
            TreeIterator i = tree.treeIterator();
            writer.write("initial depth " + i.currentDepth() + "\n");
            writer.flush();
            writer.write("size: " + tree.size() + "\n");
            writer.flush();
            while (i.hasNext()) {
                Node n = (Node)i.next();
                writer.write(i.currentDepth() + "  " + n.getNumber() + " " + n.getFunctionValue("gui", null) + "\n");
                writer.flush();
            }
            writer.write("size: " + tree.size() + "\n");
            writer.write("duration: " + (System.currentTimeMillis() - startTime) + " ms\n");
            writer.write("finish depth: " + i.currentDepth());
            writer.flush();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + e.getMessage() + Logging.stackTrace(e));
        }

    }

    public static void main(String[] args) {
        NodeQuery q = getQuery(args);
        doTest(new java.io.OutputStreamWriter(System.out), q);

    }

}
