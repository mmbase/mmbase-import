/*
 * BugReport.java
 *
 * Created on June 7, 2002, 9:01 AM
 */

package org.mmbase.applications.bugtracker;

/**
 *
 * @author  mmbase
 * @mmbase-nodemanager-name bugreport
 * @mmbase-nodemanager-field issue STRING
 * @mmbase-nodemanager-field rationale STRING
 * @mmbase-nodemanager-field description STRING
 *
 * @mmbase-nodemanager-name daterel
 * @mmbase-nodemanager-extends insrel
 * @mmbase-nodemanager-field date INTEGER
 *
 * @mmbase-relationmanager-name bugstatusrel
 * @mmbase-relationmanager-nodemanager daterel
 * @mmbase-relationmanager-source bugreport
 * @mmbase-relationmanager-destination bugreportstatus
 *
 * @mmbase-relationmanager-name bugenvrel
 * @mmbase-relationmanager-source bugreport
 * @mmbase-relationmanager-destination bugreportenv
 *
 * @mmbase-relationmanager-name histrel
 * @mmbase-relationmanager-source bugreport
 * @mmbase-relationmanager-destination bughistory
 *
 * @mmbase-relationmanager-name maintainerrel
 * @mmbase-relationmanager-source bugreport
 * @mmbase-relationmanager-destination bugtrackeruser
 *
 * @mmbase-relationmanager-name interestedrel
 * @mmbase-relationmanager-source bugreport
 * @mmbase-relationmanager-destination bugtrackeruser
 */
public class BugReport {
    
    /** Creates a new instance of BugReport */
    public BugReport() {
    }
    
    public String getIssue(){
        return "issue";
    }
    
    public String getRationale(){
        return "rationale";
    }
    
    public String getDescription(){
        return "description";
    }
    
}
