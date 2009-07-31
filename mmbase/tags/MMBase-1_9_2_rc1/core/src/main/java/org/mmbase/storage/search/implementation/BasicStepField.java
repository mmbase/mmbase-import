/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search.implementation;

import org.mmbase.bridge.Field;
import org.mmbase.core.CoreField;
import org.mmbase.storage.search.*;
import org.mmbase.util.SizeMeasurable;
import org.mmbase.util.SizeOf;

/**
 * Basic implementation.
 * The field alias is not set on default.
 *
 * @author Rob van Maris
 * @version $Id$
 * @since MMBase-1.7
 */
public class BasicStepField implements StepField, SizeMeasurable {

    private final CoreField field;

    private final Step step;

    private String alias = null;

    protected boolean modifiable = true;

    /**
     * Tests if a value is acceptable for comparison with a certain field.
     * @param value The value to be tested.
     * @param field The non-null field.
     * @throws IllegalArgumentException when the value is not acceptable
     *         for this field.
     */
    // package visibility!
    static void testValue(Object value, StepField field) {
        int type = field.getType();

        // Test for null value.
        if (value == null) {
            throw new IllegalArgumentException("Invalid value for " + org.mmbase.core.util.Fields.getTypeDescription(type) + " field: " + value);
        }

        // Test for compatible type.
        boolean ok;
        switch (type) {
        case Field.TYPE_BINARY:
            ok = value instanceof byte[];
            break;
            // Numerical types.
        case Field.TYPE_INTEGER:
        case Field.TYPE_LONG:
            ok = value instanceof Number || value instanceof java.util.Date;
            break;
        case Field.TYPE_FLOAT:
        case Field.TYPE_DOUBLE:
        case Field.TYPE_NODE:
            ok = value instanceof Number || value instanceof org.mmbase.bridge.Node;
            break;

            // String types.
        case Field.TYPE_XML:

        case Field.TYPE_STRING:
            ok = value instanceof String;
            break;
        case Field.TYPE_BOOLEAN:
            ok = value instanceof Boolean;
            break;
        case Field.TYPE_DATETIME:
            ok = value instanceof java.util.Date || value instanceof Number;
            break;
        case Field.TYPE_LIST:
            ok = value instanceof java.util.List;
            break;


        default: // Unknown field type, should not occur.
            throw new IllegalStateException("Unknown field type: " + type);
        }

        if (!ok) {
            throw new IllegalArgumentException("Invalid value for " + org.mmbase.core.util.Fields.getTypeDescription(type) + " field: "
                                               + value + ", of type " + value.getClass().getName());
        }
    }

    /**
     * Compares two field values for equality.
     * Numerical fields are compared based on their numerical value, as they
     * may be of different type.
     *
     * @param value1 The first value, either a <code>String</code>
     *        or a <code>Number</code>
     * @param value2 The second value, either a <code>String</code>
     *        or a <code>Number</code>
     * @return True if both values represent the same string or numerical value.
     */
    // package visibility!
    static boolean equalFieldValues(Object value1, Object value2) {
        if (value1 instanceof Number) {
            if (value2 instanceof Number) {
                Number number1 = (Number) value1;
                Number number2 = (Number) value2;
                return Double.doubleToLongBits(number1.doubleValue()) == Double.doubleToLongBits(number2.doubleValue());
            } else {
                return false;
            }
        } else {
            return (value1 == null? value2 == null: value1.equals(value2));
        }
    }

    /**
     * Constructor.
     *
     * @param step The associated step.
     * @param field The associated field.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    public BasicStepField(Step step, CoreField field) {
        if (step == null) {
            throw new IllegalArgumentException("Invalid step value: " + step);
        }
        this.step = step;

        if (field == null) {
            throw new IllegalArgumentException("Invalid field value: " + field + " for " + step);
        }
        // Check field belongs to step
        if (!step.getTableName().equals(field.getParent().getTableName())) {
            throw new IllegalArgumentException("Invalid field value, belongs to step " + field.getParent().getTableName()
                                               + " instead of step " +  step.getTableName() + ": "
                                               + field);
        }
        this.field = field;
    }

    /**
     * @since MMBase-1.9.2
     */
    public void setUnmodifiable() {
        modifiable = true;
    }


