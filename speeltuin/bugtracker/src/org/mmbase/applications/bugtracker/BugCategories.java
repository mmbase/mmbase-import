/*
 * BugCategories.java
 *
 * Created on June 7, 2002, 9:37 AM
 */

package org.mmbase.applications.bugtracker;

/**
 *
 * @author  mmbase
 */
public class BugCategories extends java.util.Vector {
    
    /** Creates a new instance of BugCategories */
    public BugCategories() {
    }
    
    public BugCategory getBugCategory(int index){
        return (BugCategory)get(index);
    }
}
