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
 * @version $Revision: 1.1 $
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
     */
    public void setMaxNumber(int maxNumber) {
        this.maxNumber = maxNumber;
    }
    
    /**
     * Sets the offset property.
     *
     * @param offset The offset value, -1 means: use
     * <code>query.getOffset()</code>.
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }
    
    /**
     * Sets the constraint property.
     *
     * @param constraint The constraint, <code>null</code> means: use
     * <code>query.getConstraint()</code>.
     */
    public void setConstraint(Constraint constraint) {
        this.constraint = constraint;
    }
    
    /**
     * Sets the fields property.
     *
     * @param fields The fields, <code>null</code> means: use
     * <code>query.getFields()</code>.
     */
    public void setFields(List fields) {
        this.fields = fields;
    }
    
    /**
     * Sets the sortOrders property.
     *
     * @param sortOrders The sortorders, <code>null</code> means: use
     * <code>query.getSortOrders()</code>.
     */
    public void setSortOrders(List sortOrders) {
        this.sortOrders = sortOrders;
    }
    
    /**
     * Sets the steps property.
     *
     * @param steps The steps, <code>null</code> means: use
     * <code>query.getSteps()</code.
     */
    public void setSteps(List steps) {
        this.steps = steps;
    }
    
    /**
     * Sets the distinct property.
     *
     * @param distinct The value of the distinct property, 
     *        <code>null</code> means: use <code>query.isDistinct()</code>.
     */
    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
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
