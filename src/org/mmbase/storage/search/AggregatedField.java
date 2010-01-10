/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search;

/**
 * A field in an aggregated query.
 * <p>
 * Each field in a aggregated query has an aggregation type, which is one of
 * the values listed below.
 * </ul>
 * This corresponds to an aggregated field in SQL SELECT-syntax.
 *
 * @author Rob van Maris
 * @version $Id$
 * @since MMBase-1.7
 */
public interface AggregatedField extends StepField {

    /**
     * Aggregation type, resulting in grouping the results on non-null
     * values of this field.
     */
    public final static int AGGREGATION_TYPE_GROUP_BY = 1;

    /**
     * Aggregation type, resulting in count of non-null
     * values in this field.
     */
    public final static int AGGREGATION_TYPE_COUNT = 2;

    /**
     * Aggregation type, resulting in count of distinct non-null
     * values in this field.
     */
    public final static int AGGREGATION_TYPE_COUNT_DISTINCT = 3;

    /** Aggregation type, resulting in minimum value in this field. */
    public final static int AGGREGATION_TYPE_MIN = 4;

    /** Aggregation type, resulting in maximum value in this field. */
    public final static int AGGREGATION_TYPE_MAX = 5;

    /**
     * Search type descriptions corresponding to the search type values:
     * {@link #AGGREGATION_TYPE_GROUP_BY}, {@link #AGGREGATION_TYPE_COUNT}, {@link #AGGREGATION_TYPE_COUNT_DISTINCT},
     * {@link #AGGREGATION_TYPE_MIN}, and {@link #AGGREGATION_TYPE_MAX}
     */
    String[] AGGREGATION_TYPE_DESCRIPTIONS = new String[] {
         null, // not specified
         "group by", "count", "count distinct",
         "min", "max"
    };

    /**
     * Gets the aggregation type.
     */
    public int getAggregationType();

    /**
     * Compares this aggregated field to the specified object. The result is
     * <code>true</code> if and only if the argument is a non-null
     * <code>AggregatedField</code> object associated with the same field,
     * using the same alias, and having the same aggregation type.
     *
     * @param obj The object to compare with.
     * @return <code>true</code> if the objects are equal,
     * <code>false</code> otherwise.
     */
    public boolean equals(Object obj);

    /**
     * Returns a string representation of this AggregatedField.
     * The string representation has the form
     * "AggregatedField(step:&lt;step&gt;, fieldname:&lt;fieldname&gt;,
     *  alias:&lt;alias&gt;, aggregationtype:&lt;aggregationtype&gt;)"
     * where
     * <ul>
     * <li><em>&lt;step&gt;</em> is the step alias returned by
     *     <code>getStep().getAlias()</code> or,
     *     when the step alias is <code>null</code>, the step tablename
     *     returned by <code>getStep().getTableName()</code>.
     * <li><em>&lt;fieldname&gt;</em> is the fieldname returned by
     *     {@link #getFieldName getFieldName()}
     * <li><em>&lt;alias&gt;</em> is the alias returned by
     *     {@link #getAlias getAlias()}
     * <li><em>&lt;aggregationtype&gt;</em> is the alias returned by
     *     {@link #getAggregationType getAggregationType()}
     * </ul>
     *
     * @return A string representation of this AggregatedField.
     */
    public String toString();
}