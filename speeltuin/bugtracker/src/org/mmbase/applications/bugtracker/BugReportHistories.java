/*
 * BugReportHistories.java
 *
 * Created on June 10, 2002, 7:18 PM
 */

package org.mmbase.applications.bugtracker;

/**
 *
 * @author  mmbase
 */
public class BugReportHistories extends java.util.Vector{
    
    /** Creates a new instance of BugReportHistories */
    public BugReportHistories() {
        super();
    }
    
    public BugReportHistory getBugReportHistory(int index){
        return (BugReportHistory)get(index);
    }
    
}
