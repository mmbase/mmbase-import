/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

import java.util.SortedSet;

import org.mmbase.storage.search.*;

/**
 * Representation of a (database) query. It is modifiable for use by bridge-users.
 *
 * @author Michiel Meeuwissen
 * @author Pierre van Rooden
 * @version $Id$
 * @since MMBase-1.7
 * @see org.mmbase.bridge.util.Queries
 */
public interface Query extends SearchQuery, org.mmbase.util.PublicCloneable<Query> {

    /**
     * Returns the Cloud for which this Query was defined.
     * @return Cloud
     */
    Cloud getCloud();


    /**
     * Adds a NodeManager to this Query. This can normally be done only once. After that you need
     * to use (one of the) 'addRelationStep'.
     *
     * @param nodeManager The nodeManager associated with the step.
     * @return The 'step' wrapping the NodeManager.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     * @see #addRelationStep(NodeManager)
     */
    Step addStep(NodeManager nodeManager);

    /**
     * Sets the alias to the given step.
     * @param step step to add the alias for
     * @param alias The alias which must be given to the step. If it is "" an alias should be
     * generated. 'null' removes the alias.
     */
    void setAlias(Step step, String alias);

    /**
     * Returns the step with given alias, or null if it is not present
     * @param stepAlias Alias for the step (may also be tableName, in which case the first step for this table is returned)
     * @return step with given alias
     */
    Step getStep(String stepAlias);

    /**
     * Adds new Relation step to the query.  Adds the next step as well, it can be retrieved by
     * calling <code> {@link org.mmbase.storage.search.RelationStep#getNext getNext()} </code> on
     * the relationstep, and cast to {@link Step Step}.
     * @param nodeManager node manager on the other side of the relation
     * @param role role of a relation
     * @param searchDir the direction of the relation
     * @return new Relation step
     *
     * @throws IllegalArgumentException when an invalid argument is supplied.
     * @throws IllegalStateException when there is no previous step.
     */
    RelationStep addRelationStep(NodeManager nodeManager, String role, String searchDir);

    /**
     * If you need to add a 'related' NodeManager without specifying a role/searchDir
     * simply use these addRelationStep.
     * @param otherManager node manager on the other side of the relation
     * @return new Relation step
     */
    RelationStep addRelationStep(NodeManager otherManager);

    /**
     * Adds a field to a step.
     * @param step step to add field to
     * @param field field to add
     * @return new StepField
     */
    StepField addField(Step step, Field field);

    /**
     * Adds a field by string
     * @param field field to add
     * @return new StepField
     */
    StepField addField(String field);

    /**
     * Removes all fields from the Query object.
     */
    void removeFields();

    /**
     * Creates a StepField object withouth adding it (needed for aggregated queries).
     * @param step step to create StepField from
     * @param field field to create StepField from
     * @return new StepField
     */
    StepField createStepField(Step step, Field field);

    /**
     * Creates a StepField object withouth adding it (needed for aggregated queries).
     * @param step step to create StepField from
     * @param fieldName name of field to create StepField from
     * @return new StepField
     */
    StepField createStepField(Step step, String fieldName);

    /**
     * Creates the step field for the given name. For a NodeQuery the arguments is simply the name of the
     * field. For a 'normal' query, it should be prefixed by the (automatic) alias of the Step.
     * @param fieldIdentifer field identifier to create StepField from
     * @return new StepField
     */
    StepField createStepField(String fieldIdentifer);

    /**
     * Add an aggregated field to a step
     * @param step step to add field to
     * @param field field to add
     * @param aggregationType Type of aggregation
     * @return new AggregatedField
     */
    AggregatedField addAggregatedField(Step step, Field field, int aggregationType);

    /**
     * Specifies wether the query result must contain only 'distinct' results.
     * @param distinct 'distinct' results
     * @return Query
     * @see org.mmbase.storage.search.implementation.BasicSearchQuery#setDistinct
     * @see #isDistinct
     */
    Query setDistinct(boolean distinct);


