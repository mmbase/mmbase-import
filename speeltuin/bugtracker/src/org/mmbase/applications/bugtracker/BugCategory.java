/*
 * BugCategory.java
 *
 * Created on June 7, 2002, 9:01 AM
 */

package org.mmbase.applications.bugtracker;

/**
 *
 * @author  mmbase
 * @mmbase-nodemanager-name bugcategory
 * @mmbase-nodemanager-field name
 *
 * @mmbase-relationmanager-name subcategoryrel
 * @mmbase-relationmanager-nodemanager categoryrel
 * @mmbase-relationmanager-directionality unidirectional
 * @mmbase-relationmanager-source bugcategory
 * @mmbase-relationmanager-destination bugcategory
 * @mmbase-relationmanager-field pos INTEGER
 */
public class BugCategory {
    
    /** Creates a new instance of BugCategory */
    public BugCategory() {
    }
    
    public BugCategories getSubCategories(){
        return new BugCategories();
    }
}
