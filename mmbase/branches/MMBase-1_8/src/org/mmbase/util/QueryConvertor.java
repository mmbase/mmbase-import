/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import org.mmbase.bridge.Field;
import org.mmbase.core.CoreField;
import org.mmbase.storage.StorageManagerFactory;
import org.mmbase.storage.search.CompositeConstraint;
import org.mmbase.storage.search.Constraint;
import org.mmbase.storage.search.FieldCompareConstraint;
import org.mmbase.storage.search.FieldValueConstraint;
import org.mmbase.storage.search.implementation.BasicCompositeConstraint;
import org.mmbase.storage.search.implementation.BasicFieldValueConstraint;
import org.mmbase.storage.search.implementation.BasicSearchQuery;
import org.mmbase.storage.search.implementation.BasicStep;
import org.mmbase.storage.search.implementation.BasicStepField;
import org.mmbase.storage.search.legacy.ConstraintParser;

/**
 * Class for the converion of a expression string to a SQL where clause.
 * The expressions string is expected to be in 'altavista' format.
 * This means that logical operators are identified by '+' (AND), '-' (NOT),
 * and '|' (OR).
 * Comparative operators are the same as those used in SCAN (i.e. '=E', '=N', etc)
 * A wildcarded strings (with '*' or '?' characters) are automatically converted
 * to a LIKE expression.
 * <br />
 * The resulting converted expression is preceded with the SQL 'WHERE ' keyword.
 * <br />
 * Note that if the expression to convert starts with "WHERE", it is not converted at all,
 * but returned as is.
 *
 * @move org.mmbase.storage.search.util
 * @author Daniel Ockeloen
 * @author Pierre van Rooden (javadocs)
 * @version $Id$
 */
public class QueryConvertor {

    static StorageManagerFactory factory = null;

    /**
     * Converts query to a SQL "where"-clause.
     * @param query the query to convert
     * @param smf the storagemanagerfactory to use when converting fieldnames
     * @deprecated Use {@link #setConstraint setConstraint()} to parse
     *        these expressions.
     */
    public static String altaVista2SQL(String query, StorageManagerFactory smf) {
        factory = smf;
        return altaVista2SQL(query);
    }

    /**
     * Converts query to a SQL "where"-clause.
     * @param query the query to convert
     * @deprecated Use {@link #setConstraint setConstraint()} to parse
     *        these expressions.
     */
    public static String altaVista2SQL(String query) {
        if (query.indexOf("where")!=-1 || query.indexOf("WHERE")!=-1) {
            return query;
        }

        StringBuffer buffer = new StringBuffer(64);
        // query = query.toLowerCase();
        DBQuery parsedQuery = new DBQuery(query);
        // log.debug("Converting: " + query);
        if(!query.equals(""))
            parsedQuery.sqlConversion(buffer);
        // log.debug("Converted to: "+buffer.toString());

        return buffer.toString();
    }

    /**
     * Sets constraint for a
     * {@link org.mmbase.storage.search.implementation.BasicSearchQuery
     * BasicSearchQuery} object.
     * <p>
     * The constraint may be specified as either one of these formats:
     * <ol>
     * <li><code>null</code> or empty.
     * <li>A SQL search condition, starting with "WHERE " (ignoring case).
     * <li>A SQL search condition, of the form "WHERE(......)" (ignoring case).
     * <li>Altavista format.
     * </ol>
     * If the query contains more than one step, the fields must be of the form
     * <em>stepalias.field</em>.
     * <p>See {@link org.mmbase.storage.search.legacy.ConstraintParser} for
     * more on how SQL search conditions are supported.
     * <p><b>Note:</b>
     * This method is provided to support different constraint formats for
     * backward compatibility (1, 3 and 4 above).
     * Do not call this method directly from new code, but rather use
     * {@link org.mmbase.storage.search.legacy.ConstraintParser} to parse
     * search constraints.
     *
     * @param query The query.
     * @param where The constraint.
     * @since MMBase-1.7
     */
    public static void setConstraint(BasicSearchQuery query, String where) {

        Constraint constraint = null;

        if (where == null || where.trim().length() == 0) {
            // Empty constraint.

        } else if (where.substring(0, 6).equalsIgnoreCase("WHERE ")) {
            // "where"-clause.
            // Strip leading "where ".
            constraint =
                new ConstraintParser(query).toConstraint(where.substring(6));

        } else if (where.substring(0, 6).equalsIgnoreCase("WHERE(")) {
            // "where"-clause, without space following "where".
            // Supported for backward compatibility.
            // Strip leading "where".
            constraint =
                new ConstraintParser(query).toConstraint(where.substring(5));

        } else {
            // AltaVista format.
            DBQuery parsedQuery = new DBQuery(where);
            constraint = parsedQuery.toConstraint(query);
        }
        query.setConstraint(constraint);
    }
}

