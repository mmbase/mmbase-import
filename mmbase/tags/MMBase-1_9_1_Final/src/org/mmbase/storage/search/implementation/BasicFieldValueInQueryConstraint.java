/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search.implementation;

import java.util.*;
import org.mmbase.storage.search.*;

/**
 * Basic implementation.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9.1
 */
public class BasicFieldValueInQueryConstraint extends BasicFieldConstraint implements FieldValueInQueryConstraint {


    private final SearchQuery query;

    /**
     * Constructor.
     *
     * @param field The associated field.
     */
    public BasicFieldValueInQueryConstraint(StepField field, SearchQuery q) {
        super(field);
        query = q;
        if (q.getFields().size() != 1) throw new IllegalArgumentException("Can only list one field");
    }


    public SearchQuery getInQuery() {
        return query;
    }

    // javadoc is inherited
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        // Must be same class (subclasses should override this)!
        if (obj != null && obj.getClass() == getClass()) {
            BasicFieldValueInQueryConstraint constraint = (BasicFieldValueInQueryConstraint) obj;

            return isInverse() == constraint.isInverse()
                && isCaseSensitive() == constraint.isCaseSensitive()
                && getField().getFieldName().equals(constraint.getField().getFieldName())
                && BasicStepField.compareSteps(getField().getStep(),constraint.getField().getStep())
                && query.equals(constraint.query);
        } else {
            return false;
        }
    }

    // javadoc is inherited
    public int hashCode() {
        return super.hashCode()
            + 89 * query.hashCode();
    }

    // javadoc is inherited
    public String toString() {
        StringBuilder sb = new StringBuilder("FieldValueInConstraint(inverse:").append(isInverse()).
            append(", field:").append(getFieldName()).
            append(", casesensitive:").append(isCaseSensitive()).
            append(", query:").append(query.toString()).
            append(")");
        return sb.toString();
    }

}
