/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search.implementation;

import org.mmbase.module.corebuilders.FieldDefs;
import org.mmbase.storage.search.*;

/**
 * Basic implementation.
 * The step alias is equal to the field name, unless it is explicitly set.
 *
 * @author Rob van Maris
 * @version $Revision: 1.1 $
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
            throw new IllegalArgumentException("Invalid value for "
            + FieldDefs.getDBTypeDescription(type) + " field: "
            + value);
        }
        
        // Test for compatible type.
        boolean ok = true;
        switch (type) {
            // Numberical types.
            case FieldDefs.TYPE_INTEGER:
            case FieldDefs.TYPE_BYTE:
            case FieldDefs.TYPE_FLOAT:
            case FieldDefs.TYPE_DOUBLE:
            case FieldDefs.TYPE_LONG:
            case FieldDefs.TYPE_NODE:
                if (!(value instanceof Number)) {
                    ok = false;
                }
                break;
                
                // String types.
            case FieldDefs.TYPE_STRING:
            case FieldDefs.TYPE_XML:
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
        
        // Alias defaults to field name.
        alias = fieldDefs.getDBName();
    }
    
    /**
     * Sets alias property.
     *
     * @param alias The alias property.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    public void setAlias(String alias) {
        if (alias == null || alias.trim().length() == 0) {
            throw new IllegalArgumentException(
            "Invalid alias value: " + alias);
        }
        this.alias = alias;
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
            return getStep().getAlias().equals(field.getStep().getAlias())
                && getFieldName().equals(field.getFieldName())
                && alias.equals(field.getAlias());
        } else {
            return false;
        }
    }
    
    // javadoc is inherited
    public int hashCode() {
        return 51 * getStep().getAlias().hashCode()
        + 53 * getFieldName().hashCode() + 59 * alias.hashCode();
    }

    // javadoc is inherited
    public String toString() {
        StringBuffer sb = new StringBuffer("StepField(step:");
        sb.append(getStep().getAlias()).
        append(", fieldname:").
        append(getFieldName()).
        append(", alias:").
        append(getAlias()).
        append(")");
        return sb.toString();
    }
    
}
