/*
 * BugtrackerUser.java
 *
 * Created on June 7, 2002, 9:02 AM
 */
package org.mmbase.applications.bugtracker;

/**
 *
 * @author  mmbase
 * @mmbase-nodemanager-name bugtrackeruser
 * @mmbase-nodemanager-field name
 * @mmbase-nodemanager-field passwd
 * @mmbase-nodemanager-field email
 *
 * @mmbase-relationmanager-name usergrouprel
 * @mmbase-relationmanager-source bugtrackeruser
 * @mmbase-relationmanager-destination bugusergroup
 */
public class BugtrackerUser {
    
    /** Creates a new instance of BugtrackerUser */
    public BugtrackerUser() {
    }
    
}
