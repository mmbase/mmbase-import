/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search.implementation;

import java.util.*;
import org.mmbase.storage.search.*;

/**
 * A <code>ModifiedQuery</code> enables a modifiable lightweight copy of a 
 * {@link org.mmbase.storage.search.SearchQuery SearchQuery} to be created
 * by wrapping the original query. 
 * <p>
 * This class is provided primarily for use by core-, security- and 
 * storage layer classes, in those rare cases where modifications may be
 * appropriate to a query before processing it.
 * <p>
 * The <code>ModifiedQuery</code> wraps the original query, and can be modified
 * without affecting the original query. Modifications are not validated, and
 * may lead to inconsistent data in the query (e.g. sorting on fields 
 * that are not in the query), resulting in a query that can not be processed
 * by the storage.
 * Avoiding such inconsistencies is the responsibility of the user.
 *
 * @author  Rob van Maris
 * @version $Revision: 1.2 $
 * @since MMBase-1.7
 */
public class ModifiableQuery implements SearchQuery {
    
    private SearchQuery query = null;
    
    /** 
     * The value of the maxNumber property, -1 means: use
     * <code>query.getMaxNumber()</code>.
     */
    private int maxNumber = -1;
    
    /** 
     * The value of the offset property, -1 means: use
     * <code>query.getOffset()</code>. 
     */
    private int offset = -1;
    
    /**
     * The constraint, <code>null</code> means: use
     * <code>query.getConstraint()</code>.
     */
    private Constraint constraint = null;

    /**
     * The fields, <code>null</code> means: use
     * <code>query.getFields()</code>.
     */
    private List fields = null;
    
    /**
     * The sortorders, <code>null</code> means: use
     * <code>query.getSortOrders()</code>.
     */
    private List sortOrders = null;
    
    /**
     * The steps, <code>null</code> means: use
     * <code>query.getSteps()</code.
     */
    private List steps = null;
    
    /**
     * The value of the distinct property, <code>null</code> means: use
     * <code>query.isDistinct()</code>.
     */
    private Boolean distinct = null;
    
    /** Creates a new instance of ModifiedQuery */
    public ModifiableQuery(SearchQuery query) {
        this.query = query;
    }
    
    /**
     * Sets the maxNumber property.
     *
     * @param maxNumber The maxNumber value, -1 means: use
     * <code>query.getMaxNumber()</code>.
     * @return This <code>ModifiableQuery</code> instance.
     */
    public ModifiableQuery setMaxNumber(int maxNumber) {
        this.maxNumber = maxNumber;
        return this;
    }
    
    /**
     * Sets the offset property.
     *
     * @param offset The offset value, -1 means: use
     * <code>query.getOffset()</code>.
     * @return This <code>ModifiableQuery</code> instance.
     */
    public ModifiableQuery setOffset(int offset) {
        this.offset = offset;
        return this;
    }
    
    /**
     * Sets the constraint property.
     *
     * @param constraint The constraint, <code>null</code> means: use
     * <code>query.getConstraint()</code>.
     * @return This <code>ModifiableQuery</code> instance.
     */
    public ModifiableQuery setConstraint(Constraint constraint) {
        this.constraint = constraint;
        return this;
    }
    
    /**
     * Sets the fields property.
     *
     * @param fields The fields, <code>null</code> means: use
     * <code>query.getFields()</code>.
     * @return This <code>ModifiableQuery</code> instance.
     */
    public ModifiableQuery setFields(List fields) {
        this.fields = fields;
        return this;
    }
    
    /**
     * Sets the sortOrders property.
     *
     * @param sortOrders The sortorders, <code>null</code> means: use
     * <code>query.getSortOrders()</code>.
     * @return This <code>ModifiableQuery</code> instance.
     */
    public ModifiableQuery setSortOrders(List sortOrders) {
        this.sortOrders = sortOrders;
        return this;
    }
    
    /**
     * Sets the steps property.
     *
     * @param steps The steps, <code>null</code> means: use
     * <code>query.getSteps()</code.
     * @return This <code>ModifiableQuery</code> instance.
     */
    public ModifiableQuery setSteps(List steps) {
        this.steps = steps;
        return this;
    }
    
    /**
     * Sets the distinct property.
     *
     * @param distinct The value of the distinct property, 
     *        <code>null</code> means: use <code>query.isDistinct()</code>.
     * @return This <code>ModifiableQuery</code> instance.
     */
    public ModifiableQuery setDistinct(Boolean distinct) {
        this.distinct = distinct;
        return this;
    }
    
    // javadoc is inherited
    public int getMaxNumber() {
        if (maxNumber != -1) {
            return maxNumber;
        } else {
            return query.getMaxNumber();
        }
    }
    
    // javadoc is inherited
    public int getOffset() {
        if (offset != -1) {
            return offset;
        } else {
            return query.getOffset();
        }
    }
    
    // javadoc is inherited
    public Constraint getConstraint() {
        if (constraint != null) {
            return constraint;
        } else {
            return query.getConstraint();
        }
    }
    
    // javadoc is inherited
    public List getFields() {
        if (fields != null) {
            return fields;
        } else {
            return query.getFields();
        }
    }
    
    // javadoc is inherited
    public List getSortOrders() {
        if (sortOrders != null) {
            return sortOrders;
        } else {
            return query.getSortOrders();
        }
    }
    
    // javadoc is inherited
    public List getSteps() {
        if (steps != null) {
            return steps;
        } else {
            return query.getSteps();
        }
    }
    
    // javadoc is inherited
    public boolean isDistinct() {
        if (distinct != null) {
            return distinct.booleanValue();
        } else {
            return query.isDistinct();
        }
    }
    
}
