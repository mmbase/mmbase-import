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
 * Representation of a (database) query. It is modifiable for use by bridge-users.
 *
 * @author Michiel Meeuwissen
 * @version $Id: Query.java,v 1.22 2003-12-09 22:51:38 michiel Exp $
 * @since MMBase-1.7
 */
public interface Query extends SearchQuery, Cloneable {

    /**
     * Returns the Cloud for which this Query was defined.
     */
    Cloud getCloud();

    /**
     * Wheter this query is 'aggregating'. You can only use 'addAggregatedField' on aggregating querys.
     * @todo Should this not appear in SearchQuery itself? Or should there be an AggregatingQuery interface?
     * It is now used in BasicCloud.getList.
     */
    boolean isAggregating();

    /**
     * Adds a NodeManager to this Query. This can normally be done only once. After that you need
     * to use (one of the) 'addRelationStep'.
     *
     * @param nodeManager The nodeManager associated with the step.
     * @return The 'step' wrapping the NodeManager.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     * @see #addRelationStep
     */
    Step addStep(NodeManager nodeManager);

    /**
     * Sets the alias to the given step.
     * @param alias The alias which must be given to the step. If it is "" an alias should be
     * generated. 'null' removes the alias.
     */
    void setAlias(Step step, String alias);

    /**
     * Returns the step with given alias, or null if it is not present
     * @param stepAlias Alias for the step (may also be tableName, in which case the first step for this table is returned)
     */
    Step getStep(String stepAlias);

    /**
     * Adds new Relation step to the query.  Adds the next step as well, it can be retrieved by
     * calling <code> {@link org.mmbase.storage.search.RelationStep#getNext getNext()} </code> on
     * the relationstep, and cast to {@link Step Step}.
     *
     * @throws IllegalArgumentException when an invalid argument is supplied.
     * @throws IllegalStateException when there is no previous step.
     */
    RelationStep addRelationStep(NodeManager nodeManager, String role, String searchDir);

    /*
     * If you need to add a 'related' NodeManager without specifying a role/searchDir
     * simply use these addRelationStep.
     */

    RelationStep addRelationStep(NodeManager otherManager);


    /**
     * Adds a field to a step.
     */
    StepField addField(Step step, Field field);

    /**
     * Adds a field by string
     */
    StepField addField(String field);

    /**
     * Creates a StepField object withouth adding it (needed for aggregated queries).
     */
    StepField createStepField(Step step, Field field);

    StepField createStepField(Step step, String fieldName);


    /**
     * Creates the step field for the given name. For a NodeQuery the arguments is simply the name of the
     * field. For a 'normal' query, it should be prefixed by the (automatic) alias of the Step.
     */

    StepField createStepField(String fieldIdentifer);

    /**
     * Add an aggregated field to a step
     */
    AggregatedField addAggregatedField(Step step, Field field, int aggregationType);


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
     * Create a contraint (for use with this Query object). The argument is a string, as also can be
     * used as an argument of the 'non-query' getList. This should be considered legacy.
     * @see  Cloud#getList(String startNodes, String nodePath, String fields, String constraints, String orderby, String directions, String searchDir, boolean distinct)
     * @see  NodeManager#getList(String constraints, String orderby, String directions)
     */
    LegacyConstraint           createConstraint(String s);


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
     * by the given set of values. If the given set is empty, a FieldValueInConstraint will be
     * constructed for the number field in stead ('number IN (-1)'), which ensures that also in that
     * case the logical thing will happen. ('<field> IN ()' fails in most databases).
     *
     * @return the new Constraint.
     */
    FieldValueInConstraint      createConstraint(StepField f, SortedSet v);

    /**
     * Changes the given constraint's 'case sensitivity' (if applicable). Default it is true.
     */
    FieldConstraint             setCaseSensitive(FieldConstraint constraint, boolean sensitive);

    /**
     * Changes the given constraint's 'inverse' (if applicable). Default it is (of course) false.
     */
    Constraint                  setInverse(Constraint c, boolean i);

   /**
     * Combines two Constraints to one new one, using a boolean operator (AND or OR). Every new
     * constraint must be combined with the ones you already have with such a new CompositeConstraint.
     *
     * If the first constraint is a composite constraint (with the same logical operator), then the
     * second one will simply be added.
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
     * Adds a node to a step.
     */
    void      addNode(Step s, Node node);

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

    /**
     * Clones this object, only without the fields
     */
    Query cloneWithoutFields();

    /**
     * Creates an unused aggregate clone of this query. If this query is not itself aggregated, all
     * fields are removed (but the contraints on them remain), and you can add aggregated fields
     * then.
     */
    Query aggregatingClone();




}
