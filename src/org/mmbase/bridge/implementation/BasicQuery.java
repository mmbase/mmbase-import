/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;

import java.util.*;
import org.mmbase.bridge.util.Queries;
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
 * @version $Id: BasicQuery.java,v 1.34 2004-02-17 09:43:42 michiel Exp $
 * @since MMBase-1.7
 * @see org.mmbase.storage.search.implementation.BasicSearchQuery
 */
public class BasicQuery implements Query  {
    

    private static final Logger log = Logging.getLoggerInstance(BasicQuery.class);

    protected boolean used = false;
    protected boolean aggregating = false; // ugly ugly, this member is in BasicSearchQuery too (but private).


    protected Authorization.QueryCheck secureConstraint = null;

    private   HashMap  aliasSequences = new HashMap(); 
    // to make unique table aliases. This is similar impl. as  in core. Why should it be at all....

    protected BasicSearchQuery query;

    protected Cloud cloud; // reference to the cloud.



    /**
     * The implicitely added 'extra' fields. These are removed if the query becomes 'distinct'. So,
     * you can e.g. not do element= on a distinct query result.     
     */
    protected List implicitFields = new ArrayList();

    /**
     * The explicitely added 'extra' fields. Because you explicitely added those, they will not be removed if the query becomes 'distinct'.
     */
    protected List explicitFields = new ArrayList();



    BasicQuery(Cloud c) {
        query = new BasicSearchQuery();
        cloud = c;
    }

    BasicQuery(Cloud c, boolean aggregating) {
        query = new BasicSearchQuery(aggregating);
        this.aggregating = aggregating;
        cloud = c;
    }

    public BasicQuery(Cloud c, BasicSearchQuery q) { // public for org.mmbase.bridge.util
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
        BasicSearchQuery bsq = new BasicSearchQuery(query, BasicSearchQuery.COPY_AGGREGATING); 
        BasicQuery clone     = new BasicQuery(cloud, bsq);
        clone.used = false;
        clone.aggregating = true;
        return clone;
    }

    public Query cloneWithoutFields() {
        BasicSearchQuery bsq = new BasicSearchQuery(query, BasicSearchQuery.COPY_WITHOUTFIELDS); 
        BasicQuery clone     = new BasicQuery(cloud, bsq);
        clone.used = false;
        clone.aggregating = false;
        return clone;
    }


    protected String createAlias(String  name) {
        if (used) throw new BridgeException("Query was used already");
        Integer seq = (Integer) aliasSequences.get(name);
        if (seq == null) {
            aliasSequences.put(name, new Integer(1));
            return name;
        } else {
            aliasSequences.put(name, new Integer(seq.intValue() + 1));
            return name + seq;
        }
    }


    public Step addStep(NodeManager nm) {
        if (used) throw new BridgeException("Query was used already");

        removeSecurityConstraint(); // if present
 
        BasicStep step = query.addStep(((BasicNodeManager)nm).builder);
        setAlias(step, ""); // "": generate alias
        if (! aggregating) {
            addField(step, nm.getField("number"));
        }

        return step;
    }

    public void setAlias(Step step, String alias) {
        if ("".equals(alias)) {
            alias = createAlias(step.getTableName());
        } 
        
        BasicStep basicStep = (BasicStep) step;
        basicStep.setAlias(alias);
    }


    // similar constants to those of CluserBuilder but still different ?!
    protected int getRelationStepDirection(String search) {
        if (search == null) {
            return RelationStep.DIRECTIONS_BOTH;
        }
        search = search.toUpperCase();
        if ("DESTINATION".equals(search)) {
            return RelationStep.DIRECTIONS_DESTINATION;
        } else if ("SOURCE".equals(search)) {
            return RelationStep.DIRECTIONS_SOURCE;
        } else if ("BOTH".equals(search)) {
            return RelationStep.DIRECTIONS_BOTH;
        } else {
            throw new BridgeException("'" + search + "' cannot be converted to a relation-step direction constant");
        }
    }


