/*
 * BugReport.java
 *
 * Created on June 7, 2002, 9:01 AM
 */

package org.mmbase.applications.bugtracker;

/**
 * @mmbase-nodemanager-name bugtracker
 * @mmbase-nodemanager-field name string
 * @mmbase-relationmanager-name maintainer
 * @mmbase-relationmanager-nodemanager maintainter
 * @mmbase-relationmanager-source bugtracker
 * @mmbase-relationmanager-destination bugtrackeruser
 */
public class Bugtracker {
    
    /** Creates a new instance of BugReport */
    public Bugtracker() {
    }

    public BugCategory getRootCategory(){
	    return new BugCategory();
    }

    public BugtrackerUsers getBugtrackerMaintainers(){
	    return new BugtrackerUsers();
    }
}
