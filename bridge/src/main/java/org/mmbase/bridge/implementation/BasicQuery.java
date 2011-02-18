/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;

import java.util.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.cache.CachePolicy;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;
import org.mmbase.security.Authorization;

/**
 * 'Basic' implementation of bridge Query. Wraps a 'BasicSearchQuery' from core.
 *
 * This  implementation is actually ussuable with other implementations of bridge too, because it has the public constructor:
 * {@link #BasicQuery(Cloud, BasicSearchQuery)}.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.7
 * @see org.mmbase.storage.search.implementation.BasicSearchQuery
 */
public class BasicQuery implements Query, java.io.Serializable  {
    private static final long serialVersionUID = 1L;


    private static final Logger log = Logging.getLoggerInstance(BasicQuery.class);

    /**
     * Whether this Query was used already. If it is used, it may not be changed any more.
     */
    protected boolean used = false;

    /**
     * Whether this Query is aggregating.
     * @todo this member is in BasicSearchQuery too (but private).
     */
    protected boolean aggregating = false; // ugly ugly, this member is in BasicSearchQuery too (but private).

    /**
     * The QueryCheck object associated with this Query, or null if no such object was determined yet.
     */
    protected Authorization.QueryCheck queryCheck = null;

    /**
     * If a the constraint was made 'secure', in insecureConstraint the original Constraint is
     * stored. This object is null if either the queryCheck object is not yet determined, or the
     * orignal query did not have constraints.
     */
    protected Constraint insecureConstraint = null;


    private   HashMap<String, Integer>  aliasSequences = new HashMap<String, Integer>(); // must be HashMap because cloneable
    // to make unique table aliases. This is similar impl. as  in core. Why should it be at all....


    /**
     * The core query which is 'wrapped'
     */
    protected BasicSearchQuery query;

    /**
     * reference to the cloud.
     */
    protected Cloud cloud;


    /**
     * The implicitely added 'extra' fields. These are removed if the query becomes 'distinct'. So,
     * you can e.g. not do element= on a distinct query result.
     */
    protected List<BasicStepField> implicitFields = new ArrayList<BasicStepField>();

    /**
     * The explicitely added 'extra' fields. Because you explicitely added those, they will not be removed if the query becomes 'distinct'.
     */
    protected List<BasicStepField> explicitFields = new ArrayList<BasicStepField>();

    public BasicQuery(Cloud c) {
        query = new BasicSearchQuery();
        cloud = c;
    }

    public BasicQuery(Cloud c, boolean aggregating) {
        query = new BasicSearchQuery(aggregating);
        this.aggregating = aggregating;
        cloud = c;
    }

    public BasicQuery(Cloud c, BasicSearchQuery q) { // public for org.mmbase.bridge.util
        query = q;
        cloud = c;
        this.aggregating = q.isAggregating();
    }


    public BasicSearchQuery getQuery() {
        return query;
    }
    @Override
    public SearchQuery unwrap() {
        return query;
    }

    protected void createNewQuery() {
        query = new BasicSearchQuery();
    }


    // SearchQuery impl:

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

    // bridge.Query impl
    @Override
    public Constraint getCleanConstraint() {
        if (queryCheck != null) {
            return insecureConstraint;
        } else {
            return query.getConstraint();
        }
    }

    // more SearchQuery impl
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
    public boolean isDistinct() {
        return query.isDistinct();
    }

    // bridge.Query impl.:

    @Override
    public boolean isAggregating() {
        return aggregating;
    }

    @Override
    public CachePolicy getCachePolicy() {
        return query.getCachePolicy();
    }

    @Override
    public void setCachePolicy(CachePolicy policy) {
        query.setCachePolicy(policy);
    }

