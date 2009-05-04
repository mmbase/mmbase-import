/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search.implementation.database;

import java.util.*;
import java.sql.*;
import java.lang.reflect.Method;
import org.mmbase.module.core.MMBase;

import org.mmbase.storage.search.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import org.mmbase.module.database.MultiConnection;

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
 * @version $Id$
 * @since MMBase-1.7
 */
public class InformixSqlHandler extends BasicSqlHandler implements SqlHandler {

    public static final String  ADD_ORDERED = "informix-query-optimizer-ordered";
    /**
     * Logger instance.
     */
    private static Logger log = Logging.getLoggerInstance(InformixSqlHandler.class.getName());

    /**
     * Constructor.
     */
    public InformixSqlHandler() {
        super();
    }

    /**
     * Dertermine if the given query will turn out to be a UNION query
     *
     * @param query
     * @return <code>true</code> if the given query will contain a UNION
     */
    private boolean isUnionQuery(SearchQuery query) {
        Iterator iSteps = query.getSteps().iterator();
        while (iSteps.hasNext()) {
            Step step = (Step) iSteps.next();
            if (step instanceof RelationStep) {
                RelationStep relationStep = (RelationStep) step;
                // If the query contains RelationSteps that are bi-directional
                // then the query will turn out to be a union query.
                if (relationStep.getDirectionality() == RelationStep.DIRECTIONS_BOTH) {
                    return true;
                }
            }
        }
        return false;
    }

    // javadoc is inherited
    public int getSupportLevel(int feature, SearchQuery query) throws SearchQueryException {
        int result;
        switch (feature) {
            case SearchQueryHandler.FEATURE_MAX_NUMBER:

                if (isUnionQuery(query)) {
                    result = SearchQueryHandler.SUPPORT_NONE;
                } else {
                    result = SearchQueryHandler.SUPPORT_OPTIMAL;
                }
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
            throw new IllegalStateException("Searchquery has no step (at least 1 step is required).");
        }
        if (query.getFields().isEmpty()) {
            throw new IllegalStateException("Searchquery has no field (at least 1 field is required).");
        }

        // Test offset set to default (= 0).
        if (query.getOffset() != SearchQuery.DEFAULT_OFFSET) {
            throw new UnsupportedOperationException("Value of offset other than "
                    + SearchQuery.DEFAULT_OFFSET + " not supported.");
        }

        // SELECT
        StringBuffer sbQuery = new StringBuffer("SELECT ");

        if (log.isDebugEnabled()) {
            log.trace("query:" + query.toString());
        }

        if (!isUnionQuery(query)) {
            /*
               Optimizer directive {+ORDERED} may not be used when using UNIONS
            */
            if (MMBase.getMMBase().getStorageManagerFactory().hasOption(ADD_ORDERED)) {

                if (query.getSteps().size() > 3) {
                    sbQuery.append("{+ORDERED} ");
                }
            }

            /*
               FIRST may not be used when using UNIONS
            */
            if (query.getMaxNumber() != -1) {
                sbQuery.append("FIRST ").
                        append(query.getMaxNumber()).
                        append(" ");
            }
        }

        // DISTINCT
        if (query.isDistinct() && !query.isAggregating()) {
            sbQuery.append("DISTINCT ");
        }

        firstInChain.appendQueryBodyToSql(sbQuery, query, firstInChain);

        return sbQuery.toString();
    }

