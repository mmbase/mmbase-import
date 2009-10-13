/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.util;

import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;


/**
 * This utility provides methods to easily create and execute queries in the bridge.
 * These methods are replacements for frequently used code snippets which are present in
 * code which uses the query objects in the mmbase bridge.
 * The implementation of these methods can also be used as documentation how to use the
 * search query api.
 *
 * @author Nico Klasens
 * @version $Id$
 */
public class SearchUtil {

    /** A Search direction of relations in queries */
    public static final String SOURCE = "SOURCE";
    /** A Search direction of relations in queries */
    public static final String DESTINATION = "DESTINATION";

    private SearchUtil() {
        // Utility
    }

    /**
     * Search for a node which contains a field value. The value is matched on equality (exact match).
     * For a string field type the match is case-insensitive.
     * If multiple nodes are found with the same value then the first node is returned
     *
     * @param cloud - user cloud to search in
     * @param managerName - name of manager to search with
     * @param fieldname - name of field to search with
     * @param value - value to search for in the field
     * @return Single node which matches the field value
     */
    public static Node findNode(Cloud cloud, String managerName, String fieldname, String value) {
        return findNode(cloud, managerName, fieldname, value, null, null);
    }

    /**
     * Search for a node which contains a field value. The value is matched on equality (exact match).
     * For a string field type the match is case-insensitive.
     * If multiple nodes are found with the same value then the first node is returned
     *
     * @param cloud - user cloud to search in
     * @param managerName - name of manager to search with
     * @param fieldname - name of field to search with
     * @param value - value to search for in the field
     * @param sortName - name of field to sort on.
     * @return Single node which matches the field value
     */
    public static Node findNode(Cloud cloud, String managerName, String fieldname, String value, String sortName) {
        return findNode(cloud, managerName, fieldname, value, sortName, null);
    }

    /**
     * Retrieve the first node based on the sort field
     *
     * @param cloud - user cloud to search in
     * @param managerName - name of manager to search with
     * @param sortName - name of field to sort on.
     * @return Single node
     */
    public static Node findOrderedNode(Cloud cloud, String managerName, String sortName) {
        return findNode(cloud, managerName, null, null, sortName, null);
    }

    /**
     * Retrieve the first node based on the sort field
     *
     * @param cloud - user cloud to search in
     * @param managerName - name of manager to search with
     * @param sortName - name of field to sort on.
     * @param sortDirection - direction of the sort (UP, DOWN)
     * @return Single node
     */
    public static Node findOrderedNode(Cloud cloud, String managerName, String sortName, String sortDirection) {
        return findNode(cloud, managerName, null, null, sortName, sortDirection);
    }

    /**
     * Search for a node which contains a field value. The value is matched on equality (exact match).
     * For a string field type the match is case-insensitive.
     * If multiple nodes are found with the same value then the first node is returned
     *
     * @param cloud - user cloud to search in
     * @param managerName - name of manager to search with
     * @param fieldname - name of field to search with
     * @param value - value to search for in the field
     * @param sortName - name of field to sort on.
     * @param sortDirection - direction of the sort (UP, DOWN)
     * @return Single node which matches the field value
     */
    public static Node findNode(Cloud cloud, String managerName, String fieldname, String value, String sortName, String sortDirection) {
        NodeList list = findNodeList(cloud, managerName, fieldname, value, sortName, sortDirection);
        if (list.size() > 0) {
            return list.getNode(0);
        }
        return null;
    }

    /**
     * Search for nodes which contain a field value. The value is matched on equality (exact match).
     * For a string field type the match is case-insensitive.
     *
     * @param cloud - user cloud to search in
     * @param managerName - name of manager to search with
     * @return list of nodes
     */
    public static NodeList findNodeList(Cloud cloud, String managerName) {
        return findNodeList(cloud, managerName, null, null, null, null);
    }

    /**
     * Search for nodes which contain a field value. The value is matched on equality (exact match).
     * For a string field type the match is case-insensitive.
     *
     * @param cloud - user cloud to search in
     * @param managerName - name of manager to search with
     * @param fieldname - name of field to search with
     * @param value - value to search for in the field
     * @return list of nodes which match the field value
     */
    public static NodeList findNodeList(Cloud cloud, String managerName, String fieldname, Object value) {
        return findNodeList(cloud, managerName, fieldname, value, null, null);
    }

    /**
     * Search for nodes which contain a field value. The value is matched on equality (exact match).
     * For a string field type the match is case-insensitive.
     *
     * @param cloud - user cloud to search in
     * @param managerName - name of manager to search with
     * @param fieldname - name of field to search with
     * @param value - value to search for in the field
     * @param sortName - name of field to sort on.
     * @return list of nodes which match the field value
     */
    public static NodeList findNodeList(Cloud cloud, String managerName, String fieldname, Object value, String sortName) {
        return findNodeList(cloud, managerName, fieldname, value, sortName, null);
    }

    /**
     * Retrieve nodes which are sorted on the field name
     *
     * @param cloud - user cloud to search in
     * @param managerName - name of manager to search with
     * @param sortName - name of field to sort on.
     * @return list of nodes
     */
    public static NodeList findOrderedNodeList(Cloud cloud, String managerName, String sortName) {
        return findNodeList(cloud, managerName, null, null, sortName, null);
    }

    /**
     * Retrieve nodes which are sorted on the field name
     *
     * @param cloud - user cloud to search in
     * @param managerName - name of manager to search with
     * @param sortName - name of field to sort on.
     * @param sortDirection - direction of the sort (UP, DOWN)
     * @return list of nodes
     */
    public static NodeList findOrderedNodeList(Cloud cloud, String managerName, String sortName, String sortDirection) {
        return findNodeList(cloud, managerName, null, null, sortName, sortDirection);
    }

    /**
     * Retrieve nodes which contain a field value. The value is matched on equality (exact match).
     * For a string field type the match is case-insensitive.
     *
     * @param cloud - user cloud to search in
     * @param managerName - name of manager to search with
     * @param fieldname - name of field to search with
     * @param value - value to search for in the field
     * @param sortName - name of field to sort on.
     * @param sortDirection - direction of the sort (UP, DOWN)
     * @return list of nodes which match the field value
     */
    public static NodeList findNodeList(Cloud cloud, String managerName, String fieldname, Object value, String sortName, String sortDirection) {
        NodeManager manager = cloud.getNodeManager(managerName);
        NodeQuery query = manager.createQuery();
        addEqualConstraint(query, manager, fieldname, value);
        if (!isEmptyOrWhitespace(sortName)) {
            addSortOrder(query, manager, sortName, sortDirection);
        }
        return manager.getList(query);
    }

    /**
     * Search for a node which is related to the parent node.
     * If multiple nodes are found then the first node is returned
     *
     * @param parent - node to start the search from
     * @param managerName - name of manager to search with
     * @param role - name of relation (relation role in the mmbase system)
     * @return Single node
     */
    public static Node findRelatedNode(Node parent, String managerName, String role) {
        return findRelatedNode(parent, managerName, role, null, null, null, null);
    }

