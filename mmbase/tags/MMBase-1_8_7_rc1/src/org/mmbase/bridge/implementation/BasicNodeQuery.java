/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.implementation;

import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.util.logging.*;

/**
 * 'Basic' implementation of bridge NodeQuery. Wraps a Query with all and only fields of one
 * Step. If there is only one step, this can wrap NodeSearchQuery of core.
 *
 * Often, queries with more steps are sensible nodequeries, because sorting (e.g. posrel.pos) or
 * filtering can be done on those steps (e.g. publishtimes).
 *
 * Nodes of this type can be used as an argument to function which do return 'real' nodes (so not clusternodes).
 *
 * @todo perhaps it would be nice to have the possibllity to query also two complete steps (also one of the neigbouring 'relation' steps).
 *         this would give nice efficient implementations of things like mm:relatednode (of mm:listrelations)
 *
 * @todo This kind of functionality should perhaps be present in NodeSearchQuery itself because you can then use it 'under' the bridge too.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.7
 * @see org.mmbase.storage.search.implementation.NodeSearchQuery
 */
public class BasicNodeQuery extends BasicQuery implements NodeQuery {

    private static final Logger log = Logging.getLoggerInstance(BasicNodeQuery.class);
    protected Step step = null;

    BasicNodeQuery(Cloud c) {
        super(c);
    }

    /**
     * node query.
     */
    BasicNodeQuery(BasicNodeManager nodeManager) {
        super(nodeManager.cloud);
        query = new NodeSearchQuery(nodeManager.getMMObjectBuilder());
        this.step = (Step) getSteps().get(0); // the only step
    }
    BasicNodeQuery(BasicNodeManager nodeManager, NodeSearchQuery q) {
        super(nodeManager.cloud);
        query = q;
        this.step = (Step) getSteps().get(0); // the only step
    }

    /**
     * Makes a multi-step node-query, based on a normal query. As a default, all fields of last steps are added (if at least there are steps already)
     *
     */
    BasicNodeQuery(Cloud cloud, SearchQuery q) {
        super(cloud);
        query = new BasicSearchQuery(q);
        List steps = query.getSteps();
        if (steps.size() > 0) {
            setNodeStep((Step) (steps.get(steps.size() -1 )));
        }
    }

    public NodeManager getNodeManager() {
        if (step == null) return null;
        if (step instanceof RelationStep) {
            RelationStep rs = (RelationStep) step;
            int role  = -1;
            Integer roleValue = rs.getRole();
            if (roleValue != null) {
                role = roleValue.intValue();
            }
            String roleName = role > 0 ? cloud.getNode(role).getStringValue("sname") : null;
            NodeManager previous = cloud.getNodeManager(rs.getPrevious().getTableName());
            NodeManager next     = cloud.getNodeManager(rs.getNext().getTableName());
            if (cloud.hasRelationManager(previous, next, roleName)) {
                return cloud.getRelationManager(previous, next, roleName);
            } else {
                if (roleName == null) {
                    return cloud.getNodeManager("insrel"); // not a relation manager, no role known
                } else {
                    return cloud.getRelationManager(roleName);
                }
            }
        } else {
            return cloud.getNodeManager(step.getTableName());
        }
    }

    public Step getNodeStep() {
        return step;
    }

    // overridden from BasicQuery (a node query does not have '.' in its field names)
    public StepField createStepField(String fieldName) {
        if (fieldName.indexOf('.') == -1) {
            BasicStepField stepField = (BasicStepField) getStepField(getNodeManager().getField(fieldName));
            // stepField.setAlias(fieldName);
            if (stepField == null) throw new  NotFoundException("No field '" + fieldName + "' found in " + getSteps());
            return stepField;
        } else {
            // It does contain dot? Perhaps one of the other steps.
            return super.createStepField(fieldName);
        }
    }

    public StepField getStepField(Field field) {
        if (query instanceof NodeSearchQuery) {
            BasicStepField stepField = ((NodeSearchQuery) query).getField(((BasicField)field).coreField);
            return stepField;
        } else {
            Iterator fields = query.getFields().iterator();
            while(fields.hasNext()) {
                StepField stepField = (StepField) fields.next();
                if (stepField.getStep().equals(step)) {
                    if (stepField.getFieldName().equals(field.getName())) {
                        return stepField;
                    }
                }

            }
        }
        //throw new  NotFoundException("Could not find field '" + field + "' in " + this);
        return null; // hmm.
    }


    public void removeFields() {
        explicitFields.clear();
        setNodeStep(step);
    }


    public List getExtraFields() {
        return Collections.unmodifiableList(explicitFields);
    }


    /**
     * Adds all fields of the gives collection, unless it is a field of the 'step' itself
     */
    protected void addFields(Collection c) {
        Iterator i = c.iterator();
        while (i.hasNext()) {
            BasicStepField sf = (BasicStepField) i.next();
            Step addedStep = sf.getStep();
            if (addedStep.equals(step)) continue; // these are among the node-fields already
            query.addField(addedStep, sf.getField());
        }
    }

    // overrides setDistinct of super, because it should consider 'step' Fields.
    public Query setDistinct(boolean distinct) {
        if (used) throw new BridgeException("Query was used already");
        query.setDistinct(distinct);
        if (distinct) { // in that case, make sure only the 'explicitely' added fields remain.
            query.removeFields();
            query.addFields(step);
            implicitFields.clear();
            addFields(explicitFields);
        }
        return this;
    }

    public Step setNodeStep(Step step) {
        assert query.getSteps().contains(step);
        if (this.step == null && step == null) return null;
        if (this.step != null && this.step.equals(step)) return this.step; // already this step.
        // Make sure the query _starts_ with the Node-fields.
        // otherwise BasicQueryHandler.getNodes could do it wrong...
        query.removeFields();
        query.addFields(step);
        addFields(explicitFields);
        Step prevStep = this.step;
        this.step = step;
        if (! isDistinct() ) {
            addFields(implicitFields);
        }
        return prevStep;
    }

    public Query cloneWithoutFields() {
        BasicSearchQuery bsq = new BasicSearchQuery(query, BasicSearchQuery.COPY_WITHOUTFIELDS);
        if (queryCheck != null) {
            removeSecurityConstraintFromClone(bsq);
        }
        BasicNodeQuery clone = new BasicNodeQuery(cloud, bsq);
        clone.used = false;
        clone.aggregating = false;
        return clone;
    }

    public NodeList getList() {
        return getNodeManager().getList(this);
    }


}
