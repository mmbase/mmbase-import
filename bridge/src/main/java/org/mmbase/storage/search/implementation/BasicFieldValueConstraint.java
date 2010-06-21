/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search.implementation;

import java.util.Date;
import org.mmbase.util.Casting;
import org.mmbase.storage.search.*;
import org.mmbase.util.logging.*;


/**
 * Basic implementation.
 * The tested operation is equality, unless it is explicitly set.
 *
 * @author Rob van Maris
 * @version $Id$
 * @since MMBase-1.7
 */
public class BasicFieldValueConstraint extends BasicFieldCompareConstraint implements FieldValueConstraint, java.io.Serializable {

    private static final Logger log = Logging.getLoggerInstance(BasicFieldValueConstraint.class);

    private Object value = null;

    /**
     * Constructor.
     * Depending on the field type, the value must be of type
     * <code>String</code> or <code>Number</code>.
     *
     * @param field The associated field.
     * @param value The non-null property value.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    public BasicFieldValueConstraint(StepField field, Object value) {
        super(field);
        setValue(value);
    }

    /**
     * Sets value property.
     * Depending on the field type, the value must be of type
     * <code>String</code> or <code>Number</code>.
     *
     * @param value The non-null property value.
     * @return This <code>BasicFieldValueConstraint</code> instance.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    public BasicFieldValueConstraint setValue(Object value) {
        if (! modifiable) throw new IllegalStateException();
        BasicStepField.testValue(value, getField());
        this.value = value;
        return this;
    }

    @Override
    public Object getValue() {
        return value;
    }


    private boolean floatMatches(double constraintDouble, double doubleTocompare, int operator)  {
        switch(operator) {
        case FieldCompareConstraint.EQUAL:         return doubleTocompare == constraintDouble;
        case FieldCompareConstraint.GREATER:       return doubleTocompare > constraintDouble;
        case FieldCompareConstraint.GREATER_EQUAL: return doubleTocompare >= constraintDouble;
        case FieldCompareConstraint.LESS:          return doubleTocompare < constraintDouble;
        case FieldCompareConstraint.LESS_EQUAL:    return doubleTocompare <= constraintDouble;
        case FieldCompareConstraint.NOT_EQUAL:     return doubleTocompare != constraintDouble;
        default:
            log.warn("operator " + FieldCompareConstraint.OPERATOR_DESCRIPTIONS[operator] + "for any numeric type");
            return true;

        }
    }

    private boolean intMatches(long constraintLong, long longToCompare, int operator) {
        switch(operator) {
        case FieldCompareConstraint.EQUAL:         return longToCompare == constraintLong;
        case FieldCompareConstraint.GREATER:       return longToCompare > constraintLong;
        case FieldCompareConstraint.GREATER_EQUAL: return longToCompare >= constraintLong;
        case FieldCompareConstraint.LESS:          return longToCompare < constraintLong;
        case FieldCompareConstraint.LESS_EQUAL:    return longToCompare <= constraintLong;
        case FieldCompareConstraint.NOT_EQUAL:     return longToCompare != constraintLong;
        default:
            log.warn("operator " + FieldCompareConstraint.OPERATOR_DESCRIPTIONS[operator] + "for any numeric type");
            return true;
        }
    }

    private boolean stringMatches(String constraintString, String stringToCompare, int operator, boolean isCaseSensitive) {
        switch(operator) {
        case FieldCompareConstraint.EQUAL:         return stringToCompare.equals(constraintString);
            // TODO: MM: I think depending on the database configuration the case-sensitivity may be important in the following 4:
        case FieldCompareConstraint.GREATER:       return stringToCompare.compareTo(constraintString) > 0;
        case FieldCompareConstraint.LESS:          return stringToCompare.compareTo(constraintString) < 0;
        case FieldCompareConstraint.LESS_EQUAL:    return stringToCompare.compareTo(constraintString) <= 0;
        case FieldCompareConstraint.GREATER_EQUAL: return stringToCompare.compareTo(constraintString) >= 0;
        case FieldCompareConstraint.LIKE:          return likeMatches(constraintString, stringToCompare, isCaseSensitive);
        case FieldCompareConstraint.NOT_EQUAL:     return ! stringToCompare.equals(constraintString);
        default:
            log.warn("operator " + FieldCompareConstraint.OPERATOR_DESCRIPTIONS[operator] + "is not supported for type String");
            return true;
        }
    }

    private final static String escapeChars=".\\?+*$()[]{}^|&";
    private boolean likeMatches(String constraintString, String stringToCompare, boolean isCaseSensitive){
        if (log.isTraceEnabled()) {
            log.trace("** method: likeMatches() stringToCompare: " + stringToCompare + ", constraintString: " + constraintString );
        }
        if(isCaseSensitive){
            constraintString = constraintString.toLowerCase();
            stringToCompare = stringToCompare.toLowerCase();
        }
        char[] chars = constraintString.toCharArray();
        StringBuffer sb = new StringBuffer();

        for (char element : chars) {
            if(element == '?'){
                sb.append(".");
            } else if(element == '%'){
                sb.append(".*");
            } else if(escapeChars.indexOf(element) > -1){
                sb.append("\\");
                sb.append(element);
            } else{
                sb.append(element);
            }
        }
        if (log.isDebugEnabled()) {
            log.trace("** new pattern: " + sb.toString());
        }
        return stringToCompare.matches(sb.toString());
    }

    @Override
    public boolean matches(Object o) {
        boolean res;
        if (o == null) {
            res = true;
        } else {
            Class fieldType = getValue().getClass();
            Object compareValue = Casting.toType(fieldType, o);
            if (fieldType.equals(Boolean.class)) {
                switch(getOperator()) {
                case FieldCompareConstraint.EQUAL:     res =  getValue().equals(compareValue); break;
                case FieldCompareConstraint.NOT_EQUAL: res = ! getValue().equals(compareValue); break;
                default:
                    throw new UnsupportedOperationException();
                }
            } else if (fieldType.equals(Float.class)) {
                res = floatMatches((Float) getValue(), (Float) compareValue, getOperator());
            } else if (fieldType.equals(Double.class)) {
                res = floatMatches((Double) getValue(), (Double) compareValue, getOperator());
            } else if (fieldType.equals(Date.class)) {
                res = intMatches(((Date) getValue()).getTime(), ((Date) compareValue).getTime(), getOperator());
            } else if (fieldType.equals(Integer.class)) {
                res = intMatches((Integer) getValue(), (Integer) compareValue, getOperator());
            } else if (fieldType.equals(Long.class)) {
                res = intMatches((Long) getValue(), (Long) compareValue, getOperator());
            } else if (fieldType.equals(String.class) || fieldType.equals(org.w3c.dom.Document.class)) {
                res = stringMatches((String) getValue(), (String) compareValue, getOperator(), isCaseSensitive());
            } else {
                throw new UnsupportedOperationException();
            }
        }
        if (isInverse()) res = ! res;
        return res;
    }

    @Override
    public boolean equals(Object obj) {
        // Must be same class (subclasses should override this)!
        if (obj != null && obj.getClass() == getClass()) {
            BasicFieldValueConstraint constraint
                = (BasicFieldValueConstraint) obj;
            return isInverse() == constraint.isInverse()
                && isCaseSensitive() == constraint.isCaseSensitive()
                && getField().getFieldName().equals(constraint.getField().getFieldName())
                && BasicStepField.compareSteps(getField().getStep(),
                    constraint.getField().getStep())
                && getOperator() == constraint.getOperator()
                && BasicStepField.equalFieldValues(value, constraint.value);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return super.hashCode()
        + (value == null? 0: value.hashCode());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("BasicFieldValueConstraint(inverse:").append(isInverse()).
        append(", field:").append(getFieldName()).
        append(", casesensitive:").append(isCaseSensitive()).
        append(", operator:").append(getOperatorDescription()).
        append(", value:").append(getValue()).
        append(")");
        return sb.toString();
    }
}
