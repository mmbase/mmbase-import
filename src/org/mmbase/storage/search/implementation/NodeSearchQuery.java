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
 * the specified nodetype. 
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
 * @version $Revision: 1.3 $
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
     *        {@link @org.mmbase.module.core.VirtualBuilder virtual} builder.
     * @throws IllegalArgumentException when an invalid argument is supplied
     */
    public NodeSearchQuery(MMObjectBuilder builder) {
        if (builder == null) {
            throw new IllegalArgumentException(
                "Invalid builder value: " + builder);
        }
        if (builder instanceof VirtualBuilder) {
            throw new IllegalArgumentException(
                "Invalid builder type, because this is a virtual builder: "
                + builder.getClass().getName());
        }
        this.builder = builder;
        Step step = super.addStep(builder);
        Iterator iFields = builder.getFields().iterator();
        while (iFields.hasNext()) {
            FieldDefs field = (FieldDefs) iFields.next();
            if (field.getDBState() == FieldDefs.DBSTATE_PERSISTENT
                    || field.getDBState() == FieldDefs.DBSTATE_SYSTEM) {
                BasicStepField stepField = super.addField(step, field);
                stepFields.put(field, stepField);
            }
        }
    }
    
    /**
     * Returns the stepfield corresponding to the specified field.
     *
     * @param field The field.
     * @return The corresponding stepfield.
     * @throws IllegalArgumentException If the field is not a
     *         persistent field of the associated nodetype.
     */
    public StepField getField(FieldDefs field) {
        StepField stepField = (StepField) stepFields.get(field);
        if (stepField == null) {
            // Not found.
            throw new IllegalArgumentException(
                "Not a persistent field of builder " + builder.getTableName()
                + ": " + field);
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
        throw new UnsupportedOperationException(
        "Adding more fields to NodeSearchQuery not supported.");
    }
    
    // javadoc is inherited
    public BasicAggregatedField addAggregatedField(Step step, FieldDefs fielDefs,
            int aggregationType) {
        throw new UnsupportedOperationException(
        "Adding more fields to NodeSearchQuery not supported.");
    }
    
}