    /**
     * Search for a node which is related to the parent node and contains a field value.
     * The value is matched on equality (exact match).
     * For a string field type the match is case-insensitive.
     * If multiple nodes are found with the same value then the first node is returned
     *
     * @param parent - node to start the search from
     * @param managerName - name of manager to search with
     * @param role - name of relation (relation role in the mmbase system)
     * @param fieldname - name of field to search with
     * @param value - value to search for in the field
     * @return Single node which matches the field value
     */
    public static Node findRelatedNode(Node parent, String managerName, String role, String fieldname, Object value) {
        return findRelatedNode(parent, managerName, role, fieldname, value, null, null);
    }

    /**
     * Search for a node which is related to the parent node and contains a field value.
     * The value is matched on equality (exact match).
     * For a string field type the match is case-insensitive.
     * If multiple nodes are found with the same value then the first node is returned
     *
     * @param parent - node to start the search from
     * @param managerName - name of manager to search with
     * @param role - name of relation (relation role in the mmbase system)
     * @param fieldname - name of field to search with
     * @param value - value to search for in the field
     * @param sortName - name of field to sort on.
     * @return Single node which matches the field value
     */
    public static Node findRelatedNode(Node parent, String managerName, String role, String fieldname, Object value, String sortName) {
        return findRelatedNode(parent, managerName, role, fieldname, value, sortName, null);
    }

    /**
     * Retrieve a node which is related to the parent node and is sorted in a field.
     * If multiple nodes are found with the same value then the first node is returned
     *
     * @param parent - node to start the search from
     * @param managerName - name of manager to search with
     * @param role - name of relation (relation role in the mmbase system)
     * @param sortName - name of field to sort on.
     * @return Single node
     */
    public static Node findRelatedOrderedNode(Node parent, String managerName, String role, String sortName) {
        return findRelatedNode(parent, managerName, role, null, null, sortName, null);
    }

    /**
     * Retrieve a node which is related to the parent node and is sorted in a field.
     * If multiple nodes are found with the same value then the first node is returned
     *
     * @param parent - node to start the search from
     * @param managerName - name of manager to search with
     * @param role - name of relation (relation role in the mmbase system)
     * @param sortName - name of field to sort on.
     * @param sortDirection - direction of the sort (UP, DOWN)
     * @return Single node
     */
    public static Node findRelatedOrderedNode(Node parent, String managerName, String role, String sortName, String sortDirection) {
        return findRelatedNode(parent, managerName, role, null, null, sortName, sortDirection);
    }

    /**
     * Search for a node which is related to the parent node and contains a field value.
     * The value is matched on equality (exact match).
     * For a string field type the match is case-insensitive.
     * If multiple nodes are found with the same value then the first node is returned
     *
     * @param parent - node to start the search from
     * @param managerName - name of manager to search with
     * @param role - name of relation (relation role in the mmbase system)
     * @param fieldname - name of field to search with
     * @param value - value to search for in the field
     * @param sortName - name of field to sort on.
     * @param sortDirection - direction of the sort (UP, DOWN)
     * @return Single node which matches the field value
     */
    public static Node findRelatedNode(Node parent, String managerName, String role, String fieldname, Object value, String sortName, String sortDirection) {
        NodeList list = findRelatedNodeList(parent, managerName, role, fieldname, value, sortName, sortDirection);
        if (list.size() > 0) {
            return list.getNode(0);
        }
        return null;
    }

    /**
     * Search for nodes which are related to the parent node.
     *
     * @param parent - node to start the search from
     * @param managerName - name of manager to search with
     * @param role - name of relation (relation role in the mmbase system)
     * @return a list of nodes
     */
    public static NodeList findRelatedNodeList(Node parent, String managerName, String role) {
        return findRelatedNodeList(parent, managerName, role, null, null, null, null);
    }

    /**
     * Search for nodes which are related to the parent node and contain a field value.
     * The value is matched on equality (exact match).
     * For a string field type the match is case-insensitive.
     *
     * @param parent - node to start the search from
     * @param managerName - name of manager to search with
     * @param role - name of relation (relation role in the mmbase system)
     * @param fieldname - name of field to search with
     * @param value - value to search for in the field
     * @return a list of nodes which match the field value
     */
    public static NodeList findRelatedNodeList(Node parent, String managerName, String role, String fieldname, Object value) {
        return findRelatedNodeList(parent, managerName, role, fieldname, value, null, null);
    }

    /**
     * Search for nodes which are related to the parent node and contain a field value.
     * The value is matched on equality (exact match).
     * For a string field type the match is case-insensitive.
     *
     * @param parent - node to start the search from
     * @param managerName - name of manager to search with
     * @param role - name of relation (relation role in the mmbase system)
     * @param fieldname - name of field to search with
     * @param value - value to search for in the field
     * @param sortName - name of field to sort on.
     * @return a list of nodes which match the field value
     */
    public static NodeList findRelatedNodeList(Node parent, String managerName, String role, String fieldname, Object value, String sortName) {
        return findRelatedNodeList(parent, managerName, role, fieldname, value, sortName, null);
    }

    /**
     * Search for nodes which are related to the parent node and contain a field value.
     * The value is matched on equality (exact match).
     * For a string field type the match is case-insensitive.
     *
     * @param parent - node to start the search from
     * @param managerName - name of manager to search with
     * @param role - name of relation (relation role in the mmbase system)
     * @param sortName - name of field to sort on.
     * @return a list of nodes
     */
    public static NodeList findRelatedOrderedNodeList(Node parent, String managerName, String role, String sortName) {
        return findRelatedNodeList(parent, managerName, role, null, null, sortName, null);
    }

    /**
     * Search for nodes which are related to the parent node and contain a field value.
     * The value is matched on equality (exact match).
     * For a string field type the match is case-insensitive.
     *
     * @param parent - node to start the search from
     * @param managerName - name of manager to search with
     * @param role - name of relation (relation role in the mmbase system)
     * @param sortName - name of field to sort on.
     * @param sortDirection - direction of the sort (UP, DOWN)
     * @return a list of nodes
     */
    public static NodeList findRelatedOrderedNodeList(Node parent, String managerName, String role, String sortName, String sortDirection) {
        return findRelatedNodeList(parent, managerName, role, null, null, sortName, sortDirection);
    }

    /**
     * Search for nodes which are related to the parent node and contain a field value.
     * The value is matched on equality (exact match).
     * For a string field type the match is case-insensitive.
     *
     * @param parent - node to start the search from
     * @param managerName - name of manager to search with
     * @param role - name of relation (relation role in the mmbase system)
     * @param fieldname - name of field to search with
     * @param value - value to search for in the field
     * @param sortName - name of field to sort on.
     * @param sortDirection - direction of the sort (UP, DOWN)
     * @return a list of nodes which match the field value
     */
    public static NodeList findRelatedNodeList(Node parent, String managerName, String role, String fieldname, Object value, String sortName, String sortDirection) {
        NodeQuery query = createRelatedNodeListQuery(parent, managerName, role, fieldname, value, sortName, sortDirection);
        return query.getNodeManager().getList(query);
    }

    /**
     * Search for nodes which are related to the parent node and contain a field value.
     * The value is matched on equality (exact match).
     * For a string field type the match is case-insensitive.
     *
     * @param parent - node to start the search from
     * @param managerName - name of manager to search with
     * @param role - name of relation (relation role in the mmbase system)
     * @param fieldname - name of field to search with
     * @param value - value to search for in the field
     * @param sortName - name of field to sort on.
     * @param sortDirection - direction of the sort (UP, DOWN)
     * @param searchdir - direction of the relation (source, destination, both)
     * @return a list of nodes which match the field value
     */
    public static NodeList findRelatedNodeList(Node parent, String managerName, String role, String fieldname, Object value, String sortName, String sortDirection, String searchdir) {
        NodeQuery query = createRelatedNodeListQuery(parent, managerName, role, fieldname, value, sortName, sortDirection, searchdir);
        return query.getNodeManager().getList(query);
    }

