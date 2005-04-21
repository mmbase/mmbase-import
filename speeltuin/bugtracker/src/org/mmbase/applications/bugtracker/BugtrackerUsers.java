/*
 * BugtrackerUsers.java
 *
 * Created on June 7, 2002, 9:10 AM
 */

package org.mmbase.applications.bugtracker;

/**
 *
 * @author  mmbase
 */
public class BugtrackerUsers extends java.util.Vector {
    
    /** Creates a new instance of BugtrackerUsers */
    public BugtrackerUsers() {
        super();
    }
    
    public BugtrackerUser getBugtrackerUserAt(int index){
        return (BugtrackerUser)get(index);
    }
}
