/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;

import java.util.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;
import org.mmbase.security.Authorization;


/**
 * 'Basic' implementation of bridge Query. Wraps a 'BasicSearchQuery' from core.
 *
 * @author Michiel Meeuwissen
 * @version $Id: BasicQuery.java,v 1.14 2003-08-05 19:31:53 michiel Exp $
 * @since MMBase-1.7
 * @see org.mmbase.storage.search.implementation.BasicSearchQuery
 */
public class BasicQuery implements Query  {
    

    private static Logger log = Logging.getLoggerInstance(BasicQuery.class);

    protected boolean used = false;
    protected boolean aggregating = false; // ugly ugly, this member is in BasicSearchQuery too (but private).


    protected Authorization.QueryCheck secureConstraint = null;

    private   HashMap  aliasSequences = new HashMap(); 
    // to make unique table aliases. This is similar impl. as  in core. Why should it be at all....

    protected BasicSearchQuery query;

    protected Cloud cloud; // reference to the cloud.




    BasicQuery(Cloud c) {
        query = new BasicSearchQuery();
        cloud = c;
    }

    BasicQuery(Cloud c, boolean aggregating) {
        query = new BasicSearchQuery(aggregating);
        this.aggregating = aggregating;
        cloud = c;
    }

    BasicQuery(Cloud c, BasicSearchQuery q) {
        query = q;
        cloud = c;
    }


    BasicSearchQuery getQuery() {
        return query;
    }

    protected void createNewQuery() {
        query = new BasicSearchQuery();
    }


    // SearchQuery impl:

