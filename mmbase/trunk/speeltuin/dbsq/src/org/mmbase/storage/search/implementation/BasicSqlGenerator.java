package org.mmbase.storage.search.implementation;

import java.util.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.storage.search.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;

/**
 * Basic implementation of class that generates SQL for a query.
 *
 * @author Rob van Maris
 * @version $Revision: 1.1 $
 */
// TODO: move somewhere else, now this duplicates code from BasicSqlHandler.
// TODO remove this class, it is only used by BasicSearchQuery#toSql().
public class BasicSqlGenerator {
    
    /** Logger instance. */
    private static Logger log
    = Logging.getLoggerInstance(BasicSqlGenerator.class.getName());
    
    /** Disallowed table/fieldnames mapped to allowed alternatives. */
    private Map disallowed2Allowed = null;
    
    /** The prefix applied to tablenames (usually basename plus underscore). */
    private String prefix = null;
    
    /**
     * Default constructor.
     *
     * @param disallowedValues Map mapping disallowed table/fieldnames
     *        to allowed alternatives.
     * @param tablePrefix The prefix applied to tablenames (usually
     *        basename plus underscore).
     */
    // package visibility!
    BasicSqlGenerator(Map disallowedValues, String prefix) {
        disallowed2Allowed = new HashMap(disallowedValues);
        this.prefix = prefix;
    }
    
