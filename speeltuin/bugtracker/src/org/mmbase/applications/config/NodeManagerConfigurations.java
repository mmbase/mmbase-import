/*
 * NodeManagerConfigurations.java
 *
 * Created on June 13, 2002, 10:57 PM
 */

package org.mmbase.applications.config;

/**
 *
 * @author  mmbase
 */
public class NodeManagerConfigurations extends java.util.Vector {
    
    /** Creates a new instance of NodeManagerConfigurations */
    public NodeManagerConfigurations() {
        super();
    }
    
    public NodeManagerConfiguration getNodeManagerConfiguration(int index){
        return (NodeManagerConfiguration)get(index);
    }
}
