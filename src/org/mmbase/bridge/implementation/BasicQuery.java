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
 * @version $Id: BasicQuery.java,v 1.4 2003-07-21 21:22:31 michiel Exp $
 * @since MMBase-1.7
 */
public class BasicQuery implements Query  {

    private static Logger log = Logging.getLoggerInstance(BasicQuery.class);

    private boolean used = false;

    BasicSearchQuery query;

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


    BasicQuery() {
        query = new BasicSearchQuery();
    }

    BasicQuery(BasicSearchQuery q) {
        query = q;
    }

    public Object clone() {
        try {
            BasicQuery clone = (BasicQuery) super.clone();
            clone.query = new BasicSearchQuery(this);
            clone.used = false;
            return clone;
        } catch (CloneNotSupportedException e) {
            // cannot happen
            throw new InternalError(e.toString());
        }
    }

    // bridge.Query impl.:

    public Step addStep(NodeManager nm) {
        return addStep(nm, nm.getName());
    }
    public Step addStep(NodeManager nm, String alias) {
        if (used) throw new BridgeException("Query was used already");
        BasicStep step = query.addStep(((BasicNodeManager)nm).builder);
        step.setAlias(alias); 
        return step;
    }


    public RelationStep addRelationStep(RelationManager rm) {
        return addRelationStep(rm, RelationStep.DIRECTIONS_BOTH); // would 'DESTINATION' not be better?
    }
    public RelationStep addRelationStep(RelationManager rm, int dir) {


        if (used) throw new BridgeException("Query was used already");
        InsRel insrel =  (InsRel) ((BasicRelationManager)rm).builder;
        MMObjectBuilder otherBuilder = ((BasicNodeManager) rm.getDestinationManager()).builder;        
        BasicRelationStep step = query.addRelationStep(insrel, otherBuilder);
        step.setDirectionality(dir); 
        ((BasicStep) step.getNext()).setAlias(rm.getDestinationManager().getName());
        return step;
    }

    
    public StepField addField(Step step, Field field) {
        if (used) throw new BridgeException("Query was used already");
        return query.addField(step, ((BasicField) field).field);
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
