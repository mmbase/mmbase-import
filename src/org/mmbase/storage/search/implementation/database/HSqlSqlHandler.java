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
 * The HSQL query handler, implements {@link
 * org.mmbase.storage.search.implementation.database.SqlHandler SqlHandler} for standard
 * hsql functionality.

 * @author Michiel Meeuwissen
 * @version $Id: HSqlSqlHandler.java,v 1.2 2003-11-27 17:58:42 robmaris Exp $
 * @since MMBase-1.7
 */
public class HSqlSqlHandler extends BasicSqlHandler implements SqlHandler {

    /** Logger instance. */
    private static Logger log = Logging.getLoggerInstance(HSqlSqlHandler.class.getName());

    /**
     * Constructor.
     *
     * @param disallowedValues Map mapping disallowed table/fieldnames
     *        to allowed alternatives.
     */
    public HSqlSqlHandler(Map disallowedValues) {
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
            throw new IllegalStateException( "Searchquery has no step (at leas 1 step is required).");
        }
        if (query.getFields().isEmpty()) {
            throw new IllegalStateException("Searchquery has no field (at least 1 field is required).");
        }

        // SELECT
        StringBuffer sbQuery = new StringBuffer("SELECT ");



        // OFFSET & LIMIT
        int offset = query.getOffset();
        int limit  = query.getMaxNumber();
        if (offset != 0 || limit > -1) {
            if (offset == 0) {
                sbQuery.append("TOP ").append(limit).append(' ');
            } else {
                if (limit == -1) {
                    sbQuery.append("LIMIT ").append(offset).append(" 0 ");
                } else {
                    sbQuery.append("LIMIT ").append(offset).append(' ').append(limit).append(' ');
                    // what if 'limit' == 0?
                    // logically, that should give no results, but it will now be interpreted as -1
                }
            }

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