    protected BasicRelationStep addRelationStep(InsRel insrel, NodeManager otherNodeManager, int direction) {
        MMObjectBuilder otherBuilder = ((BasicNodeManager) otherNodeManager).builder;        
        BasicRelationStep relationStep = query.addRelationStep(insrel, otherBuilder);
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
    public RelationStep addRelationStep(NodeManager otherNodeManager) {
        return addRelationStep(otherNodeManager, null, "BOTH"); 
    }


    public RelationStep addRelationStep(NodeManager otherNodeManager, String role, String direction) {
        if ("".equals(role)) role = null;
        return addRelationStep(otherNodeManager, role, direction, true);
    }


    protected RelationStep addRelationStep(NodeManager otherNodeManager, String role, String direction, boolean warnOnImpossibleStep) {
        if (used) throw new BridgeException("Query was used already");

        // a bit silly that two lookups are needed
        int relationDir = getRelationStepDirection(direction); 
        int searchDir   = ClusterBuilder.getSearchDir(direction);

        TypeRel typeRel = BasicCloudContext.mmb.getTypeRel();
        if (role == null) {
            InsRel insrel =  BasicCloudContext.mmb.getInsRel();
            BasicRelationStep step = addRelationStep(insrel, otherNodeManager, relationDir);
            if (!typeRel.optimizeRelationStep(step, cloud.getNodeManager(step.getPrevious().getTableName()).getNumber(), otherNodeManager.getNumber(), -1, searchDir)) {
                if (warnOnImpossibleStep) {
                    log.warn("Added an impossible relation step (" + step + " to " + otherNodeManager + ") to the query. The query-result will always be empty now (so you could as well not execute it).");
                }
            }
            return step;
        } else {
            RelDef relDef = BasicCloudContext.mmb.getRelDef();
            int r = relDef.getNumberByName(role);
            if (r == -1) {
                throw new NotFoundException("Role '" + role + "' does not exist.");
            }
            MMObjectNode relDefNode = relDef.getNode(r);
            InsRel insrel = ((RelDef)relDefNode.getBuilder()).getBuilder(relDefNode.getNumber());
            BasicRelationStep step =  addRelationStep(insrel, otherNodeManager, relationDir);
            step.setRole(new Integer(r));
            if (! cloud.hasNodeManager(role)) {
                step.setAlias(createAlias(role));
            }
            if (! typeRel.optimizeRelationStep(step, cloud.getNodeManager(step.getPrevious().getTableName()).getNumber(), otherNodeManager.getNumber(), r, searchDir)) {
                if (warnOnImpossibleStep) {
                    log.warn("Added an impossible relation step (" + step + " to " + otherNodeManager + ") to the query. The query-result will always be empty now (so you could as well not execute it).");
                }
            }
            return step;
        }
    }

    public void removeFields() {
        query.removeFields();
        explicitFields.clear();
        Iterator i = implicitFields.iterator();
        while (i.hasNext()) {
            BasicStepField sf = (BasicStepField) i.next();
            Step addedStep = sf.getStep();
            query.addField(addedStep, sf.getFieldDefs());
        }
        
    }

    
    public StepField addField(Step step, Field field) {
        if (used) throw new BridgeException("Query was used already");
        BasicStepField sf = query.addField(step, ((BasicField) field).field);
        explicitFields.add(sf);
        implicitFields.remove(sf); // it's explicitly added now
        return sf;
    }
    public StepField addField(String fieldIdentifier) {
        // code copied from createStepField, should be centralized
        if (used) throw new BridgeException("Query was used already");
        int dot = fieldIdentifier.indexOf('.');
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
            BasicStepField sf = query.addField(step, ((BasicField) field).field);
            implicitFields.add(sf);
        }
    }

    public StepField createStepField(Step step, Field field) {
        return new BasicStepField(step, ((BasicField) field).field);
    }

    public StepField createStepField(Step step, String fieldName) {
        return createStepField(step, cloud.getNodeManager(step.getTableName()).getField(fieldName));
    }

    

    public Step getStep(String stepAlias) {
        return Queries.searchStep(getSteps(), stepAlias);
    }

    public StepField createStepField(String fieldIdentifier) {
        // code copied from addField, should be centralized
        int dot = fieldIdentifier.indexOf('.');
        String stepAlias = fieldIdentifier.substring(0, dot);
        String fieldName = fieldIdentifier.substring(dot + 1);
        Step step = getStep(stepAlias);
        if (step == null) throw new  NotFoundException("No step with alias '" + stepAlias + "' found in " + getSteps()); 
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
        if (distinct) { // in that case, make sure only the 'explicitely' added fields remain.
            query.removeFields();
            implicitFields.clear();
            Iterator i = explicitFields.iterator();
            while (i.hasNext()) {                
                BasicStepField sf = (BasicStepField) i.next();
                query.addField(sf.getStep(), sf.getFieldDefs());
            }
        }
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
        if (v.size() == 0) { // make sure the query becomes empty!
            Step step = f.getStep();
            StepField nf = createStepField(step, "number");            
            BasicFieldValueInConstraint c = new BasicFieldValueInConstraint(nf);
            c.addValue(new Integer(-1));
            return c;
        } else {
            BasicFieldValueInConstraint c = new BasicFieldValueInConstraint(f);
            Iterator i = v.iterator();
            while (i.hasNext()) {
                c.addValue(i.next());
            }
            return c;
        }
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

    public void addNode(Step  s, Node node) {
        if (used) throw new BridgeException("Query was used already");
        BasicStep step = (BasicStep) s;
        step.addNode(node.getNumber());
        return;
        
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

    /**
     * Applies a security-constraint to this Query. Such a constraint can be removed easily (needed
     * before cloning a query and so on).
     * @see #removeSecurityConstraint
     */
    void setSecurityConstraint(Authorization.QueryCheck c) {
        if (c != null && c.getConstraint() != null) {
            Queries.addConstraint(this, c.getConstraint());
        }
        secureConstraint = c;
    }

    /**
     * Remove a previously set security constraint (if set one)
     * @see #setSecurityConstraint
     */
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

    public Cloud getCloud() {
        return cloud;
    }

    public boolean equals(Object obj) {
        return query.equals(obj);
    }
    
    // javadoc is inherited
    public int hashCode() {
        return query.hashCode();
    }


    public String toString() {
        return query.toString() + (used ? "(used)" : "");
            
    }
    


}
