/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.core.util;

import java.util.*;

import org.mmbase.bridge.Field;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.cache.*;
import org.mmbase.core.CoreField;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.core.*;
import org.mmbase.storage.*;
import org.mmbase.storage.util.Index;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.util.QueryConvertor;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * A StorageConnector object is associated with a specific builder.
 * It provides methods for loading nodes from the cloud (using the search query classes),
 * either indivbidual nodes or nodelists.
 *
 * @since MMBase-1.8
 * @author Pierre van Rooden
 * @version $Id$
 */
public class StorageConnector {

    /**
     * Max length of a query, informix = 32.0000 so we assume a bit less for other databases (???).
     */
    private static final int MAX_QUERY_SIZE = 20000;

    private static final Logger log = Logging.getLoggerInstance(StorageConnector.class);

    /**
     * Determines whether the cache need be refreshed.
     * Seems useless, as this value is never changed (always true)
     * @see #processSearchResults
     */
    /*
     public static final boolean REPLACE_CACHE = true;
    */

    /**
     * Whenever a list should always return the correct types of nodes
     * old behaviour is not...
     * This is needed, when you want for example use the following code:
     * <pre>
     * MMObjectNode node = MMObjectBuilder.getNode(123);
     * Enumeration relations = node.getRelations("posrel");
     * while(enumeration.hasNext()) {
     *   MMObjectNode posrel = (MMObjectNode) enumeration.getElement();
     *   int pos = posrel.getIntValue("pos");
     * }
     * </pre>
     * When the return of correct node types is the following code has to be used..
     * <pre>
     * MMObjectNode node = MMObjectBuilder.getNode(123);
     * Enumeration relations = node.getRelations("posrel");
     * while(enumeration.hasNext()) {
     *   MMObjectNode posrel = (MMObjectNode) enumeration.getElement();
     *   // next lines is needed when the return of correct nodes is not true
     *   posrel = posrel.parent.getNode(posrel.getNumber());
     *   // when the line above is skipped, the value of pos will always be -1
     *   int pos = posrel.getIntValue("pos");
     * }
     * </pre>
     * Maybe this should be fixed in some otherway,.. but when we want to use the inheritance  you
     * _really_ need this thing turned into true.
     */
    /*
     private static boolean CORRECT_NODE_TYPES = true;
    */

    /**
     * Maximum number of nodes to return on a query (-1 means no limit, and is also the default)
     */
    protected int maxNodesFromQuery = -1;

    /**
     * @javadoc
     */
    protected final MMObjectBuilder builder;

    // indices for the storage layer
    private Map indices = new HashMap();

    /**
     * @javadoc
     */
    public StorageConnector(MMObjectBuilder builder) {
        this.builder = builder;
    }

    /**
     * Determine the number of objects in this table.
     * @return The number of entries in the table.
     */
    public int size() {
        try {
            return builder.getMMBase().getStorageManager().size(builder);
        } catch (StorageException se) {
            log.error(se.getMessage());
            return -1;
        }
    }

    /**
     * Check whether the table is accessible.
     * In general, this means the table does not exist. Please note that this routine may
     * also return false if the table is inaccessible due to insufficient rights.
     * @return <code>true</code> if the table is accessible, <code>false</code> otherwise.
     */
    public boolean created() {
        try {
            return builder.getMMBase().getStorageManager().exists(builder);
        } catch (StorageException se) {
            log.error(se.getMessage() + Logging.stackTrace(se));
            return false;
        }
    }

    public Map getIndices() {
        return indices;
    }

    public void addIndex(Index index) {
        if (index != null && index.getParent() == builder) {
            indices.put(index.getName(),index);
        }
    }

    public void addIndices(List indexList) {
        if (indexList != null ) {
            for (Iterator i = indexList.iterator(); i.hasNext(); ) {
                addIndex((Index)i.next());
            }
        }
    }

    public Index getIndex(String key) {
        return (Index)indices.get(key);
    }

