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



/**
 * @author Michiel Meeuwissen
 * @version $Id: BasicQuery.java,v 1.8 2003-07-25 20:44:31 michiel Exp $
 * @since MMBase-1.7
 * @see org.mmbase.storage.search.implementation.BasicSearchQuery
 */
public class BasicQuery implements Query  {
    

    private static Logger log = Logging.getLoggerInstance(BasicQuery.class);

    protected boolean used = false;
    private   int     aliasSequence = 0;

    protected BasicSearchQuery query;




    BasicQuery() {
        query = new BasicSearchQuery();
    }

    BasicQuery(boolean aggregated) {
        query = new BasicSearchQuery(aggregated);
    }

    BasicQuery(BasicSearchQuery q) {
        query = q;
    }


    BasicSearchQuery getQuery() {
        return query;
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


    public Object clone() {
        try {
            BasicQuery clone = (BasicQuery) super.clone();
            clone.query = new BasicSearchQuery(query); 
            clone.used = false;
            return clone;
        } catch (CloneNotSupportedException e) {
            // cannot happen
            throw new InternalError(e.toString());
        }
    }
    public Query aggregatedClone() {
        try {
            BasicQuery clone = (BasicQuery) super.clone();
            clone.query = new BasicSearchQuery(query, true); 
            clone.used = false;
            return clone;
        } catch (CloneNotSupportedException e) {
            // cannot happen
            throw new InternalError(e.toString());
        }
        
    }

    // bridge.Query impl.:

    public Step addStep(NodeManager nm) {
        if (used) throw new BridgeException("Query was used already");
        BasicStep step = query.addStep(((BasicNodeManager)nm).builder);
        step.setAlias(step.getTableName() + aliasSequence++); 
        return step;
    }


    public RelationStep addRelationStep(RelationManager rm) {
        return addRelationStep(rm, RelationStep.DIRECTIONS_BOTH); // would 'DESTINATION' not be better?
    }
    public RelationStep addRelationStep(RelationManager rm, int dir) {
        if (used) throw new BridgeException("Query was used already");
        InsRel insrel =  (InsRel) ((BasicRelationManager)rm).builder;
        MMObjectBuilder otherBuilder = ((BasicNodeManager) rm.getDestinationManager()).builder;        
        BasicRelationStep relationStep = query.addRelationStep(insrel, otherBuilder);
        relationStep.setDirectionality(dir); 
        relationStep.setAlias(relationStep.getTableName() + aliasSequence++); 
        BasicStep next = (BasicStep) relationStep.getNext();
        next.setAlias(next.getTableName() + aliasSequence++); 

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

    
    public StepField addField(Step step, Field field) {
        if (used) throw new BridgeException("Query was used already");
        return query.addField(step, ((BasicField) field).field);
    }

    public StepField getStepField(Step step, Field field) {
        return new BasicStepField(step, ((BasicField) field).field);
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
    
    public CompositeConstraint        createConstraint(Constraint c1, int operator, Constraint c2) {
        BasicCompositeConstraint c = new BasicCompositeConstraint(operator);        
        c.addChild(c1);
        c.addChild(c2);
        return c;
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