    // javadoc is inherited
    public void appendQueryBodyToSql(StringBuffer sb, SearchQuery query, SqlHandler firstInChain) throws SearchQueryException {

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

        boolean storesAsFile = org.mmbase.module.core.MMBase.getMMBase().getStorageManagerFactory().hasOption(org.mmbase.storage.implementation.database.Attributes.STORES_BINARY_AS_FILE);
        Iterator iFields = lFields.iterator();
        boolean appended = false;
        while (iFields.hasNext()) {
            StepField field = (StepField) iFields.next();
            if (field.getType() == org.mmbase.bridge.Field.TYPE_BINARY) continue;
            if (appended) {
                sb.append(',');
            }
            appended = true;

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

        }

        if (log.isDebugEnabled()) {
            log.trace("Base field part of query : " + sb);
        }

        // vector to save OR-Elements (Searchdir=BOTH) for migration to UNION-query
        List orElements = new ArrayList();

        // save AND-Elements from relationString for migration to UNION-query
        StringBuffer andElements = new StringBuffer();

        // Tables
        sb.append(" FROM ");
        Iterator iSteps = query.getSteps().iterator();
        while (iSteps.hasNext()) {
            Step step = (Step) iSteps.next();

            appendTableName(sb, step);

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
            if (log.isDebugEnabled()) {
                log.trace("Node constraint string : " + sbNodes);
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

                        // Gather the "And"-elements
                        if (andElements.length() > 0) {
                            andElements.append(" AND ");
                        }
                        appendField(andElements, previousStep, "number", multipleSteps);
                        andElements.append("=");
                        appendField(andElements, relationStep, "dnumber", multipleSteps);
                        andElements.append(" AND ");
                        appendField(andElements, nextStep, "number", multipleSteps);
                        andElements.append("=");
                        appendField(andElements, relationStep, "snumber", multipleSteps);
                        if (relationStep.getCheckedDirectionality()) {
                            andElements.append(" AND ");
                            appendField(andElements, relationStep, "dir", multipleSteps);
                            andElements.append("<>1");
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

                        // Gather the "And"-elements
                        if (andElements.length() > 0) {
                            andElements.append(" AND ");
                        }

                        appendField(andElements, previousStep, "number", multipleSteps);
                        andElements.append("=");
                        appendField(andElements, relationStep, "snumber", multipleSteps);
                        andElements.append(" AND ");
                        appendField(andElements, nextStep, "number", multipleSteps);
                        andElements.append("=");
                        appendField(andElements, relationStep, "dnumber", multipleSteps);

                        break;

                    case RelationStep.DIRECTIONS_BOTH:

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

                        // Gather al the OR- elements for the union
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
                            // role is given lets add the rnumber
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

                        // end of hacking
                        break;

                    case RelationStep.DIRECTIONS_ALL:
                        throw new UnsupportedOperationException("Directionality 'ALL' is not (yet) supported");

                    case RelationStep.DIRECTIONS_EITHER:
                        throw new UnsupportedOperationException("Directionality 'EITHER' is not (yet) supported");

                    default: // Invalid directionality value.
                        throw new IllegalStateException("Invalid directionality value: " + relationStep.getDirectionality());
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

        if (log.isDebugEnabled()) {
            log.trace("Relation string : " + sbRelations);
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
        if (isUnionQuery(query)) {
            // 1)
            // we first need to figure out the additional constraints in
            // order to add them to the relational constraints
            StringBuffer unionConstraints = new StringBuffer();
            if (query.getConstraint() != null) {
                Constraint constraint = query.getConstraint();
                if (sbConstraints.length() > 0) {
                    // Combine constraints.
                    // if sbConstraints allready ends with " AND " before adding " AND "
                    if (log.isDebugEnabled()) {
                        log.debug("sbConstraints:" + sbConstraints);
                        log.debug("sbConstraints.length:" + sbConstraints.length());
                    }

                    // have to check if the constraint end with "AND ", sometimes it's not :-(
                    if (sbConstraints.length() >= 4) {
                        if (!sbConstraints.substring(sbConstraints.length() - 4, sbConstraints.length()).equals("AND ")) {
                            unionConstraints.append(" AND ");
                        }
                    }

                    if (constraint instanceof CompositeConstraint) {
                        appendCompositeConstraintToSql(unionConstraints, (CompositeConstraint) constraint,
                                query, false, true, firstInChain);
                    } else {
                        firstInChain.appendConstraintToSql(unionConstraints, constraint, query,
                                false, true);
                    }
                } else {
                    // Only regular constraints.
                    if (constraint instanceof CompositeConstraint) {
                        appendCompositeConstraintToSql(unionConstraints, (CompositeConstraint) constraint,
                                query, false, false, firstInChain);
                    } else {
                        firstInChain.appendConstraintToSql(unionConstraints, constraint, query,
                                false, false);
                    }
                }
            }

            if (log.isDebugEnabled()) {
                log.trace("Union constraint : " + unionConstraints);
            }

            /*
               2) Combine the OR-Elements
               Nested Looping through the OR-Elements to make unique combinations
            */

            if (unionConstraints.length() > 0) {
                unionConstraints.insert(0, " AND ");
            }

            List combinedElements = new ArrayList();
            boolean skipCombination = false;
            for (int counter = 0; counter < orElements.size(); counter++) {
                for (int counter2 = counter; counter2 < orElements.size(); counter2++) {
                    // determine if the combination is valid and may be added to the relational constraints
                    if (counter%2==0 && counter2-counter==1) {
                       // this combination may not be added
                       skipCombination=true;
                    } else {
                       skipCombination=false;
                    }
                    // Don't combine  with same element. That doesn't make sense
                    // If there are just two relation-constraint-elements, we don't need to combine
                    if (counter != counter2 && orElements.size() > 2) {
                        // also add the additinal constraints
                        if (!skipCombination) {
                            combinedElements.add(orElements.get(counter) + " AND " + orElements.get(counter2) + unionConstraints);
                        }
                    } else {
                        // If there's just one OR (two OR-elements), add the elements seperately
                        // also add the additinal constraints
                        if (counter == counter2 && orElements.size() <= 2) {
                            combinedElements.add(orElements.get(counter) + "" + unionConstraints);
                        }
                    }
                }
            }

            /*
               3) Here we're going to create a new BaseQuery,
                  we need that in order to re-use that for every union
            */
            Iterator e = combinedElements.iterator();
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

            if (log.isDebugEnabled()) {
                log.trace("Base query including fields and tables : " + sb);
            }

            // now add the combined relation-constraints as UNIONS
            while (e.hasNext()) {
                combinedElement = (String) e.next();
                if (teller != 1) {
                    if (sb.indexOf("COUNT") > -1) {
                        unionRelationConstraints.append(" UNION ALL ").append(baseQuery);
                    } else {
                        unionRelationConstraints.append(" UNION ").append(baseQuery);
                    }
                }

                // Make sure the unionRelationConstraint ends with " AND " or a " WHERE"
                if (unionRelationConstraints.length() >= 4) {
                    if (!unionRelationConstraints.substring(unionRelationConstraints.length() - 4, unionRelationConstraints.length()).equals("AND ") && !unionRelationConstraints.substring(unionRelationConstraints.length() - 6, unionRelationConstraints.length()).equals("WHERE ")) {
                        unionRelationConstraints.append(" AND ");
                    }
                }

                if (andElements.length() > 0) {
                    unionRelationConstraints.append(andElements).append(" AND ");
                }

                unionRelationConstraints.append(" " + combinedElement + " ");

                if (log.isDebugEnabled()) {
                    log.trace("Union relation constraint " + teller + " : " + unionRelationConstraints);
                }
                teller++;
            }

            // add the stuff to sb
            if (sbConstraints.length() > 0) {
                sb.append(" WHERE ").append(sbConstraints.toString()).append(unionRelationConstraints);
            } else {
                sb.append(" WHERE ").append(unionRelationConstraints);
            }

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
                    boolean found = false;
                    for (int i = 0; i < query.getFields().size(); i++) {
                        StepField sf = (StepField) query.getFields().get(i);
                        String field = sf.getStep().getAlias() + "." + sf.getFieldName();

                        // search for the matching field to obtain the number of the field
                        if (field.equals(orderByField.toString())) {
                            // match found
                            sb.append((i + 1) + " ");
                            // prevent that the field is listed twice in this order-by
                            found = true;
                            break;
                        }
                    }
                      if (! found) {
                          throw new RuntimeException("Could not find the field " + orderByField + " in " + query.getFields() + " !");
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
                            throw new IllegalStateException("Invalid direction value: " + sortOrder.getDirection());
                    }

                    if (iSortOrders.hasNext()) {
                        sb.append(",");
                    }
                }
            }
            log.debug("Completed generation of UNION query:" + sb.toString());
        } else {
            sbConstraints.append(sbRelations); // Constraints by relations.
            if (query.getConstraint() != null) {
                Constraint constraint = query.getConstraint();
                if (sbConstraints.length() > 0) {
                    // Combine constraints.
                    sbConstraints.append(" AND ");
                    if (constraint instanceof CompositeConstraint) {
                        appendCompositeConstraintToSql(sbConstraints, (CompositeConstraint) constraint,
                                query, false, true, firstInChain);
                    } else {
                        firstInChain.appendConstraintToSql(sbConstraints, constraint, query,
                                false, true);
                    }
                } else {
                    // Only regular constraints.
                    if (constraint instanceof CompositeConstraint) {
                        appendCompositeConstraintToSql(sbConstraints, (CompositeConstraint) constraint,
                                query, false, false, firstInChain);
                    } else {
                        firstInChain.appendConstraintToSql(sbConstraints, constraint, query,
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
                            throw new IllegalStateException("Invalid direction value: " + sortOrder.getDirection());
                    }

                    if (iSortOrders.hasNext()) {
                        sb.append(",");
                    }
                }
            }
            log.debug("Completed generation of query:" + sb.toString());
        }
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
                if (con instanceof MultiConnection) {
                    closeInformix((MultiConnection)con);
                }
                con.close();
            }
        } catch (Exception g) {}
    }

    private void closeInformix(MultiConnection activeConnection) {
        Connection con = activeConnection.getRealConnection();
        try {
            Method scrub = Class.forName("com.informix.jdbc.IfxConnection").getMethod("scrubConnection", null);
            scrub.invoke(con, null);
        } catch (Exception e) {
            log.error("Exception while calling releaseBlob(): " + e.getMessage());
        }
    }

}
