package org.mmbase.storage.search.implementation.database;

import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.database.support.MMJdbc2NodeInterface;
import org.mmbase.storage.search.*;
import org.mmbase.util.logging.*;
import org.mmbase.module.database.MultiConnection;
import java.sql.*;
import java.util.*;


/**
 * Basic implementation.
 *
 * @author Rob van Maris
 * @version $Revision: 1.2 $
 */
public class BasicSqlHandler implements SqlHandler {
    
    /** Empty StepField array. */
    private static final StepField[] STEP_FIELD_ARRAY = new StepField[0];
    
    /** Logger instance. */
    private static Logger log
    = Logging.getLoggerInstance(SearchQueryHandler.class.getName());
    
    /** MMBase instance. */
    private MMBase mmbase = null;
    
    /** Disallowed table/fieldnames mapped to allowed alternatives. */
    private Map disallowed2Allowed = null;
    
    /**
     * Utility method, modifies strings for use in SQL statements.
     * This amounts to replacing all single quotes by two single quotes.
     *
     * @param str The input string.
     * @return The modified string.
     */
    // TODO: perhaps place this somewhere else?
    public static String toSqlString(String str) {
        String result = str;
        if (str != null) {
            int offset = str.indexOf('\'');
            if (offset != -1) {
                // At least one single quote found.
                StringBuffer sb = new StringBuffer(str.length() + 4);
                int start = 0;
                do {
                    sb.append(str.substring(start, offset)).append("''");
                    start = offset + 1;
                    offset = str.indexOf('\'', start);
                } while (offset != -1);
                sb.append(str.substring(start, str.length()));
                result = sb.toString();
                if (log.isDebugEnabled()) {
                    log.debug("converted string \"" + str + "\" to \"" + result + "\"");
                }
            }
        }
        return result;
    }
    
    /**
     * Default constructor.
     *
     * @param disallowedValues Map mapping disallowed table/fieldnames
     *        to allowed alternatives.
     */
    public BasicSqlHandler(Map disallowedValues) {
        // TODO: test if MMBase is properly initialized first.
        mmbase = MMBase.getMMBase();
        disallowed2Allowed = new HashMap(disallowedValues);
    }
    
    // javadoc is inherited
    public String toSql(SearchQuery query, SqlHandler firstInChain)
    throws SearchQueryException {
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
        
        // TODO: throw exception if maxNumber or offset set to non-default values.
        
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
    
    // javadoc is inherited
    public void appendQueryBodyToSql(StringBuffer sb, SearchQuery query,
    SqlHandler firstInChain)
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
            sb.append(getAllowedValue(tableAlias)).
            append(".").
            append(getAllowedValue(fieldName));
            
            // Field alias.
            String fieldAlias = field.getAlias();
            sb.append(" AS ").
            append(getAllowedValue(fieldAlias));
            
            if (iFields.hasNext()) {
                sb.append(",");
            }
        }
        
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
            append(getAllowedValue(tableName));
            
            // Table alias
            sb.append(" ").
            append(getAllowedValue(tableAlias));
            
            if (iSteps.hasNext()) {
                sb.append(",");
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
        
        // ORDER BY
        List sortOrders = query.getSortOrders();
        if (sortOrders.size() > 0) {
            sb.append(" ORDER BY ");
            Iterator iSortOrders = sortOrders.iterator();
            while (iSortOrders.hasNext()) {
                SortOrder sortOrder = (SortOrder) iSortOrders.next();
                
                // Field alias.
                String fieldAlias = sortOrder.getField().getAlias();
                sb.append(getAllowedValue(fieldAlias));
                
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
    
    // javadoc is inherited
    // TODO: what exception to throw when an unsupported constraint is
    // encountered (currently throws UnsupportedOperationException).
    public void appendConstraintToSql(StringBuffer sb, Constraint constraint,
    SearchQuery query, boolean inverse, boolean inComposite) {
        
        // Net effect of inverse setting with constraint inverse property.
        boolean overallInverse = inverse ^ constraint.isInverse();
        
        if (constraint instanceof FieldConstraint) {
            
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
                        value = toSqlString((String) value); // escape single quotes in string
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
                        value = toSqlString((String) value); // escape single quotes in string
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
            throw new IllegalArgumentException(
            "Unknown constraint type: "
            + constraint.getClass().getName());
        }
    }
    
    // javadoc is inherited
    public int getSupportLevel(int feature, SearchQuery query)
    throws SearchQueryException {
        int result;
        switch (feature) {
            case SearchQueryHandler.FEATURE_MAX_NUMBER:
                if (query.getMaxNumber() == -1){
                    result = SearchQueryHandler.SUPPORT_OPTIMAL;
                } else {
                    result = SearchQueryHandler.SUPPORT_NONE;
                }
                break;
                
            case SearchQueryHandler.FEATURE_OFFSET:
                if (query.getOffset() == 0) {
                    result = SearchQueryHandler.SUPPORT_OPTIMAL;
                } else {
                    result = SearchQueryHandler.SUPPORT_NONE;
                }
                break;
                
            default:
                result = SearchQueryHandler.SUPPORT_NONE;
        }
        return result;
    }
    
    // javadoc is inherited
    public int getSupportLevel(Constraint constraint, SearchQuery query)
    throws SearchQueryException {
        return constraint.getBasicSupportLevel();
    }
    
    // javadoc is inherited
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
     * Represents a CompositeConstraint object as a constraint in SQL format,
     * appending the result to a stringbuffer.
     * When it is part of a composite expression, it will be surrounded by
     * parenthesis when needed.
     *
     * @param sb The stringbuffer to append to.
     * @param compositeConstraint The composite constraint.
     * @param query The searchquery containing the constraint.
     * @param inverse True when the inverse constraint must be represented,
     *        false otherwise.
     * @param inComposite True when the constraint is part of
     *        a composite expression.
     * @param firstInChain The first element in the chain of handlers.
     *        At some point <code>appendConstraintToSql()</code> will have
     *        to be called on this handler, to generate the constraints in 
     *        the composite.
     */
    protected void appendCompositeConstraintToSql(
    StringBuffer sb, CompositeConstraint compositeConstraint, SearchQuery query, 
    boolean inverse, boolean inComposite, SqlHandler firstInChain)
    throws SearchQueryException {
        
        // Net effect of inverse setting with constraint inverse property.
        boolean overallInverse = inverse ^ compositeConstraint.isInverse();
        
        String strOperator = null;
        if (compositeConstraint.getLogicalOperator() == CompositeConstraint.LOGICAL_AND) {
            if (overallInverse) {
                // Inverse: NOT (A and B and C) = NOT A or NOT B or NOT C
                strOperator = " OR ";
            } else {
                strOperator = " AND ";
            }
        } else if (compositeConstraint.getLogicalOperator() == CompositeConstraint.LOGICAL_OR) {
            if (overallInverse) {
                // Inverse: NOT (A or B or C) = NOT A and NOT B and NOT C
                strOperator = " AND ";
            } else {
                strOperator = " OR ";
            }
        } else {
            throw new IllegalStateException(
            "Invalid logical operator: " + compositeConstraint.getLogicalOperator()
            + ", must be either "
            + CompositeConstraint.LOGICAL_AND + " or "
            + CompositeConstraint.LOGICAL_OR);
        }
        List childs = compositeConstraint.getChilds();

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
            firstInChain.appendConstraintToSql(
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
    }
    
}