    /**
     * Maps string to value that is allowed as table or field name.
     *
     * @param The string value.
     * @return The mapped value.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    public String getAllowedValue(String value) {
        if (value == null) {
            throw new IllegalArgumentException(
            "Invalid value: " + value);
        }
        String allowedValue = (String) disallowed2Allowed.get(value);
        if (allowedValue == null) {
            allowedValue = value;
        }
        return allowedValue;
    }
    
    /**
     * Represents a Constraint object as a constraint in SQL format,
     * appending the result to a stringbuffer.
     * When it is part of a composite expression, it will be surrounded by
     * parenthesis when needed.
     *
     * @param sb The stringbuffer to append to.
     * @param constraint The constraint.
     * @param inverse True when the inverse constraint must be represented,
     *        false otherwise.
     * @param inComposite True when the constraint is part of
     *        a composite expression.
     * @throws IllegalStateException when the constraint is not complete.
     */
    // package visibility!
     // TODO: what exception to throw when an unsupported constraint is
     // encountered (currently throws UnsupportedOperationException).
    void appendConstraintToSql(StringBuffer sb, Constraint constraint,
    SearchQuery query, boolean inverse, boolean inComposite) {
        
        // Net effect of inverse setting with constraint inverse property.
        boolean overallInverse = inverse ^ constraint.isInverse();
        
        if (constraint instanceof CompositeConstraint) {
            
            // Composite constraint
            CompositeConstraint composite = (CompositeConstraint) constraint;
            String strOperator = null;
            if (composite.getLogicalOperator() == CompositeConstraint.LOGICAL_AND) {
                if (overallInverse) {
                    // Inverse: NOT (A and B and C) = NOT A or NOT B or NOT C
                    strOperator = " OR ";
                } else {
                    strOperator = " AND ";
                }
            } else if (composite.getLogicalOperator() == CompositeConstraint.LOGICAL_OR) {
                if (overallInverse) {
                    // Inverse: NOT (A or B or C) = NOT A and NOT B and NOT C
                    strOperator = " AND ";
                } else {
                    strOperator = " OR ";
                }
            } else {
                throw new IllegalStateException(
                "Invalid logical operator: " + composite.getLogicalOperator()
                + ", must be either "
                + CompositeConstraint.LOGICAL_AND + " or "
                + CompositeConstraint.LOGICAL_OR);
            }
            List childs = composite.getChilds();
            
            // Test for at least 1 child.
            if (childs.isEmpty()) {
                throw new IllegalStateException(
                "Composite constraint has no child "
                + "(at least 1 child is required).");
            }
            
            boolean hasMultipleChilds = childs.size() > 1;
            
            // Opening parenthesis, when part of composite expression
            // and with multiple childs.
            if (inComposite && hasMultipleChilds) {
                sb.append("(");
            }
            
            // Recursively append all childs.
            Iterator iChilds = childs.iterator();
            while (iChilds.hasNext()) {
                Constraint child = (Constraint) iChilds.next();
                appendConstraintToSql(
                sb, child, query, overallInverse, hasMultipleChilds);
                if (iChilds.hasNext()) {
                    sb.append(strOperator);
                }
            }
            
            // Closing parenthesis, when part of composite expression
            // and with multiple childs.
            if (inComposite && hasMultipleChilds) {
                sb.append(")");
            }
        } else if (constraint instanceof FieldConstraint) {
            
            // Field constraint
            FieldConstraint fieldConstraint = (FieldConstraint) constraint;
            StepField field = fieldConstraint.getField();
            int fieldType = field.getType();
            String fieldName = field.getFieldName();
            String tableAlias = field.getStep().getAlias();
            
            if (fieldConstraint instanceof FieldValueInConstraint) {
                
                // Field value-in constraint
                FieldValueInConstraint valueInConstraint
                = (FieldValueInConstraint) fieldConstraint;
                Set values = valueInConstraint.getValues();
                if (values.size() == 0) {
                    throw new IllegalStateException(
                    "Field value-in constraint specifies no values "
                    + "(at least 1 value is required).");
                }
                sb.append(getAllowedValue(tableAlias)).
                append(".").
                append(getAllowedValue(fieldName)).
                append(overallInverse? " NOT IN (": " IN (");
                Iterator iValues = values.iterator();
                while (iValues.hasNext()) {
                    Object value = iValues.next();
                    if (fieldType == FieldDefs.TYPE_STRING
                    || fieldType == FieldDefs.TYPE_XML) {
                        value = BasicSqlHandler.toSqlString((String) value); // escape single quotes in string
                        sb.append("'").
                        append(value).
                        append("'");
                    } else {
                        sb.append(value);
                    }
                    if (iValues.hasNext()) {
                        sb.append(",");
                    }
                }
                sb.append(")");
                
            } else if (fieldConstraint instanceof FieldNullConstraint) {
                
                // Field null constraint
                sb.append(getAllowedValue(tableAlias)).
                append(".").
                append(getAllowedValue(fieldName)).
                append(overallInverse? " IS NOT NULL": " IS NULL");
                
            } else if (fieldConstraint instanceof FieldCompareConstraint) {
                
                // Field compare constraint
                FieldCompareConstraint fieldCompareConstraint
                = (FieldCompareConstraint) fieldConstraint;
                sb.append(overallInverse? "NOT ": "").
                append(getAllowedValue(tableAlias)).
                append(".").
                append(getAllowedValue(fieldName));
                switch (fieldCompareConstraint.getOperator()) {
                    case FieldValueConstraint.LESS:
                        sb.append("<");
                        break;
                        
                    case FieldValueConstraint.EQUAL:
                        sb.append("=");
                        break;
                        
                    case FieldValueConstraint.GREATER:
                        sb.append(">");
                        break;
                        
                    case FieldValueConstraint.LIKE:
                        sb.append(" LIKE ");
                        break;
                        
                    default:
                        throw new IllegalStateException(
                        "Unknown operator value in constraint: "
                        + fieldCompareConstraint.getOperator());
                }
                if (fieldCompareConstraint instanceof FieldValueConstraint) {
                    // FieldValueConstraint.
                    FieldValueConstraint fieldValueConstraint
                    = (FieldValueConstraint) fieldCompareConstraint;
                    Object value = fieldValueConstraint.getValue();
                    if (fieldType == FieldDefs.TYPE_STRING
                    || fieldType == FieldDefs.TYPE_XML) {
                        value = BasicSqlHandler.toSqlString((String) value); // escape single quotes in string
                        sb.append("'").
                        append(value).
                        append("'");
                    } else {
                        sb.append(value);
                    }
                } else if (fieldCompareConstraint instanceof CompareFieldsConstraint) {
                    // CompareFieldsConstraint
                    CompareFieldsConstraint compareFieldsConstraint
                    = (CompareFieldsConstraint) fieldCompareConstraint;
                    StepField field2 = compareFieldsConstraint.getField2();
                    String fieldName2 = field2.getFieldName();
                    String tableAlias2 = field2.getStep().getAlias();
                    sb.append(getAllowedValue(tableAlias2)).
                    append(".").
                    append(getAllowedValue(fieldName2));
                } else {
                    throw new UnsupportedOperationException(
                    "Unknown constraint type: "
                    + constraint.getClass().getName());
                }
            } else {
                throw new UnsupportedOperationException(
                "Unknown constraint type: "
                + constraint.getClass().getName());
            }
        } else {
            throw new UnsupportedOperationException(
            "Unknown constraint type: "
            + constraint.getClass().getName());
        }
    }

