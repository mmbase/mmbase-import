/*
 * BugReportEnvironements.java
 *
 * Created on June 10, 2002, 7:20 PM
 */

package org.mmbase.applications.bugtracker;

/**
 *
 * @author  mmbase
 */
public class BugReportEnvironements extends java.util.Vector{
    
    /** Creates a new instance of BugReportEnvironements */
    public BugReportEnvironements() {
        super();
    }
    
    public BugReportEnvironement getBugReportEnvironement(int index){
        return (BugReportEnvironement)get(index);
    }
    
}
