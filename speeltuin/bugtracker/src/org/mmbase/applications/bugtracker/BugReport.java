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
 * @mmbase-nodemanager-field status INTEGER
 * @mmbase-nodemanager-field rationale STRING
 * @mmbase-nodemanager-field description STRING
 *
 * @mmbase-relationmanager-name bugstatusrel
 * @mmbase-relationamanger-nodemanager bugstatusrel
 * @mmbase-relationmanager-field date INTEGER
 * @mmbase-relationmanager-source bugreport
 * @mmbase-relationmanager-destination bugreportstatus
 */
public class BugReport {
    
    /** Creates a new instance of BugReport */
    public BugReport() {
    }
    
}