    public synchronized Index createIndex(String key) {
        Index index = getIndex(key);
        if (index == null) {
            index = new Index(builder, key);
            indices.put(key,index);
        }
        return index;
    }

    public void addToIndex(String key, Field field) {
        createIndex(key).add(field);
    }

    public void removeFromIndex(String key, Field field) {
        Index index = createIndex(key);
        if (index != null) {
            index.remove(field);
        }
    }

    public boolean isInIndex(String key, Field field) {
        Index index = getIndex(key);
        return index != null && index.contains(field);
    }



    // retrieve nodes
    /**
     * Retrieves a node based on it's number (a unique key).
     * @todo when something goes wrong, the method currently catches the exception and returns null.
     *       It should actually throw a NotFoundException instead.
     * @param number The number of the node to search for
     * @param useCache If false, a fresh copy is returned.
     * @return <code>null</code> if the node does not exist, the key is invalid,or a
     *       <code>MMObjectNode</code> containing the contents of the requested node.
     */
    public  MMObjectNode getNode(final int number, final boolean useCache) throws StorageException {
        if (log.isDebugEnabled()) {
            log.trace("Getting node with number " + number);
        }
        if (number < 0) {
            throw new IllegalArgumentException("Tried to obtain node from builder '" + builder.getTableName() + "' with an illegal number = " + number);
        }
        MMObjectNode node = null;

        Integer numberValue = new Integer(number);
        // try cache if indicated to do so
        node = builder.getNodeFromCache(numberValue);
        if (node != null) {
            log.trace("Found in cache!");
            if (useCache) {
                return node;
            } else {
                return new MMObjectNode(node);
            }
        }

        MMBase mmb = builder.getMMBase();
        // not in cache. We are going to put it in.
        // retrieve node's objecttype
        MMObjectBuilder nodeBuilder = getBuilderForNode(number);
        // use storage factory if present
        log.debug("Getting node from storage");
        node = mmb.getStorageManager().getNode(nodeBuilder, number);
        // store in cache if indicated to do so
        if (useCache) {
            if (log.isDebugEnabled()) {
                log.debug("Caching node from storage" + node);
            }
            node = builder.safeCache(numberValue, node);
        }
        if (log.isDebugEnabled()) {
            log.debug("Returning " + node);
        }
        if (useCache) {
            return node;
        } else {
            return new MMObjectNode(node);
        }
    }

    private Set warnedBuilders = new HashSet();

    public MMObjectBuilder getBuilderForNode(final int number) {
        MMBase mmb = builder.getMMBase();
        MMObjectBuilder nodeBuilder = builder;
        int nodeType = getNodeType(number);
        if (nodeType < 0) {
            // the node does not exists, which according to javadoc should return null
            throw new StorageNotFoundException("Cannot determine node type of node with number =" + number);
        }
        // if the type is not for the current builder, determine the real builder
        if (nodeType != builder.getNumber()) {
            if (log.isDebugEnabled()) {
                log.debug(" " + nodeType + "!=" + builder.getNumber());
            }
            String builderName = mmb.getTypeDef().getValue(nodeType);
            if (builderName == null) {
                log.error("The nodetype name of node #" + number + " could not be found (nodetype # " + nodeType + "), taking '" + builder.getTableName() + "'");
                builderName = builder.getTableName();
            }
            nodeBuilder = mmb.getBuilder(builderName);
            if (nodeBuilder == null) {
                if (! warnedBuilders.contains(new Integer(nodeType))) {
                    log.warn("Builder " + builderName + "(" + nodeType + ") is not loaded, taking 'object'. (more errors of this kind are logged on debug)");
                    warnedBuilders.add(new Integer(nodeType));
                }
                log.debug("Node #" + number + "'s builder " + builderName + "(" + nodeType + ") is not loaded, taking 'object'.");
                nodeBuilder = mmb.getBuilder("object");
            }
        }
        return nodeBuilder;
    }