    /**
     * @since MMBase-1.7.1
     */
    protected void removeSecurityConstraintFromClone(BasicSearchQuery clone) {
        if (log.isDebugEnabled()) {
            log.debug("Removing " + queryCheck + " FROM " + clone);
        }
        if (queryCheck != null) {
            Constraint secureConstraint = queryCheck.getConstraint();
            if (secureConstraint != null) {
                Constraint constraint = clone.getConstraint();
                // remove it from clone (by modifying the 'cloned' constraint)
                if (secureConstraint.equals(constraint)) {
                    clone.setConstraint(null);
                } else { // must be part of the composite constraint
                    BasicCompositeConstraint compConstraint = (BasicCompositeConstraint) constraint;
                    compConstraint.removeChild(secureConstraint); // remove it
                    if (compConstraint.getChilds().size() == 0) { // no need to let it  then
                        clone.setConstraint(null);
                    }
                    if (compConstraint.getChilds().size() == 1) { // no need to let it composite then
                        Constraint newConstraint = compConstraint.getChilds().get(0);
                        clone.setConstraint(newConstraint);
                    }
                }
            }
        }

    }

    @Override@SuppressWarnings("unchecked")
    public BasicQuery clone() { // also works for descendants (NodeQuery)
        try {
            BasicQuery clone = (BasicQuery) super.clone();
            clone.query = query.clone();
            clone.aliasSequences = (HashMap<String, Integer>) aliasSequences.clone();
            removeSecurityConstraintFromClone(clone.query);
            clone.insecureConstraint = null;
            clone.queryCheck = null;
            clone.used = false;
            return clone;
        } catch (CloneNotSupportedException e) {
            // cannot happen
            throw new InternalError(e.toString());
        }
    }
    @Override
    public Query aggregatingClone() {
        BasicSearchQuery bsq = new BasicSearchQuery(query, BasicSearchQuery.COPY_AGGREGATING);
        removeSecurityConstraintFromClone(bsq);
        BasicQuery clone     = new BasicQuery(cloud, bsq);
        clone.used = false;
        clone.aggregating = true;
        return clone;
    }

    @Override
    public Query cloneWithoutFields() {
        BasicSearchQuery bsq = new BasicSearchQuery(query, BasicSearchQuery.COPY_WITHOUTFIELDS);
        removeSecurityConstraintFromClone(bsq);
        BasicQuery clone     = new BasicQuery(cloud, bsq);
        clone.used = false;
        clone.aggregating = false;
        return clone;
    }

    /**
     * Creates a unique alias for this Query based on a given base String
     */
    protected String createAlias(String  name) {
        if (used) throw new BridgeException("Query was used already");
        Integer seq = aliasSequences.get(name);
        if (seq == null) {
            seq = 0;
        } else {
            seq = seq.intValue() + 1;
        }
        aliasSequences.put(name, seq);
        return glueAlias(name, seq);
    }

    /**
     * Glues a string and integer together to a new string.
     */
    protected String glueAlias(String aliasBase, Integer seq) {
        if (seq == null) return aliasBase;
        int s = seq.intValue();
        if (s == 0) {
            return aliasBase;
        } else {
            return aliasBase + s;
        }
    }


    @Override
    public Step addStep(NodeManager nm) {
        if (used) throw new BridgeException("Query was used already");

        removeSecurityConstraint(); // if present
        NodeManager builder = getCloud().getNodeManager(nm.getName());
        if (builder == null) throw new BridgeException("No builder with name " + nm.getName() + " (perhaps " + nm + " is virtual?)");
        BasicStep step = query.addStep(builder.getName());
        setAlias(step, ""); // "": generate alias
        if (! aggregating) {
            addFieldImplicit(step, nm.getField("number"));
        }

        return step;
    }

    @Override
    public void setAlias(Step step, String alias) {
        String currentAlias = step.getAlias();
        String aliasBase    = step.getTableName();

        // check if it was the lastely 'automaticly' create alias, in which case we free the sequence number again
        // (also to fix #6547)

        Integer currentSeq  = aliasSequences.get(aliasBase);
        if (currentSeq != null && glueAlias(aliasBase, currentSeq).equals(currentAlias)) {
            if (currentSeq.intValue() == 0) {
                aliasSequences.put(aliasBase, null);
            } else {
                aliasSequences.put(aliasBase, currentSeq.intValue() - 1);
            }
        }
        if ("".equals(alias)) {
            alias = createAlias(aliasBase);
        }

        BasicStep basicStep = (BasicStep) step;
        basicStep.setAlias(alias);
    }

