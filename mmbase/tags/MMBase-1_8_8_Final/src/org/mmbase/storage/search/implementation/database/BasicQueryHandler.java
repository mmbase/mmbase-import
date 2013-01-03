/*
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search.implementation.database;

import java.sql.*;
import java.util.*;
import javax.sql.DataSource;

import org.mmbase.cache.NodeCache;
import org.mmbase.cache.Cache;
import org.mmbase.bridge.NodeManager;
import org.mmbase.core.CoreField;
import org.mmbase.module.core.*;
import org.mmbase.storage.implementation.database.DatabaseStorageManager;
import org.mmbase.storage.implementation.database.DatabaseStorageManagerFactory;
import org.mmbase.storage.search.*;
import org.mmbase.util.logging.*;
import org.mmbase.storage.search.implementation.ModifiableQuery;


/**
 * Basic implementation using a database.
 * Uses a {@link org.mmbase.storage.search.implementation.database.SqlHandler SqlHandler}
 * to create SQL string representations of search queries.
 * <p>
 * In order to execute search queries, these are represented as SQL strings
 * by the handler, and in this form executed on the database.
 *
 * @author Rob van Maris
 * @version $Id$
 * @since MMBase-1.7
 */
public class BasicQueryHandler implements SearchQueryHandler {

    /** Empty StepField array. */
    private static final StepField[] STEP_FIELD_ARRAY = new StepField[0];


    private static final Logger log = Logging.getLoggerInstance(BasicQueryHandler.class);

    /** Sql handler used to generate SQL statements. */
    private SqlHandler sqlHandler = null;

    /** MMBase instance. */
    private MMBase mmbase = null;

    /**
     * Default constructor.
     *
     * @param sqlHandler The handler use to create SQL string representations
     *        of search queries.
     */
    public BasicQueryHandler(SqlHandler sqlHandler) {
        this.sqlHandler = sqlHandler;
        mmbase = MMBase.getMMBase();
    }


    // javadoc is inherited
    public List getNodes(SearchQuery query, MMObjectBuilder builder) throws SearchQueryException {

        List results;
        Connection con = null;
        Statement stmt = null;
        String sqlString = null;

        try {
            // Flag, set if offset must be supported by skipping results.
            boolean mustSkipResults =
                (query.getOffset() != SearchQuery.DEFAULT_OFFSET) &&
                (sqlHandler.getSupportLevel(SearchQueryHandler.FEATURE_OFFSET, query) == SearchQueryHandler.SUPPORT_NONE);


            // Flag, set if sql handler supports maxnumber.
            boolean sqlHandlerSupportsMaxNumber = sqlHandler.getSupportLevel(SearchQueryHandler.FEATURE_MAX_NUMBER, query) != SearchQueryHandler.SUPPORT_NONE;

            // report about offset and max support (for debug purposes)
            if (log.isDebugEnabled()) {
                log.debug("Database offset support = " + (sqlHandler.getSupportLevel(SearchQueryHandler.FEATURE_OFFSET, query) != SearchQueryHandler.SUPPORT_NONE));
                log.debug("mustSkipResults = " + mustSkipResults);
                log.debug("Database max support = " + sqlHandlerSupportsMaxNumber);
            }

            sqlString = createSqlString(query, mustSkipResults, sqlHandlerSupportsMaxNumber);

            log.debug("sql: " + sqlString);
            
            // Execute the SQL... ARGH !!! Has to move!
            // get connection...
            DataSource dataSource = ((DatabaseStorageManagerFactory) mmbase.getStorageManagerFactory()).getDataSource();
            con = dataSource.getConnection();
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sqlString);

            try {
                if (mustSkipResults) {
                    log.debug("skipping results, to provide weak support for offset");
                    for (int i = 0; i < query.getOffset(); i++) {
                        rs.next();
                    }
                }

                // Now store results as cluster-/real nodes.
                StepField[] fields = (StepField[]) query.getFields().toArray(STEP_FIELD_ARRAY);
                int maxNumber = query.getMaxNumber();

                // now, we dispatch the reading of the result set to the right function wich instantiates Nodes of the right type.
                if (builder instanceof ClusterBuilder) {
                    results = readNodes((ClusterBuilder) builder, fields, rs, sqlHandlerSupportsMaxNumber, maxNumber, query.getSteps().size());
                } else if (builder instanceof ResultBuilder) {
                    results = readNodes((ResultBuilder) builder, fields, rs, sqlHandlerSupportsMaxNumber, maxNumber);
                } else {
                    results = readNodes(builder, fields, rs, sqlHandlerSupportsMaxNumber, maxNumber);
                }
            } finally {
                rs.close();
            }
        } catch (SQLException e) {
            // Something went wrong, log exception
            // and rethrow as SearchQueryException.
            if (log.isDebugEnabled()) {
                log.debug("Query failed:" + query + "\n" + e + Logging.stackTrace(e));
            }
            throw new SearchQueryException("Query '" + (sqlString == null ? "" + query.toString() : sqlString)  + "' failed: " + e.getClass().getName() + ": " + e.getMessage(), e);
        } finally {
            closeConnection(con, stmt);
        }