    /**
     * Limits the query-result to maxNumber records.
     * @param maxNumber max number of results
     * @return Query
     * @see org.mmbase.storage.search.implementation.BasicSearchQuery#setMaxNumber
     */
    Query setMaxNumber(int maxNumber);

    /**
     * Offsets the query-result with offset records.
     * @param offset offset in results
     * @return Query
     * @see org.mmbase.storage.search.implementation.BasicSearchQuery#setOffset
     */
    Query setOffset(int offset);

    /**
     * Gets the 'clean' constraint on this query. I.e. the constraint which were automaticly added
     * because of security are stripped away, and it is garanteed that you get back what you put in.
     *
     * It is adviced that you use this in stead of SearchQuery#getConstraint, because that function
     * is used by the Query handlers, which <em>do</em> need the security constraints. But otherwise
     * you don't want to see those.
     *
     * @return Constraint
     * @since MMBase-1.7.1
     */
    Constraint getCleanConstraint();

    // Constraints and so on..


    /**
     * Create a constraint (for use with this Query object). The argument is a string, as also can be
     * used as an argument of the 'non-query' getList. This should be considered legacy.
     * @param s String with LegacyConstraint
     * @return LegacyConstraint
     * @see  Cloud#getList(String startNodes, String nodePath, String fields, String constraints, String orderby, String directions, String searchDir, boolean distinct)
     * @see  NodeManager#getList(String constraints, String orderby, String directions)
     */
    LegacyConstraint createConstraint(String s);

    /**
     * Create a constraint (for use with this Query object). The given field must be 'null'.
     * @param f Stepfield
     * @return FieldNullConstraint
     */
    FieldNullConstraint createConstraint(StepField f);

    /**
     * Create a constraint (for use with this Query object). The given field must equal the given
     * value 'v'.
     * @param f field
     * @param v value
     * @return FieldValueConstraint
     */
    FieldValueConstraint createConstraint(StepField f, Object v);

    /**
     * Create a constraint (for use with this Query object). The given field and the given
     * value 'v', combined with given operator must evaluate to true.
     * @param f field
     * @param op operator
     * @param v value
     * @return FieldValueConstraint
     */
    FieldValueConstraint createConstraint(StepField f, int op, Object v);

    /**
     * Create a constraint (for use with this Query object). The given date field and the given
     * value 'v', combined with given operator must evaluate to true for the specified date part.
     * @param f field
     * @param op operator
     * @param v value
     * @param part part of the date value
     * @return FieldValueConstraint
     */
    FieldValueConstraint createConstraint(StepField f, int op, Object v, int part);

    /**
     * Create a constraint (for use with this Query object). The two given fields , combined with
     * given operator must evaluate to true.
     * @param f field
     * @param op operator
     * @param v value
     * @return CompareFieldsConstraint
     */
    CompareFieldsConstraint createConstraint(StepField f, int op, StepField  v);

    /**
     * Create a constraint (for use with this Query object). The given field must lie between the
     * two given values.
     * @param f field
     * @param o1 value one
     * @param o2 value two
     * @return FieldValueBetweenConstraint
     */
    FieldValueBetweenConstraint createConstraint(StepField f, Object o1, Object o2);

    /**
     * Create a constraint (for use with this Query object). The given field value must be contained
     * by the given set of values. If the given set is empty, a FieldValueInConstraint will be
     * constructed for the number field in stead ('number IN (-1)'), which ensures that also in that
     * case the logical thing will happen. ('<field> IN ()' fails in most databases).
     * @param f field
     * @param v value
     * @return the new Constraint.
     */
    FieldValueInConstraint createConstraint(StepField f, SortedSet<? extends Object> v);

    FieldValueInQueryConstraint createConstraint(StepField f, Query q);

    /**
     * Changes the given constraint's 'case sensitivity' (if applicable). Default it is true.
     * @param constraint constraint to change
     * @param sensitive case sensitivity
     * @return modified FieldConstraint
     */
    FieldConstraint setCaseSensitive(FieldConstraint constraint, boolean sensitive);