    public List getSteps() {
        return query.getSteps();
    }
    public List getFields() {
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
    public List getSortOrders() {
        return query.getSortOrders();
    }
    public boolean isDistinct() {
        return query.isDistinct();
    }


    // bridge.Query impl.:

    public boolean isAggregating() {
        return aggregating;
    }

    public Object clone() { // also works for descendants (NodeQuery)
        try {
            BasicQuery clone = (BasicQuery) super.clone();
            clone.query = (BasicSearchQuery) query.clone();
            clone.aliasSequences = (HashMap) aliasSequences.clone();
            clone.used = false;
            return clone;
        } catch (CloneNotSupportedException e) {
            // cannot happen
            throw new InternalError(e.toString());
        }
    }
    public Query aggregatingClone() {
        BasicSearchQuery bsq = new BasicSearchQuery(query, true); 
        BasicQuery clone = new BasicQuery(cloud, bsq);
        clone.used = false;
        clone.aggregating = true;
        return clone;
    }


    protected String createAlias(Step step) {
        String tableName = step.getTableName();
        Integer seq = (Integer) aliasSequences.get(tableName);
        if (seq == null) {
            aliasSequences.put(tableName, new Integer(1));
            return tableName;
        } else {
            aliasSequences.put(tableName, new Integer(seq.intValue() + 1));
            return tableName + seq;
        }
    }


    public Step addStep(NodeManager nm) {
        if (used) throw new BridgeException("Query was used already");

        removeSecurityConstraint(); // if present

        BasicStep step = query.addStep(((BasicNodeManager)nm).builder);
        
        step.setAlias(createAlias(step));
        addField(step, nm.getField("number")); // how works distinct in mmbase?
        return step;
    }


    protected RelationStep addRelationStep(InsRel insrel, NodeManager otherNodeManager, int searchDir) {
        MMObjectBuilder otherBuilder = ((BasicNodeManager) otherNodeManager).builder;        
        BasicRelationStep relationStep = query.addRelationStep(insrel, otherBuilder);
        relationStep.setDirectionality(searchDir); 
        relationStep.setAlias(createAlias(relationStep));
        BasicStep next = (BasicStep) relationStep.getNext();
        next.setAlias(createAlias(next));
        addField(next, otherNodeManager.getField("number")); // distinct?
        /*
          optimize query 
        relationStep.setCheckedDirectionality(true);
        // Check directionality is requested and supported.
        if (dir != SEARCH_ALL && InsRel.usesdir) {
            relationStep.setCheckedDirectionality(true);
        }
        BasicCloudContext.mmb;

        too much copying from ClusterBuilder -> like to centralize code somewhere
        */
        return relationStep;
    }
    public RelationStep addRelationStep(NodeManager otherNodeManager) {
        return addRelationStep(otherNodeManager, RelationStep.DIRECTIONS_BOTH); // would 'DESTINATION' not be better?
    }

    public RelationStep addRelationStep(NodeManager otherNodeManager, int searchDir) {
        return addRelationStep(BasicCloudContext.mmb.getInsRel(), otherNodeManager, searchDir); 
    }


    public RelationStep addRelationStep(RelationManager rm) {
        return addRelationStep(rm, RelationStep.DIRECTIONS_BOTH); // would 'DESTINATION' not be better?
    }
    public RelationStep addRelationStep(RelationManager rm, int searchDir) {
        if (used) throw new BridgeException("Query was used already");
        // could check here if the relationmanager 'fits' the last existing step.
        InsRel insrel =  (InsRel) ((BasicRelationManager)rm).builder;
        return addRelationStep(insrel, rm.getDestinationManager(), searchDir);
    }

    
    public StepField addField(Step step, Field field) {
        if (used) throw new BridgeException("Query was used already");
        return query.addField(step, ((BasicField) field).field);
    }

    public StepField createStepField(Step step, Field field) {
        return new BasicStepField(step, ((BasicField) field).field);
    }

    
    /**
     * Returns the step with given alias, or null if it is not present
     */
    protected Step getStep(String stepAlias) {
        Iterator i = getSteps().iterator();
        while (i.hasNext()) {
            Step step = (Step) i.next();
            if (stepAlias.equals(step.getAlias())) {
                return step;
            }
        }
        return null;
    }

    public StepField createStepField(String fieldIdentifier) {
        int point = fieldIdentifier.indexOf('.');
        String stepAlias = fieldIdentifier.substring(0, point);
        String fieldName = fieldIdentifier.substring(point + 1);
        Step step = getStep(stepAlias);
        NodeManager nm = cloud.getNodeManager(step.getTableName());
        Field field = nm.getField(fieldName);
        return createStepField(step, field);
    }

    public AggregatedField addAggregatedField(Step step, Field field, int aggregationType) {
        if (used) throw new BridgeException("Query was used already");
        BasicAggregatedField aggregatedField =  query.addAggregatedField(step, ((BasicField) field).field, aggregationType);
        aggregatedField.setAlias(field.getName()); 
        
        if (this instanceof NodeQuery) {
            NodeQuery nodeQuery = (NodeQuery) this;
            ((BasicStep) step).setAlias(nodeQuery.getNodeManager().getName()); 
            // Step needs alias, because otherwise clusterbuilder chokes.
            // And node-manager.getList is illogical, because a aggregated result is certainly not a 'real' node.
        }

        // TODO, think of something better. --> a good way to present aggregated results.

        return aggregatedField;
    }
    
    public Query setDistinct(boolean distinct) {
        if (used) throw new BridgeException("Query was used already");
        query.setDistinct(distinct);
        return this;
    }

    public Query setMaxNumber(int maxNumber) {
        if (used) throw new BridgeException("Query was used already");
        query.setMaxNumber(maxNumber);
        return this;
    }
    public Query setOffset(int offset) {
        if (used) throw new BridgeException("Query was used already");
        query.setOffset(offset);
        return this;

    }

    public LegacyConstraint      createConstraint(String s) {
        return new BasicLegacyConstraint(s);
    }

    public FieldNullConstraint    createConstraint(StepField f) {
        return new BasicFieldNullConstraint(f);
    }

    public FieldValueConstraint        createConstraint(StepField f, Object v) {
        return createConstraint(f, FieldCompareConstraint.EQUAL, v);
    }

    public FieldValueConstraint        createConstraint(StepField f, int op, Object v) {
        BasicFieldValueConstraint c = new BasicFieldValueConstraint(f, v);
        c.setOperator(op);
        return c;
    }
    public CompareFieldsConstraint     createConstraint(StepField f, int op, StepField  v) {
        BasicCompareFieldsConstraint c = new BasicCompareFieldsConstraint(f, v);        
        c.setOperator(op);
        return c;
    }
    public FieldValueBetweenConstraint createConstraint(StepField f, Object o1, Object o2) {
        return new BasicFieldValueBetweenConstraint(f, o1, o2);
    }
    public FieldValueInConstraint      createConstraint(StepField f, SortedSet v) {
        BasicFieldValueInConstraint c = new BasicFieldValueInConstraint(f);
        Iterator i = v.iterator();
        while (i.hasNext()) {
            c.addValue(i.next());
        }
        return c;
    }
    
    public Constraint                  setInverse(Constraint c, boolean i) {
        ((BasicConstraint) c).setInverse(i);
        return c;        
    }

    public FieldConstraint             setCaseSensitive(FieldConstraint c, boolean s) {
        ((BasicFieldConstraint) c).setCaseSensitive(s);
        return c;
        
    }
    public CompositeConstraint        createConstraint(Constraint c1, int operator, Constraint c2) {
        if (c1 instanceof CompositeConstraint && ((CompositeConstraint) c1).getLogicalOperator() == operator) {
            if (used) throw new BridgeException("Query was used already (so cannot modify composite constraints)");
            ((BasicCompositeConstraint) c1).addChild(c2);
            return (CompositeConstraint) c1;
        } else {        
            BasicCompositeConstraint c = new BasicCompositeConstraint(operator);        
            c.addChild(c1);
            c.addChild(c2);
            return c;
        }

    }

    public void setConstraint(Constraint c) {
        if (used) throw new BridgeException("Query was used already");
        query.setConstraint(c);
    }

    public SortOrder addSortOrder(StepField f, int direction) {
        if (used) throw new BridgeException("Query was used already");
        BasicSortOrder s = query.addSortOrder(f);
        s.setDirection(direction);
        return s;
        
    }

    public boolean isUsed() {
        return used;
    }
    public boolean markUsed() {
        boolean wasUsed = used;
        used = true;
        return wasUsed;
    }


    boolean isSecure() { 
        return secureConstraint != null && secureConstraint.isChecked();
    }

    void setSecurityConstraint(Authorization.QueryCheck c) {
        if (c != null && c.getConstraint() != null) {
            Constraint constraint = query.getConstraint();
            if (constraint != null) {
                log.debug("compositing constraint");
                Constraint compConstraint = createConstraint(constraint, CompositeConstraint.LOGICAL_AND, c.getConstraint());
                query.setConstraint(compConstraint);
            } else {
                query.setConstraint(c.getConstraint());
            }
        }
        secureConstraint = c;
    }

    void removeSecurityConstraint() {
        if (secureConstraint != null && secureConstraint.getConstraint() != null) {
            Constraint constraint = query.getConstraint();
            if (secureConstraint.equals(constraint)) {
                query.setConstraint(null);
            } else { // must be part of the composite constraint
                BasicCompositeConstraint compConstraint = (BasicCompositeConstraint) constraint;
                compConstraint.removeChild(secureConstraint.getConstraint()); // remove it
                if (compConstraint.getChilds().size() == 1) { // no need to let it composite then
                    Constraint newConstraint = (Constraint) compConstraint.getChilds().get(0);
                    query.setConstraint(newConstraint);
                }
            }
            secureConstraint = null;            
        }
    }


    public boolean equals(Object obj) {
        return query.equals(obj);
    }
    
    // javadoc is inherited
    public int hashCode() {
        return query.hashCode();
    }


    public String toString() {
        return (used ? "used: " : "unused: ") + query.toString();
    }
    


}
