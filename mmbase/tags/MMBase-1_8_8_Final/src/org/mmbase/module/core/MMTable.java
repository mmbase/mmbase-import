/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

import java.util.*;

import org.mmbase.core.*;
import org.mmbase.core.util.StorageConnector;
import org.mmbase.module.corebuilders.*;
import org.mmbase.storage.*;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.util.functions.FunctionProvider;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * MMTable is the base abstraction of a cloud of objects stored in one database table,
 * essentially a cloud of objects of the same type.
 * It provides a starting point for MMObjectBuilder by defining a scope - the database table -
 * and basic functionality to create the table and query properties such as its size.
 * This class does not contain actual management of nodes (this is left to MMOBjectBuilder).
 *
 * @author Daniel Ockeloen
 * @author Pierre van Rooden (javadoc)
 * @version $Id$
 */
public abstract class MMTable extends FunctionProvider {

    private static final Logger log = Logging.getLoggerInstance(MMTable.class);

    /**
     * The MMBase module that this table belongs to
     */
    protected MMBase mmb;

    /**
     * The table name
     */
    protected String tableName;

    /**
     * Maximum number of nodes to return on a query (-1 means no limit, and is also the default)
     */
    protected int maxNodesFromQuery = -1;

    // indices for the storage layer
    private Map indices = new HashMap();

    // link to the storage layer
    protected StorageConnector storageConnector;

    /**
     * Empty constructor.
     */
    public MMTable() {
    }

    /**
     * Set the MMBase object, and retrieve the storage layer.
     * @param m the MMBase object to set as owner of this builder
     */
    public void setMMBase(MMBase m) {
        mmb = m;
    }

    /**
     * Return the MMBase object
     * @since 1.7
     */
    public MMBase getMMBase() {
        return mmb;
    }

    /**
     * Set tablename of the builder. Should be used to initialize a MMTable object before calling init().
     * @param tableName the name of the table
     */
    public void setTableName(String tableName) {
        this.tableName=tableName;
    }

    /**
     * Retrieve the table name (without the clouds' base name)
     * @return a <code>String</code> containing the table name
     * @since MMBase-1.7
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Retrieve the full table name (including the clouds' base name)
     * @return a <code>String</code> containing the full table name
     * @since MMBase-1.7
     */
    public String getFullTableName() {
        return mmb.baseName + "_" + tableName;
    }

    /**
     * Determine the number of objects in this table.
     * @return The number of entries in the table.
     */
    public int size() {
        return storageConnector.size();
    }

    /**
     * Check whether the table is accessible.
     * In general, this means the table does not exist. Please note that this routine may
     * also return false if the table is inaccessible due to insufficient rights.
     * @return <code>true</code> if the table is accessible, <code>false</code> otherwise.
     */
    public boolean created() {
        return storageConnector.created();
    }

    public StorageConnector getStorageConnector() {
        return storageConnector;
    }

/*
    public Map getIndices() {
        return storageConnector.getIndices();
    }

    public void addIndex(Index index) {
        storageConnector.addIndex(index);
    }

    public void addIndices(List indexList) {
        storageConnector.addIndices(indexList);
    }

    public Index getIndex(String key) {
        return storageConnector.getIndex(key);
    }

    public Index createIndex(String key) {
        return storageConnector.createIndex(key);
    }

    public void addToIndex(String key, Field field) {
        createIndex(key).add(field);
    }

    public void removeFromIndex(String key, Field field) {
        storageConnector.removeFromIndex(key, field);
    }

    public boolean isInIndex(String key, Field field) {
        storageConnector.isInIndex(key, field);
    }
*/

    // retrieve nodes
    /**
     * Retrieves a node based on it's number (a unique key).
     * @todo when something goes wrong, the method currently catches the exception and returns null.
     *       It should actually throw a NotFoundException instead.
     * @param number The number of the node to search for
     * @param useCache If true, the node is retrieved from the node cache if possible.
     * @return <code>null</code> if the node does not exist, the key is invalid,or a
     *       <code>MMObjectNode</code> containing the contents of the requested node.
     */
    public MMObjectNode getNode(final int number, boolean useCache) {
        try {
            return storageConnector.getNode(number, useCache);
        } catch(IllegalArgumentException iae) {
            log.warn(iae.getMessage());
            if (log.isDebugEnabled()) {
                log.debug(iae);
            }
            return null;
        } catch(StorageNotFoundException se) {
            return null;
        } catch(StorageException se) {
            log.error(se.getMessage(), se);
            return null;
        }
    }

