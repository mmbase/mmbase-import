/*
 * InformixSqlHandler.java
 *
 * Created on October 17, 2002, 3:39 PM
 */

package org.mmbase.storage.search.implementation.database;

import java.util.*;
import org.mmbase.storage.search.*;
import org.mmbase.util.logging.*;

/**
 * The Informix query handler, implements {@link 
 * org.mmbase.storage.search.implementation.database.SqlHandler SqlHandler} for standard
 * Informix functionality.
 * <br>
 * Derived from {@link BasicSqlHandler BasicSqlHandler}, overrides
 * <ul>
 * <li>{@link #toSql toSql()}, implements {@link 
 * org.mmbase.storage.search.SearchQueryHandler#FEATURE_MAX_NUMBER 
 * FEATURE_MAX_NUMBER}, by adding a construct like "<code>SELECT FIRST 20</code>"
 * in front of the body, when appropriate.
 * <li>{@link #getSupportLevel(int,SearchQuery) getSupportLevel(int,SearchQuery)},
 * returns {@link 
 * org.mmbase.storage.search.SearchQueryHandler#SUPPORT_OPTIMAL 
 * SUPPORT_OPTIMAL} for this feature, delegates to the superclass for 
 * other features.
 * </ul>
 *
 * @author Rob van Maris
 * @version $Revision: 1.1 $
 */
public class InformixSqlHandler extends BasicSqlHandler implements SqlHandler {
    
    /** Logger instance. */
    private static Logger log
    = Logging.getLoggerInstance(InformixSqlHandler.class.getName());
    
    /**
     * Default constructor.
     *
     * @param disallowedValues Map mapping disallowed table/fieldnames
     *        to allowed alternatives.
     */
    public InformixSqlHandler(Map disallowedValues) {
        super(disallowedValues);
    }
    
    // javadoc is inherited
    public int getSupportLevel(int feature, SearchQuery query) throws SearchQueryException {
        int result;
        switch (feature) {
            case SearchQueryHandler.FEATURE_MAX_NUMBER:
                result = SearchQueryHandler.SUPPORT_OPTIMAL;
                break;
                
            default:
                result = super.getSupportLevel(feature, query);
        }
        return result;
    }
    
    // javadoc is inherited
    public String toSql(SearchQuery query, SqlHandler firstInChain) throws SearchQueryException {
        
        // XXX TODO: test table and field aliases for uniqueness.
        
        // Test for at least 1 step and 1 field.
        if (query.getSteps().isEmpty()) {
            throw new IllegalStateException(
            "Searchquery has no step (at leas 1 step is required).");
        }
        if (query.getFields().isEmpty()) {
            throw new IllegalStateException(
            "Searchquery has no field (at least 1 field is required).");
        }
        
        // SELECT
        StringBuffer sbQuery = new StringBuffer("SELECT ");
        // TODO: throw exception if offset set to non-default value.
        
        
        // FIRST
        if (query.getMaxNumber() != -1) {
            // Maxnumber set.
            sbQuery.append("FIRST ").
            append(query.getMaxNumber()).
            append(" ");
        }
        
        // DISTINCT
        if (query.isDistinct()) {
            sbQuery.append("DISTINCT ");
        }
        
        firstInChain.appendQueryBodyToSql(sbQuery, query, firstInChain);

        String strSQL = sbQuery.toString();
        if (log.isDebugEnabled()) {
            log.debug("generated SQL: " + strSQL);
        }
        return strSQL;
    }
}
