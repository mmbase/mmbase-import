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
 * @version $Id: Query.java,v 1.5 2003-07-22 10:55:40 michiel Exp $
 * @todo Not sure this interface needed, perhaps everything should be done with a query-converter
 * @since MMBase-1.7
 */
public interface Query extends SearchQuery, Cloneable {

    /**
     * Adds a NodeManager to this Query.
     *
     * @param nodeManager The nodeManager associated with the step.
     * @return The 'step' wrapping the NodeManager.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    Step addStep(NodeManager nodeManager);
    
    
    /**
     * Adds new RelationManager to the query.  Adds the next Step (containing the Destination
     * Manager) as well, it can be retrieved by calling <code> {@link
     * org.mmbase.storage.search.RelationStep#getNext getNext()} </code> on the relationstep, and
     * cast to {@link Step Step}.
     *
     * @param RelationManager the relation type associated with the step
     * @return The new relationstep.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     * @throws IllegalStateException when there is no previous step.
     */
    RelationStep addRelationStep(RelationManager relationManager);

    /**
     * Also explicitely state the direction of the relation. This can be needed if the
     * RelationManager has two equals sides.
     * @param RelationManager the relation type associated with the step
     * @return The new relationstep.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     * @throws IllegalStateException when there is no previous step.
     */
    RelationStep addRelationStep(RelationManager relationManager, int directionality);

    /**
     * Adds a field to a step.
     */
    StepField addField(Step step, Field field);

    /**
     * Specifies wether the query result must contain only 'distinct' results.
     * @see org.mmbase.storage.search.implementation.BasicSearchQuery#setDistinct
     * @see #isDistinct
     */
    Query setDistinct(boolean distinct);

    /**
     * Limits the query-result to maxNumber records.
     * @see org.mmbase.storage.search.implementation.BasicSearchQuery#setMaxNumber
     * @see #getMaxNumber
     */
    Query setMaxNumber(int maxNumber);

    /**
     * Offsets the query-result with offset records.
     * @see org.mmbase.storage.search.implementation.BasicSearchQuery#setOffset
     * @see #getOffset
     */
    Query setOffset(int offset);


    // Constraints and so on..


    /**
     * Create a contraint (for use with this Query object). The given field must be 'null'.
     */
    FieldNullConstraint         createConstraint(StepField f);

    /**
     * Create a contraint (for use with this Query object). The given field must equal the given
     * value 'v'.
     */
    FieldValueConstraint        createConstraint(StepField f, Object v);

    /**
     * Create a contraint (for use with this Query object). The given field and the given
     * value 'v', combined with given operator must evaluate to true.
     */
    FieldValueConstraint        createConstraint(StepField f, int op, Object v);
    /**
     * Create a contraint (for use with this Query object). The two given fields , combined with
     * given operator must evaluate to true.
     */
    CompareFieldsConstraint     createConstraint(StepField f, int op, StepField  v);
    /**
     * Create a contraint (for use with this Query object). The given field must lie between the
     * two given values.
     */
    FieldValueBetweenConstraint createConstraint(StepField f, Object o1, Object o2);
    /**
     * Create a contraint (for use with this Query object). The given field value must be contained
     * by the given set of values. 
     */
    FieldValueInConstraint      createConstraint(StepField f, SortedSet v);

    /**
     * Combines two Constraints to one new one, using a boolean operator (AND or OR). Every new
     * constraint must be combined with the ones you already have with such a new CompositeConstraint.
     */
    CompositeConstraint         createConstraint(Constraint c1, int op, Constraint c2);

    /**
     * The (composite) constraint can actually be set into the query with this method.
     */
    void setConstraint(Constraint c);


    /**
     * Adds an order on a certain field.
     * @see org.mmbase.storage.search.implementation.BasicSearchQuery#addSortOrder
     * @see #getSortOrders
     */
    SortOrder addSortOrder(StepField f, int direction);

    /**
     * Whether this query was used or not. If is was used, then you cannot modify it anymore (would
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
