/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search.implementation.database;

import org.mmbase.storage.search.*;
import org.mmbase.util.logging.*;

/**
 * The HSQL query handler, implements {@link
 * org.mmbase.storage.search.implementation.database.SqlHandler SqlHandler} for standard
 * hsql functionality.

 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.7
 */
public class HSqlSqlHandler extends BasicSqlHandler implements SqlHandler {

    private static final Logger log = Logging.getLoggerInstance(HSqlSqlHandler.class);

    /**
     * Constructor.
     */
    public HSqlSqlHandler() {
        super();
    }

    // javadoc is inherited
    @Override
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

    /**
     * @javadoc
     */
    @Override
    protected void appendDateField(StringBuilder sb, Step step, String fieldName, boolean multipleSteps, int datePart) {
        String datePartFunction = null;
        switch (datePart) {
        case FieldValueDateConstraint.CENTURY:
            datePartFunction = "CENTURY";
            break;
        case FieldValueDateConstraint.QUARTER:
            datePartFunction = "QUARTER";
            break;
        case FieldValueDateConstraint.WEEK:
            datePartFunction = "WEEK";
            break;
        case FieldValueDateConstraint.DAY_OF_YEAR:
            datePartFunction = "DAYOFYEAR";
            break;
        case FieldValueDateConstraint.DAY_OF_WEEK:
            datePartFunction = "DAYOFWEEK";
            break;
        default:
            log.debug("Unknown datePart " + datePart);
        }
        if (datePartFunction != null) {
            sb.append(datePartFunction);
            sb.append("(");
            appendField(sb, step, fieldName, multipleSteps);
            sb.append(")");
        } else {
            super.appendDateField(sb, step, fieldName, multipleSteps, datePart);
        }
    }

    // javadoc is inherited
    @Override
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
        StringBuilder sbQuery = new StringBuilder("SELECT ");



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
            log.debug("generated SQL: " + query + " -- >" + strSQL);
        }
        return strSQL;
    }
}
