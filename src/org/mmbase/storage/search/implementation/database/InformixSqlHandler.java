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
import org.mmbase.module.core.MMBase;

/**
 * The Informix query handler, implements {@link
 * org.mmbase.storage.search.implementation.database.SqlHandler SqlHandler} for standard
 * Informix functionality.
 * <br />
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
 * @version $Id: InformixSqlHandler.java,v 1.6 2004-02-19 15:55:27 mark Exp $
 * @since MMBase-1.7
 */
public class InformixSqlHandler extends BasicSqlHandler implements SqlHandler {

    /** Logger instance. */
    private static Logger log
            = Logging.getLoggerInstance(InformixSqlHandler.class.getName());

    /** MMBase instance. */
    private MMBase mmbase = null;

    /**
     * Constructor.
     *
     * @param disallowedValues Map mapping disallowed table/fieldnames
     *        to allowed alternatives.
     */
    public InformixSqlHandler(Map disallowedValues) {
        super(disallowedValues);
        mmbase = MMBase.getMMBase();
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
        // XXX should table and field aliases be tested for uniqueness?

        // Test for at least 1 step and 1 field.
        if (query.getSteps().isEmpty()) {
            throw new IllegalStateException(
                    "Searchquery has no step (at least 1 step is required).");
        }
        if (query.getFields().isEmpty()) {
            throw new IllegalStateException(
                    "Searchquery has no field (at least 1 field is required).");
        }

        // Test offset set to default (= 0).
        if (query.getOffset() != SearchQuery.DEFAULT_OFFSET) {
            throw new UnsupportedOperationException(
                    "Value of offset other than "
                    + SearchQuery.DEFAULT_OFFSET + " not supported.");
        }

        // SELECT
        StringBuffer sbQuery = new StringBuffer("SELECT ");

        log.debug("query:" + query.toString());

        // Optimizer directive {+ORDERED}
        if (query.getSteps().size() > 3) {
            /*
               {+ORDERED} may not be used when using UNIONS
                 placing token, which i replace later on
            */
            sbQuery.append("$ORDERED");
        }

        // FIRST
        StringBuffer firstClause = new StringBuffer();
        if (query.getMaxNumber() != -1) {
            // Maxnumber set.
            /*
                FIRST may not be used when using UNIONS
                  placing token, which i replace later on
             */
            sbQuery.append("$FIRST");
            firstClause.append("FIRST ").
                    append(query.getMaxNumber()).
                    append(" ");
        }

        // DISTINCT
        if (query.isDistinct()) {
            sbQuery.append("DISTINCT ");
        }

        firstInChain.appendQueryBodyToSql(sbQuery, query, firstInChain);

        // $FIRST-TOKEN REPLACEMENT
        if (query.getMaxNumber() != -1) {
            if (sbQuery.indexOf("UNION") > -1) {
                // no first-clause allowed in UNION. Replace Token by nothing
                while (sbQuery.indexOf("$FIRST") > -1) {
                    sbQuery.replace(sbQuery.indexOf("$FIRST"), sbQuery.indexOf("$FIRST") + 6, "");
                }
            } else {
                // replace token by firstClause
                sbQuery.replace(sbQuery.indexOf("$FIRST"), sbQuery.indexOf("$FIRST") + 6, firstClause.toString());
            }
        }

        // $ORDERED-TOKEN REPLACEMENT
        if (sbQuery.indexOf("$ORDERED") > -1) {
            if (sbQuery.indexOf("UNION") > -1) {
                // no $ORDERED-clause allowed in UNION. Replace Token by nothing
                while (sbQuery.indexOf("$ORDERED") > -1) {
                    log.debug("3");
                    sbQuery.replace(sbQuery.indexOf("$ORDERED"), sbQuery.indexOf("$ORDERED") + 8, "");
                }
            } else {
                // replace token by firstClause
                sbQuery.replace(sbQuery.indexOf("$ORDERED"), sbQuery.indexOf("$ORDERED") + 8, "{+ORDERED} ");
            }
        }

        String strSQL = sbQuery.toString();
        if (log.isDebugEnabled()) {
            log.debug("generated SQL: " + strSQL);
        }
        return strSQL;
    }

