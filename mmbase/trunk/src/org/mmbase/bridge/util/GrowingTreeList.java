/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.util;

import java.util.Iterator;

import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;
import org.mmbase.util.logging.*;

/**
 * Queries a Tree from MMBase. A Tree is presented as a List of MultiLevel results (ClusterNodes),
 * combined with a smart iterator which iterates through the elements these lists as if it was one
 * list ordered as a Tree.
 *
 *
 * @author  Michiel Meeuwissen
 * @version $Id: GrowingTreeList.java,v 1.3 2004-06-17 11:35:50 johannes Exp $
 * @since   MMBase-1.7
 */

public  class GrowingTreeList extends TreeList {
    private static final Logger log = Logging.getLoggerInstance(GrowingTreeList.class);

    protected PathElement  pathElement;
    protected int maxNumberOfSteps;

    /**
     * @param q              The 'base' query defining the minimal depth of the tree elements
     * @param pathElement    The pathElement structure defines one 'relationStep', an array of them
     *                       defines by which the branches will grow everytime
     * @param maxDepth       You must supply a maximal depth of the nodes, because MMBase is basicly a network rather then a tree, so
     *                        tree representations could be infinitely deep.
     */

    public GrowingTreeList(NodeQuery q, PathElement pathElement, int maxDepth) {

        super(q);

        this.pathElement = pathElement;
        maxNumberOfSteps = 2 * maxDepth - 1; // dont consider relation steps.

        if (maxNumberOfSteps < numberOfSteps) {
            throw new IllegalArgumentException("Query is already deeper than maxdepth");
        }
    }

   

    public int size() {
        while (! foundEnd) {
            addPathElement();
        }
        return super.size();
    }

    protected NodeList getList(int queryNumber) {
        while (queryNumber >= queries.size() && (!foundEnd)) {
            addPathElement();
        }
        return super.getList(queryNumber);
    }

    /**
     * Generates a new query (and does a count on it)
     */
    protected void addPathElement() {
        if (numberOfSteps + 2  > maxNumberOfSteps) {
            foundEnd = true;
        } else {
            RelationStep step = grow(pathElement.nodeManager, pathElement.role, pathElement.searchDir);
            if (step != null) {
                // add sortorder to the query
                Step nextStep = step.getNext();

                // Step doesn't have a .getQuery() method, so we'll have to fall back to this:
                Query newQuery = (Query)queries.get(queries.size() - 1);

                if (pathElement.sortFieldRelation != null && !"".equals(pathElement.sortFieldRelation)) {
                    if (pathElement.sortOrderRelation != SortOrder.ORDER_ASCENDING && pathElement.sortOrderRelation != SortOrder.ORDER_DESCENDING)
                        pathElement.sortOrderRelation = SortOrder.ORDER_DESCENDING;
                    StepField sf1 = newQuery.createStepField(step, pathElement.sortFieldRelation);
                    newQuery.addSortOrder(sf1, pathElement.sortOrderRelation);
                }

                if (pathElement.sortFieldNodes != null && !"".equals(pathElement.sortFieldNodes)) {
                    if (pathElement.sortOrderNodes != SortOrder.ORDER_ASCENDING && pathElement.sortOrderNodes != SortOrder.ORDER_DESCENDING)
                        pathElement.sortOrderNodes = SortOrder.ORDER_DESCENDING;

                    StepField sf2 = newQuery.createStepField(nextStep, pathElement.sortFieldNodes);
                    newQuery.addSortOrder(sf2, pathElement.sortOrderNodes);
                }
                
                // Always add a sort on number fields, because the sort order
                // needs to be unique and consistent every time
                StepField sf1 = newQuery.createStepField(step, "number");
                newQuery.addSortOrder(sf1, SortOrder.ORDER_DESCENDING);
                StepField sf2 = newQuery.createStepField(nextStep, "number");
                newQuery.addSortOrder(sf2, SortOrder.ORDER_DESCENDING);
            }
        }
    }

    public static class PathElement {
        NodeManager nodeManager;
        String      role;
        String      searchDir;
        String      fields;
        String      sortFieldNodes = null;
        int         sortOrderNodes = 0;
        String      sortFieldRelation = null;
        int         sortOrderRelation = 0;

        public PathElement(NodeManager nm, String r, String sd) {
            nodeManager = nm;
            role = r;
            searchDir = sd;
        }

        public PathElement(NodeManager nm, String r, String sd, String so, int sdir) {
            nodeManager = nm;
            role = r;
            searchDir = sd;
            if (so != null && so.startsWith(r + ".")) {
                // Change 'posrel.pos' to 'pos'
                sortFieldRelation = so.substring(r.length() + 1, so.length());
                sortOrderRelation = sdir;
            } else {
                sortFieldNodes = so;
                sortOrderNodes = sdir;
            }
        }
    }


    public static void  main(String[] args) {

        NodeQuery q = getQuery(args);
        Cloud    cloud = q.getCloud();

        NodeManager object = cloud.getNodeManager("object");

        TreeList tree = new GrowingTreeList(q, new PathElement(object, null, "destination"), 5);

        Iterator i = tree.iterator();
        while (i.hasNext()) {
            Node n = (Node) i.next();
            System.out.println(n.toString());
        }
    }
}
