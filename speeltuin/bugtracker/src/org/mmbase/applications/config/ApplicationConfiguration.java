package org.mmbase.applications.config;

/**
 * This class represents the cloud configuration of an mmbase application
 * @author Kees Jongenburger
 * @version $ID: $
 */
public interface ApplicationConfiguration {
    /**
     * @return the name of the application
     **/
    public String getName();
    
    /**
     * @return the list of NodeManager configurations defined by the application. This list contains
     * node managers and "relation" node managers
     **/
    public NodeManagerConfigurations getNodeManagerConfigurations();
    
    /**
     * @return the list of defined possible relations between to nodemanagers (using a certain "relation" node manager)
     **/
    public RelationManagerConfigurations getRelationManagerConfigurations();
    
}
