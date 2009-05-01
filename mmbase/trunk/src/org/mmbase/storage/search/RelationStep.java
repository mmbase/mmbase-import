/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search;

/**
 * A relationstep refers to a table of relations between the previous and next step. Relationstep are used to create a path of related objects.
 * <p>
 * This corresponds to a constraint joining the three tables in SQL SELECT-syntax.
 * <p>
 * Note that tables can also be joined using FieldCompareConstraints.
 *
 * @author Rob van Maris
 * @version $Id$
 * @since MMBase-1.7
 */
public interface RelationStep extends Step {

    /**
     * Directionality following relations both ways (source to destination AND destination to source).
     * Whether unidirectional relations are shown is dependent on the value of the checkedDirectionality property.
     */
    int DIRECTIONS_BOTH = 0;

    /**
     * Directionality following relations from source to destination.
     * E.g. where the previous step is source and the next step is destination.
     * Whether unidirectional relations are shown is dependent on the value of the checkedDirectionality property.
     */
    int DIRECTIONS_DESTINATION = 1;

    /**
     * Directionality following relations from destination to source.
     * E.g. where the previous step is destination and the next step is source.
     * This value ignores the value of the checkedDirectionality property.
     */
    int DIRECTIONS_SOURCE = 2;


    /**
     * Directionality following relations both ways, including unidirectional relations.
     * This value ignores the value of the checkedDirectionality property.
     */
    int DIRECTIONS_ALL = 4;

    /**
     * Directionality following relations from destination to source.
     * E.g. where the previous step is destination and the next step is source.
     * Whether unidirectional relations are shown is dependent on the value of the checkedDirectionality property.
     */
    int DIRECTIONS_EITHER = 5;

    /**
     * Directionality names corresponding to the direction values.
     * As a result DIRECTIONALITY_DESCRIPTIONS[directionality] is the directionality
     * name: "both", "destination", "source", "all" or "either".
     */
    public final static String[] DIRECTIONALITY_DESCRIPTIONS = new String[] {
         "both",
         "destination",
         "source",
         null, // reserved
         "all",
         "either"
    };

    /**
     * Gets the value of the checkedDirectionality property. This property
     * determines how uni/bi-directionality affects which relations are
     * followed from destination to source, when the directionality property
     * is set to {@link #DIRECTIONS_SOURCE} or {@link #DIRECTIONS_BOTH}.
     * <p>
     * When this value is true, only bi-directional relations are followed
     * from destination to source.
     * Otherwise unidirectional relations are followed from destination to
     * source as well.
     */
    boolean getCheckedDirectionality();

    /**
     * Gets the directionality mode used with this relation. This is one of
     * values defined in this class.
     */
    int getDirectionality();

    /**
     * Gets the role for this relation, if specified.
     * I.e. the nodenumber of the corresponding
     * {@link org.mmbase.module.corebuilders.RelDef RelDef} node, or
     * <code>null</code>.
     */
    Integer getRole();

    /**
     * Gets the previous step.
     */
    Step getPrevious();

    /**
     * Gets the next step.
     */
    Step getNext();

    /**
     * Compares this relationstep to the specified object. The result is
     * <code>true</code> if and only if the argument is a non-null
     * RelationStep object with the same directionality and role,
     * associated with the same tablename,
     * using the same alias and including the same nodes.
     *
     * @param obj The object to compare with.
     * @return <code>true</code> if the objects are equal,
     * <code>false</code> otherwise.
     * @see Step#equals
     */
    public boolean equals(Object obj);

    // javadoc is inherited
    public int hashCode();

    /**
     * Returns a string representation of this RelationStep.
     * The string representation has the form
     * "RelationStep(tablename:&lt;tablename&gt;, alias:&lt;alias&gt;,
     *  nodes:&lt;nodes&gt;, dir:&lt;dir&gt;, role:&lt;role&gt;)"
     * where
     * <ul>
     * <li><em>&lt;tablename&gt;</em> is the tablename returnedby
     *     {@link #getTableName getTableName()}
     * <li><em>&lt;alias&gt;</em> is the alias returned by {@link #getAlias getAlias()}
     * <li><em>&lt;nodes&gt;</em> is the string representation of the ordered list
     *      of nodenumbers returned by {@link #getNodes getNodes()}
     * <li><em>&lt;dir&gt;</em> is the name of
     *     the directionality returned by
     *     {@link #getDirectionality getDirectionality()}
     * <li><em>&lt;role&gt;</em> is the role returned by
     *     {@link #getRole getRole()}
     * </ul>
     *
     * @return A string representation of this RelationStep.
     */
    public String toString();

}
