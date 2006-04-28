/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */
package org.mmbase.storage.search.implementation;

import java.util.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.storage.search.*;

/**
 * A <code>NodeSearchQuery</code> implements a <code>SearchQuery</code>
 * that retrieves nodes of one specified nodetype.
 * <p>
 * The constructor creates the query with all persistent fields belonging to 
 * the specified nodetype excluding byte[] type fields. 
 * Use {@link #getField(FieldDefs) getField()} to retrieve each of these fields. 
 * <p>
 * Once an instance is constructed, it is not possible to add more fields/steps.
 * Consequently calling one of these methods always results in an 
 * <code>UnsupportedOperationException</code>:
 * <ul>
 * <li>{@link #addStep(MMObjectBuilder) addStep()}
 * <li>{@link #addRelationStep(InsRel,MMObjectBuilder) addRelationStep()}
 * <li>{@link #addField(Step,FieldDefs) addField()}
 * <li>{@link #addAggregatedField(Step,FieldDefs,int) addAggregatedField()}
 *
 * @author  Rob van Maris
 * @version $Id: NodeSearchQuery.java,v 1.9 2003-08-27 21:41:33 michiel Exp $
 * @since MMBase-1.7
 */
public class NodeSearchQuery extends BasicSearchQuery implements SearchQuery {
    
    /** Builder for the specified nodetype. */
    private MMObjectBuilder builder = null;
    
    /** Map, maps fields to stepfields. */
    private Map stepFields = new HashMap();
    

    /** 
     * Creator.
     *
     * @param builder The builder for the nodetype, must not be a 
     *        {@link org.mmbase.module.core.VirtualBuilder virtual} builder.
     * @throws IllegalArgumentException When an invalid argument is supplied.
     */
    public NodeSearchQuery(MMObjectBuilder builder) {
        if (builder == null) {
            throw new IllegalArgumentException("Invalid builder value: " + builder);
        }
        if (builder instanceof VirtualBuilder) {
            throw new IllegalArgumentException("Invalid builder type, because this is a virtual builder: " + builder.getClass().getName());
        }
        Step step = super.addStep(builder);
        addFields(step);
        this.builder = builder;
    }

    /*
    NodeSearchQuery(SearchQuery searchQuery) {
        super(searchQuery);
        List steps = searchQuery.getSteps();
        if (steps.size() != 1) throw new IllegalArgumentException("Given search-query cannot be a NodeSearchQuery");
        BasicStep step = (BasicStep) steps.get(0);
        fields.clear();
        addFields(step);    
        builder = step.getBuilder();
    }
    */

    protected void copySteps(SearchQuery q) {
        // no need, can be done by clone
    }


    protected void copyFields(SearchQuery q) {
        // no need, can be done by clone
    }
    /**
     * Returns the stepfield corresponding to the specified field.
     *
     * @param field The field.
     * @return The corresponding stepfield.
     * @throws IllegalArgumentException When the field is not a
     *         persistent field of the associated nodetype.
     */
    public BasicStepField getField(FieldDefs field) {
        BasicStepField stepField = (BasicStepField) stepFields.get(field);
        if (stepField == null) {
            // Not found.
            throw new IllegalArgumentException("Not a persistent field of builder " + builder.getTableName() + ": " + field);
        }
        return stepField;
    }
    
    /**
     * Returns the builder for the specified nodetype.
     *
     * @return The builder.
     */
    public MMObjectBuilder getBuilder() {
        return builder;
    }
    
    // javadoc is inherited
    public BasicStep addStep(MMObjectBuilder builder) {
        throw new UnsupportedOperationException(
        "Adding more steps to NodeSearchQuery not supported.");
    }
    
    // javadoc is inherited
    public BasicRelationStep addRelationStep(
            InsRel builder, MMObjectBuilder nextBuilder) {
        throw new UnsupportedOperationException(
        "Adding more steps to NodeSearchQuery not supported.");
    }
    
    // javadoc is inherited
    public BasicStepField addField(Step step, FieldDefs fieldDefs) {
        if (builder != null) { // this means: inited already.
            throw new UnsupportedOperationException("Adding more fields to NodeSearchQuery not supported.");
        } else {
            return super.addField(step, fieldDefs);
        }
    }
    // MM
    protected void mapField(FieldDefs field, StepField stepField) {
        stepFields.put(field, stepField);
    }
    
    // javadoc is inherited
    public BasicAggregatedField addAggregatedField(Step step, FieldDefs fielDefs,
            int aggregationType) {
        throw new UnsupportedOperationException(
        "Adding more fields to NodeSearchQuery not supported.");
    }
    
}
