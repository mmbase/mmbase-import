/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search;

/**
 * A stepfield refers to a field in a step.
 *  Each stepfield has an unique alias to identify it.
 * <p>
 * This corresponds to a prefixed fieldname in a SQL SELECT-syntax.
 *
 * @author Rob van Maris
 * @version $Id$
 * @since MMBase-1.7
 */
public interface StepField {
    /**
     * Gets the name of the associated field (without prefix).
     * <p>
     * This corresponds to the fieldname in SQL SELECT-syntax.
     */
    String getFieldName();

    /**
     * Gets the alias for the associated field.
     * <p>
     * This corresponds to the field alias in SQL SELECT-syntax.
     */
    String getAlias();

    /**
     * Gets the step associated with this fieldstep.
     */
    Step getStep();

    /**
     * Gets the type of the associated field.
     * This is one of the values defined in {@link org.mmbase.bridge.Field Field}.
     */
    int getType();

    /**
     * Compares this stepfield to the specified object. The result is
     * <code>true</code> if and only if the argument is a non-null
     * StepField object associated with the same field, using the same alias.
     *
     * @param obj The object to compare with.
     * @return <code>true</code> if the objects are equal,
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object obj);

    // javadoc is inherited
    @Override
    public int hashCode();

    /**
     * Returns a string representation of this StepField.
     * The string representation has the form
     * "StepField(step:&lt;step&gt;, fieldname:&lt;fieldname&gt;, alias:&lt;alias&gt;)"
     * where
     * <ul>
     * <li><em>&lt;step&gt;</em> is the step alias returned by
     *     <code>getStep().getAlias()</code> or,
     *     when the step alias is <code>null</code>, the step tablename
     *     returned by <code>getStep().getTableName()</code>.
     * <li><em>&lt;fieldname&gt;</em> is the fieldname returned by
     *     {@link #getFieldName getFieldName()}
     * <li><em>&lt;alias&gt;</em> is the alias returned by {@link #getAlias getAlias()}
     * </ul>
     *
     * @return A string representation of this StepField.
     */
    @Override
    public String toString();

}
