package org.mmbase.applications.bugtracker;

/**
 */
public class BugCategories extends java.util.Vector {
    
    /** Creates a new instance of BugCategories */
    public BugCategories() {
    }
    
    public BugCategory getBugCategory(int index){
        return (BugCategory)get(index);
    }
}