    /**
     * Create a query for a list of nodes which are related to the parent node and contain a field value.
     * The value is matched on equality (exact match).
     * For a string field type the match is case-insensitive.
     * Relation direction is destination
     *
     * @param parent - node to start the search from
     * @param managerName - name of manager to search with
     * @param role - name of relation (relation role in the mmbase system)
     * @param fieldname - name of field to search with
     * @param value - value to search for in the field
     * @param sortName - name of field to sort on.
     * @param sortDirection - direction of the sort (UP, DOWN)
     * @return a query for a list of nodes which match the field value
     */
    public static NodeQuery createRelatedNodeListQuery(Node parent, String managerName, String role, String fieldname, Object value, String sortName, String sortDirection) {
        return createRelatedNodeListQuery(parent, managerName, role, fieldname, value, sortName, sortDirection, DESTINATION);
    }

    /**
     * Create a query for a list of nodes which are related to the parent node and contain a field value.
     * The value is matched on equality (exact match).
     * For a string field type the match is case-insensitive.
     *
     * @param parent - node to start the search from
     * @param managerName - name of manager to search with
     * @param role - name of relation (relation role in the mmbase system)
     * @param fieldname - name of field to search with
     * @param value - value to search for in the field
     * @param sortName - name of field to sort on.
     * @param sortDirection - direction of the sort (UP, DOWN)
     * @param searchdir - direction of the relation (source, destination, both)
     * @return a query for a list of nodes which match the field value
     */
    public static NodeQuery createRelatedNodeListQuery(Node parent, String managerName, String role, String fieldname, Object value, String sortName, String sortDirection, String searchdir) {
        NodeQuery query = createRelatedNodeListQuery(parent, managerName, role, searchdir);
        addFeatures(query, parent, managerName, role, fieldname, value, sortName, sortDirection, searchdir);
        return query;
    }

    /**
     * Create a query for a list of nodes which are related to the parent node and contain a field value.
     * The value is matched on equality (exact match).
     * For a string field type the match is case-insensitive.
     *
     * @param parent - node to start the search from
     * @param managerName - name of manager to search with
     * @param role - name of relation (relation role in the mmbase system)
     * @return a query for a list of nodes
     */
    public static NodeQuery createRelatedNodeListQuery(Node parent, String managerName, String role) {
        return createRelatedNodeListQuery(parent, managerName, role, DESTINATION);
    }

    /**
     * Create a query for a list of nodes which are related to the parent node and contain a field value.
     * The value is matched on equality (exact match).
     * For a string field type the match is case-insensitive.
     *
     * @param parent - node to start the search from
     * @param managerName - name of manager to search with
     * @param role - name of relation (relation role in the mmbase system)
     * @param searchdir - direction of the relation (source, destination, both)
     * @return a query for a list of nodes
     */
    public static NodeQuery createRelatedNodeListQuery(Node parent, String managerName, String role, String searchdir) {
        NodeManager manager;
        if (isEmptyOrWhitespace(managerName)) {
            manager = parent.getCloud().getNodeManager("object");
        }
        else {
            manager = parent.getCloud().getNodeManager(managerName);
        }

        NodeQuery query = parent.getCloud().createNodeQuery();
        Step step1 = query.addStep(parent.getNodeManager());
        query.addNode(step1, parent);

        RelationStep step2 = query.addRelationStep(manager, role, searchdir);
        Step step3 = step2.getNext();
        query.setNodeStep(step3); // makes it ready for use as NodeQuery
        return query;
    }

    /**
     * Create a query for a list of nodes which are related to the parent node and contain a field value.
     * The value is matched on equality (exact match).
     * For a string field type the match is case-insensitive.
     *
     * @param parentNodes - nodes to start the search from
     * @param managerName - name of manager to search with
     * @param role - name of relation (relation role in the mmbase system)
     * @return a query for a list of nodes
     */
    public static NodeQuery createRelatedNodeListQuery(NodeList parentNodes, String managerName, String role) {
        return createRelatedNodeListQuery(parentNodes, managerName, role, DESTINATION);
    }

    /**
     * Create a query for a list of nodes which are related to the parent node and contain a field value.
     * The value is matched on equality (exact match).
     * For a string field type the match is case-insensitive.
     *
     * @param parentNodes - nodes to start the search from
     * @param managerName - name of manager to search with
     * @param role - name of relation (relation role in the mmbase system)
     * @param searchdir - direction of the relation (source, destination, both)
     * @return a query for a list of nodes
     */
    public static NodeQuery createRelatedNodeListQuery(NodeList parentNodes, String managerName, String role, String searchdir) {
        if (parentNodes.isEmpty()) {
            throw new IllegalArgumentException("paretnodes is empty. should be at leat one");
        }
        Node parent = parentNodes.getNode(0);

        NodeManager manager = parent.getCloud().getNodeManager(managerName);

        NodeQuery query = parent.getCloud().createNodeQuery();
        Step step1 = query.addStep(parent.getNodeManager());
        for (Iterator iter = parentNodes.iterator(); iter.hasNext();) {
            Node parentNode = (Node) iter.next();
            query.addNode(step1, parentNode);
        }

        RelationStep step2 = query.addRelationStep(manager, role, searchdir);
        Step step3 = step2.getNext();
        query.setNodeStep(step3); // makes it ready for use as NodeQuery
        return query;
    }

    /**
     * Search for a list of relations which are related to the parent node.
     *
     * @param parent - node to start the search from
     * @param managerName - name of manager to search with
     * @param role - name of relation (relation role in the mmbase system)
     * @param sortName - name of field to sort on.
     * @param sortDirection - direction of the sort (UP, DOWN)
     * @return a list of relations
     */
    public static RelationList findRelations(Node parent, String managerName, String role, String sortName, String sortDirection) {
        return findRelations(parent, managerName, role, sortName, sortDirection, DESTINATION);
    }

    /**
     * Search for a list of relations which are related to the parent node.
     *
     * @param parent - node to start the search from
     * @param managerName - name of manager to search with
     * @param role - name of relation (relation role in the mmbase system)
     * @param sortName - name of field to sort on.
     * @param sortDirection - direction of the sort (UP, DOWN)
     * @param searchdir - direction of the relation (source, destination, both)
     * @return a list of relations
     */
    public static RelationList findRelations(Node parent, String managerName, String role, String sortName, String sortDirection, String searchdir) {
        NodeManager manager = parent.getCloud().getNodeManager(managerName);
        NodeQuery query = parent.getCloud().createNodeQuery();
        Step step1 = query.addStep(parent.getNodeManager());
        query.addNode(step1, parent);
        RelationStep step = query.addRelationStep(manager, role, searchdir);
        query.setNodeStep(step);

        if (!isEmptyOrWhitespace(sortName)) {
            RelationManager relationManager = parent.getCloud().getRelationManager(parent.getNodeManager(), manager, role);
            addRelationSortOrder(query, relationManager, sortName, sortDirection);
        }

        NodeManager nm = query.getNodeManager();
        return (RelationList) nm.getList(query);
    }

