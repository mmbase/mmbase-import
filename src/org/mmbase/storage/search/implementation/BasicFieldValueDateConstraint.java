/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search.implementation;

import org.mmbase.storage.search.*;

/**
 * A constraint on a 'part' of a DateTime field. E.g. where extract(YEAR, lastmodified) = 2004.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8
 */
public class BasicFieldValueDateConstraint extends BasicFieldValueConstraint implements FieldValueDateConstraint {

    /** The date part. */
    private int part = -1; // unset

    /**
     * Constructor.
     * Depending on the field type, the value must be of type
     * <code>String</code> or <code>Number</code>.
     *
     * @param field The associated field.
     * @param value The non-null property value.
     * @param part  Which part of the date to compare
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    public BasicFieldValueDateConstraint(StepField field, Object value, int part) {
        super(field, value);
        if (field.getType() != org.mmbase.bridge.Field.TYPE_DATETIME) {
            throw new IllegalArgumentException("Date value constraints can only be applied to 'DATETIME' type fields");
        }
        setPart(part);
    }

    public int getPart() {
        return part;
    }

    /**
     * Returns a description of the part
     */
    public String getPartDescription() {
        try {
            return FieldValueDateConstraint.PART_DESCRIPTIONS[part];
        } catch (IndexOutOfBoundsException ioobe) {
            return null;
        }
    }

    public void setPart(int p) {
        part = p;
    }

    // javadoc is inherited
    public boolean equals(Object obj) {
        return super.equals(obj) && ((FieldValueDateConstraint)obj).getPart() == part;
    }

    // javadoc is inherited
    public int hashCode() {
        return super.hashCode() + part * 117;
    }

    // javadoc is inherited
    public String toString() {
        StringBuilder sb = new StringBuilder("BasicFieldValueDateConstraint(inverse:").append(isInverse()).
        append(", field:").append(getFieldName()).
        append(", casesensitive:").append(isCaseSensitive()).
        append(", operator:").append(getOperatorDescription()).
        append(", value:").append(getValue()).
        append(", date-part:").append(getPartDescription()).
        append(")");
        return sb.toString();
    }
}
