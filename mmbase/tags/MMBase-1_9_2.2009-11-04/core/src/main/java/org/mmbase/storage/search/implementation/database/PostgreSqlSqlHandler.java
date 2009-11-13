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
import org.mmbase.module.corebuilders.TypeRel;

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


    private boolean localeMakesCaseInsensitive = false;

    /**
     * Constructor.
     */
    public PostgreSqlSqlHandler() {
        super();
        /* TODO: make this work..
        DataSource ds =  ((org.mmbase.storage.implementation.database.DatabaseStorageManagerFactory) org.mmbase.module.core.MMBase.getMMBase().getStorageManagerFactory()).getDataSource();
        Connection con = null;
        Statement statement = null;
        ResultSet results = null;
        try {
            con = ds.getConnection();
            statement = con.createStatement();
            results = statement.executeQuery("select 'ab' > 'Ac'");
            results.next();
            localeMakesCaseInsensitive = results.getBoolean(0);
        } catch (Exception e) {
            log.error(e);
        } finally {
            if (results != null) try { results.close(); } catch (Exception e) {};
            if (statement != null) try { statement.close(); } catch (Exception e) {};
            if (con != null) try { con.close(); } catch (Exception e) {};
        }
        log.info("Postgresql database instance is case " + (localeMakesCaseInsensitive ? "INSENSITIVE" : "SENSITIVE") + " (because of Locale settings)");
        */

    }

    // javadoc is inherited
    @Override public int getSupportLevel(int feature, SearchQuery query) throws SearchQueryException {
        int result;
        switch (feature) {
        case SearchQueryHandler.FEATURE_MAX_NUMBER:
            result = SearchQueryHandler.SUPPORT_OPTIMAL;
            break;

        case SearchQueryHandler.FEATURE_OFFSET:
            result = SearchQueryHandler.SUPPORT_OPTIMAL;
            break;
        case SearchQueryHandler.FEATURE_REGEXP:
            result = SearchQueryHandler.SUPPORT_OPTIMAL;
            break;
        default:
            result = super.getSupportLevel(feature, query);
        }
        return result;
    }


    @Override protected boolean useLower(FieldConstraint constraint) {
        if (constraint instanceof FieldCompareConstraint) {
            return  ((FieldCompareConstraint) constraint).getOperator() != FieldCompareConstraint.LIKE; // we can use  ILIKE
        } else {
            return true;
        }
    }


    @Override protected StringBuilder appendLikeOperator(StringBuilder sb, FieldConstraint constraint) {
        if (constraint.isCaseSensitive()) {
            sb.append(" LIKE ");
        } else {
            sb.append(" ILIKE ");
        }
        return sb;
    }


    /**
     * Normally, Postgresql does not sort case senstively, so we should not sort on
     * UPPER(fieldname). This is mainly very bad if the query is also distinct. (ERROR: for SELECT
     * DISTINCT, ORDER BY expressions must appear in select list), may occur.
     */
    @Override protected StringBuilder appendSortOrderField(StringBuilder sb, SortOrder sortOrder, boolean multipleSteps, SearchQuery query) {
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

    @Override protected StringBuilder appendRegularExpressionOperator(StringBuilder sb, FieldConstraint constraint) {
        if (constraint.isCaseSensitive()) {
            sb.append(" ~ ");
        } else {
            sb.append(" ~* ");
        }
        return sb;
    }

    /**
     * <a href="http://www.postgresql.org/docs/7.4/static/functions-datetime.html">date time
     * functions</a>
     *
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
    @Override
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
        StringBuilder sbQuery = new StringBuilder("SELECT ");

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
     * @see org.mmbase.storage.search.implementation.database.BasicSqlHandler#appendTableName(java.lang.StringBuilder, org.mmbase.storage.search.Step)
     */
    @Override
    protected void appendTableName(StringBuilder sb, Step step) {
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
                int searchDir = rs.getDirectionality();
                RelDef reldef = mmbase.getRelDef();

                // TODO it is not necessary to determin the full table, because the only used
                // informations are:
                // 1. whether it has 1 entry.
                // 2. if it has, which it is.
                //
                // TODO Seems to be a bit of code-duplication from TypeRel#optimizeRelationStep,
                // perhaps an extra helper method must be created in TypeRel.

                Set<String> tables = new HashSet<String>();
                TypeRel typeRel = mmbase.getTypeRel();
                for (Integer rnumber : reldef.getRoles()) {
                    log.debug(" considering role " + rnumber + "(" + reldef.getNode(rnumber).getStringValue("sname"));
                    boolean sourceToDestination =
                        searchDir != RelationStep.DIRECTIONS_SOURCE
                        && typeRel.contains(sourceBuilder, destinationBuilder, rnumber, TypeRel.INCLUDE_PARENTS_AND_DESCENDANTS);
                    boolean destinationToSource =
                        searchDir != RelationStep.DIRECTIONS_DESTINATION
                        && typeRel.contains(destinationBuilder, sourceBuilder, rnumber, TypeRel.INCLUDE_PARENTS_AND_DESCENDANTS);

                    if (sourceToDestination || destinationToSource) {
                        tables.add(reldef.getBuilder(rnumber).getTableName());
                    }
                }
                if (tables.size() == 1) {
                    if (log.isDebugEnabled()) {
                        log.debug("No role defined but only one table possible (" + tables + "), adding with ONLY");
                    }
                    sb.append(" ONLY ").
                        append(mmbase.getBaseName()).
                        append('_').
                        append( tables.iterator().next());
                    appendTableAlias(sb, step);
                    return;
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("Not adding ONLY to table name because role of " + step + " is null, and the following tables are possible " + tables);
                    }
                    // falling back to super.
                }
            }
        }
        super.appendTableName(sb, step);
    }
}