    /**
     * Finds the relation-nodes between two specified nodes
     * @param source - source node
     * @param destination - destination node
     * @param role - name of relation (relation role in the mmbase system)
     * @param searchdir - direction of the relation (source, destination, both)
     * @return a list of relations
     *
     * @since MMBase-1.8.5
     */
    public static RelationList findRelations(Node source, Node destination, String role, String searchdir) {

        Cloud cloud = source.getCloud();
        RelationManager relationManager =
            role == null ?
            cloud.getNodeManager("insrel").toRelationManager() :
            cloud.getRelationManager(source.getNodeManager(), destination.getNodeManager(), role);

        NodeQuery q = relationManager.createQuery();
        if (DESTINATION.equalsIgnoreCase(searchdir)) {
            Queries.addConstraint(q, Queries.createConstraint(q, "snumber", FieldCompareConstraint.EQUAL, source));
            Queries.addConstraint(q, Queries.createConstraint(q, "dnumber", FieldCompareConstraint.EQUAL, destination));
        } else if (SOURCE.equalsIgnoreCase(searchdir)) {
            Queries.addConstraint(q, Queries.createConstraint(q, "dnumber", FieldCompareConstraint.EQUAL, source));
            Queries.addConstraint(q, Queries.createConstraint(q, "snumber", FieldCompareConstraint.EQUAL, destination));
        } else {
            Queries.addConstraint(q, Queries.createConstraint(q, "snumber", FieldCompareConstraint.EQUAL, source));
            Queries.addConstraint(q, Queries.createConstraint(q, "dnumber", FieldCompareConstraint.EQUAL, destination));
            if (Queries.count(q) == 0) {
                RelationManager relationManager2 =
                    role == null ?
                    cloud.getNodeManager("insrel").toRelationManager() :
                    cloud.getRelationManager(destination.getNodeManager(), source.getNodeManager(), role);
                NodeQuery q2 = relationManager2.createQuery();
                Queries.addConstraint(q2, Queries.createConstraint(q2, "dnumber", FieldCompareConstraint.EQUAL, source));
                Queries.addConstraint(q2, Queries.createConstraint(q2, "snumber", FieldCompareConstraint.EQUAL, destination));
                if (Queries.count(q2) > 0) {
                    q = q2;
                    relationManager = relationManager2;
                }
            }
        }
        return new CollectionRelationList(relationManager.getList(q), cloud);
    }


    /**
     * Add constraints and sort orders to a query.
     * The field value is matched on equality (exact match).
     * For a string field type the match is case-insensitive.
     *
     * @param query - the query to add the constrains and sort orders to
     * @param parent - node to start the search from
     * @param managerName - name of manager to search with
     * @param role - name of relation (relation role in the mmbase system)
     * @param fieldname - name of field to search with
     * @param value - value to search for in the field
     * @param sortName - name of field to sort on.
     * @param sortDirection - direction of the sort (UP, DOWN)
     */
    public static void addFeatures(NodeQuery query, Node parent, String managerName, String role, String fieldname, Object value, String sortName, String sortDirection) {
        addFeatures(query, parent, managerName, role, fieldname, value, sortName, sortDirection, DESTINATION);
    }


    /**
     * Add constraints and sort orders to a query.
     * The field value is matched on equality (exact match).
     * For a string field type the match is case-insensitive.
     *
     * @param query - the query to add the constrains and sort orders to
     * @param parent - node to start the search from
     * @param managerName - name of manager to search with
     * @param role - name of relation (relation role in the mmbase system)
     * @param fieldname - name of field to search with
     * @param value - value to search for in the field
     * @param sortName - name of field to sort on.
     * @param sortDirection - direction of the sort (UP, DOWN)
     * @param searchdir - direction of the relation (source, destination, both)
     */
    public static void addFeatures(NodeQuery query, Node parent, String managerName, String role, String fieldname, Object value, String sortName, String sortDirection, String searchdir) {
        NodeManager manager = null;
        if (!isEmptyOrWhitespace(managerName)) {
            manager = parent.getCloud().getNodeManager(managerName);
            addEqualConstraint(query, manager, fieldname, value);
        }

        if (!isEmptyOrWhitespace(sortName)) {
            if (sortName.startsWith(role + ".")) {
                String sortField = sortName.substring(role.length() + 1);
                RelationManager relationManager = null;
                if (managerName == null) {
                    if (hasAllowedRelation(parent, role, searchdir)) {
                        relationManager = parent.getCloud().getRelationManager(role);
                    }
                }
                else {
                    if (SOURCE.equals(searchdir)) {
                        relationManager = parent.getCloud().getRelationManager(manager, parent.getNodeManager(), role);
                    }
                    else {
                        relationManager = parent.getCloud().getRelationManager(parent.getNodeManager(), manager, role);
                    }
                }
                if (relationManager == null) {
                    throw new IllegalArgumentException("Relation " + role + " not possible between " +
                            parent.getNodeManager().getName() + " and " + managerName);
                }
                addRelationSortOrder(query, relationManager, sortField, sortDirection);
            }
            else {
                if (manager != null) {
                    addSortOrder(query, manager, sortName, sortDirection);
                }
            }
        }
    }

    /**
     * Check to see if a relation is allowed from the parent node
     * @param parent - node to start  from
     * @param role - name of relation (relation role in the mmbase system)
     * @param searchdir - direction of the relation (source, destination, both)
     * @return true when relation is allowed
     */
    public static boolean hasAllowedRelation(Node parent, String role, String searchdir) {
        return parent.getNodeManager().getAllowedRelations((String) null, role, searchdir) != null;
    }

    /**
     * Add a sort order to a query for a field of the manager.
     *
     * @param query - the query to add the sort order to
     * @param manager - manager of the sort field
     * @param sortName - name of field to sort on.
     * @param sortDirection - direction of the sort (UP, DOWN)
     */
    public static void addSortOrder(NodeQuery query, NodeManager manager, String sortName, String sortDirection) {
        StepField sf = query.getStepField(manager.getField(sortName));
        addSortOrder(query, sf, sortDirection);
    }

    /**
     * Add a sort order to a query for a field of the relation manager.
     *
     * @param query - the query to add the sort order to
     * @param role - relation manager (relation role in the mmbase system)
     * @param sortName - name of field to sort on.
     * @param sortDirection - direction of the sort (UP, DOWN)
     */
    public static void addRelationSortOrder(NodeQuery query, RelationManager role, String sortName, String sortDirection) {
        Field field = role.getField(sortName);
        StepField sf = query.createStepField(query.getStep(role.getForwardRole()), field);
        addSortOrder(query, sf, sortDirection);
    }

    /**
     * Add a sort order to a query for a field of the manager.
     *
     * @param query - the query to add the sort order to
     * @param sf - StepField of the sort order
     * @param sortName - name of field to sort on.
     * @param sortDirection - direction of the sort (UP, DOWN)
     */
    public static void addSortOrder(NodeQuery query, StepField sf, String sortDirection) {
        int dir = SortOrder.ORDER_ASCENDING;
        if ("DOWN".equalsIgnoreCase(sortDirection)) {
           dir = SortOrder.ORDER_DESCENDING;
        }
        query.addSortOrder(sf, dir);
    }