    /**
     * Retrieves an object's type. If necessary, the type is added to the cache.
     * @todo when something goes wrong, the method currently catches the exception and returns -1.
     *       It should actually throw a NotFoundException instead.
     * @param number The number of the node to search for
     * @return an <code>int</code> value which is the object type (otype) of the node.
     */
    public int getNodeType(int number) {
        try {
            return storageConnector.getNodeType(number);
        } catch(StorageException se) {
            log.error(Logging.stackTrace(se));
            return -1;
        }
    }

    // Search and query methods on a table

    /**
     * Convert virtual nodes to real nodes based on their otype
     *
     * Normally a multirelations-search will return virtual nodes. These nodes
     * will only contain values which where specified in the field-vector.
     * This method will make real nodes of those virtual nodes.
     *
     * @param virtuals containing virtual nodes
     * @return List containing real nodes, directly from this Builders
     * @since MMBase-1.6.2
     */
    protected List getNodes(Collection virtuals)  {
        List result = new ArrayList();
        try {
            result = storageConnector.getNodes(virtuals);
        } catch (SearchQueryException sqe) {
            log.error(sqe.getMessage() + Logging.stackTrace(sqe));
        }
        return result;
    }

    /**
     * Counts number of nodes matching a specified constraint.
     *
     * @param where The constraint, can be a SQL where-clause, a MMNODE
     *        expression or an altavista-formatted expression.
     * @return The number of nodes, or -1 when failing to retrieve the data.
     * @deprecated Use {@link #count(NodeSearchQuery) count(NodeSearchQuery)}
     *             instead.
     */
    public int count(String where) {
        // In order to support this method:
        // - Exceptions of type SearchQueryExceptions are caught.
        int result = -1;
        NodeSearchQuery query = storageConnector.getSearchQuery(where);
        try {
            result = count(query);
        } catch (SearchQueryException e) {
            log.error(e);
        }
        return result;
    }

    /**
     * Counts number of nodes matching a specified constraint.
     * The constraint is specified by a query that selects nodes of
     * a specified type, which must be the nodetype corresponding
     * to this builder.
     *
     * @param query The query.
     * @return The number of nodes.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     * @throws SearchQueryException when failing to retrieve the data.
     * @since MMBase-1.7
     */
    public int count(NodeSearchQuery query) throws SearchQueryException {
        return storageConnector.count(query);
    }

    /**
     * Returns nodes matching a specified constraint.
     * The constraint is specified by a query that selects nodes of
     * a specified type, which must be the nodetype corresponding
     * to this builder.
     *
     * @param query The query.
     * @return The nodes.
     * @throws IllegalArgumentException When the nodetype specified
     *         by the query is not the nodetype corresponding to this builder.
     * @since MMBase-1.7
     */
    public List getNodes(NodeSearchQuery query) throws SearchQueryException {
        return storageConnector.getNodes(query);
    }

    // most of those below are deprecated

    /**
     * Build a set command string from a set nodes
     * @param nodes Vector containg the nodes to put in the set
     * @param fieldName fieldname whose values should be put in the set
     * @return a comma-seperated list of values, as a <code>String</code>
     * @deprecated should be moved?
     */
    /*
    public String buildSet(Vector nodes, String fieldName) {
        StringBuffer result = new StringBuffer("(");
        Enumeration enumeration = nodes.elements();
        MMObjectNode node;

        while (enumeration.hasMoreElements()) {
            node = (MMObjectNode)enumeration.nextElement();

            if(enumeration.hasMoreElements()) {
                result.append(node.getValue(fieldName)).append(", ");
            } else {
                result.append(node.getValue(fieldName));
            }

        }
        result.append(')');
        return result.toString();
    }
    */

    /**
     * Enumerate all the objects that match the searchkeys
     * @param where scan expression that the objects need to fulfill
     * @return an <code>Enumeration</code> containing all the objects that apply.
     * @deprecated Use {@link #getNodes(NodeSearchQuery)
     *             getNodes(NodeSearchQuery} to perform a node search.
     */
    public Enumeration search(String where) {
        return searchVector(where).elements();
    }

    /**
     * Enumerate all the objects that match the searchkeys
     * @param where where clause that the objects need to fulfill
     * @param sort  order in which to return the objects
     * @return an <code>Enumeration</code> containing all the objects that apply.
     * @deprecated Use {@link #getNodes(NodeSearchQuery)
     *             getNodes(NodeSearchQuery} to perform a node search.
     */
    /*
    public Enumeration search(String where, String sort) {
        return searchVector(where, sort).elements();
    }
    */

