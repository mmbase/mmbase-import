/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.util;

import java.util.Iterator;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;

/**
 * Queries a Tree from MMBase. A Tree is presented as a List of MultiLevel results (ClusterNodes),
 * combined with a smart iterator which iterates through the elements these lists as if it was one
 * list ordered as a Tree.
 *
 *
 * @author  Michiel Meeuwissen
 * @version $Id: GrowingTreeList.java,v 1.2 2004-02-11 20:43:22 keesj Exp $
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
            grow(pathElement.nodeManager, pathElement.role, pathElement.searchDir);
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
