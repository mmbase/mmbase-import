package org.mmbase.applications.config;

/**
 * list of nodemanager configurations
 * @author Kees Jongenburger
 * @version $Id: NodeManagerConfigurations.java,v 1.2 2002-06-27 19:20:30 kees Exp $
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
