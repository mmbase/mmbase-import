/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search;

/**
 * A constraint on a stepfield.
 *
 * @author Rob van Maris
 * @version $Id$
 * @since MMBase-1.7
 */
public interface FieldConstraint extends Constraint {
    /**
     * Gets the associated stepfield. 
     */
    StepField getField();

    /**
     * Tests if a the string comparison is case sensitive. This property is ignored if the associated field is not of string type.
     */
    boolean isCaseSensitive();

    /** @link dependency 
     * @clientRole **/
    /*#StepField lnkStepField;*/
}