/**
 * Basic Class for parsing values and expressions.
 */
class ParseItem {

    /**
     * Appends the converted item to the stringbuffer.
     * @param result the stringbuffer to which to add the item
     */
    public void sqlConversion(StringBuffer result) {
    }

    /**
     * Returns the converted item as a <code>String</code>
     */
    public String toString() {
        StringBuffer result = new StringBuffer();
        this.sqlConversion(result);
        return result.toString();
    }
}

/**
 * Basic Class for parsing a set of conditional expressions.
 */
class DBQuery  extends ParseItem {
    // logger
    //private static Logger log = Logging.getLoggerInstance(DBQuery.class.getName());

    public Vector items = new Vector();

    /**
     * Creates the query
     * @item the query to convert
     */
    public DBQuery(String query) {
        StringTokenizer parser = new StringTokenizer(query, "+-|",true);
        ParseItem item;

        while (parser.hasMoreTokens()) {
            item = new DBConditionItem(parser.nextToken());
            items.addElement(item);

            if (parser.hasMoreTokens()) {
                item = new DBLogicalOperator(parser.nextToken());
                items.addElement(item);
            }
        }
    }

    /**
     * Appends the converted query to the stringbuffer.
     * @param result the stringbuffer to which to add the query
     */
    public void sqlConversion(StringBuffer result) {
        Enumeration enumeration = items.elements();

        result.append("WHERE ");

        while (enumeration.hasMoreElements()) {
            ((ParseItem)enumeration.nextElement()).sqlConversion(result);
        }
    }

