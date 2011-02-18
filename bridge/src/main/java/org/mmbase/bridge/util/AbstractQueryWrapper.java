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

    @Override
    public SearchQuery unwrap() {
        return query.unwrap();
    }

    // Query

    @Override
    public Cloud getCloud() { return query.getCloud(); }

    @Override
    public Step addStep(NodeManager nodeManager) { return query.addStep(nodeManager); }
    @Override
    public void setAlias(Step step, String alias) {  query.setAlias(step, alias); }
    @Override
    public Step getStep(String stepAlias) { return query.getStep(stepAlias); }
    @Override
    public RelationStep addRelationStep(NodeManager nodeManager, String role, String searchDir) {
        return query.addRelationStep(nodeManager, role, searchDir);
    }

    @Override
    public RelationStep addRelationStep(NodeManager otherManager) {
        return query.addRelationStep(otherManager);
    }
    @Override
    public StepField addField(Step step, Field field) {
        return query.addField(step, field);
    }

    @Override
    public  StepField addField(String field) {
        return query.addField(field);
    }
    @Override
    public void removeFields() {
        query.removeFields();
    }
    @Override
    public StepField createStepField(Step step, Field field) {
        return query.createStepField(step, field);
    }
    @Override
    public StepField createStepField(Step step, String fieldName) {
        return query.createStepField(step, fieldName);
    }

    @Override
    public StepField createStepField(String fieldIdentifier) {
        return query.createStepField(fieldIdentifier);
    }

    @Override
    public AggregatedField addAggregatedField(Step step, Field field, int aggregationType) {
        return query.addAggregatedField(step, field, aggregationType);
    }

    @Override
    public Query setDistinct(boolean distinct) {
        query.setDistinct(distinct);
        return this;
    }

    @Override
    public Query setMaxNumber(int maxNumber) {
        query.setMaxNumber(maxNumber);
        return this;
    }

    @Override
    public Query setOffset(int offset) {
        query.setOffset(offset);
        return this;
    }
    @Override
    public Constraint getCleanConstraint() {
        return query.getCleanConstraint();
    }

    @Override
    public LegacyConstraint createConstraint(String s) {
        return query.createConstraint(s);
    }

    @Override
    public FieldNullConstraint createConstraint(StepField f) {
        return query.createConstraint(f);
    }


    @Override
    public FieldValueConstraint createConstraint(StepField f, Object v) {
        return query.createConstraint(f, v);
    }


    @Override
    public FieldValueConstraint createConstraint(StepField f, int op, Object v) {
        return query.createConstraint(f, op, v);
    }
    @Override
    public FieldValueConstraint createConstraint(StepField f, int op, Object v, int part) {
        return query.createConstraint(f, op, v, part);
    }
    @Override
    public CompareFieldsConstraint createConstraint(StepField f, int op, StepField  v) {
        return query.createConstraint(f, op, v);
    }

    @Override
    public FieldValueBetweenConstraint createConstraint(StepField f, Object o1, Object o2) {
        return query.createConstraint(f, o1, o2);
    }

    @Override
    public FieldValueInConstraint createConstraint(StepField f, SortedSet<?> v) {
        return query.createConstraint(f, v);
    }

    @Override
    public FieldValueInQueryConstraint createConstraint(StepField f, Query q) {
        return query.createConstraint(f, q);
    }

    @Override
    public FieldConstraint setCaseSensitive(FieldConstraint constraint, boolean sensitive) {
        return query.setCaseSensitive(constraint, sensitive);
    }

    @Override
    public Constraint setInverse(Constraint c, boolean i) {
        return query.setInverse(c, i);
    }

    @Override
    public CompositeConstraint createConstraint(Constraint c1, int op, Constraint c2) {
        return query.createConstraint(c1, op, c2);
    }
    @Override
    public void setConstraint(Constraint c) {
        query.setConstraint(c);
    }

    @Override
    public SortOrder addSortOrder(StepField f, int direction, boolean caseSensitive, int part) {
        return query.addSortOrder(f, direction, caseSensitive, part);
    }

    @Override
    public SortOrder addSortOrder(StepField f, int direction, boolean caseSensitive) {
        return query.addSortOrder(f, direction, caseSensitive);
    }

    @Override
    public SortOrder addSortOrder(StepField f, int direction) {
        return query.addSortOrder(f, direction);
    }

    @Override
    public void addNode(Step s, Node node) {
        query.addNode(s, node);
    }
    @Override
    public void addNode(Step s, int number) {
        query.addNode(s, number);
    }

    @Override
    public boolean isUsed() {
        return query.isUsed();
    }

    @Override
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

    @Override
    public Query cloneWithoutFields() {
        return query.cloneWithoutFields();
    }

    @Override
    public Query aggregatingClone() {
        return query.aggregatingClone();
    }

    @Override
    public NodeList getList() {
        return query.getList();
    }

    @Override
    public String toSql() {
        return query.toSql();
    }


    @Override
    public void removeImplicitFields() {
        query.removeImplicitFields();
    }


    // SearchQuery

    @Override
    public boolean isDistinct() {
        return query.isDistinct();
    }
    @Override
    public boolean isAggregating() {
        return query.isAggregating();
    }
    @Override
    public List<Step> getSteps() {
        return query.getSteps();
    }
    @Override
    public List<StepField> getFields() {
        return query.getFields();
    }
    @Override
    public Constraint getConstraint() {
        return query.getConstraint();
    }
    @Override
    public int getMaxNumber() {
        return query.getMaxNumber();
    }
    @Override
    public int getOffset() {
        return query.getOffset();
    }
    @Override
    public List<SortOrder> getSortOrders() {
        return query.getSortOrders();
    }
    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
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
    @Override
    public org.mmbase.cache.CachePolicy getCachePolicy() {
        return query.getCachePolicy();
    }
    @Override
    public void setCachePolicy(org.mmbase.cache.CachePolicy policy) {
        query.setCachePolicy(policy);
    }

}
