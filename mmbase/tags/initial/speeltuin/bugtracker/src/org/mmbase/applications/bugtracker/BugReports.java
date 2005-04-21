/*
 * BugReports.java
 *
 * Created on June 10, 2002, 7:17 PM
 */

package org.mmbase.applications.bugtracker;

/**
 *
 * @author  mmbase
 */
public class BugReports extends java.util.Vector{
    
    /** Creates a new instance of BugReports */
    public BugReports() {
        super();
    }
    
    public BugReport getButReport(int index){
        return (BugReport)get(index);
    }
}