    /**
     * Converts this query to a constraint for a search query.
     *
     * @param SearchQuery The search query.
     * @return The constraint.
     */
    // package access!
    Constraint toConstraint(BasicSearchQuery query) {
        BasicCompositeConstraint compositeConstraint = null;
        BasicFieldValueConstraint fieldValueConstraint = null;

        Iterator iItems = items.iterator();
        DBLogicalOperator logicalOperator = null;
        while (iItems.hasNext()) {

            // Logical operator requires compositeConstraint.
            if (logicalOperator != null) {

                // Create new composite constraint when not present already.
                if (compositeConstraint == null) {
                    if (logicalOperator.logOperator == DBLogicalOperator.OR) {
                        compositeConstraint
                            = new BasicCompositeConstraint(
                                CompositeConstraint.LOGICAL_OR);
                    } else {
                        compositeConstraint
                            = new BasicCompositeConstraint(
                                CompositeConstraint.LOGICAL_AND);
                    }
                    compositeConstraint.addChild(fieldValueConstraint);

                // If a composite constraint is already present, add a new
                // child composite constraint if the logical operator is
                // not compatible.
                } else if (compositeConstraint.getLogicalOperator()
                        == CompositeConstraint.LOGICAL_AND) {
                    if (logicalOperator.logOperator
                            == DBLogicalOperator.OR) {
                        BasicCompositeConstraint compositeConstraint2
                            = new BasicCompositeConstraint(
                                CompositeConstraint.LOGICAL_OR);
                        compositeConstraint2.addChild(compositeConstraint);
                        compositeConstraint = compositeConstraint2;
                    }
                } else if (compositeConstraint.getLogicalOperator()
                        == CompositeConstraint.LOGICAL_OR) {
                    if (logicalOperator.logOperator
                            != DBLogicalOperator.OR) {
                        BasicCompositeConstraint compositeConstraint2
                            = new BasicCompositeConstraint(
                                CompositeConstraint.LOGICAL_AND);
                        compositeConstraint2.addChild(compositeConstraint);
                        compositeConstraint = compositeConstraint2;
                    }
                }
            }

            DBConditionItem condition = (DBConditionItem) iItems.next();

            // Find corresponding field in query.
            BasicStepField field = null;
            Iterator iFields = query.getFields().iterator();
            while (iFields.hasNext()) {
                BasicStepField field2 = (BasicStepField) iFields.next();
                String alias2 = field2.getStep().getAlias();
                if (alias2 == null) {
                    alias2 = field2.getStep().getTableName();
                }
                if ((condition.prefix == null
                        || alias2.equals(condition.prefix))
                    && field2.getFieldName().equals(condition.fieldName)) {
                    field = field2;
                    break;
                }
            }

            if (field == null) {
                // Field not found, find step and add field.
                BasicStep step = null;
                if (condition.prefix == null) {
                    step = (BasicStep) query.getSteps().get(0);
                } else {
                    Iterator iSteps = query.getSteps().iterator();
                    while (iSteps.hasNext()) {
                        BasicStep step2 = (BasicStep) iSteps.next();
                        if (step2.getAlias().equals(condition.prefix)) {
                            step = step2;
                            break;
                        }
                    }
                    if (step == null) {
                        // Step not found.
                        throw new IllegalStateException("Step with alias '"
                            + condition.prefix + "' not found in this query: "
                            + query);
                    }
                }

                CoreField coreField = step.getBuilder().getField(condition.fieldName);
                if (coreField == null) {
                    // Field not found.
                    throw new IllegalStateException("Field with name '"
                        + condition.fieldName + "' not found in builder "
                        + step.getTableName());
                } else {
                    field = query.addField(step, coreField);
                }
            }

            int fieldType = field.getType();
            if (fieldType == Field.TYPE_STRING
                || fieldType == Field.TYPE_XML) {
                // String field.
                fieldValueConstraint = new BasicFieldValueConstraint(field, condition.value.getValue());
                fieldValueConstraint.setCaseSensitive(false);
            } else {
                // Numerical field.
                Object numericalValue = new Double(condition.value.getValue());
                fieldValueConstraint = new BasicFieldValueConstraint(field, numericalValue);
            }

            switch (condition.operator) {
                case DBConditionItem.NOTEQUAL:
                    fieldValueConstraint.setOperator(FieldValueConstraint.NOT_EQUAL);
                    break;

                case DBConditionItem.EQUAL:
                    if (fieldType == Field.TYPE_STRING
                        || fieldType == Field.TYPE_XML) {
                        fieldValueConstraint.setOperator(FieldCompareConstraint.LIKE);
                    } else {
                        fieldValueConstraint.setOperator(FieldCompareConstraint.EQUAL);
                    }
                    break;

                case DBConditionItem.GREATER:
                    fieldValueConstraint.setOperator(FieldCompareConstraint.GREATER);
                    break;

                case DBConditionItem.SMALLER:
                    fieldValueConstraint.setOperator(FieldCompareConstraint.LESS);
                    break;

                case DBConditionItem.GREATEREQUAL:
                    fieldValueConstraint.setOperator(FieldCompareConstraint.GREATER_EQUAL);
                    break;

                case DBConditionItem.SMALLEREQUAL:
                    fieldValueConstraint.setOperator(FieldCompareConstraint.LESS_EQUAL);
                    break;

                default:
                    // Unknown operator.
                    throw new IllegalStateException(
                        "Invalid operator value: " + condition.operator);

            }

            // Add to compositeConstraint when present.
            if (compositeConstraint != null) {
                fieldValueConstraint.setInverse(logicalOperator.logOperator
                    == DBLogicalOperator.NOT);
                compositeConstraint.addChild(fieldValueConstraint);
            }

            if (iItems.hasNext()) {
                logicalOperator = (DBLogicalOperator) iItems.next();
            }
        }

        if (compositeConstraint != null) {
            return compositeConstraint;
        } else {
            return fieldValueConstraint;
        }
    }
}