    protected BasicRelationStep addRelationStep(String insrel, NodeManager otherNodeManager, int direction) {
        BasicRelationStep relationStep = query.addRelationStep(insrel, otherNodeManager.getName());
        relationStep.setDirectionality(direction);
        relationStep.setAlias(createAlias(relationStep.getTableName()));
        NodeManager relationManager = otherNodeManager.getCloud().getNodeManager(relationStep.getTableName());
        BasicStep next = (BasicStep) relationStep.getNext();
        next.setAlias(createAlias(next.getTableName()));
        if (! aggregating) {
            // the number fields must always be queried, otherwise the original node cannot be found back (e.g. <mm:node element=)
            addFieldImplicit(relationStep, relationManager.getField("number"));  // query relation node
            addFieldImplicit(next,         otherNodeManager.getField("number"));  // and next node
            // distinct?
        }
        return relationStep;
    }
    @Override
    public RelationStep addRelationStep(NodeManager otherNodeManager) {
        return addRelationStep(otherNodeManager, null, "BOTH");
    }


    @Override
    public RelationStep addRelationStep(NodeManager otherNodeManager, String role, String direction) {
        if ("".equals(role)) role = null;
        return addRelationStep(otherNodeManager, role, direction, true);
    }


    protected RelationStep addRelationStep(NodeManager otherNodeManager, String role, String direction, boolean warnOnImpossibleStep) {
        if (used) throw new BridgeException("Query was used already");

        // a bit silly that two lookups are needed
        int relationDir = Queries.getRelationStepDirection(direction);

        //if (BasicCloudContext.mmb == null) throw new IllegalStateException("MMBase is not started!");
        if (role == null) {
            BasicRelationStep step = addRelationStep("insrel", otherNodeManager, relationDir);
            /* TODO
            TypeRel typeRel = BasicCloudContext.mmb.getTypeRel();
            if (!typeRel.optimizeRelationStep(step, cloud.getNodeManager(step.getPrevious().getTableName()).getNumber(), otherNodeManager.getNumber(), -1, relationDir)) {
                if (relationDir != RelationStep.DIRECTIONS_SOURCE &&
                    relationDir != RelationStep.DIRECTIONS_DESTINATION &&
                    warnOnImpossibleStep) {
                    log.warn("Added an impossible relation step (" + step + " to " + otherNodeManager + ") to the query. The query-result will always be empty now (so you could as well not execute it).");
                    log.warn(Logging.applicationStacktrace());
                }
            }
            */
            return step;
        } else {
            RelationManager rm = cloud.getRelationManager(role);
            int r = rm.getNumber();
            if (r == -1) {
                throw new NotFoundException("Role '" + role + "' does not exist.");
            }
            BasicRelationStep step =  addRelationStep(rm.getName(), otherNodeManager, relationDir);
            step.setRole(r);
            if (! cloud.hasNodeManager(role)) {
                step.setAlias(createAlias(role));
            }
            /* TODO
              if (! typeRel.optimizeRelationStep(step, cloud.getNodeManager(step.getPrevious().getTableName()).getNumber(), otherNodeManager.getNumber(), r, relationDir)) {
              if (relationDir != RelationStep.DIRECTIONS_SOURCE &&
                    relationDir != RelationStep.DIRECTIONS_DESTINATION &&
                    warnOnImpossibleStep) {
                    // not fully specified, and nothing found, warn about that.
                    log.warn("Added an impossible relation step (" + step + " to " + otherNodeManager + ") to the query. The query-result will always be empty now (so you could as well not execute it). ");
                    log.warn(Logging.applicationStacktrace());
                }
            }
            */
            return step;
        }
    }

    @Override
    public void removeFields() {
        query.removeFields();
        explicitFields.clear();
        for (BasicStepField sf : implicitFields) {
            Step addedStep = sf.getStep();
            query.addField(addedStep, sf.getField());
        }

    }