    /**
     * Enumerate all the objects that match the searchkeys
     * @param where where clause that the objects need to fulfill
     * @param sort  order in which to return the objects
     * @param direction sorts ascending if <code>true</code>, descending if <code>false</code>.
     *        Only applies if a sorted order is given.
     * @return an <code>Enumeration</code> containing all the objects that apply.
     * @deprecated Use {@link #getNodes(NodeSearchQuery)
     *             getNodes(NodeSearchQuery} to perform a node search.
     */
    /*
    public Enumeration search(String where, String sort, boolean direction) {
        return searchVector(where, sort, direction).elements();
    }
    */

    /**
     * Returns a vector containing all the objects that match the searchkeys
     * @param where scan expression that the objects need to fulfill
     * @return a vector containing all the objects that apply.
     * @deprecated Use {@link #getNodes(NodeSearchQuery)
     *             getNodes(NodeSearchQuery} to perform a node search.
     */
    public Vector searchVector(String where) {
        // In order to support this method:
        // - Exceptions of type SearchQueryExceptions are caught.
        // - The result is converted to a vector.
        Vector result = new Vector();
        NodeSearchQuery query = storageConnector.getSearchQuery(where);
        try {
            List nodes = getNodes(query);
            result.addAll(nodes);
        } catch (SearchQueryException e) {
            log.error(e);
        }
        return result;
    }

    /**
     * Returns all the nodes from the builder.
     * @return The nodes.
     */
    public List getNodes() {
        try {
            List nodes = storageConnector.getNodes();
            if (nodes != null) {
                return nodes;
            }
        } catch (SearchQueryException e) {
            log.error(e);
        }
        return new ArrayList();
    }

    /**
     * Returns a vector containing all the objects that match the searchkeys
     * @param where       where clause that the objects need to fulfill
     * @param sorted      a comma separated list of field names on wich the
     *                    returned list should be sorted
     * @return a vector containing all the objects that apply.
     * @deprecated Use {@link #getNodes(NodeSearchQuery)
     *             getNodes(NodeSearchQuery} to perform a node search.
     */
    /*
    public Vector searchVector(String where, String sorted) {
        return searchVector(where, sorted, true);
    }
    */

    /**
     * Returns a vector containing all the objects that match the searchkeys
     * @param where where clause that the objects need to fulfill
     * @param sorted order in which to return the objects
     * @param direction sorts ascending if <code>true</code>, descending if <code>false</code>.
     *        Only applies if a sorted order is given.
     * @return a vector containing all the objects that apply.
     * @deprecated Use {@link #getNodes(NodeSearchQuery)
     *             getNodes(NodeSearchQuery} to perform a node search.
     */
    /*
    public Vector searchVector(String where, String sorted, boolean direction) {
        String directions = (direction? "UP": "DOWN");
        return searchVector(where, sorted, directions);
    }
    */

    /**
     * Returns a vector containing all the objects that match the searchkeys in
     * a given order.
     *
     * @param where       where clause that the objects need to fulfill
     * @param sorted      a comma separated list of field names on wich the
     *                    returned list should be sorted
     * @param directions  A comma separated list of the values indicating wether
     *                    to sort up (ascending) or down (descending) on the
     *                    corresponding field in the <code>sorted</code>
     *                    parameter or <code>null</code> if sorting on all
     *                    fields should be up.
     *                    The value DOWN (case insensitive) indicates
     *                    that sorting on the corresponding field should be
     *                    down, all other values (including the
     *                    empty value) indicate that sorting on the
     *                    corresponding field should be up.
     *                    If the number of values found in this parameter are
     *                    less than the number of fields in the
     *                    <code>sorted</code> parameter, all fields that
     *                    don't have a corresponding direction value are
     *                    sorted according to the last specified direction
     *                    value.
     * @return            a vector containing all the objects that apply in the
     *                    requested order
     * @deprecated Use {@link #getNodes(NodeSearchQuery)
     *             getNodes(NodeSearchQuery} to perform a node search.
     */
    /*
    public Vector searchVector(String where, String sorted, String directions) {
        // In order to support this method:
        // - Exceptions of type SearchQueryExceptions are caught.
        // - The result is converted to a vector.
        Vector result = new Vector();
        NodeSearchQuery query = getSearchQuery(where, sorted, directions);
        try {
            List nodes = getNodes(query);
            result.addAll(nodes);
        } catch (SearchQueryException e) {
            log.error(e);
        }
        return result;
    }
    */