/**
 * Class for conversion of boolean xpressions to their SQL equivalent.
 * This class converts the following conditional operators encountered in the
 * parameter passed to the constructor :<br />
 * '=='' or '=E' to '='<br />
 * '=N' to '<>'<br />
 * '=G' to '&gt;'<br />
 * '=g' to '&gt;='<br />
 * '=S' to '&lt;'<br />
 * '=s' to '&lt;='<br />
 * It also wraps string values with the SQL lower() function, and uses LIKE
 * when wildcards are used in a stringvalue.
 *
 */
class DBConditionItem extends ParseItem {
    public static final int NOTEQUAL=0, EQUAL = 1, GREATER = 2, SMALLER = 3, GREATEREQUAL=4,SMALLEREQUAL=5;
    // logger
    //private static Logger log = Logging.getLoggerInstance(DBConditionItem.class.getName());

    /** The fieldname. */
    String fieldName = null;

    /** The table alias prefix (if present). */
    String prefix = null;

    /**
     * The field identifier as it appears in SQL expressions, with
     * table alias prefix (if present), and the fieldname converted to
     * an allowed fieldname.
     */
    String identifier = null;

    /**
     * The comparison operator, must be one of the constants defined
     * in this class.
     */
    int operator = 0;

    /** The value to compare to. */
    DBValue value = null;

    /**
     * Creates the boolean expression
     * @item the expression to convert
     */
    public DBConditionItem(String item) {
        int conditionPos;
        char operatorChar;

        conditionPos = item.indexOf('=');
        if (conditionPos == -1) {
            throw new IllegalArgumentException(
            "No '=' found in query item '" + item + "'");
        }

        fieldName = item.substring(0, conditionPos);
        int prefixPos = fieldName.indexOf(".");
        if (prefixPos != -1) {
            prefix = fieldName.substring(0, prefixPos);
            fieldName = fieldName.substring(prefixPos + 1);
        }
        if (QueryConvertor.factory != null) {
            identifier = (String)QueryConvertor.factory.getStorageIdentifier(fieldName);
        } else {
            identifier = fieldName;
        }
        if (prefix != null) {
            identifier = prefix +"."+ identifier;
        }

        value = DBValue.abstractCreation(item.substring(conditionPos+2));

        operatorChar = item.charAt(conditionPos + 1);
        // log.debug("char="+operatorChar);
        switch (operatorChar) {
        case '=':
        case 'E':
            operator = EQUAL;
            break;
        case 'N':
            operator = NOTEQUAL;
            break;
        case 'G':
            operator = GREATER;
            break;
        case 'g':
            operator = GREATEREQUAL;
            break;
        case 'S':
            operator = SMALLER;
            break;
        case 's':
            operator = SMALLEREQUAL;
            break;
        default:
            break;
        }
    }

    /**
     * Appends the converted expression to the stringbuffer.
     * @param result the stringbuffer to which to add the expression
     */
    public void sqlConversion(StringBuffer result) {
        if (value instanceof DBWildcardStringValue || value instanceof DBStringValue)
            result.append("lower(").append(identifier).append(")");
            //result.append("").append(identifier).append("");
        else
            result.append(identifier);

        if (value instanceof DBWildcardStringValue) {
            result.append(" LIKE ");
        }
        else {
            switch (operator) {
            case EQUAL:
                result.append(" = ");
                break;
            case NOTEQUAL:
                result.append(" <> ");
                break;
            case GREATER:
                result.append(" > ");
                break;
            case GREATEREQUAL:
                result.append(" >= ");
                break;
            case SMALLER:
                result.append(" < ");
                break;
            case SMALLEREQUAL:
                result.append(" <= ");
                break;
            default:
                result.append(" = ");
            }
        }
        value.sqlConversion(result);
    }
}