    @Override
    public StepField addField(Step step, Field field) {
        if (used) throw new BridgeException("Query was used already");
        BasicStepField sf = new BasicStepField(step, field);
        if (! implicitFields.remove(sf)) {// it's explicitly added now
            if (! field.isVirtual()) {
                sf = query.addField(step, field);
            } else {
                log.debug("Not adding the field " + field + " because it is not in storage (this is a virtual field)");
            }
        }
        explicitFields.add(sf);
        return sf;
    }
    @Override
    public StepField addField(String fieldIdentifier) {
        // code copied from createStepField, should be centralized
        if (used) throw new BridgeException("Query was used already");
        int dot = fieldIdentifier.indexOf('.');
        if (dot <= 0) throw new BridgeException("No step alias found in field identifier '" + fieldIdentifier + "'. Expected a dot in it.");
        String stepAlias = fieldIdentifier.substring(0, dot);
        String fieldName = fieldIdentifier.substring(dot + 1);
        Step step = getStep(stepAlias);
        if (step == null) throw new  NotFoundException("No step with alias '" + stepAlias + "' found in " + getSteps());
        NodeManager nm = cloud.getNodeManager(step.getTableName());
        Field field = nm.getField(fieldName);
        return addField(step, field);
    }

    /**
     * Fields which are added 'implicity' should be added by this function.
     */
    protected void addFieldImplicit(Step step, Field field) {
        if (used) throw new BridgeException("Query was used already");
        if (! query.isDistinct()) {
            BasicStepField sf = query.addFieldUnlessPresent(step, field);
            if (! implicitFields.contains(sf)) {
                implicitFields.add(sf);
            }
        }
    }

    @Override
    public StepField createStepField(Step step, Field field) {
        if (field == null) throw new BridgeException("Field is null");
        return new BasicStepField(step, field);
    }

    @Override
    public StepField createStepField(Step step, String fieldName) {
        return createStepField(step, cloud.getNodeManager(step.getTableName()).getField(fieldName));
    }



    @Override
    public Step getStep(String stepAlias) {
        return Queries.searchStep(getSteps(), stepAlias);
    }

    @Override
    public StepField createStepField(String fieldIdentifier) {
        // code copied from addField, should be centralized
        int dot = fieldIdentifier.indexOf('.');
        if (dot <= 0) throw new BridgeException("No step alias found in field identifier '" + fieldIdentifier + "'. Expected a dot in it.");
        String stepAlias = fieldIdentifier.substring(0, dot);
        String fieldName = fieldIdentifier.substring(dot + 1);
        Step step = getStep(stepAlias);
        if (step == null) {
            throw new  NotFoundException("No step with alias '" + stepAlias + "' found in " + getSteps());
        }
        NodeManager nm = cloud.getNodeManager(step.getTableName());
        Field field = nm.getField(fieldName);
        return createStepField(step, field);
    }

    @Override
    public AggregatedField addAggregatedField(Step step, Field field, int aggregationType) {
        if (used) throw new BridgeException("Query was used already");
        BasicAggregatedField aggregatedField =  query.addAggregatedField(step, field, aggregationType);
        // aggregatedField.setAlias(field.getName());

        if (this instanceof NodeQuery) { // UGLY!
            NodeQuery nodeQuery = (NodeQuery) this;
            ((BasicStep) step).setAlias(nodeQuery.getNodeManager().getName());
            // Step needs alias, because otherwise clusterbuilder chokes.
            // And node-manager.getList is illogical, because a aggregated result is certainly not a 'real' node.
        }

        // TODO, think of something better. --> a good way to present aggregated results.

        return aggregatedField;
    }


    /**
     * @since MMBase-1.9.1
     */
    @Override
    public void removeImplicitFields() {
       query.removeFields();
       implicitFields.clear();
       for (BasicStepField sf : explicitFields) {
           query.addField(sf.getStep(), sf.getField());
       }
    }

