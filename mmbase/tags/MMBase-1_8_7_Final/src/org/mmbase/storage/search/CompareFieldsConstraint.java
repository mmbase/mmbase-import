/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search;

/**
 * A constraint that compares the value of two stepfields. 
 *
 * @author Rob van Maris
 * @version $Id$
 * @since MMBase-1.7
 */
public interface CompareFieldsConstraint extends FieldCompareConstraint {
    /**
     * Gets the second associated field. 
     */
    StepField getField2();

    /**
     * Returns a string representation of this CompareFieldsConstraint. 
     * The string representation has the form 
     * "CompareFieldsConstraint(inverse:&lt:inverse&gt;, field:&lt;field&gt;, 
     *  casesensitive:&lt;casesensitive&gt;, operator:&lt;operator&gt;,
     *  field2:&lt;field2&gt;)"
     * where 
     * <ul>
     * <li><em>&lt;inverse&gt;</em>is the value returned by
     *      {@link #isInverse isInverse()}
     * <li><em>&lt;field&gt;</em> is the field alias returned by 
     *     <code>FieldConstraint#getField().getAlias()</code>
     * <li><em>&lt;casesensitive&gt;</em> is the value returned by
     *     {@link FieldConstraint#isCaseSensitive isCaseSensitive()}
     * <li><em>&lt;operator&gt;</em> is the value returned by
     *     {@link FieldCompareConstraint#getOperator getOperator()}
     * <li><em>&lt;field2&gt;</em> is the field alias returned by
     *     <code>#getField2().getAlias()</code>
     * </ul>
     *
     * @return A string representation of this CompareFieldsConstraint.
     */
    public String toString();

    /** @link dependency 
     * @supplierRole field2*/
    /*#StepField lnkStepField1;*/
}