    /**
     * Retrieves an object's type. If necessary, the type is added to the cache.
     * @todo when something goes wrong, the method currently catches the exception and returns -1.
     *       It should actually throw a NotFoundException instead.
     * @param number The number of the node to search for
     * @return an <code>int</code> value which is the object type (otype) of the node.
     */
    public int getNodeType(int number) throws StorageException {
        if (number < 0 ) {
            throw new IllegalArgumentException("node number was invalid (" + number + " < 0)" );
        } else {
            return builder.getMMBase().getStorageManager().getNodeType(number);
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
     */
    public List getNodes(Collection virtuals) throws SearchQueryException  {
        List result = new ArrayList();

        int numbersSize = 0;
        NodeSearchQuery query = new NodeSearchQuery(builder);
        BasicStep step = (BasicStep) query.getSteps().get(0);
        List subResult = new ArrayList();

        Iterator i = virtuals.iterator();
        while(i.hasNext()) {
            MMObjectNode node = (MMObjectNode) i.next();

            // check if this node is already in cache
            //Integer number = Integer.valueOf(node.getNumber()); // 1.5 only
            Integer number = new Integer(node.getNumber());
            if(builder.isNodeCached(number)) {
                result.add(builder.getNodeFromCache(number));
                // else seek it with a search on builder in db
            } else {
                numbersSize +=  ("," + number).length();
                subResult.add(number);
                step.addNode(number.intValue());
            }

            if(numbersSize > MAX_QUERY_SIZE) {
                addSubResult(query, subResult, result);
                query = new NodeSearchQuery(builder);
                step = (BasicStep) query.getSteps().get(0);
                numbersSize = 0;
                subResult.clear();
            }
        }

        // now that we have a comma seperated string of numbers, we can
        // the search with a where-clause containing this list
        if(numbersSize > 0) {
            addSubResult(query, subResult, result);
        } // else everything from cache

        // check that we didnt loose any nodes
        assert assertSizes(virtuals, result);

        return result;
    }

    /**
     * @param query Query with nodestep with added nodes.
     * @param subResult List of Integer
     * @param result    List to which the real nodes must be added.
     * @since MMBase-1.8.2
     */
    protected void addSubResult(final NodeSearchQuery query, final List subResult, final List result) throws SearchQueryException {
        List rawNodes = getRawNodes(query, true);
        // convert this list to a map, for easy reference when filling result.
         // would the creation of this Map not somehow be avoidable?
        Map rawMap = new HashMap();
        Iterator i = rawNodes.iterator();
        while (i.hasNext()) {
            MMObjectNode n = (MMObjectNode) i.next();
            //rawMap.put(Integer.valueOf(n.getNumber()), n); // 1.5 only..
            rawMap.put(new Integer(n.getNumber()), n);
        }
        Iterator j = subResult.iterator();
        while (j.hasNext()) {
            Integer n = (Integer) j.next();
            result.add(rawMap.get(n));
        }
    }

    /**
     * @since MMBase-1.8.2
     */
    protected boolean assertSizes(Collection virtuals, Collection result) {
        if (virtuals.size() != result.size()) {
            log.error(" virtuals " + virtuals + " result " + result);
            return false;
        } else {
            return true;
        }
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
     */
    public int count(SearchQuery query) throws SearchQueryException {
        // Test if nodetype corresponds to builder.
        verifyBuilderQuery(query);

        // Wrap in modifiable query, replace fields by one count field.
        ModifiableQuery modifiedQuery = new ModifiableQuery(query);
        Step step = (Step) query.getSteps().get(0);
        CoreField numberField = builder.getField(MMObjectBuilder.FIELD_NUMBER);
        AggregatedField field = new BasicAggregatedField(step, numberField, AggregatedField.AGGREGATION_TYPE_COUNT);
        List newFields = new ArrayList(1);
        newFields.add(field);
        modifiedQuery.setFields(newFields);

        AggregatedResultCache cache = AggregatedResultCache.getCache();

        List results = (List) cache.get(modifiedQuery);
        if (results == null) {
            // Execute query, return result.
            results = builder.getMMBase().getSearchQueryHandler().getNodes(modifiedQuery, new ResultBuilder(builder.getMMBase(), modifiedQuery));
            cache.put(modifiedQuery, results);
        }
        ResultNode result = (ResultNode) results.get(0);
        return result.getIntValue(MMObjectBuilder.FIELD_NUMBER);
    }

    private void verifyBuilderQuery(SearchQuery query) throws SearchQueryException {
        String builderName = null;
        if (query instanceof NodeQuery) {
            builderName = ((NodeQuery)query).getNodeManager().getName();
        } else if (query instanceof NodeSearchQuery) {
            builderName = ((NodeSearchQuery)query).getBuilder().getTableName();
        }
        if (builderName != null && !builderName.equals(builder.getTableName())) {
            throw new IllegalArgumentException("Query passed runs on '" + builderName + "' but was passed to '" + builder.getTableName() + "'");
        }
    }

    /**
     * Returns the Cache which should be used for the result of a certain query. The current
     * implementation only makes the distinction between queries for the 'related nodes caches' and
     * for the 'node list caches'. Multilevel queries are not done here, so are at the moment not
     * anticipated.
     * 
     * It returns a Map rather then a Cache. The idea behind this is that if in the future a
     * query-result can be in more than one cache, a kind of 'chained map' can be returned, to
     * reflect that.
     * @todo Perhaps other usefull parameters like query-duration and query-result could be added
     * (in that case searching a result should certainly returns such a chained map, because then of
     * course you don't have those).
     */
    protected Map getCache(SearchQuery query) {
        List steps = query.getSteps();
        if (steps.size() == 3) {
            Step step0 = (Step) steps.get(0);
            Collection nodes = step0.getNodes();
            if (nodes != null && nodes.size() == 1) {
                return RelatedNodesCache.getCache();
            }
        }
        return NodeListCache.getCache();

    }

    /**
     * Returns nodes matching a specified constraint.
     * The constraint is specified by a query that selects nodes of
     * a specified type, which must be the nodetype corresponding
     * to this builder.
     *
     * Cache is used, but not filled (because this function is used to calculate subresults)
     *
     * @param query The query.
     * @param useCache if true, the querycache is used
     * @return The nodes.
     * @throws IllegalArgumentException When the nodetype specified
     *         by the query is not the nodetype corresponding to this builder.
     */
    private List getRawNodes(SearchQuery query, boolean useCache) throws SearchQueryException {
        // Test if nodetype corresponds to builder.
        verifyBuilderQuery(query);
        List results = useCache ? (List) getCache(query).get(query) : null;

        // if unavailable, obtain from storage
        if (results == null) {
            log.debug("result list is null, getting from storage");
            results = builder.getMMBase().getSearchQueryHandler().getNodes(query, builder);
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Found from cache" + ((Cache) getCache(query)).getName() + " " + results);
            }
        }
        return results;
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
     */
    public List getNodes(SearchQuery query) throws SearchQueryException {
        return getNodes(query, true);
    }

    /**
     * Returns nodes matching a specified constraint.
     * The constraint is specified by a query that selects nodes of
     * a specified type, which must be the nodetype corresponding
     * to this builder.
     *
     * @param query The query.
     * @param useCache if true, the querycache is used
     * @return The nodes.
     * @throws IllegalArgumentException When the nodetype specified
     *         by the query is not the nodetype corresponding to this builder.
     */
    public List getNodes(SearchQuery query, boolean useCache) throws SearchQueryException {
        List results = getRawNodes(query, useCache);
        // TODO (later): implement maximum set by maxNodesFromQuery?
        // Perform necessary postprocessing.
        processSearchResults(results);
        if (useCache) {
            getCache(query).put(query, results);
        }
        return results;
    }

    /**
     * Returns all the nodes from the associated builder.
     * @return The nodes.
     */
    public List getNodes() throws SearchQueryException {
        return getNodes(new NodeSearchQuery(builder));
    }

    /**
     * Performs some necessary postprocessing on nodes retrieved from a
     * search query.
     * This consists of the following actions:
     * <ul>
     * <li>Stores retrieved nodes in the node cache, or
     * <li>Replace partially retrieved nodes in the result by complete nodes.
     *     Nodes are partially retrieved when their type is a inheriting type
     *     of this builder's type, having additional fields. For these nodes
     *     additional queries are performed to retrieve the complete nodes.
     * <li>Removes nodes with invalid node number from the result.
     * </ul>
     *
     * @param results The nodes. After returning, partially retrieved nodes
     *        in the result are replaced <em>in place</em> by complete nodes.
     */
    private void processSearchResults(List results) {
        Map convert = new HashMap();
        int convertCount = 0;
        int convertedCount = 0;
        int cacheGetCount = 0;
        int cachePutCount = 0;

        ListIterator resultsIterator = results.listIterator();
        while (resultsIterator.hasNext()) {
            MMObjectNode node = (MMObjectNode) resultsIterator.next();
            Integer number = new Integer(node.getNumber());
            if(number.intValue() < 0) {
                // never happened to me, and never should!
                log.error("invalid node found, node number was invalid:" + node.getNumber()+", storage invalid?");
                // dont know what to do with this node,...
                // remove it from the results, continue to the next one!
                resultsIterator.remove();
                continue;
            }

            boolean fromCache = false;
            // only active when builder loaded (oType != -1)
            // maybe we got the wrong node typeback, if so
            // try to retrieve the correct node from the cache first
            int oType = builder.getNumber();
            if(oType != -1 && oType != node.getOType()){
                // try to retrieve the correct node from the
                // nodecache
                MMObjectNode cachedNode = builder.getNodeFromCache(number);
                if(cachedNode != null) {
                    node = cachedNode;
                    resultsIterator.set(node);
                    fromCache = true;
                    cacheGetCount ++;
                } else {
                    // add this node to the list of nodes that still need to
                    // be converted..
                    // we dont request the builder here, for this we need the
                    // typedef table, which could generate an additional query..
                    Integer nodeType = new Integer(node.getOType());
                    Set nodes = (Set) convert.get(nodeType);
                    // create an new entry for the type, if not yet there...
                    if (nodes == null) {
                        nodes = new HashSet();
                        convert.put(nodeType, nodes);
                    }
                    nodes.add(node);
                    convertCount ++;
                }
/*
            } else if (oType == node.getOType()) {
                MMObjectNode oldNode = builder.getNodeFromCache(number);
                // when we want to use cache also for new found nodes
                // and cache may not be replaced, use the one from the
                // cache..
                if(!REPLACE_CACHE && oldNode != null) {
                    node = oldNode;
                    resultsIterator.set(node);
                    fromCache = true;
                    cacheGetCount++;
                }
            } else {
                // skipping everything, our builder hasnt been started yet...
*/
            }

            // we can add the node to the cache _if_
            // it was not from cache already, and it
            // is of the correct type..
            if(!fromCache && oType == node.getOType()) {
                // can someone tell me what this has to do?
                // clear the changed signal
                node.clearChanged(); // huh?
                node = builder.safeCache(number, node);
                cachePutCount++;
            }
        }

        if (/* CORRECT_NODE_TYPES && */ convert.size() > 0){
            // retieve the nodes from the builders....
            // and put them into one big hashmap (integer/node)
            // after that replace all the nodes in result, that
            // were invalid.
            Map convertedNodes = new HashMap();

            // process all the different types (builders)
            Iterator types = convert.entrySet().iterator();
            while(types.hasNext()){
                Map.Entry typeEntry = (Map.Entry) types.next();
                int nodeType = ((Integer)typeEntry.getKey()).intValue();
                Set nodes = (Set) typeEntry.getValue();
                MMObjectNode typedefNode;
                try {
                    typedefNode = getNode(nodeType, true);
                } catch (Exception e) {
                    log.error("Exception during conversion of nodelist to right types.  Nodes (" + nodes + ") of current type " + nodeType + " will be skipped. Probably the storage is inconsistent. Message: " + e.getMessage());

                    continue;
                }
                if(typedefNode == null) {
                    // builder not known in typedef?
                    // skip this builder and process to next one..
                    // TODO: research: add incorrect node to node's cache?
                    log.error("Could not find typedef node #" + nodeType);
                    continue;
                }
                MMObjectBuilder conversionBuilder = builder.getMMBase().getBuilder(typedefNode.getStringValue("name"));
                if(conversionBuilder == null) {
                    // could not find the builder that was in typedef..
                    // maybe it is not active?
                    // TODO: research: add incorrect node's to node cache?
                    log.error("Could not find builder with name:" + typedefNode.getStringValue("name") + " refered by node #" + typedefNode.getNumber()+", is it active?");
                    continue;
                }
                try {
                    Iterator converted = conversionBuilder.getStorageConnector().getNodes(nodes).iterator();
                    while(converted.hasNext()) {
                        MMObjectNode current = (MMObjectNode) converted.next();
                        if (current != null) {
                            convertedNodes.put(new Integer(current.getNumber()), current);
                        }
                    }
                } catch (SearchQueryException sqe) {
                    log.error(sqe.getMessage() + Logging.stackTrace(sqe));
                    // no nodes
                }
            }

            // insert all the corrected nodes that were found into the list..
            for(int i = 0; i < results.size(); i++) {
                MMObjectNode current = (MMObjectNode) results.get(i);
                Integer number = new Integer(current.getNumber());
                if(convertedNodes.containsKey(number)) {
                    // converting the node...
                    results.set(i, convertedNodes.get(number));
                    convertedCount ++;
                }
                current = (MMObjectNode) results.get(i);
                if(current.getNumber() < 0) {
                    // never happened to me, and never should!
                    throw new RuntimeException("invalid node found, node number was invalid:" + current.getNumber());
                }
            }
        } else if(convert.size() != 0) {
            log.warn("we still need to convert " + convertCount + " of the " + results.size() + " nodes"
                     + "(number of different types:"+ convert.size()  +")");
        }
        if(log.isDebugEnabled()) {
            log.debug("retrieved " + results.size() +
                      " nodes, converted " + convertedCount +
                      " of the " + convertCount +
                      " invalid nodes(" + convert.size() +
                      " types, " + cacheGetCount +
                      " from cache, " + cachePutCount + " to cache)");
        }
    }

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
    public NodeSearchQuery getSearchQuery(String where) {
        NodeSearchQuery query;

        if (where != null && where.startsWith("MMNODE ")) {
            // MMNODE expression.
            query = convertMMNodeSearch2Query(where);
        } else {
            query = new NodeSearchQuery(builder);
            QueryConvertor.setConstraint(query, where);
        }

        return query;
    }

    /**
     * Creates query based on an MMNODE expression.
     *
     * @deprecated MMNODE expressions are deprecated, scan only?
     * @param expr The MMNODE expression.
     * @return The query.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    private NodeSearchQuery convertMMNodeSearch2Query(String expr) {
        NodeSearchQuery query = new NodeSearchQuery(builder);
        BasicCompositeConstraint constraints = new BasicCompositeConstraint(CompositeConstraint.LOGICAL_AND);
        String logicalOperator = null;

        // Strip leading string "MMNODE " from expression, parse
        // fieldexpressions and logical operators.
        // (legacy: eol characters '\n' and '\r' are interpreted as "AND NOT")
        StringTokenizer tokenizer = new StringTokenizer(expr.substring(7), "+-\n\r", true);
        while (tokenizer.hasMoreTokens()) {
            String fieldExpression = tokenizer.nextToken();

            // Remove prefix if present (example episodes.title==).
            int pos = fieldExpression.indexOf('.');
            if (pos != -1) {
                fieldExpression = fieldExpression.substring(pos + 1);
            }

            // Break up field expression in fieldname, comparison operator
            // and value.
            pos = fieldExpression.indexOf('=');
            if (pos != -1 && fieldExpression.length() > pos + 2) {
                String fieldName = fieldExpression.substring(0, pos);
                char comparison = fieldExpression.charAt(pos + 1);
                String value = fieldExpression.substring(pos + 2);

                // Add corresponding constraint to constraints.
                CoreField field = builder.getField(fieldName);
                if (field == null) {
                    throw new IllegalArgumentException(
                        "Invalid MMNODE expression: " + expr);
                }
                StepField stepField = query.getField(field);
                BasicConstraint constraint = parseFieldPart(stepField, comparison, value);
                constraints.addChild(constraint);

                // Set to inverse if preceded by a logical operator that is
                // not equal to "+".
                if (logicalOperator != null && !logicalOperator.equals("+")) {
                    constraint.setInverse(true);
                }
            } else {
                // Invalid expression.
                throw new IllegalArgumentException(
                    "Invalid MMNODE expression: " + expr);
            }

            // Read next logical operator.
            if (tokenizer.hasMoreTokens()) {
                logicalOperator = tokenizer.nextToken();
            }
        }

        List childs = constraints.getChilds();
        if (childs.size() == 1) {
            query.setConstraint((FieldValueConstraint) childs.get(0));
        } else if (childs.size() > 1) {
            query.setConstraint(constraints);
        }
        return query;
    }

    /**
     * Creates a {@link org.mmbase.storage.search.FieldCompareConstraint
     * FieldCompareConstraint}, based on parts of a field expression in a
     * MMNODE expression.
     *
     * @deprecated MMNODE expressions are deprecated
     * @param field The field
     * @param comparison The second character of the comparison operator.
     * @param strValue The value to compare with, represented as
     *        <code>String<code>.
     * @return The constraint.
     */
    private BasicFieldValueConstraint parseFieldPart(StepField field, char comparison, String strValue) {

        Object value = strValue;

        // For numberical fields, convert string representation to Double.
        if (field.getType() != Field.TYPE_STRING &&
            field.getType() != Field.TYPE_XML &&
            field.getType() != Field.TYPE_UNKNOWN) {
                // backwards comp fix. This is needed for the scan editors.
                int length = strValue.length();
                if (strValue.charAt(0) == '*' && strValue.charAt(length - 1) == '*') {
                    strValue = strValue.substring(1, length - 1);
                }
                value = Double.valueOf(strValue);
        }

        BasicFieldValueConstraint constraint = new BasicFieldValueConstraint(field, value);

        switch (comparison) {
        case '=':
        case 'E':
            // EQUAL (string field)
            if (field.getType() == Field.TYPE_STRING ||
                field.getType() == Field.TYPE_XML) {
                // Strip first and last character of value, when
                // equal to '*'.
                String str = (String) value;
                int length = str.length();
                if (str.charAt(0) == '*' && str.charAt(length - 1) == '*') {
                    value = str.substring(1, length - 1);
                }

                // Convert to LIKE comparison with wildchard characters
                // before and after (legacy).
                constraint.setValue('%' + (String) value + '%');
                constraint.setCaseSensitive(false);
                constraint.setOperator(FieldCompareConstraint.LIKE);

                // EQUAL (numerical field)
            } else {
                constraint.setOperator(FieldCompareConstraint.EQUAL);
            }
            break;

        case 'N':
            constraint.setOperator(FieldCompareConstraint.NOT_EQUAL);
            break;

        case 'G':
            constraint.setOperator(FieldCompareConstraint.GREATER);
            break;

        case 'g':
            constraint.setOperator(FieldCompareConstraint.GREATER_EQUAL);
            break;

        case 'S':
            constraint.setOperator(FieldCompareConstraint.LESS);
            break;

        case 's':
            constraint.setOperator(FieldCompareConstraint.LESS_EQUAL);
            break;

        default:
            throw new IllegalArgumentException("Invalid comparison character: '" + comparison + "'");
        }
        return constraint;
    }



}