    @Override
    public Query setDistinct(boolean distinct) {
        if (used) throw new BridgeException("Query was used already");
        query.setDistinct(distinct);
        if (distinct) { // in that case, make sure only the 'explicitely' added fields remain.
            removeImplicitFields();
        }
        return this;
    }

    @Override
    public Query setMaxNumber(int maxNumber) {
        if (used) throw new BridgeException("Query was used already");
        query.setMaxNumber(maxNumber);
        return this;
    }
    @Override
    public Query setOffset(int offset) {
        if (used) throw new BridgeException("Query was used already");
        query.setOffset(offset);
        return this;

    }

    @Override
    public LegacyConstraint      createConstraint(String s) {
        return new BasicLegacyConstraint(s);
    }

    @Override
    public FieldNullConstraint createConstraint(StepField f) {
        return new BasicFieldNullConstraint(f);
    }

    @Override
    public FieldValueConstraint createConstraint(StepField f, Object v) {
        return createConstraint(f, FieldCompareConstraint.EQUAL, v);
    }

    @Override
    public FieldValueConstraint createConstraint(StepField f, int op, Object v, int part) {
        BasicFieldValueConstraint c = new BasicFieldValueDateConstraint(f, v, part);
        c.setOperator(op);
        return c;
    }

    @Override
    public FieldValueConstraint createConstraint(StepField f, int op, Object v) {
        if (v instanceof Node) v = ((Node) v).getNumber();
        BasicFieldValueConstraint c = new BasicFieldValueConstraint(f, v);
        c.setOperator(op);
        return c;
    }

    @Override
    public CompareFieldsConstraint createConstraint(StepField f, int op, StepField  v) {
        BasicCompareFieldsConstraint c = new BasicCompareFieldsConstraint(f, v);
        c.setOperator(op);
        return c;
    }

    @Override
    public FieldValueBetweenConstraint createConstraint(StepField f, Object o1, Object o2) {
        return new BasicFieldValueBetweenConstraint(f, o1, o2);
    }

    @Override
    public FieldValueInConstraint createConstraint(StepField f, SortedSet<?> v) {
        if (v.size() == 0) { // make sure the query becomes empty!
            Step step = f.getStep();
            StepField nf = createStepField(step, "number");
            BasicFieldValueInConstraint c = new BasicFieldValueInConstraint(nf);
            c.addValue(-1);
            return c;
        } else {
            BasicFieldValueInConstraint c = new BasicFieldValueInConstraint(f);
            for (Object o : v) {
                c.addValue(o);
            }
            return c;
        }
    }

    @Override
    public FieldValueInQueryConstraint createConstraint(StepField f, Query q) {
        return  new BasicFieldValueInQueryConstraint(f, q);
    }

    @Override
    public Constraint setInverse(Constraint c, boolean i) {
        ((BasicConstraint) c).setInverse(i);
        return c;
    }

    @Override
    public FieldConstraint setCaseSensitive(FieldConstraint c, boolean s) {
        ((BasicFieldConstraint) c).setCaseSensitive(s);
        return c;

    }
    @Override
    public CompositeConstraint createConstraint(Constraint c1, int operator, Constraint c2) {
        if ((!used) && c1 instanceof BasicCompositeConstraint && ((CompositeConstraint) c1).getLogicalOperator() == operator) {
            if (c2 != null) ((BasicCompositeConstraint) c1).addChild(c2);
            return (CompositeConstraint) c1;
        } else {
            BasicCompositeConstraint c = new BasicCompositeConstraint(operator);
            if (c1 != null) c.addChild(c1);
            if (c2 != null) c.addChild(c2);
            return c;
        }
    }

    @Override
    public void setConstraint(Constraint c) {
        if (used) throw new BridgeException("Query was used already");
        query.setConstraint(c);
    }

    @Override
    public SortOrder addSortOrder(StepField f, int direction) {
        return addSortOrder(f, direction, false);
    }

    @Override
    public SortOrder addSortOrder(StepField f, int direction, boolean caseSensitive) {
        return addSortOrder(f, direction, caseSensitive, -1);
    }

