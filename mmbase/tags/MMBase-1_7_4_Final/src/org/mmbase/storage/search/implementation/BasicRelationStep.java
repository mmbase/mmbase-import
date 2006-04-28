/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search.implementation;

import org.mmbase.module.corebuilders.InsRel;
import org.mmbase.storage.search.*;

/**
 * Basic implementation.
 * <p>
 * The checkedDirectionality property defaults to false.
 * The directionality property defaults to DIRECTIONS_BOTH.
 *
 * @author Rob van Maris
 * @version $Id: BasicRelationStep.java,v 1.8 2004-02-23 19:01:03 pierre Exp $
 * @since MMBase-1.7
 */
public class BasicRelationStep extends BasicStep implements RelationStep {

    /** Checked directionality property. */
    private boolean checkedDirectionality = false;

    /** Directionality property. */
    private int directionality = RelationStep.DIRECTIONS_BOTH;

    /** Role property. */
    private Integer role = null;

    /** Previous step. */
    private Step previous = null;

    /** Next step. */
    private Step next = null;

    /**
     * Creator.
     *
     * @param builder The relation builder.
     * @param previous The previous step.
     * @param next The next step.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    // package visibility!
    BasicRelationStep(InsRel builder,
        Step previous, Step next) {
        super(builder);
        if (previous == null) {
            throw new IllegalArgumentException(
            "Invalid previous value: " + previous);
        }
        this.previous = previous;
        if (next == null) {
            throw new IllegalArgumentException(
            "Invalid next value: " + next);
        }
        this.next = next;
    }

    /**
     * Sets checkedDirectionality property.
     *
     * @param checkedDirectionality The checkedDirectionality property.
     * @return This <code>BasicRelationStep</code> instance.
     * @see #getCheckedDirectionality
     */
    public BasicRelationStep setCheckedDirectionality(
            boolean checkedDirectionality) {
        this.checkedDirectionality = checkedDirectionality;
        return this;
    }

    /**
     * Sets directionality property.
     *
     * @param directionality The directionality.
     * Must be one of the values defined in <code>
     * {@link org.mmbase.storage.search.RelationStep RelationStep}.</code>
     * @return This <code>BasicRelationStep</code> instance.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    public BasicRelationStep setDirectionality(int directionality) {
        if (directionality != RelationStep.DIRECTIONS_SOURCE
            && directionality != RelationStep.DIRECTIONS_DESTINATION
            && directionality != RelationStep.DIRECTIONS_BOTH
            && directionality != RelationStep.DIRECTIONS_ALL
            && directionality != RelationStep.DIRECTIONS_EITHER) {
            throw new IllegalArgumentException(
            "Invalid directionality value: " + directionality);
        }
        this.directionality = directionality;
        return this;
    }

    /**
     * Sets role property.
     *
     * @param role The role.
     * @return This <code>BasicRelationStep</code> instance.
     */
    public BasicRelationStep setRole(Integer role) {
        this.role = role;
        return this;
    }

    // javadoc is inherited
    public boolean getCheckedDirectionality() {
        return checkedDirectionality;
    }

    // javadoc is inherited
    public int getDirectionality() {
        return directionality;
    }

    // javadoc is inherited
    public Integer getRole() {
        return role;
    }

    // javadoc is inherited
    public Step getPrevious() {
        return previous;
    }

    // javadoc is inherited
    public Step getNext() {
        return next;
    }

    // javadoc is inherited
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof RelationStep) {
            RelationStep step = (RelationStep) obj;
            return getTableName().equals(step.getTableName())
                && getAlias().equals(step.getAlias())
                && getNodes().equals(step.getNodes())
                && step.getDirectionality() == directionality
                && (role == null? step.getRole() == null: role.equals(step.getRole()));
        } else {
            return false;
        }
    }

 // javadoc is inherited
    public int hashCode() {
        String alias = getAlias();
        return 41 * (getTableName().hashCode()
                     + 43 * ( (alias != null ? alias.hashCode() : 0)
                              + 47 * (getNodes().hashCode()
                                      + 113 * (directionality
                                               + 31 * (role != null ? role.intValue() : 0)))));
}

    // javadoc is inherited
    public String toString() {
        StringBuffer sb = new StringBuffer("RelationStep(tablename:");
        sb.append(getTableName()).
        append(", alias:").
        append(getAlias()).
        append(", nodes:").
        append(getNodes()).
        append(", dir:").
        append(RelationStep.DIRECTIONALITY_NAMES[getDirectionality()]).
        append(", role:").
        append(getRole()).
        append(")");
        return sb.toString();
    }

}
