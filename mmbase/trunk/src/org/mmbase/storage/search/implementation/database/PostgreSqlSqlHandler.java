/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search.implementation.database;

import java.util.*;
import org.mmbase.storage.search.*;
import org.mmbase.util.logging.*;

/**
 * The PostgreSQL query handler, implements {@link
 * org.mmbase.storage.search.implementation.database.SqlHandler SqlHandler}
 * for standard PostgreSql functionality.
 * <br />
 * Derived from {@link BasicSqlHandler BasicSqlHandler}, overrides
 * <ul>
 * <li>{@link #toSql toSql()}, implements {@link
 * org.mmbase.storage.search.SearchQueryHandler#FEATURE_MAX_NUMBER
 * FEATURE_MAX_NUMBER} and {@link
 * org.mmbase.storage.search.SearchQueryHandler#FEATURE_OFFSET
 * FEATURE_OFFSET}, by adding a construct like "<code>LIMIT 20</code>" or
 * "<code>LIMIT 20 OFFSET 80</code>" after the body, when appropriate.
 * <li>{@link #getSupportLevel(int,SearchQuery) getSupportLevel(int,SearchQuery)},
 * returns {@link
 * org.mmbase.storage.search.SearchQueryHandler#SUPPORT_OPTIMAL
 * SUPPORT_OPTIMAL} for these features, delegates to the superclass for
 * other features.
 * </ul>
 *
 * @author Rob van Maris
 * @version $Id: PostgreSqlSqlHandler.java,v 1.5 2003-12-11 12:23:36 michiel Exp $
 * @since MMBase-1.7
 */
public class PostgreSqlSqlHandler extends BasicSqlHandler implements SqlHandler {

    /** Logger instance. */
    private static final Logger log = Logging.getLoggerInstance(PostgreSqlSqlHandler.class);

    /**
     * Constructor.
     *
     * @param disallowedValues Map mapping disallowed table/fieldnames
     *        to allowed alternatives.
     */
    public PostgreSqlSqlHandler(Map disallowedValues) {
        super(disallowedValues);
    }

    // javadoc is inherited
    public int getSupportLevel(int feature, SearchQuery query) throws SearchQueryException {
        int result;
        switch (feature) {
            case SearchQueryHandler.FEATURE_MAX_NUMBER:
                result = SearchQueryHandler.SUPPORT_OPTIMAL;
                break;

            case SearchQueryHandler.FEATURE_OFFSET:
                result = SearchQueryHandler.SUPPORT_OPTIMAL;
                break;
                /*
            case SearchQueryHandler.FEATURE_REGEXP:
                result = SearchQueryHandler.SUPPORT_OPTIMAL;
                break;
                */
            default:
                result = super.getSupportLevel(feature, query);
        }
        return result;
    }



    // javadoc is inherited
    public String toSql(SearchQuery query, SqlHandler firstInChain) throws SearchQueryException {
        // XXX should table and field aliases be tested for uniqueness?

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

        // DISTINCT
        if (query.isDistinct()) {
            sbQuery.append("DISTINCT ");
        }

        firstInChain.appendQueryBodyToSql(sbQuery, query, firstInChain);


        int offset = query.getOffset();
        // LIMIT
        if (query.getMaxNumber() != -1) {
            // Maxnumber set.
            sbQuery.append(" LIMIT ").append(query.getMaxNumber());
        } else {
            // could append LIMIT ALL, but why bother
        }

        if (offset != 0) {
            sbQuery.append(" OFFSET ").append(offset);
        }

        String strSQL = sbQuery.toString();
        if (log.isDebugEnabled()) {
            log.debug("generated SQL: " + strSQL);
        }
        return strSQL;
    }
}