    /**
     * Add a constraint to a query
     * The value is matched on equality (exact match).
     * For a string field type the match is case-insensitive.
     *
     * @param query - the query to add the constraint to
     * @param manager - manager of the constraint field
     * @param fieldname - name of field to search with
     * @param value - value to search for in the field
     */
    public static void addEqualConstraint(NodeQuery query, NodeManager manager, String fieldname, String value) {
        FieldValueConstraint constraint = createEqualConstraint(query, manager, fieldname, value);
        addConstraint(query, constraint);
    }

    /**
     * Add a constraint to a query
     * The value is matched on equality (exact match).
     * For a string field type the match is case-insensitive.
     *
     * @param query - the query to add the constraint to
     * @param manager - manager of the constraint field
     * @param fieldname - name of field to search with
     * @param value - value to search for in the field
     */
    public static void addEqualConstraint(Query query, NodeManager manager, String fieldname, String value) {
        FieldValueConstraint constraint = createEqualConstraint(query, manager, fieldname, value);
        addConstraint(query, constraint);
    }

    /**
     * Create a constraint for a query
     * The value is matched on equality (exact match).
     * For a string field type the match is case-insensitive.
     *
     * @param query - the query to add the constraint to
     * @param manager - manager of the constraint field
     * @param fieldname - name of field to search with
     * @param value - value to search for in the field
     * @return constraint which matches a field value
     */
    public static FieldValueConstraint createEqualConstraint(NodeQuery query, NodeManager manager, String fieldname, String value) {
        Field keyField = manager.getField(fieldname);
        FieldValueConstraint constraint = createEqualConstraint(query, keyField, value);
        return constraint;
    }

    /**
     * Create a constraint for a query
     * The value is matched on equality (exact match).
     * For a string field type the match is case-insensitive.
     *
     * @param query - the query to add the constraint to
     * @param manager - manager of the constraint field
     * @param fieldname - name of field to search with
     * @param value - value to search for in the field
     * @return constraint which matches a field value
     */
    public static FieldValueConstraint createEqualConstraint(Query query, NodeManager manager, String fieldname, String value) {
        Field keyField = manager.getField(fieldname);
        FieldValueConstraint constraint = createEqualConstraint(query, keyField, value);
        return constraint;
    }

    /**
     * Add a constraint to a query
     * The value is matched on equality (exact match).
     *
     * @param query - the query to add the constraint to
     * @param manager - manager of the constraint field
     * @param fieldname - name of field to search with
     * @param value - value to search for in the field
     */
    public static void addEqualConstraint(NodeQuery query, NodeManager manager, String fieldname, Integer value) {
        FieldValueConstraint constraint = createEqualConstraint(query, manager, fieldname, value);
        addConstraint(query, constraint);
    }

    /**
     * Add a constraint to a query
     * The value is matched on equality (exact match).
     *
     * @param query - the query to add the constraint to
     * @param manager - manager of the constraint field
     * @param fieldname - name of field to search with
     * @param value - value to search for in the field
     */
    public static void addEqualConstraint(Query query, NodeManager manager, String fieldname, Integer value) {
        FieldValueConstraint constraint = createEqualConstraint(query, manager, fieldname, value);
        addConstraint(query, constraint);
    }

    /**
     * Create a constraint for a query
     * The value is matched on equality (exact match).
     *
     * @param query - the query to add the constraint to
     * @param manager - manager of the constraint field
     * @param fieldname - name of field to search with
     * @param value - value to search for in the field
     * @return constraint which matches a field value
     */
    public static FieldValueConstraint createEqualConstraint(NodeQuery query, NodeManager manager, String fieldname, Integer value) {
        Field keyField = manager.getField(fieldname);
        return createEqualConstraint(query, keyField, value);
    }

    /**
     * Create a constraint for a query
     * The value is matched on equality (exact match).
     *
     * @param query - the query to add the constraint to
     * @param manager - manager of the constraint field
     * @param fieldname - name of field to search with
     * @param value - value to search for in the field
     * @return constraint which matches a field value
     */
    public static FieldValueConstraint createEqualConstraint(Query query, NodeManager manager, String fieldname, Integer value) {
        Field keyField = manager.getField(fieldname);
        return createEqualConstraint(query, keyField, value);
    }

    /**
     * Add a constraint to a query
     * The value is matched on equality (exact match).
     *
     * @param query - the query to add the constraint to
     * @param manager - manager of the constraint field
     * @param fieldname - name of field to search with
     * @param value - value to search for in the field
     */
    public static void addEqualConstraint(NodeQuery query, NodeManager manager, String fieldname, Boolean value) {
        FieldValueConstraint constraint = createEqualConstraint(query, manager, fieldname, value);
        addConstraint(query, constraint);
    }

    /**
     * Add a constraint to a query
     * The value is matched on equality (exact match).
     *
     * @param query - the query to add the constraint to
     * @param manager - manager of the constraint field
     * @param fieldname - name of field to search with
     * @param value - value to search for in the field
     */
    public static void addEqualConstraint(Query query, NodeManager manager, String fieldname, Boolean value) {
        FieldValueConstraint constraint = createEqualConstraint(query, manager, fieldname, value);
        addConstraint(query, constraint);
    }

    /**
     * Create a constraint for a query
     * The value is matched on equality (exact match).
     *
     * @param query - the query to add the constraint to
     * @param manager - manager of the constraint field
     * @param fieldname - name of field to search with
     * @param value - value to search for in the field
     * @return constraint which matches a field value
     */
    public static FieldValueConstraint createEqualConstraint(NodeQuery query, NodeManager manager, String fieldname, Boolean value) {
        Field keyField = manager.getField(fieldname);
        return createEqualConstraint(query, keyField, value);
    }

    /**
     * Create a constraint for a query
     * The value is matched on equality (exact match).
     *
     * @param query - the query to add the constraint to
     * @param manager - manager of the constraint field
     * @param fieldname - name of field to search with
     * @param value - value to search for in the field
     * @return constraint which matches a field value
     */
    public static FieldValueConstraint createEqualConstraint(Query query, NodeManager manager, String fieldname, Boolean value) {
        Field keyField = manager.getField(fieldname);
        return createEqualConstraint(query, keyField, value);
    }

    /**
     * Add a constraint to a query
     * The value is matched on equality (exact match).
     * For a string field type the match is case-insensitive.
     *
     * @param query - the query to add the constraint to
     * @param manager - manager of the constraint field
     * @param fieldname - name of field to search with
     * @param value - value to search for in the field
     */
    public static void addEqualConstraint(NodeQuery query, NodeManager manager, String fieldname, Object value) {
        if (!isEmptyOrWhitespace(fieldname)) {
            if (value instanceof String) {
                addEqualConstraint(query, manager, fieldname, (String) value);
            }
            else if (value instanceof Integer) {
                addEqualConstraint(query, manager, fieldname, (Integer) value);
            }
            else if (value instanceof Boolean) {
                addEqualConstraint(query, manager, fieldname, (Boolean) value);
            }
            else {
                FieldValueConstraint constraint = createEqualConstraint(query, manager, fieldname, value);
                addConstraint(query, constraint);
            }
        }
    }

