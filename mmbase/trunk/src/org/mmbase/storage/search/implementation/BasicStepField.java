/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search.implementation;

import org.mmbase.bridge.Node;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.module.corebuilders.FieldDefs;
import org.mmbase.storage.search.*;

/**
 * Basic implementation.
 * The field alias is not set on default.
 *
 * @author Rob van Maris
 * @version $Id: BasicStepField.java,v 1.8 2004-01-30 12:25:49 pierre Exp $
 * @since MMBase-1.7
 */
public class BasicStepField implements StepField {

    /** Associated field definition. */
    private FieldDefs fieldDefs = null;

    /** Associated step. */
    private Step step = null;

    /** Alias property. */
    private String alias = null;

    /**
     * Tests if a value is acceptable for comparison with a certain field, and
     * returns the value in the best useable form.
     * For a INTEGER, FLOAT, DOUBLE, or LONG field, the value need be a Number or a
     * String containing a number, and the method returns teh vaklue as a Number.
     * For a NODE field, acceptable values are a number, an MMObjectNode, or a bridge
     * Node object, and the method returns the value as a Number.
     * A STRING or XML field should have a string value.
     * A BYTE should be a byte array.
     *
     * @todo accept DOM objects for XML fields and aliasses for nodes
     * @param value The value to be tested.
     * @param field The non-null field.
     * @return the value in the best useable form for comparison with the field
     * @throws IllegalStateException if the field type is unknown.
     * @throws IllegalArgumentException when the value is <code>null</code> or not acceptable
     *         for this field.
     */
    // package visibility!
    static Object testValue(Object value, StepField field) {
        int type = field.getType();

        // Test for null value.
        if (value == null) {
            throw new IllegalArgumentException("Invalid value for "
            + FieldDefs.getDBTypeDescription(type) + " field: "
            + value);
        }

        // Test for compatible type.
        boolean ok = true;
        switch (type) {
            // byte array types
            case FieldDefs.TYPE_BYTE: //(keesj:) byte in mmbase stands for byte array
                                      //I'm not sure a byte array is numerical
                if (!(value instanceof byte[])) {
                    ok = false;
                }
                break;
            // Node type (can also be a number).
            // XXX TODO: This code does not take into account the use of aliasses
            case FieldDefs.TYPE_NODE:
                if (value instanceof MMObjectNode) { // core node as a value
                    value = new Long(((MMObjectNode)value).getNumber());
                    break;
                }
                if (value instanceof Node) { // bridge node as a value
                    value = new Long(((Node)value).getNumber());
                    break;
                }
            // Numberical types.
            case FieldDefs.TYPE_INTEGER:
            case FieldDefs.TYPE_FLOAT:
            case FieldDefs.TYPE_DOUBLE:
            case FieldDefs.TYPE_LONG:
                if (!(value instanceof Number)) {
                    // attempt to convert a string to a number
                    try {
                       value = Double.valueOf(value.toString());
                    } catch (NumberFormatException nfe) {
                        ok = false;
                    }
                }
                break;
                // String types.
            case FieldDefs.TYPE_XML:
                // XXX TODO: This code does not take into account the use of DOM objects
            case FieldDefs.TYPE_STRING:
                if (!(value instanceof String)) {
                    ok = false;
                }
                break;

            default: // Unknown field type, should not occur.
                throw new IllegalStateException("Unknown field type: " + type);
        }
        if (!ok) {
            throw new IllegalArgumentException("Invalid value for "
            + FieldDefs.getDBTypeDescription(type) + " field: "
            + value + ", of type " + value.getClass().getName());
        }
        return value;
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
                return number1.doubleValue() == number2.doubleValue();
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
     * @param fieldDefs The associated fieldDefs.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    public BasicStepField(Step step, FieldDefs fieldDefs) {
        if (step == null) {
            throw new IllegalArgumentException(
            "Invalid step value: " + step);
        }
        this.step = step;

        if (fieldDefs == null) {
            throw new IllegalArgumentException(
            "Invalid fieldDefs value: " + fieldDefs);
        }
        // Check fieldDefs belongs to step
        if (!step.getTableName().equals(fieldDefs.getParent().getTableName())) {
            throw new IllegalArgumentException(
            "Invalid fieldDefs value, belongs to step " + fieldDefs.getParent().getTableName()
            + " instead of step " +  step.getTableName() + ": "
            + fieldDefs);
        }
        this.fieldDefs = fieldDefs;
    }

    /**
     * Sets alias property.
     *
     * @param alias The alias property.
     * @return This <code>BasicStepField</code> instance.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    public BasicStepField setAlias(String alias) {
        if (alias != null && alias.trim().length() == 0) {
            throw new IllegalArgumentException(
            "Invalid alias value: " + alias);
        }
        this.alias = alias;
        return this;
    }

    /**
     * Gets the associated fieldDefs.
     *
     * @return The fieldDefs.
     */
    public FieldDefs getFieldDefs() {
        return fieldDefs;
    }

    // javadoc is inherited
    public String getFieldName() {
        return fieldDefs.getDBName();
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
        return fieldDefs.getDBType();
    }

    // javadoc is inherited
    public boolean equals(Object obj) {
        if (obj instanceof StepField) {
            StepField field = (StepField) obj;
            return BasicStepField.compareSteps(getStep(), field.getStep())
                && getFieldName().equals(field.getFieldName())
                && (alias == null? true: alias.equals(field.getAlias()));
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

    // javadoc is inherited
    public String toString() {
        StringBuffer sb = new StringBuffer("StepField(step:");
        if (getStep().getAlias() == null) {
            sb.append(getStep().getTableName());
        } else {
            sb.append(getStep().getAlias());
        }
        sb.append(", fieldname:").append(getFieldName()).
        append(", alias:").append(getAlias()).append(")");
        return sb.toString();
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

}