/**
 * Basic Class for storing values.
 */
class DBValue extends ParseItem {

    private String value = null;

    /**
     * Constructor, only subclasses can be instantiated.
     */
    protected DBValue() {}

    /**
     * Determines whether a value is a string, a string with wildcards, or
     * a number, and returns the appropriate class.
     * @param value the value to parse
     * @return the appropriate subclass of <code>DBValue</code>
     */
    public static DBValue abstractCreation(String value) {
        value = value.toLowerCase();
        if (value.startsWith("'")) {
            if (value.indexOf('?') >= 0 || value.indexOf('*') >= 0)
                return new DBWildcardStringValue(Strip.Chars(value,"' ",Strip.BOTH));
            else
                return new DBStringValue(Strip.Chars(value,"' ",Strip.BOTH));
        }
        else
            return new DBNumberValue(value);
    }

    /**
     * Sets value property.
     *
     * @param value The string representation of the value.
     */
    protected void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets value property.
     *
     * @return The string representation of the value.
     */
    public String getValue() {
        return value;
    }
}

/**
 * Class for storing numeric values.
 */
class DBNumberValue extends DBValue {
    /**
     * Creates the numeric value
     * @value the value to convert
     */
    public DBNumberValue(String value) {
        // Protection against empty numbers
        if (value == null || value.length() == 0) {
            setValue(Integer.toString(Integer.MIN_VALUE));
        } else {
            setValue(value);
        }
    }

    /**
     * Appends the converted value to the stringbuffer.
     * @param result the stringbuffer to which to add the expression
     */
    public void sqlConversion(StringBuffer result) {
        result.append(getValue());
    }
}

/**
 * Class for storing and converting string values.
 * Wraps the result with quotes.
 */
class DBStringValue extends DBValue {
    /**
     * Creates the string value
     * @value the value to convert
     */
    public DBStringValue(String value) {
        setValue(value);
    }

    /**
     * Appends the converted value to the stringbuffer.
     * @param result the stringbuffer to which to add the expression
     */
    public void sqlConversion(StringBuffer result) {
        result.append("'").append(getValue()).append("'");
    }

}

/**
 * Class for storing and converting string values with wildcards.
 * Wraps the result with quotes and replaces any wildcards with
 * SQL-wildcards.
 */
class DBWildcardStringValue extends DBValue {
    /**
     * Creates the wildcarded string value
     * @value the value to convert
     */
    public DBWildcardStringValue(String value) {
        if (value == null) {
            value = "";
        }
        setValue(value.replace('*', '%').replace('?', '_'));
    }

    /**
     * Appends the converted value to the stringbuffer.
     * @param result the stringbuffer to which to add the expression
     */
    public void sqlConversion(StringBuffer result) {
        result.append("'").append(getValue()).append("'");
    }
}

/**
 * Class for conversion of operators to their SQL equivalent.
 * This class converts:<br />
 * '+' to 'AND'<br />
 * '-' to 'AND NOT'<br />
 * '|' to 'OR'<br />
 */
class DBLogicalOperator extends ParseItem {
    public static final char AND = '+';
    public static final char NOT = '-';
    public static final char OR ='|';

    char logOperator;

    /**
     * Creates the operator
     * @operator the original operator to convert
     */
    public DBLogicalOperator(String operator) {
        if      (operator.equals("+")) logOperator = AND;
        else if (operator.equals("-")) logOperator = NOT;
        else if (operator.equals("|")) logOperator = OR;
    }

    public DBLogicalOperator(char operator) {
        logOperator = operator;
    }

    /**
     * Appends the converted operator to the stringbuffer.
     * @param result the stringbuffer to which to add the operator
     */
    public void sqlConversion(StringBuffer result) {
        switch (logOperator) {
            case AND:
                result.append(" AND ");
                break;
            case NOT:
                result.append(" AND NOT ");
                break;
            case OR:
                result.append(" OR ");
                break;
            default:
                break;
        }
    }
}