    /**
     * Represents body of a SearchQuery object as a string in SQL format,
     * using the database configuration. <br>
     * The body of the SQL query string is defined as the substring containing
     * fields, tables, constraints and orders.
     *
     * @param query The searchquery.
     * @return SQL string representation of the query.
     * @throws IllegalStateException when the query is not complete,
     *         or its parameters are not set to proper values.
     */
    public void appendQueryBodyToSql(StringBuffer sbQuery, SearchQuery query)
    throws SearchQueryException {
        StringBuffer sbNodes = new StringBuffer();
        StringBuffer sbRelations = new StringBuffer();
        
        // Fields expression
        Iterator iFields = query.getFields().iterator();
        while (iFields.hasNext()) {
            StepField field = (StepField) iFields.next();
            
            // Fieldname prefixed by table alias.
            String tableAlias = field.getStep().getAlias();
            String fieldName = field.getFieldName();
            sbQuery.append(getAllowedValue(tableAlias)).
            append(".").
            append(getAllowedValue(fieldName));
            
            // Field alias.
            String fieldAlias = field.getAlias();
            sbQuery.append(" AS ").
            append(getAllowedValue(fieldAlias));
            
            if (iFields.hasNext()) {
                sbQuery.append(",");
            }
        }
        
        // Tables
        sbQuery.append(" FROM ");
        Iterator iSteps = query.getSteps().iterator();
        while (iSteps.hasNext()) {
            Step step = (Step) iSteps.next();
            String tableName = step.getTableName();
            String tableAlias = step.getAlias();
            
            // Tablename, prefixed with basename and underscore
            sbQuery.append(prefix)
                .append(getAllowedValue(tableName));
            
            // Table alias
            sbQuery.append(" ").
            append(getAllowedValue(tableAlias));
            
            if (iSteps.hasNext()) {
                sbQuery.append(",");
            }
            
            // Included nodes.
            SortedSet nodes = step.getNodes();
            if (nodes.size() > 0) {
                if (sbNodes.length() > 0) {
                    sbNodes.append(" AND ");
                }
                sbNodes.append(getAllowedValue(tableAlias)).
                append(".").
                append(getAllowedValue("number")).
                append(" IN (");
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
                String relationAlias = relationStep.getAlias();
                String previousAlias = relationStep.getPrevious().getAlias();
                String nextAlias = relationStep.getNext().getAlias();
                if (sbRelations.length() > 0) {
                    sbRelations.append(" AND ");
                }
                switch (relationStep.getDirectionality()) {
                    case RelationStep.DIRECTIONS_SOURCE:
                        sbRelations.append("(").
                        append(getAllowedValue(previousAlias)).
                        append(".").
                        append(getAllowedValue("number")).
                        append("=").
                        append(getAllowedValue(relationAlias)).
                        append(".").
                        append(getAllowedValue("dnumber")).
                        append(" AND ").
                        append(getAllowedValue(nextAlias)).
                        append(".").
                        append(getAllowedValue("number")).
                        append("=").
                        append(getAllowedValue(relationAlias)).
                        append(".").
                        append(getAllowedValue("snumber")).
                        append(")");
                        break;
                        
                    case RelationStep.DIRECTIONS_DESTINATION:
                        sbRelations.append("(").
                        append(getAllowedValue(previousAlias)).
                        append(".").
                        append(getAllowedValue("number")).
                        append("=").
                        append(getAllowedValue(relationAlias)).
                        append(".").
                        append(getAllowedValue("snumber")).
                        append(" AND ").
                        append(getAllowedValue(nextAlias)).
                        append(".").
                        append(getAllowedValue("number")).
                        append("=").
                        append(getAllowedValue(relationAlias)).
                        append(".").
                        append(getAllowedValue("dnumber")).
                        append(")");
                        break;
                        
                    case RelationStep.DIRECTIONS_BOTH:
                        sbRelations.append("((").
                        append(getAllowedValue(previousAlias)).
                        append(".").
                        append(getAllowedValue("number")).
                        append("=").
                        append(getAllowedValue(relationAlias)).
                        append(".").
                        append(getAllowedValue("dnumber")).
                        append(" AND ").
                        append(getAllowedValue(nextAlias)).
                        append(".").
                        append(getAllowedValue("number")).
                        append("=").
                        append(getAllowedValue(relationAlias)).
                        append(".").
                        append(getAllowedValue("snumber")).
                        append(") OR (").
                        append(getAllowedValue(previousAlias)).
                        append(".").
                        append(getAllowedValue("number")).
                        append("=").
                        append(getAllowedValue(relationAlias)).
                        append(".").
                        append(getAllowedValue("snumber")).
                        append(" AND ").
                        append(getAllowedValue(nextAlias)).
                        append(".").
                        append(getAllowedValue("number")).
                        append("=").
                        append(getAllowedValue(relationAlias)).
                        append(".").
                        append(getAllowedValue("dnumber")).
                        append("))");
                        break;
                        
                    default: // Invalid directionality value.
                        throw new IllegalStateException(
                        "Invalid directionality value: " + relationStep.getDirectionality());
                }
            }
        }
        
        // Constraints
        StringBuffer sbConstraints = new StringBuffer();
        sbConstraints.append(sbNodes); // Constraints by included nodes.
        if (sbConstraints.length() > 0 && sbRelations.length() > 0) {
            sbConstraints.append(" AND ");
        }
        sbConstraints.append(sbRelations); // Constraints by relations.
        if (query.getConstraint() != null) {
            if (sbConstraints.length() > 0) {
                // Combine constraints.
                sbConstraints.append(" AND ");
                appendConstraintToSql( // Regular constraint(s).
                sbConstraints, query.getConstraint(), query,
                false, true);
            } else {
                // Only regular constraints.
                appendConstraintToSql( // Regular constraint(s).
                sbConstraints, query.getConstraint(), query,
                false, false);
            }
        }
        if (sbConstraints.length() > 0) {
            sbQuery.append(" WHERE ").
            append(sbConstraints.toString());
        }
        
        // ORDER BY
        List sortOrders = query.getSortOrders();
        if (sortOrders.size() > 0) {
            sbQuery.append(" ORDER BY ");
            Iterator iSortOrders = sortOrders.iterator();
            while (iSortOrders.hasNext()) {
                SortOrder sortOrder = (SortOrder) iSortOrders.next();
                
                // Field alias.
                String fieldAlias = sortOrder.getField().getAlias();
                sbQuery.append(getAllowedValue(fieldAlias));
                
                // Sort direction.
                switch (sortOrder.getDirection()) {
                    case SortOrder.ORDER_ASCENDING:
                        sbQuery.append(" ASC");
                        break;
                        
                    case SortOrder.ORDER_DESCENDING:
                        sbQuery.append(" DESC");
                        break;
                        
                    default: // Invalid direction value.
                        throw new IllegalStateException(
                        "Invalid direction value: " + sortOrder.getDirection());
                }
                
                if (iSortOrders.hasNext()) {
                    sbQuery.append(",");
                }
            }
        }
    }
    
    /**
     * Represents a SearchQuery object as a string in SQL format,
     * using the database configuration.
     *
     * @param query The searchquery.
     * @return SQL string representation of the query.
     * @throws IllegalStateException when the query is not complete,
     *         or its parameters are not set to proper values.
     */
    // TODO: refactor into smaller methods.
    public String toSql(SearchQuery query) throws SearchQueryException {
        
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
        
        // DISTINCT
        if (query.isDistinct()) {
            sbQuery.append("DISTINCT ");
        }
        
        appendQueryBodyToSql(sbQuery, query);
        
        String strSQL = sbQuery.toString();
        if (log.isDebugEnabled()) {
            log.debug("generated SQL: " + strSQL);
        }
        return strSQL;
    }
    
}