    /**
     * Changes the given constraint's 'inverse' (if applicable). Default it is (of course) false.
     * @param c constraint
     * @param i inverse
     * @return Inversed constraint
     */
    Constraint setInverse(Constraint c, boolean i);

   /**
     * Combines two Constraints to one new one, using a boolean operator (AND or OR). Every new
     * constraint must be combined with the ones you already have with such a new CompositeConstraint.
     *
     * If the first constraint is a composite constraint (with the same logical operator), then the
     * second one will simply be added.
     * @param c1 constraint one
     * @param op operator ({@link CompositeConstraint#LOGICAL_AND}, {@link CompositeConstraint#LOGICAL_OR})
     * @param c2 constraint two
     * @return a Composite constraint (might not be a new one)
     */
    CompositeConstraint createConstraint(Constraint c1, int op, Constraint c2);

    /**
     * The (composite) constraint can actually be set into the query with this method.
     * @param c constraint
     */
    void setConstraint(Constraint c);


    /**
     * Adds an order on a certain field.
     * @param f field
     * @param direction {@link SortOrder#ORDER_ASCENDING} or {@link SortOrder#ORDER_DESCENDING}
     * @param caseSensitive case sensitivity
     * @param part part to sort on for a date value
     * @return new SortOrder
     * @see org.mmbase.storage.search.implementation.BasicSearchQuery#addSortOrder
     * @since MMBase-1.9
     */
    SortOrder addSortOrder(StepField f, int direction, boolean caseSensitive, int part);

    /**
     * Defaulting version of {@link #addSortOrder(StepField, int, boolean, int)} (no date parts)
     * @param f field
     * @param direction {@link SortOrder#ORDER_ASCENDING} or {@link SortOrder#ORDER_DESCENDING}
     * @param caseSensitive case sensitivity
     * @return new SortOrder
     * @since MMBase-1.8
     */
    SortOrder addSortOrder(StepField f, int direction, boolean caseSensitive);

    /**
     * Defaulting version of {@link #addSortOrder(StepField, int, boolean, int)} (sorting case
     * insensitively, and no date parts).
     * @param f field
     * @param direction  {@link SortOrder#ORDER_ASCENDING} or {@link SortOrder#ORDER_DESCENDING}
     * @return new SortOrder
     */
    SortOrder addSortOrder(StepField f, int direction);

    /**
     * Adds a node to a step.
     * @param s step
     * @param node node to add
     */
    void addNode(Step s, Node node);

    /**
     * @param s query step
     * @param number node number
     * @since MMBase-1.8
     */
    void addNode(Step s, int number);

    /**
     * Whether this query was used or not. If is was used, then you cannot modify it anymore (would
     * kill caches, and references to 'original query' would get invalid)
     * @return query already used or not
     */
    boolean isUsed();

    /**
     * Mark this query 'used'. It has to be copied first, if you want to add things to it.
     * @return if this query is was used before this method call
     */
    boolean markUsed();

    /**
     * Create an (unused) clone
     * @return Cloned Query
     */
    Query clone();

    /**
     * Clones this object, only without the fields
     * @return Cloned Query
     */
    Query cloneWithoutFields();

    /**
     * Creates an unused aggregate clone of this query. If this query is not itself aggregated, all
     * fields are removed (but the constraints on them remain), and you can add aggregated fields
     * then.
     * @return Cloned Query
     */
    Query aggregatingClone();

    /**
     * Executes the query and returns the resulting node list.
     * @return resulting node list
     * @since MMBase-1.8
     */
    NodeList getList();

    /**
     * Shows the query in a human-readable SQL form. This is probably not the query which will
     * actually be sent to the database. This method is provided because 'toString' on a Query object
     * is pretty complete, but pretty undigestable for mere mortals too. Implementions can also
     * implement getSql(), which would make this available in e.g. EL too.
     *
     * @return human-readable SQL
     * @since MMBase-1.8
     */
    String toSql();


    void removeImplicitFields();

}