    /**
     * Add a constraint to a query
     * The value is matched on equality (exact match).
     * For a string field type the match is case-insensitive.
     *
     * @param query - the query to add the constraint to
     * @param manager - manager of the constraint field
     * @param fieldname - name of field to search with
     * @param value - value to search for in the field
     */
    public static void addEqualConstraint(Query query, NodeManager manager, String fieldname, Object value) {
        FieldValueConstraint constraint = createEqualConstraint(query, manager, fieldname, value);
        addConstraint(query, constraint);
    }

    /**
     * Create a constraint for a query
     * The value is matched on equality (exact match).
     * For a string field type the match is case-insensitive.
     *
     * @param query - the query to add the constraint to
     * @param manager - manager of the constraint field
     * @param fieldname - name of field to search with
     * @param value - value to search for in the field
     * @return constraint which matches a field value
     */
    public static FieldValueConstraint createEqualConstraint(NodeQuery query, NodeManager manager, String fieldname, Object value) {
        Field keyField = manager.getField(fieldname);
        return createEqualConstraint(query, keyField, value);
    }

    /**
     * Create a constraint for a query
     * The value is matched on equality (exact match).
     * For a string field type the match is case-insensitive.
     *
     * @param query - the query to add the constraint to
     * @param manager - manager of the constraint field
     * @param fieldname - name of field to search with
     * @param value - value to search for in the field
     * @return constraint which matches a field value
     */
    public static FieldValueConstraint createEqualConstraint(Query query, NodeManager manager, String fieldname, Object value) {
        Field keyField = manager.getField(fieldname);
        return createEqualConstraint(query, keyField, value);
    }

    /**
     * Add a constraint to a query
     * The value is matched on equality (exact match).
     * For a string field type the match is case-insensitive.
     *
     * @param query - the query to add the constraint to
     * @param field - the constraint field
     * @param value - value to search for in the field
     */
    public static  void addEqualConstraint(NodeQuery query, Field field, String value) {
        FieldValueConstraint constraint = createEqualConstraint(query, field, value);
        addConstraint(query, constraint);
    }

    /**
     * Add a constraint to a query
     * The value is matched on equality (exact match).
     * For a string field type the match is case-insensitive.
     *
     * @param query - the query to add the constraint to
     * @param field - the constraint field
     * @param value - value to search for in the field
     */
    public static  void addEqualConstraint(Query query, Field field, String value) {
        FieldValueConstraint constraint = createEqualConstraint(query, field, value);
        addConstraint(query, constraint);
    }

    /**
     * Create a constraint for a query
     * The value is matched on equality (exact match).
     * For a string field type the match is case-insensitive.
     *
     * @param query - the query to add the constraint to
     * @param field - the constraint field
     * @param value - value to search for in the field
     * @return constraint which matches a field value
     */
    public static FieldValueConstraint createEqualConstraint(NodeQuery query, Field field, String value) {
        boolean caseSensitive = false;
        return createEqualConstraint(query, field, value, caseSensitive);
    }

    /**
     * Create a constraint for a query
     * The value is matched on equality (exact match).
     *
     * @param query - the query to add the constraint to
     * @param field - the constraint field
     * @param value - value to search for in the field
     * @param caseSensitive - case sensitivity of the value
     * @return constraint which matches a field value
     */
    public static FieldValueConstraint createEqualConstraint(NodeQuery query, Field field, String value, boolean caseSensitive) {
        FieldValueConstraint constraint = query.createConstraint(query.getStepField(field),
                FieldCompareConstraint.EQUAL, value);
        query.setCaseSensitive(constraint, caseSensitive);
        return constraint;
    }

    /**
     * Create a constraint for a query
     * The value is matched on equality (exact match).
     * For a string field type the match is case-insensitive.
     *
     * @param query - the query to add the constraint to
     * @param field - the constraint field
     * @param value - value to search for in the field
     * @return constraint which matches a field value
     */
    public static FieldValueConstraint createEqualConstraint(Query query, Field field, String value) {
        boolean caseSensitive = false;
        return createEqualConstraint(query, field, value, caseSensitive);
    }

    /**
     * Create a constraint for a query
     * The value is matched on equality (exact match).
     *
     * @param query - the query to add the constraint to
     * @param field - the constraint field
     * @param value - value to search for in the field
     * @param caseSensitive - case sensitivity of the value
     * @return constraint which matches a field value
     */
    public static FieldValueConstraint createEqualConstraint(Query query, Field field, String value, boolean caseSensitive) {
        StepField equalsField = findField(query, field);
        FieldValueConstraint constraint = query.createConstraint(equalsField,
                FieldCompareConstraint.EQUAL, value);
        query.setCaseSensitive(constraint, caseSensitive);
        return constraint;
    }

    /**
     * Add a constraint to a query
     * The value is matched on equality (exact match).
     *
     * @param query - the query to add the constraint to
     * @param field - the constraint field
     * @param value - value to search for in the field
     */
    public static  void addEqualConstraint(NodeQuery query, Field field, Integer value) {
        FieldValueConstraint constraint = createEqualConstraint(query, field, value);
        addConstraint(query, constraint);
    }

    /**
     * Add a constraint to a query
     * The value is matched on equality (exact match).
     *
     * @param query - the query to add the constraint to
     * @param field - the constraint field
     * @param value - value to search for in the field
     */
    public static  void addEqualConstraint(Query query, Field field, Integer value) {
        FieldValueConstraint constraint = createEqualConstraint(query, field, value);
        addConstraint(query, constraint);
    }

    /**
     * Create a constraint for a query
     * The value is matched on equality (exact match).
     *
     * @param query - the query to add the constraint to
     * @param field - the constraint field
     * @param value - value to search for in the field
     * @return constraint which matches a field value
     */
    public static FieldValueConstraint createEqualConstraint(NodeQuery query, Field field, Integer value) {
        FieldValueConstraint constraint = query.createConstraint(query.getStepField(field),
                FieldCompareConstraint.EQUAL, value);
        return constraint;
    }

    /**
     * Create a constraint for a query
     * The value is matched on equality (exact match).
     *
     * @param query - the query to add the constraint to
     * @param field - the constraint field
     * @param value - value to search for in the field
     * @return constraint which matches a field value
     */
    public static FieldValueConstraint createEqualConstraint(Query query, Field field, Integer value) {
        StepField equalsField = findField(query, field);
        FieldValueConstraint constraint = query.createConstraint(equalsField,
                FieldCompareConstraint.EQUAL, value);
        return constraint;
    }

    /**
     * Add a constraint to a query
     * The value is matched on equality (exact match).
     *
     * @param query - the query to add the constraint to
     * @param field - the constraint field
     * @param value - value to search for in the field
     */
    public static  void addEqualConstraint(NodeQuery query, Field field, Boolean value) {
        FieldValueConstraint constraint = createEqualConstraint(query, field, value);
        addConstraint(query, constraint);
    }

    /**
     * Add a constraint to a query
     * The value is matched on equality (exact match).
     *
     * @param query - the query to add the constraint to
     * @param field - the constraint field
     * @param value - value to search for in the field
     */
    public static  void addEqualConstraint(Query query, Field field, Boolean value) {
        FieldValueConstraint constraint = createEqualConstraint(query, field, value);
        addConstraint(query, constraint);
    }

