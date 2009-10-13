/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search.implementation.database;

import org.mmbase.storage.search.*;
import org.mmbase.util.logging.*;
import java.util.*;
import org.mmbase.module.corebuilders.RelDef;
import org.mmbase.module.core.MMObjectNode;

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
 * @version $Id$
 * @since MMBase-1.7
 */
public class PostgreSqlSqlHandler extends BasicSqlHandler implements SqlHandler {

    private static final Logger log = Logging.getLoggerInstance(PostgreSqlSqlHandler.class);

    /**
     * Constructor.
     */
    public PostgreSqlSqlHandler() {
        super();
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


    protected boolean useLower(FieldCompareConstraint constraint) {
        if (constraint.getOperator() == FieldCompareConstraint.LIKE) {
            return false;
        } else {
            return true;
        }
    }


    protected StringBuffer appendLikeOperator(StringBuffer sb, boolean caseSensitive) {
        if (caseSensitive) {
            sb.append(" LIKE ");
        } else {
            sb.append(" ILIKE ");
        }
        return sb;
    }


    /// TODO: Needs to determine the value of this (select 'a' > 'A' or so?)
    private final boolean localeMakesCaseInsensitive = false;

    /**
     * Normally, Postgresql does not sort case senstively, so we should not sort on
     * UPPER(fieldname). This is mainly very bad if the query is also distinct. (ERROR: for SELECT
     * DISTINCT, ORDER BY expressions must appear in select list), may occur.
     */
    protected StringBuffer appendSortOrderField(StringBuffer sb, SortOrder sortOrder, boolean multipleSteps, SearchQuery query) {
        if (localeMakesCaseInsensitive) {
            if (sortOrder.isCaseSensitive()) {
                log.warn("Don't now how to sort case sensitively if the locale make case insensitive in Postgresql for " + sortOrder + " it will be ignored.");
            }
            appendField(sb, sortOrder, multipleSteps);
            return sb;
        } else {
            if (query.isDistinct() && ! sortOrder.isCaseSensitive()) {
                StepField sf = sortOrder.getField();
                if (sf.getType() == org.mmbase.bridge.Field.TYPE_STRING ) {
                    log.warn("With a case sensitive locale, it is impossible to sort a distinct query case insensitively. Will sort it case sensitively in stead: " + sortOrder);
                }
                appendField(sb, sortOrder, multipleSteps);
                return sb;
            } else {
                return super.appendSortOrderField(sb, sortOrder, multipleSteps);
            }
        }
    }
    /*
    protected StringBuffer appendRegularExpressionOperator(StringBuffer sb, boolean caseSensitive) {
        if (caseSensitive) {
            sb.append(" ~ ");
        } else {
            sb.append(" ~* ");
        }
        return sb;
    }
    */

    /**
     * <a href="http://www.postgresql.org/docs/7.4/static/functions-datetime.html">date time
     * functions</a>
     *
     * @javadoc
     */
    protected void appendDateField(StringBuffer sb, Step step, String fieldName, boolean multipleSteps, int datePart) {
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
            datePartFunction = "DOY";
            break;
        case FieldValueDateConstraint.DAY_OF_WEEK:
            datePartFunction = "DOW";
            break;
        case FieldValueDateConstraint.MILLISECOND:
            datePartFunction = "MILLISECONDS";
            break;
        default:
            log.debug("Unknown datePart " + datePart);
        }
        if (datePartFunction != null) {
            sb.append("EXTRACT(");
            sb.append(datePartFunction);
            sb.append(" FROM ");
            appendField(sb, step, fieldName, multipleSteps);
            sb.append(')');
        } else {
            // others are supported in super..
            super.appendDateField(sb, step, fieldName, multipleSteps, datePart);
        }
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


    /**
     * Optimizes postgresql queries by adding the ONLY keyword to a relation-table, provided that the
     * role was given (and therefor the selection only applies to the given table).
     *
     * @see org.mmbase.storage.search.implementation.database.BasicSqlHandler#appendTableName(java.lang.StringBuffer, org.mmbase.storage.search.Step)
     */
    protected void appendTableName(StringBuffer sb, Step step) {
        if(step instanceof RelationStep) {
            RelationStep rs = (RelationStep) step;
            if (rs.getRole() != null) {
                if (log.isDebugEnabled()) {
                    log.debug("Adding ONLY keyword to tablename " + step.getTableName());
                }
                sb.append(" ONLY ");
            } else {
                org.mmbase.module.core.MMBase mmbase = org.mmbase.module.core.MMBase.getMMBase();
                // no role specified, check if more than one role on sub tables are possible...
                int sourceBuilder = mmbase.getBuilder(rs.getPrevious().getTableName()).getObjectType();
                int destinationBuilder = mmbase.getBuilder(rs.getNext().getTableName()).getObjectType();
                int directionality = rs.getDirectionality();
                RelDef reldef = mmbase.getRelDef();
                Set tables = new HashSet();
                Iterator allowed = mmbase.getTypeRel().getAllowedRelations(sourceBuilder, destinationBuilder, 0, directionality).iterator();
                while(allowed.hasNext()) {
                    MMObjectNode typeRel = (MMObjectNode) allowed.next();
                    int rnumber = typeRel.getIntValue("rnumber");
                    tables.add(reldef.getBuilder(rnumber).getTableName());
                }
                if (tables.size() == 1) {
                    if (log.isDebugEnabled()) {
                        log.debug("No role defined but only one table possible (" + tables + "), adding with ONLY");
                    }
                    sb.append(" ONLY ").
                        append(mmbase.getBaseName()).
                        append('_').
                        append((String) tables.iterator().next());
                    appendTableAlias(sb, step);
                    return;
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("Not adding ONLY to table name because role of " + step + " is null, and the following tables are possible " + tables);
                    }
                }
            }
        }
        super.appendTableName(sb, step);
    }
}