        return results;
    }

    /**
     * Safely close a database connection and/or a database statement.
     * @param con The connection to close. Can be <code>null</code>.
     * @param stmt The statement to close, prior to closing the connection. Can be <code>null</code>.
     */
    protected void closeConnection(Connection con, Statement stmt) {
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (Exception g) {}
        try {
            if (con != null) {
                con.close();
            }
        } catch (Exception g) {}
    }

    /**
     * Makes a String of a query, taking into consideration if the database supports offset and
     * maxnumber features. The resulting String is an SQL query which can be fed to the database.
     * @param query the query to convert to sql
     * @return the sql string
     * @throws SearchQueryException when error occurs while making the string
     */
    public String createSqlString(SearchQuery query) throws SearchQueryException {
        // Flag, set if offset must be supported by skipping results.
        boolean mustSkipResults =
            (query.getOffset() != SearchQuery.DEFAULT_OFFSET) &&
            (sqlHandler.getSupportLevel(SearchQueryHandler.FEATURE_OFFSET, query) == SearchQueryHandler.SUPPORT_NONE);


        // Flag, set if sql handler supports maxnumber.
        boolean sqlHandlerSupportsMaxNumber = sqlHandler.getSupportLevel(SearchQueryHandler.FEATURE_MAX_NUMBER, query) != SearchQueryHandler.SUPPORT_NONE;

        // report about offset and max support (for debug purposes)
        if (log.isDebugEnabled()) {
            log.debug("Database offset support = " + (sqlHandler.getSupportLevel(SearchQueryHandler.FEATURE_OFFSET, query) != SearchQueryHandler.SUPPORT_NONE));
            log.debug("mustSkipResults = " + mustSkipResults);
            log.debug("Database max support = " + sqlHandlerSupportsMaxNumber);
        }

        return createSqlString(query, mustSkipResults, sqlHandlerSupportsMaxNumber);
    }

    
    /**
     * Makes a String of a query, taking into consideration if the database supports offset and
     * maxnumber features. The resulting String is an SQL query which can be fed to the database.
     */

    private String createSqlString(SearchQuery query, boolean mustSkipResults, boolean sqlHandlerSupportsMaxNumber) throws SearchQueryException {
        int maxNumber = query.getMaxNumber();
        // Flag, set if maxnumber must be supported by truncating results.
        boolean mustTruncateResults = (maxNumber != SearchQuery.DEFAULT_MAX_NUMBER) && (! sqlHandlerSupportsMaxNumber);
        String sqlString;
       if (mustSkipResults) { // offset not supported, but needed
           log.debug("offset used in query and not supported in database.");
           ModifiableQuery modifiedQuery = new ModifiableQuery(query);
           modifiedQuery.setOffset(SearchQuery.DEFAULT_OFFSET);

           if (mustTruncateResults) {
               log.debug("max used in query but not supported in database.");
               // Weak support for offset, weak support for maxnumber:
               modifiedQuery.setMaxNumber(SearchQuery.DEFAULT_MAX_NUMBER); // apply no maximum, but truncate result
           } else if (maxNumber != SearchQuery.DEFAULT_MAX_NUMBER) {
               log.debug("max used in query and supported by database.");
               // Because offset is not supported add max with the offset.
               // Weak support for offset, sql handler supports maxnumber:
               modifiedQuery.setMaxNumber(query.getOffset() + maxNumber);
           }
           sqlString = sqlHandler.toSql(modifiedQuery, sqlHandler);

       } else {
           log.debug("offset not used or offset is supported by the database.");
           if (mustTruncateResults) {
               log.debug("max used in query but not supported in database.");
               // Sql handler supports offset, or not offset is specified.
               // weak support for maxnumber:
               ModifiableQuery modifiedQuery = new ModifiableQuery(query);
               modifiedQuery.setMaxNumber(SearchQuery.DEFAULT_MAX_NUMBER); // apply no maximum, but truncate result
               sqlString = sqlHandler.toSql(modifiedQuery, sqlHandler);
           } else {
               // Offset not used, maxnumber not used.
               log.debug("no need for modifying Query");
               sqlString = sqlHandler.toSql(query, sqlHandler);
           }
       }
       // TODO: test maximum sql statement length is not exceeded.
       return sqlString;
    }

    /**
     * Read the result list and creates a List of ClusterNodes.
     */
    private List readNodes(ClusterBuilder builder, StepField[] fields, ResultSet rs, boolean sqlHandlerSupportsMaxNumber, int maxNumber, int numberOfSteps) throws SQLException {
        List results = new ArrayList();
        DatabaseStorageManager storageManager = (DatabaseStorageManager)mmbase.getStorageManager();

        boolean storesAsFile = builder.getMMBase().getStorageManagerFactory().hasOption(org.mmbase.storage.implementation.database.Attributes.STORES_BINARY_AS_FILE);
        // Truncate results to provide weak support for maxnumber.
        try {
            while (rs.next() && (results.size()<maxNumber || maxNumber==-1)) {
                try {
                    ClusterNode node = new ClusterNode(builder, numberOfSteps);
                    node.start();

                    int j = 1;
                    // make use of Node-cache to fill fields
                    // especially XML-fields can be heavy, otherwise (Documnents must be instantiated)
                    for (int i = 0; i < fields.length; i++) {
                        String fieldName = fields[i].getFieldName(); // why not getAlias first?
                        Step step = fields[i].getStep();
                        String alias = step.getAlias();
                        if (alias == null) {
                            // Use tablename as alias when no alias is specified.
                            alias = step.getTableName();
                        }
                        CoreField field = builder.getField(alias +  '.' + fieldName);
                        if (field.getType() == CoreField.TYPE_BINARY) continue;
                        Object value = storageManager.getValue(rs, j++, field, false);
                        node.storeValue(alias +  '.' + fieldName, value);
                    }
                    node.clearChanged();
                    node.finish();
                    results.add(node);
                } catch (Exception e) {
                    // log error, but continue with other nodes
                    log.error(e.getMessage(), e);
                }
            }
        } catch (SQLException sqe) {
            // log error, but return results.
            log.error(sqe);
        }
        return results;
    }

    /**
     * Read the result list and creates a List of ResultNodes
     */
    private List readNodes(ResultBuilder builder, StepField[] fields, ResultSet rs, boolean sqlHandlerSupportsMaxNumber, int maxNumber) throws SQLException {
        List results = new ArrayList();
        DatabaseStorageManager storageManager = (DatabaseStorageManager)mmbase.getStorageManager();

        boolean storesAsFile = builder.getMMBase().getStorageManagerFactory().hasOption(org.mmbase.storage.implementation.database.Attributes.STORES_BINARY_AS_FILE);
        // Truncate results to provide weak support for maxnumber.
        try {
            while (rs.next() && (maxNumber>results.size() || maxNumber==-1)) {
                try {
                    ResultNode node = new ResultNode(builder);
                    node.start();
                    int j = 1;
                    for (int i = 0; i < fields.length; i++) {
                        String fieldName = fields[i].getAlias();
                        if (fieldName == null) {
                            fieldName = fields[i].getFieldName();
                        }
                        CoreField field = builder.getField(fieldName);
                        if (field != null && field.getType() == CoreField.TYPE_BINARY) continue;
                        Object value = storageManager.getValue(rs, j++, field, false);
                        node.storeValue(fieldName, value);
                    }
                    node.clearChanged();
                    node.finish();
                    results.add(node);
                } catch (Exception e) {
                    // log error, but continue with other nodes
                    log.error(e.getMessage(), e);
                }
            }
        } catch (SQLException sqe) {
            // log error, but return results.
            log.error(sqe);
        }
        return results;
    }

    /**
     * Read the result list and creates a List of normal MMObjectNodes.
     */
    private List readNodes(MMObjectBuilder builder, StepField[] fields, ResultSet rs, boolean sqlHandlerSupportsMaxNumber, int maxNumber) throws SQLException {
        List results= new ArrayList();
        DatabaseStorageManager storageManager = (DatabaseStorageManager)mmbase.getStorageManager();

        boolean storesAsFile = builder.getMMBase().getStorageManagerFactory().hasOption(org.mmbase.storage.implementation.database.Attributes.STORES_BINARY_AS_FILE);
        // determine indices of queried fields
        Map fieldIndices = new HashMap();
        Step nodeStep = fields[0].getStep();
        int j = 1;
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].getType() == CoreField.TYPE_BINARY) continue;
            Integer index = new Integer(j++);
            if (fields[i].getStep() == nodeStep) {
                String fieldName =  fields[i].getFieldName();
                CoreField field = builder.getField(fieldName);
                if (field == null) {
                    log.warn("Did not find the field '" + fieldName + "' in builder " + builder);
                    continue; // could this happen?
                }
                fieldIndices.put(field, index);
            }
        }

        // Test if ALL fields are queried
        List builderFields = builder.getFields(NodeManager.ORDER_CREATE);
        StringBuffer missingFields = null;
        for (Iterator f = builderFields.iterator(); f.hasNext();) {
            CoreField field = (CoreField)f.next();
            if (field.inStorage()) {
                if (field.getType() == CoreField.TYPE_BINARY) continue;
                if (fieldIndices.get(field) == null) {
                    if (missingFields == null) {
                        missingFields = new StringBuffer(field.getName());
                    } else {
                        missingFields.append(", ").append(field.getName());
                    }
                }
            }
        }

        // if not all field are queried, this is a virtual node
        boolean isVirtual = missingFields != null;
        if (isVirtual) {
            log.warn("This query returns virtual nodes (not querying: '" + missingFields + "')");
        }

        // Truncate results to provide weak support for maxnumber.
        try {
            NodeCache nodeCache = NodeCache.getCache();
            Cache typeCache = Cache.getCache("TypeCache");
            int builderType = builder.getObjectType();
            Integer oTypeInteger = new Integer(builderType);
            while (rs.next() && (maxNumber > results.size() || maxNumber==-1)) {
                try {
                    /*
                     * This while statement does not deal with mmbase inheritance
                     * It creates nodes based on the builder passed in. Nodes with 
                     * subtypes of this builder are only filled with the field values
                     * of this builder. Builders of a subtype are not stored in the nodeCache
                     * to limit the time scope of these nodes, because they are not complete. 
                     */
                    
                    MMObjectNode node;
                    if (!isVirtual) {
                        node = new MMObjectNode(builder, false);
                    } else {
                        node = new VirtualNode(builder);
                    }
                    node.start();
                    for (Iterator i = builder.getFields(NodeManager.ORDER_CREATE).iterator(); i.hasNext(); ) {
                        CoreField field = (CoreField)i.next();
                        if (! field.inStorage()) continue;
                        Integer index = (Integer) fieldIndices.get(field);
                        Object value = null;
                        String fieldName = field.getName();
                        if (index != null) {
                            value = storageManager.getValue(rs, index.intValue(), field, true);
                        } else {
                            java.sql.Blob b = null;
                            if (field.getType() == CoreField.TYPE_BINARY && storesAsFile) {
                                log.debug("Storage did not return data for '" + fieldName + "', supposing it on disk");
                                // must have been a explicitely specified 'blob' field
                                b = storageManager.getBlobValue(node, field, true);
                            } else if (field.getType() == CoreField.TYPE_BINARY) {
                                // binary fields never come directly from the database
                                value = MMObjectNode.VALUE_SHORTED;
                            } else if (! isVirtual){
                                // field wasn't returned by the db - this must be a Virtual node, otherwise fail!
                                // (this shoudln't occur)
                                throw new IllegalStateException("Storage did not return data for field '" + fieldName + "'");
                            }
                            if (b != null) {
                                if (b.length() == -1) {
                                    value = MMObjectNode.VALUE_SHORTED;
                                } else {
                                    value = b.getBytes(0L, (int) b.length());
                                }
                            }
                        }
                        node.storeValue(fieldName, value);
                    }
                    node.clearChanged();
                    node.finish();

                    // The following code fills the type- and node-cache as far as this is possible at this stage.
                    // (provided the node is persistent)
                    if (! isVirtual) {
                        int otype = node.getOType();
                        Integer number = new Integer(node.getNumber());
                        if (otype == builderType) {
                            MMObjectNode cacheNode = (MMObjectNode) nodeCache.get(number);
                            if (cacheNode != null) {
                                node = cacheNode;
                            } else {
                                nodeCache.put(number, node);
                            }
                            typeCache.put(number, oTypeInteger);
                        } else {
                            typeCache.put(number, new Integer(otype));
                        }
                    }

                    results.add(node);
                } catch (Exception e) {
                    // log error, but continue with other nodes
                    log.error(e.getMessage(), e);
                }
            }
        } catch (SQLException sqe) {
            // log error, but return results.
            log.error(sqe);
        }
        return results;
    }


    // javadoc is inherited
    public int getSupportLevel(int feature, SearchQuery query) throws SearchQueryException {
        int supportLevel;
        switch (feature) {
        case SearchQueryHandler.FEATURE_OFFSET:
            // When sql handler does not support OFFSET, this query handler
            // provides weak support by skipping resultsets.
            // (falls through)
        case SearchQueryHandler.FEATURE_MAX_NUMBER:
            // When sql handler does not support MAX NUMBER, this query
            // handler provides weak support by truncating resultsets.
            int handlerSupport = sqlHandler.getSupportLevel(feature, query);
            if (handlerSupport == SearchQueryHandler.SUPPORT_NONE) {
                // TODO: implement weak support.
                //supportLevel = SearchQueryHandler.SUPPORT_WEAK;
                supportLevel = SearchQueryHandler.SUPPORT_NONE;
            } else {
                supportLevel = handlerSupport;
            }
            break;

        default:
            supportLevel = sqlHandler.getSupportLevel(feature, query);
        }
        return supportLevel;
    }

    // javadoc is inherited
    public int getSupportLevel(Constraint constraint, SearchQuery query) throws SearchQueryException {
        return sqlHandler.getSupportLevel(constraint, query);
    }

}
