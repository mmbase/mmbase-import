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

/**
 * Queries a Tree from MMBase. A Tree is presented as a List of MultiLevel results (ClusterNodes),
 * combined with a smart iterator which iterates through the elements these lists as if it was one
 * list ordered as a Tree.
 *
 *
 * @author  Michiel Meeuwissen
 * @version $Id: GrowingTreeList.java,v 1.10 2005-06-17 13:23:58 michiel Exp $
 * @since   MMBase-1.7
 */

public  class GrowingTreeList extends TreeList {

    protected Constraint cleanConstraint;
    protected NodeQuery  pathElementTemplate;
    //protected NodeQuery  shiftElementTemplate;
    protected int maxNumberOfSteps;

    /**
     * @param q              The 'base' query defining the minimal depth of the tree elements
     * @param maxDepth       You must supply a maximal depth of the nodes, because MMBase is basicly a network rather then a tree, so
     *                        tree representations could be infinitely deep.
     * @param nodeManager    Destination Nodemanager in the tree
     * @param role           Role of the relations in the tree
     * @param searchDir      Direction of the relations in the tree
     * @since MMBase-1.7.1
     */
    public GrowingTreeList(NodeQuery q, int maxDepth, NodeManager nodeManager, String role, String searchDir) {
        super(q);

        if (nodeManager == null) nodeManager = cloud.getNodeManager("object");
        pathElementTemplate = cloud.createNodeQuery();
        //shiftElementTemplate = cloud.createNodeQuery();
        Step step = pathElementTemplate.addStep(cloud.getNodeManager("object"));
        pathElementTemplate.setAlias(step, "object0");
        pathElementTemplate.setNodeStep(pathElementTemplate.addRelationStep(nodeManager, role, searchDir).getNext());

        setMaxDepth(maxDepth);
    }

    public GrowingTreeList(NodeQuery q, int maxDepth) {
        super(q);
        pathElementTemplate = cloud.createNodeQuery();
        Step step = pathElementTemplate.addStep(cloud.getNodeManager("object"));
        pathElementTemplate.setAlias(step, "object0");

        setMaxDepth(maxDepth);
    }

    /**
     * As long as the tree is not 'started' yet, max depth can still be changed.
     * @param maxDepth max number of Steps
     * @since MMBase-1.7.1
     */

    public void setMaxDepth(int maxDepth) {
        maxNumberOfSteps = 2 * maxDepth - 1; // dont consider relation steps.


        if (maxNumberOfSteps < numberOfSteps) {
            throw new IllegalArgumentException("Query is already deeper than maxdepth");
        }
    }

    /**
     * Returns the Query which is used as a template to 'grow' the query. You can change it, add sort-orders and add constraints before
     * the tree is 'started'.
     * All but the first step of this query are added. This query itself is never used.
     * @return Query which is used as a template
     * @since MMBase-1.7.1
     */

    public NodeQuery getTemplate() {
        return pathElementTemplate;
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
     *
     */
    protected void addPathElement() {
        if (! pathElementTemplate.isUsed()) {
            //Queries.sortUniquely(pathElementTemplate);
            pathElementTemplate.markUsed();
            cleanConstraint = pathElementTemplate.getCleanConstraint();
        }
        if (numberOfSteps + 2  > maxNumberOfSteps) {
            foundEnd = true;
        } else {
            Iterator steps = pathElementTemplate.getSteps().iterator();;
            steps.next(); // ignore first step
            while (steps.hasNext()) {
                RelationStep relationStepTemplate = (RelationStep) steps.next();
                Step         stepTemplate         = (Step)         steps.next();
                String role;
                {   // it's a pity but role cannot be requested directly from RelationStep
                    // some hackery
                    Integer      reldef = relationStepTemplate.getRole();
                    if (reldef == null) {
                        role = null;
                    } else {
                        role = cloud.getNode(reldef.intValue()).getStringValue("sname");
                    }
                }

                RelationStep newStep = grow(cloud.getNodeManager(stepTemplate.getTableName()),
                                            role,
                                            RelationStep.DIRECTIONALITY_DESCRIPTIONS[relationStepTemplate.getDirectionality()]);
                if (newStep == null) {
                    foundEnd = true;
                    break;
                }
                // Step doesn't have a .getQuery() method, so we'll have to fall back to this:
                Query newQuery = (Query)queries.get(queries.size() - 1);

                // add sortorder to the query
                Step nextStep = newStep.getNext();

                if (cleanConstraint != null) {
                    Constraint newStepConstraint         = Queries.copyConstraint(cleanConstraint, stepTemplate, newQuery, nextStep);
                    Constraint newRelationStepConstraint = Queries.copyConstraint(cleanConstraint, relationStepTemplate, newQuery, newStep);
                    Queries.addConstraint(newQuery, newStepConstraint);
                    Queries.addConstraint(newQuery, newRelationStepConstraint);
                }


                Queries.copySortOrders(pathElementTemplate.getSortOrders(), stepTemplate, newQuery, nextStep);
                Queries.copySortOrders(pathElementTemplate.getSortOrders(), relationStepTemplate, newQuery, newStep);

                if (numberOfSteps + 2  > maxNumberOfSteps) {
                    foundEnd = true;
                    break;
                }
            }
        }
    }


    public static void  main(String[] args) {

        NodeQuery q = getQuery(args);
        Cloud    cloud = q.getCloud();

        NodeManager object = cloud.getNodeManager("object");

        TreeList tree = new GrowingTreeList(q, 5, object, null, "destination");

        Iterator i = tree.iterator();
        while (i.hasNext()) {
            Node n = (Node) i.next();
            System.out.println(n.toString());
        }
    }
}
