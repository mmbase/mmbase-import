/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search.implementation;

import org.mmbase.storage.search.*;

/**
 * Basic implementation.
 *
 * @author Rob van Maris
 * @version $Id$
 * @since MMBase-1.7
 */
public class BasicDateSortOrder extends BasicSortOrder implements DateSortOrder {

    /** The date part. */
    private int part = -1; // unset

    /**
     * Constructor.
     * Creates new BasicDateSortOrder instance, with
     * direction <code>SortOrder.ORDER_ASCENDING</code>.
     *
     * @param field The associated stepfield.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    public BasicDateSortOrder(StepField field) {
        super(field);
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
        if (obj instanceof DateSortOrder) {
            DateSortOrder order = (DateSortOrder) obj;
            return super.equals(obj) && part == order.getPart();
        } else {
            return false;
        }
    }

    // javadoc is inherited
    public int hashCode() {
        return super.hashCode() + part * 117;
    }

    // javadoc is inherited
    public String toString() {
        return "DateSortOrder(field:" + BasicStepField.getFieldName(getField()) + ", dir:" + getDirectionDescription() +
               ", part: " + getPartDescription() + ")";
    }

}
