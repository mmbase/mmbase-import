package org.mmbase.storage.search.implementation;

import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.database.support.MMJdbc2NodeInterface;
import org.mmbase.storage.search.*;
import org.mmbase.util.logging.*;
import org.mmbase.module.database.MultiConnection;
import java.sql.*;
import java.util.*;


/**
 * Basic implementation using a database.
 * Uses a {@link org.mmbase.storage.search.SqlHandler SqlHandler}
 * to create SQL string representations of search queries.
 * <p>
 * In order to execute search queries, these are represented as SQL strings
 * by the handler, and in this form executed on the database.
 *
 * @author Rob van Maris
 * @version $Revision: 1.1 $
 */
public class BasicQueryHandler implements SearchQueryHandler {
    
    /** Empty StepField array. */
    private static final StepField[] STEP_FIELD_ARRAY = new StepField[0];
    
    /** Logger instance. */
    private static Logger log
    = Logging.getLoggerInstance(SearchQueryHandler.class.getName());
    
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
        // TODO: test if MMBase is properly initialized first.
        mmbase = MMBase.getMMBase();
    }
    
    // javadoc is inherited
    public List getNodes(SearchQuery query, MMObjectBuilder builder)
    throws SearchQueryException {
        StepField[] fields =
        (StepField[]) query.getFields().toArray(STEP_FIELD_ARRAY);
        List steps = query.getSteps();
        List results = new ArrayList();
        String sqlString = null;
        MultiConnection con = null;
        Statement stmt = null;
        try {
            // Generate the SQL string for the query.
            sqlString = sqlHandler.toSql(query, sqlHandler);
            
            // Execute the SQL and store results as cluster-/real nodes.
            // TODO: implement offset/limit here when not supported by database.
            MMJdbc2NodeInterface database = mmbase.getDatabase();
            con = mmbase.getConnection();
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sqlString);
            while (rs.next()) {
                MMObjectNode node = null;
                if (builder instanceof ClusterBuilder) {
                    // Cluster nodes.
                    node = new ClusterNode(builder, steps.size());
                } else {
                    // Real nodes.
                    node = new MMObjectNode(builder);
                }
                for (int i = 0; i < fields.length; i++) {
                    String fieldName = fields[i].getFieldName();
                    int fieldType = fields[i].getType();
                    String prefix = "";
                    if (builder instanceof ClusterBuilder) {
                        // Prefix for cluster nodes.
                        prefix = fields[i].getStep().getAlias() + ".";
                    }
                    // TODO: use alternative to decodeDBnodeField, to
                    // circumvent the code in decodeDBnodeField that tries to
                    // reverse replacement of "disallowed" fieldnames.
                    database.decodeDBnodeField(node, fieldName, rs, i + 1, prefix);
                }
                if (builder instanceof ClusterBuilder) {
                    // Finished initializing clusternode.
                    ((ClusterNode) node).initializing = false;
                }
                results.add(node);
            }
        } catch (Exception e) {
            // something went wrong print it to the logs
            // TODO: implement toString() method for query.
            log.error("Query failed:" + query);
            log.error(Logging.stackTrace(e));
            return null;
        } finally {
            mmbase.closeConnection(con,stmt);
        }
        return results;
    }
    
    // javadoc is inherited
    public int getSupportLevel(int feature, SearchQuery query) throws SearchQueryException {
        return sqlHandler.getSupportLevel(feature, query);
    }
    
    // javadoc is inherited
    public int getSupportLevel(Constraint constraint, SearchQuery query) throws SearchQueryException {
        return sqlHandler.getSupportLevel(constraint, query);
    }
    
}
