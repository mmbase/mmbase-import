/*
 * BugReportVersionInformation.java
 *
 * Created on June 10, 2002, 6:49 PM
 */

package org.mmbase.applications.bugtracker;

/**
 *
 * @author  mmbase
 * @mmbase-nodemanager-name bugreportenv
 * @mmbase-nodemanager-field mmbasename STRING
 * @mmbase-nodemanager-field mmbaseversion STRING
 * @mmbase-nodemanager-field applicationservername STRING
 * @mmbase-nodemanager-field applicationserverversion STRING
 * @mmbase-nodemanager-field databasename STRING
 * @mmbase-nodemanager-field databaseversion STRING
 * @mmbase-nodemanager-field jdbcdrivername STRING
 * @mmbase-nodemanager-field jdbcdriverversion STRING
 * @mmbase-nodemanager-field worked BOOLEAN
 */
public class BugReportEnvironement {
    String osName;
    String osVersion;
    
    /**
     * mmbase-1.5-bin
     * mmbase-1.5-src + patch bla di bla
     * mmbase cvs branch 1.5 date 20/05/2002
     **/
    String mmbaseName;
    String mmbaseVersion;
    
    String jdkName;
    String jdkVersion;
    
    String applicationServerName;
    String applicationServerVersion;
    
    String databaseName;
    String databaseVersion;
    
    String jdbcDriverName;
    String jdbcDriverVersion;
    
    boolean worked;
    
    /** Creates a new instance of BugReportVersionInformation */
    public BugReportEnvironement() {
    }
    
    public String getOSName(){
      return osName;  
    }
    
    public String getOSVersion(){
        return osVersion;
    }
    
    public String getMMBaseVersion(){
        return mmbaseVersion;
    }
    
    public String getApplicationServerName(){
        return applicationServerName;
    }
    
    public String getApplicationServerVersion(){
        return applicationServerVersion;
    }    
    
    public boolean getWorked(){
        return worked;
    }
}
