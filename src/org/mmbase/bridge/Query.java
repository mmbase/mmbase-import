/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;
import org.mmbase.storage.search.*;
import java.util.SortedSet;

/**
 * Representation of a (database query). It is modifiable for use by bridge-users.
 *
 * @author Michiel Meeuwissen
 * @version $Id: Query.java,v 1.2 2003-07-21 20:51:34 michiel Exp $
 * @todo Not sure this interface needed, perhaps everything should be done with a query-converter
 * @since MMBase-1.7
 */
public interface Query extends SearchQuery, Cloneable {

    /**
     * Adds new step to this SearchQuery.
     *
     * @param builder The builder associated with the step.
     * @return The new step.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    Step addStep(NodeManager nodeManager);
    Step addStep(NodeManager nodeManager, String alias);
    

    
    /**
     * Adds new relationstep to this SearchQuery.
     * This adds the next step as well, it can be retrieved by calling <code>
     * {@link org.mmbase.storage.search.RelationStep#getNext getNext()}
     * </code> on the relationstep, and cast to {@link BasicStep BasicStep}.
     *
     * @param builder The builder associated with the relation step.
     * @param nextBuilder The builder associated with the next step.
     * @return The new relationstep.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     * @throws IllegalStateException when there is no previous step.
     */
    RelationStep addRelationStep(RelationManager relationManager);

    StepField addField(Step step, Field field);

    Query setDistinct(boolean distinct);
    Query setMaxNumber(int maxNumber);
    Query setOffset(int offset);

    /**
     * Constraints and so on..
     */

    FieldValueConstraint        createConstraint(StepField f, Object v);
    FieldValueConstraint        createConstraint(StepField f, int op, Object v);
    CompareFieldsConstraint     createConstraint(StepField f, int op, StepField  v);
    FieldValueBetweenConstraint createConstraint(StepField f, Object o1, Object o2);
    FieldValueInConstraint      createConstraint(StepField f, SortedSet v);
    CompositeConstraint         createConstraint(Constraint c1, int op, Constraint c2);

    void setConstraint(Constraint c);

    /**
     * Whether this query was used or not. It is was used, then you cannot modify it anymore (would
     * kill caches, and references to 'original query' would get invalid)
     */
    boolean isUsed();

    /**
     * Mark this query 'used'. It has to be copied first, if you want to add things to it.
     */
    boolean markUsed();

    /**
     * Create an (unused) clone
     */
    Object clone();

}