    /**
     * Create a constraint for a query
     * The value is matched on equality (exact match).
     *
     * @param query - the query to add the constraint to
     * @param field - the constraint field
     * @param value - value to search for in the field
     * @return constraint which matches a field value
     */
    public static FieldValueConstraint createEqualConstraint(NodeQuery query, Field field, Boolean value) {
        FieldValueConstraint constraint = query.createConstraint(query.getStepField(field),
                FieldCompareConstraint.EQUAL, value);
        return constraint;
    }

    /**
     * Create a constraint for a query
     * The value is matched on equality (exact match).
     *
     * @param query - the query to add the constraint to
     * @param field - the constraint field
     * @param value - value to search for in the field
     * @return constraint which matches a field value
     */
    public static FieldValueConstraint createEqualConstraint(Query query, Field field, Boolean value) {
        StepField equalsField = findField(query, field);
        FieldValueConstraint constraint = query.createConstraint(equalsField,
                FieldCompareConstraint.EQUAL, value);
        return constraint;
    }

    /**
     * Add a constraint to a query
     * The value is matched on equality (exact match).
     * For a string field type the match is case-insensitive.
     *
     * @param query - the query to add the constraint to
     * @param field - the constraint field
     * @param value - value to search for in the field
     */
    public static  void addEqualConstraint(NodeQuery query, Field field, Object value) {
        FieldValueConstraint constraint = createEqualConstraint(query, field, value);
        addConstraint(query, constraint);
    }

    /**
     * Add a constraint to a query
     * The value is matched on equality (exact match).
     * For a string field type the match is case-insensitive.
     *
     * @param query - the query to add the constraint to
     * @param field - the constraint field
     * @param value - value to search for in the field
     */
    public static  void addEqualConstraint(Query query, Field field, Object value) {
        FieldValueConstraint constraint = createEqualConstraint(query, field, value);
        addConstraint(query, constraint);
    }

    /**
     * Create a constraint for a query
     * The value is matched on equality (exact match).
     * For a string field type the match is case-insensitive.
     *
     * @param query - the query to add the constraint to
     * @param field - the constraint field
     * @param value - value to search for in the field
     * @return constraint which matches a field value
     */
    public static FieldValueConstraint createEqualConstraint(NodeQuery query, Field field, Object value) {
        FieldValueConstraint constraint = query.createConstraint(query.getStepField(field),
                FieldCompareConstraint.EQUAL, value);
        return constraint;
    }

    /**
     * Create a constraint for a query
     * The value is matched on equality (exact match).
     * For a string field type the match is case-insensitive.
     *
     * @param query - the query to add the constraint to
     * @param field - the constraint field
     * @param value - value to search for in the field
     * @return constraint which matches a field value
     */
    public static FieldValueConstraint createEqualConstraint(Query query, Field field, Object value) {
        StepField equalsField = findField(query, field);
        FieldValueConstraint constraint = query.createConstraint(equalsField,
                FieldCompareConstraint.EQUAL, value);
        return constraint;
    }

    /**
     * Find a step field in a query based on a bridge field
     *
     * @param query - the query
     * @param field - the bridge field which belongs to a node manager
     * @return a step field in the query
     */
    public static StepField findField(Query query, Field field) {
        StepField equalsField = null;
        Iterator fields = query.getFields().iterator();
        while(fields.hasNext()) {
            StepField stepField = (StepField) fields.next();
            if (stepField.getStep().getTableName().equals(field.getNodeManager().getName())) {
                if (stepField.getFieldName().equals(field.getName())) {
                    equalsField = stepField;
                }
            }
        }
        if (equalsField == null) {
            Step equalsStep = query.getStep(field.getNodeManager().getName());
            if (equalsStep == null) {
                throw new IllegalArgumentException("Step " + field.getNodeManager().getName() + " not found in query");

            }
            equalsField = query.createStepField(equalsStep, field);
        }
        if (equalsField == null) {
            throw new IllegalArgumentException("Field " + field.getName() + " not found in query");
        }

        return equalsField;
    }

    /**
     * Add a constraint to a query
     * The value is matched on likelihood (wildcard % match).
     * For a string field type the match is case-insensitive.
     *
     * @param query - the query to add the constraint to
     * @param field - the constraint field
     * @param value - value to search for in the field
     */
    public static  void addLikeConstraint(NodeQuery query, Field field, String value) {
        FieldValueConstraint constraint = createLikeConstraint(query, field, value);
        addConstraint(query, constraint);
    }

    /**
     * Create a constraint for a query
     * The value is matched on likelihood (wildcard % match).
     * For a string field type the match is case-insensitive.
     *
     * @param query - the query to add the constraint to
     * @param field - the constraint field
     * @param value - value to search for in the field
     * @return constraint which matches a field value
     */
    public static FieldValueConstraint createLikeConstraint(NodeQuery query, Field field, String value) {
        StepField stepField = query.getStepField(field);
        return createLikeConstraint(query, stepField, value);
    }

    /**
     * Add a constraint to a query
     * The value is matched on likelihood (wildcard % match).
     * For a string field type the match is case-insensitive.
     *
     * @param query - the query to add the constraint to
     * @param field - the constraint field
     * @param value - value to search for in the field
     */
    public static  void addLikeConstraint(Query query, Field field, String value) {
        FieldValueConstraint constraint = createLikeConstraint(query, field, value);
        addConstraint(query, constraint);
    }

    /**
     * Create a constraint for a query
     * The value is matched on likelihood (wildcard % match).
     * For a string field type the match is case-insensitive.
     *
     * @param query - the query to add the constraint to
     * @param field - the constraint field
     * @param value - value to search for in the field
     * @return constraint which matches a field value
     */
    public static FieldValueConstraint createLikeConstraint(Query query, Field field, String value) {
        StepField stepField = findField(query, field);
        return createLikeConstraint(query, stepField, value);
    }

    /**
     * Create a constraint for a query
     * The value is matched on likelihood (wildcard % match).
     * For a string field type the match is case-insensitive.
     *
     * @param query - the query to add the constraint to
     * @param field - the constraint field
     * @param value - value to search for in the field
     * @return constraint which matches a field value
     */
    public static FieldValueConstraint createLikeConstraint(Query query, StepField stepField, String value) {
        FieldValueConstraint constraint = query.createConstraint(stepField,
                FieldCompareConstraint.LIKE, "%" + value + "%");
        query.setCaseSensitive(constraint, false);
        return constraint;
    }

    /**
     * Add a date and time constraint to a query where the value is between now
     * and the days passed in. The days can be a negative number.
     *
     * @param query - the query to add the constraint to
     * @param manager - manager of the constraint field
     * @param fieldname - name of field to search with
     * @param daysToCompare - value to search for in the field
     */
    public static  void addDayConstraint(NodeQuery query, NodeManager manager, String fieldname,
            String daysToCompare) {

        if (!isEmptyOrWhitespace(daysToCompare) && !daysToCompare.equals("0")
                && daysToCompare.matches("\\-?\\d+")) {

            Field field = manager.getField(fieldname);

            long now = (System.currentTimeMillis());

            long msecondsToCompare = (1000 * 60 * 60 * 24 * Long.parseLong(daysToCompare));
            long date = now + msecondsToCompare;

            long compareField1 = (msecondsToCompare < 0) ? date : now;
            long compareField2 = (msecondsToCompare < 0) ? now : date;

            addDatetimeConstraint(query, field, compareField1, compareField2);
        }
    }