    // javadoc is inherited
    public void appendQueryBodyToSql(StringBuffer sb, SearchQuery query, SqlHandler firstInChain)
            throws SearchQueryException {

        // Buffer expressions for included nodes, like
        // "x.number in (...)".
        StringBuffer sbNodes = new StringBuffer();

        // Buffer expressions for relations, like
        // "x.number = r.snumber AND y.number = r.dnumber".
        StringBuffer sbRelations = new StringBuffer();

        // Buffer fields to group by, like
        // "alias1, alias2, ..."
        StringBuffer sbGroups = new StringBuffer();

        boolean multipleSteps = query.getSteps().size() > 1;

        // Fields expression
        List lFields = new ArrayList();
        lFields.addAll(query.getFields());

        // When 'distinct', make sure all fields used for sorting are
        // included in the query.
        // Some databases require this (including PostgreSQL).
        // By fixing this here, the result of the query remains consistent
        // across databases, while requiring no modification in the calling
        // code.
        if (query.isDistinct()) {
            Iterator iSortOrder = query.getSortOrders().iterator();
            while (iSortOrder.hasNext()) {
                SortOrder sortOrder = (SortOrder) iSortOrder.next();
                StepField field = sortOrder.getField();
                if (lFields.indexOf(field) == -1) {
                    lFields.add(field);
                }
            }
        }

        Iterator iFields = lFields.iterator();
        while (iFields.hasNext()) {
            StepField field = (StepField) iFields.next();

            // Fieldname prefixed by table alias.
            Step step = field.getStep();
            String fieldName = field.getFieldName();
            String fieldAlias = field.getAlias();

            if (field instanceof AggregatedField) {
                int aggregationType = ((AggregatedField) field).getAggregationType();
                if (aggregationType == AggregatedField.AGGREGATION_TYPE_GROUP_BY) {

                    // Group by.
                    appendField(sb, step, fieldName, multipleSteps);

                    // Append to "GROUP BY"-buffer.
                    if (sbGroups.length() > 0) {
                        sbGroups.append(",");
                    }
                    if (fieldAlias != null) {
                        sbGroups.append(getAllowedValue(fieldAlias));
                    } else {
                        appendField(sbGroups, step,
                                fieldName, multipleSteps);
                    }
                } else {

                    // Aggregate function.
                    switch (aggregationType) {
                        case AggregatedField.AGGREGATION_TYPE_COUNT:
                            sb.append("COUNT(");
                            break;

                        case AggregatedField.AGGREGATION_TYPE_COUNT_DISTINCT:
                            sb.append("COUNT(DISTINCT ");
                            break;

                        case AggregatedField.AGGREGATION_TYPE_MIN:
                            sb.append("MIN(");
                            break;

                        case AggregatedField.AGGREGATION_TYPE_MAX:
                            sb.append("MAX(");
                            break;

                        default:
                            throw new IllegalStateException("Invalid aggregationType value: " + aggregationType);
                    }
                    appendField(sb, step, fieldName, multipleSteps);
                    sb.append(")");
                }

            } else {

                // Non-aggregate field.
                appendField(sb, step, fieldName, multipleSteps);
            }

            // Field alias.
            if (fieldAlias != null) {
                sb.append(" AS ")
                        .append(getAllowedValue(fieldAlias));
            }

            if (iFields.hasNext()) {
                sb.append(",");
            }
        }

        // vector needed to save OR-Elements for migration to UNION-query
        Vector orElements = new Vector();
        boolean relationalConstraintInBothDirections = false;

        // Tables
        sb.append(" FROM ");
        Iterator iSteps = query.getSteps().iterator();
        while (iSteps.hasNext()) {
            Step step = (Step) iSteps.next();
            String tableName = step.getTableName();
            String tableAlias = step.getAlias();

            // Tablename, prefixed with basename and underscore
            sb.append(mmbase.getBaseName()).
                    append("_").
                    //Currently no replacement strategy is implemented for
                    //invalid tablenames.
                    //This would be useful, but requires modification to
                    //the insert/update/delete code as well.
                    //append(getAllowedValue(tableName));
                    append(tableName);

            // Table alias (tablename when table alias not set).
            if (tableAlias != null) {
                sb.append(" ").
                        append(getAllowedValue(tableAlias));
            } else {
                sb.append(" ").
                        append(getAllowedValue(tableName));
            }

            if (iSteps.hasNext()) {
                sb.append(",");
            }

            // Included nodes.
            SortedSet nodes = step.getNodes();
            if (nodes.size() > 0) {
                if (sbNodes.length() > 0) {
                    sbNodes.append(" AND ");
                }
                appendField(sbNodes, step, "number", multipleSteps);
                sbNodes.append(" IN (");
                Iterator iNodes = nodes.iterator();
                while (iNodes.hasNext()) {
                    Integer node = (Integer) iNodes.next();
                    sbNodes.append(node);
                    if (iNodes.hasNext()) {
                        sbNodes.append(",");
                    }
                }
                sbNodes.append(")");
            }

            // Relation steps.
            if (step instanceof RelationStep) {
                RelationStep relationStep = (RelationStep) step;
                Step previousStep = relationStep.getPrevious();
                Step nextStep = relationStep.getNext();
                if (sbRelations.length() > 0) {
                    sbRelations.append(" AND ");
                }
                switch (relationStep.getDirectionality()) {
                    case RelationStep.DIRECTIONS_SOURCE:
                        sbRelations.append("(");
                        appendField(sbRelations, previousStep, "number", multipleSteps);
                        sbRelations.append("=");
                        appendField(sbRelations, relationStep, "dnumber", multipleSteps);
                        sbRelations.append(" AND ");
                        appendField(sbRelations, nextStep, "number", multipleSteps);
                        sbRelations.append("=");
                        appendField(sbRelations, relationStep, "snumber", multipleSteps);
                        if (relationStep.getCheckedDirectionality()) {
                            sbRelations.append(" AND ");
                            appendField(sbRelations, relationStep, "dir", multipleSteps);
                            sbRelations.append("<>1");
                        }
                        break;

                    case RelationStep.DIRECTIONS_DESTINATION:
                        sbRelations.append("(");
                        appendField(sbRelations, previousStep, "number", multipleSteps);
                        sbRelations.append("=");
                        appendField(sbRelations, relationStep, "snumber", multipleSteps);
                        sbRelations.append(" AND ");
                        appendField(sbRelations, nextStep, "number", multipleSteps);
                        sbRelations.append("=");
                        appendField(sbRelations, relationStep, "dnumber", multipleSteps);
                        break;

                    case RelationStep.DIRECTIONS_BOTH:

                        // set to true
                        relationalConstraintInBothDirections = true;


                        if (relationStep.getRole() != null) {
                            sbRelations.append("(((");
                        } else {
                            sbRelations.append("((");
                        }
                        appendField(sbRelations, previousStep, "number", multipleSteps);
                        sbRelations.append("=");
                        appendField(sbRelations, relationStep, "dnumber", multipleSteps);
                        sbRelations.append(" AND ");
                        appendField(sbRelations, nextStep, "number", multipleSteps);
                        sbRelations.append("=");
                        appendField(sbRelations, relationStep, "snumber", multipleSteps);
                        if (relationStep.getCheckedDirectionality()) {
                            sbRelations.append(" AND ");
                            appendField(sbRelations, relationStep, "dir", multipleSteps);
                            sbRelations.append("<>1");
                        }
                        sbRelations.append(") OR (");

                        appendField(sbRelations, previousStep, "number", multipleSteps);
                        sbRelations.append("=");
                        appendField(sbRelations, relationStep, "snumber", multipleSteps);
                        sbRelations.append(" AND ");
                        appendField(sbRelations, nextStep, "number", multipleSteps);
                        sbRelations.append("=");
                        appendField(sbRelations, relationStep, "dnumber", multipleSteps);
                        if (relationStep.getRole() != null) {
                            sbRelations.append("))");
                        } else {
                            sbRelations.append(")");
                        }

                        // here starts the code to gather al the OR- elements for the union
                        // start of First element
                        StringBuffer orElement = new StringBuffer();
                        orElement.append("(");

                        appendField(orElement, previousStep, "number", multipleSteps);
                        orElement.append("=");
                        appendField(orElement, relationStep, "dnumber", multipleSteps);
                        orElement.append(" AND ");
                        appendField(orElement, nextStep, "number", multipleSteps);
                        orElement.append("=");
                        appendField(orElement, relationStep, "snumber", multipleSteps);
                        if (relationStep.getCheckedDirectionality()) {
                            orElement.append(" AND ");
                            appendField(orElement, relationStep, "dir", multipleSteps);
                            orElement.append("<>1");
                        }

                        if (relationStep.getRole() != null) {
                            // ooops role  is given lets add the rnumber
                            orElement.append(" AND ");
                            appendField(orElement, relationStep, "rnumber", multipleSteps);
                            orElement.append("=").
                                    append(relationStep.getRole());
                        }

                        orElement.append(")");
                        orElements.add(orElement);

                        // Start of second element
                        orElement = new StringBuffer();
                        orElement.append("(");

                        appendField(orElement, previousStep, "number", multipleSteps);
                        orElement.append("=");
                        appendField(orElement, relationStep, "snumber", multipleSteps);
                        orElement.append(" AND ");
                        appendField(orElement, nextStep, "number", multipleSteps);
                        orElement.append("=");
                        appendField(orElement, relationStep, "dnumber", multipleSteps);

                        if (relationStep.getRole() != null) {
                            // ooops role  is given lets add the rnumber
                            orElement.append(" AND ");
                            appendField(orElement, relationStep, "rnumber", multipleSteps);
                            orElement.append("=").
                                    append(relationStep.getRole());
                        }

                        orElement.append(")");
                        orElements.add(orElement);

                        relationalConstraintInBothDirections = true;
                        // end of hacking
                        break;

                    default: // Invalid directionality value.
                        throw new IllegalStateException(
                                "Invalid directionality value: " + relationStep.getDirectionality());
                }
                if (relationStep.getRole() != null) {
                    sbRelations.append(" AND ");
                    appendField(sbRelations, relationStep, "rnumber", multipleSteps);
                    sbRelations.append("=").
                            append(relationStep.getRole());
                }
                sbRelations.append(")");
            }
        }

        // Constraints
        StringBuffer sbConstraints = new StringBuffer();
        sbConstraints.append(sbNodes); // Constraints by included nodes.
        if (sbConstraints.length() > 0 && sbRelations.length() > 0) {
            sbConstraints.append(" AND ");
        }

        /*
           If there are relational Constraints in both directions
           we need to
           1) figure out the contraints that we use in each of the UNION-constraints
           2) combine possible different OR-Elements
           3) create the query with all the UNIONS
           4) add the GROUP BY Clause
           5) add the ORDER BY Clause
        */
        StringBuffer unionRelationConstraints = new StringBuffer();
        if (relationalConstraintInBothDirections) {
            // 1)
            // we first need to figure out the additional constraints in
            // order to add them to the relational constraints
            StringBuffer unionConstraints = new StringBuffer();
            if (query.getConstraint() != null) {
                Constraint constraint = query.getConstraint();
                if (sbConstraints.length() > 0) {
                    // Combine constraints.
                    unionConstraints.append(" AND ");
                    if (constraint instanceof CompositeConstraint) {
                        appendCompositeConstraintToSql(
                                unionConstraints, (CompositeConstraint) constraint,
                                query, false, true, firstInChain);
                    } else {
                        firstInChain.appendConstraintToSql(
                                unionConstraints, constraint, query,
                                false, true);
                    }
                } else {
                    // Only regular constraints.
                    if (constraint instanceof CompositeConstraint) {
                        appendCompositeConstraintToSql(
                                unionConstraints, (CompositeConstraint) constraint,
                                query, false, false, firstInChain);
                    } else {
                        firstInChain.appendConstraintToSql(
                                unionConstraints, constraint, query,
                                false, false);
                    }
                }
            }

            /*
               2) Combine the OR-Elements
               Nested Looping through the OR-Elements to make unique combinations
            */
            Vector combinedElements = new Vector();
            for (int counter = 0; counter < orElements.size(); counter++) {
                for (int counter2 = counter; counter2 < orElements.size(); counter2++) {
                    // Dont combine  with same element. That doesn't make sense
                    // If there are just two relation-constraint-elements, we don't need to combine
                    if (counter != counter2 && orElements.size() > 2) {
                        // also add the additinal constraints
                        combinedElements.addElement(orElements.elementAt(counter) + " AND " + orElements.elementAt(counter2) + unionConstraints);
                    } else {
                        // If there's just one OR (two OR-elements), add the elements seperately
                        // also add the additinal constraints
                        if (counter == counter2 && orElements.size() <= 2) combinedElements.addElement(orElements.elementAt(counter) + "" + unionConstraints);
                    }
                }
            }

            /*
               3) Okay, here we're goin to create a new BaseQuery,
               we need that in  order to re-use that for every union
            */
            Enumeration e = combinedElements.elements();
            String combinedElement = "";
            int teller = 1;

            // New base-query. We need to reuse that for every union
            StringBuffer baseQuery = new StringBuffer();
            baseQuery.append(sb);   // add "Select ... From ..."

            // add the constraints
            if (sbConstraints.length() + combinedElements.size() > 0) {
                baseQuery.append(" WHERE ").
                        append(sbConstraints.toString());
            }

            // now add the combined relation-constraints as UNIONS
            while (e.hasMoreElements()) {
                combinedElement = (String) e.nextElement();
                if (teller != 1) {
                    unionRelationConstraints.append(" UNION ").append(baseQuery);
                }
                unionRelationConstraints.append(" " + combinedElement + " ");
                teller++;
            }

            // add the stuff to sb
            sb.append(" WHERE ").append(unionRelationConstraints);

            /*
               4) add GROUP BY Clause
            */
            /* ToDo: implement group by

            if (sbGroups.length() > 0) {
                sb.append(" GROUP BY ").
                        append(sbGroups.toString());
            }
            */

            /*
                5) adding ORDER BY
                   eg. ORDER BY 1,3,4
            */
            List sortOrders = query.getSortOrders();
            if (sortOrders.size() > 0) {
                sb.append(" ORDER BY ");
                Iterator iSortOrders = sortOrders.iterator();
                while (iSortOrders.hasNext()) {
                    SortOrder sortOrder = (SortOrder) iSortOrders.next();

                    // Field alias.
                    String fieldAlias = sortOrder.getField().getAlias();
                    Step step = sortOrder.getField().getStep();
                    StringBuffer orderByField = new StringBuffer();
                    if (fieldAlias != null) {
                        orderByField.append(getAllowedValue(fieldAlias));
                    } else {
                        appendField(orderByField, step, sortOrder.getField().getFieldName(), multipleSteps);
                    }

                    // Loop through the fields until we find a match

                    for (int i = 0; i < query.getFields().size(); i++) {
                        StepField sf = (StepField) query.getFields().get(i);
                        String field = sf.getStep().getAlias() + "." + sf.getFieldName();

                        // search for the matching field to obtain the number of the field
                        if (field.equals(orderByField.toString())) {
                            // match found
                            sb.append((i + 1) + " ");
                        }
                    }

                    // Sort direction.
                    switch (sortOrder.getDirection()) {
                        case SortOrder.ORDER_ASCENDING:
                            sb.append(" ASC");
                            break;

                        case SortOrder.ORDER_DESCENDING:
                            sb.append(" DESC");
                            break;

                        default: // Invalid direction value.
                            throw new IllegalStateException(
                                    "Invalid direction value: " + sortOrder.getDirection());
                    }

                    if (iSortOrders.hasNext()) {
                        sb.append(",");
                    }
                }
            }
            log.debug("Completed generation of UNION query:" + sb.toString());
        } else {
            // if no OR it it does this
            sbConstraints.append(sbRelations); // Constraints by relations.
            if (query.getConstraint() != null) {
                Constraint constraint = query.getConstraint();
                if (sbConstraints.length() > 0) {
                    // Combine constraints.
                    sbConstraints.append(" AND ");
                    if (constraint instanceof CompositeConstraint) {
                        appendCompositeConstraintToSql(
                                sbConstraints, (CompositeConstraint) constraint,
                                query, false, true, firstInChain);
                    } else {
                        firstInChain.appendConstraintToSql(
                                sbConstraints, constraint, query,
                                false, true);
                    }
                } else {
                    // Only regular constraints.
                    if (constraint instanceof CompositeConstraint) {
                        appendCompositeConstraintToSql(
                                sbConstraints, (CompositeConstraint) constraint,
                                query, false, false, firstInChain);
                    } else {
                        firstInChain.appendConstraintToSql(
                                sbConstraints, constraint, query,
                                false, false);
                    }
                }
            }
            if (sbConstraints.length() > 0) {
                sb.append(" WHERE ").
                        append(sbConstraints.toString());
            }

            // GROUP BY
            if (sbGroups.length() > 0) {
                sb.append(" GROUP BY ").
                        append(sbGroups.toString());
            }

            // ORDER BY
            List sortOrders = query.getSortOrders();
            if (sortOrders.size() > 0) {
                sb.append(" ORDER BY ");
                Iterator iSortOrders = sortOrders.iterator();
                while (iSortOrders.hasNext()) {
                    SortOrder sortOrder = (SortOrder) iSortOrders.next();

                    // Field alias.
                    String fieldAlias = sortOrder.getField().getAlias();
                    Step step = sortOrder.getField().getStep();
                    if (fieldAlias != null) {
                        sb.append(getAllowedValue(fieldAlias));
                    } else {
                        appendField(sb, step, sortOrder.getField().getFieldName(), multipleSteps);
                    }

                    // Sort direction.
                    switch (sortOrder.getDirection()) {
                        case SortOrder.ORDER_ASCENDING:
                            sb.append(" ASC");
                            break;

                        case SortOrder.ORDER_DESCENDING:
                            sb.append(" DESC");
                            break;

                        default: // Invalid direction value.
                            throw new IllegalStateException(
                                    "Invalid direction value: " + sortOrder.getDirection());
                    }

                    if (iSortOrders.hasNext()) {
                        sb.append(",");
                    }
                }
            }
        }
    }
}