    /**
     * As searchVector. Differences are:
     * - Throws exception on SQL errors
     * @since MMBase-1.6
     * @deprecated Use {@link #getNodes(NodeSearchQuery)
     *             getNodes(NodeSearchQuery} to perform a node search.
     */
    /*
    public List searchList(String where) {
        // In order to support this method:
        // - Exceptions of type SearchQueryExceptions are wrapped
        //   inside an RuntimeException.
        NodeSearchQuery query = getSearchQuery(where);
        try {
            return getNodes(query);
        } catch (SearchQueryException e) {
            throw new RuntimeException(e);
        }
    }
    */

    /**
     * As searchVector
     * But
     * - throws Exception on error
     * - returns List
     *
     * @param where Constraint, represented by scan MMNODE expression,
     *        AltaVista format or SQL "where"-clause.
     * @param sorted Comma-separated list of names of fields to sort on.
     * @param directions Comma-separated list of sorting directions ("UP"
     *        or "DOWN") of the fields to sort on.
     * @since MMBase-1.6
     * @deprecated Use {@link #getNodes(NodeSearchQuery)
     *             getNodes(NodeSearchQuery} to perform a node search.
     */
    /*
    public List searchList(String where, String sorted, String directions) {
        // In order to support this method:
        // - Exceptions of type SearchQueryExceptions are wrapped
        //   inside an RuntimeException.
        NodeSearchQuery query = getSearchQuery(where, sorted, directions);
        try {
            return getNodes(query);
        } catch (SearchQueryException e) {
            if (log.isDebugEnabled()) {
                log.debug(e + Logging.stackTrace(e));
            }
            throw new RuntimeException(e);
        }
    }
    */

    /**
     * Creates search query that retrieves nodes matching a specified
     * constraint.
     *
     * @param where The constraint, can be a SQL where-clause, a MMNODE
     *        expression, an altavista-formatted expression, empty or
     *        <code>null</code>.
     * @return The query.
     * @since MMBase-1.7
     */
    /*
    NodeSearchQuery getSearchQuery(String where) {
        NodeSearchQuery query;

        if (where != null && where.startsWith("MMNODE ")) {
            // MMNODE expression.
            query = storageConnector.convertMMNodeSearch2Query(where);
        } else {
            query = new NodeSearchQuery((MMObjectBuilder)this);
            QueryConvertor.setConstraint(query, where);
        }

        return query;
    }
    */

    /**
     * Creates search query that retrieves a sorted list of nodes,
     * matching a specified constraint.
     *
     * @param where The constraint, can be a SQL where-clause, a MMNODE
     *        expression or an altavista-formatted expression.
     * @param sorted Comma-separated list of names of fields to sort on.
     * @param directions Comma-separated list of sorting directions ("UP"
     *        or "DOWN") of the fields to sort on.
     *        If the number of sorting directions is less than the number of
     *        fields to sort on, the last specified direction is applied to
     *        the remaining fields.
     * @since MMBase-1.7
     */
    /*
    NodeSearchQuery getSearchQuery(String where, String sorted, String directions) {
        NodeSearchQuery query = getSearchQuery(where);
        if (directions == null) {
            directions = "";
        }
        StringTokenizer sortedTokenizer = new StringTokenizer(sorted, ",");
        StringTokenizer directionsTokenizer = new StringTokenizer(directions, ",");

        String direction = "UP";
        while (sortedTokenizer.hasMoreElements()) {
            String fieldName = sortedTokenizer.nextToken().trim();
            CoreField coreField = getField(fieldName);
            if (coreField == null) {
                throw new IllegalArgumentException(
                "Not a known field of builder " + getTableName()
                + ": '" + fieldName + "'");
            }
            StepField field = query.getField(coreField);
            BasicSortOrder sortOrder = query.addSortOrder(field);
            if (directionsTokenizer.hasMoreElements()) {
                direction = directionsTokenizer.nextToken().trim();
            }
            if (direction.equalsIgnoreCase("DOWN")) {
                sortOrder.setDirection(SortOrder.ORDER_DESCENDING);
            } else {
                sortOrder.setDirection(SortOrder.ORDER_ASCENDING);
            }
        }
        return query;
    }
    */

    /**
     * Adds nodenumbers to be included to query retrieving nodes.
     *
     * @param query The query.
     * @param nodeNumbers Comma-separated list of nodenumbers.
     * @since MMBase-1.7
     */
    /*
    void addNodesToQuery(NodeSearchQuery query, String nodeNumbers) {
        BasicStep step = (BasicStep) query.getSteps().get(0);
        StringTokenizer st = new StringTokenizer(nodeNumbers, ",");
        while (st.hasMoreTokens()) {
            String str = st.nextToken().trim();
            int nodeNumber = Integer.parseInt(str);
            step.addNode(nodeNumber);
        }
    }
    */

}