    /**
     * Add a date and time constraint to a query
     *
     * @param query - the query to add the constraint to
     * @param field - the constraint field
     * @param from - from value to search for in the field
     * @param to - to value to search for in the field
     */
    public static void addDatetimeConstraint(NodeQuery query, Field field, long from, long to) {
        FieldValueBetweenConstraint constraint = createDatetimeConstraint(query, field, from, to);
        addConstraint(query, constraint);
    }

    /**
     * Create a date and time constraint for a query
     * The value is matched on likelihood (wildcard % match).
     * For a string field type the match is case-insensitive.
     *
     * @param query - the query to add the constraint to
     * @param field - the constraint field
     * @param from - from value to search for in the field
     * @param to - to value to search for in the field
     * @return constraint which matches a field value
     */
    public static FieldValueBetweenConstraint createDatetimeConstraint(NodeQuery query, Field field, long from, long to) {
        FieldValueBetweenConstraint constraint = null;
        if (field.getType() == Field.TYPE_DATETIME) {
            constraint = query.createConstraint(query.getStepField(field),
                    new Date(from), new Date(to));
        }
        else {
            constraint = query.createConstraint(query.getStepField(field),
                    new Long(from), new Long(to));
        }
        return constraint;
    }

    /**
     * Limit the result set of the query.
     * Note: converting a query with a limit to a counted query
     * {@link org.mmbase.bridge.util.Queries#count(Query)} will only limit the
     * result set for that query A result set is than always one.
     * @param query - the query to add the constraint to
     * @param offset - the offset where the result set should start
     * @param maxNumber - the maximum number of results which are allowed to return
     */
    public static void addLimitConstraint(NodeQuery query, int offset, int maxNumber) {
        if (offset > 0) {
            query.setOffset(offset);
        }
        if (maxNumber > 0) {
            query.setMaxNumber(maxNumber);
        }
    }

    /**
     * Add a constraint to the query which limits the node types of the nodes in the result
     * @param query - the query to add the constraint to
     * @param types - names of node managers
     */
    public static void addTypeConstraints(NodeQuery query, List types) {
        FieldValueInConstraint constraint = createTypeConstraints(query, types);
        addConstraint(query, constraint);
    }

    /**
     * Create a constraint for the query which limits the node types of the nodes in the result
     * @param query - the query to add the constraint to
     * @param types - names of node managers
     * @return constraint with node types
     */
    public static FieldValueInConstraint createTypeConstraints(NodeQuery query, List types) {
        Cloud cloud = query.getCloud();
        SortedSet set = new TreeSet();
        for (Iterator iter = types.iterator(); iter.hasNext();) {
            String type = (String) iter.next();
            NodeManager manager = cloud.getNodeManager(type);
            set.add(new Integer(manager.getNumber()));
        }
        Field field = query.getNodeManager().getField("otype");
        return createInConstraint(query, field, set);
    }

    /**
     * Add a constraint to the query which limits the nodes in the result based on the number
     * @param query - the query to add the constraint to
     * @param field - the constraint field
     * @param nodes - node which should be used for the constraint
     */
    public static void addNodesConstraints(Query query, Field field, NodeList nodes) {
        SortedSet set = createNodeNumberSet(nodes);
        addInConstraint(query, field, set);
    }

    /**
     * Create a set with the node numbers of the list of nodes
     * @param nodes - list of nodes
     * @return Set sorted on node number
     */
    public static SortedSet createNodeNumberSet(NodeList nodes) {
        SortedSet set = new TreeSet();
        for (Iterator iter = nodes.iterator(); iter.hasNext();) {
            Node node = (Node) iter.next();
            set.add(new Integer(node.getNumber()));
        }
        return set;
    }

    /**
     * Add a constraint to the query which limits the values in the result based on the set
     * @param query - the query to add the constraint to
     * @param field - the constraint field
     * @param set - set with possible values
     */
    public static void addInConstraint(Query query, Field field, SortedSet set) {
        FieldValueInConstraint constraint = createInConstraint(query, field, set);
        addConstraint(query, constraint);
    }

    /**
     * Create a constraint for the query which limits the values in the result based on the set
     * @param query - the query to add the constraint to
     * @param field - the constraint field
     * @param set - set with possible values
     * @return in-constraint
     */
    public static FieldValueInConstraint createInConstraint(Query query, Field field, SortedSet set) {
        query.getStep(field.getNodeManager().getName());
        StepField stepfield = findField(query, field);
        FieldValueInConstraint constraint = query.createConstraint(stepfield, set);
        return constraint;
    }

    /**
     * Add a constraint to the query. When there is already a constraint then
     * the constraint is added with an AND operator
     * @param query - the query to add the constraint to
     * @param constraint - the constraint
     */
    public static void addConstraint(Query query, Constraint constraint) {
        addConstraint(query, constraint, CompositeConstraint.LOGICAL_AND);
    }

    /**
     * Create a AND composite constraint for the query
     * @param query - the query to add the constraint to
     * @param first - first constraint
     * @param second - second constraint
     * @return composite constraint
     */
    public static Constraint createANDConstraint(Query query, Constraint first, Constraint second) {
        return createLogicalConstraint(query, first, second, CompositeConstraint.LOGICAL_OR);
    }

    /**
     * Add a constraint to the query. When there is already a constraint then
     * the constraint is added with an OR operator
     * @param query - the query to add the constraint to
     * @param constraint - the constraint
     */
    public static void addORConstraint(Query query, Constraint constraint) {
        addConstraint(query, constraint, CompositeConstraint.LOGICAL_OR);
    }

    /**
     * Create a OR composite constraint for the query
     * @param query - the query to add the constraint to
     * @param first - first constraint
     * @param second - second constraint
     * @return composite constraint
     */
    public static Constraint addORConstraint(Query query, Constraint first, Constraint second) {
        return createLogicalConstraint(query, first, second, CompositeConstraint.LOGICAL_OR);
    }

    /**
     * Add a constraint to the query. When there is already a constraint then
     * the constraint is added with the operator specified
     * @param query - the query to add the constraint to
     * @param constraint - the constraint
     * @param operator - the logical operator (CompositeConstraint.LOGICAL_OR, CompositeConstraint.LOGICAL_AND)
     */
    public static void addConstraint(Query query, Constraint constraint, int operator) {
        if (query.getConstraint() == null) {
            query.setConstraint(constraint);
        }
        else {
            CompositeConstraint newc = createLogicalConstraint(query, query.getConstraint(), constraint, operator);
            query.setConstraint(newc);
        }
    }

    /**
     * Create a composite constraint for the query
     * @param query - the query to add the constraint to
     * @param first - first constraint
     * @param second - second constraint
     * @param operator - the logical operator (CompositeConstraint.LOGICAL_OR, CompositeConstraint.LOGICAL_AND)
     * @return composite constraint
     */
    public static CompositeConstraint createLogicalConstraint(Query query, Constraint first,
                                                                Constraint second, int operator) {
        CompositeConstraint newc = query.createConstraint(first, operator, second);
        return newc;
    }

    /**
     * is Empty Or Whitespace.String
     *
     * @param str String to check emptiness
     * @return boolean is it empty
     */
    public static boolean isEmptyOrWhitespace(String str) {
        return (str == null) || "".equals(str.trim());
    }

}
