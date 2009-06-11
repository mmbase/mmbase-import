/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.util;

import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;
import java.util.*;

import org.mmbase.util.logging.*;

/**
 * Abstract base class for both {@link QueryWrapper} and {@link NodeQueryWrapper}.
 *
 * @author  Michiel Meeuwissen
 * @version $Id$
 * @since   MMBase-1.9.2
 */

public abstract class AbstractQueryWrapper<Q extends Query> implements Query {

    private static final Logger log = Logging.getLoggerInstance(AbstractQueryWrapper.class);

    protected Q query;
    protected AbstractQueryWrapper(Q q) {
        this.query = q;
    }

    @SuppressWarnings("unchecked")
    public void cloneQuery() {
        query = (Q) query.clone();
    }

    public String getSql() {
        return query.toSql();
    }
    public Q getQuery() {
        return query;
    }

    // Query

    public Cloud getCloud() { return query.getCloud(); }

    public Step addStep(NodeManager nodeManager) { return query.addStep(nodeManager); }
    public void setAlias(Step step, String alias) {  query.setAlias(step, alias); }
    public Step getStep(String stepAlias) { return query.getStep(stepAlias); }
    public RelationStep addRelationStep(NodeManager nodeManager, String role, String searchDir) {
        return query.addRelationStep(nodeManager, role, searchDir);
    }

    public RelationStep addRelationStep(NodeManager otherManager) {
        return query.addRelationStep(otherManager);
    }
    public StepField addField(Step step, Field field) {
        return query.addField(step, field);
    }

    public  StepField addField(String field) {
        return query.addField(field);
    }
    public void removeFields() {
        query.removeFields();
    }
    public StepField createStepField(Step step, Field field) {
        return query.createStepField(step, field);
    }
    public StepField createStepField(Step step, String fieldName) {
        return query.createStepField(step, fieldName);
    }

    public StepField createStepField(String fieldIdentifier) {
        return query.createStepField(fieldIdentifier);
    }

    public AggregatedField addAggregatedField(Step step, Field field, int aggregationType) {
        return query.addAggregatedField(step, field, aggregationType);
    }

    public Query setDistinct(boolean distinct) {
        query.setDistinct(distinct);
        return this;
    }

    public Query setMaxNumber(int maxNumber) {
        query.setMaxNumber(maxNumber);
        return this;
    }

    public Query setOffset(int offset) {
        query.setOffset(offset);
        return this;
    }
    public Constraint getCleanConstraint() {
        return query.getCleanConstraint();
    }

    public LegacyConstraint createConstraint(String s) {
        return query.createConstraint(s);
    }

    public FieldNullConstraint createConstraint(StepField f) {
        return query.createConstraint(f);
    }


    public FieldValueConstraint createConstraint(StepField f, Object v) {
        return query.createConstraint(f, v);
    }


    public FieldValueConstraint createConstraint(StepField f, int op, Object v) {
        return query.createConstraint(f, op, v);
    }
    public FieldValueConstraint createConstraint(StepField f, int op, Object v, int part) {
        return query.createConstraint(f, op, v, part);
    }
    public CompareFieldsConstraint createConstraint(StepField f, int op, StepField  v) {
        return query.createConstraint(f, op, v);
    }

    public FieldValueBetweenConstraint createConstraint(StepField f, Object o1, Object o2) {
        return query.createConstraint(f, o1, o2);
    }

    public FieldValueInConstraint createConstraint(StepField f, SortedSet<? extends Object> v) {
        return query.createConstraint(f, v);
    }

    public FieldValueInQueryConstraint createConstraint(StepField f, Query q) {
        return query.createConstraint(f, q);
    }

    public FieldConstraint setCaseSensitive(FieldConstraint constraint, boolean sensitive) {
        return query.setCaseSensitive(constraint, sensitive);
    }

    public Constraint setInverse(Constraint c, boolean i) {
        return query.setInverse(c, i);
    }

    public CompositeConstraint createConstraint(Constraint c1, int op, Constraint c2) {
        return query.createConstraint(c1, op, c2);
    }
    public void setConstraint(Constraint c) {
        query.setConstraint(c);
    }

    public SortOrder addSortOrder(StepField f, int direction, boolean caseSensitive, int part) {
        return query.addSortOrder(f, direction, caseSensitive, part);
    }

    public SortOrder addSortOrder(StepField f, int direction, boolean caseSensitive) {
        return query.addSortOrder(f, direction, caseSensitive);
    }

    public SortOrder addSortOrder(StepField f, int direction) {
        return query.addSortOrder(f, direction);
    }

    public void addNode(Step s, Node node) {
        query.addNode(s, node);
    }
    public void addNode(Step s, int number) {
        query.addNode(s, number);
    }

    public boolean isUsed() {
        return query.isUsed();
    }

    public boolean markUsed() {
        return query.markUsed();
    }
    @Override
    @SuppressWarnings("unchecked")
    public Query clone() {
        try {
            AbstractQueryWrapper clone = (AbstractQueryWrapper) super.clone();
            clone.query = query.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) {
            // java sucks
            return null;
        }
    }

    public Query cloneWithoutFields() {
        return query.cloneWithoutFields();
    }

    public Query aggregatingClone() {
        return query.aggregatingClone();
    }

    public NodeList getList() {
        return query.getList();
    }

    public String toSql() {
        return query.toSql();
    }


    public void removeImplicitFields() {
        query.removeImplicitFields();
    }


    // SearchQuery

    public boolean isDistinct() {
        return query.isDistinct();
    }
    public boolean isAggregating() {
        return query.isAggregating();
    }
    public List<Step> getSteps() {
        return query.getSteps();
    }
    public List<StepField> getFields() {
        return query.getFields();
    }
    public Constraint getConstraint() {
        return query.getConstraint();
    }
    public int getMaxNumber() {
        return query.getMaxNumber();
    }
    public int getOffset() {
        return query.getOffset();
    }
    public List<SortOrder> getSortOrders() {
        return query.getSortOrders();
    }
    @Override
    public boolean equals(Object obj) {
        return query.equals(obj);
    }
    @Override
    public int hashCode() {
        return query.hashCode();
    }
    @Override
    public String toString() {
        return query.toString();
    }


    // Cacheable
    public org.mmbase.cache.CachePolicy getCachePolicy() {
        return query.getCachePolicy();
    }
    public void setCachePolicy(org.mmbase.cache.CachePolicy policy) {
        query.setCachePolicy(policy);
    }

}
