/*
 * RelationManagerConfigurations.java
 *
 * Created on June 13, 2002, 10:59 PM
 */

package org.mmbase.applications.config;

/**
 *
 * @author  mmbase
 */
public class RelationManagerConfigurations extends java.util.Vector {
    
    /** Creates a new instance of RelationManagerConfigurations */
    public RelationManagerConfigurations() {
    }
    
    public RelationManagerConfiguration getRelationManagerConfiguration(int index){
        return (RelationManagerConfiguration)get(index);
    }
}