    @Override
    public SortOrder addSortOrder(StepField f, int direction, boolean caseSensitive, int part) {
        if (used) throw new BridgeException("Query was used already");
        if (f == null) throw new BridgeException("Cannot add sortorder on 'null' step field");
        BasicSortOrder o = query.addSortOrder(f);
        o.setDirection(direction);
        o.setCaseSensitive(caseSensitive);
        if (o instanceof BasicDateSortOrder) {
            ((BasicDateSortOrder)o).setPart(part);
        }
        return o;
    }

    /**
     * @since MMBase-1.7.1
     */
    @Override
    public void addNode(Step  s, int nodeNumber) {
        if (used) throw new BridgeException("Query was used already");
        BasicStep step = (BasicStep) s;
        if (step == null) throw new IllegalArgumentException("Step may not be null");
        step.addNode(nodeNumber);
    }

    @Override
    public void addNode(Step  s, Node node) {
        addNode(s, node.getNumber());
    }

    @Override
    public boolean isUsed() {
        return used;
    }

    @Override
    public boolean markUsed() {
        boolean wasUsed = used;
        query.markUsed();
        //assert queryCheck != null; // should this perhaps be implicitely done then?
        used = true;
        return wasUsed;
    }


    boolean isSecure() {
        return queryCheck != null && queryCheck.isChecked();
    }

    /**
     * Applies a security-constraint to this Query. Such a constraint can be removed easily (needed
     * before cloning a query and so on).
     * @see #removeSecurityConstraint
     */
    void setSecurityConstraint(Authorization.QueryCheck c) {
        if (queryCheck != null) {
            throw new BridgeException("Already a security constraints set");
        }
        if (insecureConstraint != null) {
            throw new BridgeException("Already a insecure constraint defined");
        }
        if (c == null) {
            throw new BridgeException("QueryCheck may not be null");
        }
        if (log.isDebugEnabled()) {
            log.debug("Setting security check " + c + " TO " + this);
        }
        queryCheck = c;

        insecureConstraint = query.getConstraint(); // current constraint
        Constraint secureConstraint = queryCheck.getConstraint();
        if (secureConstraint != null) {
            if (insecureConstraint != null) {
                BasicCompositeConstraint compConstraint = new BasicCompositeConstraint(CompositeConstraint.LOGICAL_AND);
                compConstraint.addChild(insecureConstraint);
                compConstraint.addChild(secureConstraint);
                query.setConstraint(compConstraint);
            } else {
                query.setConstraint(secureConstraint);
            }
        }
    }

    /**
     * Remove a previously set security constraint (if set one)
     * @see #setSecurityConstraint
     */
    void removeSecurityConstraint() {
        if (log.isDebugEnabled()) {
            log.debug("Removing " + queryCheck + " FROM " + this);
        }
        if (queryCheck != null) {
            query.setConstraint(insecureConstraint);
            insecureConstraint = null;
        }
        queryCheck = null;

    }

    @Override
    public Cloud getCloud() {
        return cloud;
    }

    @Override
    public NodeList getList() {
        return cloud.getList(this);
    }

    @Override
    public boolean equals(Object obj) {
        while (obj instanceof SearchQueryWrapper) {
            obj = ((SearchQueryWrapper)obj).unwrap();
        }
        return query.equals(obj);
    }

    // javadoc is inherited
    @Override
    public int hashCode() {
        return query.hashCode();
    }


    @Override
    public String toString() {
        return query.toString() + (used ? " (used)" : "") + " INSECURE: " + insecureConstraint + " QUERYCHECK: " + queryCheck;

    }

    /**
     * Getter for use in EL. Returns {@link #toSql}.
     */
    public String getSql() {
        return toSql();
    }

    @Override
    public String toSql() {
        try {
            return cloud.getCloudContext().getSearchQueryHandler().createSqlString(query);
        } catch (org.mmbase.storage.search.SearchQueryException sqe) {
            log.warn(sqe);
            return query.toString();
        }

    }



}