    /**
     * Sets alias property.
     *
     * @param alias The alias property.
     * @return This <code>BasicStepField</code> instance.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    public BasicStepField setAlias(String alias) {
        if (! modifiable) throw new IllegalStateException();
        if (alias != null && alias.trim().length() == 0) {
            throw new IllegalArgumentException("Invalid alias value: " + alias);
        }
        this.alias = alias;
        return this;
    }

    /**
     * Gets the associated field.
     *
     * @return The field.
     */
    public CoreField getField() {
        return field;
    }

    // javadoc is inherited
    public final String getFieldName() {
        return field.getName();
    }

    // javadoc is inherited
    public String getAlias() {
        return alias;
    }

    // javadoc is inherited
    public Step getStep() {
        return step;
    }

    // javadoc in inherited
    public int getType() {
        return field.getType();
    }

    // javadoc is inherited
    public boolean equals(Object obj) {
        if (obj instanceof StepField) {
            StepField f = (StepField) obj;
            return field.getName().equals(f.getFieldName())
                && compareSteps(step, f.getStep())
                && (alias == null? f.getAlias() == null : alias.equals(f.getAlias()));
        } else {
            return false;
        }
    }

    // javadoc is inherited
    public int hashCode() {
        return (getStep().getAlias() == null?
            47 * getStep().getTableName().hashCode():
            51 * getStep().getAlias().hashCode())
        + 53 * getFieldName().hashCode()
        + (alias == null? 0: 59 * alias.hashCode());
    }

    /**
     * Utility method, compares steps by their alias or table name.
     * Steps are considered equal if their aliases are equal.
     * When their aliases are <code>null</code>, the steps are considered
     * equal if their tablenames are equal as well.
     * <p>
     * This can be used to verify that both steps refer to the same step
     * in a <code>SearchQuery</code> object.
     * Note that this differs from the equality defined by their
     * {@link org.mmbase.storage.search.Step#equals() equals()} method.
     *
     * @param step1 The first step.
     * @param step2 The second step.
     * @return <code>true</code> when the steps are considered equal,
     *         <code>false</code> otherwise.
     */
    // package visibility!
    static boolean compareSteps(Step step1, Step step2) {
        String alias1 = step1.getAlias();
        if (alias1 == null) {
            return step2.getAlias() == null
            && step1.getTableName().equals(step2.getTableName());
        } else {
            return alias1.equals(step2.getAlias());
        }
    }

    /**
     * Returns the field's fieldname, possibly extended with the step's name if known.
     * May return null or partial fieldnames if not all data is available (for use in debugging).
     * @param field the fieldname whose name to return
     */
    static public String getFieldName(StepField field) {
        String fieldName = null;
        if (field != null) {
            fieldName = field.getAlias();
            if (fieldName == null) {
                fieldName = field.getFieldName();
            }
            Step step = field.getStep();
            if (step != null)  {
                if (step.getAlias() != null) {
                    fieldName = step.getAlias() + "." + fieldName;
                } else {
                    fieldName = step.getTableName() + "." + fieldName;
                }
            }
        }
        return fieldName;
    }


    // javadoc is inherited
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Step step = getStep();
        if (step == null) {
            sb.append("null");
        } else {
            String stepAlias = step.getAlias();
            if (stepAlias == null) {
                sb.append(getStep().getTableName());
            } else {
                sb.append(stepAlias);
            }
        }
        sb.append(".").append(getFieldName());
        String alias = getAlias();
        if (alias != null) {
            sb.append(" as ").append(alias);
        }
        return sb.toString();
    }

    public int getByteSize() {
        return getByteSize(new SizeOf());
    }

    public int getByteSize(SizeOf sizeof) {
        int size = 21;
        size += sizeof.sizeof(alias);
        return size;
    }

}
