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
 * @version $Id: BasicSortOrder.java,v 1.5 2003-07-14 20:47:11 michiel Exp $
 * @since MMBase-1.7
 */
public class BasicSortOrder implements SortOrder {
    
    /** Associated stepfield. */
    private StepField field = null;
    
    /** Direction property. */
    private int direction = SortOrder.ORDER_ASCENDING;
    
    /**
     * Constructor.
     * Creates new BasicSortOrder instance, with
     * direction <code>SortOrder.ORDER_ASCENDING</code>.
     *
     * @param field The associated stepfield.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    public BasicSortOrder(StepField field) {
        if (field == null) {
            throw new IllegalArgumentException(
            "Invalid field value: " + field);
        }
        this.field = field;
    }
    
    /**
     * Sets direction.
     *
     * @param direction The direction.
     * @return This <code>BasicSortOrder</code> instance.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    public BasicSortOrder setDirection(int direction) {
        if (direction != SortOrder.ORDER_ASCENDING
        && direction != SortOrder.ORDER_DESCENDING) {
            throw new IllegalArgumentException(
            "Invalid direction value: " + direction);
        }
        this.direction = direction;
        return this;
    }
    
    // javadoc is inherited
    public StepField getField() {
        return field;
    }
    
    // javadoc is inherited
    public int getDirection() {
        return direction;
    }
    
    // javadoc is inherited
    public boolean equals(Object obj) {
        if (obj instanceof SortOrder) {
            SortOrder order = (SortOrder) obj;
            return field.getFieldName().equals(order.getField().getFieldName())
                && BasicStepField.compareSteps(
                    field.getStep(), order.getField().getStep())
                && direction == order.getDirection();
        } else {
            return false;
        }
    }
    
    // javadoc is inherited
    public int hashCode() {
        String alias = field.getStep().getAlias();
        return 
            61 * field.getFieldName().hashCode() 
            + 67 * (alias != null ? alias.hashCode() : 1) + 103 * direction;
    }

    // javadoc is inherited
    public String toString() {
        return "SortOrder(field:" + getField().getAlias() + ", dir:" + getDirection() + ")";
    }
    
}
